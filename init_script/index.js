const jetpack = require("fs-jetpack");

class Replacer {

	constructor(source, replaced) {
		this.source = source;
		this.replaced = replaced;
	}

	process(path, content, next) {
		if (!content.includes(this.source)) {
			next(path, content);
			return;
		}
		console.log(`Replacer working on "${path}", replaced string "${this.source}"`);
		const newContent = content.replaceAll(this.source, this.replaced);
		next(path, newContent);
	}

}

class Copier {

	constructor() {

	}

	process(path, content, next) {
		
	}

}


(async () => {
	try {
		console.log("Starting...");

		const src = jetpack.cwd("../app");
		const dst = jetpack.cwd("../../scaffold");

		const logReplacer = new Replacer('console.log("', 'console.log("Prefixed by Replacer: ');
		const secondReplacer = new Replacer('Prefixed by Replacer: ', 'Prefixed by Replacer - ');

		const copier = (newPath, newContent) => {
			dst.write(`app/${newPath}`, newContent);
	  	}

	  	const handlers = [logReplacer, secondReplacer];


		src.find({ matching: "*" }).forEach((path) => {
		  const content = src.read(path);

		  console.log(`Walking path: ${path}`);

		  const secondHandler = (p, c) => {
		  		secondReplacer.process(p, c, copier);
		  };
		  logReplacer.process(path, content, secondHandler);

		  // logReplacer.process(path, content, copier);

		});

	} catch(err) {
	    return console.log(err);
	} 
})();