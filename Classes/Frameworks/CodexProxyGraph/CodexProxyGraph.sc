CodexProxyGraph : CodexComposite {
	var <nodes, <cleanup_list, <server;

	*makeTemplates { | templater |
		templater.codexProxyGraph("graph");
	}

	initComposite {
		server = server ?? { Server.default };

		cleanup_list ?? {
			cleanup_list = List.new;
		} !? { this.free };

		nodes = CodexModules.new;

		modules.keysValuesDo({ | key, value |
			if(key!=\graph){
				case
				{ value.isFunction }
				{
					var node = NodeProxy.new(server).source_(value);
					nodes.add(key -> node);
					cleanup_list.add({ node.free });
				}
				{ value.isKindOf(NodeProxy) }
				{
					nodes.add(key -> value);
					cleanup_list.add({ value.free });
				};
			};
		});

		this.initGraph;
	}

	initGraph {
		modules.graph(server, nodes, cleanup_list);
	}

	free {
		cleanup_list.do(_.value);
		cleanup_list.clear;
	}

	storeNode { | key, node |
		var source;
		case
		{ node.isFunction }{
			source = node.def.sourceCode;
		}
		{ node.isKindOf(NodeProxy) }
		{
			source = node.asCode;
		};

		if(source.notNil and: { source.find("open Function").isNil })
		{
			var path = this.moduleFolder+/+key++".scd";
			var file = File.open(path, "w");
			file.write(source);
			file.close;
		}
		//else
		{
			"Could not store new module. Make sure all functions are closed".warn;
		};
		this.reloadScripts;
	}

	doesNotUnderstand { | selector ... args |
		if(know, {
			var node = nodes[selector];
			node !? {
				^node.functionPerformList(
					\value,
					nodes,
					args
				);
			};
			if(selector.isSetter, {
				if(args[0].isFunction, {
					args[0] = NodeProxy.new(server).source = args[0];
				});
				^nodes[selector.asGetter].source = args[0];
			}, { ^this });
		});
		^this.superPerformList(\doesNotUnderstand, selector, args);
	}

	server_{ | newServer |
		var previous = server;
		protect({
			server = newServer;
			this.initComposite;
		}, { | err |
			err !? {
				server = previous;
				this.initComposite;
			};
		});
	}
}
