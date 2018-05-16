package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.util.core.EnergyConfig;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import cofh.core.util.helpers.*;
import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine.Type;
import cofh.thermalexpansion.gui.client.machine.GuiCharger;
import cofh.thermalexpansion.gui.container.machine.ContainerCharger;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.item.ItemCapacitor;
import cofh.thermalexpansion.util.managers.machine.ChargerManager;
import cofh.thermalexpansion.util.managers.machine.ChargerManager.ChargerRecipe;
import cofh.thermalfoundation.init.TFFluids;
import com.google.common.collect.Iterables;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static cofh.core.util.core.SideConfig.*;

public class TileCharger extends TileMachineBase {

	private static final int TYPE = Type.CHARGER.getMetadata();
	private static final int ENERGY_TRANSFER[] = new int[] { 1, 4, 9, 16, 25 };
	public static int basePower = 50;

	public static int repairEnergy = 500;
	public static int repairFluid = CoreProps.MB_PER_XP / 4;
	public static int wirelessRange = 32;

	//public static final int WIRELESS_RANGE[] = new int[] { 3, 5, 7, 9, 11 };

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 2 }, { 0, 2 }, { 0, 2 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_ALL, OPEN, OMNI };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		ALT_SIDE_CONFIGS[TYPE] = new SideConfig();
		ALT_SIDE_CONFIGS[TYPE].numConfig = 2;
		ALT_SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 2 }, { 0, 2 }, { 0, 2 } };
		ALT_SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, OPEN };
		ALT_SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 1, 1, 1, 1 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, false, false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false, false, true, false };

		VALID_AUGMENTS[TYPE] = new HashSet<>();
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_CHARGER_THROUGHPUT);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_CHARGER_REPAIR);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_CHARGER_WIRELESS);

		LIGHT_VALUES[TYPE] = 7;

		for (int i = 0; i < ENERGY_TRANSFER.length; i++) {
			ENERGY_TRANSFER[i] *= 2000;
		}
		GameRegistry.registerTileEntity(TileCharger.class, "thermalexpansion:machine_charger");

		config();
	}

	public static void config() {

		String category = "Machine.Charger";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy consumption (in RF/t) for an Energetic Infuser. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower, smallStorage);

		comment = "Adjust this value to change the amount of RF per point of durability in the Energetic Infuser with the Flux Reconstruction augment.";
		repairEnergy = ThermalExpansion.CONFIG.getConfiguration().getInt("RepairEnergy", category, repairEnergy, 100, 10000, comment);

		comment = "Adjust this value to change the amount of Essence of Knowledge per point of durability in the Energetic Infuser with the Flux Reconstruction augment.";
		repairFluid = ThermalExpansion.CONFIG.getConfiguration().getInt("RepairFluid", category, repairFluid, 1, 1000, comment);

		comment = "Adjust this value to change the wireless range for the Energetic Infuser with the Parabolic Flux Coupling augment.";
		wirelessRange = ThermalExpansion.CONFIG.getConfiguration().getInt("WirelessRange", category, wirelessRange, 8, 128, comment);

		wirelessRange = wirelessRange * wirelessRange;
	}

	private int inputTracker;
	private int outputTracker;

	private IEnergyContainerItem containerItem = null;
	private boolean hasContainerItem = false;

	private IEnergyStorage handler = null;
	private boolean hasEnergyHandler = false;

	private boolean hasRepairItem = false;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	private List<EntityPlayer> players = new ArrayList<>();

	/* AUGMENTS */
	protected boolean augmentThroughput;
	protected boolean augmentRepair;
	protected boolean augmentWireless;
	protected boolean flagRepair;
	protected boolean flagWireless;

	public TileCharger() {

		super();
		inventory = new ItemStack[1 + 1 + 1 + 1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);
		tank.setLock(TFFluids.fluidExperience);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	/* CONTAINER ITEM */
	private void updateContainerItem() {

		boolean curActive = isActive;

		if (augmentWireless || augmentRepair) {
			transferContainerItem();
			processOff();
		} else if (isActive) {
			processTickContainerItem();

			if (canFinishContainerItem()) {
				transferContainerItem();
				transferOutput();
				transferInput();

				if (!redstoneControlOrDisable() || !canStartContainerItem()) {
					processOff();
				} else {
					processTickContainerItem();
				}
			}
		} else if (redstoneControlOrDisable()) {
			if (timeCheck()) {
				transferOutput();
				transferInput();
			}
			if (timeCheckEighth() && canStartContainerItem()) {
				processTickContainerItem();
				isActive = true;
			}
		}
		updateIfChanged(curActive);
		chargeEnergy();
	}

	private boolean canStartContainerItem() {

		if (!EnergyHelper.isEnergyContainerItem(inventory[1]) || augmentRepair || augmentWireless) {
			containerItem = null;
			hasContainerItem = false;
			return false;
		}
		return true;
	}

	private boolean canFinishContainerItem() {

		return containerItem.getEnergyStored(inventory[1]) >= containerItem.getMaxEnergyStored(inventory[1]) || augmentRepair || augmentWireless;
	}

	private int processTickContainerItem() {

		int energy = containerItem.receiveEnergy(inventory[1], calcEnergyItem(), false);
		energyStorage.modifyEnergyStored(-energy);
		return energy;
	}

	/* HANDLER */
	private void updateHandler() {

		boolean curActive = isActive;

		if (augmentWireless || augmentRepair) {
			transferHandler();
			processOff();
		} else if (isActive) {
			processTickHandler();

			if (canFinishHandler()) {
				transferHandler();
				transferOutput();
				transferInput();

				if (!redstoneControlOrDisable() || !canStartHandler()) {
					processOff();
				} else {
					processTickHandler();
				}
			}
		} else if (redstoneControlOrDisable()) {
			if (timeCheck()) {
				transferOutput();
				transferInput();
			}
			if (timeCheckEighth() && canStartHandler()) {
				processTickHandler();
				isActive = true;
			}
		}
		updateIfChanged(curActive);
		chargeEnergy();
	}

	private boolean canStartHandler() {

		if (!EnergyHelper.isEnergyHandler(inventory[1]) || augmentRepair || augmentWireless) {
			handler = null;
			hasEnergyHandler = false;
			return false;
		}
		return true;
	}

	private boolean canFinishHandler() {

		return handler.canReceive() && handler.getEnergyStored() >= handler.getMaxEnergyStored() || augmentRepair || augmentWireless;
	}

	private int processTickHandler() {

		int energy = handler.receiveEnergy(calcEnergyItem(), false);
		energyStorage.modifyEnergyStored(-energy);
		return energy;
	}

	/* REPAIR ITEM */
	public void updateRepairItem() {

		boolean curActive = isActive;

		if (isActive) {
			processTickRepairItem();

			if (canFinishRepairItem()) {
				transferRepairItem();
				transferOutput();
				transferInput();
				energyStorage.modifyEnergyStored(processRem);
				processRem = 0;

				if (!redstoneControlOrDisable() || !canStartRepairItem()) {
					processOff();
				} else {
					processTickRepairItem();
				}
			}
		} else if (redstoneControlOrDisable()) {
			if (timeCheck()) {
				transferOutput();
				transferInput();
			}
			if (timeCheckEighth() && canStartRepairItem()) {
				processTickRepairItem();
				isActive = true;
			}
		}
		updateIfChanged(curActive);
		chargeEnergy();
	}

	private boolean canStartRepairItem() {

		if (!inventory[1].isItemStackDamageable() || !augmentRepair) {
			hasRepairItem = false;
			return false;
		}
		return true;
	}

	private boolean canFinishRepairItem() {

		return inventory[1].getItemDamage() <= 0 || !augmentRepair;
	}

	private int processTickRepairItem() {

		if (inventory[1].getItemDamage() <= 0) {
			return 0;
		}
		int energy = calcEnergyRepair();
		processRem += energy;
		if (processRem >= repairEnergy) {
			inventory[1].setItemDamage(inventory[1].getItemDamage() - 1);
			tank.modifyFluidStored(-repairFluid);
			processRem -= repairEnergy;
		}
		energyStorage.modifyEnergyStored(-energy);
		return energy;
	}

	/* WIRELESS CHARGING */
	public void updateWireless() {

		boolean curActive = isActive;

		if (timeCheckEighth()) {
			findPlayersInRange();
		}
		if (isActive) {
			processTickWireless();

			if (!redstoneControlOrDisable() || calcEnergyWireless() <= 0) {
				processOff();
			}
		} else if (redstoneControlOrDisable()) {
			if (timeCheckEighth() && calcEnergyWireless() > 0) {
				processTickWireless();
				isActive = true;
			}
		}
		updateIfChanged(curActive);
		chargeEnergy();
	}

	private void findPlayersInRange() {

		players.clear();
		Vec3d posCenter = new Vec3d(pos).addVector(0.5D, 0.5D, 0.5D);
		for (EntityPlayer player : world.playerEntities) {
			if (player.getPositionVector().squareDistanceTo(posCenter) < wirelessRange) {
				players.add(player);
			}
		}
	}

	private int processTickWireless() {

		int energy = 0;
		for (EntityPlayer player : players) {
			energy += chargeInventoryItem(player.inventory, false);
		}
		return energy;
	}

	private int chargeInventoryItem(InventoryPlayer inventory, boolean simulate) {

		Iterable<ItemStack> equipment = Iterables.concat(Arrays.asList(BaublesHelper.getBaubles(inventory.player), inventory.mainInventory));

		for (ItemStack stack : equipment) {
			if (energyStorage.getEnergyStored() <= 0) {
				return 0;
			}
			if (!stack.isEmpty() && stack.getItem() instanceof ItemCapacitor) {
				int energy = ((IEnergyContainerItem) stack.getItem()).receiveEnergy(stack, Math.min(energyStorage.getEnergyStored(), getEnergyTransfer(level) / 2), simulate);
				if (energy > 0) {
					if (!simulate) {
						energyStorage.modifyEnergyStored(-energy);
					}
					return energy;
				}
			}
		}
		return 0;
	}

	/* STANDARD */
	@Override
	public void update() {

		if (hasContainerItem) {
			updateContainerItem();
		} else if (hasEnergyHandler) {
			updateHandler();
		} else if (hasRepairItem) {
			updateRepairItem();
		} else if (augmentWireless) {
			updateWireless();
		} else {
			super.update();
		}
	}

	@Override
	protected int getMaxInputSlot() {

		// This is a hack to prevent super() logic from working.
		return -1;
	}

	@Override
	protected boolean canStart() {

		if (!inventory[1].isEmpty()) {
			if (energyStorage.getEnergyStored() <= 0 || hasContainerItem || hasEnergyHandler) {
				return false;
			}
			if (!augmentWireless) {
				ChargerRecipe recipe = ChargerManager.getRecipe(inventory[1]);
				if (recipe == null) {
					return false;
				}
				if (inventory[1].getCount() < recipe.getInput().getCount()) {
					return false;
				}
				ItemStack output = recipe.getOutput();
				return inventory[2].isEmpty() || inventory[2].isItemEqual(output) && inventory[2].getCount() + output.getCount() <= output.getMaxStackSize();
			}
		}
		if (inventory[0].isEmpty() || energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		if (!augmentWireless && !augmentRepair) {
			if (!hasContainerItem && EnergyHelper.isEnergyContainerItem(inventory[0])) {
				inventory[1] = ItemHelper.cloneStack(inventory[0], 1);
				inventory[0].shrink(1);

				if (inventory[0].getCount() <= 0) {
					inventory[0] = ItemStack.EMPTY;
				}
				containerItem = (IEnergyContainerItem) inventory[1].getItem();
				hasContainerItem = true;
				return false;
			}
			if (!hasEnergyHandler && EnergyHelper.isEnergyHandler(inventory[0])) {
				inventory[1] = ItemHelper.cloneStack(inventory[0], 1);
				inventory[0].shrink(1);

				if (inventory[0].getCount() <= 0) {
					inventory[0] = ItemStack.EMPTY;
				}
				handler = inventory[1].getCapability(CapabilityEnergy.ENERGY, null);
				hasEnergyHandler = true;
				return false;
			}
		} else if (!augmentWireless) {
			if (!hasRepairItem && inventory[0].isItemStackDamageable()) {
				inventory[1] = ItemHelper.cloneStack(inventory[0], 1);
				inventory[0].shrink(1);

				if (inventory[0].getCount() <= 0) {
					inventory[0] = ItemStack.EMPTY;
				}
				processRem = 0;
				hasRepairItem = true;
				return false;
			}
		} else {
			return true;
		}
		ChargerRecipe recipe = ChargerManager.getRecipe(inventory[0]);
		if (recipe == null) {
			return false;
		}
		if (inventory[0].getCount() < recipe.getInput().getCount()) {
			return false;
		}
		ItemStack output = recipe.getOutput();
		return inventory[2].isEmpty() || inventory[2].isItemEqual(output) && inventory[2].getCount() + output.getCount() <= output.getMaxStackSize();
	}

	@Override
	protected boolean hasValidInput() {

		if (augmentWireless) {
			return true;
		}
		if (hasContainerItem || hasEnergyHandler || augmentRepair && hasRepairItem) {
			return true;
		}
		ChargerRecipe recipe = ChargerManager.getRecipe(inventory[1]);
		return recipe != null && recipe.getInput().getCount() <= inventory[1].getCount();
	}

	@Override
	protected void processStart() {

		if (!inventory[1].isEmpty()) {
			ChargerRecipe recipe = ChargerManager.getRecipe(inventory[1]);
			processMax = recipe.getEnergy() * energyMod / ENERGY_BASE;
			processRem = processMax;
		} else {
			ChargerRecipe recipe = ChargerManager.getRecipe(inventory[0]);
			processMax = recipe.getEnergy() * energyMod / ENERGY_BASE;
			processRem = processMax;

			inventory[1] = ItemHelper.cloneStack(inventory[0], recipe.getInput().getCount());
			inventory[0].shrink(recipe.getInput().getCount());

			if (inventory[0].getCount() <= 0) {
				inventory[0] = ItemStack.EMPTY;
			}
		}
	}

	@Override
	protected void processFinish() {

		ChargerRecipe recipe = ChargerManager.getRecipe(inventory[1]);

		if (recipe == null) {
			processOff();
			return;
		}
		ItemStack output = recipe.getOutput();
		if (inventory[2].isEmpty()) {
			inventory[2] = ItemHelper.cloneStack(output);
		} else {
			inventory[2].grow(output.getCount());
		}
		inventory[1] = ItemStack.EMPTY;
	}

	@Override
	protected void transferInput() {

		if (!getTransferIn()) {
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

		if (!getTransferOut()) {
			return;
		}
		if (inventory[2].isEmpty()) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				if (transferItem(2, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	private void transferContainerItem() {

		if (hasContainerItem) {
			if (inventory[2].isEmpty()) {
				inventory[2] = ItemHelper.cloneStack(inventory[1], 1);
				inventory[1] = ItemStack.EMPTY;
				containerItem = null;
				hasContainerItem = false;
			} else {
				if (ItemHelper.itemsIdentical(inventory[1], inventory[2]) && inventory[1].getMaxStackSize() > 1 && inventory[2].getCount() + 1 <= inventory[2].getMaxStackSize()) {
					inventory[2].grow(1);
					inventory[1] = ItemStack.EMPTY;
					containerItem = null;
					hasContainerItem = false;
				}
			}
		}
		if (!hasContainerItem && EnergyHelper.isEnergyContainerItem(inventory[0]) && !augmentRepair) {
			inventory[1] = ItemHelper.cloneStack(inventory[0], 1);
			inventory[0].shrink(1);

			if (inventory[0].getCount() <= 0) {
				inventory[0] = ItemStack.EMPTY;
			}
			containerItem = (IEnergyContainerItem) inventory[1].getItem();
			hasContainerItem = true;
		}
	}

	private void transferHandler() {

		if (hasEnergyHandler) {
			if (inventory[2].isEmpty()) {
				inventory[2] = ItemHelper.cloneStack(inventory[1], 1);
				inventory[1] = ItemStack.EMPTY;
				handler = null;
				hasEnergyHandler = false;
			} else {
				if (ItemHelper.itemsIdentical(inventory[1], inventory[2]) && inventory[1].getMaxStackSize() > 1 && inventory[2].getCount() + 1 <= inventory[2].getMaxStackSize()) {
					inventory[2].grow(1);
					inventory[1] = ItemStack.EMPTY;
					handler = null;
					hasEnergyHandler = false;
				}
			}
		}
		if (!hasEnergyHandler && EnergyHelper.isEnergyHandler(inventory[0]) && !augmentRepair) {
			inventory[1] = ItemHelper.cloneStack(inventory[0], 1);
			inventory[0].shrink(1);

			if (inventory[0].getCount() <= 0) {
				inventory[0] = ItemStack.EMPTY;
			}
			handler = inventory[1].getCapability(CapabilityEnergy.ENERGY, null);
			hasEnergyHandler = true;
		}
	}

	private void transferRepairItem() {

		if (hasRepairItem) {
			if (inventory[2].isEmpty()) {
				inventory[2] = ItemHelper.cloneStack(inventory[1], 1);
				inventory[1] = ItemStack.EMPTY;
				hasRepairItem = false;
			} else {
				if (ItemHelper.itemsIdentical(inventory[1], inventory[2]) && inventory[1].getMaxStackSize() > 1 && inventory[2].getCount() + 1 <= inventory[2].getMaxStackSize()) {
					inventory[2].grow(1);
					inventory[1] = ItemStack.EMPTY;
					hasRepairItem = false;
				}
			}
		}
		if (!hasRepairItem && inventory[0].isItemStackDamageable() && augmentRepair) {
			inventory[1] = ItemHelper.cloneStack(inventory[0], 1);
			inventory[0].shrink(1);

			if (inventory[0].getCount() <= 0) {
				inventory[0] = ItemStack.EMPTY;
			}
			hasRepairItem = true;
		}
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiCharger(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerCharger(inventory, this);
	}

	@Override
	public FluidTankCore getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	public boolean augmentRepair() {

		return augmentRepair && flagRepair;
	}

	public boolean augmentWireless() {

		return augmentWireless && flagWireless;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger(CoreProps.TRACK_IN);
		outputTracker = nbt.getInteger(CoreProps.TRACK_OUT);

		if (augmentRepair && inventory[1].isItemStackDamageable()) {
			hasRepairItem = true;
		} else if (EnergyHelper.isEnergyContainerItem(inventory[1])) {
			containerItem = (IEnergyContainerItem) inventory[1].getItem();
			hasContainerItem = true;
		} else if (EnergyHelper.isEnergyHandler(inventory[1])) {
			handler = inventory[1].getCapability(CapabilityEnergy.ENERGY, null);
			hasEnergyHandler = true;
		} else if (inventory[1].isItemStackDamageable()) {
			hasRepairItem = true;
			/*
			 * This seems weird to have twice but it's a catch for the case where the repair augment is removed
			 * and the output slot is full and remains such until the chunk is unloaded.
			 */
		}
		tank.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger(CoreProps.TRACK_IN, inputTracker);
		nbt.setInteger(CoreProps.TRACK_OUT, outputTracker);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addBool(augmentThroughput);
		payload.addBool(augmentRepair);
		payload.addBool(augmentWireless);

		payload.addBool(hasContainerItem);
		payload.addBool(hasEnergyHandler);
		payload.addBool(hasRepairItem);
		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		augmentThroughput = payload.getBool();
		augmentRepair = payload.getBool();
		augmentWireless = payload.getBool();
		flagRepair = augmentRepair;
		flagWireless = augmentWireless;

		hasContainerItem = payload.getBool();
		hasEnergyHandler = payload.getBool();
		hasRepairItem = payload.getBool();
		tank.setFluid(payload.getFluidStack());
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		augmentThroughput = false;
		augmentRepair = false;
		augmentWireless = false;
	}

	@Override
	protected void postAugmentInstall() {

		super.postAugmentInstall();

		if (augmentThroughput) {
			energyStorage.setMaxTransfer(getEnergyTransfer(level) * 4);
		}
		if (!augmentRepair) {
			tank.drain(tank.getCapacity(), true);
		}
		if (augmentWireless) {
			if (processRem > 0) {
				processOff();
			}
			energyStorage.setMaxTransfer(getEnergyTransfer(level) * 2);
		}
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentThroughput && TEProps.MACHINE_CHARGER_THROUGHPUT.equals(id)) {
			augmentThroughput = true;
			hasModeAugment = true;
			return true;
		}
		if (!augmentRepair && TEProps.MACHINE_CHARGER_REPAIR.equals(id)) {
			augmentRepair = true;
			hasModeAugment = true;
			tank.setLock(TFFluids.fluidExperience);
			return true;
		}
		if (!augmentWireless && TEProps.MACHINE_CHARGER_WIRELESS.equals(id)) {
			augmentWireless = true;
			hasModeAugment = true;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	private int calcEnergyItem() {

		if (!augmentThroughput) {
			return calcEnergy();
		}
		return Math.min(energyStorage.getEnergyStored(), getEnergyTransfer(level));
	}

	private int calcEnergyRepair() {

		if (tank.getFluidAmount() < repairFluid) {
			return 0;
		}
		return Math.max(calcEnergy(), repairEnergy);
	}

	private int calcEnergyWireless() {

		if (energyStorage.getEnergyStored() <= 0) {
			return 0;
		}
		int energy = 0;
		for (EntityPlayer player : players) {
			energy += chargeInventoryItem(player.inventory, true);
		}
		return energy;
	}

	private int getEnergyTransfer(int level) {

		return ENERGY_TRANSFER[MathHelper.clamp(level, 0, 4)];
	}

	/* IEnergyInfo */
	@Override
	public int getInfoEnergyPerTick() {

		if (!isActive) {
			return 0;
		}
		if (augmentWireless) {
			return getEnergyTransfer(level) / 2;
		} else if ((EnergyHelper.isEnergyContainerItem(inventory[1]) || EnergyHelper.isEnergyHandler(inventory[1])) && augmentThroughput) {
			return getEnergyTransfer(level);
		} else {
			return calcEnergy();
		}
	}

	@Override
	public int getInfoMaxEnergyPerTick() {

		if (augmentWireless) {
			return getEnergyTransfer(level) / 2;
		} else if ((EnergyHelper.isEnergyContainerItem(inventory[1]) || EnergyHelper.isEnergyHandler(inventory[1])) && augmentThroughput) {
			return getEnergyTransfer(level);
		} else {
			return energyConfig.maxPower;
		}
	}

	/* IAccelerable */
	@Override
	public int updateAccelerable() {

		if (hasContainerItem) {
			return processTickContainerItem();
		} else if (hasEnergyHandler) {
			return processTickHandler();
		} else if (hasRepairItem) {
			return processTickRepairItem();
		} else if (augmentWireless) {
			return processTickWireless();
		} else {
			return processTick();
		}
	}

	/* IInventory */
	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		ItemStack stack = super.decrStackSize(slot, amount);

		if (ServerHelper.isServerWorld(world) && slot == 1) {
			if (isActive && (inventory[slot].isEmpty() || !hasValidInput())) {
				processOff();
				containerItem = null;
				hasContainerItem = false;

				handler = null;
				hasEnergyHandler = false;

				hasRepairItem = false;
			}
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		if (ServerHelper.isServerWorld(world) && slot == 1) {
			if (isActive && !inventory[slot].isEmpty()) {
				if (stack.isEmpty() || !stack.isItemEqual(inventory[slot]) || !hasValidInput()) {
					isActive = false;
					wasActive = true;
					tracker.markTime(world);
					processRem = 0;
				}
			}
			containerItem = null;
			hasContainerItem = false;

			handler = null;
			hasEnergyHandler = false;

			hasRepairItem = false;
		}
		inventory[slot] = stack;

		//		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
		//			stack.setCount(getInventoryStackLimit());
		//		}
		markChunkDirty();
	}

	@Override
	public void markDirty() {

		if (isActive && !hasValidInput()) {
			containerItem = null;
			hasContainerItem = false;

			handler = null;
			hasEnergyHandler = false;

			hasRepairItem = false;
		}
		super.markDirty();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		if (augmentWireless) {
			return false;
		}
		if (augmentRepair) {
			return slot != 0 || stack.isItemStackDamageable() || ChargerManager.recipeExists(stack);
		}
		if (slot != 0) {
			return true;
		}
		if (EnergyHelper.isEnergyContainerItem(stack)) {
			IEnergyContainerItem containerItem = (IEnergyContainerItem) stack.getItem();
			return containerItem.getEnergyStored(stack) < containerItem.getMaxEnergyStored(stack);
		}
		if (EnergyHelper.isEnergyHandler(stack)) {
			IEnergyStorage containerItem = EnergyHelper.getEnergyHandler(stack);
			return containerItem.getEnergyStored() < containerItem.getMaxEnergyStored();
		}
		return ChargerManager.recipeExists(stack);
	}

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return super.hasCapability(capability, from) || augmentRepair && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (augmentRepair && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler() {

				@Override
				public IFluidTankProperties[] getTankProperties() {

					FluidTankInfo info = tank.getInfo();
					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, true, false) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					return tank.fill(resource, doFill);
					//					if (from == null || allowInsertion(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
					//						return tank.fill(resource, doFill);
					//					}
					//					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (isActive) {
						return null;
					}
					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return tank.drain(resource, doDrain);
					}
					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (isActive) {
						return null;
					}
					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return tank.drain(maxDrain, doDrain);
					}
					return null;
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
