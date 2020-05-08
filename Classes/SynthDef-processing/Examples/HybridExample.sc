HybridExample : HybridAbstraction {
	var synth, <>server;
	var isFreed = false;

	*new {
		^super.new
		.server_(HybAbstExample.server);
	}

	*defineSynthDefs {
		this.registerSynthDef(
			SynthDef(\sine, {
				var env = EnvGen.kr(Env.asr(\atk.kr(0), 1, \release.kr(1)),
					\gate.kr(1),
					doneAction: Done.freeSelf
				);
				var lag = \lag.kr(0.1);
				var sig = SinOsc.ar(\freq.kr(400, lag));
				var out = sig * env * \ampDB.kr(-12, lag * 2).dbamp;
				Out.ar(\out.kr(0), Pan2.ar(out, \pan.kr(0)))
			});
		)
	}

	play {
		this.makeSynth;
		if(isFreed, {
			HybAbstExample.prAddInstance(this);
		});
	}

	makeSynth { |args, target, addAction|
		this.freeSynth(1e-3);
		synth = Synth(
			HybAbstExample.formatSynthName(\sine),
			args ?? {[\freq, 400]},
			target ?? {server.defaultGroup},
			addAction ? \addToHead
		).register;
	}

	isPlaying {
		^synth.isPlaying;
	}

	set { |...args|
		if(this.isPlaying){
			server.sendMsg(15, synth.nodeID, *(args.asOSCArgArray));
			^this;
		};
	}

	freeSynth {|time(1)|
		this.set(\release, time, \gate, 0);
	}

	free {|time(1)|
		this.freeSynth(time);
		super.free;
		isFreed = true;
	}

}