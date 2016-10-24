package cofh.thermalexpansion.block;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

/**
 * Base default types.
 */
public enum EnumType implements IStringSerializable {
    CREATIVE,
    BASIC,
    HARDENED,
    REINFORCED,
    RESONANT;

    @Override
    public String getName() {
        return name().toLowerCase(Locale.US);
    }

    public int meta() {
        return ordinal();
    }

    public static EnumType fromMeta(int meta) {
        try {
            return values()[meta];
        } catch (IndexOutOfBoundsException e){
           throw new RuntimeException("Someone has requested an invalid metadata for a block inside ThermalExpansion.", e);
        }
    }

    public static int meta(EnumType type) {
        return type.ordinal();
    }
}
