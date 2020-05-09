ModuleManager {
	//module stuff
	*new{
		this.prCheckModulePath;
		^super.new;
	}

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

	*defaultModulePath{
		this.subclassResponsibility(thisMethod);
	}

	*isValidModule { |string|
		^(PathName(string).extension=="scd");
	}

	*loadModules {
		var paths = this.modulePath.getPaths;
		var objects = paths.select({|item|
			this.isValidModule(item);
		});
		objects = objects.collect({|item|
			var name = PathName(item).fileNameWithoutExtension;
			name[0] = name[0].toLower;
			[name.asSymbol, item.load]
		}).flatten(1).asEvent;
		^objects;
	}

	*prCheckModulePath {
		if(this.modulePathIsWritten.not){
			this.modulePath_(this.defaultModulePath);
		};
	}
}
