CodexTemplater {
	classvar <>templateDir;
	var <>path;

	*new { | moduleFolder |
		moduleFolder ?? {Error("No path set.").throw};
		^super.newCopyArgs(moduleFolder.asString);
	}

	synthDef { | templateName("synthDef") |
		this.makeTemplate(templateName, SynthDef);
	}

	pattern { | templateName("pattern") |
		this.makeTemplate(templateName, Pattern);
	}

	function { | templateName("function") |
		this.makeTemplate(templateName, Function);
	}

	synth { | templateName("synth") |
		this.makeTemplate(templateName, Node);
	}

	event { | templateName("event") |
		this.makeTemplate(templateName, Event);
	}

	blank { | templateName("module") |
		this.makeTemplate(templateName);
	}

	array { | templateName("array") |
		this.makeTemplate(templateName, Array);
	}

	list { | templateName("list") | 
		this.makeTemplate(templateName, List);
	}

	makeExtTemplate { | templateName, object, path |
		this.setTemplateDir(path ?? { this.class.defaultPath });
		{this.makeTemplate(templateName, object)}
		.protect({this.resetTemplateDir});
		this.resetTemplateDir;
	}

	makeTemplate { | templateName, object |
		this.class.targetCopy(templateName, path, object);
	}

	resetTemplateDir {
		this.class.templateDir = this.class.defaultPath;
	}

	setTemplateDir { | newPath |
		this.class.templateDir_(newPath);
	}

	*targetCopy { | templateName, path, object|
		var targetPath = path+/+templateName.asString++".scd";
		try({this.copyFile(object, targetPath)}, {
			this.copyFile(object, targetPath.increment);
		});
	}

	*defaultPath {
		^(PathName(this.filenameString).pathOnly+/+"Templates");
	}

	*copyFile { |type("blank"), filename|
		var currentPath;
		templateDir ?? {templateDir = this.defaultPath};
		currentPath = templateDir+/+type.lowerFirstChar++".scd";
		File.copy(currentPath, filename);
	}
}
