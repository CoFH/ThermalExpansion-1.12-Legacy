package cofh.thermalexpansion.client.model;

import codechicken.lib.model.loader.IBakedModelLoader;
import com.google.common.collect.ImmutableList.Builder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by covers1624 on 25/10/2016.
 */
public class ThermalExpansionBakedModelLoader implements IBakedModelLoader {

    public static final ThermalExpansionBakedModelLoader INSTANCE = new ThermalExpansionBakedModelLoader();

    public static class ThermalExpansionKeyProvider implements IModKeyProvider {

        public static final ThermalExpansionKeyProvider INSANCE = new ThermalExpansionKeyProvider();

        @Override
        public String getMod() {
            return "ThermalExpansion";
        }

        @Override
        public String createKey(ItemStack stack) {
            return null;
        }

        @Override
        public String createKey(IBlockState state) {
            return null;
        }
    }

    @Override
    public IModKeyProvider createKeyProvider() {
        return ThermalExpansionKeyProvider.INSANCE;
    }

    @Override
    public void addTextures(Builder<ResourceLocation> builder) {

    }

    @Override
    public IBakedModel bakeModel(String key) {
        return null;
    }
}
