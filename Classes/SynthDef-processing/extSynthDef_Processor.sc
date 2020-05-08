// + SynthDef_Processor{
//
// 	*postControlNames{|synthDefName, inputClassSymbol|
// 		var controlNameArray = this.getControlNames(synthDefName, inputClassSymbol);
// 		format("SynthDef name: %", synthDefName).postln;
// 		"Control names: [\n".post;
// 		controlNameArray.do{|item, index|
// 			"\t\t".post; item.post;
// 			if(index < (controlNameArray.size - 1)){
// 				",".post;
// 			};
// 			"\n".post;
// 		};
// 		"];\n".postln;
// 	}
//
// 	*postSynthsAndControls{|inputClassSymbol|
// 		var targetList = synthDefDictionary[inputClassSymbol];
//
// 		if(targetList.isNil.not){
//
// 			targetList.do{|synthDef|
// 				this.postControlNames(synthDef.name, inputClassSymbol);
// 			};
//
// 		};
// 	}
//
// 	*postAllSynthsAndControls{
// 		synthDefDictionary.keys.do{|key, index|
//
// 			"------------------------------".postln;
// 			("Class: "++key++"\n").postln;
// 			this.postSynthsAndControls(key);
// 			"------------------------------".postln;
//
// 		};
// 	}
//
// }