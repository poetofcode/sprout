const { utils } = require('../utils');

class SubscriptionMiddleware {

	constructor(context, repositories) {
		this.context = context;
        this.repositories = context.repositories;
	}

    subscribe() { 
        return async (req, res, next) => {
            const user = res.locals.session.user;
            this.repositories.subscriptions.enableSubscription(user, true);
            res.send(utils.wrapResult('Ok'));
        }
    }

    unsubscribe() {
        return async (req, res, next) => {
            const user = res.locals.session.user;
            this.repositories.subscriptions.enableSubscription(user, false);
            res.send(utils.wrapResult('Ok'));
        }
    }
}

exports.create = (context) => new SubscriptionMiddleware(context);


/*
            try {
                const user = await this.repositories.users.createUser(login, password);
                res.send(utils.wrapResult(user));
            } catch (err) {
                next(err);
            }
            */