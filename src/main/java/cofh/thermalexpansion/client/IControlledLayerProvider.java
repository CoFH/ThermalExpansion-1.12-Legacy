package cofh.thermalexpansion.client;

import net.minecraft.tileentity.TileEntity;

/**
 * Created by covers1624 on 30/10/2016.
 */
public interface IControlledLayerProvider {

    boolean shouldUsePass(int pass, TileEntity tileEntity);

}
