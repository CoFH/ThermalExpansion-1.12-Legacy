package thermalexpansion.block.dynamo;

import cofh.render.IconRegistry;
import cofh.util.BlockHelper;
import cofh.util.FluidHelper;
import cofh.util.ItemHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.ShapedOreRecipe;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.BlockTEBase;
import thermalexpansion.core.TEProps;
import thermalexpansion.item.TEAugments;
import thermalexpansion.item.TEItems;
import thermalexpansion.util.crafting.TECraftingHandler;

public class BlockDynamo extends BlockTEBase {

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

		TileDynamoBase tile = (TileDynamoBase) world.getTileEntity(x, y, z);

		if (tile == null) {
			return false;
		}
		return tile.facing == BlockHelper.SIDE_OPPOSITE[side.ordinal()];
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {

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
			GameRegistry.addRecipe(new ShapedOreRecipe(dynamoSteam, new Object[] { " C ", "GIG", "IRI", 'C', TEItems.powerCoilSilver, 'G', "gearCopper", 'I',
					"ingotCopper", 'R', "dustRedstone" }));
		}
		if (enable[Types.MAGMATIC.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(dynamoMagmatic, new Object[] { " C ", "GIG", "IRI", 'C', TEItems.powerCoilSilver, 'G', "gearInvar", 'I',
					"ingotInvar", 'R', "dustRedstone" }));
		}
		if (enable[Types.COMPRESSION.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(dynamoCompression, new Object[] { " C ", "GIG", "IRI", 'C', TEItems.powerCoilSilver, 'G', "gearTin",
					'I', "ingotTin", 'R', "dustRedstone" }));
		}
		if (enable[Types.REACTANT.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(dynamoReactant, new Object[] { " C ", "GIG", "IRI", 'C', TEItems.powerCoilSilver, 'G', "gearBronze",
					'I', "ingotBronze", 'R', "dustRedstone" }));
		}
		if (enable[Types.ENERVATION.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(dynamoEnervation, new Object[] { " C ", "GIG", "IRI", 'C', TEItems.powerCoilSilver, 'G', "gearElectrum",
					'I', "ingotElectrum", 'R', "dustRedstone" }));
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

		GameRegistry.registerCustomItemStack("dynamoSteam", dynamoSteam);
		GameRegistry.registerCustomItemStack("dynamoMagmatic", dynamoMagmatic);
		GameRegistry.registerCustomItemStack("dynamoCompression", dynamoCompression);
		GameRegistry.registerCustomItemStack("dynamoReactant", dynamoReactant);
		GameRegistry.registerCustomItemStack("dynamoEnervation", dynamoEnervation);
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
