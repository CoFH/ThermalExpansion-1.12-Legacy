package thermalexpansion.block.tank;

import cofh.api.tileentity.ITileInfo;
import cofh.network.CoFHPacket;
import cofh.network.ITilePacketHandler;
import cofh.util.BlockHelper;
import cofh.util.FluidHelper;
import cofh.util.MathHelper;
import cofh.util.ServerHelper;
import cofh.util.StringHelper;
import cofh.util.fluid.FluidTankAdv;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileTEBase;

public class TileTank extends TileTEBase implements IFluidHandler, ITilePacketHandler, ITileInfo {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileTank.class, "thermalexpansion.Tank");
	}

	protected static final int UPDATE_FACTOR = 4;
	public static final int RENDER_LEVELS = 128;
	public static int[] CAPACITY = { 1000, 8000, 32000, 128000, 512000 };

	static {
		String category = "block.tweak";
		CAPACITY[4] = MathHelper.clampI(ThermalExpansion.config.get(category, "Tank.Resonant.Capacity", CAPACITY[4]), CAPACITY[4] / 8, CAPACITY[4] * 1000);
		CAPACITY[3] = MathHelper.clampI(ThermalExpansion.config.get(category, "Tank.Reinforced.Capacity", CAPACITY[3]), CAPACITY[3] / 8, CAPACITY[4]);
		CAPACITY[2] = MathHelper.clampI(ThermalExpansion.config.get(category, "Tank.Hardened.Capacity", CAPACITY[2]), CAPACITY[2] / 8, CAPACITY[3]);
		CAPACITY[1] = MathHelper.clampI(ThermalExpansion.config.get(category, "Tank.Basic.Capacity", CAPACITY[1]), CAPACITY[1] / 8, CAPACITY[2]);
	}

	int compareTracker;
	int lastDisplayLevel;
	FluidTankAdv tank;

	boolean cached = false;
	boolean adjacentTanks[] = new boolean[2];
	IFluidHandler adjacentHandlers[] = new IFluidHandler[2];

	public byte mode;
	public byte type;

	public TileTank() {

		tank = new FluidTankAdv(CAPACITY[1]);
	}

	public TileTank(int metadata) {

		tank = new FluidTankAdv(CAPACITY[metadata]);
		type = (byte) metadata;
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.tank." + BlockTank.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return type;
	}

	@Override
	public int getComparatorInput(int side) {

		return compareTracker;
	}

	@Override
	public int getLightValue() {

		if (tank.getFluid() == null || tank.getFluid().getFluid() == null) {
			return 0;
		}
		int fluidLightLevel = tank.getFluid().getFluid().getLuminosity();
		// if under 1/4 full, half light level
		if (tank.getFluidAmount() <= CAPACITY[type] / 4) {
			return fluidLightLevel >> 1;
		}
		// if over 3/4 full, full light level
		if (tank.getFluidAmount() >= CAPACITY[type] * 3 / 4) {
			return fluidLightLevel;
		}
		// otherwise scale between half and full
		return (fluidLightLevel >> 1) + (fluidLightLevel - (fluidLightLevel >> 1)) * (tank.getFluidAmount() - (CAPACITY[type] >> 2)) / (CAPACITY[type] >> 1);
	}

	@Override
	public boolean onWrench(EntityPlayer player, int bSide) {

		mode = (byte) (++mode % 2);
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	@Override
	public void onNeighborBlockChange() {

		super.onNeighborBlockChange();
		updateAdjacentHandlers();
	}

	@Override
	public void onNeighborTileChange(int tileX, int tileY, int tileZ) {

		super.onNeighborTileChange(tileX, tileY, tileZ);
		updateAdjacentHandlers();
	}

	@Override
	public void updateEntity() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (!cached) {
			onNeighborBlockChange();
		}
		if (mode == 1) {
			transferFluid();
		}
		if (timeCheck()) {
			int curScale = getScaledFluidStored(15);
			if (curScale != compareTracker) {
				compareTracker = curScale;
				callNeighborTileChange();
			}
		}
		if (worldObj.getTotalWorldTime() % 4 == 0) {
			updateRender();
		}
		super.updateEntity();
	}

	protected int getScaledFluidStored(int scale) {

		return tank.getFluid() == null ? 0 : tank.getFluid().amount * scale / tank.getCapacity();
	}

	protected void transferFluid() {

		if (tank.getFluidAmount() <= 0 || adjacentHandlers[0] == null) {
			return;
		}
		tank.drain(
				adjacentHandlers[0].fill(ForgeDirection.VALID_DIRECTIONS[1],
						new FluidStack(tank.getFluid(), Math.min(FluidContainerRegistry.BUCKET_VOLUME, tank.getFluidAmount())), true), true);

		if (tank.getFluidAmount() <= 0) {
			updateRender();
		}
	}

	protected void updateAdjacentHandlers() {

		byte curMode = mode;

		TileEntity tile = BlockHelper.getAdjacentTileEntity(this, 0);
		if (FluidHelper.isFluidHandler(tile)) {
			adjacentHandlers[0] = (IFluidHandler) tile;

			if (tile instanceof TileTank) {
				mode = 1;
				adjacentTanks[0] = true;
			} else {
				adjacentTanks[0] = false;
			}
		} else {
			adjacentHandlers[0] = null;
			adjacentTanks[0] = false;
		}
		tile = BlockHelper.getAdjacentTileEntity(this, 1);
		if (FluidHelper.isFluidHandler(tile)) {
			adjacentHandlers[1] = (IFluidHandler) tile;

			if (tile instanceof TileTank) {
				adjacentTanks[1] = true;
			} else {
				adjacentTanks[1] = false;
			}
		} else {
			adjacentHandlers[1] = null;
			adjacentTanks[1] = false;
		}
		if (curMode != mode) {
			sendUpdatePacket(Side.CLIENT);
		}
	}

	public int getTankCapacity() {

		return tank.getCapacity();
	}

	public int getTankFluidAmount() {

		return tank.getFluidAmount();
	}

	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	public void calcLastDisplay() {

		lastDisplayLevel = (int) (tank.getFluidAmount() / (float) CAPACITY[type] * (RENDER_LEVELS - 1));
	}

	public void updateRender() {

		int curDisplayLevel = 0;

		if (tank.getFluidAmount() > 0) {
			curDisplayLevel = (int) (tank.getFluidAmount() / (float) CAPACITY[type] * (RENDER_LEVELS - 1));
			if (curDisplayLevel == 0) {
				curDisplayLevel = 1;
			}
			if (lastDisplayLevel == 0) {
				lastDisplayLevel = curDisplayLevel;
				sendUpdatePacket(Side.CLIENT);
				return;
			}
		} else if (lastDisplayLevel != 0) {
			lastDisplayLevel = 0;
			sendUpdatePacket(Side.CLIENT);
		}
		if (curDisplayLevel <= lastDisplayLevel - UPDATE_FACTOR) {
			lastDisplayLevel = curDisplayLevel;
			sendUpdatePacket(Side.CLIENT);
		} else if (curDisplayLevel >= lastDisplayLevel + UPDATE_FACTOR) {
			lastDisplayLevel = curDisplayLevel;
			sendUpdatePacket(Side.CLIENT);
		}
	}

	/* GUI METHODS */
	@Override
	public boolean hasGui() {

		return false;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		type = nbt.getByte("Type");
		mode = nbt.getByte("Mode");

		tank = new FluidTankAdv(CAPACITY[type]);
		tank.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Type", type);
		nbt.setByte("Mode", mode);

		tank.writeToNBT(nbt);
	}

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();

		payload.addByte(type);
		payload.addByte(mode);
		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		type = payload.getByte();
		mode = payload.getByte();
		tank.setFluid(payload.getFluidStack());
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (from.ordinal() == 0 && mode == 1 && !adjacentTanks[0]) {
			return 0;
		}
		if (from.ordinal() > 1 && from.ordinal() < 6) {
			return 0;
		}
		int amount = tank.fill(resource, doFill);

		if (from.ordinal() != 1 && adjacentHandlers[1] != null && adjacentTanks[1]) {
			if (amount == 0) {
				return adjacentHandlers[1].fill(ForgeDirection.DOWN, resource, doFill);
			} else if (amount != resource.amount) {
				FluidStack remaining = resource.copy();
				remaining.amount -= amount;
				return amount + adjacentHandlers[1].fill(ForgeDirection.DOWN, remaining, doFill);
			}
		}
		return amount;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		if (from.ordinal() == 0 && mode == 1) {
			return null;
		}
		if (from.ordinal() > 1 && from.ordinal() < 6) {
			return null;
		}
		return tank.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		if (from.ordinal() == 0 && mode == 1) {
			return null;
		}
		if (from.ordinal() > 1 && from.ordinal() < 6) {
			return null;
		}
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		return from.ordinal() < 2;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return from.ordinal() < 1;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return new FluidTankInfo[] { tank.getInfo() };
	}

	/* ITileInfo */
	@Override
	public void getTileInfo(List<String> info, ForgeDirection side, EntityPlayer player, boolean debug) {

		if (debug) {
			return;
		}
		if (tank.getFluid() != null) {
			info.add(StringHelper.localize("info.cofh.fluid") + ": " + StringHelper.getFluidName(tank.getFluid()));
			info.add(StringHelper.localize("info.cofh.amount") + ": " + tank.getFluidAmount() + "/" + tank.getCapacity() + " mB");
		} else {
			info.add(StringHelper.localize("info.cofh.fluid") + ": " + StringHelper.localize("info.cofh.empty"));
		}
	}

}
