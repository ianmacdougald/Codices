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

	// exists { ^File.exists(this); }
	exists { ^this.pathMatch.isEmpty.not }

	increment {
		^FileIncrementer(PathName(this).fileName, this.dirname).increment;
	}

	getFilePaths {
		^PathName(this).files.collect(_.fullPath);
	}

	getFileNames {
		^PathName(this).files.collect(_.fileName);
	}

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
	isPath {
		^this.asString.isPath;
	}
}
