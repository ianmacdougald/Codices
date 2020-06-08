SynthDef(\synth, {
	var env = EnvGen.kr(Env.asr(\atk.kr(0), 1, \release.kr(1)),
		\gate.kr(1),
		doneAction: Done.freeSelf
	);
	var lag = \lag.kr(0.1);
	var sig = SinOsc.ar(\freq.kr(400, lag));
	var out = sig * env * \ampDB.kr(-12, lag * 2).dbamp;
	Out.ar(\out.kr(0), Pan2.ar(out, \pan.kr(0)))
});
