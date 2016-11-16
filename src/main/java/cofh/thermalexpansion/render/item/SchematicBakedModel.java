package cofh.thermalexpansion.render.item;

import codechicken.lib.render.item.IStackPerspectiveAwareModel;
import codechicken.lib.util.TransformUtils;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.item.TEItems;
import cofh.thermalexpansion.util.helpers.SchematicHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel.MapWrapper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

/**
 * Created by covers1624 on 6/11/2016.
 */
public class SchematicBakedModel implements IStackPerspectiveAwareModel {

    private IBakedModel schematic;

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (schematic == null) {
            schematic = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager().getModel(new ModelResourceLocation("thermalexpansion:diagram", "type=schematic"));
        }
        return schematic.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return null;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemStack stack, TransformType cameraTransformType) {
        if (StringHelper.isShiftKeyDown() && cameraTransformType == TransformType.GUI) {
            ItemStack currentItem = SchematicHelper.getOutput(stack, CoreUtils.getClientPlayer().worldObj);
            if (currentItem != null) {
                if (!currentItem.getUnlocalizedName().equals(TEItems.diagramSchematic.getUnlocalizedName())) {
                    IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(currentItem);

                    if (model instanceof IPerspectiveAwareModel) {
                        return ((IPerspectiveAwareModel) model).handlePerspective(cameraTransformType);
                    } else {
                        return MapWrapper.handlePerspective(model, model.getItemCameraTransforms().getTransform(cameraTransformType), cameraTransformType);
                    }

                }
            }
        }
        return MapWrapper.handlePerspective(this, TransformUtils.DEFAULT_ITEM, cameraTransformType);
    }
}
