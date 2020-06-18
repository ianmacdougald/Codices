Composite {
	classvar <directory, <folderManager, id = 'sc-modules';
	var <moduleSet, <modules, templater;

	*new { | moduleSet(\default), from |
		this.establish;
		^super.newCopyArgs(moduleSet).getModules(from).initComposite;
	}

	*basicNew { | moduleSet(\default), from | 
		^super.newCopyArgs(moduleSet);
	}

	*establish { 
		directory ?? {directory = PathStorage.at(id) ?? { 
			PathStorage.setAt(this.defaultDirectory, id);
		}}; 
		folderManager ?? {
			folderManager = FolderManager.new(this.classFolder); 
		}; 
		this.checkDefaults;
	}

	*defaultDirectory { 
		^(Main.packages.asDict.at('CodexIan')+/+id);
	}
	
	*checkDefaults {
		var scripts = this.filenameString.path.getScriptPaths;
		if(this.classFolder.exists.not and: {scripts.isEmpty.not}, { 
			folderManager.copyFilesTo(
				scripts, 
				(this.classFolder+/+"default").mkdir
			);
		}); 
	}

	getModules { |from|
		templater = Templater(this.moduleFolder);
		this.processFolders(from);
		this.loadModules;
	}

	initComposite {}

	moduleFolder { 
		^(this.class.classFolder+/+moduleSet);
	}

	*classFolder { 
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
			modules.add(this.getModuleName(script) -> script.load);
		});
	}

	getModuleName { |input|
		 ^PathName(input)
		.fileNameWithoutExtension
		.lowerFirstChar.asSymbol; 
	}

	*directory_{|newPath|
		directory = PathStorage.setAt(newPath, id);
	}

	moduleSet_{|newSet, from|
		moduleSet = newSet; 
		this.getModules(from);
		this.initComposite;
	}

	*moduleSets {
		^PathName(this.classFolder).folders
		.collectAs({|m|m.folderName.asSymbol}, Set);
	}
}
