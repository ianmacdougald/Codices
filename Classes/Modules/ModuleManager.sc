ModuleManager {
	//module stuff
	*new{
		this.checkModulePath;
		^super.new;
	}

	//instance method(s)
	modulePathDialog {
		this.class.modulePathDialog;
	}

	//class methods
	*modulePath {
		^PathStorage.path(this.name);
	}

	*modulePath_{|newPath|
		PathStorage.path_(newPath, this.name);
	}

	*modulePathIsWritten {
		^this.modulePath.isNil.not;
	}

	*modulePathExists {
		^this.modulePath.pathMatch.isEmpty.not;
	}

	*modulePathDialog {
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
	}

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
