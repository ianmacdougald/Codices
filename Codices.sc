CodexSectioner : Codex {
	var <order, <index = -1, <>wrap = false;
	var freeFunctions;

	initCodex {
		order = this.arrange;
		index = -1;
		freeFunctions = [ ];
	}

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
		if(wrap){
			index = index % order.size;
		}{
			index = index.clip(0.0, order.size - 1);
		};
		modules[order[index]].value;
	}

	previous {
		if(index > 0){
			index = index - 1;
			modules[order[index]].value;
		} { this.reset };
	}

	arrange {
		var keys = modules.keys.asArray.copy;
		^keys.sort({ | a, b | a.endNumber < b.endNumber });
	}

	reset {
		index = -1;
		// this.next;
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
		this.reset;
	}
}

CodexJIT : Codex {
	*preload { | modules |
		modules.put(\proxySpace, ProxySpace.new);
	}

	proxySpace { ^this.modules[\proxySpace] }

	clock_{ | newClock |
		this.proxySpace.clock = newClock;
		this.quant = newClock.beatsPerBar;
	}

	clock { ^this.proxySpace.clock }

	tempo_{ | newTempo |
		this.clock !? { this.clock.tempo = newTempo };
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

	quant { ^this.proxySpace.quant }

	push { this.proxySpace.push }

	pop { this.proxySpace.pop }

	clear { this.proxySpace.clear }

	fadeTime_{ | dt | this.proxySpace.fadeTime_(dt) }

	fadeTime { ^this.proxySpace.fadeTime }
}

CodexProxier : CodexJIT {
	var <order, <index = -1, <>wrap = false;

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

	initCodex {
		order = this.arrange;
		index = -1;
	}

	next {
		index = index + 1;
		if(wrap){
			index = index % order.size;
		}{
			index = index.clip(0.0, order.size - 1);
		};
		modules[order[index]].value;
	}

	previous {
		if(index > 0){
			index = index - 1;
			modules[order[index]].value;
		} { this.clear };
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