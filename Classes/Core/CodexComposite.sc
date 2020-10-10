CodexComposite {
	classvar <directory, id = 'scmodules', cache;
	var <moduleSet, <modules, <>know = false;

	*initClass {
		Class.initClassTree(Dictionary);
		Class.initClassTree(CodexStorage);
		Class.initClassTree(List);
		directory = CodexStorage.at(id) ?? {
			CodexStorage.setAt(
				Main.packages.asDict.at(\CodexIan)
				+/+"scmodules",
				id
			);
		};
		cache = CodexCache.new;
		this.allSubclasses.do({ | class |
			Class.initClassTree(class);
			class.copyVersions;
		});
	}

	*basicNew { | moduleSet, from |
		^super.newCopyArgs(
			moduleSet ?? { Error("No module set specified").throw }
		);
	}

	*new { | moduleSet, from |
		^this.basicNew(moduleSet).initCodex(from);
	}

	initCodex { | from |
		this.loadModules(from).initComposite;
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

	*directory_{ | newPath |
		directory = CodexStorage.setAt(newPath, id);
	}

	open { | ... keys |
		var ide = Platform.ideName;
		keys = keys.flat;
		case { ide=="scqt" }{ this.open_scqt(keys: keys) }
		{ ide=="scnvim" }{
			var shell = "echo $SHELL".unixCmdGetStdOut.split($/).last;
			shell = shell[..(shell.size - 2)];
			this.open_scvim(shell, true, true, keys: keys);
		}
		{ ide=="scvim" }{
			var shell = "echo $SHELL".unixCmdGetStdOut.split($/).last;
			shell = shell[..(shell.size - 2)];
			this.open_scvim(shell, false, true, keys: keys);
		};
	}

	open_scqt { | ... keys |
		if(\Document.asClass.notNil, {
			keys.do{ | item |
				var file = this.moduleFolder+/+item.asString++".scd";
				if(File.exists(file), {
					\Document.asClass.perform(
						\open, file
					);
				});
			};
		});
	}

	open_scvim {
		| shell("sh"), neovim(false), vertically(false) ...keys |
		var cmd = "vim", paths = "";
		keys.do({ | item |
			paths = paths++this.moduleFolder
			+/+item.asString++".scd ";
		});
		if(neovim, { cmd = $n++cmd });
		if(vertically, { cmd = cmd++" -o "}, { cmd = cmd++" -O " });
		paths.do{ | path | cmd=cmd++path};
		if(\GnomeTerminal.asClass.notNil, {
			cmd.perform(\runInGnomeTerminal, shell);
		}, { cmd.perform(\runInTerminal, shell) });
	}

	openModules { this.open(keys: modules.keys.asArray) }

	closeModules {
		if(Platform.ideName=="scqt", {
			if(\Document.asClass.notNil, {
				\Document.asClass.perform(\allDocuments).do {
					| doc, index |
					if(doc.dir==this.moduleFolder, {
						doc.close;
					});
				}
			});
		})
	}

	*clearCache { cache.removeAt(this.name).clear }

	*cache { ^cache.at(this.name) }

	*allCaches { ^cache }

	doesNotUnderstand { | selector ... args |
		if(know, {
			var module = modules[selector];
			module !? {
				^module.functionPerformList(\value, this, args);
			};
			if(selector.isSetter, {
				if(args[0].isKindOf(modules[selector.asGetter].class), {
					^modules[selector.asGetter] = args[0];
				}, {
					warn(
						"Can only overwrite pseudo-variable"
						++"with object of same type."
					);
					^this;
				});
			});
		});
		^this.superPerformList(\doesNotUnderstand, selector, args);
	}
}
