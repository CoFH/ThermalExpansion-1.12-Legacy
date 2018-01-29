package cofh.thermalexpansion.util;

import codechicken.lib.block.property.unlisted.UnlistedPropertyBase;

//TODO, move to ccl.
public class UnlistedGenericProperty<T> extends UnlistedPropertyBase<T> {

	private final Class<T> clazz;

	public UnlistedGenericProperty(String name, Class<T> clazz) {
		super(name);
		this.clazz = clazz;
	}

	@Override
	public Class<T> getType() {

		return clazz;
	}

	@Override
	public String valueToString(T value) {

		return value.toString();
	}
}
