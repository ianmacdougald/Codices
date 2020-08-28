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

	synthDefKeys { 
		^modules.keys.asArray.select({ | item |
			modules.at(item).isKindOf(SynthDef)
		})
	}

	findSynthDefs {
		^modules.select({ | item | item.isKindOf(SynthDef) }).asArray;
	}

	nameSynthDefs {
		this.synthDefKeys.do{ | key | 
			var name = this.formatName(modules[key].name);
			this.class.cache[moduleSet][key].name = name;
			modules[key].name = name;
		}
	}

	formatName { | symbol |
		var string = symbol.asString;
		string = this.stripTag(string);
		^this.tag(this.name, this.tag(moduleSet, string)).asSymbol;
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
