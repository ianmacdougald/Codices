FileIncrementer {
	var fileTemplate, <folder, <>extension;
	var <currentIncrement, previousFileName;

	*new {|fileTemplate = "some-file.wav", folder|
		^super.new
		.fileTemplate_(fileTemplate)
		.folder_(folder)
	}

	fileTemplate_{|newTemplate|
		extension = newTemplate.extension;
		fileTemplate = newTemplate.removeExtension;
		currentIncrement = this.getEndNumber(fileTemplate);
		fileTemplate = this.noEndNumber(fileTemplate);
	}

	fileTemplate {
		^(fileTemplate++"."++extension);
	}

	folder_{|newFolder|
		if(newFolder.isString.not){
			case
			{newFolder.isNil}{newFolder = "~/Desktop".standardizePath}
			{newFolder.isKindOf(PathName)}{newFolder = newFolder.pathOnly}
			{newFolder.isKindOf(Buffer)}{newFolder = newFolder.path};
		};
		folder = newFolder;
	}

	increment {
		if(previousFileName.isNil or: {previousFileName.exists}){
			previousFileName = this.nextFileName;
		};
		^previousFileName;
	}

	reset {
		currentIncrement = -1;
	}

	formatFileName {|template|
		var return = folder+/+template;
		if(extension.isEmpty.not){
			return = return++"."++extension;
		};
		^return;
	}

	nextFileName {
		var tmpInc = currentIncrement + 1;
		var filename = this.formatFileName(fileTemplate++tmpInc);
		while({filename.exists}, {
			tmpInc = tmpInc + 1;
			filename = this.formatFileName(fileTemplate++tmpInc);
		});
		currentIncrement = tmpInc;
		^filename;
	}

	getEndNumber {|input|
		^PathName(input).endNumber;
	}

	noEndNumber {|input|
		^PathName(input).noEndNumbers;
	}
}

+ PathName {
	increment {
		^FileIncrementer.new(this.fileName, this.pathOnly).increment;
	}
}
