package cofh.thermalexpansion.block.light;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;

import cofh.api.block.IBlockConfigGui;
import cofh.core.render.IconRegistry;
import cofh.lib.util.helpers.ColorHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.repack.codechicken.lib.vec.Cuboid6;
import cofh.repack.codechicken.lib.vec.Rotation;
import cofh.repack.codechicken.lib.vec.SwapYZ;
import cofh.repack.codechicken.lib.vec.Transformation;
import cofh.repack.codechicken.lib.vec.Vector3;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.block.simple.BlockFrame;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.render.transformation.TorchTransformation;
import cofh.thermalexpansion.util.crafting.RecipeStyle;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

public class BlockLight extends BlockTEBase implements IBlockConfigGui {

	public static Cuboid6[] models;

	static {

		double d1 = 0;
		models = new Cuboid6[6];
		{ // full block
			models[0] = new Cuboid6(d1, d1, d1, 1 - d1, 1 - d1, 1 - d1);
		}
		{ // flat lamp
			double d4 = 1. / 16, d5 = 15. / 16, d6 = 2. / 16;
			models[1] = new Cuboid6(d4 + d1, d1, d4 + d1, d5 - d1, d6 - d1, d5 - d1);
		}
		{ // button lamp
			double d4 = 0.5 - 2. / 16, d5 = 0.5 + 2. / 16, d6 = 2. / 16;
			models[2] = new Cuboid6(d4 + d1, d1, d4 + d1, d5 - d1, d6 - d1, d5 - d1);
		}
		{ // tall lamp
			double d4 = 0.5 - 3. / 16, d5 = 0.5 + 3. / 16, d6 = 7. / 16;
			models[3] = new Cuboid6(d4 + d1, d1, d4 + d1, d5 - d1, d6 - d1, d5 - d1);
		}
		{ // wide lamp
			double d4 = 0.5 - 2. / 16, d5 = 0.5 + 2. / 16, d6 = 2. / 16;
			models[4] = new Cuboid6(d4 + d1, d1, d1, d5 - d1, d6 - d1, 1 - d1);
		}
		{ // torch lamp
			double d4 = 0.5 - 1. / 16, d5 = 0.5 + 1. / 16, d6 = 10. / 16;
			models[5] = new Cuboid6(d4 + d1, d1, d4 + d1, d5 - d1, d6 - d1, d5 - d1);
		}
	}

	public static Transformation getTransformation(int style, int alignment) {

		Transformation ret = TorchTransformation.sideTransformations[0];
		switch (style) {
		case 1:
		case 2:
		case 3:
			ret = Rotation.sideRotations[alignment].at(Vector3.center);
			break;
		case 4:
			ret = Rotation.quarterRotations[alignment >> 3].with(Rotation.sideRotations[alignment & 7]).at(Vector3.center);
			break;
		case 5:
			ret = TorchTransformation.sideTransformations[alignment];
			break;
		case 0:
		default:
			break;
		}

		return ret;
	}

