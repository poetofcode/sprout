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

	constructor(subDir, dstDir, ignoreList) {
		this.subDir = subDir;
		this.dstDir = dstDir;
		this.ignoreList = ignoreList;
	}

	process(path, content, next) {
		const newPath = `${this.subDir}/${path}`;

		let isIgnored = false;
		this.ignoreList.forEach((item) => {
			const itemRevertSlashes = item.replaceAll('/', '\\');
			if (path.includes(item) || path.includes(itemRevertSlashes)) {
				isIgnored = true;
				return;
			}
		});
		if (isIgnored) {
			console.log(`Copy: path "${path}" ignored`);
			next(path, content);
			return;
		}

		const dst = jetpack.cwd(this.dstDir);
		dst.write(newPath, content);

		// jetpack.copy(`../client/${path}`, `${this.dstDir}/${newPath}`, { overwrite: true });

		console.log(`Copied from "${path}" to "${newPath}"`);
		next(newPath, content);
	}

}


class Filter {

	constructor(extensions, srcRoot, dstRoot, subDir, ignoreList) {
		this.extensions = extensions;
		this.srcRoot = srcRoot;
		this.subDir = subDir;
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
			console.log(`Filter: file '${path}' copied without changes`);
			jetpack.copy(
				`${this.srcRoot}/${path}`, 
				`${this.dstRoot}/${this.subDir}/${path}`, 
				{ overwrite: true }
			);
		}
	}

}


class Reader {

	constructor(srcDir, ignoreList) {
		this.src = jetpack.cwd(srcDir);
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
			console.log(`Reader: path "${path}" ignored`);
			return;
		}

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

		const packageReplacer = new Replacer('com.poetofcode.sproutclient', 'org.example.new_app');

		const ignoreList = [
			'build/',
			'composeApp/kcef-bundle',
			'composeApp/cache',
			'composeApp/DawnCache',
			'composeApp/GPUCache',
			'composeApp/appcache',
			'composeApp/google-services.json'
		]

		const filter = new Filter([
			'.kt', 
			'.js', 
			'.json', 
			'.kt', 
			'.kts', 
			'.gradle',
			'.properties',
			'.toml',
			'.xml'
		], '../client', 'client', '../../scaffold', ignoreList);


		const reader = new Reader("../client", ignoreList);
		const copier = new Copier('client', '../../scaffold', ignoreList);
		const loggerBefore = new Logger(`=================================\nProcessing path "$path"`);
		const loggerAfter = new Logger(`End of processing, out path "$path"`);

	  	const handlers = [
	  		loggerBefore,
	  		filter,
	  		reader,
	  		packageReplacer,
	  		copier,
	  		loggerAfter
  		];

		jetpack.cwd("../client").find({ matching: "*" }).forEach((path) => {
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