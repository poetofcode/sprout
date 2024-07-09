const axios = require('axios');
const { XMLParser } = require('fast-xml-parser');
const createMailer = require('../utils/mailer.js').createMailer;
var iconv = require('iconv-lite');

const parser = new XMLParser();

async function launch(context) {
    console.log("Workers started");

    // TODO оборачивать вызов в try/catch

    const mailer = createMailer(context);
    await mailer.init();
    context.mailer = mailer;

    /*
    TODO: раскомментировать:

    setInterval(() => {
        workers.forEach((worker) => { await worker(context) });
    }, 10000);
    */


    setInterval(async () => {
        const workerPromises = workers.map((worker) => worker(context));
        await Promise.all(workerPromises);
    }, 10000);


    const workerPromises = workers.map((worker) => worker(context));
    await Promise.all(workerPromises);

    // console.log(context);
}


const jokeWorker = async (context) => { 
    const response = await axios({
        method: 'get',
        url: `http://rzhunemogu.ru/Rand.aspx`,
        responseType: 'arraybuffer',
        responseEncoding: 'binary'
    });

    const responseData = iconv.decode(response.data.toString('binary'), 'windows1251').toString();
    let parsed = parser.parse(responseData);
    const joke = parsed.root.content;
    const jokeCollection = context.getDb().collection('jokes');

    const newJoke = { text: joke };
    const insertResult = await jokeCollection.insertOne(newJoke);

    console.log(insertResult);
}


const notificationWorker = async (context) => {
    console.log("Работает notificationWorker");
    // console.log(context);

    const userIds = context.repositories.subscriptions.getSubscriptions();
    const lastJoke = await getLastJoke(context.getDb());

    const notificationPromises = userIds.map((userId) => {
        return context.repositories.notifications.createNotification(
            {
                title: "Новый анекдот",
                text: lastJoke.text,
                image: "",
            },
            lastJoke._id,
            userId._id
        )
    });
    await Promise.all(notificationPromises);
}

const debugWorker = async (context) => {
    console.log('Вывод debugWorker\'а:');
    // console.log(context.repositories.subscriptions.getSubscriptions());

    const userIds = context.repositories.subscriptions.getSubscriptions();

    // Тут мы должны брать последний анекдот
    // Потом мы должны пробегать по списку ids и вызывать что-то вроде:
    //  находить в БД юзера с соответствующим id и находить его email
    //  mailer.send(userEmail, lastJoke)
    //  в этом mailer'е реализовать отсылку по емейлу


    const lastJoke = await getLastJoke(context.getDb());
    // console.log(`Last joke:`);
    // console.log(lastJoke);


    async function sendOneMail(userId, lastJoke) {
        const foundUser = await context.repositories.users.findUserById(userId);
        console.log(foundUser);
        const mailerStatus = await context.mailer.send(foundUser.login, lastJoke.text);
        console.log(mailerStatus);
    }
    
    const sendPromises = userIds.map((userId) => sendOneMail(userId, lastJoke));
    await Promise.all(sendPromises);
}


async function getLastJoke(db) {
    const jokeCollection = db.collection('jokes');
    const lastJoke = await jokeCollection.findOne({}, { sort: { _id: -1 } });
    return lastJoke;
}


const workers = [
    // jokeWorker,
    debugWorker,
    notificationWorker
]


exports.launch = launch;