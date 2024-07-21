const ObjectId = require("mongodb").ObjectId;
const { utils } = require('../utils');
var iconv = require('iconv-lite');
const createPushSender = require('../utils/push_sender.js').create;

class PushWorker {

	constructor(context) {
		this.context = context;
    	this.pushSender = createPushSender();
	}

    async doWork() {
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


	    const withTokensNotSentPromises = withPushTokens.map((item, index) => {
	        item.pushTokens = pushTokensNotSent[index];
	        return item;
	    })
	    .filter((item) => item.pushTokens.length > 0)
	    .map((item) => {
	       return this.pushSender.sendPush(item, item.pushTokens); 
	    });
	    const sendResponses = await Promise.all(withTokensNotSentPromises);

	    if (sendResponses.length == 0) {
	        return;
	    }

	    // Сохраняем статусы отправки
	    const createStatusPromises = withPushTokens.map((item, index) => {
	        if (sendResponses[index] && sendResponses[index].responses) {
	            item.sendResponse = sendResponses[index].responses;
	        }

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

}

exports.create = (context) => new PushWorker(context); 