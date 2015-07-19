package cofh.thermalexpansion.block.device;

import cofh.api.inventory.IInventoryConnection;
import cofh.core.CoFHProps;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.InventoryHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileAugmentable;
import cofh.thermalexpansion.gui.client.device.GuiBreaker;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cpw.mods.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileCollector extends TileAugmentable implements IInventoryConnection {

	static final int TYPE = BlockDevice.Types.COLLECTOR.ordinal();
	static SideConfig defaultSideConfig = new SideConfig();

	public static void initialize() {

		defaultSideConfig = new SideConfig();
		defaultSideConfig.numConfig = 2;
		defaultSideConfig.slotGroups = new int[][] { {}, {} };
		defaultSideConfig.allowInsertionSide = new boolean[] { false, false };
		defaultSideConfig.allowExtractionSide = new boolean[] { false, false };
		defaultSideConfig.allowInsertionSlot = new boolean[] {};
		defaultSideConfig.allowExtractionSlot = new boolean[] {};
		defaultSideConfig.sideTex = new int[] { 0, 4 };
		defaultSideConfig.defaultSides = new byte[] { 0, 0, 0, 0, 0, 0 };

		GameRegistry.registerTileEntity(TileBreaker.class, "thermalexpansion.Collector");
		configure();
	}

	public static void configure() {

		String comment = "Enable this to allow for Collectors to be securable.";
		enableSecurity = ThermalExpansion.config.get("Security", "Device.Collector.Securable", enableSecurity, comment);
	}

	public static boolean enableSecurity = true;

	int area = 2;

	public LinkedList<ItemStack> stuffedItems = new LinkedList<ItemStack>();

	public TileCollector() {

		sideConfig = defaultSideConfig;
	}

	@Override
	public void setDefaultSides() {

		sideCache = getDefaultSides();
		sideCache[facing ^ 1] = 1;
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.device." + BlockDevice.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity;
	}

	@Override
	public boolean sendRedstoneUpdates() {

		return true;
	}

	@Override
	public void updateEntity() {

		super.updateEntity();
		if (ServerHelper.isServerWorld(worldObj) && worldObj.getTotalWorldTime() % CoFHProps.TIME_CONSTANT_HALF == 0 && redstoneControlOrDisable()) {
			if (!isEmpty()) {
				outputBuffer();
			}
			if (isEmpty()) {
				collectItems();
			}
		}
	}

	public boolean isEmpty() {

		return stuffedItems.size() == 0;
	}

	public void collectItems() {

		int coords[] = BlockHelper.getAdjacentCoordinatesForSide(xCoord, yCoord, zCoord, facing);

		stuffedItems.addAll(collectItemsInArea(worldObj, coords[0], coords[1], coords[2], area));
	}

	public void outputBuffer() {

		for (int i = 0; i < 6; i++) {
			if (i != facing && sideCache[i] == 1) {
				int coords[] = BlockHelper.getAdjacentCoordinatesForSide(xCoord, yCoord, zCoord, i);
				TileEntity theTile = worldObj.getTileEntity(coords[0], coords[1], coords[2]);

				if (InventoryHelper.isInsertion(theTile)) {
					LinkedList<ItemStack> newStuffed = new LinkedList<ItemStack>();
					for (ItemStack curItem : stuffedItems) {
						if (curItem == null || curItem.getItem() == null) {
							curItem = null;
						} else {
							curItem = InventoryHelper.addToInsertion(theTile, i, curItem);
						}
						if (curItem != null) {
							newStuffed.add(curItem);
						}
					}
					stuffedItems = newStuffed;
				}
			}
		}
	}

	public static List<ItemStack> collectItemsInArea(World worldObj, int x, int y, int z, int area) {

		int area2 = 1 + area;

		List<ItemStack> stacks = new ArrayList<ItemStack>();
		List<EntityItem> result = worldObj.getEntitiesWithinAABB(EntityItem.class,
				AxisAlignedBB.getBoundingBox(x - area, y - area, z - area, x + area2, y + area2, z + area2));
		for (int i = 0; i < result.size(); i++) {
			EntityItem entity = result.get(i);
			if (entity.isDead || entity.getEntityItem().stackSize <= 0) {
				continue;
			}
			stacks.add(entity.getEntityItem());
			entity.worldObj.removeEntity(entity);
		}
		return stacks;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiBreaker(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		NBTTagList list = nbt.getTagList("StuffedInv", 10);
		stuffedItems.clear();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			stuffedItems.add(ItemStack.loadItemStackFromNBT(compound));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		NBTTagList list = new NBTTagList();
		list = new NBTTagList();
		for (int i = 0; i < stuffedItems.size(); i++) {
			if (stuffedItems.get(i) != null) {
				NBTTagCompound compound = new NBTTagCompound();
				stuffedItems.get(i).writeToNBT(compound);
				list.appendTag(compound);
			}
		}
		nbt.setTag("StuffedInv", list);
	}

	/* IInventoryConnection */
	@Override
	public ConnectionType canConnectInventory(ForgeDirection from) {

		// TODO Auto-generated method stub
		return null;
	}

	/* IReconfigurableFacing */

	/* ISidedTexture */
	@Override
	public IIcon getTexture(int side, int pass) {

		// TODO Auto-generated method stub
		return null;
	}

}
