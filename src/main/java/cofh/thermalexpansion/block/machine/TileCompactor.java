package cofh.thermalexpansion.block.machine;

import cofh.core.network.PacketCoFHBase;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine.Type;
import cofh.thermalexpansion.gui.client.machine.GuiCompactor;
import cofh.thermalexpansion.gui.container.machine.ContainerCompactor;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.managers.machine.CompactorManager;
import cofh.thermalexpansion.util.managers.machine.CompactorManager.CompactorRecipe;
import cofh.thermalexpansion.util.managers.machine.CompactorManager.Mode;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Arrays;
import java.util.HashSet;

public class TileCompactor extends TileMachineBase {

	private static final int TYPE = Type.COMPACTOR.getMetadata();
	private static final Mode[] VALUES = new Mode[4];
	public static int basePower = 20;

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1 }, { 0, 1 }, { 0, 1 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_ALL, OPEN, OMNI };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false, true, false };

		VALID_AUGMENTS[TYPE] = new HashSet<>();
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_COMPACTOR_MINT);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_COMPACTOR_GEAR);

		VALUES[0] = Mode.PRESS;
		VALUES[1] = Mode.STORAGE;
		VALUES[2] = Mode.MINT;
		VALUES[3] = Mode.GEAR;

		GameRegistry.registerTileEntity(TileCompactor.class, "thermalexpansion:machine_compactor");

		config();
	}

	public static void config() {

		String category = "Machine.Compactor";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy consumption (in RF/t) for a Compactor. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower, smallStorage);
	}

	private int inputTracker;
	private int outputTracker;

	public byte mode;
	public byte modeFlag;

	/* AUGMENTS */
	protected boolean augmentMint;
	protected boolean augmentGear;

	public TileCompactor() {

		super();
		inventory = new ItemStack[1 + 1 + 1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	protected boolean canStart() {

		if (inventory[0].isEmpty() || energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		CompactorRecipe recipe = CompactorManager.getRecipe(inventory[0], VALUES[mode]);

		if (recipe == null) {
			return false;
		}
		if (inventory[0].getCount() < recipe.getInput().getCount()) {
			return false;
		}
		ItemStack output = recipe.getOutput();

		return inventory[1].isEmpty() || inventory[1].isItemEqual(output) && inventory[1].getCount() + output.getCount() <= output.getMaxStackSize();
	}

	@Override
	protected boolean hasValidInput() {

		CompactorRecipe recipe = CompactorManager.getRecipe(inventory[0], VALUES[mode]);
		return recipe != null && recipe.getInput().getCount() <= inventory[0].getCount();
	}

	@Override
	protected void processStart() {

		processMax = CompactorManager.getRecipe(inventory[0], VALUES[mode]).getEnergy() * energyMod / ENERGY_BASE;
		processRem = processMax;
	}

	@Override
	protected void processFinish() {

		CompactorRecipe recipe = CompactorManager.getRecipe(inventory[0], VALUES[mode]);

		if (recipe == null) {
			processOff();
			return;
		}
		ItemStack output = recipe.getOutput();
		if (inventory[1].isEmpty()) {
			inventory[1] = ItemHelper.cloneStack(output);
		} else {
			inventory[1].grow(output.getCount());
		}
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
		if (inventory[1].isEmpty()) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				if (transferItem(1, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiCompactor(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerCompactor(inventory, this);
	}

	public void toggleMode() {

		switch (VALUES[mode]) {
			case PRESS:
				setMode(1);
				break;
			case MINT:
			case GEAR:
				setMode(0);
				break;
			case STORAGE:
				setMode(augmentMint ? 2 : augmentGear ? 3 : 0);
				break;
		}
	}

	private void setMode(int mode) {

		this.mode = (byte) mode;
		sendModePacket();
	}

	/* NETWORK METHODS */

	/* CLIENT -> SERVER */
	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addByte(mode);

		return payload;
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		mode = payload.getByte();
		modeFlag = mode;

		if (isActive) {
			processOff();
		}
		callNeighborTileChange();
	}

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addBool(augmentMint);
		payload.addBool(augmentGear);
		payload.addByte(mode);
		payload.addByte(modeFlag);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		augmentMint = payload.getBool();
		augmentGear = payload.getBool();
		mode = payload.getByte();
		modeFlag = payload.getByte();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger("TrackIn");
		outputTracker = nbt.getInteger("TrackOut");
		mode = nbt.getByte("Mode");
		modeFlag = mode;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut", outputTracker);
		nbt.setByte("Mode", mode);

		return nbt;
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		augmentMint = false;
		augmentGear = false;
	}

	@Override
	protected void postAugmentInstall() {

		super.postAugmentInstall();

		if (!augmentMint && VALUES[mode] == Mode.MINT) {
			mode = 0;
			modeFlag = 0;
			processOff();
		}
		if (!augmentGear && VALUES[mode] == Mode.GEAR) {
			mode = 0;
			modeFlag = 0;
			processOff();
		}
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentMint && TEProps.MACHINE_COMPACTOR_MINT.equals(id)) {
			augmentMint = true;
			hasModeAugment = true;
			return true;
		}
		if (!augmentGear && TEProps.MACHINE_COMPACTOR_GEAR.equals(id)) {
			augmentGear = true;
			hasModeAugment = true;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot != 0 || CompactorManager.isItemValid(stack);
	}

}
