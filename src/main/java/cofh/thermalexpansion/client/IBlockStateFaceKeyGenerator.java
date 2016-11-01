package cofh.thermalexpansion.client;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

/**
 * Created by covers1624 on 2/11/2016.
 */
public interface IBlockStateFaceKeyGenerator {

    String generateKey(IExtendedBlockState state, EnumFacing face);

}
