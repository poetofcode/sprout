const axios = require('axios');
const { XMLParser } = require('fast-xml-parser');

const parser = new XMLParser();

async function launch(context) {
    console.log("Workers started");

    // setInterval(() => {
    //     workers.forEach((worker) => worker(context));
    // }, 5000);

    workers.forEach((worker) => worker(context));
}


const jokeWorker = async (context) => { 
    const response = await axios({
        method: 'get',
        url: `http://rzhunemogu.ru/Rand.aspx`
    });

    let parsed = parser.parse(response.data);
    const joke = parsed.root.content;
    const jokeCollection = context.getDb().collection('jokes');

    const newJoke = { text: joke };
    const insertResult = await jokeCollection.insertOne(newJoke);

    console.log(insertResult);
}


const workers = [
    jokeWorker
]



exports.launch = launch;