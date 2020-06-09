HybridExample : Hybrid {
	var synth;

	makeTemplates { 
		templater.synthDef; 
	}

	play { |args([\freq, 400]), target(server), addAction(\addToHead)|
		var class = this.class;
		this.free(1e-3);
		synth = Synth(
			class.dictionary[class.name].keys.asArray[0],
			args, 
			target, 
			addAction
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

	free {
		if(this.isPlaying, {synth.free});
	}
}
