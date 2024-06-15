const { utils } = require('../utils');

class SubscriptionMiddleware {

	constructor(context, repositories) {
		this.context = context;
        this.repositories = context.repositories;
	}

    getSubscription() {
        return async (req, res, next) => {
            const ids = this.repositories.subscriptions.getSubscriptions();
            const session = res.locals.session;
            const isSubscribed = ids.includes(session.user._id.toString());
            res.send(utils.wrapResult({ isSubscribed_: isSubscribed }));
        }
    }

    subscribe() { 
        return async (req, res, next) => {
            const user = res.locals.session.user;
            this.repositories.subscriptions.enableSubscription(user, true);
            res.send(utils.wrapResult({ status: 'Ok' }));
        }
    }

    unsubscribe() {
        return async (req, res, next) => {
            const user = res.locals.session.user;
            this.repositories.subscriptions.enableSubscription(user, false);
            res.send(utils.wrapResult({ status: 'Ok' }));
        }
    }
}

exports.create = (context) => new SubscriptionMiddleware(context);