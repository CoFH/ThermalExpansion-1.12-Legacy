package cofh.thermalexpansion.block.light;

import cofh.api.block.IBlockConfigGui;
import cofh.core.render.IconRegistry;
import cofh.lib.util.helpers.ColorHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.block.simple.BlockFrame;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.util.crafting.TransposerManager;
import cofh.thermalfoundation.fluid.TFFluids;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class BlockLight extends BlockTEBase implements IBlockConfigGui {

	public BlockLight() {

		super(Material.glass);
		setHardness(3.0F);
		setResistance(150.0F);
		setStepSound(soundTypeGlass);
		setBlockName("thermalexpansion.light");
		basicGui = false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		return new TileLight();
	}

	@Override
	public boolean openConfigGui(IBlockAccess world, int x, int y, int z, ForgeDirection side, EntityPlayer player) {

		return ((TileLight) world.getTileEntity(x, y, z)).openGui(player);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {

		for (int i = 0; i < Types.values().length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {

		if (stack.stackTagCompound != null) {
			TileLight tile = (TileLight) world.getTileEntity(x, y, z);

			if (stack.stackTagCompound.hasKey("Color")) {
				tile.setColor(stack.stackTagCompound.getInteger("Color"));
			}
			tile.dim = stack.stackTagCompound.getBoolean("Dim");
			tile.mode = stack.stackTagCompound.getByte("Mode");
		}
		super.onBlockPlacedBy(world, x, y, z, living, stack);
	}

	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {

	}

	@Override
	public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int color) {

		TileLight theTile = (TileLight) world.getTileEntity(x, y, z);

		if (ServerHelper.isServerWorld(world)) {
			return theTile.setColor(ColorHelper.getDyeColor(15 - color));
		}
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int bSide, float hitX, float hitY, float hitZ) {

		TileLight theTile = (TileLight) world.getTileEntity(x, y, z);

		if (ItemHelper.isPlayerHoldingItem(Items.glowstone_dust, player)) {
			if (ServerHelper.isServerWorld(world)) {
				theTile.resetColor();

				if (!player.capabilities.isCreativeMode) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemHelper.consumeItem(player.getCurrentEquippedItem()));
				}
				world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.orb", 0.25F, 1.0F);
			}
			return true;
		}
		return super.onBlockActivated(world, x, y, z, player, bSide, hitX, hitY, hitZ);
	}

	@Override
	public boolean shouldCheckWeakPower(IBlockAccess world, int x, int y, int z, int side) {

		return true;
	}

	@Override
	public int getRenderType() {

		return TEProps.renderIdLight;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getMixedBrightnessForBlock(IBlockAccess world, int x, int y, int z) {

		TileLight tile = (TileLight) world.getTileEntity(x, y, z);
		return world.getLightBrightnessForSkyBlocks(x, y, z, tile.getInternalLight());
	}

	@Override
	public int getRenderBlockPass() {

		return 1;
	}

	@Override
	public boolean canRenderInPass(int pass) {

		renderPass = pass;
		return pass < 2;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		IconRegistry.addIcon("Light0", "thermalexpansion:light/Illuminator_Frame", ir);
		IconRegistry.addIcon("Light1", "thermalexpansion:light/Lamp_Effect", ir);
		IconRegistry.addIcon("LightEffect", "thermalexpansion:light/Illuminator_Effect", ir);
		IconRegistry.addIcon("LightHalo", "thermalexpansion:light/Lamp_Halo", ir);
	}

	@Override
	public NBTTagCompound getItemStackTag(World world, int x, int y, int z) {

		NBTTagCompound tag = super.getItemStackTag(world, x, y, z);
		TileLight tile = (TileLight) world.getTileEntity(x, y, z);

		if (tag == null) {
			tag = new NBTTagCompound();
		}
		if (tile.modified) {
			tag.setInteger("Color", tile.color);
		}
		if (tile.dim) {
			tag.setBoolean("Dim", tile.dim);
		}
		tag.setByte("Mode", tile.mode);
		return tag.hasNoTags() ? null : tag;
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		TileLight.initialize();
		TileLightFalse.initialize();

		illuminator = new ItemStack(this, 1, 0);
		lampLumiumRadiant = new ItemStack(this, 1, 1);
		lampLumium = new ItemStack(this, 1, 2);

		GameRegistry.registerCustomItemStack("illuminator", illuminator);
		GameRegistry.registerCustomItemStack("lampLumiumRadiant", lampLumiumRadiant);
		GameRegistry.registerCustomItemStack("lampLumium", lampLumium);

		return true;
	}

	@Override
	public boolean postInit() {

		if (enable[Types.ILLUMINATOR.ordinal()]) {
			TransposerManager.addTEFillRecipe(2000, BlockFrame.frameIlluminator, illuminator, new FluidStack(TFFluids.fluidGlowstone, 500), false);
		}
		if (enable[Types.LAMP_LUMIUM_RADIANT.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemHelper.cloneStack(lampLumiumRadiant, 4), new Object[] { " L ", "GLG", " S ", 'L', "ingotLumium",
				'G', "blockGlassHardened", 'S', "ingotSignalum" }));
		}
		if (enable[Types.LAMP_LUMIUM.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(ItemHelper.cloneStack(lampLumium, 4), new Object[] { " L ", "GLG", " S ", 'L', "dustLumium", 'G',
				"blockGlassHardened", 'S', "ingotSignalum" }));
		}
		return true;
	}

	public static enum Types {
		ILLUMINATOR, LAMP_LUMIUM_RADIANT, LAMP_LUMIUM;

		public static Types getType(int meta) {

			Types[] types = values();
			return types[meta % types.length];
		}
	}

	public static final String[] NAMES = { "illuminator", "lampLumiumRadiant", "lampLumium" };
	public static boolean[] enable = new boolean[Types.values().length];

	static {
		String category = "Light.";

		for (int i = 0; i < Types.values().length; i++) {
			enable[i] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[i]), "Recipe.Enable", true);
		}
	}

	public static ItemStack illuminator;
	public static ItemStack lampLumiumRadiant;
	public static ItemStack lampLumium;

}
