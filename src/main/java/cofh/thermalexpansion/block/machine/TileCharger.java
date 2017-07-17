package cofh.thermalexpansion.block.machine;

import cofh.core.network.PacketCoFHBase;
import cofh.core.util.helpers.*;
import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine.Type;
import cofh.thermalexpansion.gui.client.machine.GuiCharger;
import cofh.thermalexpansion.gui.container.machine.ContainerCharger;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.managers.machine.ChargerManager;
import cofh.thermalexpansion.util.managers.machine.ChargerManager.ChargerRecipe;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Arrays;
import java.util.HashSet;

public class TileCharger extends TileMachineBase {

	private static final int TYPE = Type.CHARGER.getMetadata();
	private static final int ENERGY_TRANSFER[] = new int[] { 1000, 4000, 9000, 16000, 25000 };
	public static int basePower = 50;

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 2 }, { 0, 2 }, { 0, 2 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { 0, 1, 4, 7, 8 };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, false, false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false, false, true, false };

		VALID_AUGMENTS[TYPE] = new HashSet<>();
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_CHARGER_THROUGHPUT);

		LIGHT_VALUES[TYPE] = 7;

		GameRegistry.registerTileEntity(TileCharger.class, "thermalexpansion:machine_charger");

		config();
	}

	public static void config() {

		String category = "Machine.Charger";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower);
	}

	private int inputTracker;
	private int outputTracker;

	private IEnergyContainerItem containerItem = null;
	private boolean hasContainerItem = false;

	private IEnergyStorage handler = null;
	private boolean hasEnergyHandler = false;

	/* AUGMENTS */
	protected boolean augmentThroughput;

	public TileCharger() {

		super();
		inventory = new ItemStack[1 + 1 + 1 + 1];
		Arrays.fill(inventory, ItemStack.EMPTY);

		createAllSlots(inventory.length);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	/* CONTAINER ITEM */
	private void updateContainerItem() {

		boolean curActive = isActive;

		if (isActive) {
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

		if (!EnergyHelper.isEnergyContainerItem(inventory[1])) {
			containerItem = null;
			hasContainerItem = false;
			return false;
		}
		return true;
	}

	private boolean canFinishContainerItem() {

		return containerItem.getEnergyStored(inventory[1]) >= containerItem.getMaxEnergyStored(inventory[1]);
	}

	private int processTickContainerItem() {

		int energy = containerItem.receiveEnergy(inventory[1], calcEnergyItem(), false);
		energyStorage.modifyEnergyStored(-energy);
		return energy;
	}

	/* HANDLER */
	private void updateHandler() {

		boolean curActive = isActive;

		if (isActive) {
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

		if (!EnergyHelper.isEnergyHandler(inventory[1])) {
			handler = null;
			hasEnergyHandler = false;
			return false;
		}
		return true;
	}

	private boolean canFinishHandler() {

		return handler.canReceive() && handler.getEnergyStored() >= handler.getMaxEnergyStored();
	}

	private int processTickHandler() {

		int energy = handler.receiveEnergy(calcEnergyItem(), false);
		energyStorage.modifyEnergyStored(-energy);
		return energy;
	}

	/* STANDARD */
	@Override
	public void update() {

		if (ServerHelper.isClientWorld(world)) {
			return;
		}
		if (hasContainerItem) {
			updateContainerItem();
		} else if (hasEnergyHandler) {
			updateHandler();
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

		if (inventory[0].isEmpty() || energyStorage.getEnergyStored() <= 0) {
			return false;
		}
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

		if (hasContainerItem || hasEnergyHandler) {
			return true;
		}
		ChargerRecipe recipe = ChargerManager.getRecipe(inventory[1]);
		return recipe != null && recipe.getInput().getCount() <= inventory[1].getCount();
	}

	@Override
	protected void processStart() {

		ChargerRecipe recipe = ChargerManager.getRecipe(inventory[0]);
		processMax = recipe.getEnergy() * energyMod / ENERGY_BASE;
		processRem = processMax;

		inventory[1] = ItemHelper.cloneStack(inventory[0], recipe.getInput().getCount());
		inventory[0].shrink(recipe.getInput().getCount());

		if (inventory[0].getCount() <= 0) {
			inventory[0] = ItemStack.EMPTY;
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
		if (!hasContainerItem && EnergyHelper.isEnergyContainerItem(inventory[0])) {
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
		if (!hasEnergyHandler && EnergyHelper.isEnergyHandler(inventory[0])) {
			inventory[1] = ItemHelper.cloneStack(inventory[0], 1);
			inventory[0].shrink(1);

			if (inventory[0].getCount() <= 0) {
				inventory[0] = ItemStack.EMPTY;
			}
			handler = inventory[1].getCapability(CapabilityEnergy.ENERGY, null);
			hasEnergyHandler = true;
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

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger("TrackIn");
		outputTracker = nbt.getInteger("TrackOut");

		if (!inventory[1].isEmpty() && EnergyHelper.isEnergyContainerItem(inventory[1])) {
			containerItem = (IEnergyContainerItem) inventory[1].getItem();
			hasContainerItem = true;
		} else if (EnergyHelper.isEnergyHandler(inventory[1])) {
			handler = inventory[1].getCapability(CapabilityEnergy.ENERGY, null);
			hasEnergyHandler = true;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut", outputTracker);
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addBool(augmentThroughput);
		payload.addBool(hasContainerItem);
		payload.addBool(hasEnergyHandler);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		augmentThroughput = payload.getBool();
		hasContainerItem = payload.getBool();
		hasEnergyHandler = payload.getBool();
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		augmentThroughput = false;
	}

	@Override
	protected void postAugmentInstall() {

		super.postAugmentInstall();

		if (augmentThroughput) {
			energyStorage.setMaxTransfer(getEnergyTransfer(level) * 4);
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
		return super.installAugmentToSlot(slot);
	}

	private int calcEnergyItem() {

		if (!augmentThroughput) {
			return calcEnergy();
		}
		return Math.min(energyStorage.getEnergyStored(), getEnergyTransfer(level));
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
		return (EnergyHelper.isEnergyContainerItem(inventory[1]) || EnergyHelper.isEnergyHandler(inventory[1])) && augmentThroughput ? getEnergyTransfer(level) : calcEnergy();
	}

	@Override
	public int getInfoMaxEnergyPerTick() {

		return (EnergyHelper.isEnergyContainerItem(inventory[1]) || EnergyHelper.isEnergyHandler(inventory[1])) && augmentThroughput ? getEnergyTransfer(level) : energyConfig.maxPower;
	}

	/* IAccelerable */
	@Override
	public int updateAccelerable() {

		if (hasContainerItem) {
			return processTickContainerItem();
		} else if (hasEnergyHandler) {
			return processTickHandler();
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
		}
		inventory[slot] = stack;

		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
			stack.setCount(getInventoryStackLimit());
		}
	}

	@Override
	public void markDirty() {

		if (isActive && !hasValidInput()) {
			containerItem = null;
			hasContainerItem = false;

			handler = null;
			hasEnergyHandler = false;
		}
		super.markDirty();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot != 0 || (EnergyHelper.isEnergyContainerItem(stack) || EnergyHelper.isEnergyHandler(stack) || ChargerManager.recipeExists(stack));
	}

}
