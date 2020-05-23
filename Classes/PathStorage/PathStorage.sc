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

	*path_{|newpath, id|
		this.checkDictionary;
		if((dictionary[id]==newpath).not, {
			dictionary.add(id->(newpath+/+""));
			this.write(dictionary);
		});
		^dictionary[id];
	}

	*path {|id|
		this.checkDictionary;
		^dictionary[id];
	}

}