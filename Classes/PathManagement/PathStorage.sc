PathStorage  {
	classvar quarkPath;
	classvar dictionary;

	*storagePath {
		^(this.pathToQuark +/+ format("%.YAML", this.name))
	}

	*parse {
		^this.storagePath.parseYAMLFile;
	}

	*write { |item|
		var fd = File.open(this.storagePath, "w+");
		fd.write(item.asYAMLString);
		fd.close;
	}

	*pathToQuark {
		quarkPath = quarkPath ?? {
			Main.packages.asDict.at('CodexIan');
		};
		^quarkPath;
	}

	*checkDictionary {
		if(dictionary.isNil){
			dictionary = this.parse;
			if(dictionary.isNil,
				{dictionary = Dictionary.new},
				{dictionary = dictionary.withSymbolKeys}
			);
		};
	}

	*setAt { |newpath, key|
		this.checkDictionary;
		if((dictionary[key]==newpath).not, {
			dictionary.add(key->(newpath+/+""));
			this.write(dictionary);
		});
		^dictionary[key];
	}

	*at { |key|
		this.checkDictionary;
		^dictionary[key];
	}

	*removeAt { |key|
		this.checkDictionary; 
		dictionary.removeAt(key); 
		this.write(dictionary);
	}

	*keys { 
		this.checkDictionary; 
		^dictionary.keys;
	}
}
