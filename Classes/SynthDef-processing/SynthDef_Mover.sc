SynthDef_Mover {
	var <server;
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

SynthDef_OnLoader : SynthDef_Mover {

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

/*
TO DO
SynthDef_OnLoaderSender : SynthDef_OnLoader {

	*new { |server| ^super.new(server); }

	action { |synthDef| synthDef.send(server); }

}*/

SynthDef_OffLoader : SynthDef_Mover {

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

+ SynthDef {
	isAdded {
		^(SynthDescLib.global
			.synthDescs[name].isNil.not);
	}

}