const ObjectId = require("mongodb").ObjectId;
const { utils } = require('../utils');
var iconv = require('iconv-lite');

class JokesRepository {

	constructor(context) {
		this.context = context;
		this.db = context.getDb();
		this.jokesCollection = this.db.collection('jokes');
	}

    async fetchJokes() {
        const jokes = await this.jokesCollection.find({}).sort({ _id: -1 }).limit(20).toArray();
        return jokes
    }

	async getLastJoke() {
	    const lastJoke = await this.jokesCollection.findOne({}, { sort: { _id: -1 } });
	    return lastJoke;
	}

}

exports.create = (context) => new JokesRepository(context); 