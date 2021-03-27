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

CodexNodeGraph : CodexComposite {
	var <nodes, <cleanup_list, <server;

	*makeTemplates { | templater |
		templater.nodeGraph("graph");
	}

	initComposite {
		server = server ?? { Server.default };

		cleanup_list ?? {
			cleanup_list = List.new;
		} !? { this.free };

		CmdPeriod.doOnce({ this.free });

		nodes = CodexModules.new;

		modules.keysValuesDo({ | key, value |
			if(key!=\graph){
				case
				{ value.isFunction }
				{
					var node = NodeProxy.new(server).source_(value);
					nodes.add(key -> node);
					cleanup_list.add({ node.free });
				}
				{ value.isKindOf(NodeProxy) }
				{
					nodes.add(key -> value);
					cleanup_list.add({ value.free });
				};
			};
		});

		this.initGraph;
	}

	initGraph {
		modules.graph(server, nodes, cleanup_list);
	}

	free {
		cleanup_list.do(_.value);
		cleanup_list.clear;
	}

	storeNode { | key, node |
		var source;
		case
		{ node.isFunction }{
			source = node.def.sourceCode;
		}
		{ node.isKindOf(NodeProxy) }
		{
			source = node.asCode;
		};

		if(source.notNil and: { source.find("open Function").isNil })
		{
			var path = this.moduleFolder+/+key++".scd";
			var file = File.open(path, "w");
			file.write(source);
			file.close;
		}
		//else
		{
			"Could not store new module. Make sure all functions are closed".warn;
		};
		this.reloadScripts;
	}

	doesNotUnderstand { | selector ... args |
		if(know, {
			var node = nodes[selector];
			node !? {
				^node.functionPerformList(
					\value,
					nodes,
					args
				);
			};
			if(selector.isSetter, {
				^nodes[selector.asGetter].source = args[0];
			}, { ^this });
		});
		^this.superPerformList(\doesNotUnderstand, selector, args);
	}

	server_{ | newServer |
		var previous = server;
		protect({
			server = newServer;
			this.initComposite;
		}, { | err |
			err !? {
				server = previous;
				this.initComposite;
			};
		});
	}
}

CodexProxierOld : CodexComposite {
	var <proxySpace, <sections, <cleanup_list;

	*makeTemplates { | templater |
		this.sectionTemplate(templater);
		this.otherTemplates(templater);
	}

	*sectionTemplate { | templater |
		templater.proxierSection("section0");
	}

	*otherTemplates { | templater | }

	initComposite {
		proxySpace = ProxySpace.new(Server.default);
		sections = this.collectSections;
		cleanup_list !? { this.cleanup } ?? { cleanup_list = List.new };
		this.initProxier;
	}

	makeSection {
		this.class.sectionTemplate(
			CodexTemplater(this.moduleFolder);
		);
	}

	collectSections {
		^modules.keys.selectAs({ | key |
			modules[key].isFunction;
		}, Array).reverse;
	}

	initProxier { }

	server { ^proxySpace.server }

	server_{ | newServer |
		proxySpace.clear;
		this.proxySpace = ProxySpace(newServer);
	}

	proxySpace_{ | newProxySpace |
		if(newProxySpace.isKindOf(ProxySpace), {
			proxySpace = newProxySpace;
		});
	}

	clear {
		proxySpace.clear;
		this.cleanup;
	}

	cleanup {
		if(cleanup_list.isEmpty.not, {
			cleanup_list.do(_.value);
			cleanup_list.clear;
		});
	}

	moduleSet_{ | newSet, from |
		this.clear;
		super.moduleSet_(newSet, from);
	}

	engage { | ... sections |
		sections.do{ | item |
			var module = modules[item];
			if(module.notNil, {
				module.value(
					modules,
					proxySpace,
					cleanup_list
				);
			});
		};
	}
}

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
		var keys = modules.keys.asArray;
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
		modules.proxySpace.use(function);
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

