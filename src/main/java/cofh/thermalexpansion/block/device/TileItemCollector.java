package cofh.thermalexpansion.block.device;

import cofh.core.init.CoreProps;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import cofh.core.util.filter.ItemFilter;
import cofh.core.util.helpers.MathHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.device.BlockDevice.Type;
import cofh.thermalexpansion.gui.client.device.GuiItemCollector;
import cofh.thermalexpansion.gui.container.device.ContainerItemCollector;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Arrays;
import java.util.List;

import static cofh.core.util.core.SideConfig.*;

public class TileItemCollector extends TileDeviceBase implements ITickable {

	private static final int TYPE = Type.ITEM_COLLECTOR.getMetadata();

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0, 1, 2, 3, 4, 5, 6, 7, 8 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, OUTPUT_ALL };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 0, 1, 0, 0, 0, 0 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { false, false, false, false, false, false, false, false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { true, true, true, true, true, true, true, true, true };

		GameRegistry.registerTileEntity(TileItemCollector.class, "thermalexpansion:device_item_collector");

		config();
	}

	public static void config() {

		String category = "Device.ItemCollector";
		BlockDevice.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}

	private static final int RADIUS = 5;
	private static final int TIME_CONSTANT = 16;

	private int outputTracker;

	private ItemFilter filter = new ItemFilter(9);

	private int offset;

	public TileItemCollector() {

		super();
		inventory = new ItemStack[9];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);

		offset = MathHelper.RANDOM.nextInt(TIME_CONSTANT);

		hasAutoOutput = true;

		enableAutoOutput = true;
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void update() {

		if (!timeCheckOffset()) {
			return;
		}
		transferOutput();

		boolean curActive = isActive;

		if (isActive) {
			collectItems();
			if (!redstoneControlOrDisable()) {
				isActive = false;
			}
		} else if (redstoneControlOrDisable()) {
			isActive = true;
		}
		updateIfChanged(curActive);

	}

	protected boolean timeCheckOffset() {

		return (world.getTotalWorldTime() + offset) % TIME_CONSTANT == 0;
	}

	protected void transferOutput() {

		if (!getTransferOut()) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				for (int j = inventory.length - 1; j >= 0; j--) {
					if (transferItem(j, 64, EnumFacing.VALUES[side])) {
						outputTracker = side;
						break;
					}
				}
			}
		}
	}

	protected void collectItems() {

		AxisAlignedBB area = new AxisAlignedBB(pos.add(-RADIUS, -RADIUS, -RADIUS), pos.add(1 + RADIUS, 1 + RADIUS, 1 + RADIUS));
		List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, area, EntitySelectors.IS_ALIVE);

		for (EntityItem item : items) {
			ItemStack groundStack = item.getItem();
			if (filter.matches(groundStack)) {
				for (int i = 0; i < inventory.length; i++) {
					if (inventory[i].isEmpty()) {
						setInventorySlotContents(i, groundStack.copy());
						groundStack.setCount(0);
					} else if (ItemHandlerHelper.canItemStacksStack(inventory[i], groundStack)) {
						int fill = inventory[i].getMaxStackSize() - inventory[i].getCount();
						if (fill > groundStack.getCount()) {
							inventory[i].grow(groundStack.getCount());
						} else {
							inventory[i].setCount(inventory[i].getMaxStackSize());
						}
						groundStack.splitStack(fill);
					}
					if (groundStack.isEmpty()) {
						item.setDead();
						break;
					}
				}
			}
		}
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiItemCollector(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerItemCollector(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		outputTracker = nbt.getInteger(CoreProps.TRACK_OUT);
		filter.deserializeNBT(nbt.getCompoundTag(CoreProps.FILTER));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger(CoreProps.TRACK_OUT, outputTracker);
		nbt.setTag(CoreProps.FILTER, filter.serializeNBT());

		return nbt;
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return filter.matches(stack);
	}

}
