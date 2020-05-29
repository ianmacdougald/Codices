ModuleTemplater {
	var <>path;

	*new { |moduleFolder|
		if(moduleFolder.isNil, { 
			Error("No path set for ModuleTemplater").throw;
		}); 

		^super.newCopyArgs(moduleFolder.asString);
	}
	
	synthDef {|moduleName("synthDef")| 
		this.class.makeTemplate(moduleName, path, SynthDef);
	} 
	pattern {|moduleName("pattern")|
		this.class.makeTemplate(moduleName, path, Pattern);
	}
	function {|moduleName("function")| 
		this.class.makeTemplate(moduleName, path, Function);
	}
	node {|moduleName("node")| 
		this.class.makeTemplate(moduleName, path, Node);
	} 
	event {|moduleName("event")|
		this.class.makeTemplate( moduleName, path, Event);
	}
	blank {|moduleName("module")|
		this.class.makeTemplate(moduleName, path);
	} 
	array {|moduleName("array")|
		this.class.makeTemplate(moduleName, path, Array); 
	}

	*modulePathString {|moduleName, path|
		if(path.isNil, { 
			Error("No path set for ModuleTemplater").throw;
		});
		^(path+/+moduleName.asString++".scd");
	}

	*moduleTemplatePath { 
		^(PathName(this.filenameSymbol.asString)
		.pathOnly+/+"templates");
	}

	*makeTemplate { |moduleName, path, object| 
		var targetPath = path+/+moduleName.asString++".scd";
		this.copyFile(object, targetPath);
	}

	*copyFile {|type("blank"), filename|
		var templatePath;
		if(filename.isNil, { 
			Error("No path set for ModuleTemplater").throw;
		});
		templatePath = this.moduleTemplatePath
		+/+this.firstToLower(type)++".scd";
		format("cp % %", templatePath, filename)
		.unixCmd(postOutput:false); 
	}

	*firstToLower {|input|
		input = input.asString; 
		input[0] = input[0].toLower; 
		^input;
	}
}
