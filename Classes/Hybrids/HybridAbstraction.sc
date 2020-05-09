HybridAbstraction {
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

	*prMakeSynthDefs{|instance|
		if(this.prCheckAddDictionary(instance)){
			this.prProcessSynthdefs;
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

	*prRemoveInstance {|toRemove|
		hybridInstances.remove(toRemove);
	}

	*prProcessSynthdefs{
		this.defineSynthDefs;
		processor.add(dictionary[this.id].asArray);
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

	*id {
		^this.name;
	}

	*modulePath {
		^PathName(this.filenameSymbol.asString).pathOnly;
	}

	*loadModules {
		var paths = this.modulePath.getPaths;
		var objects = paths.select({|item| PathName(item).extension=="scd"});
		objects = objects.collect(_.load);
		^objects;
	}

	*defineSynthDefs {
		var objects = this.loadModules;
		objects.do{|item| this.prTestObject(item)};
	}

	*prTestObject{|object|
		format("object is %", object).postln;
		case
		{object.isCollection and: {object.isString.not}}{
			object.flat.do{|item|
				format("\t\titem is: %", item).postln;
				this.prTestAdd(item);
			};
		}{object.isFunction}{
			var eval = object.value;
			this.prTestObject(eval);
		}{this.prTestAdd(object)};
	}

	*prTestAdd { |obj|
		if(obj.isKindOf(SynthDef)){
			var name = this.formatSynthName(obj.name);
			obj.name = name;
			this.prAddToDictionary(obj);
		};
	}

	*prAddToDictionary { |synthDef|
		if(this.prSubdictionaryExists, {
			dictionary[this.id].add(synthDef.name -> synthDef);
		});
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