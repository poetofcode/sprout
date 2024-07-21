const ObjectId = require("mongodb").ObjectId;
const { utils } = require('../utils');
const createMailer = require('../utils/mailer.js').createMailer;

class MailerWorker {

    constructor(context) {
        this.context = context;
    }

    async doWork() {
        console.log('Вывод debugWorker\'а:');

        if (!this.mailer) {
            this.mailer = createMailer(context);
            await mailer.init();
        }

        const userIds = context.repositories.subscriptions.getSubscriptions();

        const lastJoke = await getLastJoke(context.getDb());

        async function sendOneMail(userId, lastJoke) {
            const foundUser = await context.repositories.users.findUserById(userId);
            const mailerStatus = await this.mailer.send(foundUser.login, lastJoke.text);
        }
        
        const sendPromises = userIds.map((userId) => sendOneMail(userId, lastJoke));
        await Promise.all(sendPromises);
    }

}

async function getLastJoke(db) {
    const jokeCollection = db.collection('jokes');
    const lastJoke = await jokeCollection.findOne({}, { sort: { _id: -1 } });
    return lastJoke;
}

exports.create = (context) => new MailerWorker(context); 