SynthDef_Processor{
	classvar processorInstances;
	classvar <synthDefDictionary, server;
	classvar quitNotified = false;
	classvar onLoader, offLoader;

	*new{|classSymbol|
		var return;

		server = Server.default;
		this.pr_Initialize(classSymbol);

		return = super.newCopyArgs;

		processorInstances = processorInstances ? List.new;
		processorInstances.add(return);

		^return;
	}

	*pr_RemoveInstance{|toRemove|
		processorInstances.remove(toRemove);
	}

	*pr_Initialize{|classSymbol|
		var shouldWeLoadSynthDefs = false;

		if(server.hasBooted){
			synthDefDictionary = synthDefDictionary ? Dictionary.new;
			// synthDefLoader = synthDefLoader ? SynthDefLoader.new;
			classSymbol ?? {
				Error("No class symbol supplied!").throw;
			};

			synthDefDictionary[classSymbol] =
			synthDefDictionary[classSymbol] ?? {
				shouldWeLoadSynthDefs = true;
				Dictionary.new;
			};

			this.pr_InitAddSynthDefs(shouldWeLoadSynthDefs, classSymbol);

			if(quitNotified==false){
				ServerQuit.add({
					this.freeSynthDefDictionary;
				});
				quitNotified = true;
			};
		}/*ELSE*/{
			Error("Server is not booted!").throw;
		};
	}

	*prSetClassSymbol{

		this.subclassResponsibility(thisMethod);

	}

	*prFormatClassSymbol{|object, id|
		var return;
		var string = object.class.asString;
		var splitArray = string.split($_);

		if(string[0..4]=="Meta_"){
			string = string[5..(string.size - 1)];
		};
		if(id.isNil.not){
			string = string++(id.asString);
		};
		return = string.asSymbol;
		^return;
	}

	*pr_InitAddSynthDefs{|goForIt = false, classSymbol|
		if(goForIt){
			this.pr_AddSynthDefs(classSymbol);
		};
	}

	*pr_AddSynthDefs{|inputClassSymbol|
		this.defineSynthDefs;
		onLoader = onLoader ? SynthDef_OnLoader.new(server);
		onLoader.process(synthDefDictionary[inputClassSymbol].asArray);
	}

	*registerSynthDef{|synthDef, addIt = false, inputClassSymbol|
		var formattedName = this.formatSynthName(synthDef.name, inputClassSymbol);
		synthDef.name = formattedName;

		//load the synthdef into the correct synthDef dictionary
		if(synthDefDictionary[inputClassSymbol].isNil){
			synthDefDictionary[inputClassSymbol] = Dictionary.new;
		};

		synthDefDictionary[inputClassSymbol].add(synthDef.name -> synthDef);

		//if instructed
		if(addIt){
			//add that synthdef to the server immediately
			synthDefDictionary[inputClassSymbol][synthDef.name].add;

		};
	}

	*formatSynthName{|name, inputClassSymbol|
		var return = name;

		if(name.asString.contains(inputClassSymbol.asString)==false){
			return = format(
				"%_%",
				inputClassSymbol.asString,
				name.asString
			).asSymbol;

		};

		^return;
	}

	*defineSynthDefs{
		this.subclassResponsibility(thisMethod);
	}

	free{
		processorInstances.remove(this);
	}

	*freeAll{
		processorInstances.copy.do{|item|
			item.free;
		};
	}

	*freeSynthDefDictionary{
		var arrayOfSynthDefs;

		synthDefDictionary.copy.do{|dictionary|
			dictionary.do{|synthDef|
				arrayOfSynthDefs = arrayOfSynthDefs.add(synthDef);
			};
		};

		this.pr_GarbageCollect(arrayOfSynthDefs);

		synthDefDictionary = Dictionary.new;
	}

	*removeSynthDefs{|dictionaryKey|

		var dictionary;

		if(dictionaryKey.class!=Symbol){
			Error("Can only remove key from dictionary that is a symbol").throw;
		};
		dictionary = synthDefDictionary.removeAt(dictionaryKey);
		if(dictionary.isNil.not){
			this.pr_GarbageCollect(dictionary.asArray);
			dictionary = nil;
		};

	}

	*pr_GarbageCollect{|synthDef|

		fork{
			offLoader = SynthDef_OffLoader.new(server);

			if(onLoader.isNil.not){
				if(onLoader.isRunning){
					while({onLoader.isRunning}, {1e-3.wait});
				};
			};

			offLoader.process(synthDef);

		};
	}

	*instances{
		^processorInstances;
	}

}