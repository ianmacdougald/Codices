CodexTemplater {
	classvar <>templateDir;
	var <>path;

	*initClass { templateDir = this.defaultPath }

	*new { | moduleFolder |
		moduleFolder ?? { Error("No path set.").throw };
		^super.newCopyArgs(moduleFolder.asString);
	}

	synthDef { | templateName("synthDef") |
		this.makeTemplate(templateName, "synthDef");
	}

	pattern { | templateName("pattern") |
		this.makeTemplate(templateName, "pattern");
	}

	function { | templateName("function") |
		this.makeTemplate(templateName, "function");
	}

	synth { | templateName("synth") |
		this.makeTemplate(templateName, "node");
	}

	event { | templateName("event") |
		this.makeTemplate(templateName, "event");
	}

	array { | templateName("array") |
		this.makeTemplate(templateName, "array");
	}

	list { | templateName("list") |
		this.makeTemplate(templateName, "list");
	}

	blank { | templateName("module") |
		this.makeTemplate(templateName);
	}

	makeExtTemplate { | templateName, fileName, path |
		this.setTemplateDir(path ?? { this.class.defaultPath });
		{this.makeTemplate(templateName, fileName)}
		.protect({this.resetTemplateDir});
		this.resetTemplateDir;
	}

	makeTemplate { | templateName, fileName |
		this.class.copyFile(templateName, fileName, path);
	}

	resetTemplateDir { this.class.templateDir = this.class.defaultPath }

	setTemplateDir { | newPath | this.class.templateDir_(newPath) }

	*defaultPath { ^(this.filenameString.dirname.dirname+/+"Templates") }

	*copyFile { | templateName, fileName, path |
		var from  = templateDir+/+templateName.asString++".scd";
		var to = path+/+templateName.asString++".scd";
		try({ File.copy(from, to) }, { File.copy(from, to.increment) });
	}
}
