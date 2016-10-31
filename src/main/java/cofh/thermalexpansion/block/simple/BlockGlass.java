package cofh.thermalexpansion.block.simple;

import codechicken.lib.raytracer.RayTracer;
import cofh.api.block.IDismantleable;
import cofh.api.core.IInitializer;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class BlockGlass extends Block implements IDismantleable, IInitializer {

    public static final PropertyEnum<Types> TYPES = PropertyEnum.create("type", Types.class);

    public BlockGlass() {

        super(Material.GLASS);
        setHardness(3.0F);
        setResistance(200.0F);
        setSoundType(SoundType.GLASS);
        setCreativeTab(ThermalExpansion.tabBlocks);
        setUnlocalizedName("thermalexpansion.glass");
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
        return new BlockStateContainer(this, TYPES);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return getMetaFromState(state) == 1 ? 15 : 0;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {

        for (int i = 0; i < 2; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            RayTraceResult traceResult = RayTracer.retrace(player);
            if (Utils.isHoldingUsableWrench(player, traceResult)) {
                if (ServerHelper.isServerWorld(world)) {
                    dismantleBlock(player, world, pos, false);
                    Utils.usedWrench(player, traceResult);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public int damageDropped(IBlockState state) {

        return getMetaFromState(state);
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, SpawnPlacementType type) {
        return false;
    }

    @Override
    protected boolean canSilkHarvest() {
        return true;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {

        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {

        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return blockState.getBlock() != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }

    //@Override
    //public IIcon getIcon(int side, int metadata) {
    //	if (metadata == 1) {
    //		return TEXTURE[1];
    //	}
    //	return TEXTURE[0];
    //}

    //@Override
    //@SideOnly(Side.CLIENT)
    //public void registerBlockIcons(IIconRegister ir) {
    //	TEXTURE[0] = ir.registerIcon("thermalexpansion:glass/Glass_Hardened");
    //	TEXTURE[1] = ir.registerIcon("thermalexpansion:glass/Glass_Hardened_Lumium");
    //}

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

    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    /* IInitializer */
    @Override
    public boolean preInit() {

        return true;
    }

    @Override
    public boolean initialize() {

        glassHardened = new ItemStack(this, 1, 0);
        glassHardenedIlluminated = new ItemStack(this, 1, 1);

        ItemHelper.registerWithHandlers("blockGlassHardened", glassHardened);
        ItemHelper.registerWithHandlers("blockGlassHardenedIlluminated", glassHardenedIlluminated);

        OreDictionary.registerOre("blockGlassHardened", glassHardenedIlluminated);

        return true;
    }


    @Override
    public boolean postInit() {

        return true;
    }

    public enum Types implements IStringSerializable {
        HARDENED,
        ILLUMINATED;

        @Override
        public String getName() {
            return name().toLowerCase(Locale.US);
        }

        public int meta() {
            return ordinal();
        }

        public static Types fromMeta(int meta) {
            try {
                return values()[meta];
            } catch (IndexOutOfBoundsException e){
                throw new RuntimeException("Someone has requested an invalid metadata for a block inside ThermalExpansion.", e);
            }
        }
    }

    //public static IIcon TEXTURE[] = new IIcon[2];

    public static ItemStack glassHardened;
    public static ItemStack glassHardenedIlluminated;

}
