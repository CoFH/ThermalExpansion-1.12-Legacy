package cofh.thermalexpansion.block;

import codechicken.lib.block.property.unlisted.UnlistedBooleanProperty;
import codechicken.lib.block.property.unlisted.UnlistedIntegerProperty;
import codechicken.lib.block.property.unlisted.UnlistedResourceLocationProperty;

public class CommonProperties {

	public static final UnlistedIntegerProperty FACING_PROPERTY;
	public static final UnlistedBooleanProperty ACTIVE_PROPERTY;
	public static final UnlistedIntegerProperty TYPE_PROPERTY;
	public static final UnlistedResourceLocationProperty ACTIVE_SPRITE_PROPERTY;

	static {
		FACING_PROPERTY = new UnlistedIntegerProperty("facing");
		ACTIVE_PROPERTY = new UnlistedBooleanProperty("active");
		TYPE_PROPERTY = new UnlistedIntegerProperty("type");
		ACTIVE_SPRITE_PROPERTY = new UnlistedResourceLocationProperty("active_texture");
	}
}
