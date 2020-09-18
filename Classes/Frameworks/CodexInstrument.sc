CodexInstrument : CodexHybrid { 
	var <>group, <synth, <ios, views, <window;
	
	*makeTemplates { | templater |
		templater.codexInstrument_synthDef;
	}

	initHybrid { 
		Routine({ 
			server.sync; 
			this.fillIOs;
			this.initInstrument;
		}).play(AppClock);
	}

	initInstrument {  }

	getArguments { | specs | }

	makeSynth { 
		synth = Synth(
			modules.synthDef.name, 
			this.getArguments(modules.synthDef.specs)++ios.asPairs, 
			group ?? { server.defaultGroup }
		).register; 
		synth.onFree({synth = nil});
		^synth;
	}

	freeSynth { 
		if(synth.isPlaying)
		{
			synth.free; 
		}
	}

	fillIOs { 
		var desc = this.class.cache[moduleSet]
		.synthDef.desc;
		var count = 0; 
		var fillIO = { | coll | 
			if(coll.isEmpty.not, { 
				coll.do { | io |
					ios.add(
						io.startingChannel.asSymbol 
						-> count
					);	
					count = count + 1 
					+ (io.numberOfChannels - 1);
				};
			})
		};
		ios = ();
		fillIO.value(desc.outputs);
		fillIO.value(desc.inputs);
	}

	setBus { | key, value | 
		if(value.isNumber)
		{
			value = value.asInteger;
		}
		//else
		{ 
			if(value.isKindOf(Bus).not)
			{
				Error("Can only set bus with a bus or bus index").throw;
			}
		}; 
		ios[key] = value;
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
