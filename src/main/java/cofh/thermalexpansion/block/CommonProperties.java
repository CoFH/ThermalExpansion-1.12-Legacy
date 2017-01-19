package cofh.thermalexpansion.block;

import codechicken.lib.block.property.unlisted.UnlistedBooleanProperty;
import codechicken.lib.block.property.unlisted.UnlistedIntegerProperty;
import codechicken.lib.block.property.unlisted.UnlistedResourceLocationProperty;

public class CommonProperties {

	@Deprecated//Use TEProps.FACING
	public static final UnlistedIntegerProperty FACING_PROPERTY;
	@Deprecated//Use TEProps.ACTIVE
	public static final UnlistedBooleanProperty ACTIVE_PROPERTY;
	@Deprecated//"Use state.getValue(VARIANT).getMetadata();"
	public static final UnlistedIntegerProperty TYPE_PROPERTY;
	//KL Go ahead and remove ^^^^^ when you rewrite BlockFrame.
	public static final UnlistedResourceLocationProperty ACTIVE_SPRITE_PROPERTY;

	static {
		FACING_PROPERTY = new UnlistedIntegerProperty("facing");
		ACTIVE_PROPERTY = new UnlistedBooleanProperty("active");
		TYPE_PROPERTY = new UnlistedIntegerProperty("type");
		ACTIVE_SPRITE_PROPERTY = new UnlistedResourceLocationProperty("active_texture");
	}
}
