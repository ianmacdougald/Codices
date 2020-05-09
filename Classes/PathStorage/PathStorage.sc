PathStorage {
	classvar storedFolder;
	classvar storedFileName;
	classvar storagePath;
	classvar dictionary;

	*path_{|newpath, id|
		this.prCheckDictionary;
		if((dictionary[id]==newpath).not, {
			dictionary.add(id->(newpath+/+""));
			this.prWriteToYAML(dictionary);
		});
		^dictionary[id];
	}

	*path {|id|
		this.prCheckDictionary;
		^dictionary[id];
	}

	*prCheckDictionary {
		if(dictionary.isNil){
			dictionary = this.prParseYAMLFile;
			if(dictionary.isNil,
				{dictionary = Dictionary.new},
				{dictionary = dictionary.withSymbolKeys}
			);
		};
	}

	*prParseYAMLFile {
		this.prSetStoragePath;
		^storagePath.parseYAMLFile;
	}

	*prSetStoragePath {
		storagePath = storagePath ?? {
			Main.packages.asDict.at('CodexIan')
			+/+ "StoredPaths.YAML"
		};
	}

	*prWriteToYAML { |dictionary|
		forkIfNeeded{
			var fd = File.open(storagePath, "w+");
			fd.write(dictionary.asYAMLString);
			fd.close;
		};
	}
}