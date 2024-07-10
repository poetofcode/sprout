const ObjectId = require("mongodb").ObjectId;
const crypto = require('crypto');

const defaultLimit = 20;
const defaultSkip = 0;

class NotificationRepository {


	constructor(context) {
		this.context = context;
		this.notificationCollection = context.getDb().collection('notifications');
	}

	async createNotification(notification, linkId, userId) {
		// TODO учесть ситуцию при которой не вставляется запись с существующим linkID
		const entityOnCreate = {
			userId: new ObjectId(userId),
			createdAt: new Date(),
			linkId: linkId,
			extras: ""
		}
		const inserted = await this.notificationCollection.updateOne(
			{ linkId: linkId },
			{ 
				$set: {
					title: notification.title,
					text: notification.text,
					image: notification.image,
				},
				$setOnInsert: entityOnCreate
			},
			{ upsert: true }
		);
		console.log(`iserted:`);
		console.log(inserted);
	}

	async getNotifications(limit, skip) {
		const l = limit || defaultLimit;
		const s = skip || defaultSkip;
        const arr = await this.notificationCollection
        	.find({})
        	.sort({ _id: -1 })
        	.limit(l)
        	.skip(s)
        	.toArray();
        return arr;
	}

	async getNotificationById(notificationId) {
        const found = await this.notificationCollection.findOne(
        	{ _id: new ObjectId(notificationId) }
    	);
        if (!found) {
            throw new Error('Not found notification');
        }
        return found;
	}

}

exports.create = (context) => new NotificationRepository(context);