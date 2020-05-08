HybridAbstraction_Base {
	//This class manages instances of its subclasses and their respective server resources
	classvar hybridInstances;
	classvar <dictionary, <server;
	classvar isInit = false;
	classvar processor;
	classvar isFreed = false;

	*new{
		if(isInit.not, {this.prInit});
		^this.prProcessInstance(super.new);
	}

	//Class method(s)
	*server_{|newServer|
		server = server ? Server.default;
	}

	//Instance method
	free{
		hybridInstances.remove(this);
	}

	//PRIVATE METHODS//
	*prInit { |id|
		this.server_(server);
		hybridInstances = List.new;
		dictionary = Dictionary.new;
		processor = SynthDefProcessor.new;
		this.prNotifyQuit;
		isInit = true;
	}

	*prProcessInstance {|instance|
		this.prAddInstance(instance);
		this.prMakeSynthDefs(instance);
		^instance;
	}

	*prNotifyQuit {
		ServerQuit.add({
			this.freeDictionary;
		});
	}

	*prAddInstance {|instance|
		hybridInstances.add(instance);
	}

	*prMakeSynthDefs{|instance|
		if(this.prCheckAddDictionary(instance)){
			this.prProcessSynthdefs(this.id);
		};
	}

	*prAddSubDictionary {
		dictionary[this.id] = Dictionary.new;
	}

	*prSubdictionaryExists {
		^dictionary[this.id].isNil.not;
	}

	*prCheckAddDictionary {
		if(this.prSubdictionaryExists.not){
			this.prAddSubDictionary;
			^true;
		};
		^false;
	}

	*prAddToDictionary { |synthDef|
		if(this.prSubdictionaryExists, {
			dictionary[this.id].add(synthDef.name -> synthDef);
		});
	}

	*prRemoveInstance {|toRemove|
		hybridInstances.remove(toRemove);
	}

	*prSetID{
		this.subclassResponsibility(thisMethod);
	}

	*prFormatID{|tag|
		var symbol;
		var string = this.class.asString;
		if(string.contains("Meta_")){
			string = string[("Meta_").size..];
		};
		if(tag.isNil.not){
			string = string++(tag.asString);
		};
		symbol = string.asSymbol;
		^symbol;
	}

	*prProcessSynthdefs{|inputID|
		this.defineSynthDefs;
		processor.add(dictionary[inputID].asArray);
	}

	*registerSynthDef{|synthDef, tag|
		var formattedName = this.formatSynthName(synthDef.name, tag);
		synthDef.name = formattedName;
		this.prAddToDictionary(synthDef, this.id);
	}

	*formatSynthName{|synthDefName, tag|
		var strid = this.id.asString;
		var defstring = synthDefName.asString;
		if(tag.isNil.not){
			defstring = defstring++tag.asString;
		};
		if(defstring.contains(strid).not){
			^format(
				"%_%",
				strid,
				defstring
			).asSymbol;
		};
		^synthDefName;
	}

	*defineSynthDefs{
		this.subclassResponsibility(thisMethod);
	}

	*id {
		this.subclassResponsibility(thisMethod);
	}

	*freeAll{
		hybridInstances.size.do{|item|
			hybridInstances[0].free;
		};
	}

	*freeDictionary{
		var arrayOfSynthDefs;
		dictionary.do{|dictionary|
			dictionary.do{|synthDef|
				arrayOfSynthDefs = arrayOfSynthDefs.add(synthDef);
			};
		};
		this.prGarbageCollect(arrayOfSynthDefs);
		dictionary.clear;
	}

	*removeSubDictionary{|subDictionaryKey|
		var subDictionary = dictionary.removeAt(subDictionaryKey);
		if(subDictionary.isNil.not){
			this.prGarbageCollect(subDictionary.asArray);
		};
	}

	*prGarbageCollect{|synthDefs|
		processor.remove(synthDefs);
	}

	*instances{
		^hybridInstances;
	}
}

HybridAbstraction : HybridAbstraction_Base {
	*new{
		^super.new;
	}

	*id {
		^this.prFormatID;
	}
}