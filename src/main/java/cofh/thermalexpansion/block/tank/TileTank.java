package cofh.thermalexpansion.block.tank;

import cofh.api.tileentity.ITileInfo;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileTEBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.*;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import javax.annotation.Nullable;

public class TileTank extends TileTEBase implements ITileInfo, ITickable {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileTank.class, "thermalexpansion.Tank");
	}

	protected static final int UPDATE_FACTOR = 4;
	public static final int RENDER_LEVELS = 128;
	public static int[] CAPACITY = { 1000, 8000, 32000, 128000, 512000 };

	static {
		String category = "Tank.";
		CAPACITY[4] = MathHelper.clamp(ThermalExpansion.config.get(category + StringHelper.titleCase(BlockTank.NAMES[4]), "Capacity", CAPACITY[4]),
				CAPACITY[4] / 8, 1000000 * 1000);
		CAPACITY[3] = MathHelper.clamp(ThermalExpansion.config.get(category + StringHelper.titleCase(BlockTank.NAMES[3]), "Capacity", CAPACITY[3]),
				CAPACITY[3] / 8, CAPACITY[4]);
		CAPACITY[2] = MathHelper.clamp(ThermalExpansion.config.get(category + StringHelper.titleCase(BlockTank.NAMES[2]), "Capacity", CAPACITY[2]),
				CAPACITY[2] / 8, CAPACITY[3]);
		CAPACITY[1] = MathHelper.clamp(ThermalExpansion.config.get(category + StringHelper.titleCase(BlockTank.NAMES[1]), "Capacity", CAPACITY[1]),
				CAPACITY[1] / 8, CAPACITY[2]);
	}

	int compareTracker;
	int lastDisplayLevel;
	FluidTankAdv tank;

	public byte mode;
	public byte type;

	boolean cached = false;
	boolean adjacentTanks[] = new boolean[2];
	IFluidHandler adjacentHandlers[] = new IFluidHandler[2];

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
	public int getComparatorInput() {
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
	public void onNeighborTileChange(BlockPos pos) {

		super.onNeighborTileChange(pos);
		updateAdjacentHandlers();
	}

	@Override
	public void update() {

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
		if (timeCheckEighth()) {
			updateRender();
		}
	}

	@Override
	public void invalidate() {

		cached = false;
		super.invalidate();
	}

	protected int getScaledFluidStored(int scale) {

		return tank.getFluid() == null ? 0 : tank.getFluid().amount * scale / tank.getCapacity();
	}

	protected void transferFluid() {

		if (tank.getFluidAmount() <= 0 || adjacentHandlers[0] == null) {
			return;
		}
		tank.drain(
				adjacentHandlers[0].fill(new FluidStack(tank.getFluid(), Math.min(Fluid.BUCKET_VOLUME, tank.getFluidAmount())), true), true);

		if (tank.getFluidAmount() <= 0) {
			updateRender();
		}
	}

	protected void updateAdjacentHandlers() {

		byte curMode = mode;

		TileEntity tile = BlockHelper.getAdjacentTileEntity(this, 0);
		if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP)) {
			adjacentHandlers[0] = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP);

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
		if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN)) {
			adjacentHandlers[1] = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN);

            adjacentTanks[1] = tile instanceof TileTank;
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
		int oldLight = getLightValue();

		if (tank.getFluidAmount() > 0) {
			curDisplayLevel = (int) (tank.getFluidAmount() / (float) CAPACITY[type] * (RENDER_LEVELS - 1));
			if (curDisplayLevel == 0) {
				curDisplayLevel = 1;
			}
			if (lastDisplayLevel == 0) {
				lastDisplayLevel = curDisplayLevel;
				sendUpdatePacket(Side.CLIENT);
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
		if (oldLight != getLightValue()) {
			updateLighting();
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
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Type", type);
		nbt.setByte("Mode", mode);

		tank.writeToNBT(nbt);
        return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addByte(type);
		payload.addByte(mode);
		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		type = payload.getByte();
		mode = payload.getByte();
		tank.setFluid(payload.getFluidStack());
	}

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return super.hasCapability(capability, facing) || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, final EnumFacing facing) {
	    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
	        return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler() {
                @Override
                public IFluidTankProperties[] getTankProperties() {
                    return FluidTankProperties.convert(new FluidTankInfo[] { tank.getInfo() });
                }

                @Override
                public int fill(FluidStack resource, boolean doFill) {
                    int ordinal = facing == null ? 6 : facing.ordinal();

                    if (ordinal == 0 && mode == 1 && !adjacentTanks[0]) {
                        return 0;
                    }
                    int amount = tank.fill(resource, doFill);

                    if (ordinal != 1 && adjacentHandlers[1] != null && adjacentTanks[1]) {
                        if (amount == 0) {
                            return adjacentHandlers[1].fill(resource, doFill);
                        } else if (amount != resource.amount) {
                            FluidStack remaining = resource.copy();
                            remaining.amount -= amount;
                            return amount + adjacentHandlers[1].fill(remaining, doFill);
                        }
                    }
                    return amount;
                }

                @Nullable
                @Override
                public FluidStack drain(FluidStack resource, boolean doDrain) {
                    int ordinal = facing == null ? 6 : facing.ordinal();

                    if (ordinal == 0 && mode == 1) {
                        return null;
                    }
                    return tank.drain(resource, doDrain);
                }

                @Nullable
                @Override
                public FluidStack drain(int maxDrain, boolean doDrain) {
                    int ordinal = facing == null ? 6 : facing.ordinal();

                    if (ordinal == 0 && mode == 1) {
                        return null;
                    }
                    return tank.drain(maxDrain, doDrain);
                }
            });
        }
        return super.getCapability(capability, facing);
    }

	/* ITileInfo */
	@Override
	public void getTileInfo(List<ITextComponent> info, EnumFacing side, EntityPlayer player, boolean debug) {

		if (debug) {
			return;
		}
		if (tank.getFluid() != null) {
			info.add(new TextComponentString(StringHelper.localize("info.cofh.fluid") + ": " + StringHelper.getFluidName(tank.getFluid())));
			info.add(new TextComponentString(StringHelper.localize("info.cofh.amount") + ": " + tank.getFluidAmount() + "/" + tank.getCapacity() + " mB"));
		} else {
			info.add(new TextComponentString(StringHelper.localize("info.cofh.fluid") + ": " + StringHelper.localize("info.cofh.empty")));
		}
	}

}
