package thermalexpansion.block.machine;

import cofh.core.CoFHProps;
import cofh.util.FluidHelper;
import cofh.util.ServerHelper;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import thermalexpansion.ThermalExpansion;

public class TileWaterGen extends TileMachineBase implements IFluidHandler {

	public static final int TYPE = BlockMachine.Types.WATER_GEN.ordinal();

	public static void initialize() {

		defaultSideData[TYPE] = new SideConfig();
		defaultSideData[TYPE].numGroup = 2;
		defaultSideData[TYPE].slotGroups = new int[][] { {}, {} };
		defaultSideData[TYPE].allowInsertion = new boolean[] { false, false };
		defaultSideData[TYPE].allowExtraction = new boolean[] { false, false };
		defaultSideData[TYPE].sideTex = new int[] { 0, 4 };

		guiIds[TYPE] = ThermalExpansion.proxy.registerGui("WaterGen", "machine", "TEBase", null, true);
		GameRegistry.registerTileEntity(TileWaterGen.class, "thermalexpansion.WaterGen");
	}

	public static int genRate = 25 * CoFHProps.TIME_CONSTANT;
	public static int transferRate = genRate / CoFHProps.TIME_CONSTANT;
	public static boolean passiveGen = true;
	public static FluidStack genStack;
	public static FluidStack genStackSmall = new FluidStack(FluidRegistry.WATER, 1);
	public static FluidStack genStackSnow = new FluidStack(FluidRegistry.WATER, 125);

	static {
		int rate = ThermalExpansion.config.get("tweak", "WaterGen.Rate", TileWaterGen.genRate / CoFHProps.TIME_CONSTANT);

		if (rate > 0 && rate <= 50) {
			genRate = rate * CoFHProps.TIME_CONSTANT;
			transferRate = genRate / CoFHProps.TIME_CONSTANT;
		} else {
			ThermalExpansion.log.info("'WaterGen.Rate' config value is out of acceptable range. Using default. (25)");
		}
		genStack = new FluidStack(FluidRegistry.WATER, genRate);
		ThermalExpansion.config.removeProperty("tweak", "WaterGen.PassiveGen");
		// passiveGen = ThermalExpansion.config.get("tweak", "WaterGen.PassiveGen", true);
	}

	FluidTank tank = new FluidTank(MAX_FLUID_SMALL);

	int adjacentSources = -1;
	FluidStack outputBuffer;
	int outputTrackerFluid;

	public TileWaterGen() {

		sideCache = new byte[] { 1, 1, 1, 1, 1, 1 };
	}

	@Override
	public int getType() {

		return TYPE;
	}

	protected void updateAdjacentSources() {

		adjacentSources = 0;
		Block block = worldObj.getBlock(xCoord - 1, yCoord, zCoord);
		int bMeta = worldObj.getBlockMetadata(xCoord - 1, yCoord, zCoord);

		if (bMeta == 0 && (block == Blocks.water || block == Blocks.flowing_water)) {
			++adjacentSources;
		}
		block = worldObj.getBlock(xCoord + 1, yCoord, zCoord);
		bMeta = worldObj.getBlockMetadata(xCoord + 1, yCoord, zCoord);

		if (bMeta == 0 && (block == Blocks.water || block == Blocks.flowing_water)) {
			++adjacentSources;
		}
		block = worldObj.getBlock(xCoord, yCoord, zCoord - 1);
		bMeta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord - 1);

		if (bMeta == 0 && (block == Blocks.water || block == Blocks.flowing_water)) {
			++adjacentSources;
		}
		block = worldObj.getBlock(xCoord, yCoord, zCoord + 1);
		bMeta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord + 1);

		if (bMeta == 0 && (block == Blocks.water || block == Blocks.flowing_water)) {
			++adjacentSources;
		}
		block = worldObj.getBlock(xCoord, yCoord + 1, zCoord);

		if (block == Blocks.snow) {
			worldObj.setBlockToAir(xCoord, yCoord + 1, zCoord);
			tank.fill(genStackSnow, true);
		}
	}

	protected boolean canStart() {

		return worldObj.getBiomeGenForCoords(xCoord, zCoord) != BiomeGenBase.hell;
	}

	protected void transferFluid() {

		if (!upgradeAutoTransfer) {
			return;
		}
		if (tank.getFluidAmount() <= 0) {
			return;
		}
		int side;
		outputBuffer = new FluidStack(tank.getFluid(), Math.min(tank.getFluidAmount(), RATE));
		for (int i = outputTrackerFluid + 1; i <= outputTrackerFluid + 6; i++) {
			side = i % 6;

			if (sideCache[side] == 1) {
				int toDrain = FluidHelper.insertFluidIntoAdjacentFluidHandler(this, side, outputBuffer, true);

				if (toDrain > 0) {
					tank.drain(toDrain, true);
					outputTrackerFluid = side;
					break;
				}
			}
		}
	}

	@Override
	public void updateEntity() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		boolean curActive = isActive;
		transferFluid();

		if (isActive) {
			if (timeCheck()) {
				if (adjacentSources >= 2) {
					tank.fill(genStack, true);
				} else {
					if (worldObj.isRaining() && worldObj.canBlockSeeTheSky(xCoord, yCoord, zCoord)) {
						tank.fill(genStack, true);
					} else if (passiveGen) {
						tank.fill(genStackSmall, true);
					}
				}
			}
			if (!redstoneControlOrDisable()) {
				isActive = false;
				wasActive = true;
				tracker.markTime(worldObj);
			}
		} else if (redstoneControlOrDisable() && canStart()) {
			isActive = true;
		}
		if (adjacentSources < 0) {
			updateAdjacentSources();
		}
		updateIfChanged(curActive);
	}

	@Override
	public void onNeighborBlockChange() {

		super.onNeighborBlockChange();
		updateAdjacentSources();
	}

	/* GUI METHODS */
	public FluidTank getTank() {

		return tank;
	}

	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	@Override
	public void receiveGuiNetworkData(int i, int j) {

		switch (i) {
		case 0:
			adjacentSources = j;
			return;
		case 1:
			if (tank.getFluid() == null) {
				tank.setFluid(new FluidStack(FluidRegistry.WATER, j));
			} else {
				tank.getFluid().amount = j;
			}
			return;
		}
	}

	@Override
	public void sendGuiNetworkData(Container container, ICrafting iCrafting) {

		iCrafting.sendProgressBarUpdate(container, 0, adjacentSources);
		iCrafting.sendProgressBarUpdate(container, 1, tank.getFluidAmount());
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		adjacentSources = nbt.getInteger("Sources");
		outputTrackerFluid = nbt.getInteger("Tracker");
		tank.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("Sources", adjacentSources);
		nbt.setInteger("Tracker", outputTrackerFluid);
		tank.writeToNBT(nbt);
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		if (from != ForgeDirection.UNKNOWN && sideCache[from.ordinal()] != 2) {
			return null;
		}
		if (resource == null || resource.getFluid() != FluidRegistry.WATER) {
			return null;
		}
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		if (from != ForgeDirection.UNKNOWN && sideCache[from.ordinal()] != 2) {
			return null;
		}
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return new FluidTankInfo[] { tank.getInfo() };
	}

}
