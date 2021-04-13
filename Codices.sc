//Makes a synth with modular arguments
CodexInstrument : Codex {
	var <synth;

	*makeTemplates { | templater |
		templater.synthDef;
	}

	initHybrid {
		var coll = modules.synthDef.allControlNames;
		var names = coll.collect(_.name);
		coll.do { | item, index |
			modules.add(names[index] -> item.defaultValue);
		};
		this.initInstrument;
	}

	initInstrument { }

	setFromSpec { | ... args |
		var specs = modules.synthDef.specs;
		var dict = args.asDict;
		dict.keys.copy.do { | key |
			var currentval = dict[key];
			specs[key] !? {
				dict[key] = currentval = specs[key].map(currentval.clip(0.0, 1.0));
				modules[key] = dict[key];
			} ?? { dict.removeAt(key) };
		};
		if(synth.isPlaying){
			synth.set(*dict.asPairs);
		};
	}

	set { | ... args |
		var dict = args.asDict;
		dict.keys.copy.do { | key |
			var currentval = dict[key];
			modules[key] !? {
				modules[key] = currentval;
			} ?? { dict.removeAt(key) };
		};
		if(synth.isPlaying){
			synth.set(*dict.asPairs);
		};
	}

	makeSynth { | target, addAction(\addToHead) |
		var arguments = modules.copy;
		arguments.removeAt(\synthDef);
		arguments = arguments.asPairs;
		if(synth.isPlaying.not){
			synth = Synth(
				modules.synthDef.name,
				arguments.asPairs,
				target,
				addAction
			);
			synth.register;
		};
	}

	free {
		if(synth.isPlaying){
			synth.free;
		}
	}

	release { | time(1.0) |
		if(synth.isPlaying){
			synth.release(time);
		}
	}

	moduleSet_{ | newSet, from |
		this.free;
		super.moduleSet_(newSet, from);
	}

	specs { ^modules.synthDef.specs }

	printOn { | stream |
		if (stream.atLimit) { ^this };
		stream << this.class.name << "[ " << Char.nl ;
		this.printItemsOn(stream);
		stream << " ]" << Char.nl;
	}

	printItemsOn { | stream |
		var addComma = false;
		var synthArgs = modules.copy.asDict;
		synthArgs.removeAt(\synthDef);
		synthArgs = synthArgs.asPairs;
		forBy(0, synthArgs.size - 1, 2, { | i |
			if(stream.atLimit){ ^this };
			i = i + 1;
			stream.tab;
			stream << "\\";
			synthArgs[i - 1].printOn(stream);
			stream.comma.space;
			synthArgs[i].printOn(stream);
			stream << Char.comma << Char.nl;
		});
	}

	doesNotUnderstand { | selector ... args |
		if(know, {
			var module = modules[selector];
			module !? {
				^module.functionPerformList(
					\value,
					modules,
					args
				);
			};
			if(selector.isSetter, {
				^this.set(selector.asGetter, args[0]);
			});
		});
		^this.superPerformList(\doesNotUnderstand, selector, args);
	}
}

//Sequences whole script modules within ProxySpace
CodexProxier : Codex {
	var <order, <index = -1, <>wrap = false;

	*makeTemplates { | templater |
		templater.blank("section0");
	}

	*loadScripts { | set |
		this.cache.add(set -> CodexProxierModules(this.classFolder+/+set));
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
			index = index % order.size
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
		modules.proxySpace.clear;
		index = -1;
	}

	reloadScripts {
		this.clear;
		super.reloadScripts;
	}

	use { | function |
		^modules.proxySpace.use(function);
	}

	make { | function |
		modules.proxySpace.make(function);
	}

	push { modules.proxySpace.push }

	pop { modules.proxySpace.pop }

}

CodexProxierModules : CodexModules {
	*new { | folder |
		^super.new(folder).make({
			~proxySpace = ProxySpace.new(Server.default);
		});
	}

	*object { ^CodexProxierSection }

	initialize { | label | }

	printItemsOn { arg stream, itemsPerLine = 5;
		var itemsPerLinem1 = itemsPerLine - 1;
		var last = this.size - 1;
		this.associationsDo({ arg item, i;
			if(item.value.isKindOf(ProxySpace)){
				stream << "(" << item.key << " -> " << "ProxySpace ()" << ")"
			} { item.printOn(stream) };
			if (i < last, { stream.comma.space;
				if (i % itemsPerLine == itemsPerLinem1, { stream.nl.space.space });
			});
		});
	}
}

CodexProxierSection : CodexObject {
	value { | ... args |
		envir[\proxySpace].use({ function.value(*args) });
	}
}

//Adds a timing process to CodexProxier if module returns number
CodexSonata : CodexProxier {
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

//A gui class for working with Codices (need to rethink)
CodexPanel : Codex {
	var <codexObject, <window;
	var <>inputs = 2, <>outputs = 2;

	*contribute { | versions |
		var toQuark = Main.packages.asDict.at(\Codices)
		+/+"Contributions"+/+"CodexPanel";
		versions.add(\ianSonata -> (toQuark+/+"ianSonata"));
	}

	*makeTemplates { | templater |
		templater.panelFunction("function");
	}

	connectTo { | newObject |
		if(newObject.isKindOf(Codex), {
			codexObject = newObject;
			this.build;
		}, { Error("Can to an object of type Codex").throw });
	}

	alwaysOnTop { ^window.alwaysOnTop }

	alwaysOnTop_{ | bool(false) |
		window.alwaysOnTop = bool;
	}

	build {
		codexObject !? {
			window = modules.function(codexObject);
			window.front;
		};
	}

	close {
		window !? { window.close };
	}

	moduleSet_{ | newSet, from |
		var bool = this.alwaysOnTop;
		this.close;
		super.moduleSet_(newSet, from);
		this.build;
		this.alwaysOnTop = bool;
	}
}

CodexGuiKit : Codex {
	*makeTemplates { | templater |
		templater.knob;
		templater.labeledKnob;
		templater.staticText;
		templater.numberBox;
		templater.slider;
		templater.labeledSlider;
		templater.button;
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
