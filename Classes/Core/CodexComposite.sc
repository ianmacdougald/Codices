CodexComposite {
	classvar <directory, id = 'sc-modules', cache;
	var <moduleSet, <modules;

	*initClass {
		Class.initClassTree(Dictionary);
		Class.initClassTree(CodexPaths);
		Class.initClassTree(List);
		directory = CodexPaths.at(id) ?? {
			CodexPaths.setAt(
				Main.packages.asDict.at(\CodexIan)
				+/+"sc-modules",
				id
			);
		};
		cache = CodexCache.new;
		this.allSubclasses.do({ | class |
			Class.initClassTree(class);
			class.copyVersions;
		});
	}

	*new { | moduleSet, from |
		^super.newCopyArgs(
			moduleSet ?? { Error("No module set specified").throw }
		).loadModules(from).initComposite;
	}

	loadModules { | from |
		modules = this.class.getModules(moduleSet, from);
	}

	*getModules { | set, from |
		if(this.notAt(set) and: { this.shouldAdd(set, from) }, {
			this.addModules(set);
		});
		^cache.modulesAt(this.name, set);
	}

	*notAt { | set | ^cache.notAt(this.name, set) }

	*shouldAdd { | set, from |
		^if(from.notNil, {
			this.copyModules(set, from);
			forkIfNeeded { this.processFolders(set, from) };
			false;
		}, {
			this.processFolders(set);
			true
		});
	}

	*copyModules { | to, from |
		if(this.notAt(from), { this.addModules(from) });
		cache.copyEntry(this.name, from, to);
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
		}, { ^input });
	}

	*classFolder { ^(this.directory +/+ this.name) }

	*scriptKey { | input |
		var string = PathName(input).fileNameWithoutExtension;
		^(string[0].toLower++string[1..]).asSymbol;
	}

	*processFolders { | set, from |
		var folder = this.asPath(set);
		if(folder.exists.not, {
			folder.mkdir;
			if(from.notNil, {
				this.copyFiles(from, folder);
			}, { this.template(folder) });
		});
	}

	*copyFiles { | from, to |
		from = this.asPath(from);
		if(from.exists, {
			from.copyScriptsTo(to);
		}, { this.template(to) });
	}

	*template { | where |
		this.makeTemplates(CodexTemplater(this.asPath(where)));
	}

	*makeTemplates { | templater | }

	*addModules { | key |
		this.cache.add(key -> this.loadScripts(key));
	}

	*copyVersions {
		var versions = List.new;
		this.contribute(versions);
		versions.do { | entry |
			if(this.isVersion(entry), {
				var folder = this.classFolder+/+entry[0].asString;
				if(folder.exists.not, {
					entry[1].copyScriptsTo(folder.mkdir);
				})
			});
		}
	}

	*isVersion { | entry |
		^(
			entry.isCollection
			and: { entry.isString.not }
			and: {
				entry.select({ | item |
					item.isString or: { item.isKindOf(Symbol)}
				}).size >= 2
			}
		);
	}

	*contribute { | versions | }

	initComposite {}

	moduleFolder { ^(this.class.classFolder+/+moduleSet) }

	reloadScripts {
		cache.removeModules(this.class.name, moduleSet);
		this.moduleSet = moduleSet;
	}

	reloadModules { this.moduleSet = moduleSet }

	moduleSet_{ | newSet, from |
		moduleSet = newSet;
		this.loadModules(from);
		this.initComposite;
	}

	*moduleSets {
		^PathName(this.classFolder).folders
		.collectAs({ | m | m.folderName.asSymbol }, Set);
	}

	moduleSets { ^this.class.moduleSets }

	*directory_{| newPath |
		directory = CodexPaths.setAt(newPath, id);
	}

	openModule { | key |
		var ide = Platform.ideName;
		if(key.isCollection and: { key.isString.not }, {
			key.do{ | k | this.openModule(k) };
		});
		case { ide=="scqt" }{ this.openModule_scqt(key) }
		{ ide=="scnvim" }{
			var shell = "echo $SHELL".unixCmdGetStdOut.split($/).last;
			shell = shell[..(shell.size - 2)];
			this.openModule_scvim(key, shell, true);
		}
		{ ide=="scvim" }{
			var shell = "echo $SHELL".unixCmdGetStdOut.split($/).last;
			shell = shell[..(shell.size - 2)];
			this.openModule_scvim(key, shell, false);
		};
	}

	openAll {
		var ide = Platform.ideName;
		var keys = modules.keys;
		case { ide=="scqt"} { keys.do{ | key | this.openModule_scqt(key) } }
		{ ide=="scnvim" }{
			var shell = "echo $SHELL".unixCmdGetStdOut.split($/).last;
			shell = shell[..(shell.size - 2)];
			this.openAll_scvim(shell, true, true);
		}
		{ ide=="scvim" }{
			var shell = "echo $SHELL".unixCmdGetStdOut.split($/).last;
			shell = shell[..(shell.size - 2)];
			this.openAll_scvim(shell, false, true);
		}
		{ format("Warning: cannot open modules from %", ide).postln };
	}

	openModule_scqt { | key |
		if(\Document.asClass.notNil, {
			var path = this.moduleFolder+/+key.asString++".scd";
			\Document.asClass.perform(\open, path);
		});
	}

	openModulesSCqt {
		if(\Document.asClass.notNil, {
			PathName(this.moduleFolder).files.do{ | file |
				\Document.asClass.perform(\open, file.fullPath);
			}
		});
	}

	openModule_scvim { | key, shell("sh"), neovim(false) |
		var cmd = "vim";
		var path = this.moduleFolder+/+key.asString++".scd";
		if(neovim, { cmd = $n++cmd });
		cmd = cmd++" "++path;
		if(\GnomeTerminal.asClass.notNil, {
			cmd.perform(\runInGnomeTerminal, shell);
		}, { cmd.perform(\runInTerminal, shell) });
	}

	openAll_scvim { | shell("sh"), neovim(false), vertically(true) |
		var cmd = "vim", paths = PathName(this.moduleFolder)
		.files.collect(_.fullPath);
		if(neovim, { cmd = $n++cmd });
		if(vertically, { cmd = cmd+" -o "}, { cmd = cmd+" -O " });
		paths.do{ | path | cmd=cmd++path++" " };
		if(\GnomeTerminal.asClass.notNil, {
			cmd.perform(\runInGnomeTerminal, shell);
		}, { cmd.perform(\runInTerminal, shell) });
	}

	*clearCache { cache.removeAt(this.name).clear }

	*cache { ^cache.at(this.name) }

	*allCaches { ^cache }
}
