package cofh.thermalexpansion.client;

import codechicken.lib.texture.TextureUtils.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

/**
 * Created by covers1624 on 25/10/2016.
 */
public interface IBlockTextureProvider extends IIconRegister, IBlockLayerProvider {


    TextureAtlasSprite getTexture(EnumFacing side, int metadata);

}
