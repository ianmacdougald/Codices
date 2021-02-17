{ | modules, parent, bounds |
	var object = modules.knob;
	var numberBox = modules.numberBox.align_(\center);
	var text = modules.staticText.align_(\center).string_(object.class.asString);
	var layout = VLayout(text, object, numberBox);
	var event = (
		view: CompositeView(parent, bounds).layout = layout,
		object: object,
		label: text,
		spec: ControlSpec.new,
		action: { | ev, obj | },
		numberBox: numberBox
	);
	object.action = { | obj |
		numberBox.value = event.spec.map(obj.value);
		event.action(obj);
	};
	event;
};