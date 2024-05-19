const axios = require('axios');
const { XMLParser } = require('fast-xml-parser');

const parser = new XMLParser();

async function launch(context) {
    console.log("Workers started");

    // TODO оборачивать вызов в try/catch

    const mailer = new Mailer(context);
    context.mailer = mailer;

    setInterval(() => {
        workers.forEach((worker) => worker(context));
    }, 10000);

    workers.forEach((worker) => worker(context));

    console.log(context);
}


const jokeWorker = async (context) => { 
    const response = await axios({
        method: 'get',
        url: `http://rzhunemogu.ru/Rand.aspx`
    });

    let parsed = parser.parse(response.data);
    const joke = parsed.root.content;
    const jokeCollection = context.getDb().collection('jokes');

    const newJoke = { text: joke };
    const insertResult = await jokeCollection.insertOne(newJoke);

    console.log(insertResult);
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


    const jokeCollection = context.getDb().collection('jokes');
    const lastJoke = await jokeCollection.findOne({}, { sort: { _id: -1 } });
    // console.log(`Last joke:`);
    // console.log(lastJoke);


    const sendPromises = [];
    userIds.forEach((userId) => {
        const foundUserPromise = context.repositories.users.findUserById(userId);
        console.log(`userId: ${userId}`);
        // console.log(foundUser);

        foundUserPromise
            .then((foundUser) => {
                console.log("Found user:");
                console.log(foundUser);

                sendPromises.push(context.mailer.send(foundUser.login, lastJoke.text));
            })
            .catch((error) => {
                console.log(`[workers] Found user error: ${error}`);
            })
    });

    await Promise.all(sendPromises);
}


class Mailer {
    constructor(context) {
        this.context = context;
    }

    async send(email, jokeText) {
        console.log(`[Mailer] email: ${email}, joke: ${jokeText}`);
    }
}


const workers = [
    // jokeWorker,
    debugWorker,
]


exports.launch = launch;