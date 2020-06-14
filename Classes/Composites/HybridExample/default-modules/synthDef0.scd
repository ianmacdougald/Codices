SynthDef.new(\sine, { 
	var freq = \freq.kr(400); 
	var sig = SinOsc.ar(\freq.kr(400)); 
	var env = EnvGen.ar(Env.perc(\atk.kr(0), \release.kr(1)), 
		doneAction: Done.freeSelf
	); 
	sig = sig * env * \ampDB.kr(-12).dbamp; 
	Out.ar(\out.kr(0), Pan2.ar(sig, \pan.kr(0)));
});
