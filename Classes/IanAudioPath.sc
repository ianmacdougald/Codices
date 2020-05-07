//This is an example of how to configure a subclass of FileConfigurer
//More or less copy and paste this, replacing the defaultPath with some other.
IanAudioPath : PathStorage {
	classvar id;
	classvar internalPath;

	*defaultPath{
		^("~/Desktop/audio".standardizePath);
	}

	*path {
		if(internalPath.isNil){
			this.prSetID;
			internalPath = super.path(id);
		};
		^internalPath;
	}

	*path_{|newpath|
		internalPath = nil;
		this.prSetID;
		^super.path_(newpath, id);
	}

	*prSetID {
		id = id ?? {super.prSetID};
	}

	*id {
		this.prSetID;
		^id;
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