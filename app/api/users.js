class UserMiddleware {

	constructor(context) {
		this.context = context;

	}

	createUser() { 
		return async (req, res, next) => {

		}
	}

}

exports.create = (context) => new UserMiddleware(context);