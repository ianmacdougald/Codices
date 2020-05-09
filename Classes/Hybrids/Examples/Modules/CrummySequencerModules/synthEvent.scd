var makeName = {|input|
	CrummySequencer.formatSynthName(input);
};

var scale = Scale.zhi;
var root = 4;
// var varsawFreq = Place([0, 2, 4, [7, 6, 6, 5, 5, 6, 6, 7]], inf).asStream;
var varsawPattern = Pbind(
	\instrument, makeName.value(\varsaw),
	\degree, Pseq([0, 2, 4, 6], inf),
	\root, Pstutter(16, Pseq([0, -4], inf))+root,
	\scale, Pstutter(16, Pseq([Scale.minor, Scale.major], inf)),
	\octave, Pstutter(64, Pseq([5, 6], inf)),
	\atk, 0.1,
	\release, 1.0,
	\timescale, Pexprand(0.5, 1.0, inf),
	\ampDB, -9,
	\broadcast, Pfunc({|ev|
		Pdefn(\degree, ev.degree);
		Pdefn(\root, ev.root);
		Pdefn(\scale, ev.scale);
		Pdefn(\octave, ev.octave)
	})
).asStream;

var sinePattern = Pbind(
	\instrument, makeName.value(\sine),
	\degree, Pdefn(\degree)
	+ Pxrand([0 ,2, 4, 5, 7, 9, 11, 14], inf) + 7,
	\root, Pdefn(\root),
	\scale, Pdefn(\scale),
	\octave, Pdefn(\octave),
	\atk, 0.1,
	\release, 1.0,
	\timescale, 1,
	\ampDB, -9,
).asStream;


(
kick: {
	Synth(makeName.value(\pmosc), [
		\freq, 72.5,
		\carModRatio, 3,
		\carRelease,0.03,
		\release, 0.75,
		\pmindex, 1,
		\ampDB, -9
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

/*sine: {
	Synth(makeName.value(\sine), [
		\freq, sineFreq.next.degreeToKey(scale).midiratio * root * 2,
		\release, 0.9,
		\atk, 0.1,
		\ampDB, -16
	])
},*/

sine: {
	sinePattern.next(()).play;
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

varsaw: {
	varsawPattern.next(()).play;
}
);