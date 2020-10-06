//This is still a bit experimental
CodexInstrument : CodexHybrid {
	var <input, <output;
	var <>group, <synth, views, <window;
	var <inputArray, <outputArray, desc;

	*makeTemplates { | templater |
		templater.codexInstrument_synthDef;
	}

	initHybrid {
		this.initInstrument;
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
			val !? {
				if(val.isCollection.not, { val = [val] });
				val.do{ | item |
					arr = arr.add(ios[0].startingChannel.asSymbol);
					if(item.isKindOf(Bus).not, {
						arr = arr.add(Bus.new(\audio, item, ios[0].numberOfChannels, server));
					}, { arr = arr.add(item) });
				};
				offset = val.size;
			};
			if(ios.size > offset, {
				ios[offset..(ios.size - 1)].do { | io, index |
					arr = arr.add(io.startingChannel.asSymbol);
					arr = arr.add(Bus.audio(server, io.numberOfChannels));
				};
			});
			^arr
		});
		^[];
	}

	setOutputs {
		this.checkDesc;
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
		this.checkDesc;
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

	checkDesc {
		desc ?? {
			desc = this.class.cache.at(moduleSet).synthDef.desc;
		}
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
