package cofh.thermalexpansion.block.automaton;

import cofh.api.energy.EnergyStorage;
import cofh.core.CoFHProps;
import cofh.core.entity.CoFHFakePlayer;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.InventoryHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.device.GuiActivator;
import cofh.thermalexpansion.gui.container.device.ContainerActivator;
import com.google.common.base.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

public class TileActivator extends TileAutomatonBase {

	private static final int TYPE = BlockAutomaton.Type.ACTIVATOR.getMetadata();

	static EnergyConfig energyConfig;
	static int ACTIVATION_ENERGY = 20;
	static int MAX_SLOT = 9;

	public static void initialize() {

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numConfig = 4;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, { 0, 1, 2, 3, 4, 5, 6, 7, 8 }, { 0, 1, 2, 3, 4, 5, 6, 7, 8 }, { 0, 1, 2, 3, 4, 5, 6, 7, 8 } };
		defaultSideConfig[TYPE].allowInsertionSide = new boolean[] { false, true, false, true };
		defaultSideConfig[TYPE].allowExtractionSide = new boolean[] { false, false, true, true };
		defaultSideConfig[TYPE].allowInsertionSlot = new boolean[] { true, true, true, true, true, true, true, true, true, false };
		defaultSideConfig[TYPE].allowExtractionSlot = new boolean[] { true, true, true, true, true, true, true, true, true, false };
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 1, 4, 7 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 1, 1, 1, 1, 1, 1 };

		GameRegistry.registerTileEntity(TileActivator.class, "thermalexpansion:activator");

		config();
	}

	public static void config() {

		String category = "Device.Activator";
		int maxPower = MathHelper.clamp(ThermalExpansion.CONFIG.get(category, "BasePower", 20), 0, 500);
		ThermalExpansion.CONFIG.set("Device.Activator", "BasePower", maxPower);
		energyConfig = new EnergyConfig();
		energyConfig.setParamsPower(maxPower);

		String comment = "This value sets how much energy the Activator uses when it actually does something. Set to 0 to disable it requiring energy.";
		maxPower = MathHelper.clamp(ThermalExpansion.CONFIG.get(category, "ActivationEnergy", ACTIVATION_ENERGY, comment), 0, 500);
		ThermalExpansion.CONFIG.set("Device.Activator", "ActivationEnergy", maxPower);
		ACTIVATION_ENERGY = maxPower;
	}

	public boolean leftClick = false;
	public byte tickSlot = 0;
	public boolean actsSneaking = false;
	public byte angle = 1;

	CoFHFakePlayer myFakePlayer;
	int slotTracker = 0;
	int[] tracker;

	static final Predicate<Entity> selectAttackable = new Predicate<Entity>() {

		@Override
		public boolean apply(Entity e) {

			return e.canBeAttackedWithItem();
		}
	};

	public TileActivator() {

		super();
		inventory = new ItemStack[10];
		energyStorage = new EnergyStorage(energyConfig.maxEnergy, energyConfig.maxPower * 3);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void cofh_validate() {

		if (ServerHelper.isServerWorld(worldObj)) {
			myFakePlayer = new CoFHFakePlayer((WorldServer) worldObj);
		}
		super.cofh_validate();
	}

	@Override
	public void setDefaultSides() {

		sideCache = getDefaultSides();
		sideCache[facing] = 0;
		sideCache[facing ^ 1] = 2;
	}

	@Override
	public void onRedstoneUpdate() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		} else if (!inWorld) {
			cofh_validate();
		}
		if (!redstoneControlOrDisable() && myFakePlayer.activeItemStack != null) {
			myFakePlayer.stopActiveHand();
		} else if (myFakePlayer.ping != worldObj.getTotalWorldTime()) {
			myFakePlayer.ping = (int) (worldObj.getTotalWorldTime() & 0x7FFFFFFFL);
			BlockPos offsetPos = getPos().offset(EnumFacing.VALUES[facing]);
			IBlockState state = worldObj.getBlockState(offsetPos);

			if (state != null && state.getBlock().isAir(state, worldObj, offsetPos)) {
				doDeploy();
			}
		}
	}

	@Override
	public void update() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		} else if (!inWorld) {
			cofh_validate();
		}
		if (hasEnergy(ACTIVATION_ENERGY)) {
			myFakePlayer.ping = (int) (worldObj.getTotalWorldTime() & 0x7FFFFFFFL);
			if (!isActive) {
				callBlockUpdate();
			}
			isActive = true;
			boolean work = false;

			if (worldObj.getTotalWorldTime() % CoFHProps.TIME_CONSTANT_HALF == 0 && redstoneControlOrDisable()) {
				work = doDeploy();
			} else {

				if (leftClick && myFakePlayer.interactionManager.durabilityRemainingOnBlock > -1) {
					work = true;
					int tickSlot = getNextStackIndex();
					myFakePlayer.interactionManager.updateBlockRemoving();
					if (myFakePlayer.interactionManager.durabilityRemainingOnBlock >= 9) {
						work = simLeftClick(myFakePlayer, getStackInSlot(tickSlot), facing);
					}
				} else if (!leftClick && myFakePlayer.activeItemStack != null) {
					work = true;
					int slot = getNextStackIndex();
					myFakePlayer.inventory.currentItem = slot;
					myFakePlayer.tickItemInUse(getStackInSlot(slot));
					checkItemsUpdated();
				}
			}
			if (work) {
				energyStorage.modifyEnergyStored(-ACTIVATION_ENERGY);
			}
		} else {
			if (isActive) {
				callBlockUpdate();
			}
			isActive = false;
		}
		chargeEnergy();
	}

	public boolean doDeploy() {

		int tickSlot = getNextStackIndex();
		ItemStack theStack = getStackInSlot(tickSlot);
		updateFakePlayer(tickSlot);

		boolean r = false;
		if (leftClick) {
			r = simLeftClick(myFakePlayer, theStack, facing);
		} else {
			r = simRightClick(myFakePlayer, theStack, getPos().offset(EnumFacing.VALUES[facing]), EnumFacing.UP);
		}
		if (theStack != null && theStack.stackSize <= 0) {
			setInventorySlotContents(tickSlot, null);
		}
		checkItemsUpdated();
		return r;
	}

	public void checkItemsUpdated() {

		ItemStack[] pInventory = myFakePlayer.inventory.mainInventory;
		int i = 0;
		for (; i < MAX_SLOT; i++) {
			setInventorySlotContents(i, pInventory[i]);
			if (inventory[i] != null && inventory[i].stackSize <= 0) {
				inventory[i] = null;
				pInventory[i] = null;
			}
		}
		for (int e = pInventory.length; i < e; i++) {
			if (InventoryHelper.addItemStackToInventory(inventory, pInventory[i], 0, MAX_SLOT - 1)) {
				pInventory[i] = null;
			}
		}
	}

	public int getNextStackIndex() {

		// FIXME: is this called too frequently? round-robin is wrong

		if ((leftClick && myFakePlayer.interactionManager.durabilityRemainingOnBlock > -1) || myFakePlayer.activeItemStack != null) {
			return slotTracker;
		}
		if (tickSlot == 0) {
			return incrementTracker();
		} else if (tickSlot == 1) {
			return getRandomStackIndex();
		}
		return 0;
	}

	public int getRandomStackIndex() {

		int i = 0;
		tracker = new int[MAX_SLOT];
		// TODO: allocating this array is probably bad

		for (int k = 0; k < MAX_SLOT; k++) {
			if (getStackInSlot(k) != null) {
				tracker[i++] = k; // track filled slots
			}
		}
		if (i == 0) {
			return incrementTracker();
		}
		int v = MathHelper.RANDOM.nextInt(i + 1); // +1 so that the tracker field is used in some cases (old behavior. wrong?)
		return i == v ? incrementTracker() : tracker[v];
	}

	public int incrementTracker() {

		slotTracker++;
		slotTracker %= MAX_SLOT;

		for (int k = slotTracker; k < MAX_SLOT; k++) {
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

		for (int i = 0; i < MAX_SLOT; i++) {
			myFakePlayer.inventory.mainInventory[i] = getStackInSlot(i);
		}
		double x = getPos().getX() + 0.5D;
		double y = getPos().getY() - 1.1D;
		double z = getPos().getZ() + 0.5D;
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
		myFakePlayer.setRotationYawHead(yaw);
		myFakePlayer.isSneaking = actsSneaking;
		myFakePlayer.eyeHeight = 0.4F;
		myFakePlayer.setItemInHand(tickSlot);

		myFakePlayer.onUpdate();
	}

	@Override
	public boolean rotateBlock() {

		if (inWorld && ServerHelper.isServerWorld(worldObj)) {
			myFakePlayer.interactionManager.cancelDestroyingBlock();
			myFakePlayer.interactionManager.durabilityRemainingOnBlock = -1;
		}
		return super.rotateBlock();
	}

	public boolean simLeftClick(EntityPlayer thePlayer, ItemStack deployingStack, int side) {

		BlockPos offsetPos = getPos().offset(EnumFacing.VALUES[facing]);
		IBlockState state = worldObj.getBlockState(offsetPos);
		if (!state.getBlock().isAir(state, worldObj, offsetPos)) {
			if (myFakePlayer.interactionManager.durabilityRemainingOnBlock == -1) {
				myFakePlayer.interactionManager.onBlockClicked(offsetPos, EnumFacing.VALUES[facing ^ 1]);
			} else if (myFakePlayer.interactionManager.durabilityRemainingOnBlock >= 9) {
				myFakePlayer.interactionManager.blockRemoving(offsetPos);
				myFakePlayer.interactionManager.durabilityRemainingOnBlock = -1;

				if (deployingStack != null) {
					deployingStack.getItem().onBlockDestroyed(deployingStack, worldObj, state, offsetPos, myFakePlayer);
				}
			}
		} else {
			myFakePlayer.interactionManager.cancelDestroyingBlock();
			myFakePlayer.interactionManager.durabilityRemainingOnBlock = -1;
			List<Entity> entities = worldObj.getEntitiesWithinAABB(Entity.class, BlockHelper.getAdjacentAABBForSide(getPos(), facing), selectAttackable);

			if (entities.size() == 0) {
				return false;
			}
			thePlayer.attackTargetEntityWithCurrentItem(entities.get(entities.size() > 1 ? MathHelper.RANDOM.nextInt(entities.size()) : 0));
		}
		return true;
	}

	public boolean simRightClick(EntityPlayer thePlayer, ItemStack deployingStack, BlockPos pos, EnumFacing side) {

		if (thePlayer.activeItemStack == null) {
			if (!simRightClick2(thePlayer, deployingStack, pos, side) && deployingStack != null) {
				List<Entity> entities = worldObj.getEntitiesWithinAABB(Entity.class, BlockHelper.getAdjacentAABBForSide(getPos(), facing));

				if (entities.size() > 0 && thePlayer.interact(entities.get(entities.size() > 1 ? MathHelper.RANDOM.nextInt(entities.size()) : 0), deployingStack, EnumHand.MAIN_HAND) != EnumActionResult.PASS) {
					return true;
				}
				//PlayerInteractEvent event = new PlayerInteractEvent.RightClickEmpty(thePlayer, Action.RIGHT_CLICK_AIR, 0, 0, 0, -1, worldObj);
				//if (event.useItem == Event.Result.DENY) {
				//	return false;
				//}
				ActionResult<ItemStack> result = deployingStack.useItemRightClick(worldObj, thePlayer, EnumHand.MAIN_HAND);
				thePlayer.inventory.setInventorySlotContents(myFakePlayer.inventory.currentItem, result.getResult() == null || result.getResult().stackSize <= 0 ? null : result.getResult());
			}
		}
		return true;
	}

	public boolean simRightClick2(EntityPlayer thePlayer, ItemStack deployingStack, BlockPos pos, EnumFacing side) {

		float f = 0.5F;
		float f1 = 0.5F;
		float f2 = 0.5F;
		int offsetY = facing == 1 ? 1 : -1;

		int angleY = 0;

		if (facing > 1) {
			if (angle == 0) {
				angleY = -1;
			}
			if (angle == 2) {
				angleY = 1;
			}
		}
		pos = pos.add(0, angleY, 0);
		PlayerInteractEvent event = new PlayerInteractEvent.RightClickBlock(thePlayer, EnumHand.MAIN_HAND, deployingStack, pos, side, new Vec3d(f, f1, f2));
		if (event.isCanceled()) {
			return false;
		}

		IBlockState state = worldObj.getBlockState(pos);

		boolean isAir = state.getBlock().isAir(state, worldObj, pos);

		if (deployingStack != null && deployingStack.getItem() != null && deployingStack.getItem().onItemUseFirst(deployingStack, thePlayer, worldObj, pos, side, f, f1, f2, EnumHand.MAIN_HAND) != EnumActionResult.PASS) {
			return true;
		}
		if (!thePlayer.isSneaking() || thePlayer.getHeldItem(EnumHand.MAIN_HAND) == null) {
			if (state.getBlock().onBlockActivated(worldObj, pos, state, thePlayer, EnumHand.MAIN_HAND, null, side, f, f1, f2)) {
				return true;
			}
		}
		if (deployingStack == null) {
			return false;
		} else {
			if (deployingStack.getItem() instanceof ItemBlock) {
				if (deployingStack.onItemUse(thePlayer, worldObj, pos.add(0, offsetY, 0), EnumHand.MAIN_HAND, EnumFacing.VALUES[facing != 1 ? 1 : 0], f, f1, f2) == EnumActionResult.PASS) {
					if (isAir) {
						if (deployingStack.onItemUse(thePlayer, worldObj, pos, EnumHand.MAIN_HAND, EnumFacing.VALUES[facing != 1 ? 1 : 0], f, f1, f2) == EnumActionResult.PASS) {
							return false;
						}
					} else {
						if (deployingStack.onItemUse(thePlayer, worldObj, pos, EnumHand.MAIN_HAND, EnumFacing.DOWN, f, f1, f2) == EnumActionResult.PASS) {
							return false;
						}
					}
				}
			} else {
				if (isAir) {
					if (deployingStack.onItemUse(thePlayer, worldObj, pos, EnumHand.MAIN_HAND, EnumFacing.VALUES[facing != 1 ? 1 : 0], f, f1, f2) == EnumActionResult.PASS) {
						if (deployingStack.onItemUse(thePlayer, worldObj, pos.add(0, offsetY, 0), EnumHand.MAIN_HAND, EnumFacing.VALUES[facing != 1 ? 1 : 0], f, f1, f2) == EnumActionResult.PASS) {
							return false;
						}
					}
				} else {
					if (deployingStack.onItemUse(thePlayer, worldObj, pos, EnumHand.MAIN_HAND, EnumFacing.DOWN, f, f1, f2) == EnumActionResult.PASS) {
						if (deployingStack.onItemUse(thePlayer, worldObj, pos.add(0, offsetY, 0), EnumHand.MAIN_HAND, EnumFacing.VALUES[facing != 1 ? 1 : 0], f, f1, f2) == EnumActionResult.PASS) {
							return false;
						}
					}
				}
			}
			if (deployingStack.stackSize <= 0) {
				MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(thePlayer, deployingStack, EnumHand.MAIN_HAND));
				thePlayer.inventory.setInventorySlotContents(myFakePlayer.inventory.currentItem, null);
			}
			return true;
		}
	}

	@Override
	protected boolean readPortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		actsSneaking = tag.getBoolean("Sneaking");
		leftClick = tag.getBoolean("LeftClick");
		tickSlot = tag.getByte("TickSlot");
		angle = tag.getByte("Angle");

		return true;
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		tag.setBoolean("Sneaking", actsSneaking);
		tag.setBoolean("LeftClick", leftClick);
		tag.setByte("TickSlot", tickSlot);
		tag.setByte("Angle", angle);

		return true;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiActivator(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerActivator(inventory, this);
	}

	@Override
	public int getInvSlotCount() {

		return MAX_SLOT;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		actsSneaking = nbt.getBoolean("Sneaking");
		leftClick = nbt.getBoolean("LeftClick");
		tickSlot = nbt.getByte("TickSlot");
		angle = nbt.getByte("Angle");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setBoolean("Sneaking", actsSneaking);
		nbt.setBoolean("LeftClick", leftClick);
		nbt.setByte("TickSlot", tickSlot);
		nbt.setByte("Angle", angle);
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addBool(leftClick);
		payload.addBool(actsSneaking);
		payload.addByte(tickSlot);
		payload.addByte(angle);

		return payload;
	}

	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addBool(leftClick);
		payload.addBool(actsSneaking);
		payload.addByte(tickSlot);
		payload.addByte(angle);

		return payload;
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		leftClick = payload.getBool();
		actsSneaking = payload.getBool();
		tickSlot = payload.getByte();
		angle = payload.getByte();
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		leftClick = payload.getBool();
		actsSneaking = payload.getBool();
		tickSlot = payload.getByte();
		angle = payload.getByte();
	}

	/* IReconfigurableFacing */
	@Override
	public boolean setFacing(int side) {

		if (side < 0 || side > 5) {
			return false;
		}
		facing = (byte) side;
		sideCache[facing] = 0;
		sideCache[facing ^ 1] = 2;
		markDirty();
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

}
