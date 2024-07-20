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
		const entityOnCreate = {
			userId: new ObjectId(userId),
			createdAt: new Date(),
			linkId: linkId,
			extras: "",
			seen: false
		}
		const inserted = await this.notificationCollection.updateOne(
			{ linkId: linkId, userId: new ObjectId(userId) },
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

	async getNotifications(limit, skip, query) {
		const l = limit || defaultLimit;
		const s = skip || defaultSkip;
		const q = query || {};
        const arr = await this.notificationCollection
        	.find(q)
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


	async getUnreadNotifications(limit, skip) {
		const l = limit || defaultLimit;
		const s = skip || defaultSkip;
		return this.getNotifications(l, s, {
			// TODO тут нужно проверять какое-то условие прочитанности
			//      а его скорее всего нужно будет устанавливать 
			//		при установке прочитки seen для юзера
		});
	}

	async markNotificationsOfUserAsSeen(userId) {
		const seenAt = new Date();
		this.notificationCollection.update(
			{ userId: new ObjectId(userId) },
			{ 
				$set: { seen: true, seenAt: seenAt }
			}
		);
	}

}

exports.create = (context) => new NotificationRepository(context);