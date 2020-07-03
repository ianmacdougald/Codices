//A function that returns a pattern.

{ | modules |

	Pbind(

		\instrument, Pfunc({
			modules[
				format("synthDef%", 3.rand).asSymbol
			].name.postln;
		}),

		\dur, 0.5,

	);

};
