{ | modules | 
	
	Pbind(
		\instrument, modules.synthDef2.name, 
		\dur, 0.1, 
		\degree, Pseq([0, 2, 4, 7], inf), 
		\mtranspose, Pstutter(4, Pseq([0, 2, -1, 3], inf)), 
		\scale, Scale.minor, 
		\octave, 5,
		\atk, 0.005, 
		\release, 1 - Pkey(\atk), 
		\ampDB, -20, 
		\pan, Pwhite(-1.0, 1.0, inf), 
		\timescale, Pkey(\dur) * Pexprand(0.5, 8.0, inf),
	);
	
}
