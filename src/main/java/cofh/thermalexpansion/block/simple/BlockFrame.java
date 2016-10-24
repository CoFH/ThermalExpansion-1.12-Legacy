package cofh.thermalexpansion.block.simple;

import codechicken.lib.item.ItemStackRegistry;
import cofh.api.block.IDismantleable;
import cofh.api.core.IInitializer;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.util.crafting.PulverizerManager;
import cofh.thermalexpansion.util.crafting.TransposerManager;
import cofh.thermalfoundation.fluid.TFFluids;
import cofh.thermalfoundation.item.TFItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;

public class BlockFrame extends Block implements IDismantleable, IInitializer {

    public static int renderPass = 0;

    public static boolean hasCenter(int metadata) {

        return metadata < Types.ILLUMINATOR.ordinal();
    }

    public static boolean hasFrame(int metadata) {

        return metadata < Types.ILLUMINATOR.ordinal();
    }

    public BlockFrame() {

        super(Material.IRON);
        setHardness(15.0F);
        setResistance(25.0F);
        setSoundType(SoundType.METAL);
        setCreativeTab(ThermalExpansion.tabBlocks);
        setUnlocalizedName("thermalexpansion.frame");
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
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        return HARDNESS[getMetaFromState(blockState)];
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        return RESISTANCE[getMetaFromState(world.getBlockState(pos))];
    }

    @Override
    public int damageDropped(IBlockState state) {

        return getMetaFromState(state);
    }

    //@Override
    public int getRenderBlockPass() {

        return 1;
    }

    //@Override
    public int getRenderType() {

        return TEProps.renderIdFrame;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, SpawnPlacementType type) {
        return false;
    }

	/*@Override
    public boolean canRenderInPass(int pass) {
		renderPass = pass;
		return pass < 2;
	}*/

