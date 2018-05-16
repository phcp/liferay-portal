package com.liferay.forms.apio.internal.representable;

import com.liferay.apio.architect.representor.Representable;
import com.liferay.apio.architect.representor.Representor;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true)
public class ThingRepresentable
	implements Representable<Thing, String, ThingIdentifier> {

	@Override
	public String getName() {
		return "thing";
	}

	@Override
	public Representor<Thing> representor(
		Representor.Builder<Thing, String> builder) {

		return builder.types(
			"jakdlk"
		).identifier(
			thing -> thing.name
		).addString(
			"portletNamespace",
			thing -> thing.name
		).build();
	}

}