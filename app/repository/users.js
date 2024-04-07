const { utils } = require('../utils');

class UserRepository {

	constructor(context) {
		this.context = context;
		this.db = context.getDb();
		this.userCollection = this.db.collection('users');
	}

    async createUser(login, password) {
		const foundUser = await this.userCollection.findOne({ login : login });
        if (foundUser) {
            return new Error(`User with login ${login} already exists`);
        }

        const newUser = {
            createdAt: new Date(),
            login: login,
            password: utils.sha1(password),
        }

        const result = await this.userCollection.insertOne(newUser);
        return newUser;
    }


}

exports.create = (context) => new UserRepository(context); 