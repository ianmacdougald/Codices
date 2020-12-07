CodexStorage  {
	classvar dictionary, <storagePath;

	*write { | item |
		var fd = File.open(storagePath, "w+");
		fd.write(item.asYAMLString);
		fd.close;
	}

	*initClass {
		Class.initClassTree(Main);
		Class.initClassTree(Quarks);
		Class.initClassTree(Dictionary);
		Class.initClassTree(Collection);
		storagePath = Main.packages.asDict.at('CodexIan')
		+/+format("%.yaml", this.name);
		this.checkDictionary;
	}

	*checkDictionary {
		if(dictionary.isNil){
			dictionary = storagePath.parseYAMLFile;
			if(dictionary.isNil,
				{ dictionary = Dictionary.new },
				{ dictionary = dictionary.withSymbolKeys }
			);
		};
	}

	*setAt { | newpath, key |
		if((dictionary[key]==newpath).not, {
			dictionary.add(key->(newpath+/+""));
			this.write(dictionary);
		});
		^dictionary[key];
	}

	*at { | key | ^dictionary[key] }

	*removeAt { | key |
		dictionary.removeAt(key);
		this.write(dictionary);
	}

	*keys { ^dictionary.keys }
}
