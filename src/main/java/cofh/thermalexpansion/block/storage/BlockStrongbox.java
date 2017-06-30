package cofh.thermalexpansion.block.storage;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.texture.IWorldBlockTextureProvider;
import codechicken.lib.texture.TextureUtils;
import cofh.core.init.CoreEnchantments;
import cofh.core.render.IModelRegister;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.render.RenderStrongbox;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
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

import static cofh.lib.util.helpers.RecipeHelper.addShapedRecipe;

public class BlockStrongbox extends BlockTEBase implements IModelRegister, IWorldBlockTextureProvider {

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
			if (TEProps.creativeTabShowAllLevels) {
				for (int j = 0; j < 5; j++) {
					items.add(itemBlock.setDefaultTag(new ItemStack(this, 1, 0), j));
				}
			} else {
				items.add(itemBlock.setDefaultTag(new ItemStack(this, 1, 0), TEProps.creativeTabLevel));
			}
			if (TEProps.creativeTabShowCreative) {
				items.add(itemBlock.setCreativeTag(new ItemStack(this, 1, 0), 4));
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
			tile.enchantHolding = (byte) EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, stack);
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

	@Override // Inventory
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getTexture(EnumFacing side, ItemStack stack) {

		return TextureUtils.getMissingSprite();
	}

	@Override // World
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getTexture(EnumFacing side, IBlockState state, BlockRenderLayer layer, IBlockAccess world, BlockPos pos) {

		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileStrongbox) {
			TileStrongbox tile = ((TileStrongbox) tileEntity);
			return tile.getBreakTexture();
		}
		return TextureUtils.getMissingSprite();
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		ModelResourceLocation location = new ModelResourceLocation(getRegistryName(), "normal");
		ModelLoader.setCustomModelResourceLocation(itemBlock, 0, location);//Suppresses model loading errors for #inventory.
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

		ThermalExpansion.proxy.addIModelRegister(this);

		return true;
	}

	@Override
	public boolean initialize() {

		TileStrongbox.initialize();

		strongbox = new ItemStack[5];

		for (int i = 0; i < 5; i++) {
			strongbox[i] = itemBlock.setDefaultTag(new ItemStack(this), i);
		}
		addRecipes();

		return true;
	}

	@Override
	public boolean postInit() {

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

	public static boolean enable;

	/* REFERENCES */
	public static ItemStack strongbox[];
	public static ItemBlockStrongbox itemBlock;

}
