Composite {
	classvar <directory, id = 'sc-modules', <modules;
	var <moduleSet, <modules;

	*initClass {
		Class.initClassTree(Dictionary);
		directory = PathStorage.at(id) ?? {
			PathStorage.setAt(this.defaultDirectory, id);
		};
		modules = Modules.new;
	}

	*new { | moduleSet(\default), from |
		^super.newCopyArgs(moduleSet)
		.loadModules(from).initComposite;
	}

	*getModules { | moduleSet, from |
		if(modules.notAt(this.name, moduleSet), {
			if(this.shouldAdd(moduleSet, from), {
				this.addModules(moduleSet);
			});
		});
		^modules.modulesAt(this.name, moduleSet)
	}

	*shouldAdd { | set, from |
		if(from.notNil, {
			this.copyModules(from);
			forkIfNeeded { this.processFolders(set, from) };
			^false;
		}, { this.processFolders(set); ^true});
	}

	*copyModules { | from, to |
		if(modules.notAt(this.name, from), {
			modules.addModules(this.name, from, this.loadScripts(from));
		});
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
		if(set!=\default, {
			var folder = this.asPath(set);
			if(folder.exists.not, {
				folder.mkdir;
				from !? { this.copyFiles(from); } ?? { this.template(folder); };
			});
		}, { this.checkDefaults; });
	}

	*copyFiles { | from, to |
		from = this.asPath(from);
		if(from.exists, {
			from.copyScriptsTo(to);
		}, { this.processTemplates(to); });
	}

	*template { | where |
		this.makeTemplates(Templater(this.asPath(where)));
	}

	*makeTemplates { | templater | }

	*addModules { | moduleSymbol |
		modules.add(this.name, moduleSymbol, this.loadScripts(moduleSymbol));
	}

	*defaultDirectory {
		^(Main.packages.asDict.at('CodexIan')+/+id);
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
		this.loadModules;
	}

	moduleSet_{| newSet, from |
		moduleSet = newSet;
		this.loadModules(from);
		this.initComposite;
	}

	*moduleSets {
		^PathName(this.classFolder).folders
		.collectAs({|m|m.folderName.asSymbol}, Set);
	}

	*directory_{| newPath |
		directory = PathStorage.setAt(newPath, id);
	}

}
