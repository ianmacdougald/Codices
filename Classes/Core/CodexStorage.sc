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

	*setAt { | key, item |
		if((dictionary[key]==item).not, {
			dictionary.add(key->item.asString);
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

+ String {

	*useCodexStorage { | bool(true) |
		CodexStorage.setAt(\useWithStrings, bool);
	}

	doesNotUnderstand { | selector ... args |
		var bool = CodexStorage.at(\useWithStrings);
		if(bool.notNil and: { bool.interpret }){
			var path = CodexStorage.at(selector);
			path !? { ^(path+/+this) };
			if(selector.isSetter){
				selector = selector.asGetter;
				CodexStorage.setAt(selector, args[0]);
				^(args[0]+/+this);
			};
		};
		^this.superPerformList(\doesNotUnderstand, selector, args);
	}
}
