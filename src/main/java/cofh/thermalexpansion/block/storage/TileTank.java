package cofh.thermalexpansion.block.storage;

import cofh.api.tileentity.ITileInfo;
import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.*;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileAugmentableSecure;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.List;

public class TileTank extends TileAugmentableSecure implements ITickable, ITileInfo {

	public static int[] CAPACITY = { 1, 4, 9, 16, 25 };

	public static final int RENDER_LEVELS = 100;

	static {
		for (int i = 0; i < CAPACITY.length; i++) {
			CAPACITY[i] *= 20000;
		}
	}

	private static boolean enableSecurity = true;

	public static void initialize() {

		GameRegistry.registerTileEntity(TileTank.class, "thermalexpansion:storage_tank");

		config();
	}

	public static void config() {

		String comment = "Enable this to allow for Tanks to be securable.";
		enableSecurity = ThermalExpansion.CONFIG.get("Security", "Tank.Securable", true, comment);

		String category = "Storage.Tank";
		BlockTank.enable = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}

	private int compareTracker;
	private int lastDisplayLevel;

	public byte enchantHolding;

	boolean renderFlag = true;
	boolean cached = false;
	boolean adjacentTanks[] = new boolean[2];

	private FluidTankCore tank = new FluidTankCore(getCapacity(0, 0));

	@Override
	public String getTileName() {

		return "tile.thermalexpansion.storage.tank.name";
	}

	@Override
	public int getType() {

		return 0;
	}

	@Override
	public void blockPlaced() {

		sendTilePacket(Side.CLIENT);
	}

	@Override
	public int getComparatorInputOverride() {

		return compareTracker;
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity;
	}

	@Override
	public boolean onWrench(EntityPlayer player, EnumFacing side) {

		enableAutoOutput = !enableAutoOutput;
		markChunkDirty();

		sendTilePacket(Side.CLIENT);
		return true;
	}

	@Override
	public int getLightValue() {

		return tank.getFluid() == null ? 0 : tank.getFluid().getFluid().getLuminosity();
	}

	@Override
	public void invalidate() {

		cached = false;
		super.invalidate();
	}

	@Override
	public void onNeighborBlockChange() {

		super.onNeighborBlockChange();
		updateAdjacentHandlers(true);
	}

	@Override
	public void onNeighborTileChange(BlockPos pos) {

		super.onNeighborTileChange(pos);
		updateAdjacentHandlers(true);
	}

	@Override
	public void update() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		transferFluid();

