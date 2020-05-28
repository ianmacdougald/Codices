ModuleManager {
	classvar internalPath;
	classvar id = \modules;
	var <key, modules; 
	var moduleTemplater; 

	*new{ arg key(\default), from; 
		^super.newCopyArgs(key).initModules(from);
	}

	initModules {arg from;  
		moduleTemplater = moduleTemplater ?? { 
			ModuleTemplate.new(this.moduleFolder);
		};

		if(File.exists(this.moduleFolder).not, { 
			this.makeModuleFolder;
			this.makeModules(from);
			this.initModules;
		}, {this.loadModules});
	}

	makeModules { |from|
		if(from.notNil, { 
			var string = this.class.moduleFolder+/+from.asString; 
			this.copyToHere(string);
		}, {this.makeModuleTemplates});
	}

	copyToHere{ |pathToCopy|
		if(File.exists(pathToCopy), { 
			format(
				"cp -r % %", 
				pathToCopy, 
				this.modulePath
			).unixCmd(postOutput: false);
		}, {this.makeModuleTemplates});
	}

	makeModuleTemplates { 
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
		^(this.class.moduleFolder+/+key);
	}

	*moduleFolder { 
		^(this.class.moduleDirectory+/+this.name.asString);
	}

	makeModuleFolder { 
		this.class.makeModuleFolder(this.moduleFolder);
	}

	*makeModuleFolder {arg moduleFolder; 
		File.mkdir(moduleFolder);
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
