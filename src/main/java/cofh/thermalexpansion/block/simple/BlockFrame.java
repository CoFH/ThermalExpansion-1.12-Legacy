package cofh.thermalexpansion.block.simple;

import codechicken.lib.block.IParticleProvider;
import codechicken.lib.block.IType;
import codechicken.lib.item.ItemStackRegistry;
import cofh.api.block.IDismantleable;
import cofh.api.core.IInitializer;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.CommonProperties;
import cofh.thermalexpansion.client.IBlockLayerProvider;
import cofh.thermalexpansion.client.bakery.IBakeryBlock;
import cofh.thermalexpansion.client.bakery.ICustomBlockBakery;
import cofh.thermalexpansion.render.RenderFrame;
import cofh.thermalexpansion.util.crafting.PulverizerManager;
import cofh.thermalexpansion.util.crafting.TransposerManager;
import cofh.thermalfoundation.fluid.TFFluids;
import cofh.thermalfoundation.item.TFItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
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
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;

public class BlockFrame extends Block implements IDismantleable, IInitializer, IBakeryBlock, IBlockLayerProvider {

    public static int renderPass = 0;

    public static boolean hasCenter(int metadata) {

        return metadata < Types.ILLUMINATOR.ordinal();
    }

    public static boolean hasFrame(int metadata) {

        return metadata < Types.ILLUMINATOR.ordinal();
    }

    public static final PropertyEnum<Types> TYPES = PropertyEnum.create("type", Types.class);

    public BlockFrame() {

        super(Material.IRON);
        setHardness(15.0F);
        setResistance(25.0F);
        setSoundType(SoundType.METAL);
        setCreativeTab(ThermalExpansion.tabBlocks);
        setUnlocalizedName("thermalexpansion.frame");
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPES).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPES, Types.fromMeta(meta));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState.Builder(this).add(TYPES).add(CommonProperties.ACTIVE_PROPERTY).build();
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

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, SpawnPlacementType type) {
        return false;
    }


    @Override
    public boolean isOpaqueCube(IBlockState state) {

        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

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
            entity.setPickupDelay(10);
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

    @Override
    public ICustomBlockBakery getCustomBakery() {
        return RenderFrame.instance;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public int getTexturePasses() {
        return 2;
    }

    @Override
    public BlockRenderLayer getRenderlayerForPass(int pass) {
        return pass > 0 ? BlockRenderLayer.TRANSLUCENT : BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public enum Types implements IStringSerializable, IType, IParticleProvider {
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
        ILLUMINATOR;

        @Override
        public String getName() {
            return name().toLowerCase(Locale.US);
        }


        public static Types fromMeta(int meta) {
            try {
                return values()[meta];
            } catch (IndexOutOfBoundsException e){
                throw new RuntimeException("Someone has requested an invalid metadata for a block inside ThermalExpansion.", e);
            }
        }

        @Override
        public int meta() {
            return ordinal();
        }

        @Override
        public IProperty<?> getTypeProperty() {
            return TYPES;
        }

        @Override
        public String getParticleTexture() {
            switch (this) {

                case MACHINE_BASIC:
                case MACHINE_HARDENED:
                case MACHINE_REINFORCED:
                case MACHINE_RESONANT:
                    return "thermalexpansion:blocks/machine/machine_frame_side";
                case CELL_BASIC:
                case CELL_HARDENED:
                    return "thermalexpansion:blocks/cell/" + getName();
                case CELL_REINFORCED_EMPTY:
                case CELL_REINFORCED_FULL:
                case CELL_RESONANT_EMPTY:
                case CELL_RESONANT_FULL:
                    return "thermalexpansion:blocks/cell/" + getName().substring(0, getName().lastIndexOf("_"));
                case TESSERACT_EMPTY:
                case TESSERACT_FULL:
                    return "thermalexpansion:blocks/tesseract/tesseract";
                case ILLUMINATOR:
                    return "thermalexpansion:blocks/light/" + getName() + "_frame";
            }
            return "minecraft:blocks/dirt";
        }
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