CodexSonata : CodexProxierOld {
	var <sectionIndex = -1;
	var <task, <timeRemaining;
	var <onLoop, <onLoopEnd;
	var <>loopDelta = 0.1;

	onLoop_{ | function |
		if(function.isFunction, { onLoop = function });
	}

	onLoopEnd_{ | function |
		if(function.isFunction, { onLoopEnd = function });
	}

	next { this.sectionIndex = sectionIndex + 1 }

	previous { this.sectionIndex = sectionIndex - 1 }

	sectionIndex_{ | newIndex |
		this.stop;
		if(newIndex < sections.size && newIndex >= 0, {
			sectionIndex = newIndex;
			this.engageTask(
				modules[sections[sectionIndex]]
				.value(
					modules,
					proxySpace,
					cleanup_list
				);
			);
		}, {
			this.reset;
			sectionIndex = -1;
		});
	}

	engageTask { | duration |
		if(duration.isNumber, {
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
		}, {
			timeRemaining = nil;
			onLoop.value(timeRemaining);
		});
	}

	start {
		if(sectionIndex<0, { this.next });
	}

	stop {
		if(task.notNil, {
			if(task.isPlaying, {
				task.stop;
			});
			task = nil;
		});
	}

	pause {
		if(task.notNil and: { task.isPlaying }, {
			task.pause;
		});
	}

	resume {
		if(task.notNil and: { task.isPlaying.not }, {
			task.resume;
		});
	}

	isPlaying { ^task.isPlaying }

	reset {
		this.stop;
		sectionIndex = -1;
	}

	clear {
		this.reset;
		super.clear;
	}
}

CodexSonataViewer : CodexSonata {
	*otherTemplates { | templater |
		templater.panel("panel");
	}

	initProxier {
		modules.panel.connectTo(this)
		.alwaysOnTop_(true);
	}

	clear {
		super.clear;
		this.reset;
	}

	close {
		this.stop;
		if(modules.panel.window.isClosed.not, {
			modules.panel.window.close;
			this.clear;
		});
	}

	moduleSet_{ | newSet, from |
		this.close;
		super.moduleSet_(newSet, from);
	}
}

CodexMovement : CodexProxierOld {
	*symphony { ^nil }

	*classFolder {
		var subfolder = if(this.symphony.isNil, { "" }, {
			this.symphony.asString});
		^this.directory+/+subfolder
		+/+this.name.asString;
	}

	getSections {
		^Pseq(this.class.nSections.collect({ | i |
			modules[("section"++i).asSymbol];
		}));
	}

	initComposite { this.initProxier }

	initProxier { }

	getPattern {
		^Pbind(
			\dur, p {
				//the movement stream should be a pseq that returns functions
				//that take in the proxy space as an argument and returns
				//the duration of the subevent.
				this.getMovements.asStream.do { | function |
					function.value(modules, proxySpace).yield;
				};
				this.cleanup;
			}
		)
	}

	cleanup {
		//proxySpace.do(_.release);
		modules.cleanup.do(_.value).clear;
	}
}

CodexSymphony {
	var <movements, player, <pattern, <proxySpace;

	*new { |...movements |
		^super.new
		.movements_(movements.flat)
		.proxySpace_(ProxySpace(Server.default))
		.initSymphony
	}

	movements_{ | newMovements |
		movements = newMovements
		.select(_.isKindOf(SchmooperMovement))
		.as(List);
		pattern = this.makePattern(movements);
	}

	initSymphony {  }

	makePattern { | array(movements) |
		^Pseq(array.collect(_.getPattern), 1);
	}

	reset {
		movements.do(_.cleanup);
		proxySpace.clear;
		if(this.isPlaying)
		{
			this.stop;
		};
		pattern = this.makePattern(movements);
	}

	clear { proxySpace.clear }

	addMovement { | index(movements.size), newMovement |
		newMovement.proxySpace = proxySpace;
		movements.insert(index, newMovement);
		this.reset;
	}

	replaceMovement { | index, newMovement |
		this.addMovement(index, newMovement);
		this.removeMovement(index + 1);
		this.reset;
	}

	removeMovement { | index |
		movements.removeAt(index);
		this.reset;
	}

	proxySpace_{ | newProxySpace |
		if(newProxySpace.isKindOf(ProxySpace), {
			proxySpace = newProxySpace;
			movements.do({ | movement |
				movement.proxySpace = proxySpace;
			});
		});
	}

	server { ^proxySpace.server }

	server_{ | newServer |
		proxySpace !? { proxySpace.clear };
		this.proxySpace = ProxySpace(newServer);
	}

	isPlaying { ^player.isPlaying }

	play {
		if(this.isPlaying, { this.stop });
		player = pattern.play(proxySpace.clock ? TempoClock.default, ());
	}

	playMovements { | indicies(movements) |
		pattern = this.makePattern(
			indicies.collect({ | i | movements[i] })
		);
		this.play;
	}

	stop {
		if(this.isPlaying, { player.stop });
		this.reset;
	}
}

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

CodexEnvir : CodexComposite {
	var <envir, cleanup;

	initCodex { | from |
		cleanup ?? { cleanup = List.new };
		envir !? { this.cleanupEnvir }  ?? {
			envir = Environment.new
		};
		envir.use({
			this.loadModules(from).initComposite;
		});
	}

	cleanupEnvir {
		cleanup.do(_.value);
		envir.keys.asArray.do { | key |
			try { envir.removeAt(key).free };
		};
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
