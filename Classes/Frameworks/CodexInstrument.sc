//This is still a bit experimental
CodexInstrument : CodexHybrid {
	var <input, <output;
	var inputArray, outputArray, desc;
	var <>group, <synth, views, <window;

	*makeTemplates { | templater |
		templater.codexInstrument_synthDef;
	}

	initHybrid {
		server.bind({
			desc = this.class.cache.at(moduleSet).synthDef.desc;
			this.output = 0;
			this.initInstrument;
		});
	}

	initInstrument { }

	getArguments { | specs | }

	makeSynth {
		synth = Synth(
			modules.synthDef.name,
			this.getArguments(modules.synthDef.specs)
			++inputArray++outputArray,
			group ?? { server.defaultGroup }
		).register;
		^synth;
	}

	freeSynth {
		if(synth.isPlaying)
		{
			synth.free;
		}
	}

	setIO { | ios, val, arr |
		if(ios.notEmpty, {
			var offset = 0;
			arr !? { arr.do(_.free) };
			arr = [];
			if(val.isKindOf(Bus).not, {
				arr = arr.add(ios.startingChannel.asSymbol);
				arr = arr.add(Bus.new(\audio, val, ios[0].numberOfChannels, server));
				offset = 1;
			});
			ios[offset..(ios.size - 1)].do { | io, index |
				arr = arr.add(io.startingChannel.asSymbol);
				arr = arr.add(Bus.audio(server, io.numberOfChannels));
			};
			^arr
		});
		^[];
	}

	setOutputs {
		outputArray = this.setIO(
			desc.outputs,
			output,
			outputArray ? []
		);
	}

	output_{ | newBus |
		output = newBus;
		this.setOutputs;
	}

	setInputs {
		inputArray = this.setIO(
			desc.inputs,
			input,
			inputArray ? []
		);
	}

	input_{ | newBus |
		input = newBus;
		this.setInputs;
	}

	window_{ | newWindow |
		if((window.isNil || try{ window.isClosed }) && newWindow.isKindOf(Window))
		{
			window = newWindow;
		}
	}

	close {
		if(window.notNil and: { window.isClosed.not })
		{
			window.close;
		}
	}

	moduleSet_{ | to, from |
		this.close;
		super.moduleSet_(to, from);
	}
}