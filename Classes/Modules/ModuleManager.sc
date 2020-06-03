//The ModuleManger is responsible for managing folders of modules and loading them.
//Can we make it so that the ModuleManager is responsible for loading modules and oversees a class that manages the folders?
//What would i call the new stuff?
//I think the folder class can be called ModuleFolder
//And the class that manages everything can be just be called Module or maybe 
//ModuleSystem is better
//Or ModularSystem is better or...Modular...I like Modular
ModuleManager {
	classvar internalPath;
	classvar id = \modules;
	var <moduleName, <modules;
	var templater, nameIsPath;

	*new {|moduleName, from|
		^super.newCopyArgs(moduleName).initModules(from);
	}

	initModules { |from|
		moduleName = moduleName ?? {
			if(File.exists(
				this.class.moduleFolder+/+"default"
			).not, {from = this.defaultsFolder});
			\default;
		};
		this.setVars;
		this.checkForFolder(from);
		this.loadModules;
	}

	defaultsFolder {
		^(this.class.implementationFolder+/+"default");
	}

	*implementationFolder {
		^PathName(this.filenameSymbol.asString).pathOnly;
	}

	*defaultsFolderName {
		this.subclassResponsibility(thisMethod);
	}

	setVars {
		nameIsPath = moduleName.isPath;
		templater = ModuleTemplater(this.moduleFolder);
	}

	moduleFolder {
		if(nameIsPath, {^moduleName});
		^(this.class.moduleFolder+/+moduleName);
	}

	*moduleFolder {
		^(this.moduleDirectory+/+this.name.asString);
	}

	*moduleDirectory {
		internalPath = internalPath ?? {
			PathStorage.path(id) ?? {
				^this.setDefaultPath;
			};
		};
		^internalPath;
	}

	*moduleDirectory_{|newpath|
		internalPath = PathStorage.path_(newpath, id);
		^internalPath;
	}

	*defaultPath {
		^(Main.packages.asDict.at('CodexIan') +/+ "sc-modules");
	}

	*setDefaultPath {
		^this.moduleDirectory_(
			this.defaultPath, id
		);
	}

	checkForFolder { |from|
		if(File.exists(this.moduleFolder).not, {
			this.makeModuleFolder;
			this.makeModules(from);
		});
	}

	makeModuleFolder {
		this.class.makeModuleFolder(this.moduleFolder);
	}

	*makeModuleFolder {|moduleFolder|
		format("mkdir -p %", moduleFolder).unixCmd(postOutput:false);
		// File.mkdir(moduleFolder);
		//format("\"mkdir -p %\"", moduleFolder).unixCmd;
	}

	makeModules { |from|
		if(from.notNil, {
			from = from.asString;
			if(from.isPath.not, {
				from = this.class.moduleFolder
				+/+from;
			});
			this.copyToHere(from);
		}, {this.makeTemplates});
	}

	copyToHere{ |pathToCopy|
		if(File.exists(pathToCopy), {
			format(
				"cp -ra % %",
				pathToCopy+/+".",
				this.moduleFolder
			).unixCmd;
		}, {this.makeTemplates});
	}

	makeTemplates {
		this.subclassResponsibility(thisMethod);
	}

	loadModules {
		modules = this.class.loadModules(this.scriptPaths);
	}

	*loadModules { |scriptPaths|
		var objs = ();
		scriptPaths.do({|script|
			if(script.isString, {
				var name = this.getModuleName(script);
				objs.add(name.asSymbol -> script.load);
			});
		});
		^objs;
	}

	*scriptPaths {|path|
		^this.getValidPaths(path);
	}

	*getValidPaths {|path|
		^path.getPaths.select({|item|
			this.isValidPath(item);
		});
	}

	*isValidPath {|path|
		^(path.extension=="scd");
	}

	*getModuleName {|script|
		var name = PathName(script)
		.fileNameWithoutExtension;
		name[0] = name[0].toLower;
		^name;
	}

	scriptPaths {
		^this.class.scriptPaths(this.moduleFolder);
	}

	moduleName_{|newName, from|
		if(newName.notNil, {
			moduleName = newName.asSymbol;
			this.initModules(from);
		});
	}

	openModules { //not implemented yet...
	}
}
