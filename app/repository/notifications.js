const ObjectId = require("mongodb").ObjectId;
const crypto = require('crypto');

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

	}

	async getNotificationById(notificationId) {

	}

}

exports.create = (context) => new NotificationRepository(context);