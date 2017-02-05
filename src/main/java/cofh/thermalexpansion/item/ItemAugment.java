package cofh.thermalexpansion.item;

import cofh.api.core.IInitializer;
import cofh.core.item.ItemMulti;
import cofh.thermalexpansion.ThermalExpansion;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemAugment extends ItemMulti implements IInitializer {

	public ItemAugment() {

		super("thermalexpansion");

		setUnlocalizedName("augment");
		setCreativeTab(ThermalExpansion.tabItems);
	}

	@SideOnly (Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {

	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {

		return true;
	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {

		return EnumActionResult.PASS;
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

	@Override
	public boolean postInit() {

		return true;
	}

	/* AUGMENT ENTRY */
	public class AugmentEntry {

		public String type;
		public int level;
		public boolean advanced;
	}

	/* REFERENCES */
	TIntObjectHashMap<AugmentEntry> augmentMap = new TIntObjectHashMap<AugmentEntry>();

	/* Fluid */
	public static ItemStack fluidStorage;

	/* Ender */
	public static ItemStack enderReception;
	public static ItemStack enderTransmission;

	/* Energy */
	public static ItemStack energyStorage;
	public static ItemStack energyTransfer;

	/* Dynamo */
	public static ItemStack dynamoEfficiency;
	public static ItemStack dynamoOutput;

	public static ItemStack dynamoCoilDuct;
	public static ItemStack dynamoThrottle;

	/* Machine */
	public static ItemStack machineSecondary;
	public static ItemStack machineSpeed;

	public static ItemStack machineSecondaryNull;
	public static ItemStack machineEssenceCrystals;             // Experience Gathering

	public static ItemStack machineFurnaceFood;
	public static ItemStack machineFurnaceOre;

	public static ItemStack machineSmelterPyrotheum;

	public static ItemStack machineInsolatorNether;
	public static ItemStack machineInsolatorEnd;

	public static ItemStack machineCompactorMint;

	public static ItemStack machineCentrifugeMobs;              // Enstabulation Chamber

	public static ItemStack machinePrecipitatorBatchSize;
	public static ItemStack machinePrecipitatorPackedIce;

	public static ItemStack machineExtruderBatchSize;
	public static ItemStack machineExtruderAndesite;
	public static ItemStack machineExtruderDiorite;
	public static ItemStack machineExtruderGranite;

	/* Automaton */
	public static ItemStack automatonDepth;
	public static ItemStack automatonRadius;

	public static ItemStack automatonBreakerFluid;
	public static ItemStack automatonCollectorEntity;

}
