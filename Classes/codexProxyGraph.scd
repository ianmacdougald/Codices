{ | modules, server, nodes, cleanup_list |
	//cleanup_list is a list for registering functions that
	//will be evaluated when the method "free" is called on the instance.

	//nodes is an environment-typed class storing the NodeProxies
	nodes.use({
		//Map nodes together here...
	});
}