var makeName = {|input|
	CrummySequencer.formatSynthName(input);
};

var scale = Scale.zhi;
var root = 3;
// var varsawFreq = Place([0, 2, 4, [7, 6, 6, 5, 5, 6, 6, 7]], inf).asStream;
var melodyPattern = Pbind(
	\instrument, makeName.value(\melody),
	\degree, Pseq([0, 2, 4, 6], inf),
	\root, Pstutter(16, Pseq([0, -4], inf))+root,
	\scale, Pstutter(16, Pseq([Scale.minor, Scale.major], inf)),
	\octave, Pstutter(64, Pseq([5, 6], inf)) - 1,
	\atk, 0.2,
	\release, 1.8,
	\timescale, Pexprand(0.5, 1.0, inf),
	\ampDB, -24,
	\ffAtk, Pwhite(0.0, 0.0625, inf),
	\ffRelease, Pexprand(0.125, 0.5, inf),
	\ffreqRatioHi, Pexprand(4.0, 16.0, inf).floor,
	\ffreqRatioLo, 4.0,
	\q, Pexprand(0.95, 0.99, inf),
	\detune, 0.2,
	\broadcast, Pfunc({|ev|
		Pdefn(\degree, ev.degree);
		Pdefn(\root, ev.root);
		Pdefn(\scale, ev.scale);
		Pdefn(\octave, ev.octave)
	})
).asStream;

var counterPattern = Pbind(
	\instrument, makeName.value(\counter),
	\n, 8,
	\degree, Pdefn(\degree)
	+ Pxrand([0, 2, 4, 5, 7, 9, 11, 14], inf)
	+ Pn(Plazy({|ev|
		var arr = Array.fill(ev.n, {|i| i * [2, 4, 7].wchoose([2, 3, 4].normalizeSum)});
		Pn(arr, inf)
	}), inf),
	\root, Pdefn(\root),
	\scale, Pdefn(\scale),
	\octave, Pdefn(\octave) + 1,
	\atk, 0.1 * Pfunc({|ev|
		Array.geom(ev.n, 1.0, 1/2)
	}),
	\release, 1.0 * Pfunc({|ev|
		Array.geom(ev.n, 1.0, 1/3).reverse
	}),
	\timescale, 1,
	\ampDB, (-9.dbamp * Pfunc({|ev|
		Array.geom(ev.n, 1.0, 1/2).normalizeSum
	})).ampdb,
	\pan, Pfunc({|ev|
		Array.rand(ev.n, -1.0, 1.0)
	}),
).asStream;


(
kick: {
	Synth(makeName.value(\pmosc), [
		\freq, 72.5,
		\carModRatio, 3,
		\carRelease,0.03,
		\release, 0.75,
		\pmindex, 1,
		\ampDB, -3
	])
},

hh: {
	Synth(makeName.value(\hh), [
		\release, 0.05,
		\pan, 1.0.bilinrand,
		\ampDB, -12
	])
},

snare: {
	Synth(makeName.value(\snare), [
		\release, 0.105,
		\pan, 0.5.bilinrand,
		\ampDB, -12
	])
},

counter: {
	counterPattern.next(()).play;
},
// varsaw: {
// 	Synth(makeName.value(\varsaw), [
// 		\freq, varsawFreq.next.degreeToKey(scale).midiratio * root,
// 		\ffRelease, 0.5,
// 		\ffreqRatioLo, exprand(0.25, 1.0),
// 		\ffreqRatioHi, exprand(4.0, 8.0).round(2.0),
// 		\atk, 0.1,
// 		\detune, 0.1,
// 		\timescale, exprand(0.5, 1.0),
// 		\ampDB, -9,
// 	])
// }

melody: {
	melodyPattern.next(()).play;
}
);