HybridAbstraction : ModuleManager {
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

	*reloadSynthDefs{
		this.clearSynthDefs;
		this.prMakeSynthDefs;
	}

	//Instance method
	reloadSynthDefs {
		this.class.reloadSynthDefs;
	}

	free{
		hybridInstances.remove(this);
	}

	//PRIVATE METHODS//
	*prInit { |id|
		this.server_(server);
		hybridInstances = List.new;
		dictionary = Dictionary.new;
		processor = SynthDefProcessor.new;
		ServerQuit.add({
			this.freeDictionary;
		});
		isInit = true;
	}

	*prProcessInstance {|instance|
		this.prAddInstance(instance);
		this.prMakeSynthDefs(instance);
		^instance;
	}

	*prAddInstance {|instance|
		hybridInstances.add(instance);
	}

	*prMakeSynthDefs{
		if(this.prCheckAddDictionary){
			this.prGetSynthDefs;
			this.prProcessSynthDefs;
		};
	}

	*prAddSubDictionary {
		dictionary[this.name] = Dictionary.new;
	}

	*prSubdictionaryExists {
		^dictionary[this.name].isNil.not;
	}

	*prCheckAddDictionary {
		if(this.prSubdictionaryExists.not){
			this.prAddSubDictionary;
			^true;
		};
		^false;
	}

	*prRemoveInstance {|toRemove|
		hybridInstances.remove(toRemove);
	}

	*prProcessSynthDefs{
		processor.add(dictionary[this.name].asArray);
	}

	*formatSynthName{|synthDefName|
		var strid = this.name.asString;
		var defstring = synthDefName.asString;
		if(defstring.contains(strid).not){
			^format(
				"%_%",
				strid,
				defstring
			).asSymbol;
		};
		^synthDefName;
	}

	*prGetSynthDefs {
		var objects = this.loadModules.asArray;
		objects.do{|item| this.prTestObject(item)};
	}

	*prTestObject{|object|
		case
		{object.isCollection and: {object.isString.not}}{
			object.flat.do{|item|
				this.prTestObject(item);
			};
		}{object.isFunction}{
			var eval = object.value;
			this.prTestObject(eval);
		}{this.prAddIfSynthDef(object)};
	}

	*prAddIfSynthDef { |obj|
		if(obj.isKindOf(SynthDef)){
			var name = this.formatSynthName(obj.name);
			obj.name = name;
			this.prAddToDictionary(obj);
		};
	}

	*prAddToDictionary { |synthDef|
		if(this.prSubdictionaryExists, {
			dictionary[this.name].add(synthDef.name -> synthDef);
		});
	}

	*freeAll{
		hybridInstances.size.do{|item|
			hybridInstances[0].free;
		};
	}

	*removeDictionary{|key|
		^dictionary.removeAt(key);
	}

	*clearSynthDefsWithKey { |key|
		var toRemove = this.removeDictionary(key);
		toRemove !? {
			toRemove = toRemove.asArray;
		};
		this.removeSynthDefs(toRemove);
	}

	*clearSynthDefs {
		this.clearSynthDefsWithKey(this.name);
	}

	*clear{
		dictionary.keysDo({|key|
			this.clearSynthDefsWithKey(key);
		});
	}

	*removeSynthDefs{|synthDefs|
		processor.remove(synthDefs);
	}

	*instances{
		^hybridInstances;
	}
}