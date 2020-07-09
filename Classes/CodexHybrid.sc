CodexHybrid : CodexComposite {
	classvar processor, <symbolsCache;
	var <>server;

	*initClass { symbolsCache = CodexCache.new; }

	*notAt { | set |
		var return = super.notAt(set);
		if(return, { symbolsCache.add(this.name, set, set) });
		^return;
	}

	initComposite {
		var class = this.class, cache = class.symbolsCache;
		if(cache.removeModules(class.name, moduleSet).notNil, {
			processor = processor ?? { CodexProcessor(server) };
			this.processSynthDefs;
		});
		this.initHybrid;
	}

	processSynthDefs  { this.class.processOn(this.nameSynthDefs) }

	*processOn { | synthDefs | processor.send(synthDefs) }

	*processOff { | synthDefs | processor.remove(synthDefs) }

	removeSynthDefs { this.class.processOff(this.findSynthDefs) }

	initHybrid {}

	findSynthDefs {
		^modules.select({ | item | item.isKindOf(SynthDef) }).asArray;
	}

	nameSynthDefs {
		^this.findSynthDefs.collect({ | def |
			def.name = this.formatName(def.name.asString).asSymbol;
		});
	}

	formatName { | string |
		string = this.stripTag(string);
		^this.tag(this.class.name, this.tag(moduleSet, string));
	}

	stripTag { | string |
		var found = string.findAll($_);
		found !? { if(found.size>=2, { ^string[(found[1]+1)..] }) } ;
		^string;
	}

	tag { | tag, name |
		tag = tag.asString; name = name.asString;
		if(name.contains(tag).not, {
			name = format("%_%", tag, name);
		});
		^name;
	}

	synthDefs { ^this.findSynthDefs.collect(_.name).as(Set); }

	reloadScripts {
		this.removeSynthDefs;
		super.reloadScripts;
	}

}
