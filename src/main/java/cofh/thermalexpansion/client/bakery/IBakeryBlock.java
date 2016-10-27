package cofh.thermalexpansion.client.bakery;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by covers1624 on 28/10/2016.
 */
public interface IBakeryBlock {

    @SideOnly(Side.CLIENT)
    ICustomBlockBakery getCustomBakery();

}
