package cofh.thermalexpansion.block.light;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;

import codechicken.lib.block.property.unlisted.UnlistedBooleanProperty;
import codechicken.lib.block.property.unlisted.UnlistedIntegerProperty;
import codechicken.lib.item.ItemStackRegistry;
import cofh.api.block.IBlockConfigGui;
import cofh.core.render.IconRegistry;
import cofh.lib.util.helpers.ColorHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.SwapYZ;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Vector3;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.block.ender.BlockEnder;
import cofh.thermalexpansion.block.ender.BlockEnder.Types;
import cofh.thermalexpansion.block.simple.BlockFrame;
import cofh.thermalexpansion.client.IBlockLayerProvider;
import cofh.thermalexpansion.client.bakery.BlockBakery;
import cofh.thermalexpansion.client.bakery.IBakeryBlock;
import cofh.thermalexpansion.client.bakery.ICustomBlockBakery;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.render.RenderLight;
import cofh.thermalexpansion.render.transformation.TorchTransformation;
import cofh.thermalexpansion.util.crafting.RecipeStyle;
import cofh.thermalexpansion.util.crafting.TransposerManager;
import cofh.thermalfoundation.fluid.TFFluids;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockStateContainer.Builder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Locale;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class BlockLight extends BlockTEBase implements IBlockConfigGui, IBakeryBlock, IBlockLayerProvider {

	public static Cuboid6[] models;

	static {

		final double d1 = 0;
		models = new Cuboid6[9];
		int i = 0;
		{ // full block
			models[i++] = new Cuboid6(d1, d1, d1, 1 - d1, 1 - d1, 1 - d1);
		}
		{ // flat lamp
			double d4 = 1. / 16, d5 = 15. / 16, d6 = 2. / 16;
			models[i++] = new Cuboid6(d4 + d1, d1, d4 + d1, d5 - d1, d6 - d1, d5 - d1);
		}
		{ // button lamp
			double d4 = 0.5 - 2. / 16, d5 = 0.5 + 2. / 16, d6 = 2. / 16;
			models[i++] = new Cuboid6(d4 + d1, d1, d4 + d1, d5 - d1, d6 - d1, d5 - d1);
		}
		{ // tall lamp
			double d4 = 0.5 - 3. / 16, d5 = 0.5 + 3. / 16, d6 = 7. / 16;
			models[i++] = new Cuboid6(d4 + d1, d1, d4 + d1, d5 - d1, d6 - d1, d5 - d1);
		}
		{ // wide lamp
			double d4 = 0.5 - 2. / 16, d5 = 0.5 + 2. / 16, d6 = 2. / 16;
			models[i++] = new Cuboid6(d4 + d1, d1, d1, d5 - d1, d6 - d1, 1 - d1);
		}
		{ // torch lamp
			double d4 = 0.5 - 1. / 16, d5 = 0.5 + 1. / 16, d6 = 10. / 16;
			models[i++] = new Cuboid6(d4 + d1, d1, d4 + d1, d5 - d1, d6 - d1, d5 - d1);
		}
		{ // display lamp
			double d4 = 0.5 - 2. / 16, d5 = 0.5 + 2. / 16, d6 = 2. / 16;
			models[i++] = new Cuboid6(d4 + d1, d1, 4. / 16 + d1, d5 - d1, d6 - d1, 12. / 16 - d1);
		}
		{ // pole lamp
			double d4 = 0.5 - 2. / 16, d5 = 0.5 + 2. / 16, d6 = 1;
			models[i++] = new Cuboid6(d4 + d1, d1, d4 + d1, d5 - d1, d6 - d1, d5 - d1);
		}
		{ // slab lamp
			double d4 = 0, d5 = 1, d6 = 0.5;
			models[i++] = new Cuboid6(d4 + d1, d1, d4 + d1, d5 - d1, d6 - d1, d5 - d1);
		}
	}

	public static Transformation getTransformation(int style, int alignment) {

		Transformation ret = TorchTransformation.sideTransformations[0];
		switch (style) {
		case 1: // plate
		case 2: // button
		case 3: // tall
		case 7: // pole
		case 8: // slab
			ret = Rotation.sideRotations[alignment].at(Vector3.center);
			break;
		case 4: // wide
		case 6: // display
			ret = Rotation.quarterRotations[alignment >> 3].with(Rotation.sideRotations[alignment & 7]).at(Vector3.center);
			break;
		case 5: // torch
			ret = TorchTransformation.sideTransformations[alignment];
			break;
		case 0:
		default:
			break;
		}

		return ret;
	}

	public static void setTileAlignment(TileLight tile, EntityPlayer player, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {

		switch (tile.style) {
		case 1: // plate
		case 2: // button
		case 3: // tall
		case 7: // pole
		case 8: // slab
			tile.alignment = (byte) side.ordinal();
			break;
		case 4: // wide
		case 6: // display
			int l = MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5f) & 1;
			tile.alignment = (byte) (side.ordinal() | (l << 3));
			break;
		case 5: // torch
			tile.alignment = (byte) side.ordinal();
			break;
		case 0:
		default:
			break;
		}
	}

	private static void addRecipes(ItemStack lamp) {

		GameRegistry.addRecipe(new RecipeStyle(2, 1, lamp, 0, ItemBlockLight.setDefaultTag(ItemHelper.cloneStack(lamp, 2), 1))); // plate
		GameRegistry.addRecipe(new RecipeStyle(1, 1, lamp, 0, ItemBlockLight.setDefaultTag(ItemHelper.cloneStack(lamp, 2), 2))); // button
		GameRegistry.addRecipe(new RecipeStyle(1, 2, lamp, 2, ItemBlockLight.setDefaultTag(ItemHelper.cloneStack(lamp, 2), 3))); // tall
		GameRegistry.addRecipe(new RecipeStyle(3, 1, lamp, 7, ItemBlockLight.setDefaultTag(ItemHelper.cloneStack(lamp, 3), 4))); // wide
		GameRegistry.addRecipe(new RecipeStyle(1, 2, lamp, 0, ItemBlockLight.setDefaultTag(ItemHelper.cloneStack(lamp, 4), 5))); // torch
		GameRegistry.addRecipe(new RecipeStyle(2, 1, lamp, 2, ItemBlockLight.setDefaultTag(ItemHelper.cloneStack(lamp, 2), 6))); // display
		GameRegistry.addRecipe(new RecipeStyle(1, 3, lamp, 0, ItemBlockLight.setDefaultTag(ItemHelper.cloneStack(lamp, 3), 7))); // pole
		GameRegistry.addRecipe(new RecipeStyle(3, 1, lamp, 0, ItemBlockLight.setDefaultTag(ItemHelper.cloneStack(lamp, 6), 8))); // slab
	}

	public static final PropertyEnum<Types> TYPES = PropertyEnum.create("type", Types.class);

    public static final UnlistedIntegerProperty COLOUR_MULTIPLIER_PROPERTY = new UnlistedIntegerProperty("colour_multiplier");
    public static final UnlistedIntegerProperty STYLE_PROPERTY = new UnlistedIntegerProperty("style");
    public static final UnlistedIntegerProperty ALIGNMENT_PROPERTY = new UnlistedIntegerProperty("alignment");
    public static final UnlistedBooleanProperty MODIFIED_PROPERTY = new UnlistedBooleanProperty("modified");


	public BlockLight() {

		super(Material.REDSTONE_LIGHT);
		setHardness(3.0F);
		setResistance(150.0F);
		setSoundType(SoundType.GLASS);
		setUnlocalizedName("thermalexpansion.light");
		basicGui = false;
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
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return BlockBakery.handleExtendedState((IExtendedBlockState) state, world.getTileEntity(pos));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        ExtendedBlockState.Builder builder = new Builder(this);
        builder.add(TYPES);
        builder.add(BlockBakery.TYPE_PROPERTY);
        builder.add(COLOUR_MULTIPLIER_PROPERTY);
        builder.add(STYLE_PROPERTY);
        builder.add(ALIGNMENT_PROPERTY);
        builder.add(MODIFIED_PROPERTY);
        builder.add(BlockBakery.ACTIVE_PROPERTY);
        return builder.build();
    }

    @Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		return new TileLight();
	}

	@Override
	public boolean openConfigGui(IBlockAccess world, BlockPos pos, EnumFacing side, EntityPlayer player) {

		return ((TileLight) world.getTileEntity(pos)).openGui(player);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {

		for (int i = 0; i < Types.values().length; i++) {
			for (byte j = 0; j < models.length; ++j) {
				list.add(ItemBlockLight.setDefaultTag(new ItemStack(item, 1, i), j));
			}
		}
	}

	//@Override
	public AxisAlignedBB getBoundingBox(World world, BlockPos pos) {

		TileLight tile = (TileLight) world.getTileEntity(pos);
		switch (tile.style) {
		case 2:
		case 5:
			return null;
		}
		Cuboid6 ret = models[tile.style].copy().apply(getTransformation(tile.style, tile.alignment));
		return ret.aabb();
	}

	/*@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {

		TileLight tile = (TileLight) world.getTileEntity(x, y, z);
		Cuboid6 ret = models[tile.style].copy().apply(getTransformation(tile.style, tile.alignment));
		switch (tile.style) {
		case 5:
			double m = 0;
			switch (tile.alignment) {
			case 3:
				m = 0.12;
			case 2:
				ret.expand(new Vector3(0.1, 0, m));
				break;
			case 5:
				m = 0.12;
			case 4:
				ret.expand(new Vector3(m, 0, 0.1));
				break;
			default:
				ret.expand(0.05);
			}
		}
		ret.apply(new SwapYZ() {

			@Override
			public void apply(Vector3 vec) {
				vec.x = MathHelper.clamp(vec.x, 0, 1);
				vec.y = MathHelper.clamp(vec.y, 0, 1);
				vec.z = MathHelper.clamp(vec.z, 0, 1);
			}

		}).setBlockBounds(this);
	}*/

    @Override
    public boolean canReplace(World world, BlockPos pos, EnumFacing side, @Nullable ItemStack stack) {
		if (super.canReplace(world, pos, side, stack)) {
			if (stack.getTagCompound() != null) {
				@SuppressWarnings("unused")
				int style = stack.getTagCompound().getByte("Style");
			}
			return true;
		}
		return false;
	}

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {
		if (stack.getTagCompound() != null) {
			TileLight tile = (TileLight) world.getTileEntity(pos);

			if (stack.getTagCompound().hasKey("Color")) {
				tile.modified = true;
				tile.setColor(stack.getTagCompound().getInteger("Color"));
			}
			tile.dim = stack.getTagCompound().getBoolean("Dim");
			tile.mode = stack.getTagCompound().getByte("Mode");
			tile.style = stack.getTagCompound().getByte("Style");
		}
		super.onBlockPlacedBy(world, pos, state, living, stack);
	}

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {

	}

    @Override
    public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color) {
		TileLight theTile = (TileLight) world.getTileEntity(pos);

		if (ServerHelper.isServerWorld(world)) {
			return theTile.setColor(ColorHelper.getDyeColor(15 - color.ordinal()));
		}
		return false;
	}

    @Override
    public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {

		return false;
	}

	//@Override
	public int getRenderType() {

		return TEProps.renderIdLight;
	}

	@SideOnly(Side.CLIENT)
    @Override
    public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos) {
		TileLight tile = (TileLight) source.getTileEntity(pos);
		return source.getCombinedLight(pos, tile.getInternalLight());
	}

    @Override
    public int getTexturePasses() {
        return 2;
    }

    @Override
    public BlockRenderLayer getRenderlayerForPass(int pass) {
        return pass >= 1 ? BlockRenderLayer.CUTOUT : BlockRenderLayer.SOLID;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}

	//@Override
	//@SideOnly(Side.CLIENT)
	//public void registerBlockIcons(IIconRegister ir) {
    //
	//	IconRegistry.addIcon("Light0", "thermalexpansion:light/Illuminator_Frame", ir);
	//	IconRegistry.addIcon("Light1", "thermalexpansion:light/Lamp_Effect", ir);
	//	IconRegistry.addIcon("LightEffect", "thermalexpansion:light/Illuminator_Effect", ir);
	//	IconRegistry.addIcon("LightHalo", "thermalexpansion:light/Lamp_Halo", ir);
	//}

	@Override
	public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		NBTTagCompound tag = super.getItemStackTag(world, pos);
		TileLight tile = (TileLight) world.getTileEntity(pos);

		if (tag == null) {
			tag = new NBTTagCompound();
		}
		if (tile.modified) {
			tag.setInteger("Color", tile.color);
		}
		if (tile.dim) {
			tag.setBoolean("Dim", tile.dim);
		}
		if (tile.mode != 0) {
			tag.setByte("Mode", tile.mode);
		}
		tag.setByte("Style", tile.style);
		return tag;
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		TileLight.initialize();
		TileLightFalse.initialize();

		illuminator = ItemBlockLight.setDefaultTag(new ItemStack(this, 1, 0), 0);
		lampLumiumRadiant = ItemBlockLight.setDefaultTag(new ItemStack(this, 1, 1), 0);
		lampLumium = ItemBlockLight.setDefaultTag(new ItemStack(this, 1, 2), 0);

        ItemStackRegistry.registerCustomItemStack("illuminator", illuminator);
		ItemStackRegistry.registerCustomItemStack("lampLumiumRadiant", lampLumiumRadiant);
        ItemStackRegistry.registerCustomItemStack("lampLumium", lampLumium);

		return true;
	}

	@Override
	public boolean postInit() {

		if (enable[Types.ILLUMINATOR.ordinal()]) {
			TransposerManager.addTEFillRecipe(2000, BlockFrame.frameIlluminator, illuminator, new FluidStack(TFFluids.fluidGlowstone, 500), false);
			addRecipes(illuminator);
		}
		if (enable[Types.LAMP_LUMIUM_RADIANT.ordinal()]) {
			GameRegistry.addRecipe(ShapedRecipe(ItemHelper.cloneStack(lampLumiumRadiant, 4), " L ", "GLG", " S ", 'L', "ingotLumium", 'G', "blockGlassHardened", 'S', "ingotSignalum"));
			addRecipes(lampLumiumRadiant);
		}
		if (enable[Types.LAMP_LUMIUM.ordinal()]) {
			GameRegistry.addRecipe(ShapedRecipe(ItemHelper.cloneStack(lampLumium, 4), " L ", "GLG", " S ", 'L', "dustLumium", 'G', "blockGlassHardened", 'S', "ingotSignalum"));
			addRecipes(lampLumium);
		}
		return true;
	}

    @Override
    public ICustomBlockBakery getCustomBakery() {
        return RenderLight.instance;
    }

    public enum Types implements IStringSerializable {
		ILLUMINATOR,
        LAMP_LUMIUM_RADIANT,
        LAMP_LUMIUM;

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

        public static Types getType(int meta) {

			Types[] types = values();
			return types[meta % types.length];
		}
	}

	public static final String[] NAMES = { "illuminator", "lampLumiumRadiant", "lampLumium" };
	public static boolean[] enable = new boolean[Types.values().length];

	static {
		String category = "Light.";

		for (int i = 0; i < Types.values().length; i++) {
			enable[i] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[i]), "Recipe.Enable", true);
		}
	}

	public static ItemStack illuminator;
	public static ItemStack lampLumiumRadiant;
	public static ItemStack lampLumium;

}
