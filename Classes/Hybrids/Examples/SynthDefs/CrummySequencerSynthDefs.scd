[
	SynthDef.new(\sine, {
		var env = EnvGen.ar(Env.perc(\atk.kr(0), \release.kr(1)), doneAction: 2);
		var sig = SinOsc.ar(\freq.kr(400));
		var out = sig * env * \amp.kr(0.5);
		Out.ar(\outchan.kr(0), Pan2.ar(out, \pan.kr(0)));
	}),

	SynthDef.new(\pmosc, {
		var atk = \atk.kr(0), release = \release.kr(1);
		var env = EnvGen.ar(Env.perc(atk, release), doneAction: 2);
		var carFreqMod = EnvGen.ar(
			Env.perc(atk, release * \carModReleaseTime.kr(1.0)), doneAction: 0);
		var freq = \freq.kr(400);
		var sig = PMOsc.ar(freq * carFreqMod.range(1, \carModRatio.kr(3)),
			freq * \modFreqRatio.kr(0.5), \pmindex.kr(3));
		var out = sig * env * \amp.kr(0.5);
		Out.ar(\outchan.kr(0), Pan2.ar(out, \pan.kr(0)));
	}),

	SynthDef.new(\hh, {
		var freq = \freq.kr(400);
		var env = EnvGen.ar(Env.perc(\atk.kr(0), \release.kr(1)), doneAction: 2);
		var sig = HPF.ar(WhiteNoise.ar(), \hpfcutoff.kr(3000));
		var out = sig * env * \amp.kr(0.5);
		Out.ar(\outchan.kr(0), Pan2.ar(out, \pan.kr(0)));
	}),

	SynthDef.new(\snare, {
		var freq = \freq.kr(400);
		var env = EnvGen.ar(Env.perc(\atk.kr(0), \release.kr(1)), doneAction: 2);
		var sig = LFNoise1.ar(3000);
		var out = sig * env * \amp.kr(0.5);
		Out.ar(\outchan.kr(0), Pan2.ar(out, \pan.kr(0)));
	}),

	SynthDef.new(\varsaw, {
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
	})
];