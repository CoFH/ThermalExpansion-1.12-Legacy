package cofh.thermalexpansion.client;

import net.minecraftforge.common.property.IExtendedBlockState;

/**
 * Created by covers1624 on 26/11/2016.
 */
public interface IBlockStateKeyGenerator {

    String generateKey(IExtendedBlockState state);

}
