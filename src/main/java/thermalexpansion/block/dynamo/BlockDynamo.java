package thermalexpansion.block.dynamo;

import cofh.core.render.IconRegistry;
import cofh.core.util.crafting.RecipeAugmentable;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.repack.codechicken.lib.vec.Cuboid6;
import cofh.repack.codechicken.lib.vec.Rotation;
import cofh.repack.codechicken.lib.vec.Vector3;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.BlockTEBase;
import thermalexpansion.core.TEProps;
import thermalexpansion.item.TEAugments;
import thermalexpansion.item.TEItems;
import thermalexpansion.util.crafting.TECraftingHandler;

public class BlockDynamo extends BlockTEBase {

	static AxisAlignedBB[] boundingBox = new AxisAlignedBB[12];
	static {
		Cuboid6 bb = new Cuboid6(0, 0, 0, 1, 10 / 16., 1);
		Vector3 p = new Vector3(0.5, 0.5, 0.5);
		boundingBox[1] = bb.toAABB();
		boundingBox[0] = bb.apply(Rotation.sideRotations[1].at(p)).toAABB();
		for (int i = 2; i < 6; ++i) {
			boundingBox[i] = bb.copy().apply(Rotation.sideRotations[i].at(p)).toAABB();
		}

		bb = new Cuboid6(.25, .5, .25, .75, 1, .75);
		boundingBox[1 + 6] = bb.toAABB();
		boundingBox[0 + 6] = bb.apply(Rotation.sideRotations[1].at(p)).toAABB();
		for (int i = 2; i < 6; ++i) {
			boundingBox[i + 6] = bb.copy().apply(Rotation.sideRotations[i].at(p)).toAABB();
		}
	}

