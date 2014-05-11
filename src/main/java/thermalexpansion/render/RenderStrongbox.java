package thermalexpansion.render;

import codechicken.lib.render.CCRenderState;
import cofh.render.RenderHelper;
import cofh.render.RenderUtils;
import cpw.mods.fml.client.registry.ClientRegistry;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import thermalexpansion.block.TEBlocks;
import thermalexpansion.block.strongbox.BlockStrongbox;
import thermalexpansion.block.strongbox.TileStrongbox;
import thermalexpansion.block.strongbox.TileStrongboxCreative;
import thermalexpansion.core.TEProps;
import thermalexpansion.render.model.ModelStrongbox;

public class RenderStrongbox extends TileEntitySpecialRenderer implements IItemRenderer {

	public static final RenderStrongbox instance = new RenderStrongbox();
	static ResourceLocation[] texture = new ResourceLocation[BlockStrongbox.Types.values().length];

	static ModelStrongbox model = new ModelStrongbox();

	static {
		ClientRegistry.bindTileEntitySpecialRenderer(TileStrongbox.class, instance);
		ClientRegistry.bindTileEntitySpecialRenderer(TileStrongboxCreative.class, instance);

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TEBlocks.blockStrongbox), instance);
	}

	public static void initialize() {

		texture[BlockStrongbox.Types.BASIC.ordinal()] = new ResourceLocation(TEProps.PATH_RENDER + "strongbox/Strongbox_Basic.png");
		texture[BlockStrongbox.Types.HARDENED.ordinal()] = new ResourceLocation(TEProps.PATH_RENDER + "strongbox/Strongbox_Hardened.png");
		texture[BlockStrongbox.Types.REINFORCED.ordinal()] = new ResourceLocation(TEProps.PATH_RENDER + "strongbox/Strongbox_Reinforced.png");
		texture[BlockStrongbox.Types.RESONANT.ordinal()] = new ResourceLocation(TEProps.PATH_RENDER + "strongbox/Strongbox_Resonant.png");
		texture[BlockStrongbox.Types.CREATIVE.ordinal()] = new ResourceLocation(TEProps.PATH_RENDER + "strongbox/Strongbox_Creative.png");
	}

	public void render(int metadata, int access, int facing, double x, double y, double z) {

		RenderHelper.bindTexture(texture[metadata]);

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glTranslated(x, y + 1.0, z + 1.0F);
		GL11.glScalef(1.0F, -1F, -1F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		GL11.glRotatef(RenderUtils.facingAngle[facing], 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		model.render(access);
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {

		RenderUtils.preRender();
		TileStrongbox strongbox = (TileStrongbox) entity;
		model.boxLid.rotateAngleX = (float) strongbox.getRadianLidAngle(f);
		render(strongbox.type, strongbox.getAccess().ordinal(), strongbox.getFacing(), x, y, z);
		CCRenderState.useNormals = false;
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {

		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {

		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

		double offset = -0.5;
		if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
			offset = 0;
		}
		int access = 0;

		if (item.stackTagCompound != null) {
			access = item.stackTagCompound.getByte("Access");
		}
		model.boxLid.rotateAngleX = 0;
		render(item.getItemDamage(), access, 5, offset, offset, offset);
	}

}
