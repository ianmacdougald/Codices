SynthDef.new(\varsaw, { 
	var freq = \freq.kr(400), timescale = \timescale.kr(1); 
	var sig = VarSaw.ar(freq * [1, \detune.kr(0.2).midiratio]); 
	var ffreqModFreq = SinOsc.kr(\ffreqModFreq.kr(0.5));
	var ffreqRatio = \ffreqRatio.kr(2.0); 
	var ffreq = (ffreqModFreq * ffreqRatio) * freq;
	sig = RLPF.ar(sig, ffreq, \rq.kr(0.5));
	sig = DelayN.ar(sig, 0.1, SinOsc.kr(\delayFreq.kr(4)
		.exprange(
			1/freq * \delayOcatveLo.kr(2).reciprocal, 
			1/freq * \delayOctaveHi.kr(4).reciprocal)
		)
	); 
	sig = sig * EnvGen.ar(Env.perc(\atk.kr(0), \release.kr(1)), 
		doneAction: Done.freeSelf
	) * \ampDB.kr(-12).dbamp; 
	Out.ar(\out.kr(0), Pan2.ar(sig, \pan.kr(0)))
});
