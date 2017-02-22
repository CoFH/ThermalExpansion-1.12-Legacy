package cofh.thermalexpansion.block.machine;

import cofh.api.energy.IEnergyContainerItem;
import cofh.lib.util.helpers.*;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.machine.GuiCharger;
import cofh.thermalexpansion.gui.container.machine.ContainerCharger;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.crafting.ChargerManager;
import cofh.thermalexpansion.util.crafting.ChargerManager.RecipeCharger;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;

public class TileCharger extends TileMachineBase {

	private static final int TYPE = BlockMachine.Type.CHARGER.getMetadata();

	private static final int ENERGY_TRANSFER[] = new int[] { 1000, 4000, 9000, 16000, 25000 };

	public static void initialize() {

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numConfig = 4;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, { 0 }, { 2 }, { 0, 2 } };
		defaultSideConfig[TYPE].allowInsertionSide = new boolean[] { false, true, false, true };
		defaultSideConfig[TYPE].allowExtractionSide = new boolean[] { false, true, true, true };
		defaultSideConfig[TYPE].allowInsertionSlot = new boolean[] { true, false, false, false };
		defaultSideConfig[TYPE].allowExtractionSlot = new boolean[] { true, false, true, false };
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 1, 4, 7 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		validAugments[TYPE] = new ArrayList<String>();
		validAugments[TYPE].add(TEProps.MACHINE_CHARGER_THROUGHPUT);

		lightValue[TYPE] = 7;

		GameRegistry.registerTileEntity(TileCharger.class, "thermalexpansion:machine_charger");

