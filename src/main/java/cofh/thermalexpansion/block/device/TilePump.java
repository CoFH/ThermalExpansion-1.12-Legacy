package cofh.thermalexpansion.block.device;

import cofh.api.energy.EnergyStorage;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileAugmentable;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.client.machine.GuiTransposer;
import cofh.thermalexpansion.gui.container.machine.ContainerTransposer;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;


public class TilePump extends TileAugmentable implements IFluidHandler {

	static final int TYPE = BlockDevice.Types.PUMP.ordinal();
	static SideConfig defaultSideConfig = new SideConfig();

	public static void initialize() {

		defaultSideConfig = new SideConfig();
		defaultSideConfig.numGroup = 2;
		defaultSideConfig.slotGroups = new int[][] { {}, {} };
		defaultSideConfig.allowInsertion = new boolean[] { false, false };
		defaultSideConfig.allowExtraction = new boolean[] { false, false };
		defaultSideConfig.sideTex = new int[] { 0, 4 };
		defaultSideConfig.defaultSides = new byte[] { 0, 0, 1, 1, 1, 1 };

		GameRegistry.registerTileEntity(TilePump.class, "thermalexpansion.Pump");
		configure();
	}

	public static void configure() {

		String comment = "Enable this to allow for Pumps to be securable. (Default: true)";
		enableSecurity = ThermalExpansion.config.get("security", "Device.Pump.Securable", enableSecurity, comment);
	}

	public static boolean enableSecurity = true;

	int outputTracker;
	FluidTankAdv tank = new FluidTankAdv(TEProps.MAX_FLUID_LARGE);

	public boolean reverse;

	public TilePump() {

		sideConfig = defaultSideConfig;
		sideCache = new byte[] { 0, 0, 1, 1, 1, 1 };
		energyStorage = new EnergyStorage(0);
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.device." + BlockDevice.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return BlockDevice.Types.PUMP.ordinal();
	}

	@Override
	public void updateEntity() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
	}

	@Override
	public void invalidate() {

		super.invalidate();
	}

	@Override
	public void validate() {

		super.validate();
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiTransposer(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTransposer(inventory, this);
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

		outputTracker = nbt.getInteger("Tracker");
		reverse = nbt.getBoolean("Rev");
		tank.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("Tracker", outputTracker);
		nbt.setBoolean("Rev", reverse);
		tank.writeToNBT(nbt);
	}

	/* IReconfigurableSides */
	@Override
	public int getNumConfig(int side) {

		return 3;
	}

	/* ISidedTexture */
	@Override
	public IIcon getTexture(int side, int pass) {

		return null;
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (!reverse || from == ForgeDirection.UNKNOWN || sideCache[from.ordinal()] != 1) {
			return 0;
		}
		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		if (reverse || from == ForgeDirection.UNKNOWN || sideCache[from.ordinal()] != 2) {
			return null;
		}
		return tank.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		if (reverse || from == ForgeDirection.UNKNOWN || sideCache[from.ordinal()] != 2) {
			return null;
		}
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		if (from == ForgeDirection.UNKNOWN) {
			return false;
		}
		return !reverse && sideCache[from.ordinal()] == 1;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		if (from == ForgeDirection.UNKNOWN) {
			return false;
		}
		return reverse && sideCache[from.ordinal()] == 2;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return new FluidTankInfo[] { tank.getInfo() };
	}

}
