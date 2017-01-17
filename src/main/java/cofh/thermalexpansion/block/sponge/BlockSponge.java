package cofh.thermalexpansion.block.sponge;

import codechicken.lib.item.ItemStackRegistry;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;

public class BlockSponge extends BlockTEBase {

	public static final PropertyEnum<Types> TYPES = PropertyEnum.create("type", Types.class);
	public static final PropertyBool SOAKED = PropertyBool.create("soaked");

	public BlockSponge() {

		super(Material.SPONGE);
		setHardness(0.6F);
		setSoundType(SoundType.PLANT);
		setUnlocalizedName("thermalexpansion.sponge");
		setDefaultState(getDefaultState().withProperty(SOAKED, false));
	}

	@Override
	public int getMetaFromState(IBlockState state) {

		return state.getValue(TYPES).meta();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {

		return getDefaultState().withProperty(TYPES, Types.fromMeta(meta));
	}

	@Override
	protected BlockStateContainer createBlockState() {

		return new BlockStateContainer(this, TYPES, SOAKED);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {

		return state.withProperty(SOAKED, ((TileSponge) worldIn.getTileEntity(pos)).getFluid() != null);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		if (metadata >= Types.values().length) {
			return null;
		}
		switch (Types.values()[metadata]) {
			case CREATIVE:
				if (!enable[Types.CREATIVE.ordinal()]) {
					return null;
				}
				return new TileSpongeCreative(metadata);
			case BASIC:
				return new TileSponge(metadata);
			case MAGMATIC:
				return new TileSpongeMagmatic(metadata);
			default:
				return null;
		}
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {

		if (enable[0]) {
			list.add(new ItemStack(item, 1, 0));
		}
		for (int i = 1; i < Types.values().length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {

		if (getMetaFromState(state) == 0 && !enable[0]) {
			world.setBlockToAir(pos);
			return;
		}
		TileEntity tile = world.getTileEntity(pos);

		if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("Fluid")) {
			FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag("Fluid"));

			if (fluid != null) {
				((TileSponge) tile).setFluid(fluid);
			}
		} else if (tile instanceof TileSponge) {
			((TileSponge) tile).absorb();
		}
		super.onBlockPlacedBy(world, pos, state, living, stack);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {

		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof TileSponge) {
			((TileSponge) tile).placeAir();
		}
	}

	@Override
	public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		NBTTagCompound tag = super.getItemStackTag(world, pos);
		TileSponge tile = (TileSponge) world.getTileEntity(pos);

		if (tile != null) {
			FluidStack fluid = tile.getFluid();

			if (fluid != null) {
				tag = new NBTTagCompound();
				tag.setTag("Fluid", fluid.writeToNBT(new NBTTagCompound()));
			}
		}
		return tag;
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		TileSponge.initialize();
		TileSpongeMagmatic.initialize();
		TileSpongeCreative.initialize();

		spongeBasic = new ItemStack(this, 1, Types.BASIC.ordinal());
		spongeMagmatic = new ItemStack(this, 1, Types.MAGMATIC.ordinal());

		ItemStackRegistry.registerCustomItemStack("spongeBasic", spongeBasic);
		ItemStackRegistry.registerCustomItemStack("spongeMagmatic", spongeMagmatic);

		return true;
	}

	@Override
	public boolean postInit() {

		if (enable[Types.BASIC.ordinal()]) {
			GameRegistry.addRecipe(ShapedRecipe(spongeBasic, "SWS", "WBW", "SWS", 'S', Items.STRING, 'W', "dustWood", 'B', "slimeball"));
		}
		if (enable[Types.MAGMATIC.ordinal()]) {
			GameRegistry.addRecipe(ShapedRecipe(spongeMagmatic, "SWS", "WBW", "SWS", 'S', Items.STRING, 'W', "dustWood", 'B', Items.MAGMA_CREAM));
		}
		return true;
	}

	public enum Types implements IStringSerializable {
		CREATIVE, BASIC, MAGMATIC;

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
			} catch (IndexOutOfBoundsException e) {
				throw new RuntimeException("Someone has requested an invalid metadata for a block inside ThermalExpansion.", e);
			}
		}

	}

	public static final String[] NAMES = { "creative", "basic", "magmatic" };
	public static boolean[] enable = new boolean[Types.values().length];

	static {
		String category = "Sponge.";

		enable[0] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[0]), "Enable", true);
		for (int i = 1; i < Types.values().length; i++) {
			enable[i] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[i]), "Recipe.Enable", true);
		}
	}

	public static ItemStack spongeCreative;
	public static ItemStack spongeBasic;
	public static ItemStack spongeMagmatic;

}
