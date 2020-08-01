CodexTemplater {
	classvar <>templateDir;
	var <>path;

	*initClass { templateDir = this.defaultPath }

	*new { | moduleFolder |
		moduleFolder ?? { Error("No path set.").throw };
		^super.newCopyArgs(moduleFolder.asString);
	}

	synthDef { | templateName("synthDef") |
		this.makeTemplate(templateName, this.defaultPath+/+"synthDef.scd");
	}

	pattern { | templateName("pattern") |
		this.makeTemplate(templateName, this.defaultPath+/+"pattern");
	}

	function { | templateName("function") |
		this.makeTemplate(templateName, this.defaultPath+/+"function.scd");
	}

	synth { | templateName("synth") |
		this.makeTemplate(templateName, this.defaultPath+/+"node.scd");
	}

	event { | templateName("event") |
		this.makeTemplate(templateName, this.defaultPath+/+"event.scd");
	}

	array { | templateName("array") |
		this.makeTemplate(templateName, this.defaultPath+/+"array.scd");
	}

	list { | templateName("list") |
		this.makeTemplate(templateName, this.defaultPath+/+"list.scd");
	}

	blank { | templateName("module") |
		this.makeTemplate(templateName, this.defaultPath+/+"module.scd");
	}

	makeTemplate { | templateName, sourcePath |
		this.class.copyFile(templateName, sourcePath, path);
	}

	*defaultPath { ^(this.filenameString.dirname.dirname+/+"Templates") }

	*copyFile { | templateName, sourcePath, path |
		var to = path+/+templateName.asString++".scd";
		try({ File.copy(sourcePath, to) }, { File.copy(sourcePath, to.increment) });
	}
}
