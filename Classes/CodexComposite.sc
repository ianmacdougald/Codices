CodexComposite {
	classvar <directory, id = 'sc-modules', <modules;
	var <moduleSet, <modules;

	*initClass {
		Class.initClassTree(Dictionary);
		Class.initClassTree(CodexPaths);
		directory = CodexPaths.at(id) ?? {
			CodexPaths.setAt(this.defaultDirectory, id);
		};
		modules = CodexCache.new;
		this.allSubclasses.do({ | class |
			Class.initClassTree(class);
			class.checkDefaults;
		});
	}

	*new { | moduleSet(\default), from |
		^super.newCopyArgs(moduleSet)
		.loadModules(from).initComposite;
	}

	*getModules { | set, from |
		if(this.notAt(set) and: { this.shouldAdd(set, from) }, {
			this.addModules(set);
		});
		^modules.modulesAt(this.name, set);
	}

	*notAt { | set | ^modules.notAt(this.name, set); }

	*shouldAdd { | set, from |
		if(from.notNil, {
			this.copyModules(set, from);
			forkIfNeeded { this.processFolders(set, from) };
			^false;
		}, { this.processFolders(set); ^true });
	}

	*copyModules { | to, from |
		if(this.notAt(from), { this.addModules(from) });
		modules.copyEntry(this.name, from, to);
	}

	*loadScripts { | at |
		var return = ();
		this.asPath(at).getScriptPaths.do({ | script |
			return.add(this.scriptKey(script) -> script.load);
		});
		^return;
	}

	*asPath { | input |
		input = input.asString;
		if(PathName(input).isRelativePath, {
			^(this.classFolder+/+input);
		}, { ^input; });
	}

	*classFolder { ^(this.directory +/+ this.name); }

	*scriptKey { | input |
		^PathName(input).fileNameWithoutExtension
		.lowerFirstChar.asSymbol;
	}

	*processFolders { | set, from |
		var folder = this.asPath(set);
		if(folder.exists.not, {
			folder.mkdir;
			from !? { this.copyFiles(from, folder) } ?? { this.template(folder) };
		});
	}

	*copyFiles { | from, to |
		from = this.asPath(from);
		if(from.exists, {
			from.copyScriptsTo(to);
		}, { this.processTemplates(to) });
	}

	*template { | where |
		this.makeTemplates(CodexTemplater(this.asPath(where)));
	}

	*makeTemplates { | templater | }

	*addModules { | moduleSymbol |
		modules.add(this.name, moduleSymbol, this.loadScripts(moduleSymbol));
	}

	*defaultDirectory {
		^(Platform.userExtensionDir.dirname+/+id);
	}

	*checkDefaults {
		var defaults = this.defaultModulesPath;
		var folder = this.classFolder+/+"default";
		if(defaults.exists && folder.exists.not, {
			defaults.copyScriptsTo(folder.mkdir);
		});
	}

	*defaultModulesPath { ^""; }

	loadModules { | from |
		modules = this.class.getModules(moduleSet, from);
	}

	initComposite {}

	moduleFolder { ^(this.class.classFolder+/+moduleSet); }

	reloadScripts {
		var class = this.class, dict = class.modules;
		dict.removeModules(class.name, moduleSet);
		this.moduleSet = moduleSet;
	}

	moduleSet_{ | newSet, from |
		moduleSet = newSet;
		this.loadModules(from);
		this.initComposite;
	}

	*moduleSets {
		^PathName(this.classFolder).folders
		.collectAs({|m|m.folderName.asSymbol}, Set);
	}

	*directory_{| newPath |
		directory = CodexPaths.setAt(newPath, id);
	}
}