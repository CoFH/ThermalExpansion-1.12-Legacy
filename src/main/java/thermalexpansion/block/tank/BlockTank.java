package thermalexpansion.block.tank;

import cofh.render.IconRegistry;
import cofh.util.CoreUtils;
import cofh.util.FluidHelper;
import cofh.util.ItemHelper;
import cofh.util.RecipeUpgrade;
import cofh.util.StringHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.BlockTEBase;
import thermalexpansion.core.TEProps;

public class BlockTank extends BlockTEBase {

	public BlockTank() {

		super(Material.glass);
		setBlockBounds(0.125F, 0F, 0.125F, 0.875F, 1F, 0.875F);
		setHardness(4.0F);
		setResistance(120.0F);
		setStepSound(soundTypeGlass);
		setBlockName("thermalexpansion.tank");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		if (metadata >= Types.values().length) {
			return null;
		}
		if (metadata == Types.CREATIVE.ordinal()) {
			if (!enable[Types.CREATIVE.ordinal()]) {
				return null;
			}
			return new TileTankCreative(metadata);
		}
		return new TileTank(metadata);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {

		for (int i = 0; i < Types.values().length; i++) {
			if (enable[i]) {
				list.add(new ItemStack(item, 1, i));
			}
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {

		if (!enable[world.getBlockMetadata(x, y, z)]) {
			world.setBlockToAir(x, y, z);
			return;
		}
		if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("Fluid")) {
			FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.stackTagCompound.getCompoundTag("Fluid"));

			if (fluid != null) {
				TileTank tile = (TileTank) world.getTileEntity(x, y, z);
				tile.tank.setFluid(fluid);
				tile.calcLastDisplay();
			}
		}
		super.onBlockPlacedBy(world, x, y, z, living, stack);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int hitSide, float hitX, float hitY, float hitZ) {

		if (super.onBlockActivated(world, x, y, z, player, hitSide, hitX, hitY, hitZ)) {
			return true;
		}
		TileTank tile = (TileTank) world.getTileEntity(x, y, z);

		if (FluidHelper.fillHandlerWithContainer(tile.getWorldObj(), tile, player)) {
			return true;
		}
		if (FluidHelper.fillContainerFromHandler(tile.getWorldObj(), tile, player, tile.getTankFluid())) {
			return true;
		}
		if (ItemHelper.isPlayerHoldingFluidContainer(player)) {
			return true;
		}
		return super.onBlockActivated(world, x, y, z, player, hitSide, hitX, hitY, hitZ);
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {

		return HARDNESS[world.getBlockMetadata(x, y, z)];
	}

	@Override
	public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {

		return RESISTANCE[world.getBlockMetadata(x, y, z)];
	}

	@Override
	public int getRenderType() {

		return TEProps.renderIdTank;
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

		if (side == ForgeDirection.UP || side == ForgeDirection.DOWN) {
			return true;
		}
		return false;
	}

	@Override
	public boolean hasComparatorInputOverride() {

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		for (int i = 0; i < Types.values().length; i++) {
			IconRegistry.addIcon("TankBottom" + 2 * i, "thermalexpansion:tank/Tank_" + StringHelper.titleCase(NAMES[i]) + "_Bottom_Blue", ir);
			IconRegistry.addIcon("TankBottom" + (2 * i + 1), "thermalexpansion:tank/Tank_" + StringHelper.titleCase(NAMES[i]) + "_Bottom_Orange", ir);

			IconRegistry.addIcon("TankTop" + 2 * i, "thermalexpansion:tank/Tank_" + StringHelper.titleCase(NAMES[i]) + "_Top_Blue", ir);
			IconRegistry.addIcon("TankTop" + (2 * i + 1), "thermalexpansion:tank/Tank_" + StringHelper.titleCase(NAMES[i]) + "_Top_Orange", ir);

			IconRegistry.addIcon("TankSide" + 2 * i, "thermalexpansion:tank/Tank_" + StringHelper.titleCase(NAMES[i]) + "_Side_Blue", ir);
			IconRegistry.addIcon("TankSide" + (2 * i + 1), "thermalexpansion:tank/Tank_" + StringHelper.titleCase(NAMES[i]) + "_Side_Orange", ir);
		}
	}

	@Override
	public NBTTagCompound getItemStackTag(World world, int x, int y, int z) {

		NBTTagCompound tag = super.getItemStackTag(world, x, y, z);
		TileTank tile = (TileTank) world.getTileEntity(x, y, z);

		if (tile != null) {
			FluidStack fluid = tile.getTankFluid();

			if (fluid != null) {
				tag = new NBTTagCompound();
				tag.setTag("Fluid", fluid.writeToNBT(new NBTTagCompound()));
			}
		}
		return tag;
	}

	/* IDismantleable */
	@Override
	public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z) {

		if (world.getBlockMetadata(x, y, z) == Types.CREATIVE.ordinal() && !CoreUtils.isOp(player)) {
			return false;
		}
		return super.canDismantle(player, world, x, y, z);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		TileTank.initialize();
		TileTankCreative.initialize();

		tankCreative = new ItemStack(this, 1, Types.CREATIVE.ordinal());
		tankBasic = new ItemStack(this, 1, Types.BASIC.ordinal());
		tankHardened = new ItemStack(this, 1, Types.HARDENED.ordinal());
		tankReinforced = new ItemStack(this, 1, Types.REINFORCED.ordinal());
		tankResonant = new ItemStack(this, 1, Types.RESONANT.ordinal());

		GameRegistry.registerCustomItemStack("tankCreative", tankCreative);
		GameRegistry.registerCustomItemStack("tankBasic", tankBasic);
		GameRegistry.registerCustomItemStack("tankHardened", tankHardened);
		GameRegistry.registerCustomItemStack("tankReinforced", tankReinforced);
		GameRegistry.registerCustomItemStack("tankResonant", tankResonant);

		return true;
	}

	@Override
	public boolean postInit() {

		if (enable[Types.BASIC.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(tankBasic, new Object[] { " G ", "GXG", " G ", 'G', "blockGlass", 'X', "ingotCopper" }));
		}
		if (enable[Types.HARDENED.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(tankHardened, new Object[] { " I ", "IXI", " I ", 'I', "ingotInvar", 'X', tankBasic }));
			GameRegistry.addRecipe(new ShapedOreRecipe(tankHardened, new Object[] { "IGI", "GXG", "IGI", 'I', "ingotInvar", 'X', "ingotCopper", 'G',
					"blockGlass" }));
		}
		if (enable[Types.REINFORCED.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(tankReinforced, new Object[] { " G ", "GXG", " G ", 'G', "glassHardened", 'X', tankHardened }));
		}
		if (enable[Types.RESONANT.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(tankResonant, new Object[] { " I ", "IXI", " I ", 'I', "ingotEnderium", 'X', tankReinforced }));
		}
		return true;
	}

	public static enum Types {
		CREATIVE, BASIC, HARDENED, REINFORCED, RESONANT
	}

	public static final String[] NAMES = { "creative", "basic", "hardened", "reinforced", "resonant" };
	public static final float[] HARDNESS = { -1.0F, 1.0F, 3.0F, 4.0F, 4.0F };
	public static final int[] RESISTANCE = { 1200, 15, 90, 120, 120 };
	public static boolean[] enable = new boolean[Types.values().length];

	static {
		String category = "block.feature";
		enable[Types.CREATIVE.ordinal()] = ThermalExpansion.config.get(category, "Tank.Creative", true);
		enable[Types.BASIC.ordinal()] = ThermalExpansion.config.get(category, "Tank.Basic", true);
		enable[Types.HARDENED.ordinal()] = ThermalExpansion.config.get(category, "Tank.Hardened", true);
		enable[Types.REINFORCED.ordinal()] = ThermalExpansion.config.get(category, "Tank.Reinforced", true);
		enable[Types.RESONANT.ordinal()] = ThermalExpansion.config.get(category, "Tank.Resonant", true);
	}

	public static ItemStack tankCreative;
	public static ItemStack tankBasic;
	public static ItemStack tankHardened;
	public static ItemStack tankReinforced;
	public static ItemStack tankResonant;

}
