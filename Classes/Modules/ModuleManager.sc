ModuleManager {
	classvar internalPath;
	classvar id = \modules;
	var key, modules; 

	*new{ arg key; 
		^super.newCopyArgs(key).initModules;
	}

	initModules { 
		if(File.exists(this.moduleFolder).not, { 
			this.makeModuleFolder;
		}, {this.loadModules});
	}

	loadModules { 
		modules = this.class.loadModules(this.scriptPaths);
	}

	*loadModules {|scriptPaths|
		var objs = ();
		scriptPaths.do({|script|
			if(script.isString, {
				var name = PathName(script)
				.fileNameWithoutExtension.asSymbol;
				objs.add(name -> script.load);
			});
		});
		^objs;
	}

	moduleFolder {
		this.moduleDirectory+/+this.name.asString+/+key.asString;
	}

	makeModuleFolder { 
		this.class.makeModuleFolder(this.moduleFolder);
	}

	*makeModuleFolder {arg moduleFolder; 
		File.mkdir(moduleFolder);
		//format("\"mkdir -p %\"", moduleFolder).unixCmd; 
	}

	openModules {
		//not implemented yet...
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

	*moduleDirectory_{|newpath, id|
		internalPath = PathStorage.path_(newpath, id);
		^internalPath;
	}

	*moduleDirectory {
		internalPath = internalPath ?? {
			PathStorage.path(id) ?? {
				^this.defaultSetPath;
			};
		};
		^internalPath;
	}
}
