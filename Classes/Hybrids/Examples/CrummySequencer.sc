CrummySequencer : HybridAbstraction {
	var window, routine, <tempo, <clock;
	var <>synthDef = \pmosc, synthEvent;
	var <buttons, pointerButtons;
	var counter = -1;

	*new{|rows = 4, tempo = 1|
		^super.new
		.loadSynthEvent
		.tempo_(tempo)
		.prMakeSequencer(rows);
	}

	*defaultModulePath {
		var path = PathName(this.filenameSymbol.asString).pathOnly;
		^(path +/+ "Modules/CrummySequencer");
	}

	setClock {
		clock = clock ? TempoClock(tempo ? 1.0);
	}

	tempo_{|newTempo|
		this.setClock;
		clock.tempo = newTempo;
	}

	closeAction {
		clock.stop;
		clock = nil;
		routine.stop;
		this.free;
	}

	close{
		window.close;
	}

	free{
		if(window.isClosed.not, {
			window.close;
		});
		super.free;
	}

	prCloseAction{
		clock.stop;
		routine.stop;
	}

	reloadModules {
		this.reloadSynthDefs;
		this.loadSynthEvent;
	}

	loadSynthEvent {
		synthEvent = CrummySequencer.loadModules.synthEvent;
		counter = -1;
	}

	prMakeSingleButton {
		^Button(window)
		.states_([
			["", Color.black],
			["", Color.black, Color.yellow]
		]);
	}

	prMakeButtons {|size|
		if(buttons.isNil){
			buttons = Array.fill(synthEvent.size, {
				Array.fill(size, {
					this.prMakeSingleButton;
				});
			});
		};
	}

	prMakePointerButtons{|size|
		pointerButtons = Array.fill(2, {
			Array.fill(size, {
				Button(window)
				.states_([
					["", Color.black, Color.black],
					["", Color.black, Color.red]
				])
				.acceptsMouse = false;
			});
		});
	}

	prMakeSequencer { |size|
		window = Window("Crummy Sequencer", Rect(100, 800, 100, 100))
		.front.alwaysOnTop_(true);

		this.prMakeButtons(size);
		this.prMakePointerButtons(size);

		routine = Routine({
			loop{
				defer{
					pointerButtons.do{|array|
						array.do{|item, index|
							if(index==counter){
								item.valueAction_(1);
							}{
								item.valueAction_(0);
							}
						}
					};
					server.bind({
						buttons.do{|column, index|
							if(column[counter].value == 1){
								case
								{index==0}{synthEvent.kick}
								{index==1}{synthEvent.hh}
								{index==2}{synthEvent.snare}
								{index==3}{synthEvent.counter}
								{index==4}{synthEvent.melody}
							};
						};
					});
				};
				counter = counter + 1 % buttons[0].size;
				(clock.beatsPerBar * (1/8)).wait;
			}
		}).play(clock);
		window.onClose = {
			this.closeAction;
		};
		window.layout = GridLayout.rows(
			pointerButtons[0],
			buttons[0],
			buttons[1],
			buttons[2],
			buttons[3],
			buttons[4],
			pointerButtons[1]
		);
		CmdPeriod.doOnce({this.close});
	}

}