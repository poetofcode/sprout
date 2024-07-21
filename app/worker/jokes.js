const ObjectId = require("mongodb").ObjectId;
const { utils } = require('../utils');
var iconv = require('iconv-lite');
const axios = require('axios');
const { XMLParser } = require('fast-xml-parser');

class JokeWorker {

	constructor(context) {
		this.context = context;
		this.parser = new XMLParser();
	}

    async doWork() {
	    const response = await axios({
	        method: 'get',
	        url: `http://rzhunemogu.ru/Rand.aspx`,
	        responseType: 'arraybuffer',
	        responseEncoding: 'binary'
	    });

	    const responseData = iconv.decode(response.data.toString('binary'), 'windows1251').toString();
	    let parsed = this.parser.parse(responseData);
	    const joke = parsed.root.content;
	    const jokeCollection = context.getDb().collection('jokes');

	    const newJoke = { text: joke };
	    const insertResult = await jokeCollection.insertOne(newJoke);
    }

}

exports.create = (context) => new JokeWorker(context); 