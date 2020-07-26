CodexHybrid : CodexComposite {
	classvar todo, processor;
	var server;

	*initClass {
		Class.initClassTree(CodexProcessor);
		todo = LinkedList.new;
		processor = CodexProcessor.new;
	}

	*notAt { | set |
		var return = super.notAt(set);
		if(return, { todo.add(this.name) });
		^return;
	}

	initComposite {
		server = Server.default;
		if(this.shouldProcess, { this.processSynthDefs });
		this.initHybrid;
	}

	shouldProcess {
		var popped = todo.pop ?? { ^false };
		if(popped==this.class.name, { ^true }, {
			todo.addFirst(popped);
			^false
		});
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

	synthDefs { ^this.findSynthDefs }
	synthDefNames { ^this.findSynthDefs.collect(_.name) }

	reloadScripts {
		this.removeSynthDefs;
		super.reloadScripts;
	}
}
