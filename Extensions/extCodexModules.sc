+ CodexModules {
	*newFrom { | modulesClass |
		if(modulesClass.isKindOfClass(Dictionary)){ 
			var modules = modulesClass.new; 
			this.keysValuesDo { | key, value |
				modules.add(key -> value);
			};
		};
		^this;
	}
}

/*+ CodexObject { 
	*newFrom { | object |
		case 
		{ object.isKindOfClass(this) }{ 
			^object.new(key, function, envir);
		}
		{ object.isCollection }{ 
			^[ key, function, envir ].as(object);
		}
		{ ^this };
	}
}

+ Object { 
	asCodexObject { | key |
		^CodexObject(key ? this.asSymbol, { this }, currentEnvironment);
	}
}*/
