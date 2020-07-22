CodexHybrid : CodexComposite {
	classvar todo, processor;
	var server;

	*initClass {
		Class.initClassTree(CodexProcessor);
		todo = CodexCache.new;
		processor = CodexProcessor.new;
	}

	*notAt { | set |
		var return = super.notAt(set);
		if(return, { todo.add(this.name, set, set) });
		^return;
	}

	initComposite {
		server = Server.default;
		if(todo.removeModules(this.name, moduleSet).notNil, {
			this.processSynthDefs;
		});
		this.initHybrid;
	}

	server_{ | newServer |
		server = newServer;
		this.processSynthDefs;
	}

	server {
		^(server ?? { server = Server.default; server } !? { server });
	}

	processSynthDefs { processor.add(this.nameSynthDefs) }

	removeSynthDefs { processor.remove(this.findSynthDefs) }

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
		^this.tag(this.name, this.tag(moduleSet, string));
	}

	stripTag { | string |
		var found = string.findAll($_);
		found !? { if(found.size>=2, { ^string[(found.last+1)..] }) } ;
		^string;
	}

	tag { | tag, name |
		tag = tag.asString; name = name.asString;
		if(name.contains(tag).not, {
			name = format("%_%", tag, name);
		});
		^name;
	}

	synthDefs { ^this.findSynthDefs.collect(_.name).as(Set) }

	reloadScripts {
		this.removeSynthDefs;
		super.reloadScripts;
	}
}
