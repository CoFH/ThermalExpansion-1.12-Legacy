package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.AugmentHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.machine.GuiSawmill;
import cofh.thermalexpansion.gui.container.machine.ContainerSawmill;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.crafting.SawmillManager;
import cofh.thermalexpansion.util.crafting.SawmillManager.RecipeSawmill;
import cofh.thermalexpansion.util.crafting.TapperManager;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class TileSawmill extends TileMachineBase {

	private static final int TYPE = BlockMachine.Type.SAWMILL.getMetadata();
	public static int basePower = 20;

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 6;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1 }, { 2 }, { 1, 2 }, { 0, 1, 2 } };
		SIDE_CONFIGS[TYPE].allowInsertionSide = new boolean[] { false, true, false, false, false, true };
		SIDE_CONFIGS[TYPE].allowExtractionSide = new boolean[] { false, true, true, true, true, true };
		SIDE_CONFIGS[TYPE].sideTex = new int[] { 0, 1, 2, 3, 4, 7 };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 3, 1, 2, 2, 2, 2 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, false, false, false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { true, true, true, true, false };

		VALID_AUGMENTS[TYPE] = new ArrayList<>();
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_SAWMILL_TAPPER);

		GameRegistry.registerTileEntity(TileSawmill.class, "thermalexpansion:machine_sawmill");

		config();
	}

	public static void config() {

		String category = "Machine.Sawmill";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower);
	}

	private int inputTracker;
	private int outputTrackerPrimary;
	private int outputTrackerSecondary;
	private int outputTrackerFluid;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	private FluidStack renderFluid = new FluidStack(TFFluids.fluidResin, 0);

	/* AUGMENTS */
	protected boolean augmentTapper;
	protected boolean flagTapper;

	public TileSawmill() {

		super();
		inventory = new ItemStack[1 + 1 + 1 + 1];
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void update() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (augmentTapper) {
			transferOutputFluid();
		}
		super.update();
	}

	@Override
	protected boolean canStart() {

		if (inventory[0] == null || energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		RecipeSawmill recipe = SawmillManager.getRecipe(inventory[0]);

		if (recipe == null) {
			return false;
		}
		if (inventory[0].stackSize < recipe.getInput().stackSize) {
			return false;
		}
		ItemStack primaryItem = recipe.getPrimaryOutput();
		ItemStack secondaryItem = recipe.getSecondaryOutput();

		if (secondaryItem != null && inventory[2] != null) {
			if (!augmentSecondaryNull && !inventory[2].isItemEqual(secondaryItem)) {
				return false;
			}
			if (!augmentSecondaryNull && inventory[2].stackSize + secondaryItem.stackSize > secondaryItem.getMaxStackSize()) {
				return false;
			}
		}
		return inventory[1] == null || inventory[1].isItemEqual(primaryItem) && inventory[1].stackSize + primaryItem.stackSize <= primaryItem.getMaxStackSize();
	}

	@Override
	protected boolean hasValidInput() {

		RecipeSawmill recipe = SawmillManager.getRecipe(inventory[0]);
		return recipe != null && recipe.getInput().stackSize <= inventory[0].stackSize;
	}

	@Override
	protected void processStart() {

		if (augmentTapper && TapperManager.mappingExists(inventory[0])) {
			renderFluid = new FluidStack(TapperManager.getFluid(inventory[0]).copy(), 0);
		}
		processMax = SawmillManager.getRecipe(inventory[0]).getEnergy() * energyMod / ENERGY_BASE;
		processRem = processMax;
	}

	@Override
	protected void processFinish() {

		RecipeSawmill recipe = SawmillManager.getRecipe(inventory[0]);

		if (recipe == null) {
			processOff();
			return;
		}
		ItemStack primaryItem = recipe.getPrimaryOutput();
		ItemStack secondaryItem = recipe.getSecondaryOutput();

		if (inventory[1] == null) {
			inventory[1] = ItemHelper.cloneStack(primaryItem);
		} else {
			inventory[1].stackSize += primaryItem.stackSize;
		}
		if (secondaryItem != null) {
			int modifiedChance = secondaryChance;

			int recipeChance = recipe.getSecondaryOutputChance();
			if (recipeChance >= 100 || worldObj.rand.nextInt(modifiedChance) < recipeChance) {
				if (inventory[2] == null) {
					inventory[2] = ItemHelper.cloneStack(secondaryItem);
				} else if (inventory[2].isItemEqual(secondaryItem)) {
					inventory[2].stackSize += secondaryItem.stackSize;
				}
				if (recipeChance > modifiedChance && worldObj.rand.nextInt(SECONDARY_BASE) < recipeChance - modifiedChance) {
					inventory[2].stackSize += secondaryItem.stackSize;
				}
				if (inventory[2].stackSize > inventory[2].getMaxStackSize()) {
					inventory[2].stackSize = inventory[2].getMaxStackSize();
				}
			}
		}
		if (augmentTapper && TapperManager.mappingExists(inventory[0])) {
			tank.fill(TapperManager.getFluid(inventory[0]).copy(), true);
		}
		inventory[0].stackSize -= recipe.getInput().stackSize;

		if (inventory[0].stackSize <= 0) {
			inventory[0] = null;
		}
	}

	@Override
	protected void transferInput() {

		if (!enableAutoInput) {
			return;
		}
		int side;
		for (int i = inputTracker + 1; i <= inputTracker + 6; i++) {
			side = i % 6;
			if (sideCache[side] == 1) {
				if (extractItem(0, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					inputTracker = side;
					break;
				}
			}
		}
	}

	@Override
	protected void transferOutput() {

		if (!enableAutoOutput) {
			return;
		}
		int side;
		if (inventory[1] != null) {
			for (int i = outputTrackerPrimary + 1; i <= outputTrackerPrimary + 6; i++) {
				side = i % 6;
				if (sideCache[side] == 2 || sideCache[side] == 4) {
					if (transferItem(1, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
						outputTrackerPrimary = side;
						break;
					}
				}
			}
		}
		if (inventory[2] == null) {
			return;
		}
		for (int i = outputTrackerSecondary + 1; i <= outputTrackerSecondary + 6; i++) {
			side = i % 6;
			if (sideCache[side] == 3 || sideCache[side] == 4) {
				if (transferItem(2, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					outputTrackerSecondary = side;
					break;
				}
			}
		}
	}

	private void transferOutputFluid() {

		if (!enableAutoOutput) {
			return;
		}
		if (tank.getFluidAmount() <= 0) {
			return;
		}
		int side;
		FluidStack output = new FluidStack(tank.getFluid(), Math.min(tank.getFluidAmount(), FLUID_TRANSFER[level]));
		for (int i = outputTrackerFluid + 1; i <= outputTrackerFluid + 6; i++) {
			side = i % 6;

			if (sideCache[side] == 3 || sideCache[side] == 4) {
				int toDrain = FluidHelper.insertFluidIntoAdjacentFluidHandler(this, EnumFacing.VALUES[side], output, true);

				if (toDrain > 0) {
					tank.drain(toDrain, true);
					outputTrackerFluid = side;
					break;
				}
			}
		}
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiSawmill(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerSawmill(inventory, this);
	}

	@Override
	public FluidTankCore getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	public boolean augmentTapper() {

		return augmentTapper && flagTapper;
	}

	public boolean fluidArrow() {

		return augmentTapper && TapperManager.mappingExists(inventory[0]);
	}

	public FluidStack getRenderFluid() {

		return renderFluid;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger("TrackIn");
		outputTrackerPrimary = nbt.getInteger("TrackOut1");
		outputTrackerSecondary = nbt.getInteger("TrackOut2");
		outputTrackerFluid = nbt.getInteger("TrackOut3");
		tank.readFromNBT(nbt);

		if (tank.getFluid() != null) {
			renderFluid = tank.getFluid().copy();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut1", outputTrackerPrimary);
		nbt.setInteger("TrackOut2", outputTrackerSecondary);
		nbt.setInteger("Trackout3", outputTrackerFluid);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addBool(augmentTapper);
		if (tank.getFluid() == null) {
			payload.addFluidStack(renderFluid);
		} else {
			payload.addFluidStack(tank.getFluid());
		}
		return payload;
	}

	@Override
	public PacketCoFHBase getTilePacket() {

		PacketCoFHBase payload = super.getTilePacket();

		payload.addFluidStack(renderFluid);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		augmentTapper = payload.getBool();
		flagTapper = augmentTapper;
		renderFluid = payload.getFluidStack();
		tank.setFluid(renderFluid);
	}

	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		renderFluid = payload.getFluidStack();
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		augmentTapper = false;
	}

	@Override
	protected void postAugmentInstall() {

		super.postAugmentInstall();

		if (!augmentTapper) {
			tank.setFluid(null);
		}
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentTapper && TEProps.MACHINE_SAWMILL_TAPPER.equals(id)) {
			augmentTapper = true;
			hasModeAugment = true;
			energyMod += 50;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot != 0 || SawmillManager.recipeExists(stack);
	}

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return super.hasCapability(capability, from) || augmentTapper && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (augmentTapper && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler() {
				@Override
				public IFluidTankProperties[] getTankProperties() {

					FluidTankInfo info = tank.getInfo();
					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, true, false) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (from != null && sideCache[from.ordinal()] < 3) {
						return null;
					}
					if (resource == null || !resource.isFluidEqual(tank.getFluid())) {
						return null;
					}
					return tank.drain(resource.amount, doDrain);
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (from != null && sideCache[from.ordinal()] < 3) {
						return null;
					}
					return tank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
