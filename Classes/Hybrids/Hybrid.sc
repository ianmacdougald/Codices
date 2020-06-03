Hybrid : Modular {
	classvar isInit = false, <dictionary, processor;
	var <server;

	*new {|moduleName, from|
		if(isInit.not, {this.initHybrid});
		^super.new(moduleName, from).initHybrid;
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
		dictionary[className].add(
			synthDef.name.asSymbol -> synthDef
		);
	}

	*formatName { |synthDef|
		^this.formatString(synthDef.name);
	}

	*formatString { |input|
		var name = this.name.asString;
		if(input.asString.contains(name).not, {
			^format("%_%", name, input.asString).asSymbol;
		});
		^input.asSymbol;
	}

	*processSynthDefs {
		processor.add(dictionary[this.name].asArray);
	}

	server_{|newServer|
		server = server ? Server.default;
	}

	reloadSynthDefs {
		this.class.clearSynthDefs;
		this.initHybrid;
	}

	*clearSynthDefs {
		var toRemove = dictionary.removeAt(this.name);
		toRemove !? {processor.remove(toRemove.asArray)};
	}

	moduleName_{|newModule, from|
		moduleName = newModule;
		this.initModular(from);
		this.reloadSynthDefs;
	}
}

