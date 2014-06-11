package thermalexpansion.block.cell;

import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.render.IconRegistry;
import cofh.util.BlockHelper;
import cofh.util.CoreUtils;
import cofh.util.ItemHelper;
import cofh.util.RecipeUpgrade;
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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.BlockTEBase;
import thermalexpansion.core.TEProps;
import thermalexpansion.item.TEItems;
import thermalexpansion.util.crafting.PulverizerManager;
import thermalexpansion.util.crafting.TransposerManager;
import thermalfoundation.fluid.TFFluids;
import thermalfoundation.item.TFItems;

public class BlockCell extends BlockTEBase {

	public BlockCell() {

		super(Material.iron);
		setHardness(20.0F);
		setResistance(120.0F);
		setBlockName("thermalexpansion.cell");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		if (metadata >= Types.values().length) {
			return null;
		}
		if (metadata == Types.CREATIVE.ordinal()) {
			return new TileCellCreative(metadata);
		}
		return new TileCell(metadata);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {

		for (int i = 0; i < Types.values().length; i++) {
			if (enable[i]) {
				if (i != Types.CREATIVE.ordinal()) {
					list.add(ItemBlockCell.setDefaultTag(new ItemStack(item, 1, i), 0));
				}
				list.add(ItemBlockCell.setDefaultTag(new ItemStack(item, 1, i), TileCell.STORAGE[i]));
			}
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {

		if (stack.stackTagCompound != null) {
			TileCell tile = (TileCell) world.getTileEntity(x, y, z);

			tile.setEnergyStored(stack.stackTagCompound.getInteger("Energy"));
			tile.energySend = stack.stackTagCompound.getInteger("Send");
			tile.energyReceive = stack.stackTagCompound.getInteger("Receive");

			int facing = BlockHelper.determineXZPlaceFacing(living);
			int storedFacing = stack.stackTagCompound.getByte("Facing");

			byte[] sideCache = stack.stackTagCompound.getByteArray("SideCache");

			if (sideCache.length <= 0) {
				sideCache = TileCell.DEFAULT_SIDES.clone();
			}
			tile.sideCache[0] = sideCache[0];
			tile.sideCache[1] = sideCache[1];
			tile.sideCache[facing] = sideCache[storedFacing];
			tile.sideCache[BlockHelper.getLeftSide(facing)] = sideCache[BlockHelper.getLeftSide(storedFacing)];
			tile.sideCache[BlockHelper.getRightSide(facing)] = sideCache[BlockHelper.getRightSide(storedFacing)];
			tile.sideCache[BlockHelper.getOppositeSide(facing)] = sideCache[BlockHelper.getOppositeSide(storedFacing)];

			tile.setControl(ControlMode.values()[stack.stackTagCompound.getByte("rsMode")]);
		}
		super.onBlockPlacedBy(world, x, y, z, living, stack);
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
	public int getRenderBlockPass() {

		return 1;
	}

	@Override
	public int getRenderType() {

		return TEProps.renderIdCell;
	}

	@Override
	public IIcon getIcon(int side, int metadata) {

		return IconRegistry.getIcon("Cell" + 2 * metadata);
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

		for (int i = 0; i < 9; i++) {
			IconRegistry.addIcon("CellMeter" + i, "thermalexpansion:cell/Cell_Meter_" + i, ir);
		}
		IconRegistry.addIcon("CellMeterCreative", "thermalexpansion:cell/Cell_Meter_Creative", ir);
		IconRegistry.addIcon("Cell" + 0, "thermalexpansion:cell/Cell_Creative", ir);
		IconRegistry.addIcon("Cell" + 1, "thermalexpansion:cell/Cell_Creative_Inner", ir);
		IconRegistry.addIcon("Cell" + 2, "thermalexpansion:cell/Cell_Basic", ir);
		IconRegistry.addIcon("Cell" + 3, "thermalexpansion:cell/Cell_Basic_Inner", ir);
		IconRegistry.addIcon("Cell" + 4, "thermalexpansion:cell/Cell_Hardened", ir);
		IconRegistry.addIcon("Cell" + 5, "thermalexpansion:cell/Cell_Hardened_Inner", ir);
		IconRegistry.addIcon("Cell" + 6, "thermalexpansion:cell/Cell_Reinforced", ir);
		IconRegistry.addIcon("Cell" + 7, "thermalexpansion:cell/Cell_Reinforced_Inner", ir);
		IconRegistry.addIcon("Cell" + 8, "thermalexpansion:cell/Cell_Resonant", ir);
		IconRegistry.addIcon("Cell" + 9, "thermalexpansion:cell/Cell_Resonant_Inner", ir);

		IconRegistry.addIcon(TEXTURE_DEFAULT + 0, "thermalexpansion:config/Config_None", ir);
		IconRegistry.addIcon(TEXTURE_DEFAULT + 1, "thermalexpansion:cell/Cell_Config_Orange", ir);
		IconRegistry.addIcon(TEXTURE_DEFAULT + 2, "thermalexpansion:cell/Cell_Config_Blue", ir);

		IconRegistry.addIcon(TEXTURE_CB + 0, "thermalexpansion:config/Config_None", ir);
		IconRegistry.addIcon(TEXTURE_CB + 1, "thermalexpansion:cell/Cell_Config_Orange_CB", ir);
		IconRegistry.addIcon(TEXTURE_CB + 2, "thermalexpansion:cell/Cell_Config_Blue_CB", ir);

		IconRegistry.addIcon("StorageRedstone", "thermalexpansion:cell/Cell_Center_Solid", ir);
	}

	@Override
	public NBTTagCompound getItemStackTag(World world, int x, int y, int z) {

		TileCell tile = (TileCell) world.getTileEntity(x, y, z);
		NBTTagCompound tag = null;

		if (tile != null) {
			tag = new NBTTagCompound();

			tag.setByteArray("SideCache", tile.sideCache);
			tag.setByte("Facing", (byte) tile.getFacing());

			tag.setInteger("Energy", tile.getEnergy());
			tag.setInteger("Send", tile.energySend);
			tag.setInteger("Receive", tile.energyReceive);

			tag.setByte("rsMode", (byte) tile.getControl().ordinal());
		}
		return tag;
	}

	@Override
	public boolean hasComparatorInputOverride() {

		return true;
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

		cellBasicFrame = TEItems.itemComponent.addItem(BASIC_FRAME_ID, "cellBasicFrame");
		cellReinforcedFrameEmpty = TEItems.itemComponent.addItem(REINFORCED_FRAME_EMPTY_ID, "cellReinforcedFrameEmpty", 1);
		cellReinforcedFrameFull = TEItems.itemComponent.addItem(REINFORCED_FRAME_FULL_ID, "cellReinforcedFrameFull", 1);

		TileCell.initialize();
		TileCellCreative.initialize();

		cellCreative = new ItemStack(this, 1, Types.CREATIVE.ordinal());
		cellBasic = new ItemStack(this, 1, Types.BASIC.ordinal());
		cellHardened = new ItemStack(this, 1, Types.HARDENED.ordinal());
		cellReinforced = new ItemStack(this, 1, Types.REINFORCED.ordinal());
		cellResonant = new ItemStack(this, 1, Types.RESONANT.ordinal());

		ItemBlockCell.setDefaultTag(cellCreative, 0);
		ItemBlockCell.setDefaultTag(cellBasic, 0);
		ItemBlockCell.setDefaultTag(cellHardened, 0);
		ItemBlockCell.setDefaultTag(cellReinforced, 0);
		ItemBlockCell.setDefaultTag(cellResonant, 0);

		GameRegistry.registerCustomItemStack("cellCreative", cellCreative);
		GameRegistry.registerCustomItemStack("cellBasic", cellBasic);
		GameRegistry.registerCustomItemStack("cellHardened", cellHardened);
		GameRegistry.registerCustomItemStack("cellReinforced", cellReinforced);
		GameRegistry.registerCustomItemStack("cellResonant", cellResonant);

		return true;
	}

	@Override
	public boolean postInit() {

		if (enable[Types.BASIC.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(cellBasic, new Object[] { " I ", "IXI", " P ", 'I', "ingotCopper", 'X', cellBasicFrame, 'P',
					TEItems.powerCoilElectrum }));
			PulverizerManager.addRecipe(4000, cellBasic, ItemHelper.cloneStack(Items.redstone, 8), ItemHelper.cloneStack(TFItems.ingotLead, 3));
		}
		if (enable[Types.HARDENED.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(cellHardened, new Object[] { " I ", "IXI", " I ", 'I', "ingotInvar", 'X', cellBasic }));
			GameRegistry.addRecipe(new ShapedOreRecipe(cellHardened, new Object[] { "IYI", "YXY", "IPI", 'I', "ingotInvar", 'X', cellBasicFrame, 'Y',
					"ingotCopper", 'P', TEItems.powerCoilElectrum }));
			PulverizerManager.addRecipe(4000, cellHardened, ItemHelper.cloneStack(Items.redstone, 8), ItemHelper.cloneStack(TFItems.ingotInvar, 3));
		}
		if (enable[Types.REINFORCED.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(cellReinforced, new Object[] { " X ", "YCY", "IPI", 'C', cellReinforcedFrameFull, 'I', "ingotLead", 'P',
					TEItems.powerCoilElectrum, 'X', "ingotElectrum", 'Y', "ingotElectrum" }));
		}
		if (enable[Types.RESONANT.ordinal()]) {
			GameRegistry.addRecipe(new RecipeUpgrade(cellResonant, new Object[] { " I ", "IXI", " I ", 'I', "ingotEnderium", 'X', cellReinforced }));
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(cellBasicFrame, new Object[] { "IGI", "GXG", "IGI", 'I', "ingotLead", 'G', "blockGlass", 'X',
				Blocks.redstone_block }));
		PulverizerManager.addRecipe(4000, cellBasicFrame, ItemHelper.cloneStack(Items.redstone, 8), ItemHelper.cloneStack(TFItems.ingotLead, 3));

		GameRegistry.addRecipe(new ShapedOreRecipe(cellReinforcedFrameEmpty, new Object[] { "IGI", "GXG", "IGI", 'I', "ingotElectrum", 'G', "glassHardened",
				'X', Items.diamond }));
		TransposerManager.addTEFillRecipe(16000, cellReinforcedFrameEmpty, cellReinforcedFrameFull, new FluidStack(TFFluids.fluidRedstone, 4000), false);
		return true;
	}

	public static enum Types {
		CREATIVE, BASIC, HARDENED, REINFORCED, RESONANT
	}

	public static final String[] NAMES = { "creative", "basic", "hardened", "reinforced", "resonant" };
	public static final float[] HARDNESS = { -1.0F, 5.0F, 15.0F, 20.0F, 20.0F };
	public static final int[] RESISTANCE = { 1200, 15, 90, 120, 120 };
	public static boolean[] enable = new boolean[Types.values().length];

	static {
		String category = "block.feature";
		enable[Types.CREATIVE.ordinal()] = ThermalExpansion.config.get(category, "Cell.Creative", true);
		enable[Types.BASIC.ordinal()] = ThermalExpansion.config.get(category, "Cell.Basic", true);
		enable[Types.HARDENED.ordinal()] = ThermalExpansion.config.get(category, "Cell.Hardened", true);
		enable[Types.REINFORCED.ordinal()] = ThermalExpansion.config.get(category, "Cell.Reinforced", true);
		enable[Types.RESONANT.ordinal()] = ThermalExpansion.config.get(category, "Cell.Resonant", true);
	}

	public static final String TEXTURE_DEFAULT = "CellConfig_";
	public static final String TEXTURE_CB = "CellConfig_CB_";

	public static String textureSelection = TEXTURE_DEFAULT;

	public static ItemStack cellCreative;
	public static ItemStack cellBasic;
	public static ItemStack cellHardened;
	public static ItemStack cellReinforced;
	public static ItemStack cellResonant;

	public static ItemStack cellBasicFrame;
	public static ItemStack cellReinforcedFrameEmpty;
	public static ItemStack cellReinforcedFrameFull;

	public static final int BASIC_FRAME_ID = 64;
	public static final int REINFORCED_FRAME_EMPTY_ID = 72;
	public static final int REINFORCED_FRAME_FULL_ID = 73;
}
