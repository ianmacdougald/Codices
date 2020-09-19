{ | modules, proxySpace |
	//The server object attached to the proxySpace.
	var server = proxySpace.server;
	//A list for registering functions for cleaning up at the end of the piece.
	var cleanup = modules.cleanup;
	proxySpace.use({ 
		//Write your code here...

		//Return a number to specify the duration of the section. 
		//Or don't if there is no specified duration.
		exprand(2.0, 8.0);
	});
}
