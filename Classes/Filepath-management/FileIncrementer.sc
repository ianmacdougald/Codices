FileIncrementer {
	var fileTemplate, <folder, <>extension;
	var <currentIncrement, previousFileName;

	*new {|fileTemplate = "some-file.wav", folder|
		^super.new
		.fileTemplate_(fileTemplate)
		.folder_(folder)
	}

	fileTemplate_{|newTemplate|
		var pathname = PathName(newTemplate);
		extension = pathname.extension;
		fileTemplate = PathName(pathname.fileNameWithoutExtension);
		currentIncrement = fileTemplate.endNumber;
		fileTemplate = fileTemplate.noEndNumbers;
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
		if(previousFileName.isNil or: {File.exists(previousFileName)}){
			previousFileName = this.prFindNextFileName;
		};
		^previousFileName;
	}

	prFormatFileName {|template|
		var return = folder+/+template;
		if(extension.isEmpty.not){
			return = return++"."++extension;
		};
		^return;
	}

	prFindNextFileName {
		var tmpInc = currentIncrement + 1;
		var filename = this.prFormatFileName(fileTemplate++tmpInc);
		while({File.exists(filename)}, {
			tmpInc = tmpInc + 1;
			filename = this.prFormatFileName(fileTemplate++tmpInc);
		});
		currentIncrement = tmpInc;
		^filename;
	}
}

+ PathName {
	increment {
		^FileIncrementer.new(this.fileName, this.pathOnly).increment;
	}
}