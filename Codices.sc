CodexSectioner : Codex {
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

	*sectionTemplate { | templater |
		templater.blank("section0");
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

CodexSonata : CodexSectioner {
	var <task, <timeRemaining;
	var <onLoop, <onLoopEnd;
	var <>loopDelta = 0.1;

	onLoop_{ | function |
		if(function.isFunction, { onLoop = function });
	}

	onLoopEnd_{ | function |
		if(function.isFunction, { onLoopEnd = function });
	}

	next {
		this.stop;
		index = index + 1;
		if(wrap){ index = index % order.size };
		if(index < order.size){
			this.makeTask(modules[order[index]].value);
		};
	}

	previous {
		this.stop;
		if(index > 0){
			index = index - 1;
			this.makeTask(modules[order[index]].value);
		} { this.clear };
	}

	stop {
		if(task.isPlaying){
			task.stop;
		};
	}

	makeTask { | duration |
		if(duration.isNumber){
			task = Task({
				timeRemaining = duration;
				while({ timeRemaining > 0 }, {
					onLoop.value(timeRemaining);
					timeRemaining = (timeRemaining - loopDelta)
					.clip(0, duration);
					loopDelta.wait;
				});
				timeRemaining = nil;
				onLoopEnd.value;
				fork { this.next };
			}).play;
		};
	}

	pause {
		if(task.isPlaying){ task.pause };
	}

	resume {
		if(task.notNil and: { task.isPlaying.not }, {
			task.resume;
		});
	}

	isPlaying { ^task.isPlaying }

	reset {
		this.stop;
		index = -1;
	}

	clear {
		this.reset;
		super.clear;
	}
}

CodexProxySpace : Codex {
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
			^this.clock.tempo
		} /* else */ {
			"Can't get tempo. No clock found".warn;
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

CodexLCE : CodexProxySpace { }

CodexProxier : CodexProxySpace {
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

	*sectionTemplate { | templater |
		templater.blank("section0");
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

	fadeTime { ^modules.proxySpace.fadeTime }
	fadeTime_{ | dt | modules.proxySpace.fadeTime_(dt) }

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

CodexProxySonata : CodexProxier {
	var <task, <timeRemaining;
	var <onLoop, <onLoopEnd;
	var <>loopDelta = 0.1;

	onLoop_{ | function |
		if(function.isFunction, { onLoop = function });
	}

	onLoopEnd_{ | function |
		if(function.isFunction, { onLoopEnd = function });
	}

	next {
		this.stop;
		index = index + 1;
		if(wrap){ index = index % order.size };
		if(index < order.size){
			this.makeTask(modules[order[index]].value);
		};
	}

	previous {
		this.stop;
		if(index > 0){
			index = index - 1;
			this.makeTask(modules[order[index]].value);
		} { this.clear };
	}

	stop {
		if(task.isPlaying){
			task.stop;
		};
	}

	makeTask { | duration |
		if(duration.isNumber){
			task = Task({
				timeRemaining = duration;
				while({ timeRemaining > 0 }, {
					onLoop.value(timeRemaining);
					timeRemaining = (timeRemaining - loopDelta)
					.clip(0, duration);
					loopDelta.wait;
				});
				timeRemaining = nil;
				onLoopEnd.value;
				fork { this.next };
			}).play;
		};
	}

	pause {
		if(task.isPlaying){ task.pause };
	}

	resume {
		if(task.notNil and: { task.isPlaying.not }, {
			task.resume;
		});
	}

	isPlaying { ^task.isPlaying }

	reset {
		this.stop;
		index = -1;
	}

	clear {
		this.reset;
		super.clear;
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

	*open_scqt { | ... keys |
		this.object.open_scqt(*keys);
	}

	*open_scvim { | shell("sh"), neovim(false), vertically(false) ... keys |
		this.object.open_scvim(shell, neovim, vertically, *keys);
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
