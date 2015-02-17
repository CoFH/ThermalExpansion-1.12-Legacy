package cofh.thermalexpansion.block.machine;

import cofh.core.CoFHProps;
import cofh.core.util.CoreUtils;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.client.machine.GuiAccumulator;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileAccumulator extends TileMachineBase implements IFluidHandler {

	static final int TYPE = BlockMachine.Types.ACCUMULATOR.ordinal();

	public static void initialize() {

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numGroup = 2;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, {} };
		defaultSideConfig[TYPE].allowInsertion = new boolean[] { false, false };
		defaultSideConfig[TYPE].allowExtraction = new boolean[] { false, false };
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 4 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 1, 1, 1, 1, 1, 1 };

		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setParamsPower(0);

		sounds[TYPE] = CoreUtils.getSoundName(ThermalExpansion.modId, "blockMachineAccumulator");

		GameRegistry.registerTileEntity(TileAccumulator.class, "thermalexpansion.Accumulator");
	}

	public static int genRate = 25 * CoFHProps.TIME_CONSTANT;
	public static int transferRate = genRate / CoFHProps.TIME_CONSTANT;
	public static boolean passiveGen = false;

	public static FluidStack genStack = new FluidStack(FluidRegistry.WATER, 25);
	public static FluidStack genStackSmall = new FluidStack(FluidRegistry.WATER, 1);
	public static FluidStack genStackSnow = new FluidStack(FluidRegistry.WATER, 125);

	static {
		String comment = "This controls how many mB/t the Accumulator generates. (Default: 25)";
		int rate = ThermalExpansion.config.get("Machine.Accumulator", "BaseRate", TileAccumulator.genRate / CoFHProps.TIME_CONSTANT, comment);

		if (rate > 0 && rate <= 1000) {
			genRate = rate * CoFHProps.TIME_CONSTANT;
			transferRate = rate;
		} else {
			ThermalExpansion.log.info("'Machine.Accumulator.BaseRate' config value is out of acceptable range. Using default. (25)");
		}
		genStack = new FluidStack(FluidRegistry.WATER, genRate);

		comment = "This controls how many mB/t the Accumulator generates without two or more adjacent source blocks, if enabled. (Default: 1)";
		rate = ThermalExpansion.config.get("Machine.Accumulator", "PassiveRate", 1, comment);

		if (rate > 0 && rate <= 1000) {

		} else {
			ThermalExpansion.log.info("'Machine.Accumulator.PassiveRate' config value is out of acceptable range. Using default. (1)");
			rate = 1;
		}
		genStackSmall = new FluidStack(FluidRegistry.WATER, rate);

		comment = "Set this to true to enable passive generation (less than two adjacent sources) for the Accumulator.";
		passiveGen = ThermalExpansion.config.get("Machine.Accumulator", "PassiveGeneration", false);
	}

	FluidTankAdv tank = new FluidTankAdv(TEProps.MAX_FLUID_SMALL);

	int adjacentSources = -1;
	int outputTrackerFluid;
	boolean inHell;
	FluidStack outputBuffer;

	public TileAccumulator() {

		super();
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void onNeighborBlockChange() {

		super.onNeighborBlockChange();
		updateAdjacentSources();
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
	protected boolean canStart() {

		return !inHell;
	}

	protected void updateAdjacentSources() {

		inHell = worldObj.getBiomeGenForCoords(xCoord, zCoord) == BiomeGenBase.hell;

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

	protected void transferFluid() {

		if (!augmentAutoTransfer) {
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
	protected void onLevelChange() {

		super.onLevelChange();

		tank.setCapacity(TEProps.MAX_FLUID_SMALL * FLUID_CAPACITY[level]);
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiAccumulator(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
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
	public void sendGuiNetworkData(Container container, ICrafting player) {

		super.sendGuiNetworkData(container, player);

		player.sendProgressBarUpdate(container, 0, adjacentSources);
		player.sendProgressBarUpdate(container, 1, tank.getFluidAmount());
	}

	@Override
	public FluidTankAdv getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inHell = nbt.getBoolean("Hell");
		adjacentSources = nbt.getInteger("Sources");
		outputTrackerFluid = nbt.getInteger("Tracker");
		tank.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setBoolean("Hell", inHell);
		nbt.setInteger("Sources", adjacentSources);
		nbt.setInteger("Tracker", outputTrackerFluid);
		tank.writeToNBT(nbt);
	}

	/* NETWORK METHODS */
	// TODO: Add these if Accumulator changes over to something else.
	// @Override
	// public CoFHPacket getGuiPacket() {
	//
	// CoFHPacket payload = super.getGuiPacket();
	//
	// return payload;
	// }
	//
	// @Override
	// protected void handleGuiPacket(CoFHPacket payload) {
	//
	// }

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
