const nodemailer = require('nodemailer');

class Mailer {
    constructor(context) {
        this.context = context;
    }

    async init() {
		// let testEmailAccount = await nodemailer.createTestAccount();
		// this.transporter = nodemailer.createTransport({
		//     host: 'smtp.ethereal.email',
		//     port: 587,
		//     secure: false,
		//     auth: {
		//         user: testEmailAccount.user,
		//         pass: testEmailAccount.pass,
		//     },
		// });

		// this.transporter = nodemailer.createTransport({
		//     service: 'gmail',
		//     auth: {
		//         user: 'user',
		//         pass: 'pass',
		//     },
		// });

		this.transporter = await nodemailer.createTransport({
 			service: 'Yandex',
		    auth: {
		      user: "example@mail.com", // generated ethereal user
		      pass: "pass" // generated ethereal password
		    }
		});
    }

    async send(email, jokeText) {
        // console.log(`[Mailer] email: ${email}, joke: ${jokeText}`);

		let result = await this.transporter.sendMail({
		    from: '"Node js" <nodejs@example.com>',
		    to: 'neverhood99@gmail.com',		 // ', user@example.com',
		    subject: 'Message from Node js',
		    text: 'This message was sent from Node js server.',
		    html:
		        'This <i>message</i> was sent from <strong>Node js</strong> server.',
		});

		console.log("[Mailer] result:");
		console.log(result);
    }
}


function createMailer(context) {
	return new Mailer(context);
}

exports.createMailer = createMailer;