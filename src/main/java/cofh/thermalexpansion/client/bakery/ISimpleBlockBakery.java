package cofh.thermalexpansion.client.bakery;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.List;

/**
 * Created by covers1624 on 28/10/2016.
 */
public interface ISimpleBlockBakery extends ICustomBlockBakery {

    List<BakedQuad> bakeQuads(EnumFacing face, IExtendedBlockState state);
}
