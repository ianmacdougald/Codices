Hybrid : Composite {
	classvar dictionary, processor, qa;
	var <>server;

	*establish { 
		super.establish;
		dictionary ?? {dictionary = Dictionary.new}; 
		processor ?? {processor = SynthDefProcessor.new};
		qa ?? {ServerQuit.add({this.removeAll}); qa = 1};	
	}

	*clearHybrids { 
		dictionary.do({ | dict | processor.remove(dict.asArray)}); 
		dictionary.clear;
	}

	*removeAll { 
		processor.remove(dictionary.removeAt(this.name).asArray);
	}

	*remove { | key | 
		processor.remove(this.dictionary.removeAt(key));
	}

	initComposite {
		server = Server.default;
		if(this.class.subDictionaryExists.not, {
			this.class.addSubDictionary;
		});
		this.makeSynthDefs;
		this.initHybrid;
	}

	initHybrid {}

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

	*freeSynthDefs {
		var toRemove = dictionary.removeAt(this.name);
		toRemove !? {processor.remove(toRemove.asArray)};
	}

	*removeSynthDef { | key | 
		processor.remove(this.dictionary.removeAt(key));
	}

	*hybridDictionary { ^dictionary; }

	*dictionary { ^dictionary[this.name]; }

	loadModules { 
		super.loadModules; 
		this.initComposite;
	}

}
