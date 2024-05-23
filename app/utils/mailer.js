class Mailer {
    constructor(context) {
        this.context = context;
    }

    async send(email, jokeText) {
        console.log(`[Mailer] email: ${email}, joke: ${jokeText}`);
    }
}


function createMailer(context) {
	return new Mailer(context);
}

exports.createMailer = createMailer;