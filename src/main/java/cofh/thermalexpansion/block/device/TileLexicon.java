package cofh.thermalexpansion.block.device;

import cofh.core.gui.container.ICustomInventory;
import cofh.core.init.CoreProps;
import cofh.core.util.helpers.ServerHelper;
import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.device.BlockDevice.Type;
import cofh.thermalexpansion.gui.client.device.GuiLexicon;
import cofh.thermalexpansion.gui.container.device.ContainerLexicon;
import cofh.thermalfoundation.util.LexiconManager;
import gnu.trove.map.hash.THashMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Arrays;
import java.util.Map;

public class TileLexicon extends TileDeviceBase implements ITickable, ICustomInventory {

	private static final int TYPE = Type.LEXICON.getMetadata();

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1 }, { 0, 1 }, { 0, 1 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { 0, 1, 4, 7, 8 };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 2, 1, 1, 1, 1, 1 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false, true };

		GameRegistry.registerTileEntity(TileLexicon.class, "thermalexpansion:device_lexicon");

		config();
	}

	public static void config() {

		String category = "Device.Lexicon";
		BlockDevice.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}

	private int inputTracker;
	private int outputTracker;

	private Map<String, ItemStack> preferredStacks = new THashMap<>();
	private ItemStack[] transmuteInv = new ItemStack[9];

	public TileLexicon() {

		super();
		inventory = new ItemStack[2];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);

		Arrays.fill(transmuteInv, ItemStack.EMPTY);

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

		if (ServerHelper.isClientWorld(world)) {
			return;
		}
		if (world.getTotalWorldTime() % CoreProps.TIME_CONSTANT_HALF != 0) {
			return;
		}
		boolean curActive = isActive;

		if (isActive) {
			transmute();
			transferOutput();
			transferInput();

			if (!redstoneControlOrDisable()) {
				isActive = false;
			}
		} else if (redstoneControlOrDisable()) {
			isActive = true;
		}
		updateIfChanged(curActive);
	}

	protected void transmute() {

		//		if (inventory[0].isEmpty()) {
		//			return;
		//		}
		//		if (hasPreferredStack(inventory[0])) {
		//
		//		}
	}

	protected void transferInput() {

		if (!enableAutoInput) {
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

		if (!enableAutoOutput) {
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

	protected void updatePreferredStacks() {

		preferredStacks.clear();

		for (ItemStack transmuteItem : transmuteInv) {
			preferredStacks.put(OreDictionaryArbiter.getOreName(transmuteItem), transmuteItem);
		}
	}

	public boolean hasPreferredStack(ItemStack stack) {

		return preferredStacks.containsKey(OreDictionaryArbiter.getOreName(stack));
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

		inputTracker = nbt.getInteger("TrackIn");
		outputTracker = nbt.getInteger("TrackOut");

		readTransmuteFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut", outputTracker);

		writeTransmuteToNBT(nbt);

		return nbt;
	}

	public void readTransmuteFromNBT(NBTTagCompound nbt) {

		NBTTagList list = nbt.getTagList("Transmute", 10);
		transmuteInv = new ItemStack[transmuteInv.length];
		Arrays.fill(transmuteInv, ItemStack.EMPTY);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int slot = tag.getInteger("Slot");

			if (slot >= 0 && slot < transmuteInv.length) {
				transmuteInv[slot] = new ItemStack(tag);
			}
		}
	}

	public void writeTransmuteToNBT(NBTTagCompound nbt) {

		if (transmuteInv.length <= 0) {
			return;
		}
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < transmuteInv.length; i++) {
			if (!transmuteInv[i].isEmpty()) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("Slot", i);
				transmuteInv[i].writeToNBT(tag);
				list.appendTag(tag);
			}
		}
		if (list.tagCount() > 0) {
			nbt.setTag("Transmute", list);
		}
	}

	/* ICustomInventory */
	@Override
	public ItemStack[] getInventorySlots(int inventoryIndex) {

		return transmuteInv;
	}

	@Override
	public int getSlotStackLimit(int slotIndex) {

		return 1;
	}

	@Override
	public void onSlotUpdate(int slotIndex) {

		updatePreferredStacks();
		markChunkDirty();
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot != 0 || LexiconManager.validOre(stack) && hasPreferredStack(stack);
	}

}