    @Override
    public boolean isOpaqueCube(IBlockState state) {

        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

	/*@Override
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
		case CELL_HARDENED:
			return IconRegistry.getIcon("FrameCellHardened");
		case CELL_REINFORCED_EMPTY:
		case CELL_REINFORCED_FULL:
			return IconRegistry.getIcon("FrameCellReinforced");
		case CELL_RESONANT_EMPTY:
		case CELL_RESONANT_FULL:
			return IconRegistry.getIcon("FrameCellResonant");
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
		case CELL_HARDENED:
			return IconRegistry.getIcon("FrameCellHardenedInner");
		case CELL_REINFORCED_EMPTY:
		case CELL_REINFORCED_FULL:
			return IconRegistry.getIcon("FrameCellReinforcedInner");
		case CELL_RESONANT_EMPTY:
		case CELL_RESONANT_FULL:
			return IconRegistry.getIcon("FrameCellResonantInner");
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
		IconRegistry.addIcon("FrameCellHardened", "thermalexpansion:cell/Cell_Hardened", ir);
		IconRegistry.addIcon("FrameCellReinforced", "thermalexpansion:cell/Cell_Reinforced", ir);
		IconRegistry.addIcon("FrameCellResonant", "thermalexpansion:cell/Cell_Resonant", ir);
		IconRegistry.addIcon("FrameTesseract", "thermalexpansion:tesseract/Tesseract", ir);
		IconRegistry.addIcon("FrameIlluminator", "thermalexpansion:light/Illuminator_Frame", ir);

		IconRegistry.addIcon("FrameMachineInner", "thermalexpansion:machine/Machine_Frame_Inner", ir);
		IconRegistry.addIcon("FrameCellBasicInner", "thermalexpansion:cell/Cell_Basic_Inner", ir);
		IconRegistry.addIcon("FrameCellHardenedInner", "thermalexpansion:cell/Cell_Hardened_Inner", ir);
		IconRegistry.addIcon("FrameCellReinforcedInner", "thermalexpansion:cell/Cell_Reinforced_Inner", ir);
		IconRegistry.addIcon("FrameCellResonantInner", "thermalexpansion:cell/Cell_Resonant_Inner", ir);
		IconRegistry.addIcon("FrameTesseractInner", "thermalexpansion:tesseract/Tesseract_Inner", ir);
		IconRegistry.addIcon("FrameIlluminatorInner", "thermalexpansion:config/Config_None", ir);

		IconRegistry.addIcon("FrameCenter" + 0, "thermalfoundation:storage/Block_Tin", ir);
		IconRegistry.addIcon("FrameCenter" + 1, "thermalfoundation:storage/Block_Electrum", ir);
		IconRegistry.addIcon("FrameCenter" + 2, "thermalfoundation:storage/Block_Signalum", ir);
		IconRegistry.addIcon("FrameCenter" + 3, "thermalfoundation:storage/Block_Enderium", ir);
		IconRegistry.addIcon("FrameCenter" + 4, "thermalexpansion:cell/Cell_Center_Solid", ir);
		IconRegistry.addIcon("FrameCenter" + 5, "thermalexpansion:cell/Cell_Center_Solid", ir);
		IconRegistry.addIcon("FrameCenter" + 6, "thermalexpansion:config/Config_None", ir);
		IconRegistry.addIcon("FrameCenter" + 7, "thermalfoundation:fluid/Fluid_Redstone_Still", ir);
		IconRegistry.addIcon("FrameCenter" + 8, "thermalexpansion:config/Config_None", ir);
		IconRegistry.addIcon("FrameCenter" + 9, "thermalfoundation:fluid/Fluid_Redstone_Still", ir);
		IconRegistry.addIcon("FrameCenter" + 10, "thermalexpansion:config/Config_None", ir);
		IconRegistry.addIcon("FrameCenter" + 11, "thermalfoundation:fluid/Fluid_Ender_Still", ir);
		IconRegistry.addIcon("FrameCenter" + 12, "thermalexpansion:config/Config_None", ir);
	}*/

    /* IDismantleable */
    @Override
    public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, BlockPos pos, boolean returnDrops) {

        int metadata = getMetaFromState(world.getBlockState(pos));
        ItemStack dropBlock = new ItemStack(this, 1, metadata);
        world.setBlockToAir(pos);

        if (!returnDrops) {
            float f = 0.3F;
            double x2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
            double y2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
            double z2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
            EntityItem entity = new EntityItem(world, pos.getX() + x2, pos.getY() + y2, pos.getZ() + z2, dropBlock);
            entity.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entity);

            CoreUtils.dismantleLog(player.getName(), this, metadata, pos);
        }
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(dropBlock);
        return ret;
    }

    @Override
    public boolean canDismantle(EntityPlayer player, World world, BlockPos pos) {
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
        frameCellHardened = new ItemStack(this, 1, Types.CELL_HARDENED.ordinal());
        frameCellReinforcedEmpty = new ItemStack(this, 1, Types.CELL_REINFORCED_EMPTY.ordinal());
        frameCellReinforcedFull = new ItemStack(this, 1, Types.CELL_REINFORCED_FULL.ordinal());
        frameCellResonantEmpty = new ItemStack(this, 1, Types.CELL_RESONANT_EMPTY.ordinal());
        frameCellResonantFull = new ItemStack(this, 1, Types.CELL_RESONANT_FULL.ordinal());
        frameTesseractEmpty = new ItemStack(this, 1, Types.TESSERACT_EMPTY.ordinal());
        frameTesseractFull = new ItemStack(this, 1, Types.TESSERACT_FULL.ordinal());
        frameIlluminator = new ItemStack(this, 1, Types.ILLUMINATOR.ordinal());

        ItemStackRegistry.registerCustomItemStack("frameMachineBasic", frameMachineBasic);
        ItemStackRegistry.registerCustomItemStack("frameMachineHardened", frameMachineHardened);
        ItemStackRegistry.registerCustomItemStack("frameMachineReinforced", frameMachineReinforced);
        ItemStackRegistry.registerCustomItemStack("frameMachineResonant", frameMachineResonant);
        ItemStackRegistry.registerCustomItemStack("frameCellBasic", frameCellBasic);
        ItemStackRegistry.registerCustomItemStack("frameCellHardened", frameCellHardened);
        ItemStackRegistry.registerCustomItemStack("frameCellReinforcedEmpty", frameCellReinforcedEmpty);
        ItemStackRegistry.registerCustomItemStack("frameCellReinforcedFull", frameCellReinforcedFull);
        ItemStackRegistry.registerCustomItemStack("frameCellResonantEmpty", frameCellResonantEmpty);
        ItemStackRegistry.registerCustomItemStack("frameCellResonantFull", frameCellResonantFull);
        ItemStackRegistry.registerCustomItemStack("frameTesseractEmpty", frameTesseractEmpty);
        ItemStackRegistry.registerCustomItemStack("frameTesseractFull", frameTesseractFull);
        ItemStackRegistry.registerCustomItemStack("frameIlluminator", frameIlluminator);

        OreDictionary.registerOre("thermalexpansion:machineFrame", frameMachineBasic);
        OreDictionary.registerOre("thermalexpansion:machineFrame", frameMachineHardened);
        OreDictionary.registerOre("thermalexpansion:machineFrame", frameMachineReinforced);
        OreDictionary.registerOre("thermalexpansion:machineFrame", frameMachineResonant);

        return true;
    }

    @Override
    public boolean postInit() {

        GameRegistry.addRecipe(ShapedRecipe(frameMachineBasic, "IGI", "GXG", "IGI", 'I', "ingotIron", 'G', "blockGlass", 'X', "gearTin"));

		/* Direct Recipes */
        // GameRegistry.addRecipe(ShapedRecipe(frameMachineHardened, new Object[] { "IGI", "GXG", "IGI", 'I', "ingotInvar", 'G', "blockGlass", 'X',
        // "gearElectrum" }));
        // GameRegistry.addRecipe(ShapedRecipe(frameMachineReinforced, new Object[] { "IGI", "GXG", "IGI", 'I', "ingotInvar", 'G', "blockGlassHardened",
        // 'X', "gearSignalum" }));
        // GameRegistry.addRecipe(ShapedRecipe(frameMachineResonant, new Object[] { "IGI", "GXG", "IGI", 'I', "ingotInvar", 'G', "blockGlassHardened",
        // 'X',
        // "gearEnderium" }));

		/* Tiered Recipes */
        GameRegistry.addRecipe(ShapedRecipe(frameMachineHardened, "IGI", " X ", "I I", 'I', "ingotInvar", 'G', "gearElectrum", 'X', frameMachineBasic));
        GameRegistry.addRecipe(ShapedRecipe(frameMachineReinforced, "IGI", " X ", "I I", 'I', "blockGlassHardened", 'G', "gearSignalum", 'X', frameMachineHardened));
        GameRegistry.addRecipe(ShapedRecipe(frameMachineResonant, "IGI", " X ", "I I", 'I', "ingotSilver", 'G', "gearEnderium", 'X', frameMachineReinforced));

        GameRegistry.addRecipe(ShapedRecipe(frameCellBasic, "IGI", "GXG", "IGI", 'I', "ingotLead", 'G', "blockGlass", 'X', Blocks.REDSTONE_BLOCK));
        PulverizerManager.addRecipe(4000, frameCellBasic, ItemHelper.cloneStack(Items.REDSTONE, 8), ItemHelper.cloneStack(TFItems.ingotLead, 3));

        GameRegistry.addRecipe(ShapedRecipe(frameCellHardened, " I ", "IXI", " I ", 'I', "ingotInvar", 'X', frameCellBasic));
        PulverizerManager.addRecipe(8000, frameCellHardened, ItemHelper.cloneStack(Items.REDSTONE, 8), ItemHelper.cloneStack(TFItems.ingotInvar, 3));

        GameRegistry.addRecipe(ShapedRecipe(frameCellReinforcedEmpty, "IGI", "GXG", "IGI", 'I', "ingotElectrum", 'G', "blockGlassHardened", 'X', "gemDiamond"));
        TransposerManager.addTEFillRecipe(16000, frameCellReinforcedEmpty, frameCellReinforcedFull, new FluidStack(TFFluids.fluidRedstone, 4000), false);

        GameRegistry.addRecipe(ShapedRecipe(frameCellResonantEmpty, " I ", "IXI", " I ", 'I', "ingotEnderium", 'X', frameCellReinforcedEmpty));
        GameRegistry.addRecipe(ShapedRecipe(frameCellResonantFull, " I ", "IXI", " I ", 'I', "ingotEnderium", 'X', frameCellReinforcedFull));
        TransposerManager.addTEFillRecipe(16000, frameCellResonantEmpty, frameCellResonantFull, new FluidStack(TFFluids.fluidRedstone, 4000), false);

        if (recipe[Types.TESSERACT_EMPTY.ordinal()]) {
            GameRegistry.addRecipe(ShapedRecipe(frameTesseractEmpty, "IGI", "GXG", "IGI", 'I', "ingotEnderium", 'G', "blockGlassHardened", 'X', "gemDiamond"));
        }
        if (recipe[Types.TESSERACT_FULL.ordinal()]) {
            TransposerManager.addTEFillRecipe(16000, frameTesseractEmpty, frameTesseractFull, new FluidStack(TFFluids.fluidEnder, 1000), false);
        }
        GameRegistry.addRecipe(ShapedRecipe(ItemHelper.cloneStack(frameIlluminator, 2), " Q ", "G G", " S ", 'G', "blockGlassHardened", 'Q', "gemQuartz", 'S', "ingotSignalum"));

        return true;
    }

    public enum Types {
        MACHINE_BASIC,
        MACHINE_HARDENED,
        MACHINE_REINFORCED,
        MACHINE_RESONANT,
        CELL_BASIC,
        CELL_HARDENED,
        CELL_REINFORCED_EMPTY,
        CELL_REINFORCED_FULL,
        CELL_RESONANT_EMPTY,
        CELL_RESONANT_FULL,
        TESSERACT_EMPTY,
        TESSERACT_FULL,
        ILLUMINATOR
    }

    public static final String[] NAMES = { "machineBasic", "machineHardened", "machineReinforced", "machineResonant", "cellBasic", "cellHardened", "cellReinforcedEmpty", "cellReinforcedFull", "cellResonantEmpty", "cellResonantFull", "tesseractEmpty", "tesseractFull", "illuminator" };
    public static final float[] HARDNESS = { 5.0F, 15.0F, 20.0F, 20.0F, 5.0F, 15.0F, 20.0F, 20.0F, 20.0F, 20.0F, 15.0F, 15.0F, 3.0F };
    public static final int[] RESISTANCE = { 15, 90, 120, 120, 15, 90, 120, 120, 120, 120, 2000, 2000, 150 };
    public static boolean[] enable = new boolean[Types.values().length];
    public static boolean[] recipe = new boolean[Types.values().length];

    static {
        for (int i = 0; i < Types.values().length; i++) {
            enable[i] = true;
            recipe[i] = true;
        }
    }

    public static ItemStack frameMachineBasic;
    public static ItemStack frameMachineHardened;
    public static ItemStack frameMachineReinforced;
    public static ItemStack frameMachineResonant;
    public static ItemStack frameCellBasic;
    public static ItemStack frameCellHardened;
    public static ItemStack frameCellReinforcedEmpty;
    public static ItemStack frameCellReinforcedFull;
    public static ItemStack frameCellResonantEmpty;
    public static ItemStack frameCellResonantFull;
    public static ItemStack frameTesseractEmpty;
    public static ItemStack frameTesseractFull;
    public static ItemStack frameIlluminator;

}
