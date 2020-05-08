SynthDef_Mover {
	var <>server;
	var <synthDescLib;
	var routine, <synthDefList;

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

	prWarnNotSet {|method, input|
		^format("Warning: % not set. Must supply %. ",
			method, input
		);
	}

	prLoadSynthDefCollection { |collection|
		collection.do{
			|item| this.prLoadSynthDef(item)
		};
	}

	prLoadSynthDef {|synthDef|
		synthDefList.add(synthDef);
	}

	prTestCollection {
		|input|
		if(input.isCollection.not){
			input = [input];
		};
		^input;
	}

	prPopAction {
		^synthDefList.removeAt(0);
	}

	prRecheckAction{
		this.subclassResponsibility(thisMethod);
	}

	prMakeRoutine {
		^forkIfNeeded({
			var previousDef = this.prPopAction;
			previousDef.name.postln;
			while({synthDefList.isEmpty.not}, {
				previousDef.name.postln;
				if(this.prRecheckAction(previousDef)){
					previousDef = this.prPopAction;
				}{server.latency.wait};
			});
		});
	}

}

SynthDef_OnLoader : SynthDef_Mover {

	*new{|server|
		^super.new(server);
	}

	prPopAction {
		^super.prPopAction.add;
	}

	prRecheckAction { |def|
		var bool = def.isAdded;
		if(bool.not){
			def.add;
		};
		^bool;
	}

}

SynthDef_OffLoader : SynthDef_Mover {

	*new {|server|
		^super.new(server);
	}

	prRecheckAction{ |def|
		var bool = def.isAdded;
		if(bool){
			SynthDef.removeAt(def.name);
		};
		^bool.not;
	}

	prPopAction {
		var def = super.prPopAction;
		SynthDef.removeAt(def.name);
		^def;
	}

}

+ SynthDef {
	isAdded {
		^(SynthDescLib.global
			.synthDescs[name].isNil.not);
	}

}