const { utils } = require('../utils');

async function launch(context) {
    console.log("Workers started");

    // TODO оборачивать вызов в try/catch

    const workers = {};
    (await utils.requireAll('app/worker/')).forEach((name, value) => {
        workers[name] = value.create(context);
    });

    const withIntervals = [
        [ workers.jokes, seconds(10) ],
        [ workers.mailer, seconds(5) ]
    ];

    withIntervals.forEach((w) => {
        console.log(w);
        const worker = w[0];
        const interval = w[1];
        utils.setIntervalImmediately(async () => { 
            worker.doWork() 
        }, interval);
    });
}

function seconds(sec) {
    return sec * 1000;
}

exports.launch = launch;