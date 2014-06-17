package thermalexpansion.block.device;

import cofh.core.CoFHProps;
import cofh.entity.PlayerFake;
import cofh.network.CoFHPacket;
import cofh.network.ITileInfoPacketHandler;
import cofh.render.IconRegistry;
import cofh.util.BlockHelper;
import cofh.util.MathHelper;
import cofh.util.ServerHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileReconfigurableInventory;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.client.device.GuiActivator;
import thermalexpansion.gui.container.device.ContainerActivator;

public class TileActivator extends TileReconfigurableInventory implements ISidedInventory, ITileInfoPacketHandler {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileActivator.class, "thermalexpansion.Activator");
	}

	public static final int[] SIDE_TEX = new int[] { 0, 1, 4 };
	public static final int[] SLOTS = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };

	public boolean leftClick = false;
	public byte tickSlot = 0;
	public boolean actsSneaking = false;
	public byte angle = 1;

	PlayerFake myFakePlayer;
	boolean needsWorld = true;
	int slotTracker = 0;

	public TileActivator() {

		inventory = new ItemStack[9];

	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.device." + BlockDevice.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return BlockDevice.Types.ACTIVATOR.ordinal();
	}

	@Override
	public boolean openGui(EntityPlayer player) {

		player.openGui(ThermalExpansion.instance, 0, worldObj, xCoord, yCoord, zCoord);
		return true;
	}

	@Override
	public boolean sendRedstoneUpdates() {

		return true;
	}

	@Override
	public void onRedstoneUpdate() {

		if (!redstoneControlOrDisable() && !needsWorld && myFakePlayer.itemInUse != null) {
			myFakePlayer.stopUsingItem();
		} else {
			int coords[] = BlockHelper.getAdjacentCoordinatesForSide(xCoord, yCoord, zCoord, facing);
			Block block = worldObj.getBlock(coords[0], coords[1], coords[2]);

			if (block != null && block.isAir(worldObj, coords[0], coords[1], coords[2])) {
				doDeploy();
			}
		}
	}

	@Override
	public void updateEntity() {

		if (ServerHelper.isServerWorld(worldObj)) {
			if (worldObj.getTotalWorldTime() % CoFHProps.TIME_CONSTANT_HALF == 0 && redstoneControlOrDisable()) {
				doDeploy();
			} else if (!needsWorld) {
				if (leftClick && myFakePlayer.theItemInWorldManager.durabilityRemainingOnBlock > -1) {
					int tickSlot = getNextStackIndex();
					myFakePlayer.theItemInWorldManager.updateBlockRemoving();
					if (myFakePlayer.theItemInWorldManager.durabilityRemainingOnBlock >= 9) {
						simLeftClick(myFakePlayer, getStackInSlot(tickSlot), facing);
					}
				} else if (!leftClick && myFakePlayer.itemInUse != null) {
					myFakePlayer.tickItemInUse(getStackInSlot(getNextStackIndex()));
				}
			}
		}
	}

	public void doDeploy() {

		int tickSlot = getNextStackIndex();
		ItemStack theStack = getStackInSlot(tickSlot);
		updateFakePlayer(tickSlot);

		if (leftClick) {
			simLeftClick(myFakePlayer, theStack, facing);
		} else {
			int coords[] = BlockHelper.getAdjacentCoordinatesForSide(xCoord, yCoord, zCoord, facing);
			simRightClick(myFakePlayer, theStack, coords[0], coords[1], coords[2], 1);
		}
		if (theStack != null && theStack.stackSize <= 0) {
			setInventorySlotContents(tickSlot, null);
		}
		checkItemsUpdated();
	}

	public void checkItemsUpdated() {

		for (int i = 0; i < getSizeInventory(); i++) {
			setInventorySlotContents(i, myFakePlayer.inventory.mainInventory[i]);
			if (inventory[i] != null && inventory[i].stackSize <= 0) {
				inventory[i] = null;
			}
		}
	}

	public int getNextStackIndex() {

		if (!needsWorld) {
			if ((leftClick && myFakePlayer.theItemInWorldManager.durabilityRemainingOnBlock > -1) || myFakePlayer.itemInUse != null) {
				return slotTracker;
			}
			if (tickSlot == 0) {
				return incrementTracker();
			} else if (tickSlot == 1) {
				return getRandomStackIndex();
			}
			return 0;
		}
		return 0;
	}

	public int getRandomStackIndex() {

		int i = -1;

		for (int k = 0; k < getSizeInventory(); k++) {
			if (getStackInSlot(k) != null && MathHelper.RANDOM.nextInt(2) == 0) {
				i = k;
			}
		}
		return i == -1 ? incrementTracker() : i;
	}

	public int incrementTracker() {

		slotTracker++;
		slotTracker %= getSizeInventory();

		for (int k = slotTracker; k < getSizeInventory(); k++) {
			if (this.inventory[k] != null) {
				slotTracker = k;
				return slotTracker;
			}
		}
		for (int k = 0; k < slotTracker; k++) {
			if (this.inventory[k] != null) {
				slotTracker = k;
				return slotTracker;
			}
		}
		slotTracker = 0;
		return slotTracker;
	}

	public void updateFakePlayer(int tickSlot) {

		if (needsWorld) {
			myFakePlayer = new PlayerFake((WorldServer) worldObj);
			needsWorld = false;
		}
		myFakePlayer.inventory.mainInventory = new ItemStack[36];
		for (int i = 0; i < getSizeInventory(); i++) {
			myFakePlayer.inventory.mainInventory[i] = getStackInSlot(i);
		}
		double x = xCoord + 0.5D;
		double y = yCoord - 1.1D;
		double z = zCoord + 0.5D;
		float pitch = this.angle == 0 ? 45.0F : this.angle == 1 ? 0F : -45F;
		float yaw;

		switch (facing) {
		case 0:
			pitch = this.angle == 0 ? -90.0F : this.angle == 1 ? 0F : 90F;
			yaw = 0.0F;
			y -= 0.51D;
			break;
		case 1:
			pitch = this.angle == 0 ? 90.0F : this.angle == 1 ? 0F : -90F;
			yaw = 0.0F;
			y += 1.51D;
			break;
		case 2:
			yaw = 180.0F;
			z -= 0.51D;
			y += .5D;
			break;
		case 3:
			yaw = 0.0F;
			z += 0.51D;
			y += .5D;
			break;
		case 4:
			yaw = 90.0F;
			x -= 0.51D;
			y += .5D;
			break;
		default:
			yaw = -90.0F;
			x += 0.51D;
			y += .5D;
		}
		myFakePlayer.setPositionAndRotation(x, y, z, yaw, pitch);
		myFakePlayer.isSneaking = actsSneaking;
		myFakePlayer.yOffset = -1.1F;
		myFakePlayer.setItemInHand(tickSlot);

		myFakePlayer.onUpdate();
	}

	public boolean simLeftClick(EntityPlayer thePlayer, ItemStack deployingStack, int side) {

		int coords[] = BlockHelper.getAdjacentCoordinatesForSide(xCoord, yCoord, zCoord, facing);

		Block theBlock = worldObj.getBlock(coords[0], coords[1], coords[2]);
		if (theBlock != Blocks.air) {
			if (myFakePlayer.theItemInWorldManager.durabilityRemainingOnBlock == -1) {
				myFakePlayer.theItemInWorldManager.onBlockClicked(coords[0], coords[1], coords[2], facing ^ 1);
			} else if (myFakePlayer.theItemInWorldManager.durabilityRemainingOnBlock >= 9) {
				myFakePlayer.theItemInWorldManager.uncheckedTryHarvestBlock(coords[0], coords[1], coords[2]);
				myFakePlayer.theItemInWorldManager.durabilityRemainingOnBlock = -1;

				if (deployingStack != null) {
					deployingStack.getItem().onBlockDestroyed(deployingStack, worldObj, theBlock, coords[0], coords[1], coords[2], myFakePlayer);
				}
			}
		} else {
			myFakePlayer.theItemInWorldManager.durabilityRemainingOnBlock = -1;
			List entities = worldObj.getEntitiesWithinAABB(Entity.class, BlockHelper.getAdjacentAABBForSide(xCoord, yCoord, zCoord, facing));

			if (entities.size() == 0) {

				return false;
			}
			thePlayer.attackTargetEntityWithCurrentItem((Entity) entities.get(entities.size() > 1 ? MathHelper.RANDOM.nextInt(entities.size() - 1) : 0));
		}
		return true;
	}

	public void simRightClick(EntityPlayer thePlayer, ItemStack deployingStack, int blockX, int blockY, int blockZ, int side) {

		if (thePlayer.itemInUse == null) {
			if (!simRightClick2(thePlayer, deployingStack, blockX, blockY, blockZ, side) && deployingStack != null) {
				List entities = worldObj.getEntitiesWithinAABB(Entity.class, BlockHelper.getAdjacentAABBForSide(xCoord, yCoord, zCoord, facing));

				if (entities.size() > 0
						&& thePlayer.interactWith((Entity) entities.get(entities.size() > 1 ? MathHelper.RANDOM.nextInt(entities.size() - 1) : 0))) {
					return;
				}
				ItemStack result = deployingStack.useItemRightClick(worldObj, thePlayer);
				thePlayer.inventory.setInventorySlotContents(myFakePlayer.inventory.currentItem, result.stackSize <= 0 ? null : result);
			}
		}
	}

	public boolean simRightClick2(EntityPlayer thePlayer, ItemStack deployingStack, int blockX, int blockY, int blockZ, int side) {

		float f = 0.5F;
		float f1 = 0.5F;
		float f2 = 0.5F;
		int offsetY = facing == 1 ? 1 : -1;

		if (facing > 1) {
			if (angle == 0) {
				blockY -= 1;
			}
			if (angle == 2) {
				blockY += 1;
			}
		}
		Block block = worldObj.getBlock(blockX, blockY, blockZ);

		boolean isAir = block.isAir(worldObj, blockX, blockY, blockZ);

		if (deployingStack != null && deployingStack.getItem() != null
				&& deployingStack.getItem().onItemUseFirst(deployingStack, thePlayer, worldObj, blockX, blockY, blockZ, side, f, f1, f2)) {
			return true;
		}
		if (!thePlayer.isSneaking() || thePlayer.getHeldItem() == null) {
			if (block.onBlockActivated(worldObj, blockX, blockY, blockZ, thePlayer, side, f, f1, f2)) {
				return true;
			}
		}
		if (deployingStack == null) {
			return false;
		} else {
			if (deployingStack.getItem() instanceof ItemBlock) {
				if (!deployingStack.tryPlaceItemIntoWorld(thePlayer, worldObj, blockX, blockY + offsetY, blockZ, facing != 1 ? 1 : 0, f, f1, f2)) {
					if (isAir) {
						if (!deployingStack.tryPlaceItemIntoWorld(thePlayer, worldObj, blockX, blockY, blockZ, facing != 1 ? 1 : 0, f, f1, f2)) {
							return false;
						}
					} else {
						if (!deployingStack.tryPlaceItemIntoWorld(thePlayer, worldObj, blockX, blockY, blockZ, 0, f, f1, f2)) {
							return false;
						}
					}
				}
			} else {
				if (isAir) {
					if (!deployingStack.tryPlaceItemIntoWorld(thePlayer, worldObj, blockX, blockY, blockZ, facing != 1 ? 1 : 0, f, f1, f2)) {
						if (!deployingStack.tryPlaceItemIntoWorld(thePlayer, worldObj, blockX, blockY + offsetY, blockZ, facing != 1 ? 1 : 0, f, f1, f2)) {
							return false;
						}
					}
				} else {
					if (!deployingStack.tryPlaceItemIntoWorld(thePlayer, worldObj, blockX, blockY, blockZ, 0, f, f1, f2)) {
						if (!deployingStack.tryPlaceItemIntoWorld(thePlayer, worldObj, blockX, blockY + offsetY, blockZ, facing != 1 ? 1 : 0, f, f1, f2)) {
							return false;
						}
					}
				}
			}
			if (deployingStack.stackSize <= 0) {
				MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(thePlayer, deployingStack));
				thePlayer.inventory.setInventorySlotContents(myFakePlayer.inventory.currentItem, null);
			}
			return true;
		}
	}

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();
		payload.addBool(actsSneaking);
		payload.addBool(leftClick);
		payload.addByte(tickSlot);
		payload.addByte(angle);
		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		actsSneaking = payload.getBool();
		leftClick = payload.getBool();
		tickSlot = payload.getByte();
		angle = payload.getByte();
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(CoFHPacket payload, boolean isServer, EntityPlayer thePlayer) {

		switch (TEProps.PacketID.values()[payload.getByte()]) {
		case MODE:
			leftClick = payload.getBool();
			actsSneaking = payload.getBool();
			tickSlot = payload.getByte();
			angle = payload.getByte();
			return;
		default:
		}
	}

	/* GUI METHODS */
	@Override
	public GuiContainer getGuiClient(InventoryPlayer inventory) {

		return new GuiActivator(inventory, this);
	}

	@Override
	public Container getGuiServer(InventoryPlayer inventory) {

		return new ContainerActivator(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		actsSneaking = nbt.getBoolean("Sneaking");
		leftClick = nbt.getBoolean("LeftClick");
		tickSlot = nbt.getByte("TickSlotB");
		if (nbt.hasKey("TickSlot")) { // Conversion code, remove in 1.0
			tickSlot = nbt.getBoolean("TickSlot") ? (byte) 0 : (byte) 1;
		}
		angle = nbt.getByte("Angle");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setBoolean("Sneaking", actsSneaking);
		nbt.setBoolean("LeftClick", leftClick);
		nbt.setByte("TickSlotB", tickSlot);
		nbt.setByte("Angle", angle);
	}

	/* IReconfigurableFacing */
	@Override
	public boolean allowYAxisFacing() {

		return true;
	}

	@Override
	public boolean setFacing(int side) {

		if (side < 0 || side > 5) {
			return false;
		}
		facing = (byte) side;
		sideCache[facing ^ 1] = 2;
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	/* IReconfigurableSides */
	@Override
	public int getNumConfig(int side) {

		return 3;
	}

	/* ISidedBlockTexture */
	@Override
	public IIcon getTexture(int side, int pass) {

		if (pass == 0) {
			return side != facing ? IconRegistry.getIcon("DeviceSide") : redstoneControlOrDisable() ? IconRegistry.getIcon("DeviceActive", getType())
					: IconRegistry.getIcon("DeviceFace", getType());
		} else if (side < 6) {
			return IconRegistry.getIcon(TEProps.textureSelection, SIDE_TEX[sideCache[side]]);
		}
		return IconRegistry.getIcon("DeviceSide");
	}

	/* ISidedInventory */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return sideCache[side] != 0 ? SLOTS : TEProps.EMPTY_INVENTORY;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {

		return sideCache[side] == 1;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {

		return sideCache[side] == 2;
	}
}
