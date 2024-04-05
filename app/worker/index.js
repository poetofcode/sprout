async function launch(context) {
    console.log("Workers started");

    setInterval(() => {
        workers.forEach((worker) => worker(context));
    }, 1000);
}

const jokeWorker = async (context) => { 
    // console.log("Joke worker") 
}

const workers = [
    jokeWorker
]


exports.launch = launch;