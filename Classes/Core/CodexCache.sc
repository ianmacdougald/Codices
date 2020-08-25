CodexCache {
	var dictionary;

	*new { ^super.new.initCache }

	initCache { dictionary = Dictionary.new }

	notAt { | key, subkey |
		this.newDictionary(key);
		^dictionary[key][subkey].isNil;
	}

	newDictionary { | key |
		dictionary[key] ?? {
			dictionary[key] = Dictionary.new;
		};
	}

	addToDictionary { | key, subkey, item |
		dictionary[key].add(subkey -> item);
	}

	add { | key, subkey, item |
		if(this.notAt(key, subkey), {
			this.addToDictionary(key, subkey, item);
		});
	}

	removeModules {  | key, subkey |
		try { ^dictionary[key].removeAt(subkey) }{ ^nil; }
	}

	removeAt { | key |
		^dictionary.removeAt(key);
	}

	at { | key | ^dictionary[key.asSymbol] }

	clear { dictionary.clear; }

	modulesAt { | key, subkey | 
		var copy = ();
		dictionary[key][subkey].keysValuesDo({
			| key, value | 
			copy.add(key -> value.copy);
		});
		^copy;
	}

	copyEntry { | key, toCopy, newEntry |
		if(this.notAt(key, newEntry), {
			dictionary[key].add(newEntry -> dictionary[key][toCopy]);
		});
	}

	keysAt { | key | ^dictionary[key].keys }

	keys { ^dictionary.keys }

	printOn { | stream |
		if (stream.atLimit) { ^this };
		stream << this.class.name << "[ " ;
		dictionary.printItemsOn(stream);
		stream << " ]" ;
	}

	do { | function | dictionary.do({ | item, index | function.value(item, index) }) }
}
