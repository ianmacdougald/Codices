+ Dictionary {

	asYAMLString {
		var string = "";
		this.keysValuesDo({|key, value, index|
			string = string++format("%: %\n", key, value);
		});
		^string;
	}

	withSymbolKeys {
		var newDictionary = Dictionary.new;
		this.keysValuesDo({|key, value|
			newDictionary.add(key.asSymbol -> value);
		});
		^newDictionary;
	}

}

+ Object {
	asYAMLString {
		var str = this.asString;
		^format("%: %\n", str, str);
	}
}

+ IdentityDictionary {

	withSymbolKeys {
		var newDictionary = IdentityDictionary.new;
		this.keysValuesDo({|key, value|
			newDictionary.add(key.asSymbol -> value);
		});
		^newDictionary;
	}

}