package thermalexpansion.block.machine;

import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.api.tileentity.ISidedTexture;
import cofh.render.IconRegistry;
import cofh.util.BlockHelper;
import cofh.util.FluidHelper;
import cofh.util.StringHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
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
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.BlockTEBase;
import thermalexpansion.core.TEProps;

public class BlockMachine extends BlockTEBase {

	public BlockMachine() {

		super(Material.iron);
		setHardness(15.0F);
		setResistance(25.0F);
		setBlockName("thermalexpansion.machine");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		if (metadata >= Types.values().length) {
			return null;
		}
		switch (Types.values()[metadata]) {
		case FURNACE:
			return new TileFurnace();
		case PULVERIZER:
			return new TilePulverizer();
		case SAWMILL:
			return new TileSawmill();
		case SMELTER:
			return new TileSmelter();
		case CRUCIBLE:
			return new TileCrucible();
		case TRANSPOSER:
			return new TileTransposer();
		case PRECIPITATOR:
			return new TilePrecipitator();
		case EXTRUDER:
			return new TileExtruder();
		case ACCUMULATOR:
			return new TileAccumulator();
		case ASSEMBLER:
			return new TileAssembler();
		case CHARGER:
			return new TileCharger();
		default:
			return null;
		}
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

		if (stack.stackTagCompound != null) {
			TileMachineBase tile = (TileMachineBase) world.getTileEntity(x, y, z);

			tile.readAugmentsFromNBT(stack.stackTagCompound);
			tile.setEnergyStored(stack.stackTagCompound.getInteger("Energy"));

			int facing = BlockHelper.determineXZPlaceFacing(living);
			int storedFacing = stack.stackTagCompound.getByte("Facing");
			byte[] sideCache = stack.stackTagCompound.getByteArray("SideCache");

			if (sideCache.length <= 0) {
				sideCache = new byte[] { 0, 0, 0, 0, 0, 0 };
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
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int hitSide, float hitX, float hitY, float hitZ) {

		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile instanceof TileExtruder || tile instanceof TilePrecipitator) {
			if (FluidHelper.fillHandlerWithContainer(world, (IFluidHandler) tile, player)) {
				return true;
			}
		}
		return super.onBlockActivated(world, x, y, z, player, hitSide, hitX, hitY, hitZ);
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
	public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {

		return false;
	}

	@Override
	public boolean isOpaqueCube() {

		return true;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {

		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {

		return true;
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {

		ISidedTexture tile = (ISidedTexture) world.getTileEntity(x, y, z);
		return tile == null ? null : tile.getTexture(side, renderPass);
	}

	@Override
	public IIcon getIcon(int side, int metadata) {

		if (side == 0) {
			return IconRegistry.getIcon("MachineBottom");
		}
		if (side == 1) {
			return IconRegistry.getIcon("MachineTop");
		}
		return side != 3 ? IconRegistry.getIcon("MachineSide") : IconRegistry.getIcon("MachineFace" + metadata);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		// Base Textures
		IconRegistry.addIcon("MachineBottom", "thermalexpansion:machine/Machine_Bottom", ir);
		IconRegistry.addIcon("MachineTop", "thermalexpansion:machine/Machine_Top", ir);
		IconRegistry.addIcon("MachineSide", "thermalexpansion:machine/Machine_Side", ir);

		// Face Textures
		for (int i = 0; i < Types.values().length; i++) {
			IconRegistry.addIcon("MachineFace" + i, "thermalexpansion:machine/Machine_Face_" + StringHelper.titleCase(NAMES[i]), ir);
			IconRegistry.addIcon("MachineActive" + i, "thermalexpansion:machine/Machine_Active_" + StringHelper.titleCase(NAMES[i]), ir);
		}

		// Config Textures
		IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 0, "thermalexpansion:config/Config_None", ir);
		IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 1, "thermalexpansion:config/Config_Blue", ir);
		IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 2, "thermalexpansion:config/Config_Red", ir);
		IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 3, "thermalexpansion:config/Config_Yellow", ir);
		IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 4, "thermalexpansion:config/Config_Orange", ir);
		IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 5, "thermalexpansion:config/Config_Green", ir);
		IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 6, "thermalexpansion:config/Config_Purple", ir);

		IconRegistry.addIcon(TEProps.TEXTURE_CB + 0, "thermalexpansion:config/Config_None", ir);
		IconRegistry.addIcon(TEProps.TEXTURE_CB + 1, "thermalexpansion:config/Config_Blue_CB", ir);
		IconRegistry.addIcon(TEProps.TEXTURE_CB + 2, "thermalexpansion:config/Config_Red_CB", ir);
		IconRegistry.addIcon(TEProps.TEXTURE_CB + 3, "thermalexpansion:config/Config_Yellow_CB", ir);
		IconRegistry.addIcon(TEProps.TEXTURE_CB + 4, "thermalexpansion:config/Config_Orange_CB", ir);
		IconRegistry.addIcon(TEProps.TEXTURE_CB + 5, "thermalexpansion:config/Config_Green_CB", ir);
		IconRegistry.addIcon(TEProps.TEXTURE_CB + 6, "thermalexpansion:config/Config_Purple_CB", ir);
	}

	@Override
	public NBTTagCompound getItemStackTag(World world, int x, int y, int z) {

		NBTTagCompound tag = super.getItemStackTag(world, x, y, z);
		TileMachineBase tile = (TileMachineBase) world.getTileEntity(x, y, z);

		if (tile != null) {
			if (tag == null) {
				tag = new NBTTagCompound();
			}
			tag.setByteArray("SideCache", tile.sideCache);
			tag.setByte("Facing", (byte) tile.getFacing());
			tag.setByte("rsMode", (byte) tile.getControl().ordinal());
			tag.setInteger("Energy", tile.getEnergyStored(ForgeDirection.UNKNOWN));

			tile.writeAugmentsToNBT(tag);
		}
		return tag;
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		TileFurnace.initialize();
		TilePulverizer.initialize();
		TileSawmill.initialize();
		TileSmelter.initialize();
		TileCrucible.initialize();
		TileTransposer.initialize();
		TilePrecipitator.initialize();
		TileExtruder.initialize();
		TileAccumulator.initialize();
		TileAssembler.initialize();
		TileCharger.initialize();

		furnace = new ItemStack(this, 1, Types.FURNACE.ordinal());
		pulverizer = new ItemStack(this, 1, Types.PULVERIZER.ordinal());
		sawmill = new ItemStack(this, 1, Types.SAWMILL.ordinal());
		smelter = new ItemStack(this, 1, Types.SMELTER.ordinal());
		crucible = new ItemStack(this, 1, Types.CRUCIBLE.ordinal());
		transposer = new ItemStack(this, 1, Types.TRANSPOSER.ordinal());
		precipitator = new ItemStack(this, 1, Types.PRECIPITATOR.ordinal());
		extruder = new ItemStack(this, 1, Types.EXTRUDER.ordinal());
		accumulator = new ItemStack(this, 1, Types.ACCUMULATOR.ordinal());
		assembler = new ItemStack(this, 1, Types.ASSEMBLER.ordinal());
		charger = new ItemStack(this, 1, Types.CHARGER.ordinal());

		GameRegistry.registerCustomItemStack("furnace", furnace);
		GameRegistry.registerCustomItemStack("pulverizer", pulverizer);
		GameRegistry.registerCustomItemStack("sawmill", sawmill);
		GameRegistry.registerCustomItemStack("smelter", smelter);
		GameRegistry.registerCustomItemStack("crucible", crucible);
		GameRegistry.registerCustomItemStack("transposer", transposer);
		GameRegistry.registerCustomItemStack("precipitator", precipitator);
		GameRegistry.registerCustomItemStack("extruder", extruder);
		GameRegistry.registerCustomItemStack("accumulator", accumulator);
		GameRegistry.registerCustomItemStack("assembler", assembler);
		GameRegistry.registerCustomItemStack("charger", charger);

		return true;
	}

	@Override
	public boolean postInit() {

		ItemStack[] machineFrames = new ItemStack[4];

		String category = "tweak.recipe";
		String comment = "If enabled, Machines use gears instead of ingots in their default recipes.";
		boolean machinesUseGears = ThermalExpansion.config.get(category, "UseGears", false, comment);
		boolean[] expensiveRecipe = new boolean[Types.values().length];

		expensiveRecipe[Types.PULVERIZER.ordinal()] = ThermalExpansion.config.get(category, "Pulverizer.AddDiamonds", false);
		expensiveRecipe[Types.SMELTER.ordinal()] = ThermalExpansion.config.get(category, "Smelter.AddDiamonds", false);

		String copperPart = "thermalexpansion:machineCopper";
		String tinPart = "thermalexpansion:machineTin";

		String prefix = machinesUseGears ? "gear" : "ingot";
		ArrayList<ItemStack> copperPartList = OreDictionary.getOres(prefix + "Copper");
		ArrayList<ItemStack> tinPartList = OreDictionary.getOres(prefix + "Tin");

		for (int i = 0; i < copperPartList.size(); i++) {
			OreDictionary.registerOre(copperPart, copperPartList.get(i));
		}
		for (int i = 0; i < tinPartList.size(); i++) {
			OreDictionary.registerOre(tinPart, tinPartList.get(i));
		}

		// if (enable[Types.FURNACE.ordinal()]) {
		// GameRegistry.addRecipe(new ShapedOreRecipe(furnace, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
		// TEItems.powerCoilGold, 'X', Items.redstone, 'Y', Blocks.brick_block }));
		// }
		// if (enable[Types.PULVERIZER.ordinal()]) {
		// if (expensiveRecipe[Types.PULVERIZER.ordinal()]) {
		// GameRegistry.addRecipe(new ShapedOreRecipe(pulverizer, new Object[] { "DXD", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
		// TEItems.powerCoilGold, 'X', Blocks.piston, 'Y', Items.flint, 'D', Items.diamond }));
		// } else {
		// GameRegistry.addRecipe(new ShapedOreRecipe(pulverizer, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
		// TEItems.powerCoilGold, 'X', Blocks.piston, 'Y', Items.flint }));
		// }
		// }
		// if (enable[Types.SAWMILL.ordinal()]) {
		// GameRegistry.addRecipe(new ShapedOreRecipe(sawmill, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
		// TEItems.powerCoilGold, 'X', Items.iron_axe, 'Y', "plankWood" }));
		// }
		// if (enable[Types.SMELTER.ordinal()]) {
		// if (expensiveRecipe[Types.SMELTER.ordinal()]) {
		// GameRegistry.addRecipe(new ShapedOreRecipe(smelter, new Object[] { "DXD", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
		// TEItems.powerCoilGold, 'X', Items.bucket, 'Y', "ingotInvar", 'D', Items.diamond }));
		// } else {
		// GameRegistry.addRecipe(new ShapedOreRecipe(smelter, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
		// TEItems.powerCoilGold, 'X', Items.bucket, 'Y', "ingotInvar" }));
		// }
		// }
		// if (enable[Types.CRUCIBLE.ordinal()]) {
		// GameRegistry.addRecipe(new ShapedOreRecipe(crucible, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
		// TEItems.powerCoilGold, 'X', BlockFrame.frameCellBasic, 'Y', Blocks.nether_brick }));
		// }
		// if (enable[Types.TRANSPOSER.ordinal()]) {
		// GameRegistry.addRecipe(new ShapedOreRecipe(transposer, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
		// TEItems.powerCoilGold, 'X', Items.bucket, 'Y', "blockGlass" }));
		// }
		// if (enable[Types.PRECIPITATOR.ordinal()]) {
		// GameRegistry.addRecipe(new ShapedOreRecipe(precipitator, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
		// TEItems.powerCoilGold, 'X', Blocks.piston, 'Y', Blocks.snow }));
		// }
		// if (enable[Types.EXTRUDER.ordinal()]) {
		// GameRegistry.addRecipe(new ShapedOreRecipe(extruder, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', tinPart, 'P',
		// TEItems.pneumaticServo, 'X', Blocks.piston, 'Y', "blockGlass" }));
		// }
		// if (enable[Types.WATER_GEN.ordinal()]) {
		// GameRegistry.addRecipe(new ShapedOreRecipe(accumulator, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', tinPart, 'P',
		// TEItems.pneumaticServo, 'X', Items.bucket, 'Y', "blockGlass" }));
		// }
		// if (enable[Types.ASSEMBLER.ordinal()]) {
		// GameRegistry.addRecipe(new ShapedOreRecipe(assembler, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
		// TEItems.powerCoilGold, 'X', Blocks.chest, 'Y', "gearTin" }));
		// }
		// if (enable[Types.CHARGER.ordinal()]) {
		// GameRegistry.addRecipe(new ShapedOreRecipe(charger, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
		// TEItems.powerCoilGold, 'X', BlockFrame.frameCellBasic, 'Y', TEItems.powerCoilSilver }));
		// }
		return true;
	}

	public static enum Types {
		FURNACE, PULVERIZER, SAWMILL, SMELTER, CRUCIBLE, TRANSPOSER, PRECIPITATOR, EXTRUDER, ACCUMULATOR, ASSEMBLER, CHARGER
	}

	public static final String[] NAMES = { "furnace", "pulverizer", "sawmill", "smelter", "crucible", "transposer", "precipitator", "extruder", "accumulator",
			"assembler", "charger" };
	public static boolean[] enable = new boolean[Types.values().length];

	static {
		String category = "block.feature";
		enable[Types.FURNACE.ordinal()] = ThermalExpansion.config.get(category, "Machine.Furnace", true);
		enable[Types.PULVERIZER.ordinal()] = ThermalExpansion.config.get(category, "Machine.Pulverizer", true);
		enable[Types.SAWMILL.ordinal()] = ThermalExpansion.config.get(category, "Machine.Sawmill", true);
		enable[Types.SMELTER.ordinal()] = ThermalExpansion.config.get(category, "Machine.Smelter", true);
		enable[Types.CRUCIBLE.ordinal()] = ThermalExpansion.config.get(category, "Machine.Crucible", true);
		enable[Types.TRANSPOSER.ordinal()] = ThermalExpansion.config.get(category, "Machine.Transposer", true);
		enable[Types.PRECIPITATOR.ordinal()] = ThermalExpansion.config.get(category, "Machine.Precipitator", true);
		enable[Types.EXTRUDER.ordinal()] = ThermalExpansion.config.get(category, "Machine.Extruder", true);
		enable[Types.ACCUMULATOR.ordinal()] = ThermalExpansion.config.get(category, "Machine.Accumulator", true);
		enable[Types.ASSEMBLER.ordinal()] = ThermalExpansion.config.get(category, "Machine.Assembler", true);
		enable[Types.CHARGER.ordinal()] = ThermalExpansion.config.get(category, "Machine.Charger", true);
	}

	public static ItemStack furnace;
	public static ItemStack pulverizer;
	public static ItemStack sawmill;
	public static ItemStack smelter;
	public static ItemStack crucible;
	public static ItemStack transposer;
	public static ItemStack precipitator;
	public static ItemStack extruder;
	public static ItemStack accumulator;
	public static ItemStack assembler;
	public static ItemStack charger;

}
