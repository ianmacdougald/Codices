FolderManager { 
	var <folder; 

	*new { |folder|
		^super.newCopyArgs(folder);
	}
	
	copyContents { |from, to|
		var toPath;
		from = this.tagPath(from.asString); 
		to = this.tagPath(to.asString); 
		to.mkdir;
		PathName(from).files.do{|file|
			var name = file.fileName; 
			File.copy(file.fullPath, to+/+name);
		}
	}

	tagPath { |input|
		 ^if(PathName(input).isRelativePath, {
			 folder+/+input
		 }, {input});
	}

	folder_{|newFolder|
		folder = newFolder; 
	}
}
