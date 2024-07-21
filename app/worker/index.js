const { utils } = require('../utils');

async function launch(context) {
    console.log("Workers started");

    // TODO оборачивать вызов в try/catch

    const workers = {};
    (await utils.requireAll('app/worker/')).forEach((name, value) => {
        workers[name] = value.create(context);
    });

    console.log(workers);


    // setInterval(async () => {
    //     const workerPromises = workers.map((worker) => worker(context));
    //     await Promise.all(workerPromises);
    // }, 10000);


    // const workerPromises = workers.map((worker) => worker(context));
    // await Promise.all(workerPromises);
}


const notificationWorker = async (context) => {
}


const workers = [
    // jokeWorker,
    // debugWorker,
    notificationWorker
]


exports.launch = launch;