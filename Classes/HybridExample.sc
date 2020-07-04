HybridExample : CodexHybrid {
	var routine, pattern;

	*defaultModulesPath {
		^this.filenameString.dirname
		+/+"ExampleModules";
	}

	initHybrid {}

	//Templater has been extended to make "patternFunctions", which are functions that return patterns.
	//This is necessary for passing in the SynthDef's name into the pattern
	*makeTemplates { | templater |
		templater.patternFunction( "sequence0" );
		templater.patternFunction( "sequence1" );
		templater.patternFunction( "sequence2" );
		//Three SynthDefs...
		templater.synthDef( "synthDef0" );
		templater.synthDef( "synthDef1" );
		templater.synthDef( "synthDef2" );
	}

	play {
		routine = fork{
			pattern = modules.sequence0.play;
			2.wait;
			pattern.stop;
			pattern = modules.sequence1.play;
			8.wait;
			pattern.stop;
			pattern = modules.sequence2.play;
			8.wait;
			pattern.stop;
			0.1.wait;
			"All done".postln;
		};
	}

	stop {
		routine.stop;
		pattern.stop;
	}
}
