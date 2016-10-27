package cofh.thermalexpansion.client.bakery;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.List;

/**
 * Created by covers1624 on 28/10/2016.
 */
public interface ICustomBlockBakery {

    IExtendedBlockState handleState(IExtendedBlockState state, TileEntity tileEntity);

    List<BakedQuad> bakeQuads(EnumFacing face, IExtendedBlockState state);

    List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack);
}
