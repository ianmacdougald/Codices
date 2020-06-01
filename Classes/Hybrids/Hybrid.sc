Hybrid : ModuleManager {
	// classvar hybridInstances;
	classvar isInit = false;
	// classvar isFreed = false;
	classvar <dictionary;
	classvar processor;
	var synthDefKey = \synthDefs;
	var <synthDefTemplater;
	var <server;

	*new {|moduleName(\default), from|
		if(isInit.not, {this.init});
		^super.new(moduleName, from)
		.processInstance;
	}

	//initializing class variables
	*init {
		// hybridInstances = List.new;
		dictionary = Dictionary.new;
		processor = SynthDefProcessor.new;
		ServerQuit.add({
			this.freeDictionary;
		});
		isInit = true;
	}

	//setting up the instance
	processInstance {
		server = server ? Server.default;
		synthDefTemplater = ModuleTemplater.new(this.synthDefFolder);
		if(this.checkDictionary, {
			// this.addInstance;
			this.makeSynthDefs;
		});
	}

	checkDictionary {
		if(this.subDictionaryExists.not, {
			this.addSubDictionary;
			^true;
		}, {^false});
	}

	*subDictionaryExists {|className|
		^dictionary[className].notNil;
	}

	subDictionaryExists {
		var class = this.class;
		^class.subDictionaryExists(class.name);
	}

	*addSubDictionary { |className|
		dictionary[className] = Dictionary.new;
	}

	addSubDictionary {
		var class = this.class;
		class.addSubDictionary(class.name);
	}

	/*	*addInstance {|instance|
	hybridInstances.add(instance);
	}

	addInstance {
	this.class.addInstance(this);
	}*/

	makeSynthDefs {
		this.getSynthDefs;
		this.processSynthDefs;
	}

	getSynthDefs {
		this.checkSynthDefFolder;
		PathName(this.synthDefFolder).files.do({|script|
			this.addToDictionary(script.fullPath.load);
		});
	}

	checkSynthDefFolder {
		if(File.exists(this.synthDefFolder).not, {
			this.class.makeModuleFolder(this.synthDefFolder);
			synthDefTemplater.synthDef;
		});
	}

	*addToDictionary {|className, synthDef|
		dictionary[className].add(synthDef.name.asSymbol -> synthDef);
	}

	addToDictionary { |synthDef|
		if(synthDef.isKindOf(SynthDef), {
			var class = this.class;
			synthDef.name = this.formatName(synthDef);
			class.addToDictionary(class.name, synthDef);
		});
	}

	formatName { |synthDef|
		^this.class.formatName(synthDef.name);
	}

	*formatName { |synthDefName|
		var strid = this.name.asString;
		var defstring = synthDefName.asString;
		if(defstring.contains(strid).not, {
			^format(
				"%_%",
				strid,
				defstring
			).asSymbol;
		});
		^synthDefName;
	}

	*processSynthDefs {|className|
		processor.add(dictionary[className].asArray);
	}

	processSynthDefs {
		var class = this.class;
		class.processSynthDefs(class.name);
	}

	//other utilities...
	server_{|newServer|
		server = server ? Server.default;
	}

	reloadSynthDefs {
		this.clearSynthDefs;
		this.makeSynthDefs;
	}

	*clearSynthDefs { |className|
		var toRemove = this.removeDictionary(className);
		toRemove !? {
			toRemove = toRemove.asArray;
			this.removeSynthDefs(toRemove);
		};
	}

	clearSynthDefs {
		var class = this.class;
		class.clearSynthDefs(class.name);
	}

	*removeDictionary { |key|
		^dictionary.removeAt(key);
	}

	*removeSynthDefs {|synthDefs|
		processor.remove(synthDefs);
	}

	reset {
		this.reloadSynthDefs;
	}

	synthDefKey_{ |newKey, from|
		synthDefKey = newKey;
		synthDefTemplater.path = this.synthDefFolder;
		this.reset;
	}

	/*	*instances {
	^hybridInstances;
	}*/

	synthDefFolder {
		^(this.moduleFolder+/+synthDefKey);
	}

	makeSynthDefFolder {
		this.class.makeModuleFolder(this.synthDefFolder);
	}

	/*
	*removeInstance {|toRemove|
	hybridInstances.remove(toRemove);
	}

	*freeAll {
	hybridInstances.size.do{|item|
	hybridInstances[0].free;
	};
	}*/
}
