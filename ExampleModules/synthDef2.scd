SynthDef.new(\saw, { 
	var freq = \freq.kr(400), timescale = \timescale.kr(1); 
	var sig = LFSaw.ar(freq * [1, \detune.kr(0.2).midiratio]); 
	var ffreq = LFNoise1.ar(ExpRand(1, 8))
	.exprange(\ffreqRatioLo.kr(2.0), \ffreqRatio.kr(8.0)) * freq; 
	sig = RLPF.ar(sig, ffreq.clip(80.0, 16000.0), ExpRand(0.1, 0.5));
	sig = sig * EnvGen.ar(
		Env.perc(\atk.kr(0), \release.kr(1)), 
		doneAction: Done.freeSelf
	) * \ampDB.kr(-12).dbamp; 
	Out.ar(\out.kr(0), Pan2.ar(sig, \pan.kr(0)))
});
