Composite {
	classvar <directory, id = 'sc-modules', <dictionary;
	var <moduleSet, <modules, <templater;

	*new { | moduleSet(\default), from |
		this.checkDefaults;
		^super.newCopyArgs(moduleSet)
		.loadModules(from).initComposite;
	}

	*basicNew { | moduleSet(\default) |
		^super.newCopyArgs(moduleSet);
	}

	*initClass {
		Class.initClassTree(Dictionary);
		Class.initClassTree(PathStorage);
		Class.initClassTree(ModuleDictionary);
		directory = PathStorage.at(id) ?? {
			PathStorage.setAt(this.defaultDirectory, id);
		};
		dictionary = ModuleDictionary.new;
	}

	*defaultDirectory {
		^(Main.packages.asDict.at('CodexIan')+/+id);
	}

	*checkDefaults {
		var defaults = this.defaultModulePath; 
		var folder = this.classFolder+/+"default";
		if(defaults.exists and: { folder.exists.not }, { 
			defaults.copyScriptsTo(folder.mkdir)
		});
	}

	*defaultModulePath { ^""; }

	loadModules { | from | 
		var class = this.class, dict = class.dictionary; 
		dict.at(class.name) ?? { dict.newDictionary(class.name) };
		if(dict.notAt(class.name, moduleSet), { 
			this.getFrom(from); 
		}, { this.getModules; });
		modules = dict.modulesAt(class.name, moduleSet);
	}

	getFrom { | from |
		if(from.notNil, { 
			this.copyModules(from);
			forkIfNeeded { this.processFolders(from); }
		}, { this.processFolders; });
	}

	switchTemplater { | folder | 
		templater !? { 
			templater.path = folder;
		} ?? { templater = Templater(folder); }
	}

	copyModules { | from |
		var class = this.class, dict = class.dictionary;
		if(dict.notAt(class.name, from), {
			dict.getModules(class.name, from, this.loadFrom(from));
		});
		dict.copyEntry(class.name, from, moduleSet);
	}

	initComposite {}

	moduleFolder { ^(this.class.classFolder+/+moduleSet); }

	folderFrom { | from | ^(this.class.classFolder+/+from); }

	*classFolder { ^(this.directory +/+ this.name); }

	processFolders { | from |
		if(this.moduleFolder.exists.not, {  
			this.moduleFolder.mkdir;
			from !? {
				(this.class.classFolder+/+from)
				.copyScriptsTo(this.moduleFolder);
			} ?? { 
				this.switchTemplater(this.moduleFolder);
				this.makeTemplates; 
			};
		});
	}

	makeTemplates {
		this.subclassResponsibility(thisMethod);
	}

	loadFrom { | from | ^this.loadScripts(this.folderFrom(from)); }

	getModules { 
		var class = this.class, dict = class.dictionary; 
		dict.getModules(
			class.name, 
			moduleSet, 
			this.loadScripts(this.moduleFolder);
		);
	}

	loadScripts { | folder |
		^folder.getScriptPaths.collect({ | script |
			[this.getModuleName(script), script.load];
		}).flat.asPairs(Event);
	}

	reloadScripts {
		var class = this.class, dict = class.dictionary;
		dict.removeModules(class.name, moduleSet);
		this.loadScripts;
	}

	getModuleName { | input |
		^PathName(input)
		.fileNameWithoutExtension
		.lowerFirstChar.asSymbol;
	}

	*directory_{| newPath |
		directory = PathStorage.setAt(newPath, id);
	}

	moduleSet_{| newSet, from |
		moduleSet = newSet;
		this.processModules(from);
		this.initComposite;
	}

	*moduleSets {
		^PathName(this.classFolder).folders
		.collectAs({|m|m.folderName.asSymbol}, Set);
	}
}
