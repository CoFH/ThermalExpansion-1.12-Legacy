package cofh.thermalexpansion.block.storage;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.ModelErrorStateProperty;
import codechicken.lib.model.bakery.generation.IBakery;
import cofh.core.init.CoreEnchantments;
import cofh.core.init.CoreProps;
import cofh.core.render.IModelRegister;
import cofh.core.util.StateMapper;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.render.BakeryTank;
import cofh.thermalfoundation.item.ItemMaterial;
import cofh.thermalfoundation.item.ItemUpgrade;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static cofh.core.util.helpers.RecipeHelper.*;

public class BlockTank extends BlockTEBase implements IModelRegister, IBakeryProvider {

	public BlockTank() {

		super(Material.GLASS);

		setUnlocalizedName("tank");

		setHardness(15.0F);
		setResistance(25.0F);
		setDefaultState(getBlockState().getBaseState());

		standardGui = false;
		configGui = true;
	}

	@Override
	protected BlockStateContainer createBlockState() {

		BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
		// UnListed
		builder.add(ModelErrorStateProperty.ERROR_STATE);
		builder.add(TEProps.TILE_TANK);

		return builder.build();
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (enable) {
			if (TEProps.creativeTabShowAllBlockLevels) {
				for (int j = 0; j <= CoreProps.LEVEL_MAX; j++) {
					items.add(itemBlock.setDefaultTag(new ItemStack(this), j));
				}
			} else {
				items.add(itemBlock.setDefaultTag(new ItemStack(this), TEProps.creativeTabLevel));
			}
			if (TEProps.creativeTabShowCreative) {
				items.add(itemBlock.setCreativeTag(new ItemStack(this)));
			}
		}
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {

		return new TileTank();
	}

	/* BLOCK METHODS */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {

		if (stack.getTagCompound() != null) {
			TileTank tile = (TileTank) world.getTileEntity(pos);

			tile.isCreative = (stack.getTagCompound().getBoolean("Creative"));
			tile.enchantHolding = (byte) MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, stack), 0, CoreEnchantments.holding.getMaxLevel());
			tile.setLevel(stack.getTagCompound().getByte("Level"));

			if (stack.getTagCompound().hasKey(CoreProps.FLUID)) {
				FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag(CoreProps.FLUID));
				tile.getTank().setFluid(fluid);
				tile.setLocked(stack.getTagCompound().getBoolean("Lock"));
			}
		}
		super.onBlockPlacedBy(world, pos, state, living, stack);
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {

		return true;
	}

	@Override
	public boolean isFullCube(IBlockState state) {

		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {

		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {

		return false;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {

		return side == EnumFacing.UP || side == EnumFacing.DOWN;
	}

	@Override
	public boolean onBlockActivatedDelegate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

		TileTank tile = (TileTank) world.getTileEntity(pos);

		if (tile == null || !tile.canPlayerAccess(player)) {
			return false;
		}
		if (ItemHelper.isPlayerHoldingNothing(player)) {
			if (player.isSneaking()) {
				tile.setLocked(!tile.isLocked());
				if (tile.isLocked()) {
					world.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.2F, 0.8F);
				} else {
					world.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.3F, 0.5F);
				}
				return true;
			}
		}
		ItemStack heldItem = player.getHeldItem(hand);
		IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

		if (FluidHelper.isFluidHandler(heldItem)) {
			FluidHelper.interactWithHandler(heldItem, handler, player, hand);
			return true;
		}
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

		return new AxisAlignedBB(0.125F, 0F, 0.125F, 0.875F, 1F, 0.875F);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {

		return BlockFaceShape.UNDEFINED;
	}

	/* HELPERS */
	@Override
	public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		NBTTagCompound retTag = super.getItemStackTag(world, pos);
		TileTank tile = (TileTank) world.getTileEntity(pos);

		if (tile != null) {
			if (tile.enchantHolding > 0) {
				CoreEnchantments.addEnchantment(retTag, CoreEnchantments.holding, tile.enchantHolding);
			}
			FluidStack fluid = tile.getTankFluid();
			if (fluid != null) {
				retTag.setTag(CoreProps.FLUID, fluid.writeToNBT(new NBTTagCompound()));
				retTag.setBoolean("Lock", tile.isLocked());
			}
		}
		return retTag;
	}

	/* RENDERING METHODS */
	@Override
	@SideOnly (Side.CLIENT)
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {

		return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {

		return ModelBakery.handleExtendedState((IExtendedBlockState) super.getExtendedState(state, world, pos), world, pos);
	}

	/* IBakeryProvider */
	@Override
	@SideOnly (Side.CLIENT)
	public IBakery getBakery() {

		return BakeryTank.INSTANCE;
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		StateMapper mapper = new StateMapper("thermalexpansion", "tank", "tank");
		ModelLoader.setCustomModelResourceLocation(itemBlock, 0, mapper.location);
		ModelLoader.setCustomStateMapper(this, mapper);
		ModelLoader.setCustomMeshDefinition(itemBlock, mapper);
		ModelRegistryHelper.register(mapper.location, new CCBakeryModel());

		ModelBakery.registerBlockKeyGenerator(this, state -> {

			TileTank tank = state.getValue(TEProps.TILE_TANK);
			StringBuilder builder = new StringBuilder(ModelBakery.defaultBlockKeyGenerator.generateKey(state));
			builder.append(",creative=").append(tank.isCreative);
			builder.append(",level=").append(tank.getLevel());
			builder.append(",holding=").append(tank.enchantHolding);
			builder.append(",output=").append(tank.getTransferOut());
			builder.append(",lock=").append(tank.isLocked());
			FluidStack stack = tank.getTankFluid();

			if (stack != null) {
				builder.append(",fluid=").append(stack.hashCode());
				builder.append(",amount=").append(stack.amount);
			}
			return builder.toString();
		});

		ModelBakery.registerItemKeyGenerator(itemBlock, stack -> {

			String fluidAppend = "";
			if (stack.getTagCompound() != null) {

				FluidStack fluid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag(CoreProps.FLUID));
				if (fluid != null && fluid.amount > 0) {
					fluidAppend = ",fluid=" + fluid.getFluid().getName() + ",amount=" + fluid.amount;
				}
			}
			return ModelBakery.defaultItemKeyGenerator.generateKey(stack) + ",creative=" + itemBlock.isCreative(stack) + ",level=" + itemBlock.getLevel(stack) + fluidAppend;
		});
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		this.setRegistryName("tank");
		ForgeRegistries.BLOCKS.register(this);

		itemBlock = new ItemBlockTank(this);
		itemBlock.setRegistryName(this.getRegistryName());
		ForgeRegistries.ITEMS.register(itemBlock);

		TileTank.initialize();

		ThermalExpansion.proxy.addIModelRegister(this);

		return true;
	}

	@Override
	public boolean initialize() {

		tank = new ItemStack[5];

		for (int i = 0; i < 5; i++) {
			tank[i] = itemBlock.setDefaultTag(new ItemStack(this), i);
		}
		tankCreative = itemBlock.setCreativeTag(new ItemStack(this));

		addRecipes();
		addUpgradeRecipes();
		addClassicRecipes();

		return true;
	}

	/* HELPERS */
	private void addRecipes() {

		// @formatter:off
		if (enable) {
			addShapedRecipe(tank[0],
					" I ",
					"ICI",
					" P ",
					'C', "ingotCopper",
					'I', "blockGlass",
					'P', ItemMaterial.redstoneServo
			);
		}
		// @formatter:on
	}

	private void addUpgradeRecipes() {

		if (!enableUpgradeKitCrafting || !enable) {
			return;
		}
		if (!enableClassicRecipes) {
			for (int j = 0; j < 4; j++) {
				addShapelessUpgradeKitRecipe(tank[j + 1], tank[j], ItemUpgrade.upgradeIncremental[j]);
			}
			for (int j = 1; j < 4; j++) {
				for (int k = 0; k <= j; k++) {
					addShapelessUpgradeKitRecipe(tank[j + 1], tank[k], ItemUpgrade.upgradeFull[j]);
				}
			}
		}
		for (int j = 0; j < 5; j++) {
			addShapelessUpgradeKitRecipe(tankCreative, tank[j], ItemUpgrade.upgradeCreative);
		}
	}

	private void addClassicRecipes() {

		if (!enableClassicRecipes || !enable) {
			return;
		}
		// @formatter:off
		addShapedRecipe(tank[1],
				"YIY",
				"ICI",
				"YPY",
				'C', "ingotCopper",
				'I', "blockGlass",
				'P', ItemMaterial.redstoneServo,
				'Y', "ingotInvar"
		);
		addShapedUpgradeRecipe(tank[1],
				" I ",
				"ICI",
				" I ",
				'C', tank[0],
				'I', "ingotInvar"
		);
		addShapedUpgradeRecipe(tank[2],
				"YIY",
				"ICI",
				"YIY",
				'C', tank[1],
				'I', "ingotElectrum",
				'Y', "blockGlassHardened"
		);
		addShapedUpgradeRecipe(tank[3],
				" I ",
				"ICI",
				" I ",
				'C', tank[2],
				'I', "ingotSignalum"
		);
		addShapedUpgradeRecipe(tank[4],
				" I ",
				"ICI",
				" I ",
				'C', tank[3],
				'I', "ingotEnderium"
		);
		// @formatter:on
	}

	public static boolean enable = true;
	public static boolean enableCreative = true;
	public static boolean enableSecurity = true;

	public static boolean enableClassicRecipes = false;
	public static boolean enableUpgradeKitCrafting = false;

	/* REFERENCES */
	public static ItemStack tank[];
	public static ItemStack tankCreative;
	public static ItemBlockTank itemBlock;

}
