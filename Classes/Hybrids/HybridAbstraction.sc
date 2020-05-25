HybridAbstraction : ModuleManager {
	//This class manages instances of its subclasses and their respective server resources
	classvar hybridInstances;
	classvar <dictionary, <server;
	classvar isInit = false;
	classvar processor;
	classvar isFreed = false;

	*new{
		if(isInit.not, {this.init});
		^this.processInstance(super.new);
	}

	initModules { }

	*server_{|newServer|
		server = server ? Server.default;
	}

	*reloadSynthDefs{
		this.clearSynthDefs;
		this.makeSynthDefs;
	}

	reloadSynthDefs {
		this.class.reloadSynthDefs;
	}

	free{
		hybridInstances.remove(this);
	}

	*init {
		this.server_(server);
		hybridInstances = List.new;
		dictionary = Dictionary.new;
		processor = SynthDefProcessor.new;
		ServerQuit.add({
			this.freeDictionary;
		});
		isInit = true;
	}

	*moduleFolder { 
		^(PathName(this.filenameSymbol.asString).pathOnly
		+/+ "Modules" +/+ this.name); 
	}

	*synthDefFolder { 
		^(this.moduleFolder +/+ "SynthDefs");
	}

	*makeModuleFolder { 
		super.makeModuleFolder(this.moduleFolder);
		super.makeModuleFolder(this.synthDefFolder);
	}

	*processInstance {|instance|
		this.addInstance(instance);
		this.makeSynthDefs(instance);
		^instance;
	}

	*addInstance {|instance|
		hybridInstances.add(instance);
	}

	*makeSynthDefs{
		if(this.checkAddDictionary, {
			this.getSynthDefs;
			this.processSynthDefs;
		});
	}

	*addSubDictionary {
		dictionary[this.name] = Dictionary.new;
	}

	*subdictionaryExists {
		^dictionary[this.name].notNil;
	}

	*checkAddDictionary {
		if(this.subdictionaryExists.not){
			this.addSubDictionary;
			^true;
		};
		^false;
	}

	*removeInstance {|toRemove|
		hybridInstances.remove(toRemove);
	}

	*processSynthDefs{
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

	*getSynthDefs {
		var objects = this.loadModules(this.scriptPaths).asArray;
		objects.do{|item| this.testObject(item)};
	}

	*scriptPaths { 
		^super.scriptPaths(this.moduleFolder);
	}

	*testObject{|object|
		case
		{object.isCollection and: {object.isString.not}}{
			object.flat.do{|item|
				this.testObject(item);
			};
		}
		{object.isFunction}{
			var eval = object.value;
			this.testObject(eval);
		}
		{this.addIfSynthDef(object)};
	}

	*addIfSynthDef { |obj|
		if(obj.isKindOf(SynthDef)){
			var name = this.formatSynthName(obj.name);
			obj.name = name;
			this.addToDictionary(obj);
		};
	}

	*addToDictionary { |synthDef|
		if(this.subdictionaryExists, {
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
