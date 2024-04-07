const { utils } = require('../utils');

class UserRepository {

	constructor(context) {
		this.context = context;
		this.db = context.getDb();
		this.userCollection = this.db.collection('users');
	}

    async createUser(login, password) {
		const foundUser = await this.userCollection.findOne({ login : login /*, deleted: false */ });
        if (foundUser) {
            throw new Error(`User with login ${login} already exists`);
        }

        const newUser = {
            createdAt: new Date(),
            login: login,
            password: utils.sha1(password),
            activated: false,
            deleted: false,
        }

        const result = await this.userCollection.insertOne(newUser);
        delete newUser.password;
        delete newUser.deleted;
        return newUser;
    }

    async findUserByLogin(login) {
        return await this.userCollection.findOne({ login : login /*, deleted: false */ });
    }


}

exports.create = (context) => new UserRepository(context); 