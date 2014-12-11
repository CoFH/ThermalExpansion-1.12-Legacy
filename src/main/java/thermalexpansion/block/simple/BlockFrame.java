package thermalexpansion.block.simple;

import cofh.api.block.IDismantleable;
import cofh.api.core.IInitializer;
import cofh.core.render.IconRegistry;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.ItemHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.core.TEProps;
import thermalexpansion.util.crafting.PulverizerManager;
import thermalexpansion.util.crafting.TransposerManager;
import thermalfoundation.fluid.TFFluids;
import thermalfoundation.item.TFItems;

public class BlockFrame extends Block implements IDismantleable, IInitializer {

	public static int renderPass = 0;

	public static boolean hasCenter(int metadata) {

		return metadata < Types.ILLUMINATOR.ordinal();
	}

	public static boolean hasFrame(int metadata) {

		return metadata < Types.ILLUMINATOR.ordinal();
	}

	public BlockFrame() {

		super(Material.iron);
		setHardness(15.0F);
		setResistance(25.0F);
		setStepSound(soundTypeMetal);
		setCreativeTab(ThermalExpansion.tabBlocks);
		setBlockName("thermalexpansion.frame");
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {

		for (int i = 0; i < Types.values().length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public int damageDropped(int i) {

		return i;
	}

	@Override
	public int getRenderBlockPass() {

		return 1;
	}

	@Override
	public int getRenderType() {

		return TEProps.renderIdFrame;
	}

	@Override
	public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {

		return false;
	}

	@Override
	public boolean canRenderInPass(int pass) {

		renderPass = pass;
		return pass < 2;
	}

	@Override
	public boolean isOpaqueCube() {

		return false;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {

		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {

		return false;
	}

	@Override
	public IIcon getIcon(int side, int metadata) {

		if (side == 6) {
			return getInnerIcon(metadata);
		} else if (side == 7) {
			return getCenterIcon(metadata);
		}
		return getFrameIcon(side, metadata);
	}

	private IIcon getFrameIcon(int side, int metadata) {

		switch (Types.values()[metadata]) {
		case CELL_BASIC:
			return IconRegistry.getIcon("FrameCellBasic");
		case CELL_REINFORCED_EMPTY:
		case CELL_REINFORCED_FULL:
			return IconRegistry.getIcon("FrameCellReinforced");
		case TESSERACT_EMPTY:
		case TESSERACT_FULL:
			return IconRegistry.getIcon("FrameTesseract");
		case ILLUMINATOR:
			return IconRegistry.getIcon("FrameIlluminator");
		default:
			if (side == 0) {
				return IconRegistry.getIcon("FrameMachineBottom");
			}
			if (side == 1) {
				return IconRegistry.getIcon("FrameMachineTop");
			}
			return IconRegistry.getIcon("FrameMachineSide");
		}
	}

	private IIcon getInnerIcon(int metadata) {

		switch (Types.values()[metadata]) {
		case CELL_BASIC:
			return IconRegistry.getIcon("FrameCellBasicInner");
		case CELL_REINFORCED_EMPTY:
		case CELL_REINFORCED_FULL:
			return IconRegistry.getIcon("FrameCellReinforcedInner");
		case TESSERACT_EMPTY:
		case TESSERACT_FULL:
			return IconRegistry.getIcon("FrameTesseractInner");
		case ILLUMINATOR:
			return IconRegistry.getIcon("FrameIlluminatorInner");
		default:
			return IconRegistry.getIcon("FrameMachineInner");
		}
	}

	private IIcon getCenterIcon(int metadata) {

		return IconRegistry.getIcon("FrameCenter", metadata);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		IconRegistry.addIcon("FrameMachineBottom", "thermalexpansion:machine/Machine_Frame_Bottom", ir);
		IconRegistry.addIcon("FrameMachineTop", "thermalexpansion:machine/Machine_Frame_Top", ir);
		IconRegistry.addIcon("FrameMachineSide", "thermalexpansion:machine/Machine_Frame_Side", ir);
		IconRegistry.addIcon("FrameCellBasic", "thermalexpansion:cell/Cell_Basic", ir);
		IconRegistry.addIcon("FrameCellReinforced", "thermalexpansion:cell/Cell_Reinforced", ir);
		IconRegistry.addIcon("FrameTesseract", "thermalexpansion:tesseract/Tesseract", ir);
		IconRegistry.addIcon("FrameIlluminator", "thermalexpansion:light/Illuminator_Frame", ir);

		IconRegistry.addIcon("FrameMachineInner", "thermalexpansion:machine/Machine_Frame_Inner", ir);
		IconRegistry.addIcon("FrameCellBasicInner", "thermalexpansion:cell/Cell_Basic_Inner", ir);
		IconRegistry.addIcon("FrameCellReinforcedInner", "thermalexpansion:cell/Cell_Reinforced_Inner", ir);
		IconRegistry.addIcon("FrameTesseractInner", "thermalexpansion:tesseract/Tesseract_Inner", ir);
		IconRegistry.addIcon("FrameIlluminatorInner", "thermalexpansion:config/Config_None", ir);

		IconRegistry.addIcon("FrameCenter" + 0, "thermalfoundation:storage/Block_Tin", ir);
		IconRegistry.addIcon("FrameCenter" + 1, "thermalfoundation:storage/Block_Electrum", ir);
		IconRegistry.addIcon("FrameCenter" + 2, "thermalfoundation:storage/Block_Signalum", ir);
		IconRegistry.addIcon("FrameCenter" + 3, "thermalfoundation:storage/Block_Enderium", ir);
		IconRegistry.addIcon("FrameCenter" + 4, "thermalexpansion:cell/Cell_Center_Solid", ir);
		IconRegistry.addIcon("FrameCenter" + 5, "thermalexpansion:config/Config_None", ir);
		IconRegistry.addIcon("FrameCenter" + 6, "thermalfoundation:fluid/Fluid_Redstone_Still", ir);
		IconRegistry.addIcon("FrameCenter" + 7, "thermalexpansion:config/Config_None", ir);
		IconRegistry.addIcon("FrameCenter" + 8, "thermalfoundation:fluid/Fluid_Ender_Still", ir);
		IconRegistry.addIcon("FrameCenter" + 9, "thermalexpansion:config/Config_None", ir);
	}

	/* IDismantleable */
	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnDrops) {

		int metadata = world.getBlockMetadata(x, y, z);
		ItemStack dropBlock = new ItemStack(this, 1, metadata);
		world.setBlockToAir(x, y, z);

		if (!returnDrops) {
			float f = 0.3F;
			double x2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
			double y2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
			double z2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
			EntityItem entity = new EntityItem(world, x + x2, y + y2, z + z2, dropBlock);
			entity.delayBeforeCanPickup = 10;
			world.spawnEntityInWorld(entity);

			CoreUtils.dismantleLog(player.getCommandSenderName(), this, metadata, x, y, z);
		}
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		ret.add(dropBlock);
		return ret;
	}

	@Override
	public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z) {

		return true;
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		return true;
	}

	@Override
	public boolean initialize() {

		frameMachineBasic = new ItemStack(this, 1, Types.MACHINE_BASIC.ordinal());
		frameMachineHardened = new ItemStack(this, 1, Types.MACHINE_HARDENED.ordinal());
		frameMachineReinforced = new ItemStack(this, 1, Types.MACHINE_REINFORCED.ordinal());
		frameMachineResonant = new ItemStack(this, 1, Types.MACHINE_RESONANT.ordinal());
		frameCellBasic = new ItemStack(this, 1, Types.CELL_BASIC.ordinal());
		frameCellReinforcedEmpty = new ItemStack(this, 1, Types.CELL_REINFORCED_EMPTY.ordinal());
		frameCellReinforcedFull = new ItemStack(this, 1, Types.CELL_REINFORCED_FULL.ordinal());
		frameTesseractEmpty = new ItemStack(this, 1, Types.TESSERACT_EMPTY.ordinal());
		frameTesseractFull = new ItemStack(this, 1, Types.TESSERACT_FULL.ordinal());
		frameIlluminator = new ItemStack(this, 1, Types.ILLUMINATOR.ordinal());

		GameRegistry.registerCustomItemStack("frameMachineBasic", frameMachineBasic);
		GameRegistry.registerCustomItemStack("frameMachineHardened", frameMachineHardened);
		GameRegistry.registerCustomItemStack("frameMachineReinforced", frameMachineReinforced);
		GameRegistry.registerCustomItemStack("frameMachineResonant", frameMachineResonant);
		GameRegistry.registerCustomItemStack("frameCellBasic", frameCellBasic);
		GameRegistry.registerCustomItemStack("frameCellReinforcedEmpty", frameCellReinforcedEmpty);
		GameRegistry.registerCustomItemStack("frameCellReinforcedFull", frameCellReinforcedFull);
		GameRegistry.registerCustomItemStack("frameTesseractEmpty", frameTesseractEmpty);
		GameRegistry.registerCustomItemStack("frameTesseractFull", frameTesseractFull);
		GameRegistry.registerCustomItemStack("frameIlluminator", frameIlluminator);

		OreDictionary.registerOre("thermalexpansion:machineFrame", frameMachineBasic);
		OreDictionary.registerOre("thermalexpansion:machineFrame", frameMachineHardened);
		OreDictionary.registerOre("thermalexpansion:machineFrame", frameMachineReinforced);
		OreDictionary.registerOre("thermalexpansion:machineFrame", frameMachineResonant);

		return true;
	}

	@Override
	public boolean postInit() {

		GameRegistry
				.addRecipe(new ShapedOreRecipe(frameMachineBasic, new Object[] { "IGI", "GXG", "IGI", 'I', "ingotIron", 'G', "blockGlass", 'X', "gearTin" }));

		/* Direct Recipes */
		// GameRegistry.addRecipe(new ShapedOreRecipe(frameMachineHardened, new Object[] { "IGI", "GXG", "IGI", 'I', "ingotInvar", 'G', "blockGlass", 'X',
		// "gearElectrum" }));
		// GameRegistry.addRecipe(new ShapedOreRecipe(frameMachineReinforced, new Object[] { "IGI", "GXG", "IGI", 'I', "ingotInvar", 'G', "blockGlassHardened",
		// 'X', "gearSignalum" }));
		// GameRegistry.addRecipe(new ShapedOreRecipe(frameMachineResonant, new Object[] { "IGI", "GXG", "IGI", 'I', "ingotInvar", 'G', "blockGlassHardened",
		// 'X',
		// "gearEnderium" }));

		/* Tiered Recipes */
		GameRegistry.addRecipe(new ShapedOreRecipe(frameMachineHardened, new Object[] { "IGI", " X ", "I I", 'I', "ingotInvar", 'G', "gearElectrum", 'X',
				frameMachineBasic }));
		GameRegistry.addRecipe(new ShapedOreRecipe(frameMachineReinforced, new Object[] { "IGI", " X ", "I I", 'I', "blockGlassHardened", 'G', "gearSignalum",
				'X', frameMachineHardened }));
		GameRegistry.addRecipe(new ShapedOreRecipe(frameMachineResonant, new Object[] { "IGI", " X ", "I I", 'I', "ingotSilver", 'G', "gearEnderium", 'X',
				frameMachineReinforced }));

		GameRegistry.addRecipe(new ShapedOreRecipe(frameCellBasic, new Object[] { "IGI", "GXG", "IGI", 'I', "ingotLead", 'G', "blockGlass", 'X',
				Blocks.redstone_block }));
		PulverizerManager.addRecipe(4000, frameCellBasic, ItemHelper.cloneStack(Items.redstone, 8), ItemHelper.cloneStack(TFItems.ingotLead, 3));

		GameRegistry.addRecipe(new ShapedOreRecipe(frameCellReinforcedEmpty, new Object[] { "IGI", "GXG", "IGI", 'I', "ingotElectrum", 'G',
				"blockGlassHardened", 'X', Items.diamond }));
		TransposerManager.addTEFillRecipe(16000, frameCellReinforcedEmpty, frameCellReinforcedFull, new FluidStack(TFFluids.fluidRedstone, 4000), false);

		GameRegistry.addRecipe(new ShapedOreRecipe(frameTesseractEmpty, new Object[] { "IGI", "GXG", "IGI", 'I', "ingotEnderium", 'G', "blockGlassHardened",
				'X', Items.diamond }));
		TransposerManager.addTEFillRecipe(16000, frameTesseractEmpty, frameTesseractFull, new FluidStack(TFFluids.fluidEnder, 1000), false);

		GameRegistry.addRecipe(new ShapedOreRecipe(frameIlluminator, new Object[] { " Q ", "G G", " S ", 'G', "blockGlassHardened", 'Q', "gemQuartz", 'S',
				"ingotSignalum" }));

		return true;
	}

	public static enum Types {
		MACHINE_BASIC, MACHINE_HARDENED, MACHINE_REINFORCED, MACHINE_RESONANT, CELL_BASIC,
		CELL_REINFORCED_EMPTY, CELL_REINFORCED_FULL, TESSERACT_EMPTY, TESSERACT_FULL,
		ILLUMINATOR
	}

	public static final String[] NAMES = { "machineBasic", "machineHardened", "machineReinforced", "machineResonant", "cellBasic", "cellReinforcedEmpty",
			"cellReinforcedFull", "tesseractEmpty", "tesseractFull", "illuminator" };

	public static ItemStack frameMachineBasic;
	public static ItemStack frameMachineHardened;
	public static ItemStack frameMachineReinforced;
	public static ItemStack frameMachineResonant;
	public static ItemStack frameCellBasic;
	public static ItemStack frameCellReinforcedEmpty;
	public static ItemStack frameCellReinforcedFull;
	public static ItemStack frameTesseractEmpty;
	public static ItemStack frameTesseractFull;
	public static ItemStack frameIlluminator;

}
