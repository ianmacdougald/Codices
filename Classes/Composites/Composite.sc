Composite {
	classvar isInit = false, id = 'sc-modules';
	classvar <directory, <folderManager;
	var <moduleSet, <modules, templater;

	*new { |moduleSet(\default), from|
		if(isInit.not, {this.initComposite});
		this.checkDefaults;
		^super.newCopyArgs(moduleSet)
		.initComposite(from).basicNew;	
	}

	*initComposite { 
		directory = PathStorage.at(id) ?? { 
			PathStorage.setAt(
				this.defaultDirectory, 
				id	
			);
		};
		folderManager = FolderManager.new(this.moduleFolder);
	}

	*defaultDirectory { 
		^(Main.packages.asDict.at('CodexIan')+/+id);
	}
	
	*checkDefaults {
		var scripts = this.filenameString.path.getScriptPaths;
		if(this.moduleFolder.exists.not and: {scripts.isEmpty.not}, { 
			folderManager.copyFilesTo(
				scripts, 
				(this.moduleFolder+/+"default").mkdir
			);
		}); 
	}

	initComposite { |from|
		templater = Templater(this.moduleFolder);
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
			from !? {fm.mkdirCopy(from, moduleSet)} ?? {
				this.moduleFolder.mkdir;
				this.makeTemplates;
			};
		});
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
		this.initComposite(from);
	}

	*moduleSets {
		^PathName(this.moduleFolder).folders
		.collectAs({|m|m.folderName.asSymbol}, Set);
	}
}
