var e = { |a(\atk.kr(0)), r(\release.kr(1)), t(\timescale.kr(1)), d(2)|
	SynthDef.wrap({
		EnvGen.ar(
			Env.perc(a, r),
			timeScale: t,
			doneAction: Done.freeSelf
		);
	});
};

var o = {
	|sig(SinOsc.ar(\freq.kr(400))), timescale(\timescale.kr(1))|
	SynthDef.wrap({
		Pan2.ar(sig * e.value(t: timescale) * \ampDB.kr(-12).dbamp, \pan.kr(0));
	});
};

[
	SynthDef.new(\sine, {
		OffsetOut.ar(\out.kr(0), o.value);
	}),

	SynthDef.new(\pmosc, {
		var freq = \freq.kr(400);
		var timescale = \timescale.kr(1);
		var carModEnv = EnvGen.ar(
			Env.perc(\carAtk.kr(0), \carRelease.kr(0.005)),
			timeScale: timescale,
			doneAction: Done.none
		).exprange(1, \carModRatio.kr(3));
		var sig = PMOsc.ar(freq * carModEnv, freq * \modFreqRatio.kr(0.5), \pmindex.kr(4));
		var sub = SinOsc.ar(freq * 0.5) * EnvGen.ar(
			Env.perc(\subAtk.kr(0.0), \subRelease.kr(0.1)),
			timeScale: timescale,
			doneAction: Done.none
		);
		sig = sig + sub * -6.dbamp;
		OffsetOut.ar(\out.kr(0), o.value(sig, timescale));
	}),

	SynthDef.new(\hh, {
		OffsetOut.ar(\out.kr(0), o.value(HPF.ar(WhiteNoise.ar, \ffreq.kr(1500))));
	}),

	SynthDef.new(\snare, {
		OffsetOut.ar(\out.kr(0), o.value(LFNoise1.ar(3000)));
	}),

	SynthDef.new(\varsaw, {
		var freq = \freq.kr(400);
		var timescale = \timescale.kr(1);
		var detune = \detune.kr(0.1).midiratio;
		var sig = VarSaw.ar(freq * [1, detune]).sum / 2;
		var ffreqRatioHi = \ffreqRatioHi.kr(8).clip(1e-4, 64.0);
		var ffreqRatioLo = \ffreqRatioLo.kr(1.0).clip(1e-4, 64.0);
		var ffEnv = EnvGen.ar(
			Env.perc(\ffAtk.kr(0.1), \ffRelease.kr(0.9)),
			timeScale: timescale,
			doneAction: Done.none
		).exprange(ffreqRatioLo, ffreqRatioHi);
		var filteredSig = BMoog.ar(sig, (freq * ffEnv).clip(20.0, 16000.0), \q.kr(0.5));
		OffsetOut.ar(\out.kr(0), o.value(filteredSig, timescale))
	})
];