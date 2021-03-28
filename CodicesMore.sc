//TippyTaps depends on this, but it needs updating
//Should be a simple interface for inspecting SynthDefs
//And making associated nodes
CodexInstrument : CodexHybrid {
	var <input, <output, desc;
	var <>group, <synth, <window;
	var inputArray, outputArray;
	// var inputArray, <outputArray, desc;

	*makeTemplates { | templater |
		templater.instrumentSynthDef;
	}

	initHybrid {
		this.initInstrument;
	}

	initInstrument { }

	getArguments { | specs | }

	makeSynth {
		synth = Synth(
			modules.synthDef.name,
			this.getArguments(modules.synthDef.specs)
			++[]++[\out, output],
			group ?? { server.defaultGroup }
		).register;
		^synth;
	}

	freeSynth {
		if(synth.isPlaying)
		{
			synth.free;
		}
	}

	/*setIO { | ios, val, arr |
	if(ios.notEmpty, {
	var offset = 0;
	// arr !? { arr.do(_.free) };
	arr = [];
	val !? {
	if(val.isCollection.not, { val = [val] });
	val.do{ | item |
	arr = arr.add(ios[0].startingChannel.asSymbol);
	if(item.isKindOf(Bus).not, {
	arr = arr.add(Bus.new(\audio, item, ios[0].numberOfChannels, server));
	}, { arr = arr.add(item) });
	};
	offset = val.size;
	};
	if(ios.size > offset, {
	ios[offset..(ios.size - 1)].do { | io, index |
	arr = arr.add(io.startingChannel.asSymbol);
	arr = arr.add(Bus.audio(server, io.numberOfChannels));
	};
	});
	^arr
	});
	^[];
	}*/

	setOutputs {
		this.checkDesc;
		outputArray = this.setIO(
			desc.outputs,
			output,
			outputArray ? []
		);
	}

	output_{ | newBus |
		output = newBus;
		this.setOutputs;
	}

	setInputs {
		this.checkDesc;
		inputArray = this.setIO(
			desc.inputs,
			input,
			inputArray ? []
		);
	}

	input_{ | newBus |
		input = newBus;
		this.setInputs;
	}

	checkDesc {
		desc ?? {
			desc = this.class.cache.at(moduleSet).synthDef.desc;
		}
	}

	window_{ | newWindow |
		if((window.isNil || try{ window.isClosed }) && newWindow.isKindOf(Window))
		{
			window = newWindow;
		}
	}

	close {
		if(window.notNil and: { window.isClosed.not })
		{
			window.close;
		}
	}

	moduleSet_{ | to, from |
		this.close;
		super.moduleSet_(to, from);
	}
}

//Sequences whole script modules within ProxySpace
CodexProxier : CodexComposite {
	var order, index, <>wrap = false;

	*makeTemplates { | templater |
		templater.blank("section0");
	}

	*addModules { | key |
		this.cache.add(key -> CodexProxierModules(this.asPath(key)));
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

	*otherTemplates { | templater | }

	initComposite {
		order = this.arrange;
		index = -1;
	}

	next {
		index = index + 1;
		if(wrap){ index = index % order.size };
		if(index < order.size){
			modules[order[index]].value;
		};
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

		^keys.collect(_.asString)
		.sort({ | a, b | a.endNumber < b.endNumber })
		.collect(_.asSymbol)
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
		var obj = super.new.know_(true).make({
			~proxySpace = ProxySpace.new(Server.default);
		});
		folder !? { obj.compileFolder(folder) };
		^obj;
	}

	addToEnvir { | key, func |
		this.add(key -> CodexProxierSection(key, func));
	}

	loadAll {
		this.shouldNotImplement(thisMethod);
	}

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

CodexProxierSection : CodexTmpModule {
	value { | ... args |
		envir[\proxySpace].use({ func.value(*args) });
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
CodexPanel : CodexComposite {
	var <codexObject, <window;
	var <>inputs = 2, <>outputs = 2;

	*contribute { | versions |
		var path = Main.packages.asDict.at(\CodicesMore)
		+/+"Contributions"+/+"CodexPanel";

		versions.add(
			[\ianSonata, path+/+"ianSonata"]
		);
	}

	*makeTemplates { | templater |
		templater.panelFunction("function");
	}

	connectTo { | newObject |
		if(newObject.isKindOf(CodexComposite), {
			codexObject = newObject;
			this.build;
		}, { Error("Can only connect to object of type CodexComposite").throw });
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

CodexGuiKit : CodexComposite {
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

CodexSingelton : CodexComposite {
	classvar <>object;
	*new { | moduleSet, from |
		object = super.new(moduleSet, from);
		if(this!=CodexSingelton){
			this.object = this.superclass.object;
		};
	}

	initComposite {
		this.class.initSingelton;
	}

	*initSingelton {}

	*moduleFolder { ^object.moduleFolder }

	*reloadScripts { object.reloadScripts }

	*reloadModules { object.reloadModules }

	*modules { ^object.modules }

	*moduleSet_{ | newSet, from |
		object.moduleSet_(newSet, from);
	}

	*moduleSet { ^object.moduleSet }

	*open { | ... keys |
		object.open(*keys);
	}

	*open_scqt { | ... keys |
		object.open_scqt(*keys);
	}

	*open_scvim { | shell("sh"), neovim(false), vertically(false) ... keys |
		object.open_scvim(shell, neovim, vertically, *keys);
	}

	*openModules {
		object.open(keys: object.modules.keys.asArray.sort);
	}

	*doesNotUnderstand { | selector ... args |
		^object.doesNotUnderstand(selector, *args)
	}
}
