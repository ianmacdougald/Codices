IanAudioPath {
	classvar internalPath;

	*defaultPath {
		^("~/Desktop/audio".standardizePath);
	}

	*path {
		if(internalPath.isNil){
			internalPath = PathStorage.at(this.name);
			if(internalPath.isNil){
				internalPath = this.setDefaultPath; 
			};
		};
		^internalPath;
	}

	*set {|newpath|
		internalPath = nil;
		^PathStorage.setAt(newpath, this.name);
	}

	*setDefaultPath {
		^this.set(this.defaultPath);
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
		IanAudioPath.set(newpath);
		^this.ianAudioPath;
	}

	asBuffer { |server(Server.default), startFrame(0),
		numFrames(-1), action, bufnum|
		^Buffer.read(server, this, startFrame, numFrames, action, bufnum);
	}

}

