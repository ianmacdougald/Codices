SynthDefProcessor_Base {
	var <server;
	var <synthDescLib;
	var routine, <synthDefList;
	var onLoader, offLoader ;

	*new { |server|
		^super.new
		.server_(server)
		.prInitList
	}

	prInitList {
		synthDefList = synthDefList ? List.new;
	}

	run {
		this.stop;
		routine = this.prMakeRoutine;
	}

	stop {
		if(this.isRunning){
			routine.stop;
		};
	}

	load {|synthDef|
		this.prLoadSynthDefCollection(
			this.prTestCollection(synthDef)
		);
	}

	process {|synthDef|
		this.load(synthDef);
		this.run;
	}

	isRunning {
		^routine.isPlaying;
	}

	server_{|newServer|
		server = server ? Server.default;
	}

	prLoadSynthDefCollection { |collection|
		collection.do{
			|item| this.prAddSynthDef(item)
		};
	}

	prAddSynthDef {|synthDef|
		synthDefList.add(synthDef);
	}

	prTestCollection {
		|input|
		if(input.isCollection.not){
			input = [input];
		};
		^input;
	}

	prPop{
		^synthDefList.removeAt(0);
	}

	prPopAction {
		var def = this.prPop;
		this.action(def);
		^def;
	}

	prRecheckAction{|synthDef|
		this.subclassResponsibility(thisMethod);
	}

	action{ |synthDef|
		this.subclassResponsibility(thisMethod);
	}

	prMakeRoutine {
		^forkIfNeeded({
			var previousDef = this.prPopAction;
			while({synthDefList.isEmpty.not}, {
				if(this.prRecheckAction(previousDef)){
					previousDef = this.prPopAction;
				}{server.latency.wait};
			});
		});
	}

}

SynthDefAdder : SynthDefProcessor_Base {

	*new{ |server| ^super.new(server); }

	action { |synthDef| synthDef.add; }

	prRecheckAction { |def|
		var bool = def.isAdded;
		if(bool.not){
			this.action(def);
		};
		^bool;
	}

}

SynthDefRemover : SynthDefProcessor_Base {

	*new {|server| ^super.new(server); }

	action {
		|synthDef|
		SynthDef.removeAt(synthDef.name);
	}

	prRecheckAction { |def|
		var bool = def.isAdded;
		if(bool){
			this.action(def);
		};
		^bool.not;
	}
}

SynthDefProcessor {
	var adder, remover;
	var lastProcessed;

	*new{
		^super.new.prInit;
	}

	prInit {
		adder = SynthDefAdder.new;
		remover = SynthDefRemover.new;
	}

	add { |synthDefs|
		adder.process(synthDefs);
		lastProcessed = synthDefs;
	}

	remove { |synthDefs|
		remover.process(synthDefs);
	}

	removeLast {
		if(lastProcessed.isNil.not){
			this.remove(lastProcessed);
		};
	}

}

+ SynthDef {
	isAdded {
		^(SynthDescLib.global
			.synthDescs[name].isNil.not);
	}

}
