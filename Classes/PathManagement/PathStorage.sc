PathStorage  {
	classvar quarkPath;
	classvar dictionary;


	*storagePath {
		^(this.pathToQuark +/+ format("%.YAML", this.name))
	}

	*parse { ^this.storagePath.parseYAMLFile; }

	*write { |item|
		var fd = File.open(this.storagePath, "w+");
		fd.write(item.asYAMLString);
		fd.close;
	}

	*initClass {
		Class.initClassTree(Main);
		Class.initClassTree(Quarks);
		Class.initClassTree(Dictionary);
		Class.initClassTree(Collection);
		quarkPath = Main.packages.asDict.at('CodexIan');
		this.checkDictionary;
	}

	*pathToQuark { ^quarkPath; }

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
		if((dictionary[key]==newpath).not, {
			dictionary.add(key->(newpath+/+""));
			this.write(dictionary);
		});
		^dictionary[key];
	}

	*at { |key| ^dictionary[key]; }

	*removeAt { |key|
		dictionary.removeAt(key);
		this.write(dictionary);
	}

	*keys { ^dictionary.keys; }
}
