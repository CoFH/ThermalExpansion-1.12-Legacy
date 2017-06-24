package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine.Type;
import cofh.thermalexpansion.gui.client.machine.GuiCentrifuge;
import cofh.thermalexpansion.gui.container.machine.ContainerCentrifuge;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager.CentrifugeRecipe;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class TileCentrifuge extends TileMachineBase {

	private static final int TYPE = Type.CENTRIFUGE.getMetadata();
	public static int basePower = 20;

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 7;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1, 2, 3, 4 }, {}, { 1, 2, 3, 4 }, { 0, 1, 2, 3, 4 }, { 0, 1, 2, 3, 4 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { 0, 1, 2, 3, 4, 7, 8 };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 3, 1, 2, 2, 2, 2 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, false, false, false, false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false, true, true, true, true, false };

		VALID_AUGMENTS[TYPE] = new HashSet<>();
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_SECONDARY_NULL);

		LIGHT_VALUES[TYPE] = 4;

		GameRegistry.registerTileEntity(TileCentrifuge.class, "thermalexpansion:machine_centrifuge");

		config();
	}

	public static void config() {

		String category = "Machine.Centrifuge";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower);
	}

	private int inputTracker;
	private int outputTracker;
	private int outputTrackerFluid;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);

	public TileCentrifuge() {

		super();
		inventory = new ItemStack[1 + 4 + 1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void update() {

		if (ServerHelper.isClientWorld(world)) {
			return;
		}
		transferOutputFluid();

		super.update();
	}

	@Override
	protected boolean canStart() {

		if (inventory[0].isEmpty() || energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		CentrifugeRecipe recipe = CentrifugeManager.getRecipe(inventory[0]);

		if (recipe == null) {
			return false;
		}
		if (inventory[0].getCount() < recipe.getInput().getCount()) {
			return false;
		}
		FluidStack fluid = recipe.getFluid();

		if (fluid != null && !augmentSecondaryNull && tank.fill(fluid, false) != fluid.amount) {
			return false;
		}
		List<ItemStack> outputs = recipe.getOutput();

		boolean valid = true;
		for (int i = 0; i < outputs.size(); i++) {
			ItemStack output = outputs.get(i);
			valid &= inventory[i + 1].isEmpty() || inventory[i + 1].isItemEqual(output) && inventory[i + 1].getCount() + output.getCount() <= output.getMaxStackSize();
		}
		return valid;
	}

	@Override
	protected boolean hasValidInput() {

		CentrifugeRecipe recipe = CentrifugeManager.getRecipe(inventory[0]);
		return recipe != null && recipe.getInput().getCount() <= inventory[0].getCount();
	}

	@Override
	protected void processStart() {

		processMax = CentrifugeManager.getRecipe(inventory[0]).getEnergy() * energyMod / ENERGY_BASE;
		processRem = processMax;
	}

	@Override
	protected void processFinish() {

		CentrifugeRecipe recipe = CentrifugeManager.getRecipe(inventory[0]);

		if (recipe == null) {
			processOff();
			return;
		}
		List<ItemStack> outputs = recipe.getOutput();

		for (int i = 0; i < outputs.size(); i++) {
			if (inventory[i + 1].isEmpty()) {
				inventory[i + 1] = ItemHelper.cloneStack(outputs.get(i));
			} else {
				inventory[i + 1].grow(outputs.get(i).getCount());
			}
		}
		FluidStack fluid = recipe.getFluid();
		tank.fill(fluid, true);

		inventory[0].shrink(recipe.getInput().getCount());

		if (inventory[0].getCount() <= 0) {
			inventory[0] = ItemStack.EMPTY;
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
			if (isPrimaryInput(sideConfig.sideTypes[sideCache[side]])) {
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
		boolean foundOutput = false;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				for (int j = 1; j < 5; j++) {
					if (transferItem(j, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
						foundOutput = true;
					}
				}
				if (foundOutput) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	private void transferOutputFluid() {

		if (!enableAutoOutput || tank.getFluidAmount() <= 0) {
			return;
		}
		int side;
		FluidStack output = new FluidStack(tank.getFluid(), Math.min(tank.getFluidAmount(), FLUID_TRANSFER[level]));
		for (int i = outputTrackerFluid + 1; i <= outputTrackerFluid + 6; i++) {
			side = i % 6;
			if (isSecondaryOutput(sideConfig.sideTypes[sideCache[side]])) {
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

		return new GuiCentrifuge(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerCentrifuge(inventory, this);
	}

	@Override
	public FluidTankCore getTank() {

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

		inputTracker = nbt.getInteger("TrackIn");
		outputTracker = nbt.getInteger("TrackOut1");
		outputTrackerFluid = nbt.getInteger("TrackOut2");
		tank.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut1", outputTracker);
		nbt.setInteger("TrackOut2", outputTrackerFluid);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addFluidStack(tank.getFluid());
		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		tank.setFluid(payload.getFluidStack());
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot != 0 || CentrifugeManager.recipeExists(stack);
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
					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, false, true) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (from != null && !allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
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

					if (from != null && !allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return null;
					}
					return tank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
