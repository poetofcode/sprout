const ObjectId = require("mongodb").ObjectId;
const crypto = require('crypto');

class NotificationStatusRepository {

	constructor(context) {
		this.context = context;
		this.statusesCollection = context.getDb().collection('notification_statuses');
	}

	async createStatus(notificationId, pushToken, isSuccess) {
		const entityOnCreate = {
			notificationId: notificationId,
			createdAt: new Date(),
			pushToken: pushToken
		}
		const inserted = await this.statusesCollection.updateOne(
			{ 
				notificationId: notificationId,
				pushToken: pushToken
			},
			{ 
				$set: {
					isSuccess: isSuccess
				},
				$setOnInsert: entityOnCreate
			},
			{ upsert: true }
		);

		console.log(`iserted notification statuses:`);
		console.log(inserted);
	}

	async filterPushTokensNotSent(notificationId, pushTokens) {
        const arr = await this.statusesCollection
        	.find({
        		notificationId: notificationId,
        		pushToken: {
        			"$in": pushTokens
        		}
        	})
        	.toArray();

        return arr.map((item) => item.pusToken);
	}


}

exports.create = (context) => new NotificationStatusRepository(context);