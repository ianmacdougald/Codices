+ String {

	normalizePathAudio{ | level(1.0), server(Server.default), 
		sampleFormat("int24") |
		var buffer, pathname = PathName(this);
		if(level <= 0.0, {level = level.dbamp});
		Buffer.read(server, this, action: { | buffer |
			buffer.normalize(level);
			File.delete(this);
			buffer.write(
				this,
				pathname.extension,
				sampleFormat,
			);
			buffer.free;
		});
	}

	asBuffer { | server(Server.default), startFrame(0), 
		numFrames(-1), action, bufnum | 
		^Buffer.read(server, this, startFrame, 
			numFrames, action, bufnum);
	}

	getBuffers { | server, startFrame, numFrames, action | 
		^this.getAudioPaths.collect(
			_.asBuffer(server, startFrame, numFrames, action)
		);
	}

	lowerFirstChar { ^this.replace(this.at(0), this.at(0).toLower) }

	// exists { ^File.exists(this); }
	exists { ^this.pathMatch.isEmpty.not }

	increment {
		var path = PathName(this);
		^CodexIncrementer(
			path.fileName, 
			path.pathOnly
		).increment;
	}

	getFilePaths { ^PathName(this).files.collect(_.fullPath) }

	getFileNames { ^PathName(this).files.collect(_.fileName) }

	copyFilesTo { | newDirectory |
		this.getFilePaths.do { | path |
			var name = PathName(path).fileName;
			File.copy(path, newDirectory+/+name);
		}
	}

	copyScriptsTo { | newDirectory |
		var scripts = this.getScriptPaths;
		if(scripts.notEmpty, {
			scripts.do { | path |
				var name = PathName(path).fileName;
				File.copy(path, newDirectory+/+name);
			}
		});
	}

	copyFolder { | newFolder |
		if(newFolder.exists.not, {
			this.copyFilesTo(newFolder.mkdir);
		}, { "Warning: String: copy failed; target exists.".postln; });
	}
}

+ Object {
	lowerFirstChar {
		^this.asString.lowerFirstChar;
	}
}

+ Symbol {
	isPath { ^this.asString.isPath }
}
