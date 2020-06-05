+ String {

	normalizePathAudio{|level(1.0), server(Server.default), sampleFormat("int24")|
		if(this.isPath, {
			var buffer, pathname = PathName(this);
			if(level <= 0.0, {level = level.dbamp});
			buffer = Buffer.read(server, this, action: {
				buffer.normalize(level);
				File.delete(this);
				buffer.write(
					this,
					pathname.extension,
					"int24",
				);
				buffer.free;
			});
		});
	}

	lowerFirstChar { 
		^this.replace(this.at(0), this.at(0).toLower);
	}

	exists { 
		^File.exists(this);	
	}

	path { 
		^PathName(this).pathOnly;
	}
}

+ Object { 
	lowerFirstChar { 
		^this.asString.lowerFirstChar;
	}
}

+ Symbol {
	isPath {
		^this.asString.isPath;
	}
}
