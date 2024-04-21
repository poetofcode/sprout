const { utils } = require('../utils');

class SubscriptionMiddleware {

	constructor(context, repositories) {
		this.context = context;
        this.repositories = context.repositories;
	}

    subscribe() { 
        return async (req, res, next) => {
            res.status(500).send("Cannot subscribe");
        }
    }

    unsubscribe() {
        return async (req, res, next) => {
            res.status(500).send("Cannot UNsubscribe");
        }
    }
}

exports.create = (context) => new SubscriptionMiddleware(context);