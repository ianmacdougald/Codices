FolderManager { 
	var <folder; 

	*new { |folder|
		^super.newCopyArgs(folder);
	}

	copy { |from to| 
		File.copy(from, to);	
	}

	mkdirCopy { |from, to| 
		var targetPath = this.tagPathExists(to);
		targetPath !? {
			this.copyFilesTo(this.getFiles(from), targetPath.mkdir);
		} ?? {"Warning: \"to\" path already exists.".postln;};
	}

	copyFilesTo { |files, targetDir|
		files.do{|file|
			var fileName = PathName(file).fileName;
			this.copy(file, targetDir+/+fileName);
		};
	}

	getFiles { |where|
		^PathName(this.tagPath(where))
		.files.collect(_.fullPath);
	}
	
	tagPath { |input|
		input = input.asString;
		^if(PathName(input).isRelativePath, {
			folder+/+input
		}, {input});
	}

	tagPathExists { |input|
		var str = this.tagPath(input); 
		^if(str.exists.not, {str}, {nil});
	}

	folder_{ |newFolder|
		folder = newFolder; 
	}

}
