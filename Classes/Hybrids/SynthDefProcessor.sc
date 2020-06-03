SynthDefProcessor_Base {
	var <>server, routine, <synthDefList; 

	*new { |server(Server.default)|
		^super.newCopyArgs(server).init;
	}

	init {
		synthDefList = List.new;
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

	pop {
		^try{synthDefList.removeAt(0)} 
	}

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

SynthDefAdder : SynthDefProcessor_Base {

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

SynthDefRemover : SynthDefProcessor_Base {

	*new {|server(Server.default)| ^super.new(server); }

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

SynthDefProcessor {
	var adder, remover;
	var lastProcessed;

	*new{
		^super.new.init;
	}

	init {
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
		^(SynthDescLib.global.synthDescs[name].notNil);
	}

}
