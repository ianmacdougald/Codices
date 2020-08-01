CodexTemplater {
	classvar defaultPath;
	var <>path;

	*initClass { defaultPath = this.filenameString.dirname.dirname+/+"Templates" }

	*new { | moduleFolder |
		moduleFolder ?? { Error("No path set.").throw };
		^super.newCopyArgs(moduleFolder.asString);
	}

	synthDef { | templateName("synthDef") |
		this.makeTemplate(templateName, defaultPath+/+"synthDef.scd");
	}

	pattern { | templateName("pattern") |
		this.makeTemplate(templateName, defaultPath+/+"pattern.scd");
	}

	function { | templateName("function") |
		this.makeTemplate(templateName, defaultPath+/+"function.scd");
	}

	synth { | templateName("synth") |
		this.makeTemplate(templateName, defaultPath+/+"node.scd");
	}

	event { | templateName("event") |
		this.makeTemplate(templateName, defaultPath+/+"event.scd");
	}

	array { | templateName("array") |
		this.makeTemplate(templateName, defaultPath+/+"array.scd");
	}

	list { | templateName("list") |
		this.makeTemplate(templateName, defaultPath+/+"list.scd");
	}

	blank { | templateName("module") |
		this.makeTemplate(templateName, defaultPath+/+"module.scd");
	}

	makeTemplate { | templateName, sourcePath |
		this.class.copyFile(templateName, sourcePath, path);
	}

	*copyFile { | templateName, sourcePath, path |
		var to = path+/+templateName.asString++".scd";
		try({ File.copy(sourcePath, to) }, { File.copy(sourcePath, to.increment) });
	}
}
