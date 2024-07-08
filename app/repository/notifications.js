const ObjectId = require("mongodb").ObjectId;
const crypto = require('crypto');

class NotificationRepository {


	constructor(context) {
		this.context = context;
		this.notificationCollection = context.getDb().collection('notifications');
	}

	async createNotification(notification) {

	}

	async getNotifications(limit, skip) {

	}

	async getNotificationById(notificationId) {

	}

}

exports.create = (context) => new NotificationRepository(context);