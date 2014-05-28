package thermalexpansion.block.machine;

import cofh.api.tileentity.ISidedBlockTexture;
import cofh.render.IconRegistry;
import cofh.util.FluidHelper;
import cofh.util.ItemHelper;
import cofh.util.StringHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.ShapedOreRecipe;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.BlockTEBase;
import thermalexpansion.block.cell.BlockCell;
import thermalexpansion.core.TEProps;
import thermalexpansion.item.TEItems;

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
		case WATER_GEN:
			return new TileWaterGen();
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
	public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {

		return false;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {

		return true;
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {

		ISidedBlockTexture tile = (ISidedBlockTexture) world.getTileEntity(x, y, z);
		return tile == null ? null : tile.getBlockTexture(side, renderPass);
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
	public boolean canRenderInPass(int pass) {

		renderPass = pass;
		return pass < 2;
	}

	@Override
	public int getRenderBlockPass() {

		return 1;
	}

	@Override
	public boolean isOpaqueCube() {

		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {

		return true;
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

	/* IInitializer */
	@Override
	public boolean initialize() {

		machineFrame = TEItems.itemComponent.addItem(0, "machineFrame");

		TileFurnace.initialize();
		TilePulverizer.initialize();
		TileSawmill.initialize();
		TileSmelter.initialize();
		TileCrucible.initialize();
		TileTransposer.initialize();
		TilePrecipitator.initialize();
		TileExtruder.initialize();
		TileWaterGen.initialize();
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
		waterGen = new ItemStack(this, 1, Types.WATER_GEN.ordinal());
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
		GameRegistry.registerCustomItemStack("waterGen", waterGen);
		GameRegistry.registerCustomItemStack("assembler", assembler);
		GameRegistry.registerCustomItemStack("charger", charger);

		return true;
	}

	@Override
	public boolean postInit() {

		String category = "tweak.recipe";
		boolean machineFrameRequireSteel = ThermalExpansion.config.get(category, "MachineFrame.RequireSteel", false);
		boolean machineFrameAllowSteel = ThermalExpansion.config.get(category, "MachineFrame.AllowSteel", true);
		boolean machinesUseGears = ThermalExpansion.config.get(category, "UseGears", false);
		boolean[] expensiveRecipe = new boolean[Types.values().length];

		expensiveRecipe[Types.PULVERIZER.ordinal()] = ThermalExpansion.config.get(category, "Pulverizer.AddDiamonds", false);
		expensiveRecipe[Types.SMELTER.ordinal()] = ThermalExpansion.config.get(category, "Smelter.AddDiamonds", false);

		String copperPart = "ingotCopper";
		String tinPart = "ingotTin";

		if (machinesUseGears) {
			copperPart = "gearCopper";
			tinPart = "gearTin";
		}
		if (ItemHelper.oreNameExists("ingotSteel")) {
			if (machineFrameRequireSteel) {
				GameRegistry.addRecipe(new ShapedOreRecipe(machineFrame, new Object[] { "IGI", "GXG", "IGI", 'I', "ingotSteel", 'G', "blockGlass", 'X',
						"ingotGold" }));
			} else if (machineFrameAllowSteel) {
				GameRegistry.addRecipe(new ShapedOreRecipe(machineFrame, new Object[] { "IGI", "GXG", "IGI", 'I', "ingotSteel", 'G', "blockGlass", 'X',
						"ingotGold" }));
				GameRegistry.addRecipe(new ShapedOreRecipe(machineFrame, new Object[] { "IGI", "GXG", "IGI", 'I', "ingotIron", 'G', "blockGlass", 'X',
						"ingotGold" }));
			} else {
				GameRegistry.addRecipe(new ShapedOreRecipe(machineFrame, new Object[] { "IGI", "GXG", "IGI", 'I', "ingotIron", 'G', "blockGlass", 'X',
						"ingotGold" }));
			}
		} else {
			GameRegistry.addRecipe(new ShapedOreRecipe(machineFrame,
					new Object[] { "IGI", "GXG", "IGI", 'I', "ingotIron", 'G', "blockGlass", 'X', "ingotGold" }));
		}
		if (enable[Types.FURNACE.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(furnace, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
					TEItems.powerCoilGold, 'X', Items.redstone, 'Y', Blocks.brick_block }));
		}
		if (enable[Types.PULVERIZER.ordinal()]) {
			if (expensiveRecipe[Types.PULVERIZER.ordinal()]) {
				GameRegistry.addRecipe(new ShapedOreRecipe(pulverizer, new Object[] { "DXD", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
						TEItems.powerCoilGold, 'X', Blocks.piston, 'Y', Items.flint, 'D', Items.diamond }));
			} else {
				GameRegistry.addRecipe(new ShapedOreRecipe(pulverizer, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
						TEItems.powerCoilGold, 'X', Blocks.piston, 'Y', Items.flint }));
			}
		}
		if (enable[Types.SAWMILL.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(sawmill, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
					TEItems.powerCoilGold, 'X', Items.iron_axe, 'Y', "plankWood" }));
		}
		if (enable[Types.SMELTER.ordinal()]) {
			if (expensiveRecipe[Types.SMELTER.ordinal()]) {
				GameRegistry.addRecipe(new ShapedOreRecipe(smelter, new Object[] { "DXD", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
						TEItems.powerCoilGold, 'X', Items.bucket, 'Y', "ingotInvar", 'D', Items.diamond }));
			} else {
				GameRegistry.addRecipe(new ShapedOreRecipe(smelter, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
						TEItems.powerCoilGold, 'X', Items.bucket, 'Y', "ingotInvar" }));
			}
		}
		if (enable[Types.CRUCIBLE.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(crucible, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
					TEItems.powerCoilGold, 'X', BlockCell.cellBasicFrame, 'Y', Blocks.nether_brick }));
		}
		if (enable[Types.TRANSPOSER.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(transposer, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
					TEItems.powerCoilGold, 'X', Items.bucket, 'Y', "blockGlass" }));
		}
		if (enable[Types.PRECIPITATOR.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(precipitator, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
					TEItems.powerCoilGold, 'X', Blocks.piston, 'Y', Blocks.snow }));
		}
		if (enable[Types.EXTRUDER.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(extruder, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', tinPart, 'P',
					TEItems.pneumaticServo, 'X', Blocks.piston, 'Y', "blockGlass" }));
		}
		if (enable[Types.WATER_GEN.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(waterGen, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', tinPart, 'P',
					TEItems.pneumaticServo, 'X', Items.bucket, 'Y', "blockGlass" }));
		}
		if (enable[Types.ASSEMBLER.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(assembler, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
					TEItems.powerCoilGold, 'X', Blocks.chest, 'Y', "gearTin" }));
		}
		if (enable[Types.CHARGER.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(charger, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
					TEItems.powerCoilGold, 'X', BlockCell.cellBasicFrame, 'Y', TEItems.powerCoilSilver }));
		}
		return true;
	}

	public static enum Types {
		FURNACE, PULVERIZER, SAWMILL, SMELTER, CRUCIBLE, TRANSPOSER, PRECIPITATOR, EXTRUDER, WATER_GEN, ASSEMBLER, CHARGER
	}

	public static final String[] NAMES = { "furnace", "pulverizer", "sawmill", "smelter", "crucible", "transposer", "precipitator", "extruder", "waterGen",
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
		enable[Types.WATER_GEN.ordinal()] = ThermalExpansion.config.get(category, "Machine.WaterGen", true);
		enable[Types.ASSEMBLER.ordinal()] = ThermalExpansion.config.get(category, "Machine.AutoCrafter", true);
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
	public static ItemStack waterGen;
	public static ItemStack assembler;
	public static ItemStack charger;

	public static ItemStack machineFrame;
}
