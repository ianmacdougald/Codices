var asymb = {| name, n, def(0.5) |
	format(name, n).asSymbol.kr(def); 
}; 
var op = { | freq(\freq.kr(400)), timescale(\timescale.kr(1)), n(1) |	
	var freqRatio = asymb.("op%FreqRatio", n, 0.5); 
	var atk = asymb.("op%Atk", n, 0);
	var release = asymb.("op%Release", n, 1.0); 
	var amp = asymb.("op%Amp", n, 1.0);
	var env = EnvGen.ar(Env.perc(atk, release), 
		timeScale: timescale
	); 
	SinOsc.ar(freq * freqRatio) * env * amp;
};

SynthDef.new(\fm4, { 
	var freq = \freq.kr(400); 
	var timescale = \timescale.kr(1);
	var ops = 4.collect({| num | op.value(freq, timescale, num)}); 
	var sig = SinOsc.ar(freq * ops.sum + freq); 
	var env = EnvGen.ar(Env.perc(\atk.kr(0), \release.kr(1)), 
		timeScale: timescale, 
		doneAction: Done.freeSelf
	); 
	sig = sig * env * \ampDB.kr(-12).dbamp; 
	Out.ar(\out.kr(0), Pan2.ar(sig, \pan.kr(0)))
});
