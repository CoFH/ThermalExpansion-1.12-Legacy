package cofh.thermalexpansion.block.light;

import cofh.thermalexpansion.block.BlockTEBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLight extends BlockTEBase {

	public static final PropertyEnum<Type> VARIANT = PropertyEnum.create("type", Type.class);

	protected BlockLight(Material material) {

		super(Material.REDSTONE_LIGHT);

		setUnlocalizedName("light");

		setHardness(3.0F);
		setResistance(150.0F);
		setSoundType(SoundType.GLASS);

		configGui = true;
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {

		for (int i = 0; i < Type.values().length; i++) {
			if (enable[i]) {
				items.add(itemBlock.setDefaultTag(new ItemStack(this, 1, i)));
			}
		}
	}

	/* TYPE METHODS */
	@Override
	public IBlockState getStateFromMeta(int meta) {

		return this.getDefaultState().withProperty(VARIANT, Type.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {

		return state.getValue(VARIANT).getMetadata();
	}

	@Override
	public int damageDropped(IBlockState state) {

		return state.getValue(VARIANT).getMetadata();
	}

	/* ITileEntityProvider */
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {

		if (meta >= Type.values().length) {
			return null;
		}
		return null;
	}

	/* BLOCK METHODS */
	@Override
	public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		return true;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {

		return false;
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		return true;
	}

	@Override
	public boolean initialize() {

		return true;
	}

	/* TYPE */
	public enum Type implements IStringSerializable {

		// @formatter:off
		ILLUMINATOR(0, "illuminator"),
		LUMIUM_LAMP(1, "lumium_lamp"),
		RADIANT_LAMP(2, "radiant_lumium_lamp");;
		// @formatter:on

		private final int metadata;
		private final String name;

		Type(int metadata, String name) {

			this.metadata = metadata;
			this.name = name;
		}

		public int getMetadata() {

			return this.metadata;
		}

		@Override
		public String getName() {

			return this.name;
		}
	}

	public static boolean[] enable = new boolean[Type.values().length];

	/* REFERENCES */
	public static ItemStack lightIlluminator;

	public static ItemBlockLight itemBlock;

}
