const axios = require('axios');
const { XMLParser } = require('fast-xml-parser');
const createMailer = require('../utils/mailer.js').createMailer;
const createPushSender = require('../utils/push_sender.js').create;
var iconv = require('iconv-lite');

const parser = new XMLParser();
const pushSender = createPushSender();

async function launch(context) {
    console.log("Workers started");

    // TODO оборачивать вызов в try/catch

    const mailer = createMailer(context);
    // const pushSender = createPushSender();
    await mailer.init();
    context.mailer = mailer;

    setInterval(async () => {
        const workerPromises = workers.map((worker) => worker(context));
        await Promise.all(workerPromises);
    }, 10000);


    const workerPromises = workers.map((worker) => worker(context));
    await Promise.all(workerPromises);
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

    const userIds = context.repositories.subscriptions.getSubscriptions();
    const lastJoke = await getLastJoke(context.getDb());

    // Создаём нотификации
    const notificationPromises = userIds.map((userId) => {
        return context.repositories.notifications.createNotification(
            {
                title: "Новый анекдот",
                text: lastJoke.text,
                image: "",
            },
            lastJoke._id,
            userId
        )
    });
    await Promise.all(notificationPromises);

    // Достаём непрочитанные нотификации
    const unreadNotifications = await context.repositories.notifications.getUnreadNotifications();
    const sessionsPromises = unreadNotifications.map((item) => {
        return context.repositories.sessions.fetchActiveSessionsByUserId(item.userId);
    });
    const sessionsArr = await Promise.all(sessionsPromises);
    const mergedArr = unreadNotifications.map((item, index) => {
        item.sessions = sessionsArr[index];
        return item;
    });

    const withPushTokens = mergedArr.map((item) => {
        const pushTokens = item.sessions.filter((s) => {
            return s.params && s.params.pushToken
        })
        .map((s) => s.params.pushToken );
        item.pushTokens = pushTokens;
        return item;
    }).filter((item) => item.pushTokens.length > 0);

    const notSentPromises = withPushTokens.map((item) => {
        return context.repositories.notificationStatuses.filterPushTokensNotSent(item._id, item.pushTokens);
    });
    const pushTokensNotSent = await Promise.all(notSentPromises);

    console.log('==================');
    console.log(pushTokensNotSent);


    const withTokensNotSentPromises = withPushTokens.map((item, index) => {
        item.pushTokens = pushTokensNotSent[index];
        return item;
    })
    .filter((item) => item.pushTokens.length > 0)
    .map((item) => {
       return pushSender.sendPush(item, item.pushTokens); 
    });
    const sendResponses = await Promise.all(withTokensNotSentPromises);

    console.log('++++++++++++++++++++++++');
    console.log(sendResponses);

    if (sendResponses.length == 0) {
        return;
    }

    // Сохраняем статусы отправки
    const createStatusPromises = withPushTokens.map((item, index) => {
        if (sendResponses[index] && sendResponses[index].responses) {
            item.sendResponse = sendResponses[index].responses;
        }

        console.log('Status of item:');
        console.log(item);

        return item;
    })
    .filter((item) => !item.sendResponses)
    .map((item) => {
        return Promise.all(item.pushTokens.map((t, idx) => {
            return context.repositories.notificationStatuses.createStatus(item._id, t, item.sendResponse[idx].success)
        }));
    });
    Promise.all(createStatusPromises);
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
    //jokeWorker,
    debugWorker,
    notificationWorker
]


exports.launch = launch;