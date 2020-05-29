ModuleManager {
	classvar internalPath;
	classvar id = \modules;
	var <moduleName, <modules;
	var templater, nameIsPath;

	*new {|moduleName(\default), from|
		^super.newCopyArgs(moduleName).initModules(from);
	}

	initModules {arg from;  
		nameIsPath = moduleName.isPath;
		templater = ModuleTemplater(this.moduleFolder);
		if(File.exists(this.moduleFolder).not, { 
			this.makeModuleFolder;
			this.makeModules(from);
		});
		this.loadModules;
	}

	moduleName_{|newName, from|
		if(newName.notNil, { 
			moduleName = newName.asSymbol; 
			this.initModules(from);
		}); 
	}

	makeModules { |from|
		if(from.notNil, {
			from = from.asString;
			if(from.isPath.not, {
				from = this.class.moduleFolder
				+/+from;
			});
			this.copyToHere(from.postln);
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

	*loadModules {|scriptPaths|
		var objs = ();
		scriptPaths.do({|script|
			if(script.isString, {
				var name = this.getModuleName(script);
				objs.add(name.asSymbol -> script.load);
			});
		});
		^objs;
	}

	*getModuleName {|script|
		var name = PathName(script)
		.fileNameWithoutExtension;
		name[0] = name[0].toLower;
		^name;
	}

	moduleFolder {
		if(nameIsPath, {^moduleName});
		^(this.class.moduleFolder+/+moduleName);
	}

	*moduleFolder {
		^(this.moduleDirectory+/+this.name.asString);
	}

	makeModuleFolder {
		this.class.makeModuleFolder(this.moduleFolder);
	}

	*makeModuleFolder {|moduleFolder|
		format("mkdir -p %", moduleFolder).unixCmd(postOutput:false);
		// File.mkdir(moduleFolder);
		//format("\"mkdir -p %\"", moduleFolder).unixCmd;
	}

	openModules { //not implemented yet...
	}

	scriptPaths {
		^this.class.scriptPaths(this.moduleFolder);
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

	*defaultPath {
		^(Main.packages.asDict.at('CodexIan') +/+ "sc-modules");
	}

	*setDefaultPath {
		^this.moduleDirectory_(
			this.defaultPath, id
		);
	}

	*moduleDirectory_{|newpath|
		internalPath = PathStorage.path_(newpath, id);
		^internalPath;
	}

	*moduleDirectory {
		internalPath = internalPath ?? {
			PathStorage.path(id) ?? {
				^this.setDefaultPath;
			};
		};
		^internalPath;
	}
}
