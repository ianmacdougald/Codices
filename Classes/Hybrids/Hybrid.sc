Hybrid : Modular {
	classvar isInit = false, <dictionary, processor;
	var <server;

	*new {|moduleName, from|
		if(isInit.not, {this.init});
		^super.new(moduleName, from).init;
	}

	*initHybrid {
		dictionary = Dictionary.new;
		processor = SynthDefProcessor.new;
		ServerQuit.add({
			this.freeDictionary;
		});
		isInit = true;
	}

	*freeDictionary { 
		dictionary.do({|subD|
			processor.remove(subD.asArray);
		});
	}

	initHybrid {
		server = server ? Server.default;
		if(this.class.subDictionaryExists.not, {
			this.class.addSubDictionary;
			this.makeSynthDefs;
		});
	}

	*subDictionaryExists {|className|
		^dictionary[this.name].notNil;
	}

	*addSubDictionary { 
		dictionary[this.name] = Dictionary.new;
	}

	makeSynthDefs {
		modules.do({|module|
			this.addToDictionary(module);
		}); 
		this.class.processSynthDefs;
	}

	addToDictionary { |object|
		case 
		{object.isCollection and: {object.isString.not}}{ 
			object.do({|item| this.addToDictionary(item)});
		}
		{object.isFunction}{this.addToDictionary(object.value)}
		{object.isKindOf(SynthDef)}{
			var class = this.class; 
			object.name = class.formatName(object); 
			class.addToDictionary(class.name, object);
		};
	}

	*addToDictionary {|className, synthDef|
		dictionary[className].add(synthDef.name.asSymbol.postln -> synthDef);
	}

	*formatName { |object| 
		var strid = this.name.asString;
		var defstring = object.name.asString;
		if(defstring.contains(strid).not, {
			^format("%_%", strid, defstring).asSymbol;
		});
		^object.name;
	}

	*processSynthDefs {
		processor.add(dictionary[this.name].asArray);
	}
	
	server_{|newServer|
		server = server ? Server.default;
	}

	reloadSynthDefs {
		this.class.clearSynthDefs;
		this.makeSynthDefs;
	}
	
	*clearSynthDefs { 
		var toRemove = dictionary.removeAt(this.name);
		toRemove !? {processor.remove(toRemove.asArray)};
	}
}
