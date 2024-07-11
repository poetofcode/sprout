// Source https://medium.com/@ravisharma23523/sending-notifications-to-mobile-devices-with-firebase-cloud-messaging-fcm-in-node-js-8fe3faead58b

const admin = require('firebase-admin');
const serviceAccount = require('../../firebaseServiceAccountKey.json');

class PushSender {

	constructor() {
		admin.initializeApp({
		  credential: admin.credential.cert(serviceAccount)
		});
	}

	async sendPush(message, tokens) {
		console.log("send Push:");
		console.log(admin);
	}

}

exports.create = () => new PushSender();