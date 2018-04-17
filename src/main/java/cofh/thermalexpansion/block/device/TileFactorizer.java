package cofh.thermalexpansion.block.device;

import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.device.BlockDevice.Type;
import cofh.thermalexpansion.gui.client.device.GuiFactorizer;
import cofh.thermalexpansion.gui.container.device.ContainerFactorizer;
import cofh.thermalexpansion.util.managers.device.FactorizerManager;
import cofh.thermalexpansion.util.managers.device.FactorizerManager.FactorizerRecipe;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Arrays;

public class TileFactorizer extends TileDeviceBase implements ITickable {

	private static final int TYPE = Type.FACTORIZER.getMetadata();

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1 }, { 0, 1 }, { 0, 1 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_ALL, OPEN, OMNI };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 2, 1, 1, 1, 1, 1 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false, true };

		LIGHT_VALUES[TYPE] = 4;

		GameRegistry.registerTileEntity(TileFactorizer.class, "thermalexpansion:device_factorizer");

		config();
	}

	public static void config() {

		String category = "Device.Factorizer";
		BlockDevice.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}

	private int inputTracker;
	private int outputTracker;

	public boolean recipeMode;

	public TileFactorizer() {

		super();
		inventory = new ItemStack[2];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);

		hasAutoInput = true;
		hasAutoOutput = true;

		enableAutoInput = true;
		enableAutoOutput = true;
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void update() {

		if (world.getTotalWorldTime() % CoreProps.TIME_CONSTANT_HALF != 0) {
			return;
		}
		transferInput();

		boolean curActive = isActive;

		if (isActive) {
			transmute();

			if (!redstoneControlOrDisable()) {
				isActive = false;
			}
		} else if (redstoneControlOrDisable()) {
			isActive = true;
		}
		transferOutput();

		updateIfChanged(curActive);
	}

	protected void transmute() {

		if (inventory[0].isEmpty()) {
			return;
		}
		FactorizerRecipe recipe = FactorizerManager.getRecipe(inventory[0], recipeMode);

		if (recipe == null) {
			return;
		}
		ItemStack input = recipe.getInput();

		if (inventory[0].getCount() < input.getCount()) {
			return;
		}
		ItemStack output = recipe.getOutput();

		if (inventory[1].getCount() + output.getCount() > inventory[1].getMaxStackSize()) {
			return;
		}
		if (inventory[1].isEmpty()) {
			inventory[0].shrink(input.getCount());
			inventory[1] = ItemHelper.cloneStack(output, output.getCount());
		} else if (inventory[1].isItemEqual(output)) {
			inventory[0].shrink(input.getCount());
			inventory[1].grow(output.getCount());
		}
	}

	protected void transferInput() {

		if (!getTransferIn()) {
			return;
		}
		int side;
		for (int i = inputTracker + 1; i <= inputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryInput(sideConfig.sideTypes[sideCache[side]])) {
				if (extractItem(0, 64, EnumFacing.VALUES[side])) {
					inputTracker = side;
					break;
				}
			}
		}
	}

	protected void transferOutput() {

		if (!getTransferOut()) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				if (transferItem(1, 64, EnumFacing.VALUES[side])) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiFactorizer(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerFactorizer(inventory, this);
	}

	public void setMode(boolean mode) {

		boolean lastMode = recipeMode;
		recipeMode = mode;
		sendModePacket();
		recipeMode = lastMode;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger("TrackIn");
		outputTracker = nbt.getInteger("TrackOut");
		recipeMode = nbt.getBoolean(CoreProps.MODE);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut", outputTracker);
		nbt.setBoolean(CoreProps.MODE, recipeMode);
		return nbt;
	}

	/* NETWORK METHODS */

	/* CLIENT -> SERVER */
	@Override
	public PacketBase getModePacket() {

		PacketBase payload = super.getModePacket();

		payload.addBool(recipeMode);

		return payload;
	}

	@Override
	protected void handleModePacket(PacketBase payload) {

		super.handleModePacket(payload);

		recipeMode = payload.getBool();

		callNeighborTileChange();
	}

	/* SERVER -> CLIENT */
	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addBool(recipeMode);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		recipeMode = payload.getBool();
	}

	/* IInventory */
	@Override
	public int getSizeInventory() {

		return 2;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot != 0 || FactorizerManager.recipeExists(stack, recipeMode);
	}

}
