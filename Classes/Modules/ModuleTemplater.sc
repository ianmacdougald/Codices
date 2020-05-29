ModuleTemplater { 
	var <>path; 

	*new { |moduleFolder|
		if(moduleFolder.isNil, { 
			Error("No path set for ModuleTemplate").throw;
		}); 

		^super.newCopyArgs(moduleFolder.asString);
	}
	synthDef {|moduleName("synthDef")| 
		var class = this.class;
		class.makeTemplate(class.synthDefString, moduleName, path);
	} 
	pattern {|moduleName("pattern")|
		var class = this.class;
		class.makeTemplate(class.patternString, moduleName, path);	
	}
	function {|moduleName("function")| 
		var class = this.class;
		class.makeTemplate(class.functionString, moduleName, path);
	}
	node {|moduleName("node")| 
		var class = this.class;
		class.makeTemplate(class.nodeString, moduleName, path);
	} 
	event {|moduleName("event")|
		var class = this.class;
		class.makeTemplate(class.eventString, moduleName, path);
	}
	blank {|moduleName("module")|
		var class = this.class;
		class.makeTemplate(class.blankString, moduleName, path);
	} 

	*makeTemplate { |string, moduleName, path|
		this.echoToFile(
			string, 
			this.modulePathString(moduleName, path)
		);
	}

	*modulePathString {|moduleName, path|
		if(path.isNil, { 
			Error("No path set for ModuleTemplate").throw;
		});
		^(path+/+moduleName.asString++".scd");
	}

	*synthDefString { 
		^this.prependMessage({
SynthDef('nil', { 

	//write your code here please...

});
		});
	}

	*patternString { 
		^this.prependMessage({
Pbind(

	//write your code here please...

);
		});
	}

	*functionString { 
		^this.prependMessage({
{arg ev/*, add additional arguments here*/;

	//write your code here please...

};
		});
	}

	*nodeString { 
		^this.prependMessage({
Synth('nil', [

	//write code here please...

]);
		});
	}

	*eventString { 
		^this.prependMessage({
(
			
	//write your code here please...

);
		});
	}

	*blankString { 
		^this.prependMessage(
	{ 

	//write your code here please...

	}
		);
	}

	*promptString { |classType|
		^format("//Define % below...\n", classType.asString);
	}

	*prependMessage {|dataStructure|
		var string = dataStructure.asCompileString; 
		string = string[1..(string.size - 2)];
		^(this.promptString(dataStructure.class)++string);
	}

	*echoToFile {|string, filename|
		if(filename.isNil, { 
			Error("No path set for ModuleTemplate").throw;
		});
		format(
			"echo \"%\" > %",
			string, 
			filename
		).unixCmd(postOutput: false);
	}
}
