const jetpack = require("fs-jetpack");

class Replacer {

	constructor(source, replaced, ignoreCase) {
		this.source = source;
		this.replaced = replaced;
		this.ignoreCase = ignoreCase;
	}

	process(path, content, next) {
		const sourcePrepared = this.ignoreCase ? this.source.toLowerCase() : this.source;
		const contentPrepared = this.ignoreCase ? content.toLowerCase() : content;
		if (!contentPrepared.includes(this.sourcePrepared)) {
			next(path, content);
			return;
		}
		console.log(`Replacer working on "${path}", replaced string "${this.source} on string "${this.replaced}"`);
		const newContent = content.replaceAll(this.source, this.replaced);
		next(path, newContent);
	}

}

class Copier {

	constructor(dstDir) {
		this.dstDir = dstDir;
		this.dst = jetpack.cwd(this.dstDir);
	}

	process(path, content, next) {
		const newPath = `${this.dstDir}/${path}`;
		this.dst.write(path, content);
		console.log(`Copied from "${path}" to "${newPath}"`);
		next(newPath, content);
	}

}

class PackageCopier extends Copier {

	constructor(dstDir, srcPackage, targetPackage) {
		super(dstDir);
		this.srcPackage = srcPackage;
		this.targetPackage = targetPackage;
	}

	process(path, content, next) {
		const srcPackageAsPath = this.srcPackage.replaceAll('.', '/');
		const targetPackageAsPath = this.targetPackage.replaceAll('.', '/');
		const pathClean = path.replaceAll('\\', '/').replaceAll('//', '/');

		if (pathClean.toLowerCase().includes(srcPackageAsPath.toLowerCase())) {
			const newPath = pathClean.toLowerCase().replace(srcPackageAsPath.toLowerCase(), targetPackageAsPath);
			this.dst.write(newPath, content);
			console.log(`PackageCopier: copied from "${path}" to "${newPath}. Exit script"`);			
		} else {
			next(path, content);
		}
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
		// Config parameters
		//
		const srcDir = '../client';
		const dstDir = '../../scaffold/client';
		const targetPackage = 'org.example.new_app';

		// Handlers 
		//
		const srcPackage = 'com.poetofcode.sproutclient';
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
		const logPatternEnd = `End of processing, out path "$path"`;

		const filter = new Filter(extToProceed, srcDir, dstDir, ignoreList);
		const reader = new Reader(srcDir);
		const packageReplacer = new Replacer(srcPackage, targetPackage, true);
		const packageCopier = new PackageCopier(dstDir, srcPackage, targetPackage);
		const copier = new Copier(dstDir);
		const loggerBefore = new Logger(logPatternStart);
		const loggerAfter = new Logger(logPatternEnd);

	  	const handlers = [
	  		loggerBefore,
	  		filter,
	  		reader,
	  		packageReplacer,
	  		packageCopier,
	  		copier,
	  		loggerAfter
  		];

		console.log("Starting...");
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