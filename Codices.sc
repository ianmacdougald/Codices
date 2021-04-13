CodexScripter : Codex {
	initCodex {
		modules.tagSynthDefs(this.class.name
			++"_"++this.moduleSet++"_");

		this.initScripter;
	}

	initScripter { }
}

CodexSections : CodexScripter {
	var <order, <index = -1, <>wrap = false;
	var freeFunctions;

	initScripter {
		order = this.arrange;
		index = -1;
		freeFunctions = [ ];
	}

	initSections { }

	*makeTemplates { | templater |
		templater.blank("section0");
	}

	addSection {
		this.class.makeTemplates(CodexTemplater(this.moduleFolder));
		this.modules.compileFolder(this.moduleFolder);
		order = this.arrange;
	}

	toSection { | newIndex(0) |
		index = newIndex.clip(0, order.size - 1) - 1;
		this.next;
	}

	next {
		index = index + 1;
		if (wrap) {
			index = index % order.size;
		} /* else */ {
			index = index.clip(0.0, order.size - 1);
		};
		modules[order[index]].value;
	}

	previous {
		if (index > 0) {
			index = index - 1;
			modules[order[index]].value;
		} /* else */ { this.reset };
	}

	arrange {
		var keys = modules.keys.asArray.copy;
		^keys.sort({ | a, b | a.endNumber < b.endNumber });
	}

	onFree { | function |
		freeFunctions = freeFunctions.add(function);
	}

	free { | ... args |
		freeFunctions.do { | function |
			function.value(*args);
		};
		freeFunctions = [];
		this.reloadModules;
	}

	reset {
		index = -1;
		this.free;
		// this.next;
	}

	moduleSet_{ | newSet, from |
		this.reset;
		super.moduleSet_(newSet, from);
	}

}

CodexProxier : CodexScripter {
	var changes;

	*makeTemplates { | templater |
		templater.function("setup");
	}

	initScripter {
		modules.setup;
		this.initProxier;
	}

	initProxier { }

	*preload { | modules |
		modules.put(\proxySpace, ProxySpace.new);
	}

	proxySpace { ^this.modules[\proxySpace] }

	clear { this.proxySpace.clear }

	moduleSet_{ | newSet, from |
		this.clear;
		super.moduleSet_(newSet, from);
	}

	fadeTime_{ | dt | this.proxySpace.fadeTime_(dt) }

	fadeTime { ^this.proxySpace.fadeTime }

	reloadModules {
		this.clear;
		this.pop;
		super.reloadModules;
	}

	clock_{ | newClock |
		if (this.proxySpace == currentEnvironment) {
			newClock.permanent = true;
		};

		case
		{ newClock.isKindOf(Clock) } {
			this.proxySpace.clock = newClock;
			this.quant = newClock.beatsPerBar;
		}
		{ newClock.isKindOf(Number) } {
			("Warning: Are you trying to set the tempo? "
				++"Try using %.tempo_ instead").format(this.class.name).postln;
		};
	}

	push {
		if (this.clock.notNil) {
			this.clock.permanent = true;
		};
		this.proxySpace.push;
	}

	pop {
		if (this.clock.notNil) {
			this.clock.permanent = false;
		};
		this.proxySpace.pop;
	}

	clock { ^this.proxySpace.clock }

	tempo_{ | newTempo |
		this.clock !? { this.clock.tempo = newTempo };
		try { this.proxySpace.use { changes.value } };
	}

	tempo {
		if (this.clock.notNil) {
			^this.clock.tempo;
		} /* else */ {
			"Failed to get tempo. No clock found.".warn;
			^nil;
		};
	}

	quant_{ | newQuant |
		this.proxySpace.quant = newQuant;
	}

	quant { ^(this.proxySpace.quant ? 1) }

	onTempoChange { | action |
		changes = changes ? FunctionList.new;
		changes.addFunc(action);
	}

	clearChanges { changes = nil }
}

CodexProxySections : CodexProxier {
	var <order, <index = -1, <>wrap = false;

	*makeTemplates { | templater |
		templater.blank("section0");
	}

	initScripter {
		order = this.arrange;
		index = -1;
	}

	addSection {
		this.class.makeTemplates(CodexTemplater(this.moduleFolder));
		this.modules.compileFolder(this.moduleFolder);
		order = this.arrange;
	}

	toSection { | newIndex(0) |
		index = newIndex.clip(0, order.size - 1) - 1;
		this.next;
	}

	next {
		index = index + 1;
		if (wrap) {
			index = index % order.size;
		} /* else */ {
			index = index.clip(0.0, order.size - 1);
		};
		this.value;
	}

	previous {
		if (index > 0) {
			index = index - 1;
			this.value;
		} /* else */ {
			this.clear;
		};
	}

	value {
		this.proxySpace.use({
			modules[order[index]].value;
		});
	}

	arrange {
		var keys = modules.keys.asArray.copy;
		keys.remove(\proxySpace);
		^keys.sort({ | a, b | a.endNumber < b.endNumber });
	}

	clear {
		super.clear;
		index = -1;
	}

	reloadScripts {
		this.clear;
		super.reloadScripts;
	}
}

CodexSingelton : Codex {
	classvar <>object;

	*new { | moduleSet, from |
		super.new(moduleSet, from);
		this.initSingelton;
	}

	initCodex { this.class.object = this }

	*initSingelton {}

	*moduleFolder { ^this.object.moduleFolder }

	*reloadScripts {
		this.object.reloadScripts;
		this.initSingelton;
	}

	*reloadModules {
		this.object.reloadModules;
		this.initSingelton;
	}

	*modules { ^(this.object !? { this.object.modules } ? nil) }

	*moduleSet_{ | newSet, from |
		this.object ?? { this.new(newSet, from) } !? {
			this.object.moduleSet_(newSet, from);
			this.initSingelton;
		};
	}

	*moduleSet { ^this.object.moduleSet }

	*open { | ... keys |
		this.object.open(*keys);
	}

	*openSCQt { | ... keys |
		this.object.openSCQt(*keys);
	}

	*openSCVim { | shell("sh"), neovim(false), vertically(false) ... keys |
		this.object.openSCVim(shell, neovim, vertically, *keys);
	}

	*openModules {
		this.object.open(keys: object.modules.keys.asArray.sort);
	}

	*doesNotUnderstand { | selector ... args |
		^try { object.perform(selector, *args) }{
			this.superPerformList(\doesNotUnderstand, selector, *args);
		};
	}
}

+ Codex {
	push { modules.push }

	pop { modules.pop }
}

//CodexJITDef : CodexSingelton { }
