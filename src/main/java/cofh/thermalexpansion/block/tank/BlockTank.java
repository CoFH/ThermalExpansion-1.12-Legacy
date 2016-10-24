package cofh.thermalexpansion.block.tank;

import codechicken.lib.item.ItemStackRegistry;
import cofh.core.util.CoreUtils;
import cofh.core.util.crafting.RecipeUpgrade;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.core.TEProps;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.util.List;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;

public class BlockTank extends BlockTEBase {

    public BlockTank() {

        super(Material.GLASS);
        //setBlockBounds(0.125F, 0F, 0.125F, 0.875F, 1F, 0.875F);
        setHardness(4.0F);
        setResistance(120.0F);
        setSoundType(SoundType.GLASS);
        setUnlocalizedName("thermalexpansion.tank");
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

        if (enable[0]) {
            list.add(new ItemStack(item, 1, 0));
        }
        for (int i = 1; i < Types.values().length; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {
        IBlockState state1 = world.getBlockState(pos);
        if (state1.getBlock().getMetaFromState(state1) == 0 && !enable[0]) {
            world.setBlockToAir(pos);
            return;
        }
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("Fluid")) {
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag("Fluid"));

            if (fluid != null) {
                TileTank tile = (TileTank) world.getTileEntity(pos);
                tile.tank.setFluid(fluid);
                tile.calcLastDisplay();
            }
        }
        super.onBlockPlacedBy(world, pos, state, living, stack);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ)) {
            return true;
        }
        TileTank tile = (TileTank) world.getTileEntity(pos);

        if (FluidHelper.fillHandlerWithContainer(tile.getWorld(), tile, player)) {
            return true;
        }
        if (FluidHelper.fillContainerFromHandler(tile.getWorld(), tile, player, tile.getTankFluid())) {
            return true;
        }
        if (ItemHelper.isPlayerHoldingFluidContainer(player)) {
            return true;
        }
        return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
    }

    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {

        return HARDNESS[blockState.getBlock().getMetaFromState(blockState)];
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        IBlockState state = world.getBlockState(pos);
        return RESISTANCE[state.getBlock().getMetaFromState(state)];
    }

    //@Override
    public int getRenderType() {
        return TEProps.renderIdTank;
    }

    //@Override
    public int getRenderBlockPass() {

        return 1;
    }

    //@Override
    public boolean canRenderInPass(int pass) {

        renderPass = pass;
        return pass < 2;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.UP || side == EnumFacing.DOWN;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    //@Override
    //@SideOnly(Side.CLIENT)
    //public void registerBlockIcons(IIconRegister ir) {
    //    for (int i = 0; i < Types.values().length; i++) {
    //        IconRegistry.addIcon("TankBottom" + 2 * i, "thermalexpansion:tank/Tank_" + StringHelper.titleCase(NAMES[i]) + "_Bottom_Blue", ir);
    //        IconRegistry.addIcon("TankBottom" + (2 * i + 1), "thermalexpansion:tank/Tank_" + StringHelper.titleCase(NAMES[i]) + "_Bottom_Orange", ir);
    //
    //        IconRegistry.addIcon("TankTop" + 2 * i, "thermalexpansion:tank/Tank_" + StringHelper.titleCase(NAMES[i]) + "_Top_Blue", ir);
    //        IconRegistry.addIcon("TankTop" + (2 * i + 1), "thermalexpansion:tank/Tank_" + StringHelper.titleCase(NAMES[i]) + "_Top_Orange", ir);
    //
    //        IconRegistry.addIcon("TankSide" + 2 * i, "thermalexpansion:tank/Tank_" + StringHelper.titleCase(NAMES[i]) + "_Side_Blue", ir);
    //        IconRegistry.addIcon("TankSide" + (2 * i + 1), "thermalexpansion:tank/Tank_" + StringHelper.titleCase(NAMES[i]) + "_Side_Orange", ir);
    //    }
    //}

    @Override
    public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

        NBTTagCompound tag = super.getItemStackTag(world, pos);
        TileTank tile = (TileTank) world.getTileEntity(pos);

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
    public boolean canDismantle(EntityPlayer player, World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock().getMetaFromState(state) == Types.CREATIVE.ordinal() && !CoreUtils.isOp(player)) {
            return false;
        }
        return super.canDismantle(player, world, pos);
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

        ItemStackRegistry.registerCustomItemStack("tankCreative", tankCreative);
        ItemStackRegistry.registerCustomItemStack("tankBasic", tankBasic);
        ItemStackRegistry.registerCustomItemStack("tankHardened", tankHardened);
        ItemStackRegistry.registerCustomItemStack("tankReinforced", tankReinforced);
        ItemStackRegistry.registerCustomItemStack("tankResonant", tankResonant);

        return true;
    }

    @Override
    public boolean postInit() {

        final String basicMaterial = "ingotCopper";
        final String hardenedMaterial = "ingotInvar";
        final String reinforcedMaterial = "blockGlassHardened";
        final String resonantMaterial = "ingotEnderium";

        if (enable[Types.BASIC.ordinal()]) {
            GameRegistry.addRecipe(ShapedRecipe(tankBasic, " G ", "GXG", " G ", 'G', "blockGlass", 'X', basicMaterial));
        }
        if (enable[Types.HARDENED.ordinal()]) {
            GameRegistry.addRecipe(new RecipeUpgrade(tankHardened, new Object[] { " I ", "IXI", " I ", 'I', hardenedMaterial, 'X', tankBasic }));
            GameRegistry.addRecipe(ShapedRecipe(tankHardened, "IGI", "GXG", "IGI", 'I', hardenedMaterial, 'X', basicMaterial, 'G', "blockGlass"));
        }
        if (enable[Types.REINFORCED.ordinal()]) {
            GameRegistry.addRecipe(new RecipeUpgrade(tankReinforced, new Object[] { " G ", "GXG", " G ", 'G', reinforcedMaterial, 'X', tankHardened }));
            GameRegistry.addRecipe(new RecipeUpgrade(tankReinforced, new Object[] { "IGI", "GXG", "IGI", 'G', reinforcedMaterial, 'I', hardenedMaterial, 'X', tankBasic }));
        }
        if (enable[Types.RESONANT.ordinal()]) {
            GameRegistry.addRecipe(new RecipeUpgrade(tankResonant, new Object[] { " I ", "IXI", " I ", 'I', resonantMaterial, 'X', tankReinforced }));
            GameRegistry.addRecipe(new RecipeUpgrade(tankResonant, new Object[] { "GIG", "IXI", "GIG", 'I', resonantMaterial, 'G', reinforcedMaterial, 'X', tankHardened }));
        }
        return true;
    }

    public enum Types {
        CREATIVE,
        BASIC,
        HARDENED,
        REINFORCED,
        RESONANT
    }

    public static final String[] NAMES = { "creative", "basic", "hardened", "reinforced", "resonant" };
    public static final float[] HARDNESS = { -1.0F, 1.0F, 3.0F, 4.0F, 4.0F };
    public static final int[] RESISTANCE = { 1200, 15, 90, 120, 120 };
    public static boolean[] enable = new boolean[Types.values().length];

    static {
        String category = "Tank.";

        enable[0] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[0]), "Enable", true);
        for (int i = 1; i < Types.values().length; i++) {
            enable[i] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[i]), "Recipe.Enable", true);
        }
    }

    public static ItemStack tankCreative;
    public static ItemStack tankBasic;
    public static ItemStack tankHardened;
    public static ItemStack tankReinforced;
    public static ItemStack tankResonant;

}
