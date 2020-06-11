Hybrid : Composite {
	classvar <dictionary, processor;
	var <server;

	*initClass { 
		super.initClass;
		dictionary = dictionary ?? {Dictionary.new}; 
		processor = processor ?? {SynthDefProcessor.new};
		ServerQuit.add({this.clearDictionary});	
	}

	*clearDictionary {
		dictionary.do({|subD| processor.remove(subD.asArray)});
		dictionary.clear;
	}

	initComposite {
		server = server ? Server.default;
		if(this.class.subDictionaryExists.not, {
			this.class.addSubDictionary;
		});
		this.makeSynthDefs;
	}

	*subDictionaryExists {|className|
		^dictionary[this.name].notNil;
	}

	*addSubDictionary {
		dictionary[this.name] = Dictionary.new;
	}

	makeSynthDefs {
		var toProcess = [];
		modules.do({|module|
			toProcess = toProcess.add(this.checkModule(module));
		});
		this.class.processSynthDefs(toProcess.flat);
	}

	checkModule { |object|
		var synthDefs = [];
		case
		{object.isCollection and: {object.isString.not}}{
			object.do({|item| ^this.checkModule(item)});
		}
		{object.isFunction}{^this.checkModule(object.value)}
		{object.isKindOf(SynthDef)}{
			object.name = this.formatName(object.name).asSymbol;
			if(this.checkDictionary(object), { 
				synthDefs = synthDefs.add(object);
			});
		};
		^synthDefs;
	}

	checkDictionary { |synthDef|
		var class = this.class;
		if(class.notInDictionary(synthDef), { 
			class.addToDictionary(synthDef);
			^true;
		}); 
		^false;
	}

	*notInDictionary { |synthDefName| 
		^dictionary[this.name][synthDefName].isNil;
	}

	*addToDictionary { |synthDef|
		dictionary[this.name].add(synthDef.name -> synthDef);
	}

	formatName { |string|
		^this.tag(this.class.name, this.tag(moduleSet, string));
	}

	tag {|tag, name|
		tag = tag.asString; name = name.asString;
		if(name.contains(tag).not, { 
			name = format("%_%", tag, name);
		});
		^name;
	}

	*processSynthDefs { |synthDef|
		processor.add(synthDef);
	}

	server_{|newServer|
		server = server ? Server.default;
	}

	*clearSynthDefs {
		var toRemove = dictionary.removeAt(this.name);
		toRemove !? {processor.remove(toRemove.asArray)};
	}

	loadModules { 
		super.loadModules; 
		this.initComposite;
	}

}
