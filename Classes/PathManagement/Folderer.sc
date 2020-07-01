Folderer {

	*new { | directory | ^super.new; }

	copyFile { | from, to | File.copy(from, to); }

	exists { | path | ^path.pathMatch.isEmpty.not; }



	copyFolder { | from, to |
		var newPath = this.getFullPath(to);
		if(this.folderExists(newPath).not, {
			this.copyFilesTo(this.getFiles(from), newPath.mkdir);
		}, { "Warning: Folderer: \"to\" path already exists.".postln});
	}

	copyFilesTo { |files, targetDir|
		files.do{ | file |
			var fileName = PathName(file).fileName;
			this.copyFile(file, targetDir+/+fileName);
		};
	}

	getFiles { | where |
		^PathName(this.getFullPath(where))
		.files.collect(_.fullPath);
	}

}