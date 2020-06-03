Modular {
	classvar isInit = false, id = 'sc-modules';
	classvar <directory, <folderManager;
	var <moduleName, <modules, templater;

	*new { |moduleName(\default), from|
		if(isInit.not, {this.initModular});
		this.checkDefaults;
		^super.newCopyArgs(moduleName)
		.initModular(from);	
	}

	*initModular { 
		directory = PathStorage.path(id) ?? { 
			PathStorage.path_(
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
		if(this.moduleFolder.exists.not, { 
			var scripts = this.filenameString.path.getScriptPaths;
			var defaultPath = this.moduleFolder+/+"default"; 
			defaultPath.mkdir;
			scripts.do({ |script|
				var scriptName = PathName(script).fileName;
				File.copy(
					script, 
					defaultPath+/+scriptName
				);
			});
		});
	}

	initModular { |from|
		templater = ModuleTemplater(this.moduleFolder);
		this.processFolders(from);
		this.loadModules;
	}

	moduleFolder { 
		^(this.class.moduleFolder+/+moduleName);
	}

	*moduleFolder { 
		^(this.directory +/+ this.name);
	}

	processFolders { |from|
		if(this.moduleFolder.exists.not, { 
			var fm = this.class.folderManager;
			from !? {fm.copyContents(from, moduleName)} ?? {
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
			var name = this.getModuleName(script); 
			modules.add(name.asSymbol -> script.load);
		});
	}

	getModuleName { |input|
		 ^PathName(input)
		.fileNameWithoutExtension
		.lowerFirstChar; 
	}

	*directory_{|newPath|
		directory = PathStorage.path_(newPath, id);
	}

	moduleName_{|newModule, from|
		moduleName = newModule; 
		this.init(from);
	}
}