		config();
	}

	public static void config() {

		String category = "Machine.Charger";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setDefaultParams(50);

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
				transferOutput();
				transferInput();
			}
			if (!redstoneControlOrDisable() || !canStartContainerItem()) {
				processOff();
			} else {
				processTickContainerItem();
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

		return containerItem.getEnergyStored(inventory[0]) >= containerItem.getMaxEnergyStored(inventory[0]);
	}

	private void processTickContainerItem() {

		energyStorage.modifyEnergyStored(-containerItem.receiveEnergy(inventory[1], calcEnergyItem(), false));
	}

	/* HANDLER */
	private void updateHandler() {

		boolean curActive = isActive;

		if (isActive) {
			processTickHandler();

			if (canFinishHandler()) {
				transferOutput();
				transferInput();
			}
			if (!redstoneControlOrDisable() || !canStartHandler()) {
				processOff();
			} else {
				processTickHandler();
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

	private void processTickHandler() {

		energyStorage.modifyEnergyStored(-handler.receiveEnergy(calcEnergy(), false));
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

	/* STANDARD */
	@Override
	public void update() {

		if (ServerHelper.isClientWorld(worldObj)) {
			if (inventory[1] == null) {
				processRem = 0;
				containerItem = null;
				hasContainerItem = false;
				handler = null;
				hasEnergyHandler = false;
			} else if (EnergyHelper.isEnergyContainerItem(inventory[1])) {
				containerItem = (IEnergyContainerItem) inventory[1].getItem();
				hasContainerItem = true;
			} else if (EnergyHelper.isEnergyHandler(inventory[1])) {
				handler = inventory[1].getCapability(CapabilityEnergy.ENERGY, null);
				hasEnergyHandler = true;
			}
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

		if (inventory[0] == null || energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		if (!hasContainerItem && EnergyHelper.isEnergyContainerItem(inventory[0])) {
			inventory[1] = ItemHelper.cloneStack(inventory[0], 1);
			inventory[0].stackSize--;

			if (inventory[0].stackSize <= 0) {
				inventory[0] = null;
			}
			containerItem = (IEnergyContainerItem) inventory[1].getItem();
			hasContainerItem = true;
			return false;
		}
		if (!hasEnergyHandler && EnergyHelper.isEnergyHandler(inventory[0])) {
			inventory[1] = ItemHelper.cloneStack(inventory[0], 1);
			inventory[0].stackSize--;

			if (inventory[0].stackSize <= 0) {
				inventory[0] = null;
			}
			handler = inventory[1].getCapability(CapabilityEnergy.ENERGY, null);
			hasEnergyHandler = true;
			return false;
		}
		RecipeCharger recipe = ChargerManager.getRecipe(inventory[0]);

		if (recipe == null) {
			return false;
		}
		if (inventory[0].stackSize < recipe.getInput().stackSize) {
			return false;
		}
		ItemStack output = recipe.getOutput();

		return inventory[2] == null || inventory[2].isItemEqual(output) && inventory[2].stackSize + output.stackSize <= output.getMaxStackSize();
	}

	@Override
	protected boolean hasValidInput() {

		if (hasContainerItem || hasEnergyHandler) {
			return true;
		}
		RecipeCharger recipe = ChargerManager.getRecipe(inventory[1]);
		return recipe != null && recipe.getInput().stackSize <= inventory[1].stackSize;
	}

	@Override
	protected void processStart() {

		RecipeCharger recipe = ChargerManager.getRecipe(inventory[0]);
		processMax = recipe.getEnergy() * energyMod / ENERGY_BASE;
		processRem = processMax;

		inventory[1] = ItemHelper.cloneStack(inventory[0], recipe.getInput().stackSize);
		inventory[0].stackSize -= recipe.getInput().stackSize;

		if (inventory[0].stackSize <= 0) {
			inventory[0] = null;
		}
	}

	@Override
	protected void processFinish() {

		RecipeCharger recipe = ChargerManager.getRecipe(inventory[1]);

		if (recipe == null) {
			processOff();
			return;
		}
		ItemStack output = recipe.getOutput();
		if (inventory[2] == null) {
			inventory[2] = ItemHelper.cloneStack(output);
		} else {
			inventory[2].stackSize += output.stackSize;
		}
		inventory[1] = null;
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

		transferContainerItem();
		transferHandler();

		if (!enableAutoOutput) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;

			if (sideCache[side] == 2) {
				if (transferItem(2, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	private void transferContainerItem() {

		if (hasContainerItem) {
			if (inventory[2] == null) {
				inventory[2] = ItemHelper.cloneStack(inventory[1], 1);
				inventory[1] = null;
				containerItem = null;
				hasContainerItem = false;
			} else {
				if (ItemHelper.itemsIdentical(inventory[1], inventory[2]) && inventory[1].getMaxStackSize() > 1 && inventory[2].stackSize + 1 <= inventory[2].getMaxStackSize()) {
					inventory[2].stackSize++;
					inventory[1] = null;
					containerItem = null;
					hasContainerItem = false;
				}
			}
		}
		if (!hasContainerItem && EnergyHelper.isEnergyContainerItem(inventory[0])) {
			inventory[1] = ItemHelper.cloneStack(inventory[0], 1);
			inventory[0].stackSize--;

			if (inventory[0].stackSize <= 0) {
				inventory[0] = null;
			}
			containerItem = (IEnergyContainerItem) inventory[1].getItem();
			hasContainerItem = true;
		}
	}

	private void transferHandler() {

		if (hasEnergyHandler) {
			if (inventory[2] == null) {
				inventory[2] = ItemHelper.cloneStack(inventory[1], 1);
				inventory[1] = null;
				handler = null;
				hasEnergyHandler = false;
			} else {
				if (ItemHelper.itemsIdentical(inventory[1], inventory[2]) && inventory[1].getMaxStackSize() > 1 && inventory[2].stackSize + 1 <= inventory[2].getMaxStackSize()) {
					inventory[2].stackSize++;
					inventory[1] = null;
					handler = null;
					hasEnergyHandler = false;
				}
			}
		}
		if (!hasEnergyHandler && EnergyHelper.isEnergyHandler(inventory[0])) {
			inventory[1] = ItemHelper.cloneStack(inventory[0], 1);
			inventory[0].stackSize--;

			if (inventory[0].stackSize <= 0) {
				inventory[0] = null;
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

		if (inventory[1] != null && EnergyHelper.isEnergyContainerItem(inventory[1])) {
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
		return super.installAugmentToSlot(slot);
	}

	/* IAccelerable */
	@Override
	public void updateAccelerable() {

		if (hasContainerItem) {
			processTickContainerItem();
		} else if (hasEnergyHandler) {
			processTickHandler();
		} else {
			processTick();
		}
	}

	/* IInventory */
	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		ItemStack stack = super.decrStackSize(slot, amount);

		if (ServerHelper.isServerWorld(worldObj) && slot == 1) {
			if (isActive && (inventory[slot] == null || !hasValidInput())) {
				isActive = false;
				wasActive = true;
				tracker.markTime(worldObj);
				processRem = 0;
				containerItem = null;
			}
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		if (ServerHelper.isServerWorld(worldObj) && slot == 1) {
			if (isActive && inventory[slot] != null) {
				if (stack == null || !stack.isItemEqual(inventory[slot]) || !hasValidInput()) {
					isActive = false;
					wasActive = true;
					tracker.markTime(worldObj);
					processRem = 0;
				}
			}
			containerItem = null;
		}
		inventory[slot] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
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
