const { utils } = require('../utils');

class JokesMiddleware {

	constructor(context, repositories) {
		this.context = context;
        this.repositories = context.repositories;
	}

	fetchJokes() { 
		return async (req, res, next) => {
            try {
            	const jokes = await this.repositories.jokes.fetchJokes();
            	res.send(utils.wrapResult(jokes));
        	} catch (err) {
        		next(err);
        	}
		}
	}

}

exports.create = (context) => new JokesMiddleware(context);