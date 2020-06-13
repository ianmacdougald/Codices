FileIncrementer {
	var fileTemplate, <>folder, <>extension;
	var <currentIncrement, previousFileName;

	*new {|fileTemplate = "some-file.wav", folder|
		^super.new
		.fileTemplate_(fileTemplate)
		.folder_(folder)
	}

	fileTemplate_{|newTemplate|
		var pathname = PathName(newTemplate);
		extension = pathname.extension;
		pathname = pathname.fileNameWithoutExtension;
		currentIncrement = PathName(pathname).endNumber;
		fileTemplate = PathName(pathname).noEndNumbers;
	}

	fileTemplate {
		^(fileTemplate++"."++extension);
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

}

+ PathName {
	increment {
		^FileIncrementer.new(this.fileName, this.pathOnly).increment;
	}
}
