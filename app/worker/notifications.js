const ObjectId = require("mongodb").ObjectId;
const { utils } = require('../utils');
var iconv = require('iconv-lite');

class NotificationWorker {

	constructor(context) {
		this.context = context;
	}

    async doWork() {
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
	                silent: false
	            },
	            lastJoke._id,
	            userId,
	        )
	    });
	    await Promise.all(notificationPromises);
    }

}

exports.create = (context) => new NotificationWorker(context); 