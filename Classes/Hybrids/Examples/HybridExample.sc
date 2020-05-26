HybridExample : HybridAbstraction {
	var synth, <>server;
	var isFreed = false;

	*new {
		^super.new
		.server_(HybridExample.server);
	}

	play {
		this.makeSynth;
		if(isFreed, {
			HybridExample.prAddInstance(this);
		});
	}

	makeSynth { |args, target, addAction|
		this.freeSynth(1e-3);
		synth = Synth(
			HybridExample.formatSynthName(\sine),
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
