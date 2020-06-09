Modular {
	classvar isInit = false, id = 'sc-modules';
	classvar <directory, <folderManager;
	var <moduleSet, <modules, templater;

	*new { |moduleSet(\default), from|
		if(isInit.not, {this.initModular});
		this.checkDefaults;
		^super.newCopyArgs(moduleSet)
		.initModular(from);	
	}

	*initModular { 
		directory = PathStorage.at(id) ?? { 
			PathStorage.setAt(
				this.defaultDirectory, 
				id	
			);
		};
		folderManager = FolderManager.new(this.moduleFolder);
	}

	*defaultDirectory { 
		^(PathName(this.filenameString).pathOnly+/+"sc-modules");
	}
	
	*checkDefaults {
		var scripts = this.filenameString.path.getScriptPaths;
		if(this.moduleFolder.exists.not and: {scripts.isEmpty.not}, { 
			var defaultPath = this.moduleFolder+/+"default"; 
			defaultPath.mkdir;
			protect{scripts.do({ |script|
				var scriptName = PathName(script).fileName;
				File.copy(
					script, 
					defaultPath+/+scriptName
				);
			})};
		});
	}

	initModular { |from|
		templater = ModuleTemplater(this.moduleFolder);
		this.processFolders(from);
		this.loadModules;
	}

	moduleFolder { 
		^(this.class.moduleFolder+/+moduleSet);
	}

	*moduleFolder { 
		^(this.directory +/+ this.name);
	}

	processFolders { |from|
		if(this.moduleFolder.exists.not, { 
			var fm = this.class.folderManager;
			from !? {fm.copyContents(from, moduleSet)} ?? {
				this.makeModuleFolder;
				this.makeTemplates;
			};
		});
	}

	makeModuleFolder { 
		File.mkdir(this.moduleFolder);
	}

	makeTemplates { 
		this.subclassResponsibility(thisMethod);
	}

	loadModules { 
		modules = ();
		this.moduleFolder.getScriptPaths.do({|script|
			var name = this.getModuleSet(script); 
			modules.add(name.asSymbol -> script.load);
		});
	}

	getModuleSet { |input|
		 ^PathName(input)
		.fileNameWithoutExtension
		.lowerFirstChar; 
	}

	*directory_{|newPath|
		directory = PathStorage.setAt(newPath, id);
	}

	moduleSet_{|newSet, from|
		moduleSet = newSet; 
		this.initModular(from);
	}

	*moduleSets {
		^PathName(this.moduleFolder).folders
		.collectAs({|m|m.folderName.asSymbol}, Set);
	}
}
