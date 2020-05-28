+ String { 
	isPath { 
		^this.pathMatch.isEmpty.not;
	}

	normalizePathAudio{|level(1.0), server(Server.default)|
		if(this.isPath, { 
			var buffer, pathname = PathName(this); 
			if(level <= 0.0, {level = level.dbamp}); 
			buffer = Buffer.read(server, this, action: {  
				buffer.normalize(level); 
				File.delete(this); 
				buffer.write(	
					this, 
					pathname.extension, 
					"int24"
				); 
				buffer.free; 
			});
		});
	}
}

+ Symbol { 
	isPath { 
		^this.asString.isPath;
	}
}

