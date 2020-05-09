//This is an example of how to configure a subclass of FileConfigurer
//More or less copy and paste this, replacing the defaultPath with some other.
IanAudioPath : PathStorage {
	classvar internalPath;

	*defaultPath{
		^("~/Desktop/audio".standardizePath);
	}

	*path {
		if(internalPath.isNil){
			internalPath = super.path(this.name);
			if(internalPath.isNil){
				internalPath = this.prSetDefaultPath;
			};
		};
		^internalPath;
	}

	*path_{|newpath|
		internalPath = nil;
		^super.path_(newpath, this.name);
	}

	*prSetDefaultPath {
		var default = this.defaultPath;
		this.path_(default);
		^default;
	}
}

+ String {

	*ianAudioPath {
		^("".ianAudioPath);
	}

	ianAudioPath {
		var path;
		while({path.isNil}, {path = IanAudioPath.path});
		^(path +/+ this);
	}

	ianAudioPath_{|newpath|
		IanAudioPath.path_(newpath);
		^this.ianAudioPath;
	}

}
