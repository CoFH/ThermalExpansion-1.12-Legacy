package cofh.thermalexpansion.block.light;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.ModelErrorStateProperty;
import codechicken.lib.model.bakery.generation.IBakery;
import cofh.core.render.IModelRegister;
import cofh.core.util.StateMapper;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.render.BakeryLight;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLight extends BlockTEBase implements IModelRegister, IBakeryProvider {

	public static final PropertyEnum<Type> VARIANT = PropertyEnum.create("type", Type.class);

	protected BlockLight() {

		super(Material.REDSTONE_LIGHT);

		setUnlocalizedName("light");

		setHardness(3.0F);
		setResistance(150.0F);
		setSoundType(SoundType.GLASS);
		setDefaultState(getBlockState().getBaseState().withProperty(VARIANT, Type.ILLUMINATOR));

		configGui = true;
	}

	@Override
	protected BlockStateContainer createBlockState() {

		BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
		// Listed
		builder.add(VARIANT);
		// UnListed
		builder.add(ModelErrorStateProperty.ERROR_STATE);
		builder.add(TEProps.TILE_LIGHT);
		builder.add(TEProps.BAKERY_WORLD);

		return builder.build();
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
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {

		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {

		return true;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		return true;
	}

	/* RENDERING METHODS */
	@Override
	@SideOnly (Side.CLIENT)
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {

		return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {

		return ModelBakery.handleExtendedState((IExtendedBlockState) super.getExtendedState(state, world, pos), world, pos);
	}

	/* IBakeryProvider */
	@Override
	public IBakery getBakery() {

		return BakeryLight.INSTANCE;
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		StateMapper mapper = new StateMapper("thermalexpansion", "light", "light");
		ModelLoader.setCustomModelResourceLocation(itemBlock, 0, mapper.location);
		ModelLoader.setCustomStateMapper(this, mapper);
		ModelLoader.setCustomMeshDefinition(itemBlock, mapper);
		ModelRegistryHelper.register(mapper.location, new CCBakeryModel());

		ModelBakery.registerBlockKeyGenerator(this, state -> {

			StringBuilder builder = new StringBuilder(state.getBlock().getRegistryName().toString() + "|" + state.getBlock().getMetaFromState(state));
			TileLightBase tile = state.getValue(TEProps.TILE_LIGHT);
			builder.append(",color=").append(tile.color);
			builder.append(",light=").append(tile.getInternalLight());
			return builder.toString();
		});

		ModelBakery.registerItemKeyGenerator(itemBlock, ModelBakery.defaultItemKeyGenerator);
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		//		this.setRegistryName("light");
		//		ForgeRegistries.BLOCKS.register(this);
		//
		//		itemBlock = new ItemBlockLight(this);
		//		itemBlock.setRegistryName(this.getRegistryName());
		//		ForgeRegistries.ITEMS.register(itemBlock);
		//
		//		TileLightBase.config();
		//
		//		TileIlluminator.initialize();

		return true;
	}

	@Override
	public boolean initialize() {

		return true;
	}

	/* HELPERS */
	private void addRecipes() {

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
	public static ItemStack lightLumiumLamp;
	public static ItemStack lightRadiantLumiumLamp;

	public static ItemBlockLight itemBlock;

}