		if (timeCheck()) {
			int curScale = getScaledFluidStored(15);
			if (curScale != compareTracker) {
				compareTracker = curScale;
				callNeighborTileChange();
			}
			if (!cached) {
				updateLighting();
				updateAdjacentHandlers(false);
				sendTilePacket(Side.CLIENT);
			}
		}
		if (renderFlag && timeCheckEighth()) {
			updateRender();
		}
	}

	@Override
	public FluidTankCore getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	@Override
	protected boolean setLevel(int level) {

		if (super.setLevel(level)) {
			tank.setCapacity(getCapacity(level, enchantHolding));

			if (isCreative && getTankFluidAmount() > 0) {
				tank.getFluid().amount = tank.getCapacity();
			}
			return true;
		}
		return false;
	}

	@Override
	protected int getNumAugmentSlots(int level) {

		return 0;
	}

	/* COMMON METHODS */
	public static int getCapacity(int level, int enchant) {

		return CAPACITY[MathHelper.clamp(level, 0, 4)] + (CAPACITY[MathHelper.clamp(level, 0, 4)] * enchant) / 2;
	}

	public int getScaledFluidStored(int scale) {

		return tank.getFluid() == null ? 0 : tank.getFluid().amount * scale / tank.getCapacity();
	}

	protected void transferFluid() {

		if (!enableAutoOutput || tank.getFluidAmount() <= 0) {
			return;
		}
		int toDrain = FluidHelper.insertFluidIntoAdjacentFluidHandler(this, EnumFacing.DOWN, new FluidStack(tank.getFluid(), Math.min(getFluidTransfer(level), tank.getFluidAmount())), true);

		if (toDrain > 0) {
			renderFlag = !isCreative;
			tank.drain(toDrain, !isCreative);
		}
	}

	protected void updateAdjacentHandlers(boolean packet) {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		boolean curAutoOutput = enableAutoOutput;

		adjacentTanks[0] = BlockHelper.getAdjacentTileEntity(this, EnumFacing.DOWN) instanceof TileTank;
		enableAutoOutput |= adjacentTanks[0];

		adjacentTanks[1] = BlockHelper.getAdjacentTileEntity(this, EnumFacing.UP) instanceof TileTank;

		if (packet && curAutoOutput != enableAutoOutput) {
			sendTilePacket(Side.CLIENT);
		}
		cached = true;
	}

	public int getTankCapacity() {

		return tank.getCapacity();
	}

	public int getTankFluidAmount() {

		return tank.getFluidAmount();
	}

	public void updateRender() {

		renderFlag = false;
		boolean sendUpdate = false;

		int curDisplayLevel = 0;
		int curLight = getLightValue();

		if (tank.getFluidAmount() > 0) {
			curDisplayLevel = (int) (tank.getFluidAmount() / (float) getCapacity(level, enchantHolding) * (RENDER_LEVELS - 1));
			if (curDisplayLevel == 0) {
				curDisplayLevel = 1;
			}
			if (lastDisplayLevel == 0) {
				lastDisplayLevel = curDisplayLevel;
				sendUpdate = true;
			}
		} else if (lastDisplayLevel != 0) {
			lastDisplayLevel = 0;
			sendUpdate = true;
		}
		if (curDisplayLevel != lastDisplayLevel) {
			lastDisplayLevel = curDisplayLevel;
			sendUpdate = true;
		}
		if (curLight != getLightValue()) {
			updateLighting();
			sendUpdate = true;
		}
		if (sendUpdate) {
			sendTilePacket(Side.CLIENT);
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

		enchantHolding = nbt.getByte("EncHolding");
		tank.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("EncHolding", enchantHolding);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getTilePacket() {

		PacketCoFHBase payload = super.getTilePacket();

		payload.addByte(enchantHolding);
		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		enchantHolding = payload.getByte();
		tank.setFluid(payload.getFluidStack());

		callBlockUpdate();
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

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return super.hasCapability(capability, from) || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {

			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler() {
				@Override
				public IFluidTankProperties[] getTankProperties() {

					FluidTankInfo info = tank.getInfo();
					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, true, true) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (isCreative) {
						if (resource == null || from == EnumFacing.DOWN && !adjacentTanks[0] && enableAutoOutput) {
							return 0;
						}
						if (resource.isFluidEqual(tank.getFluid())) {
							return 0;
						}
						tank.setFluid(new FluidStack(resource.getFluid(), getCapacity(level, enchantHolding)));
						sendTilePacket(Side.CLIENT);
						updateRender();
						return 0;
					}
					if (resource == null || from == EnumFacing.DOWN && !adjacentTanks[0] && enableAutoOutput) {
						return 0;
					}
					renderFlag = true;
					int amount = tank.fill(resource, doFill);

					if (adjacentTanks[1] && from != EnumFacing.UP) {
						if (amount != resource.amount) {
							FluidStack remaining = resource.copy();
							remaining.amount -= amount;
							return amount + FluidHelper.insertFluidIntoAdjacentFluidHandler(worldObj, pos, EnumFacing.UP, remaining, doFill);
						}
					}
					return amount;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (isCreative) {
						if (!FluidHelper.isFluidEqual(resource, tank.getFluid())) {
							return null;
						}
						return resource.copy();
					}
					renderFlag = true;
					return tank.drain(resource, doDrain);
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (isCreative) {
						if (tank.getFluid() == null) {
							return null;
						}
						return new FluidStack(tank.getFluid(), maxDrain);
					}
					renderFlag = true;
					return tank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
