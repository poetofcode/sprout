const jetpack = require("fs-jetpack");

(async () => {
	try {
		console.log("Starting...");

		const src = jetpack.cwd("../app");
		const dst = jetpack.cwd("../../scaffold");

		src.find({ matching: "*" }).forEach((path) => {
		  const content = src.read(path);
		  // const transformedContent = transformTheFileHoweverYouWant(content);
		  const transformedContent = content;

		  console.log(`Walking path: ${path}`);

		  if (path.includes('mailer.js')) {
		  	console.log(`File contents:\n\n${content}\n\n`);
		  }

		  dst.write(`app/${path}`, transformedContent);
		});

	} catch(err) {
	    return console.log(err);
	} 
})();
