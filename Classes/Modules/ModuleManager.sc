ModuleManager {
	classvar internalPath;
	classvar id = \modules;
	var objectDictionary;

	*new{
		^super.new;
	}

	*defaultPath {
		^(Main.packages.asDict.at('CodexIan') +/+ "sc-modules");
	}

	*moduleFolder {
		internalPath = internalPath ?? {
			var return = PathStorage.path(id);
			if(return.isNil, {
				return = this.moduleFolderPath_(this.defaultPath, id);
			});
            return;
		};
		^internalPath;
	}

	*moduleFolder_{|newPath, id|
		^PathStorage.path_(newPath, id);
	}

	getModules {|key|
		this.moduleFolder+/+key.asString;
	}

	scriptDictionary {|key, path|
		var scripts = this.getValidPaths(path);
		var dictionary = Dictionary.new;
		scripts.do({|script|
			var name = PathName(script)
			.fileNameWithoutExtension.asSymbol;
			dictionary.add(key -> script);
		});
		^dictionary;
	}

	// objectDictionary {|path|
	// 	var dictionary = Dictionary.new;
	// 	this.scriptDictionary.keysValuesDo({|key, script|
	// 		dictionary.putPairs([key, script.load]);
	// 	});
	// 	^dictionary;
	// }
	//
	// loadObjects {
	//
	// }

	getValidPaths {|path|
		^PathName(path).files.select({|file|
			this.isValidPath(file);
		});
	}

	isValidPath { |path|
		^(PathName(path).extension=="scd");
	}


	/*	//instance method(s)
	modulePathDialog {
	this.class.modulePathDialog;
	}

	//class methods
	*modulePath {
	^ModuleStorage.path(this.name);
	}

	*modulePath_{|newPath|
	PathStorage.path_(newPath, this.name);
	}*/

	/*	*modulePathIsWritten {
	^this.modulePath.isNil.not;
	}

	*modulePathExists {
	^this.modulePath.pathMatch.isEmpty.not;
	}*/

	/*	*modulePathDialog {
	FileDialog(
	{|newPath|
	fork{
	PathStorage.path_(newPath, this.name);
	while({this.modulePath.isNil}, {1e-4.wait});
	this.loadModules;
	};
	}, {},
	fileMode: 2,
	stripResult: true,
	path: this.defaultModulePath
	);
	}

	*openModuleFolder {
	FileDialog(
	{|path|
	Document.open(path);
	}, {},
	fileMode: 3,
	stripResult: true,
	path: this.modulePath
	);
	}*/

	*openAllModules{
		this.modulePath.getPaths.do{|item|
			Document.open(item);
		};
	}

	*openModule{|moduleName|
		Document.open(this.modulePath+/+moduleName);
	}

	*defaultModulePath{^("~".standardizePath)}

	*isValidModule { |string|
		^(PathName(string).extension=="scd");
	}

	*validModulePaths {
		^this.modulePath.getPaths.select({|item|
			this.isValidModule(item);
		});
	}

	*checkModulePath {
		if(this.modulePathIsWritten.not){
			if(this.modulePathIsWritten.not, {
				this.modulePathDialog;
			});
		};
	}

	*lowerFirstChar {|filename|
		filename[0] = filename[0].toLower;
		^filename;
	}

	*loadModules {
		^this.validModulePaths.collect({|item|
			var name = PathName(item).fileNameWithoutExtension;
			[this.lowerFirstChar(name).asSymbol, item.load];
		}).flatten(1).asEvent;
	}

}

/*ModuleManager : StoragePath {
classvar quarkPath;
classvar moduleFolder;
classvar id = \Modules;

*quarkPath {
quarkPath = quarkPath ?? {
Main.packages.asDict.at('CodexIan');
};
^quarkPath;
}

*defaultPath {
this.quarkPath +/+ id.asString;
}

*modulePath {
if(internalPath.isNil){
internalPath = PathStorage.path(id);
if(internalPath.isNil, {
internalPath = PathStorage.path_(this.defaultPath, id);
});
};
^internalPath;
}

*modulePath_{|newpath|
internalPath = nil;
^PathStorage.path_(newpath, id);
}

classModulesPath {|id|
^(this.path +/+ id.asString);
}
}*/
