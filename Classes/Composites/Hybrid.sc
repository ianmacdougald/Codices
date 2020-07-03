Hybrid : Composite {
	classvar synthDefs, processor;
	var <>server;

	*initClass {
		processor = SynthDefProcessor.new;
		synthDefs = Dictionary.new;
		StartUp.add ({
			ServerQuit.add({this.removeAll});
		});
		this.checkDefaults;
	}

	*clearHybrids {
		synthDefs.do({ | dict | processor.remove(dict.asArray)});
		synthDefs.clear;
	}

	*removeAll {
		processor.remove(synthDefs.removeAt(this.name).asArray);
	}

	*removeAt { | key |
		processor.remove(this.synthDefs.removeAt(key));
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

	*subDictionaryExists {
		^synthDefs[this.name].notNil;
	}

	*addSubDictionary {
		synthDefs[this.name] = Dictionary.new;
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
		^synthDefs[this.name][synthDefName].isNil;
	}

	*addToDictionary { |synthDef|
		synthDefs[this.name].add(synthDef.name -> synthDef);
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
		var toRemove = synthDefs.removeAt(this.name);
		toRemove !? {processor.remove(toRemove.asArray)};
	}

	*removeSynthDef { | key |
		processor.remove(this.synthDefs.removeAt(key));
	}

	*allSynthDefs { ^synthDefs; }

	*synthDefs { ^synthDefs[this.name]; }

}
