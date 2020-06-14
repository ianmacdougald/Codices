{ | modules | 

	Pbind(
		\instrument, modules.synthDef1.name, 
		\dur, 0.2, 
		\degree, Pseq([0, 2, 4, 7], inf), 
		\mtranspose, Pstutter(8, Pseq([0, 4], inf)) + 3, 
		\scale, Scale.minor, 

		\op0Atk, 0.0, 
		\op0Release, 0.25, 
		\op0FreqRatio, 0.5, 
		\op0Amp, 0.5, 

		\op1Atk, 0.5, 
		\op1Release, 0.05, 
		\op1FreqRatio, 2.5, 
		\op1Amp, 0.1, 

		\op2Atk, 0.3, 
		\op2Release, 0.75, 
		\op2FreqRatio, 0.25, 
		\op2Amp, 0.2, 

		\op3Atk, Pseq([0.0, Pn(0.25, 3)], inf), 
		\op3Release, 1 - Pkey(\op3Atk), 
		\op3FreqRatio, Pexprand(1, 8, inf).floor + 0.005,
		\op3Amp, 0.05
	)
}
