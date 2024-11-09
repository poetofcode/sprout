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

	constructor(dstDir) {
		this.dstDir = dstDir;
	}

	process(path, content, next) {
		const newPath = `${this.dstDir}/${path}`;

		const dst = jetpack.cwd(this.dstDir);
		dst.write(path, content);

		console.log(`Copied from "${path}" to "${newPath}"`);
		next(newPath, content);
	}

}


class Filter {

	constructor(extensions, srcRoot, dstRoot, ignoreList) {
		this.extensions = extensions;
		this.srcRoot = srcRoot;
		this.dstRoot = dstRoot;
		this.ignoreList = ignoreList;
	}

	process(path, content, next) {
		let isIgnored = false;
		this.ignoreList.forEach((item) => {
			const itemRevertSlashes = item.replaceAll('/', '\\');
			if (path.includes(item) || path.includes(itemRevertSlashes)) {
				isIgnored = true;
				return;
			}
		});
		if (isIgnored) {
			console.log(`Filter: path "${path}" ignored`);
			return;
		}

		if (this.extensions == null) {
			next(path, content);
			return;
		}

		const isProcess = this.extensions.some((ext) => {
			return path.includes(ext);
		});

		if (isProcess) {
			next(path, content, next);
		} else {
			const newPath = `${this.dstRoot}/${path}`;
			console.log(`Filter: file '${path}' copied without changes, new path: ${newPath}`);
			jetpack.copy(
				`${this.srcRoot}/${path}`, 
				newPath, 
				{ overwrite: true }
			);
		}
	}

}


class Reader {

	constructor(srcDir) {
		this.src = jetpack.cwd(srcDir);
	}

	process(path, content, next) {
		content = this.src.read(path);
		console.log(`Read content from "${path}"`);
		next(path, content);
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


		// Config parameters
		//
		const srcDir = '../client';
		const dstDir = '../../scaffold/client';
		const targetPackage = 'org.example.new_app';

		// Handlers 
		//
		const packageReplacer = new Replacer('com.poetofcode.sproutclient', targetPackage);

		const ignoreList = [
			'build/',
			'composeApp/kcef-bundle',
			'composeApp/cache',
			'composeApp/DawnCache',
			'composeApp/GPUCache',
			'composeApp/appcache',
			'composeApp/google-services.json'
		]

		const extToProceed = [
			'.kt', 
			'.js', 
			'.json', 
			'.kt', 
			'.kts', 
			'.gradle',
			'.properties',
			'.toml',
			'.xml'
		];

		const logPatternStart = `=================================\nProcessing path "$path"`;
		const logPatterEnd = `End of processing, out path "$path"`;

		const filter = new Filter(extToProceed, srcDir, dstDir, ignoreList);
		const reader = new Reader(srcDir);
		const copier = new Copier(dstDir);
		const loggerBefore = new Logger(logPatterStart);
		const loggerAfter = new Logger(logPatterEnd);

	  	const handlers = [
	  		loggerBefore,
	  		filter,
	  		reader,
	  		packageReplacer,
	  		copier,
	  		loggerAfter
  		];

		jetpack.cwd(srcDir).find({ matching: "*" }).forEach((path) => {
		  function run(p, c, hIndex) {
		  	handlers[hIndex].process(p, c, (nextPath, nextContent) => {
		  		if(hIndex + 1 > handlers.length - 1) {
		  			return;
		  		}
		  		run(nextPath, nextContent, hIndex + 1);
		  	})
		  }

		  run(path, 'Not read yet', 0);
		});

	} catch(err) {
	    return console.log(err);
	} 
})();