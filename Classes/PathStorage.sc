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
		if(dictionary[id].isNil){
			this.prSetDefaultPath;
		};
		^dictionary[id];
	}

	*defaultPath {
		this.subclassResponsibility(thisMethod);
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

	*prSetDefaultPath {
		this.path_(this.defaultPath);
	}

	*prParseYAMLFile {
		this.prSetStoragePath;
		^storagePath.parseYAMLFile;
	}

	*prSetStoragePath {
		storagePath = storagePath ?? {
			Main.packages.asDict.at('FileManagement')
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

	*prSetID {
		var id = this.asString;
		if(id.contains("Meta_")){
			id = id[("Meta_".size)..];
		};
		^id.asSymbol;
	}
}