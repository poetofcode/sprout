const ObjectId = require("mongodb").ObjectId;
const { utils } = require('../utils');

class JokesRepository {

	constructor(context) {
		this.context = context;
		this.db = context.getDb();
		this.jokesCollection = this.db.collection('jokes');
	}

    async fetchJokes() {
        const jokes = await this.jokesCollection.find({}).sort({ _id: -1 }).limit(20).toArray();
        return jokes;
    }

}

exports.create = (context) => new JokesRepository(context); 