	public BlockLight() {

		super(Material.redstoneLight);
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
			for (byte j = 0; j < models.length; ++j) {
				list.add(ItemBlockLight.setDefaultTag(new ItemStack(item, 1, i), j));
			}
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(World world, int x, int y, int z) {

		TileLight tile = (TileLight) world.getTileEntity(x, y, z);
		switch (tile.style) {
		case 2:
		case 5:
			return null;
		}
		Cuboid6 ret = models[tile.style].copy().apply(getTransformation(tile.style, tile.alignment));
		return ret.add(new Vector3(x, y, z)).toAABB();
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {

		TileLight tile = (TileLight) world.getTileEntity(x, y, z);
		Cuboid6 ret = models[tile.style].copy().apply(getTransformation(tile.style, tile.alignment));
		switch (tile.style) {
		case 5:
			double m = 0;
			switch (tile.alignment) {
			case 3:
				m = 0.12;
			case 2:
				ret.expand(new Vector3(0.1, 0, m));
				break;
			case 5:
				m = 0.12;
			case 4:
				ret.expand(new Vector3(m, 0, 0.1));
				break;
			default:
				ret.expand(0.05);
			}
		}
		ret.apply(new SwapYZ() {

			@Override
			public void apply(Vector3 vec) {
				vec.x = MathHelper.clamp(vec.x, 0, 1);
				vec.y = MathHelper.clamp(vec.y, 0, 1);
				vec.z = MathHelper.clamp(vec.z, 0, 1);
			}

		}).setBlockBounds(this);
	}

	@Override
	public boolean canReplace(World world, int x, int y, int z, int side, ItemStack stack) {

		if (super.canReplace(world, x, y, z, side, stack)) {
			if (stack.stackTagCompound != null) {
				@SuppressWarnings("unused")
				int style = stack.stackTagCompound.getByte("Style");
			}
			return true;
		}
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {

		if (stack.stackTagCompound != null) {
			TileLight tile = (TileLight) world.getTileEntity(x, y, z);

			if (stack.stackTagCompound.hasKey("Color")) {
				tile.modified = true;
				tile.setColor(stack.stackTagCompound.getInteger("Color"));
			}
			tile.dim = stack.stackTagCompound.getBoolean("Dim");
			tile.mode = stack.stackTagCompound.getByte("Mode");
			tile.style = stack.stackTagCompound.getByte("Style");
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
	public boolean shouldCheckWeakPower(IBlockAccess world, int x, int y, int z, int side) {

		return true;
	}

	@Override
	public boolean isNormalCube() {

		return false;
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
		if (tile.mode != 0) {
			tag.setByte("Mode", tile.mode);
		}
		tag.setByte("Style", tile.style);
		return tag.hasNoTags() ? null : tag;
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		TileLight.initialize();
		TileLightFalse.initialize();

		illuminator = ItemBlockLight.setDefaultTag(new ItemStack(this, 1, 0), 0);
		lampLumiumRadiant = ItemBlockLight.setDefaultTag(new ItemStack(this, 1, 1), 0);
		lampLumium = ItemBlockLight.setDefaultTag(new ItemStack(this, 1, 2), 0);

		GameRegistry.registerCustomItemStack("illuminator", illuminator);
		GameRegistry.registerCustomItemStack("lampLumiumRadiant", lampLumiumRadiant);
		GameRegistry.registerCustomItemStack("lampLumium", lampLumium);

		return true;
	}

	@Override
	public boolean postInit() {

		if (enable[Types.ILLUMINATOR.ordinal()]) {
			TransposerManager.addTEFillRecipe(2000, BlockFrame.frameIlluminator, illuminator, new FluidStack(TFFluids.fluidGlowstone, 500), false);
			addRecipes(illuminator);
		}
		if (enable[Types.LAMP_LUMIUM_RADIANT.ordinal()]) {
			GameRegistry.addRecipe(ShapedRecipe(ItemHelper.cloneStack(lampLumiumRadiant, 4), new Object[] { " L ", "GLG", " S ", 'L', "ingotLumium", 'G',
					"blockGlassHardened", 'S', "ingotSignalum" }));
			addRecipes(lampLumiumRadiant);
		}
		if (enable[Types.LAMP_LUMIUM.ordinal()]) {
			GameRegistry.addRecipe(ShapedRecipe(ItemHelper.cloneStack(lampLumium, 4), new Object[] { " L ", "GLG", " S ", 'L', "dustLumium", 'G',
					"blockGlassHardened", 'S', "ingotSignalum" }));
			addRecipes(lampLumium);
		}
		return true;
	}

	private static void addRecipes(ItemStack lamp) {

		GameRegistry.addRecipe(new RecipeStyle(2, 1, lamp, 0, ItemBlockLight.setDefaultTag(ItemHelper.cloneStack(lamp, 2), 1)));
		GameRegistry.addRecipe(new RecipeStyle(1, 1, lamp, 0, ItemBlockLight.setDefaultTag(ItemHelper.cloneStack(lamp, 2), 2)));
		GameRegistry.addRecipe(new RecipeStyle(1, 2, lamp, 2, ItemBlockLight.setDefaultTag(ItemHelper.cloneStack(lamp, 2), 3)));
		GameRegistry.addRecipe(new RecipeStyle(3, 1, lamp, 0, ItemBlockLight.setDefaultTag(ItemHelper.cloneStack(lamp, 6), 4)));
		GameRegistry.addRecipe(new RecipeStyle(1, 2, lamp, 0, ItemBlockLight.setDefaultTag(ItemHelper.cloneStack(lamp, 4), 5)));
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
