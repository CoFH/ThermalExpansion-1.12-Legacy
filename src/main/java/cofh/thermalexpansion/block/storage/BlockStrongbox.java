package cofh.thermalexpansion.block.storage;

import codechicken.lib.model.ModelRegistryHelper;
import cofh.core.init.CoreEnchantments;
import cofh.core.init.CoreProps;
import cofh.core.render.IModelRegister;
import cofh.core.util.helpers.MathHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.render.RenderStrongbox;
import cofh.thermalfoundation.item.ItemUpgrade;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;

import static cofh.core.util.helpers.RecipeHelper.*;

public class BlockStrongbox extends BlockTEBase implements IModelRegister {

	public BlockStrongbox() {

		super(Material.IRON);

		setUnlocalizedName("strongbox");

		setHardness(15.0F);
		setResistance(25.0F);
		setDefaultState(getBlockState().getBaseState());
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

	/* ITileEntityProvider */
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		return new TileStrongbox();
	}

	/* BLOCK METHODS */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {

		if (stack.getTagCompound() != null) {
			TileStrongbox tile = (TileStrongbox) world.getTileEntity(pos);

			tile.isCreative = (stack.getTagCompound().getBoolean("Creative"));
			tile.enchantHolding = (byte) MathHelper.clamp(EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, stack), 0, CoreEnchantments.holding.getMaxLevel());
			tile.setLevel(stack.getTagCompound().getByte("Level"));

			if (stack.getTagCompound().hasKey("Inventory")) {
				tile.readInventoryFromNBT(stack.getTagCompound());
			}
		}
		super.onBlockPlacedBy(world, pos, state, living, stack);
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
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

		return new AxisAlignedBB(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {

		return BlockFaceShape.UNDEFINED;
	}

	/* HELPERS */
	@Override
	public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		NBTTagCompound retTag = super.getItemStackTag(world, pos);
		TileStrongbox tile = (TileStrongbox) world.getTileEntity(pos);

		if (tile != null) {
			if (tile.enchantHolding > 0) {
				CoreEnchantments.addEnchantment(retTag, CoreEnchantments.holding, tile.enchantHolding);
			}
			tile.writeInventoryToNBT(retTag);
		}
		return retTag;
	}

	/* IDismantleable */
	@Override
	public ArrayList<ItemStack> dismantleBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player, boolean returnDrops) {

		NBTTagCompound retTag = getItemStackTag(world, pos);

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileStrongbox) {
			TileStrongbox strongbox = (TileStrongbox) tile;
			strongbox.inventory = new ItemStack[strongbox.inventory.length];
			Arrays.fill(strongbox.inventory, ItemStack.EMPTY);
		}
		return dismantleDelegate(retTag, world, pos, player, returnDrops, false);
	}

	/* RENDERING METHODS */
	@Override
	@SideOnly (Side.CLIENT)
	public EnumBlockRenderType getRenderType(IBlockState state) {

		return EnumBlockRenderType.INVISIBLE;
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		ModelResourceLocation location = new ModelResourceLocation(getRegistryName(), "normal");
		ModelLoader.setCustomModelResourceLocation(itemBlock, 0, location); // Suppresses model loading errors for #inventory.
		ModelLoader.setCustomMeshDefinition(itemBlock, (stack) -> location);
		ModelRegistryHelper.register(location, RenderStrongbox.INSTANCE);
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		this.setRegistryName("strongbox");
		ForgeRegistries.BLOCKS.register(this);

		itemBlock = new ItemBlockStrongbox(this);
		itemBlock.setRegistryName(this.getRegistryName());
		ForgeRegistries.ITEMS.register(itemBlock);

		TileStrongbox.initialize();

		ThermalExpansion.proxy.addIModelRegister(this);

		return true;
	}

	@Override
	public boolean initialize() {

		strongbox = new ItemStack[5];

		for (int i = 0; i < 5; i++) {
			strongbox[i] = itemBlock.setDefaultTag(new ItemStack(this), i);
		}
		strongboxCreative = itemBlock.setCreativeTag(new ItemStack(this));

		addRecipes();
		addUpgradeRecipes();
		addClassicRecipes();

		return true;
	}

	/* HELPERS */
	private void addRecipes() {

		// @formatter:off
		if (enable) {
			addShapedRecipe(strongbox[0],
					" I ",
					"ICI",
					" I ",
					'C', "chestWood",
					'I', "ingotTin"
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
				addShapelessUpgradeKitRecipe(strongbox[j + 1], strongbox[j], ItemUpgrade.upgradeIncremental[j]);
			}
			for (int j = 1; j < 4; j++) {
				for (int k = 0; k <= j; k++) {
					addShapelessUpgradeKitRecipe(strongbox[j + 1], strongbox[k], ItemUpgrade.upgradeFull[j]);
				}
			}
		}
		for (int j = 0; j < 5; j++) {
			addShapelessUpgradeKitRecipe(strongboxCreative, strongbox[j], ItemUpgrade.upgradeCreative);
		}
	}

	private void addClassicRecipes() {

		if (!enableClassicRecipes || !enable) {
			return;
		}
		// @formatter:off
		addShapedRecipe(strongbox[1],
				"YIY",
				"ICI",
				"YIY",
				'C', "chestWood",
				'I', "ingotTin",
				'Y', "ingotInvar"
		);
		addShapedUpgradeRecipe(strongbox[1],
				" I ",
				"ICI",
				" I ",
				'C', strongbox[0],
				'I', "ingotInvar"
		);
		addShapedUpgradeRecipe(strongbox[2],
				"YIY",
				"ICI",
				"YIY",
				'C', strongbox[1],
				'I', "ingotElectrum",
				'Y', "blockGlassHardened"
		);
		addShapedUpgradeRecipe(strongbox[3],
				" I ",
				"ICI",
				" I ",
				'C', strongbox[2],
				'I', "ingotSignalum"
		);
		addShapedUpgradeRecipe(strongbox[4],
				" I ",
				"ICI",
				" I ",
				'C', strongbox[3],
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
	public static ItemStack strongbox[];
	public static ItemStack strongboxCreative;
	public static ItemBlockStrongbox itemBlock;

}
