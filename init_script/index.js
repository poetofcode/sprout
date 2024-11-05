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

	constructor(subDir, dstDir) {
		this.subDir = subDir;
		this.dstDir = dstDir;
	}

	process(path, content, next) {
		const newPath = `${this.subDir}/${path}`;
		const dst = jetpack.cwd(this.dstDir);
		dst.write(newPath, content);
		next(newPath, content);
	}

}


(async () => {
	try {
		console.log("Starting...");

		const src = jetpack.cwd("../app");

		const logReplacer = new Replacer('console.log("', 'console.log("Prefixed by Replacer: ');
		const secondReplacer = new Replacer('Prefixed by Replacer: ', 'Prefixed by Replacer - ');
		const copier = new Copier('app', '../../scaffold');

	  	const handlers = [logReplacer, secondReplacer, copier];

		src.find({ matching: "*" }).forEach((path) => {
		  const content = src.read(path);

		  console.log(`Walking path: ${path}`);

		  function run(p, c, hIndex) {
		  	handlers[hIndex].process(p, c, (nextPath, nextContent) => {
		  		if(hIndex + 1 > handlers.length - 1) {
		  			return;
		  		}
		  		run(nextPath, nextContent, hIndex + 1);
		  	})
		  }

		  run(path, content, 0);
		});

	} catch(err) {
	    return console.log(err);
	} 
})();