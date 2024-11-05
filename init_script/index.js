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
		console.log(`Copied from "${path}" to "${newPath}"`);
		next(newPath, content);
	}

}


class Logger {

	/*
		Pattern example: "File $path and its content $content"
	*/

	constructor(pattern) {
		this.pattern = pattern;
	}

	process(path, content, next) {
		const out = this.pattern.replaceAll(`$path`, path).replaceAll(`$content`, content);
		console.log(out);
		next(path, content);
	}

}


(async () => {
	try {
		console.log("Starting...");

		const src = jetpack.cwd("../app");

		const logReplacer = new Replacer('console.log("', 'console.log("Prefixed by Replacer: ');
		const secondReplacer = new Replacer('Prefixed by Replacer: ', 'Prefixed by Replacer - ');
		const copier = new Copier('app', '../../scaffold');
		const loggerBefore = new Logger(`=================================\nProcessing path "$path"`);
		const loggerAfter = new Logger(`End of processing, out path "$path"`);

	  	const handlers = [
	  		loggerBefore,
	  		logReplacer, 
	  		secondReplacer, 
	  		copier,
	  		loggerAfter
  		];

		src.find({ matching: "*" }).forEach((path) => {
		  const content = src.read(path);

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