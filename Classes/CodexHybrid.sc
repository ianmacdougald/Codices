CodexHybrid : CodexComposite {
	classvar synthDefs, processor;
	var <>server;

	*initClass {
		processor = CodexProcessor.new;
		synthDefs = CodexCache.new;
		StartUp.add ({
			ServerQuit.add({this.removeAll});
		});
	}

	*clearSynthDefs {
		var toRemove = List.new;
		this.synthDefs.do({ | dict | toRemove.add(dict.asArray); }).clear;
		processor.remove(toRemove.flat);
	}

	*clearAllSynthDefs {
		var toRemove = List.new;
		synthDefs.do({ | dict | toRemove.add(dict.asArray); }).clear;
		processor.remove(toRemove.flat);
	}

/*	*removeAt { | key |
		processor.remove(this.synthDefs.removeAt(key));
	}*/

	initComposite {
		var class = this.class;
		server = Server.default;
		if(class.allSynthDefs.notAt(class.name, moduleSet), {
			this.makeSynthDefs;
		});
		this.initHybrid;
	}

	initHybrid {}

	makeSynthDefs {
		var class = this.class, defs = class.allSynthDefs;
		var toProcess = this.getSynthDefs;
		class.processSynthDefs(toProcess);
		defs.addToDictionary(class.name, moduleSet, toProcess);
	}

	getSynthDefs {
		^modules.select({ | item |
			var return = false;
			if(item.isKindOf(SynthDef), {
				item.name = this.formatName(item.name).asSymbol;
				return = true;
			});
			return;
		}).asArray.flat;
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

	*processSynthDefs { | synthDef |
		processor.add(synthDef);
	}

	*removeSynthDefs { | key |
		processor.remove(synthDefs.removeModules(this.name, key));
	}

	*allSynthDefs { ^synthDefs; }

	*synthDefs { ^synthDefs[this.name]; }

	synthDefs { ^synthDefs[this.class.name][moduleSet]; }

	clearSynthDefs { this.class.removeSynthDefs(moduleSet); }

	reloadScripts {
		this.clearSynthDefs;
		super.reloadScripts;
	}

}
