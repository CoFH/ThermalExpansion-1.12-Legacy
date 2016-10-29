package cofh.thermalexpansion.client;

import net.minecraft.util.BlockRenderLayer;

/**
 * Created by covers1624 on 28/10/2016.
 */
public interface IBlockLayerProvider {

    int getTexturePasses();

    BlockRenderLayer getRenderlayerForPass(int pass);
}
