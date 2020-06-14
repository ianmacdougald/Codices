{ |modules|

	Pbind(
		\instrument, modules.synthDef0.name, 
		\dur, 0.1, 
		\timescale, Pkey(\dur) * 4,
		\degree, Pseq([0, 2, 4, 7], inf), 
		\scale, Scale.minor, 
		\mtranspose, Pstutter(8, Pseq([0, 4], inf)), 
		\octave, Pstutter(32, Pseq([5, 6], inf)), 
		\pan, Pwhite(-1.0, 1.0, inf), 
		\ampDB, Pseq([-12, Pn(-18, 3)], inf)
	);

}
