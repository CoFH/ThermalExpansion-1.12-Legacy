package cofh.thermalexpansion.block.device;

import cofh.api.core.ICustomInventory;
import cofh.core.render.IconRegistry;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileAugmentable;
import cofh.thermalexpansion.core.TEProps;
import cpw.mods.fml.common.registry.GameRegistry;

import gnu.trove.map.hash.THashMap;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;

public class TileLexicon extends TileAugmentable implements ICustomInventory {

	static final int TYPE = BlockDevice.Types.LEXICON.ordinal();
	static SideConfig defaultSideConfig = new SideConfig();
	static EnergyConfig energyConfig = new EnergyConfig();

	public static void initialize() {

		defaultSideConfig = new SideConfig();
		defaultSideConfig.numConfig = 4;
		defaultSideConfig.slotGroups = new int[][] { {}, { 0, 1, 2, 3, 4, 5 }, { 6, 7, 8, 9, 10, 11 } };
		defaultSideConfig.allowInsertionSide = new boolean[] { false, true, false, true };
		defaultSideConfig.allowExtractionSide = new boolean[] { false, false, true, true };
		defaultSideConfig.allowInsertionSlot = new boolean[] { true, true, true, true, true, true, false, false, false, false, false, false, false };
		defaultSideConfig.allowExtractionSlot = new boolean[] { true, true, true, true, true, true, true, true, true, true, true, true, false };
		defaultSideConfig.sideTex = new int[] { 0, 1, 4, 7 };
		defaultSideConfig.defaultSides = new byte[] { 1, 1, 1, 1, 1, 1 };

		GameRegistry.registerTileEntity(TileLexicon.class, "thermalexpansion.lexicon");
		configure();
	}

	public static void configure() {

		String comment = "Enable this to allow for Lexicons to be securable.";
		enableSecurity = ThermalExpansion.config.get("Security", "Device.Lexicon.Securable", enableSecurity, comment);
	}

	public static boolean enableSecurity = true;

	Map<String, ItemStack> conversions = new THashMap<String, ItemStack>(9);
	ItemStack[] conversionItems = new ItemStack[9];

	public TileLexicon() {

		sideConfig = defaultSideConfig;

		inventory = new ItemStack[6 + 6 + 1];
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.device." + BlockDevice.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return TYPE;
	}

	/* GUI METHODS */
	// @Override
	// public Object getGuiClient(InventoryPlayer inventory) {
	//
	// return new GuiLexicon(inventory, this);
	// }
	//
	// @Override
	// public Object getGuiServer(InventoryPlayer inventory) {
	//
	// return new ContainerLexicon(inventory, this);
	// }

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		readConversionsFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		writeConversionsToNBT(nbt);
	}

	public void readConversionsFromNBT(NBTTagCompound nbt) {

		NBTTagList list = nbt.getTagList("Conversions", 10);

		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int slot = tag.getInteger("Slot");
			if (slot >= 0 && slot < conversionItems.length) {
				conversionItems[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}

	public void writeConversionsToNBT(NBTTagCompound nbt) {

		if (conversionItems.length <= 0) {
			return;
		}
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < augments.length; i++) {
			if (conversionItems[i] != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("Slot", i);
				conversionItems[i].writeToNBT(tag);
				list.appendTag(tag);
			}
		}
		nbt.setTag("Conversions", list);
	}

	/* ICustomInventory */
	@Override
	public ItemStack[] getInventorySlots(int inventoryIndex) {

		return conversionItems;
	}

	@Override
	public int getSlotStackLimit(int slotIndex) {

		return 1;
	}

	@Override
	public void onSlotUpdate() {

		markDirty();
	}

	/* ISidedTexture */
	@Override
	public IIcon getTexture(int side, int pass) {

		if (pass == 0) {
			return side != facing ? IconRegistry.getIcon("DeviceSide") : isActive && redstoneControlOrDisable() ? IconRegistry.getIcon("DeviceActive",
					getType()) : IconRegistry.getIcon("DeviceFace", getType());
		} else if (side < 6) {
			return IconRegistry.getIcon(TEProps.textureSelection, sideConfig.sideTex[sideCache[side]]);
		}
		return IconRegistry.getIcon("DeviceSide");
	}

}
