package cofh.thermalexpansion.block;

import codechicken.lib.block.property.unlisted.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by covers1624 on 31/10/2016.
 */
public class CommonProperties {
    public static final UnlistedMapProperty SPRITE_FACE_LAYER_PROPERTY;
    public static final UnlistedMapProperty SPRITE_FACE_PROPERTY;
    public static final UnlistedResourceLocationProperty PARTICLE_SPRITE_PROPERTY;
    public static final UnlistedIntegerProperty FACING_PROPERTY;
    public static final UnlistedBooleanProperty ACTIVE_PROPERTY;
    public static final UnlistedIntegerProperty TYPE_PROPERTY;
    public static final UnlistedResourceLocationProperty ACTIVE_SPRITE_PROPERTY;

    static {
        SPRITE_FACE_LAYER_PROPERTY = new UnlistedMapProperty("sprite_face_layer_map");
        SPRITE_FACE_PROPERTY = new UnlistedMapProperty("sprite_face_property");
        PARTICLE_SPRITE_PROPERTY = new UnlistedResourceLocationProperty("particle");
        FACING_PROPERTY = new UnlistedIntegerProperty("facing");
        ACTIVE_PROPERTY = new UnlistedBooleanProperty("active");
        TYPE_PROPERTY = new UnlistedIntegerProperty("type");
        ACTIVE_SPRITE_PROPERTY = new UnlistedResourceLocationProperty("active_texture");
    }
}