	public BlockDynamo() {

		super(Material.iron);
		setHardness(15.0F);
		setResistance(25.0F);
		setBlockName("thermalexpansion.dynamo");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		if (metadata >= Types.values().length) {
			return null;
		}
		switch (Types.values()[metadata]) {
		case STEAM:
			return new TileDynamoSteam();
		case MAGMATIC:
			return new TileDynamoMagmatic();
		case COMPRESSION:
			return new TileDynamoCompression();
		case REACTANT:
			return new TileDynamoReactant();
		case ENERVATION:
			return new TileDynamoEnervation();
		default:
			return null;
		}
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB bb, List list, Entity entity) {

		int facing = ((TileDynamoBase) world.getTileEntity(x, y, z)).facing;

		AxisAlignedBB base, coil;
		base = boundingBox[facing].copy().offset(x, y, z);
		coil = boundingBox[facing + 6].copy().offset(x, y, z);

		if (coil.intersectsWith(bb)) {
			list.add(coil);
		}

		if (base.intersectsWith(bb)) {
			list.add(base);
		}
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {

		for (int i = 0; i < Types.values().length; i++) {
			if (enable[i]) {
				list.add(ItemBlockDynamo.setDefaultTag(new ItemStack(item, 1, i)));
			}
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {

		TileDynamoBase tile = (TileDynamoBase) world.getTileEntity(x, y, z);

		tile.rotateBlock();

		if (stack.stackTagCompound != null) {
			tile.readAugmentsFromNBT(stack.stackTagCompound);
			tile.installAugments();
			tile.setEnergyStored(stack.stackTagCompound.getInteger("Energy"));
		}
		super.onBlockPlacedBy(world, x, y, z, living, stack);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int hitSide, float hitX, float hitY, float hitZ) {

		TileDynamoBase tile = (TileDynamoBase) world.getTileEntity(x, y, z);

		if (tile instanceof IFluidHandler) {
			if (FluidHelper.fillHandlerWithContainer(world, (IFluidHandler) tile, player)) {
				return true;
			}
		}
		return super.onBlockActivated(world, x, y, z, player, hitSide, hitX, hitY, hitZ);
	}

	@Override
	public int getRenderBlockPass() {

		return 0;
	}

	@Override
	public int getRenderType() {

		return TEProps.renderIdDynamo;
	}

	@Override
	public boolean hasComparatorInputOverride() {

		return true;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {

		TileEntity tile = world.getTileEntity(x, y, z);

		if (!(tile instanceof TileDynamoBase)) {
			return false;
		}
		TileDynamoBase theTile = (TileDynamoBase) tile;
		return theTile.facing == BlockHelper.SIDE_OPPOSITE[side.ordinal()];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		IconRegistry.addIcon("DynamoCoilRedstone", "thermalexpansion:dynamo/Dynamo_Coil_Redstone", ir);

		IconRegistry.addIcon("Dynamo" + Types.STEAM.ordinal(), "thermalexpansion:dynamo/Dynamo_Steam", ir);
		IconRegistry.addIcon("Dynamo" + Types.MAGMATIC.ordinal(), "thermalexpansion:dynamo/Dynamo_Magmatic", ir);
		IconRegistry.addIcon("Dynamo" + Types.COMPRESSION.ordinal(), "thermalexpansion:dynamo/Dynamo_Compression", ir);
		IconRegistry.addIcon("Dynamo" + Types.REACTANT.ordinal(), "thermalexpansion:dynamo/Dynamo_Reactant", ir);
		IconRegistry.addIcon("Dynamo" + Types.ENERVATION.ordinal(), "thermalexpansion:dynamo/Dynamo_Enervation", ir);
	}

	@Override
	public NBTTagCompound getItemStackTag(World world, int x, int y, int z) {

		NBTTagCompound tag = super.getItemStackTag(world, x, y, z);
		TileDynamoBase tile = (TileDynamoBase) world.getTileEntity(x, y, z);

		if (tile != null) {
			if (tag == null) {
				tag = new NBTTagCompound();
			}
			tag.setInteger("Energy", tile.getEnergyStored(ForgeDirection.UNKNOWN));
			tile.writeAugmentsToNBT(tag);
		}
		return tag;
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		TileDynamoBase.configure();
		TileDynamoSteam.initialize();
		TileDynamoMagmatic.initialize();
		TileDynamoCompression.initialize();
		TileDynamoReactant.initialize();
		TileDynamoEnervation.initialize();

		if (defaultRedstoneControl) {
			defaultAugments[0] = ItemHelper.cloneStack(TEAugments.generalRedstoneControl);
		}
		dynamoSteam = ItemBlockDynamo.setDefaultTag(new ItemStack(this, 1, Types.STEAM.ordinal()));
		dynamoMagmatic = ItemBlockDynamo.setDefaultTag(new ItemStack(this, 1, Types.MAGMATIC.ordinal()));
		dynamoCompression = ItemBlockDynamo.setDefaultTag(new ItemStack(this, 1, Types.COMPRESSION.ordinal()));
		dynamoReactant = ItemBlockDynamo.setDefaultTag(new ItemStack(this, 1, Types.REACTANT.ordinal()));
		dynamoEnervation = ItemBlockDynamo.setDefaultTag(new ItemStack(this, 1, Types.ENERVATION.ordinal()));

		GameRegistry.registerCustomItemStack("dynamoSteam", dynamoSteam);
		GameRegistry.registerCustomItemStack("dynamoMagmatic", dynamoMagmatic);
		GameRegistry.registerCustomItemStack("dynamoCompression", dynamoCompression);
		GameRegistry.registerCustomItemStack("dynamoReactant", dynamoReactant);
		GameRegistry.registerCustomItemStack("dynamoEnervation", dynamoEnervation);

		return true;
	}

	@Override
	public boolean postInit() {

		if (enable[Types.STEAM.ordinal()]) {
			GameRegistry.addRecipe(new RecipeAugmentable(dynamoSteam, defaultAugments, new Object[] { " C ", "GIG", "IRI", 'C', TEItems.powerCoilSilver, 'G',
					"gearCopper", 'I', "ingotCopper", 'R', "dustRedstone" }));
		}
		if (enable[Types.MAGMATIC.ordinal()]) {
			GameRegistry.addRecipe(new RecipeAugmentable(dynamoMagmatic, defaultAugments, new Object[] { " C ", "GIG", "IRI", 'C', TEItems.powerCoilSilver,
					'G', "gearInvar", 'I', "ingotInvar", 'R', "dustRedstone" }));
		}
		if (enable[Types.COMPRESSION.ordinal()]) {
			GameRegistry.addRecipe(new RecipeAugmentable(dynamoCompression, defaultAugments, new Object[] { " C ", "GIG", "IRI", 'C', TEItems.powerCoilSilver,
					'G', "gearTin", 'I', "ingotTin", 'R', "dustRedstone" }));
		}
		if (enable[Types.REACTANT.ordinal()]) {
			GameRegistry.addRecipe(new RecipeAugmentable(dynamoReactant, defaultAugments, new Object[] { " C ", "GIG", "IRI", 'C', TEItems.powerCoilSilver,
					'G', "gearBronze", 'I', "ingotBronze", 'R', "dustRedstone" }));
		}
		if (enable[Types.ENERVATION.ordinal()]) {
			GameRegistry.addRecipe(new RecipeAugmentable(dynamoEnervation, defaultAugments, new Object[] { " C ", "GIG", "IRI", 'C', TEItems.powerCoilSilver,
					'G', "gearElectrum", 'I', "ingotElectrum", 'R', "dustRedstone" }));
		}
		TECraftingHandler.addSecureRecipe(dynamoSteam);
		TECraftingHandler.addSecureRecipe(dynamoMagmatic);
		TECraftingHandler.addSecureRecipe(dynamoCompression);
		TECraftingHandler.addSecureRecipe(dynamoEnervation);
		TECraftingHandler.addSecureRecipe(dynamoReactant);

		return true;
	}

	public static void refreshItemStacks() {

		dynamoSteam = ItemBlockDynamo.setDefaultTag(dynamoSteam);
		dynamoMagmatic = ItemBlockDynamo.setDefaultTag(dynamoMagmatic);
		dynamoCompression = ItemBlockDynamo.setDefaultTag(dynamoCompression);
		dynamoReactant = ItemBlockDynamo.setDefaultTag(dynamoReactant);
		dynamoEnervation = ItemBlockDynamo.setDefaultTag(dynamoEnervation);
	}

	public static enum Types {
		STEAM, MAGMATIC, COMPRESSION, REACTANT, ENERVATION
	}

	public static final String[] NAMES = { "steam", "magmatic", "compression", "reactant", "enervation" };
	public static boolean[] enable = new boolean[Types.values().length];

	public static ItemStack[] defaultAugments = new ItemStack[4];

	public static boolean defaultRedstoneControl = true;

	static {
		String category = "block.feature";
		enable[Types.STEAM.ordinal()] = ThermalExpansion.config.get(category, "Dynamo.Steam", true);
		enable[Types.MAGMATIC.ordinal()] = ThermalExpansion.config.get(category, "Dynamo.Magmatic", true);
		enable[Types.COMPRESSION.ordinal()] = ThermalExpansion.config.get(category, "Dynamo.Compression", true);
		enable[Types.REACTANT.ordinal()] = ThermalExpansion.config.get(category, "Dynamo.Reactant", true);
		enable[Types.ENERVATION.ordinal()] = ThermalExpansion.config.get(category, "Dynamo.Enervation", true);
	}

	public static ItemStack dynamoSteam;
	public static ItemStack dynamoMagmatic;
	public static ItemStack dynamoCompression;
	public static ItemStack dynamoReactant;
	public static ItemStack dynamoEnervation;

}
