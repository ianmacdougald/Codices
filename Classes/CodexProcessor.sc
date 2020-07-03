CodexRoutinizer {
	var <>server, routine, <synthDefList;

	*new { |server(Server.default)|
		^super.newCopyArgs(server).init;
	}

	init {
		synthDefList = List.new;
		ServerBoot.add({ routine = this.makeRoutine; });
	}

	run {
		this.stop;
		routine = this.makeRoutine;
	}

	stop {
		if(this.isRunning){
			routine.stop;
		};
	}

	load { |synthDef|
		this.loadSynthDefCollection(
			this.testCollection(synthDef)
		);
	}

	process { |synthDef|
		this.load(synthDef);
		this.run;
	}

	isRunning {
		^routine.isPlaying;
	}

	loadSynthDefCollection { |collection|
		collection.do{
			|item| this.addSynthDef(item)
		};
	}

	addSynthDef { |synthDef|
		if(synthDef.isKindOf(SynthDef), {
			synthDefList.add(synthDef);
		});
	}

	testCollection { |input|
		if(input.isCollection.not){
			input = [input];
		};
		^input;
	}

	pop { ^try({synthDefList.removeAt(0)}, {^nil}); }

	popAction {
		var def = this.pop;
		def !? {this.action(def)};
		^def;
	}

	recheckAction{|synthDef|
		this.subclassResponsibility(thisMethod);
	}

	action{ |synthDef|
		this.subclassResponsibility(thisMethod);
	}

	makeRoutine {
		^forkIfNeeded({
			var previousDef = this.popAction;
			while({synthDefList.isEmpty.not}, {
				if(this.recheckAction(previousDef)){
					previousDef = this.popAction;
				}{server.latency.wait};
			});
		});
	}

}

CodexAdder : CodexRoutinizer {

	*new{ |server(Server.default)| ^super.new(server); }

	action { |synthDef| synthDef.add; }

	recheckAction { |def|
		var bool = def.isAdded.not;
		if(bool){
			this.action(def);
		};
		^bool.not;
	}

}

CodexRemover : CodexRoutinizer {

	*new { | server(Server.default) | ^super.new(server); }

	action { |synthDef|
		synthDef !? {SynthDef.removeAt(synthDef.name)};
	}

	recheckAction { |def|
		var bool = def.isAdded;
		if(bool){
			this.action(def);
		};
		^bool.not;
	}
}

CodexProcessor {
	var <server, adder, remover;

	*new{ | server(Server.default) |
		^super.newCopyArgs(server).init;
	}

	*initClass {
		Class.initClassTree(Server);
		Class.initClassTree(SynthDef);
	}

	init {
		adder = CodexAdder.new(server);
		remover = CodexRemover.new(server);
	}

	add { |synthDefs|
		adder.process(synthDefs);
	}

	remove { |synthDefs|
		remover.process(synthDefs);
	}

	server_{|newServer|
		remover.server = adder.server = server = newServer;
	}

}

+ SynthDef {

	isAdded {
		^(SynthDescLib.global.synthDescs[name].notNil);
	}

}
