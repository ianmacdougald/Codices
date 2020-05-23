//This is an example of how to configure a subclass of FileConfigurer
//More or less copy and paste this, replacing the defaultPath with some other.
IanAudioPath {
	classvar internalPath;

	*defaultPath{
		^("~/Desktop/audio".standardizePath);
	}

	*path {
		if(internalPath.isNil){
			internalPath = PathStorage.path(this.name);
			if(internalPath.isNil){
				internalPath = this.path_(this.defaultPath);
			};
		};
		^internalPath;
	}

	*path_{|newpath|
		internalPath = nil;
		^PathStorage.path_(newpath, this.name);
	}

	*setDefaultPath {
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

	asBuffer { |server(Server.default), startFrame(0),
		numFrames(-1), action, bufnum|
		^Buffer.read(server, this, startFrame, numFrames, action, bufnum);
	}

}

