+ String{
	//return a flat Array of all filepaths associated with a string of a certain file path
	getPaths {
		^PathName(this).getPaths;
	}
}

+ Buffer{
	getPaths {
		^PathName(this.path).getPaths;
	}
}

+ PathName{
	getPaths {
		var entries;
		if(this.isFile, {
			^[fullPath];
		});
		^this.entries.getPaths;
	}
}

+ Collection{
	getPaths {
		//This method takes a collection of strings referring to paths or buffers or even pathnames
		//and returns a single dimensional array containing all of the files referred to by the
		//paths associated with that object
		var strings = [];
		//run through the collection item by item
		this.do{|item, index|
			strings = strings++item.getPaths;
		};
		^strings;
	}
}

+ Object{
	getPaths{
		^nil;
	}
}
