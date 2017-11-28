package cofh.thermalexpansion.block.storage;

import cofh.api.item.IUpgradeItem;
import cofh.api.item.IUpgradeItem.UpgradeType;
import cofh.api.tileentity.ITileInfo;
import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.helpers.*;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileAugmentableSecure;
import cofh.thermalexpansion.gui.client.storage.GuiTank;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class TileTank extends TileAugmentableSecure implements ITickable, ITileInfo {

	public static final int CAPACITY_BASE = 20000;
	public static final int[] CAPACITY = { 1, 4, 9, 16, 25 };
	public static final int RENDER_LEVELS = 100;

	public static void initialize() {

		GameRegistry.registerTileEntity(TileTank.class, "thermalexpansion:storage_tank");

		config();
	}

	public static void config() {

		String category = "Storage.Tank";
		String comment = "If TRUE, Tanks are enabled.";
		BlockTank.enable = ThermalExpansion.CONFIG.get(category, "Enable", BlockTank.enable, comment);

		comment = "If TRUE, Tanks may be turned into Creative versions using a Creative Conversion Kit.";
		BlockTank.enableCreative = ThermalExpansion.CONFIG.get(category, "Creative", BlockTank.enableCreative, comment);

		comment = "If TRUE, Tanks are securable.";
		BlockTank.enableSecurity = ThermalExpansion.CONFIG.get(category, "Securable", BlockTank.enableSecurity, comment);

		comment = "If TRUE, 'Classic' Crafting is enabled - Non-Creative Upgrade Kits WILL NOT WORK in a Crafting Grid.";
		BlockTank.enableClassicRecipes = ThermalExpansion.CONFIG.get(category, "ClassicCrafting", BlockTank.enableClassicRecipes, comment);

		comment = "If TRUE, Tanks can be upgraded in a Crafting Grid using Kits. If Classic Crafting is enabled, only the Creative Conversion Kit may be used in this fashion.";
		BlockTank.enableUpgradeKitCrafting = ThermalExpansion.CONFIG.get(category, "UpgradeKitCrafting", BlockTank.enableUpgradeKitCrafting, comment);

		int capacity = CAPACITY_BASE;
		comment = "Adjust this value to change the amount of Fluid (in mB) stored by a Basic Tank. This base value will scale with block level.";
		capacity = ThermalExpansion.CONFIG.getConfiguration().getInt("BaseCapacity", category, capacity, capacity / 5, capacity * 5, comment);

		for (int i = 0; i < CAPACITY.length; i++) {
			CAPACITY[i] *= capacity;
		}
	}

	private int compareTracker;
	private int lastDisplayLevel;

	public byte enchantHolding;

	boolean lock = false;
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

		super.blockPlaced();

		sendTilePacket(Side.CLIENT);
	}

	@Override
	public int getComparatorInputOverride() {

		return compareTracker;
	}

	@Override
	public boolean enableSecurity() {

		return BlockTank.enableSecurity;
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

		if (redstoneControlOrDisable()) {
			transferFluid();
		}
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

	/* IUpgradeable */
	@Override
	public boolean canUpgrade(ItemStack upgrade) {

		if (!AugmentHelper.isUpgradeItem(upgrade)) {
			return false;
		}
		UpgradeType uType = ((IUpgradeItem) upgrade.getItem()).getUpgradeType(upgrade);
		int uLevel = ((IUpgradeItem) upgrade.getItem()).getUpgradeLevel(upgrade);

		switch (uType) {
			case INCREMENTAL:
				if (uLevel == level + 1) {
					return !BlockTank.enableClassicRecipes;
				}
				break;
			case FULL:
				if (uLevel > level) {
					return !BlockTank.enableClassicRecipes;
				}
				break;
			case CREATIVE:
				return !isCreative && BlockTank.enableCreative;
		}
		return false;
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

		if (ServerHelper.isClientWorld(world)) {
			return;
		}
		boolean curAutoOutput = enableAutoOutput;

		adjacentTanks[0] = BlockHelper.getAdjacentTileEntity(this, EnumFacing.DOWN) instanceof TileTank;
		adjacentTanks[1] = BlockHelper.getAdjacentTileEntity(this, EnumFacing.UP) instanceof TileTank;

		if (!lock && getTankFluid() == null) {
			enableAutoOutput |= adjacentTanks[0];
		}
		if (packet && curAutoOutput != enableAutoOutput) {
			sendTilePacket(Side.CLIENT);
		}
		cached = true;
	}

	public void setLocked(boolean lock) {

		if (getTankFluid() == null) {
			lock = false;
		}
		this.lock = lock;
		tank.setLocked(lock);
		sendTilePacket(Side.CLIENT);
	}

	public boolean isLocked() {

		return lock;
	}

	public int getScaledFluidStored(int scale) {

		return tank.getFluid() == null ? 0 : tank.getFluid().amount * scale / tank.getCapacity();
	}

	public int getTankFluidAmount() {

		return tank.getFluidAmount();
	}

	public void updateRender() {

		renderFlag = false;
		boolean sendUpdate = false;

		int curDisplayLevel = 0;

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
		if (lastDisplayLevel != curDisplayLevel) {
			lastDisplayLevel = curDisplayLevel;
			sendUpdate = true;
		}
		if (sendUpdate) {
			updateLighting();
			sendTilePacket(Side.CLIENT);
		}
	}

	/* GUI METHODS */
	@Override
	public Object getConfigGuiClient(InventoryPlayer inventory) {

		return new GuiTank(inventory, this);
	}

	@Override
	public Object getConfigGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
	}

	@Override
	public boolean hasConfigGui() {

		return true;
	}

	// This is ONLY used in GUIs.
	public void toggleLock() {

		lock = !lock;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		enchantHolding = nbt.getByte("EncHolding");

		super.readFromNBT(nbt);

		tank.readFromNBT(nbt);
		lock = tank.isLocked();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("EncHolding", enchantHolding);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */

	/* CLIENT -> SERVER */
	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addBool(lock);

		return payload;
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		setLocked(payload.getBool());
	}

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addBool(lock);
		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	public PacketCoFHBase getTilePacket() {

		PacketCoFHBase payload = super.getTilePacket();

		payload.addByte(enchantHolding);
		payload.addBool(lock);
		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		lock = payload.getBool();
		tank.setFluid(payload.getFluidStack());
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketCoFHBase payload) {

		super.handleTilePacket(payload);

		enchantHolding = payload.getByte();
		lock = payload.getBool();
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
			info.add(new TextComponentString(lock ? StringHelper.localize("info.cofh.locked") : StringHelper.localize("info.cofh.unlocked")));
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
						tank.setFluid(new FluidStack(resource, getCapacity(level, enchantHolding)));
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
							return amount + FluidHelper.insertFluidIntoAdjacentFluidHandler(world, pos, EnumFacing.UP, remaining, doFill);
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
