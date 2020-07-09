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

	recheckAction { | synthDef |
		var bool = this.bool(synthDef);
		if(bool){ this.action(synthDef) };
		^bool.not;
	}

	bool { | synthDef | this.subclassResponsibility(thisMethod) }

	action{ | synthDef | this.subclassResponsibility(thisMethod) }

	makeRoutine {
		^forkIfNeeded({
			var previousDef = this.popAction;
			while({synthDefList.isEmpty.not}, {
				//if(this.recheckAction(previousDef)){
					//previousDef = this.popAction;
				//}{server.latency.wait};
				previousDef = this.popAction;
			});
		});
	}

}

CodexAdder : CodexRoutinizer {

	action { | synthDef | synthDef.add;  }

	bool { | synthDef | ^synthDef.isAdded.not }

}

CodexSender : CodexAdder {
	action { | synthDef | synthDef.send(server) }
}

CodexRemover : CodexRoutinizer {

	action { |synthDef|
		synthDef !? {SynthDef.removeAt(synthDef.name)};
	}

	bool { | synthDef | ^synthDef.isAdded }
}

CodexProcessor {
	var <server, adder, remover, sender;

	*new{ | server(Server.default) |
		^super.newCopyArgs(server).init;
	}

	*initClass {
		Class.initClassTree(Server);
		Class.initClassTree(SynthDef);
	}

	init {
		adder = CodexAdder(server);
		remover = CodexRemover(server);
		sender = CodexSender(server);
	}

	add { | synthDefs | adder.process(synthDefs) }

	send { | synthDefs | sender.process(synthDefs) }

	remove { | synthDefs | remover.process(synthDefs) }

	server_{ | newServer |
		remover.server = adder.server = server = newServer;
	}

}

+ SynthDef {

	isAdded { ^(SynthDescLib.global.synthDescs[name].notNil) }

}
