package thermalexpansion.block.dynamo;

import cofh.render.IconRegistry;
import cofh.util.BlockHelper;
import cofh.util.EnergyHelper;
import cofh.util.FluidHelper;
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
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.ShapedOreRecipe;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.BlockTEBase;
import thermalexpansion.core.TEProps;
import thermalexpansion.item.TEItems;
import thermalexpansion.util.Utils;

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
		default:
			return null;
		}
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {

		for (int i = 0; i < Types.values().length; ++i) {
			if (enable[i]) {
				list.add(new ItemStack(item, 1, i));
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int hitSide, float hitX, float hitY, float hitZ) {

		Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
		TileDynamoBase tile = (TileDynamoBase) world.getTileEntity(x, y, z);

		if (tile instanceof IFluidHandler) {
			if (FluidHelper.fillHandlerWithContainer(world, (IFluidHandler) tile, player)) {
				return true;
			}
		} else if (tile instanceof TileDynamoManual) {
			if (Utils.isHoldingNothing(player)) {
				tile.receiveEnergy(ForgeDirection.UNKNOWN, 1000, false);
				player.getFoodStats().addStats(-1, -0.2F);
			} else if (EnergyHelper.isPlayerHoldingEnergyContainerItem(player)) {
				tile.receiveEnergy(ForgeDirection.UNKNOWN, EnergyHelper.extractEnergyFromHeldContainer(player, tile.receiveEnergy(ForgeDirection.UNKNOWN, 10000, true), false), false);
			}
		}
		return super.onBlockActivated(world, x, y, z, player, hitSide, hitX, hitY, hitZ);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {

		TileDynamoBase tile = (TileDynamoBase) world.getTileEntity(x, y, z);

		if (tile != null) {
			tile.rotateBlock();
		}
		super.onBlockPlacedBy(world, x, y, z, living, stack);
	}

	@Override
	public int getRenderBlockPass() {

		return 0;
	}

	@Override
	public boolean hasComparatorInputOverride() {

		return true;
	}

	@Override
	public int getRenderType() {

		return TEProps.renderIdDynamo;
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

		IconRegistry.addIcon("Dynamo" + 0, "thermalexpansion:dynamo/Dynamo_Steam", ir);
		IconRegistry.addIcon("Dynamo" + 1, "thermalexpansion:dynamo/Dynamo_Magmatic", ir);
		IconRegistry.addIcon("Dynamo" + 2, "thermalexpansion:dynamo/Dynamo_Compression", ir);
		IconRegistry.addIcon("Dynamo" + 3, "thermalexpansion:dynamo/Dynamo_Reactant", ir);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		TileDynamoSteam.initialize();
		TileDynamoMagmatic.initialize();
		TileDynamoCompression.initialize();
		TileDynamoReactant.initialize();

		dynamoSteam = new ItemStack(this, 1, Types.STEAM.ordinal());
		dynamoMagmatic = new ItemStack(this, 1, Types.MAGMATIC.ordinal());
		dynamoCompression = new ItemStack(this, 1, Types.COMPRESSION.ordinal());
		dynamoReactant = new ItemStack(this, 1, Types.REACTANT.ordinal());

		GameRegistry.registerCustomItemStack("dynamoSteam", dynamoSteam);
		GameRegistry.registerCustomItemStack("dynamoMagmatic", dynamoMagmatic);
		GameRegistry.registerCustomItemStack("dynamoCompression", dynamoCompression);
		GameRegistry.registerCustomItemStack("dynamoReactant", dynamoReactant);

		return true;
	}

	@Override
	public boolean postInit() {

		if (enable[Types.STEAM.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(dynamoSteam, new Object[] { " C ", "GIG", "IRI", 'C', TEItems.powerCoilSilver, 'G', "gearCopper", 'I', "ingotCopper", 'R', Items.redstone }));
		}
		if (enable[Types.MAGMATIC.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(dynamoMagmatic, new Object[] { " C ", "GIG", "IRI", 'C', TEItems.powerCoilSilver, 'G', "gearInvar", 'I', "ingotInvar", 'R', Items.redstone }));
		}
		if (enable[Types.COMPRESSION.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(dynamoCompression, new Object[] { " C ", "GIG", "IRI", 'C', TEItems.powerCoilSilver, 'G', "gearTin", 'I', "ingotTin", 'R', Items.redstone }));
		}
		if (enable[Types.REACTANT.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(dynamoReactant, new Object[] { " C ", "GIG", "IRI", 'C', TEItems.powerCoilSilver, 'G', "gearBronze", 'I', "ingotBronze", 'R', Items.redstone }));
		}
		return true;
	}

	public static enum Types {
		STEAM, MAGMATIC, COMPRESSION, REACTANT
	}

	public static final String[] NAMES = { "steam", "magmatic", "compression", "reactant" };
	public static boolean[] enable = new boolean[Types.values().length];

	static {
		String category = "block.feature";
		enable[Types.STEAM.ordinal()] = ThermalExpansion.config.get(category, "Dynamo.Steam", true);
		enable[Types.MAGMATIC.ordinal()] = ThermalExpansion.config.get(category, "Dynamo.Magmatic", true);
		enable[Types.COMPRESSION.ordinal()] = ThermalExpansion.config.get(category, "Dynamo.Compression", true);
		enable[Types.REACTANT.ordinal()] = ThermalExpansion.config.get(category, "Dynamo.Reactant", true);
	}

	public static ItemStack dynamoSteam;
	public static ItemStack dynamoMagmatic;
	public static ItemStack dynamoCompression;
	public static ItemStack dynamoReactant;

}
