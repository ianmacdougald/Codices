CodexHybrid : CodexComposite {
	classvar todo, processor;
	var server;

	*initClass {
		Class.initClassTree(CodexProcessor);
		processor = CodexProcessor.new;
	}

	*addModules { | key |
		super.addModules(key);
		this.processSynthDefs(key);
	}

	*copyModules { | to, from |
		super.copyModules(to, from);
		this.processSynthDefs(to);
	}

	*processSynthDefs { | key  |
		processor.add(this.findSynthDefs(key));
	}

	*findSynthDefs { | key |
		^this.cache[key].select({ | module |
			if(module.isKindOf(SynthDef), {
				module.name = this.formatName(module.name, key);
				true;
			}, { false });
		});
	}

	*formatName { | symbol, key |
		var string = this.stripTag(symbol.asString);
		^this.tag(this.name, this.tag(key, string)).asSymbol;
	}

	*stripTag { | string |
		var found = string.findAll($_);
		found !? { if(found.size>=2, { ^string[(found.last+1)..] }) } ;
		^string;
	}

	*tag { | tag, name |
		tag = tag.asString; name = name.asString;
		if(name.contains(tag).not, {
			name = format("%_%", tag, name);
		});
		^name;
	}

	*removeSynthDefs { | key |
		processor.remove(this.findSynthDefs(key));
	}

	initComposite {
		server = Server.default;
		this.initHybrid;
	}

	server_{ | newServer |
		server = newServer;
		this.class.processSynthDefs(moduleSet);
	}

	server {
		^(server ?? { server = Server.default; server } !? { server });
	}

	initHybrid {}

	reloadScripts {
		this.removeSynthDefs;
		super.reloadScripts;
	}
}
