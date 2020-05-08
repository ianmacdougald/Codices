MakeACrummySequencer : SynthDef_Processor{
	classvar <classSymbol, <synthDefDictionary;
	classvar <instances;

	var window, buttonRows, pointerButtons;
	var routine, <>tempo = 210, realTempo, <clock;
	var isThereClock = false, isThereSequence = false;
	var <>synthDef = \pmosc, <synthDefDictionary;
	var calledFromClose = false;

	*new{|numberOfRows = 4, bpm = 210|
		var return;

		classSymbol = this.prFormatClassSymbol(this);
		instances = instances ? List.new;
		return = super.new(classSymbol)
		.setTempo(bpm).pr_MakeSequencer(numberOfRows);

		synthDefDictionary = super.synthDefDictionary[classSymbol];
		instances.add(return);

		return.prInit;
		^return;
	}

	prInit{
		synthDefDictionary = this.class.synthDefDictionary;
	}

	setUpClock{
		clock = TempoClock(tempo/60);
		isThereClock = true;
	}

	setTempo{|bpm = 60|
		tempo = bpm;
		realTempo = tempo/60;
		if(isThereClock){
			clock.tempo = realTempo;
		}{
			this.setUpClock;
			this.setTempo(bpm);
		}
	}

	close{
		window.close;
	}

	*registerSynthDef{
		|synthdef, addIt = false|
		super.registerSynthDef(synthdef, addIt, classSymbol);
	}

	registerSynthDef{|synthdef, addIt=false|
		this.class.registerSynthDef(synthdef, addIt);
	}

	*formatSynthName{|name|
		var return;
		return = super.formatSynthName(name, classSymbol);
		^return;
	}

	formatSynthName{|name|
		var return = this.class.formatSynthName(name);
		^return;
	}

	free{
		if(window.isClosed==false){
			window.close;
		}/*ELSE*/{
			this.prCloseAction;
		};
		super.free;
		instances.remove(this);
		if(instances.isEmpty){
			var class = this.class;
			class.
			removeSynthDefs(class.classSymbol);
		}
	}

	*freeAll{
		instances.copy.do{|item|
			item.free;
		};
	}

	*defineSynthDefs{|synthDictionary|
		var synthdef;

		synthdef = SynthDef.new(\sine, {
			var env = EnvGen.ar(Env.perc(\atk.kr(0), \release.kr(1)), doneAction: 2);
			var sig = SinOsc.ar(\freq.kr(400));
			var out = sig * env * \amp.kr(0.5);
			Out.ar(\outchan.kr(0), Pan2.ar(out, \pan.kr(0)));
		});

		this.registerSynthDef(synthdef);

		synthdef = SynthDef.new(\pmosc, {
			var atk = \atk.kr(0), release = \release.kr(1);
			var env = EnvGen.ar(Env.perc(atk, release), doneAction: 2);
			var carFreqMod = EnvGen.ar(
				Env.perc(atk, release * \carModReleaseTime.kr(1.0)), doneAction: 0);
			var freq = \freq.kr(400);
			var sig = PMOsc.ar(freq * carFreqMod.range(1, \carModRatio.kr(3)),
				freq * \modFreqRatio.kr(0.5), \pmindex.kr(3));
			var out = sig * env * \amp.kr(0.5);
			Out.ar(\outchan.kr(0), Pan2.ar(out, \pan.kr(0)));
		}).add;

		this.registerSynthDef(synthdef);

		synthdef = SynthDef.new(\hh, {
			var freq = \freq.kr(400);
			var env = EnvGen.ar(Env.perc(\atk.kr(0), \release.kr(1)), doneAction: 2);
			var sig = HPF.ar(WhiteNoise.ar(), \hpfcutoff.kr(3000));
			var out = sig * env * \amp.kr(0.5);
			Out.ar(\outchan.kr(0), Pan2.ar(out, \pan.kr(0)));
		});

		this.registerSynthDef(synthdef);

		synthdef = SynthDef.new(\snare, {
			var freq = \freq.kr(400);
			var env = EnvGen.ar(Env.perc(\atk.kr(0), \release.kr(1)), doneAction: 2);
			var sig = LFNoise1.ar(3000);
			var out = sig * env * \amp.kr(0.5);
			Out.ar(\outchan.kr(0), Pan2.ar(out, \pan.kr(0)));
		});

		this.registerSynthDef(synthdef);

		synthdef = SynthDef.new(\varsaw, {
			var freq = \freq.kr(400);
			var atk = \atk.kr(0), release = \release.kr(1);
			var env = EnvGen.ar(Env.perc(atk, release), doneAction: Done.freeSelf);
			var filterEnv = EnvGen.ar(Env.perc(atk+\ffAtkOffset.kr(0),
				release * \ffReleaseRatio.kr(1)));
			var sig = VarSaw.ar(freq, \phase.kr(0.25),
				SinOsc.kr(\widthRate.kr(7) + \widthOffset.kr(#[0.0, 0.01]))
				.range(\widthLo.kr(0.25), \widthHi.kr(0.75))
			);
			var filteredSig = BMoog.ar(sig.sum,
				freq * filterEnv.range(\ffRatioLo.kr(1.25), \ffRatioHi.kr(4)), \q.kr(0.5));
			var out = filteredSig * env * \amp.kr(0.5);
			Out.ar(\outchan.kr(0), Pan2.ar(out, \pan.kr(0)));
		});

		this.registerSynthDef(synthdef);
	}

	*prSetClassSymbol{
		classSymbol = this.prFormatClassSymbol(this);
		// super.prSetClassSymbol(classSymbol);
	}

	prCloseAction{
		routine.stop;
		clock.stop;
	}

	pr_MakeSequencer{ |size|
		var makeKick, makeHH, makeSine, makeSnare, makeVarsaw, closeWindow;

		if(isThereSequence == false){

			if(isThereClock == false){
				this.setUpClock();
			};

			this.setTempo(tempo);

			window = Window("Drum Sequencer", Rect(100, 800, 100, 100))
			.front.alwaysOnTop_(true);

			buttonRows = Array.fill(5, {
				Array.fill(size, {
					Button(window)
					.states_([
						["", Color.black],
						["", Color.black, Color.yellow]
					])
				})
			});

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

			makeKick = { |amp = 0.5|
				Synth('MakeACrummySequencer_pmosc', [
					\freq, 42.5,
					\carModRatio, 3,
					\carModReleaseTime,0.03,
					\release, 0.75,
					\pmindex, 1,
					\amp, amp
				])
			};

			makeHH = {|amp = 0.35|
				Synth('MakeACrummySequencer_hh', [
					\release, 0.05,
					\pan, 1.0.bilinrand,
					\amp, amp
				])
			};

			makeSnare = {|amp = 0.35|
				Synth('MakeACrummySequencer_snare', [
					\release, 0.105,
					\pan, 0.5.bilinrand,
					\amp, amp
				])
			};

			makeSine = {|amp = 0.5, freq = 400|
				Synth('MakeACrummySequencer_varsaw', [
					\release, 0.75,
					\filterReleaseTime, 0.5,
					\atk, 0.1,
					\amp, amp,
					\freq, freq
				])
			};

			makeVarsaw = {|amp = 0.125, freq = 400|
				Synth('MakeACrummySequencer_varsaw', [
					\widthRate, 3,
					\filterReleaseTime, 0.5,
					\freq, freq,
					\amp, amp,
					\atk, 0.1
				])
			};

			routine = Routine({
				var counter = 0;
				var sineFreqs = Place([0, 2, 4, [7, 6, 6, 5, 5, 6, 6, 7]], inf).asStream;
				var varSawFreqs = Place([7, 4, 5, [10, 3, 3, 2, 1, -1, 3, 4]], inf).asStream;
				loop{
					var freqForVarSaw = Scale.major.degreeToFreq(varSawFreqs.next, 48.midicps, 1);
					var freqForSine = Scale.major.degreeToFreq(sineFreqs.next, 60.midicps, 1);
					{
						pointerButtons.do{|array|
							array.do{|item, index|
								if(index==counter){
									item.valueAction_(1);
								}{
									item.valueAction_(0);
								}
							}
						};
						buttonRows.do{|column, index|
							if(column[counter].value == 1){
								case
								{index==0}{makeKick.value(0.25)}
								{index==1}{makeHH.value(0.125)}
								{index==2}{makeSnare.value(0.25)}
								{index==3}{makeSine.(0.01, freqForSine)}
								{index==4}{makeVarsaw.(0.01, freqForVarSaw)}
							};
						}
					}.defer;

					counter = counter + 1 % buttonRows[0].size;

					(clock.beatsPerBar * (1/8)).wait;
				}

			}).play(clock);

			window.onClose = {

				this.free;
			};

			window.layout = GridLayout.rows(
				pointerButtons[0],
				buttonRows[0],
				buttonRows[1],
				buttonRows[2],
				buttonRows[3],
				buttonRows[4],
				pointerButtons[1]
			);

			CmdPeriod.doOnce({this.close});

			isThereSequence = true;
		}

	}
}



