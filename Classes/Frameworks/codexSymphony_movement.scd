{ | modules, proxySpace |
	var cleanupList = modules.cleanup;
	proxySpace.use({
		//Write code here...

		//Add resources to be freed at the end of the section.
		//cleanupList.add({proxySpace.server.sendMsg('/b_free', 0)});

		//Return a value specifying the subsection's duration here...
		1;
	});
	//Or here...
	//1;
};
