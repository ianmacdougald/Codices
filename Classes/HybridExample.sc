HybridExample : CodexHybrid {
	var routine, pattern;

	*defaultModulesPath {
		^this.filenameString.dirname
		+/+"ExampleModules";
	}

	initHybrid {}

	*makeTemplates { | templater |
		//Three custom templates...
		templater.patternFunction( "sequence0" );
		templater.patternFunction( "sequence1" );
		templater.patternFunction( "sequence2" );
		//Three SynthDefs...
		templater.synthDef( "synthDef0" );
		templater.synthDef( "synthDef1" );
		templater.synthDef( "synthDef2" );
	}

	play {
		routine ?? { 
			routine = fork{
				pattern = modules.sequence0.play;
				2.wait;
				pattern.stop;
				pattern = modules.sequence1.play;
				4.wait;
				pattern.stop;
				pattern = modules.sequence2.play;
				2.wait;
				pattern.stop;
				pattern = nil;
				0.1.wait;
				"Hybrid Example : All done".postln;
			};
		};
	}

	stop {
		routine !? { 
			routine.stop; 
			pattern !? {
				pattern.stop; 
				pattern = nil;
			}; 
			routine = nil;
		}
	}
}
