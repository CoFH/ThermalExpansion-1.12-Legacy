package cofh.thermalexpansion.core;

import codechicken.lib.texture.TextureUtils.IIconRegister;
import cofh.api.tileentity.ISidedTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;

/**
 * Created by covers1624 on 25/10/2016.
 */
public interface IBlockTextureProvider extends IIconRegister {


    TextureAtlasSprite getTexture(EnumFacing side, int metadata);

    int getTexturePasses();

    BlockRenderLayer getRenderlayerForPass(int pass);

}
