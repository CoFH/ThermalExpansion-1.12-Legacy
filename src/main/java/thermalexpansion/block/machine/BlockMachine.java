package thermalexpansion.block.machine;

import cofh.api.tileentity.ISidedTexture;
import cofh.render.IconRegistry;
import cofh.util.BlockHelper;
import cofh.util.FluidHelper;
import cofh.util.ItemHelper;
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
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.BlockTEBase;
import thermalexpansion.block.simple.BlockFrame;
import thermalexpansion.core.TEProps;
import thermalexpansion.item.TEAugments;
import thermalexpansion.item.TEItems;
import thermalexpansion.util.ReconfigurableHelper;
import thermalexpansion.util.crafting.RecipeMachine;
import thermalexpansion.util.crafting.TECraftingHandler;

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
				for (int j = 0; j < 4; j++) {
					if (creativeTiers[j]) {
						list.add(ItemBlockMachine.setDefaultTag(new ItemStack(item, 1, i), (byte) j));
					}
				}
			}
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {

		if (stack.stackTagCompound != null) {
			TileMachineBase tile = (TileMachineBase) world.getTileEntity(x, y, z);

			tile.readAugmentsFromNBT(stack.stackTagCompound);
			tile.installAugments();
			tile.setEnergyStored(stack.stackTagCompound.getInteger("Energy"));

			int facing = BlockHelper.determineXZPlaceFacing(living);
			int storedFacing = ReconfigurableHelper.getFacing(stack);
			byte[] sideCache = ReconfigurableHelper.getSideCache(stack, tile.getDefaultSides());

			tile.sideCache[0] = sideCache[0];
			tile.sideCache[1] = sideCache[1];
			tile.sideCache[facing] = 0;
			tile.sideCache[BlockHelper.getLeftSide(facing)] = sideCache[BlockHelper.getLeftSide(storedFacing)];
			tile.sideCache[BlockHelper.getRightSide(facing)] = sideCache[BlockHelper.getRightSide(storedFacing)];
			tile.sideCache[BlockHelper.getOppositeSide(facing)] = sideCache[BlockHelper.getOppositeSide(storedFacing)];
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
			ReconfigurableHelper.setItemStackTagReconfig(tag, tile);
			tag.setInteger("Energy", tile.getEnergyStored(ForgeDirection.UNKNOWN));

			tile.writeAugmentsToNBT(tag);
		}
		return tag;
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		TileMachineBase.configure();
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

		if (defaultAutoTransfer) {
			defaultAugments[0] = ItemHelper.cloneStack(TEAugments.generalAutoTransfer);
		}
		if (defaultRedstoneControl) {
			defaultAugments[1] = ItemHelper.cloneStack(TEAugments.generalRedstoneControl);
		}
		if (defaultReconfigSides) {
			defaultAugments[2] = ItemHelper.cloneStack(TEAugments.generalReconfigSides);
		}
		furnace = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.FURNACE.ordinal()));
		pulverizer = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.PULVERIZER.ordinal()));
		sawmill = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.SAWMILL.ordinal()));
		smelter = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.SMELTER.ordinal()));
		crucible = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.CRUCIBLE.ordinal()));
		transposer = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.TRANSPOSER.ordinal()));
		precipitator = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.PRECIPITATOR.ordinal()));
		extruder = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.EXTRUDER.ordinal()));
		accumulator = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.ACCUMULATOR.ordinal()));
		assembler = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.ASSEMBLER.ordinal()));
		charger = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.CHARGER.ordinal()));

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
		String comment = "If enabled, Machines use ingots instead of gears in their default recipes.";

		String machineFrame = "thermalexpansion:machineFrame";
		String copperPart = "thermalexpansion:machineCopper";

		String prefix = ThermalExpansion.config.get(category, "Machines.UseIngots", false, comment) ? "ingot" : "gear";
		ArrayList<ItemStack> copperPartList = OreDictionary.getOres(prefix + "Copper");

		for (int i = 0; i < copperPartList.size(); i++) {
			OreDictionary.registerOre(copperPart, copperPartList.get(i));
		}
		if (enable[Types.FURNACE.ordinal()]) {
			GameRegistry.addRecipe(new RecipeMachine(furnace, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
					TEItems.powerCoilGold, 'X', "dustRedstone", 'Y', Blocks.brick_block }));
		}
		if (enable[Types.PULVERIZER.ordinal()]) {
			GameRegistry.addRecipe(new RecipeMachine(pulverizer, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
					TEItems.powerCoilGold, 'X', Blocks.piston, 'Y', Items.flint }));
		}
		if (enable[Types.SAWMILL.ordinal()]) {
			GameRegistry.addRecipe(new RecipeMachine(sawmill, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
					TEItems.powerCoilGold, 'X', Items.iron_axe, 'Y', "plankWood" }));
		}
		if (enable[Types.SMELTER.ordinal()]) {
			GameRegistry.addRecipe(new RecipeMachine(smelter, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
					TEItems.powerCoilGold, 'X', Items.bucket, 'Y', "ingotInvar" }));
		}
		if (enable[Types.CRUCIBLE.ordinal()]) {
			GameRegistry.addRecipe(new RecipeMachine(crucible, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
					TEItems.powerCoilGold, 'X', BlockFrame.frameCellBasic, 'Y', Blocks.nether_brick }));
		}
		if (enable[Types.TRANSPOSER.ordinal()]) {
			GameRegistry.addRecipe(new RecipeMachine(transposer, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
					TEItems.powerCoilGold, 'X', Items.bucket, 'Y', "blockGlass" }));
		}
		if (enable[Types.PRECIPITATOR.ordinal()]) {
			GameRegistry.addRecipe(new RecipeMachine(precipitator, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
					TEItems.powerCoilGold, 'X', Blocks.piston, 'Y', Blocks.snow }));
		}
		if (enable[Types.EXTRUDER.ordinal()]) {
			GameRegistry.addRecipe(new RecipeMachine(extruder, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
					TEItems.pneumaticServo, 'X', Blocks.piston, 'Y', "blockGlass" }));
		}
		if (enable[Types.ACCUMULATOR.ordinal()]) {
			GameRegistry.addRecipe(new RecipeMachine(accumulator, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
					TEItems.pneumaticServo, 'X', Items.bucket, 'Y', "blockGlass" }));
		}
		if (enable[Types.ASSEMBLER.ordinal()]) {
			GameRegistry.addRecipe(new RecipeMachine(assembler, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
					TEItems.powerCoilGold, 'X', Blocks.chest, 'Y', "gearTin" }));
		}
		if (enable[Types.CHARGER.ordinal()]) {
			GameRegistry.addRecipe(new RecipeMachine(charger, new Object[] { " X ", "YCY", "IPI", 'C', machineFrame, 'I', copperPart, 'P',
					TEItems.powerCoilGold, 'X', BlockFrame.frameCellBasic, 'Y', TEItems.powerCoilSilver }));
		}
		TECraftingHandler.addMachineUpgradeRecipes(furnace);
		TECraftingHandler.addMachineUpgradeRecipes(pulverizer);
		TECraftingHandler.addMachineUpgradeRecipes(sawmill);
		TECraftingHandler.addMachineUpgradeRecipes(smelter);
		TECraftingHandler.addMachineUpgradeRecipes(crucible);
		TECraftingHandler.addMachineUpgradeRecipes(transposer);
		TECraftingHandler.addMachineUpgradeRecipes(precipitator);
		TECraftingHandler.addMachineUpgradeRecipes(extruder);
		TECraftingHandler.addMachineUpgradeRecipes(accumulator);
		TECraftingHandler.addMachineUpgradeRecipes(assembler);
		TECraftingHandler.addMachineUpgradeRecipes(charger);

		TECraftingHandler.addSecureRecipe(furnace);
		TECraftingHandler.addSecureRecipe(pulverizer);
		TECraftingHandler.addSecureRecipe(sawmill);
		TECraftingHandler.addSecureRecipe(smelter);
		TECraftingHandler.addSecureRecipe(crucible);
		TECraftingHandler.addSecureRecipe(transposer);
		TECraftingHandler.addSecureRecipe(precipitator);
		TECraftingHandler.addSecureRecipe(extruder);
		TECraftingHandler.addSecureRecipe(accumulator);
		TECraftingHandler.addSecureRecipe(assembler);
		TECraftingHandler.addSecureRecipe(charger);

		return true;
	}

	public static void refreshItemStacks() {

		furnace = ItemBlockMachine.setDefaultTag(furnace);
		pulverizer = ItemBlockMachine.setDefaultTag(pulverizer);
		sawmill = ItemBlockMachine.setDefaultTag(sawmill);
		smelter = ItemBlockMachine.setDefaultTag(smelter);
		crucible = ItemBlockMachine.setDefaultTag(crucible);
		transposer = ItemBlockMachine.setDefaultTag(transposer);
		precipitator = ItemBlockMachine.setDefaultTag(precipitator);
		extruder = ItemBlockMachine.setDefaultTag(extruder);
		accumulator = ItemBlockMachine.setDefaultTag(accumulator);
		assembler = ItemBlockMachine.setDefaultTag(assembler);
		charger = ItemBlockMachine.setDefaultTag(charger);

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
	}

	public static enum Types {
		FURNACE, PULVERIZER, SAWMILL, SMELTER, CRUCIBLE, TRANSPOSER, PRECIPITATOR, EXTRUDER, ACCUMULATOR, ASSEMBLER, CHARGER
	}

	public static final String[] NAMES = { "furnace", "pulverizer", "sawmill", "smelter", "crucible", "transposer", "precipitator", "extruder", "accumulator",
			"assembler", "charger" };
	public static boolean[] enable = new boolean[Types.values().length];
	public static boolean[] creativeTiers = new boolean[4];
	public static ItemStack[] defaultAugments = new ItemStack[3];

	public static boolean defaultAutoTransfer = true;
	public static boolean defaultRedstoneControl = true;
	public static boolean defaultReconfigSides = true;

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

		category = "block.tweak";
		defaultAutoTransfer = ThermalExpansion.config.get(category, "Machines.DefaultAugments.AutoTransfer", true);
		defaultRedstoneControl = ThermalExpansion.config.get(category, "Machines.DefaultAugments.RedstoneControl", true);
		defaultReconfigSides = ThermalExpansion.config.get(category, "Machines.DefaultAugments.ReconfigSides", true);

		creativeTiers[0] = ThermalExpansion.config.get(category, "CreativeTab.Machines.Tier0", false);
		creativeTiers[1] = ThermalExpansion.config.get(category, "CreativeTab.Machines.Tier1", false);
		creativeTiers[2] = ThermalExpansion.config.get(category, "CreativeTab.Machines.Tier2", false);
		creativeTiers[3] = ThermalExpansion.config.get(category, "CreativeTab.Machines.Tier3", true);
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
