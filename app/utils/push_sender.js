// Source https://medium.com/@ravisharma23523/sending-notifications-to-mobile-devices-with-firebase-cloud-messaging-fcm-in-node-js-8fe3faead58b

const admin = require('firebase-admin');
const serviceAccount = require('../../firebaseServiceAccountKey.json');

class PushSender {

	constructor() {
		admin.initializeApp({
		  credential: admin.credential.cert(serviceAccount)
		});
	}

	async sendPush(msg, tokens) {
		console.log("send Push:");
		console.log(admin);

		// const registrationTokens = ['TOKEN_1', 'TOKEN_2', 'TOKEN_3'];
		const message = {
		  notification: {
		    title: msg.title,
		    body: msg.text,
		    // TODO use msg.image
		  },
		  tokens: tokens
		};
		return admin.messaging().sendMulticast(message);
	}

}

exports.create = () => new PushSender();