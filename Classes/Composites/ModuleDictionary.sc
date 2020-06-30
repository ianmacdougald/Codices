ModuleDictionary {
	var dictionary;

	*new { ^super.new.makeDictionary; }

	makeDictionary { dictionary = Dictionary.new; }

	notInDictionary { | key, subkey |
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

	addEntry { | key, subkey, item |
		if(this.notInDictionary(key, subkey), {
			this.addToDictionary(key, subkey, item);
		});
	}

	removeModules {  | key, subkey |
		if(this.notInDictionary(key, subkey).not, {
			^dictionary[key].removeAt(subkey);
		});
		^nil;
	}

	removeAt { | key |
		^dictionary.removeAt(key);
	}

	at { | key | ^dictionary.at(key); }

	clear { dictionary.clear; }

	modulesAt { | key, subkey | ^dictionary[key].at(subkey); }

	copyEntry { | key, toCopy, newEntry |
		if(this.notInDictionary(key, newEntry), {
			dictionary[key].add(newEntry -> dictionary[key][toCopy].copy);
			// this.addSubDictionary(key, newEntry);
			/*dictionary[key][toCopy].keysValuesDo({ | key, value |
				dictionary[key][newEntry].add(key -> value.copy);
			});*/
		});
	}

	keysAt { | key | ^dictionary[key].keys; }

	keys { ^dictionary.keys; }

	printOn { | stream |
		if (stream.atLimit) { ^this };
		stream << this.class.name << "[ " ;
		dictionary.printItemsOn(stream);
		stream << " ]" ;
	}
}