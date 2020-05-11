+ String{

	getPaths {
		^PathName(this).getPaths;
	}

	getAudioPaths {
		^this.getPaths.select({|item|
			this.isValidPath(item);
		});
	}

	isValidPath { |input|
		var ext = PathName(input).extension;
		^this.validAudioPaths.find([ext]).isNil.not;
	}

	validAudioPaths {
		^[
			"wav",
			"aiff",
			"oof",
			"mp3"
		];
	}

}

+ Buffer{

	getPaths {
		^PathName(this.path).getPaths;
	}

}

+ PathName{

	getPaths {
		var entries;
		if(this.isFile, {
			^[fullPath];
		});
		^this.entries.getPaths;
	}

	getAudioPaths {
		fullPath.getAudioPaths;
	}

}

+ Array {

	getPaths {
		var strings = [];
		this.do{|item, index|
			strings = strings++item.getPaths;
		};
		^strings.as(this.class);
	}

	getAudioPaths {
		var strings = [];
		this.do{|item, index|
			strings = strings++item.getAudioPaths;
		};
		^strings.as(this.class);
	}

}

+ Object{

	getPaths{
		^nil;
	}

}
