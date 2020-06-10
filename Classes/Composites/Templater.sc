Templater {
	classvar <templatePath;
	var <>path;

	*new { |moduleFolder|
		moduleFolder ?? {Error("No path set.").throw};
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
	synth {|moduleName("synth")| 
		this.class.makeTemplate(moduleName, path, Node);
	} 
	event {|moduleName("event")|
		this.class.makeTemplate(moduleName, path, Event);
	}
	blank {|moduleName("module")|
		this.class.makeTemplate(moduleName, path);
	} 
	array {|moduleName("array")|
		this.class.makeTemplate(moduleName, path, Array); 
	}

	*resetTemplatePath { 
		templatePath = (PathName(this.filenameSymbol.asString)
		.pathOnly+/+"templates");
	}

	*setTemplatePath { |newPath|
		templatePath = newPath;
	}

	*makeTemplate { |moduleName, path, object| 
		var targetPath = path+/+moduleName.asString++".scd";
		try({this.copyFile(object, targetPath)}, {
			this.copyFile(object, targetPath.increment);
		});
	}

	*copyFile {|type("blank"), filename|
		var currentPath;
		templatePath ?? {this.resetTemplatePath};
		currentPath = templatePath+/+type.lowerFirstChar++".scd";
		File.copy(currentPath, filename);
	}
}
