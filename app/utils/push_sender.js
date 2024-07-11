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
		    title: 'Новый анекдот',
		    body: 'Hello, this is a test notification for multiple devices!'
		  },
		  tokens: tokens
		};
		admin.messaging().sendMulticast(message)
		  .then((response) => {
		    console.log('Multicast notification sent:', response);
		  })
		  .catch((error) => {
		    console.error('Error sending multicast notification:', error);
		  });
	}

}

exports.create = () => new PushSender();