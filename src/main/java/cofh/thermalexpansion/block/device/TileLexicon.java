package cofh.thermalexpansion.block.device;

import cofh.core.init.CoreProps;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.device.BlockDevice.Type;
import cofh.thermalexpansion.gui.client.device.GuiLexicon;
import cofh.thermalexpansion.gui.container.device.ContainerLexicon;
import cofh.thermalfoundation.util.LexiconManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Arrays;
import java.util.Map;

import static cofh.core.util.core.SideConfig.*;

public class TileLexicon extends TileDeviceBase implements ITickable {

	private static final int TYPE = Type.LEXICON.getMetadata();

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1 }, { 0, 1 }, { 0, 1 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_ALL, OPEN, OMNI };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 2, 1, 1, 1, 1, 1 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false, true };

		LIGHT_VALUES[TYPE] = 12;

		GameRegistry.registerTileEntity(TileLexicon.class, "thermalexpansion:device_lexicon");

		config();
	}

	public static void config() {

		String category = "Device.Lexicon";
		BlockDevice.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}

	private int inputTracker;
	private int outputTracker;

	private Map<String, ItemStack> preferredStacks = new Object2ObjectOpenHashMap<>();

	public TileLexicon() {

		super();
		inventory = new ItemStack[11];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length - 9);

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
		if (hasPreferredStack(inventory[0])) {
			ItemStack transmuteStack = ItemHelper.cloneStack(getPreferredStack(inventory[0]), inventory[0].getCount());

			if (inventory[1].isEmpty()) {
				inventory[0] = ItemStack.EMPTY;
				inventory[1] = transmuteStack;
			} else if (inventory[1].isItemEqual(transmuteStack)) {
				int total = inventory[1].getCount() + transmuteStack.getCount();
				if (total <= inventory[1].getMaxStackSize()) {
					inventory[0] = ItemStack.EMPTY;
					inventory[1].grow(transmuteStack.getCount());
				} else {
					inventory[0].shrink(inventory[1].getMaxStackSize() - inventory[1].getCount());
					inventory[1].setCount(inventory[1].getMaxStackSize());
				}
			}
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

	public void updatePreferredStacks() {

		preferredStacks.clear();

		for (int i = 2; i < inventory.length; i++) {
			if (!inventory[i].isEmpty()) {
				preferredStacks.put(OreDictionaryArbiter.getOreName(inventory[i]), inventory[i]);
			}
		}
	}

	public boolean hasPreferredStack(ItemStack stack) {

		return preferredStacks.containsKey(OreDictionaryArbiter.getOreName(stack));
	}

	public ItemStack getPreferredStack(ItemStack stack) {

		return preferredStacks.get(OreDictionaryArbiter.getOreName(stack));
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiLexicon(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerLexicon(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger(CoreProps.TRACK_IN);
		outputTracker = nbt.getInteger(CoreProps.TRACK_OUT);

		updatePreferredStacks();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger(CoreProps.TRACK_IN, inputTracker);
		nbt.setInteger(CoreProps.TRACK_OUT, outputTracker);

		return nbt;
	}

	/* IInventory */
	@Override
	public int getSizeInventory() {

		return 2;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot != 0 || LexiconManager.validOre(stack) && hasPreferredStack(stack) && !ItemHelper.itemsIdentical(stack, getPreferredStack(stack));
	}

}
