Templater {
	classvar <templatePath;
	var <>path;

	*new { |moduleFolder|
		moduleFolder ?? {Error("No path set.").throw};
		^super.newCopyArgs(moduleFolder.asString);
	}
	
	synthDef {|templateName("synthDef")| 
		this.class.makeTemplate(templateName, path, SynthDef);
	} 
	pattern {|templateName("pattern")|
		this.class.makeTemplate(templateName, path, Pattern);
	}
	function {|templateName("function")| 
		this.class.makeTemplate(templateName, path, Function);
	}
	synth {|templateName("synth")| 
		this.class.makeTemplate(templateName, path, Node);
	} 
	event {|templateName("event")|
		this.class.makeTemplate(templateName, path, Event);
	}
	blank {|templateName("module")|
		this.class.makeTemplate(templateName, path);
	} 
	array {|templateName("array")|
		this.class.makeTemplate(templateName, path, Array); 
	}

	*resetTemplatePath { 
		templatePath = (PathName(this.filenameSymbol.asString)
		.pathOnly+/+"templates");
	}

	*setTemplatePath { |newPath|
		templatePath = newPath;
	}

	*makeTemplate { |templateName, path, object| 
		var targetPath = path+/+templateName.asString++".scd";
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
