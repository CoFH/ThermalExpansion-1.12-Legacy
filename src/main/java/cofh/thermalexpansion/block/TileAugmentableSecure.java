package cofh.thermalexpansion.block;

import codechicken.lib.util.ServerUtils;
import cofh.api.item.IAugmentItem;
import cofh.api.tileentity.IAugmentable;
import cofh.api.tileentity.ISecurable;
import cofh.core.CoFHProps;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.GuiHandler;
import cofh.thermalexpansion.util.Utils;
import com.google.common.base.Strings;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IWorldNameable;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

public abstract class TileAugmentableSecure extends TileTEBase implements IAugmentable, ISecurable, IWorldNameable {

	/* AUGMENTS */
	protected boolean[] augmentStatus = new boolean[3];
	protected ItemStack[] augments = new ItemStack[3];

	/* SECURITY */
	protected GameProfile owner = CoFHProps.DEFAULT_OWNER;
	protected AccessMode access = AccessMode.PUBLIC;
	protected boolean canAccess = true;

	/* LEVEL FEATURES */
	protected byte level = 0;
	protected boolean hasAutoInput = false;
	protected boolean hasAutoOutput = false;

	protected boolean enableAutoInput = false;
	protected boolean enableAutoOutput = false;

	protected static final int ENERGY_CAPACITY[] = new int[] { 2, 3, 4, 5, 5 };
	protected static final int ENERGY_TRANSFER[] = new int[] { 3, 6, 12, 24, 24 };
	protected static final int FLUID_CAPACITY[] = new int[] { 1, 2, 4, 8, 8 };
	protected static final int FLUID_TRANSFER[] = new int[] { 100, 200, 400, 800, 800 };
	protected static final int ITEM_TRANSFER[] = new int[] { 8, 16, 32, 64, 64 };

	public boolean isSecured() {

		return !SecurityHelper.isDefaultUUID(owner.getId());
	}

	public boolean enableSecurity() {

		return true;
	}

	protected void onLevelChange() {

	}

	/* GUI METHODS */
	@Override
	public void receiveGuiNetworkData(int id, int data) {

		if (data == 0) {
			canAccess = false;
		} else {
			canAccess = true;
		}
	}

	@Override
	public void sendGuiNetworkData(Container container, IContainerListener listener) {

		super.sendGuiNetworkData(container, listener);
		if (listener instanceof EntityPlayer) {
			listener.sendProgressBarUpdate(container, 0, canPlayerAccess(((EntityPlayer) listener)) ? 1 : 0);
		}
	}

	public boolean canAccess() {

		return canAccess;
	}

	@Override
	public boolean openGui(EntityPlayer player) {

		if (canPlayerAccess(player)) {
			if (hasGui()) {
				player.openGui(ThermalExpansion.instance, GuiHandler.TILE_ID, worldObj, pos.getX(), pos.getY(), pos.getZ());
			}
			return hasGui();
		}
		if (ServerHelper.isServerWorld(worldObj)) {
			player.addChatMessage(new TextComponentTranslation("chat.cofh.secure", getOwnerName()));
		}
		return false;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		owner = CoFHProps.DEFAULT_OWNER;
		access = AccessMode.values()[nbt.getByte("Access")];

		String uuid = nbt.getString("OwnerUUID");
		String name = nbt.getString("Owner");
		if (!Strings.isNullOrEmpty(uuid)) {
			setOwner(new GameProfile(UUID.fromString(uuid), name));
		} else {
			setOwnerName(name);
		}

		if (!enableSecurity()) {
			access = AccessMode.PUBLIC;
		}
		level = nbt.getByte("Level");
		onLevelChange();

		readAugmentsFromNBT(nbt);
		installAugments();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Access", (byte) access.ordinal());
		nbt.setString("OwnerUUID", owner.getId().toString());
		nbt.setString("Owner", owner.getName());

		nbt.setByte("Level", level);

		writeAugmentsToNBT(nbt);
		return nbt;
	}

	public void readAugmentsFromNBT(NBTTagCompound nbt) {

		NBTTagList list = nbt.getTagList("Augments", 10);

		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			int slot = tag.getInteger("Slot");
			if (slot >= 0 && slot < augments.length) {
				augments[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}

	public void writeAugmentsToNBT(NBTTagCompound nbt) {

		if (augments.length <= 0) {
			return;
		}
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < augments.length; i++) {
			if (augments[i] != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("Slot", i);
				augments[i].writeToNBT(tag);
				list.appendTag(tag);
			}
		}
		nbt.setTag("Augments", list);
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addByte((byte) access.ordinal());
		payload.addUUID(owner.getId());
		payload.addString(owner.getName());

		payload.addByte(level);

		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		access = ISecurable.AccessMode.values()[payload.getByte()];

		if (!isServer) {
			owner = CoFHProps.DEFAULT_OWNER;
			setOwner(new GameProfile(payload.getUUID(), payload.getString()));

			byte curLevel = level;
			level = payload.getByte();

			if (curLevel != level) {
				onLevelChange();
			}
		} else {
			payload.getUUID();
			payload.getString();
			payload.getByte();
		}
	}

	/* HELPERS */
	protected boolean hasAugment(String type, int augLevel) {

		for (int i = 0; i < augments.length; i++) {
			if (Utils.isAugmentItem(augments[i]) && ((IAugmentItem) augments[i].getItem()).getAugmentLevel(augments[i], type) == augLevel) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasDuplicateAugment(String type, int augLevel, int slot) {

		for (int i = 0; i < augments.length; i++) {
			if (i != slot && Utils.isAugmentItem(augments[i]) && ((IAugmentItem) augments[i].getItem()).getAugmentLevel(augments[i], type) == augLevel) {
				return true;
			}
		}
		return false;
	}

	protected boolean installAugment(int slot) {

		return true;
	}

	protected void onInstalled() {

	}

	protected void resetAugments() {

	}

	/* IAugmentable */
	@Override
	public ItemStack[] getAugmentSlots() {

		return augments;
	}

	@Override
	public boolean[] getAugmentStatus() {

		return augmentStatus;
	}

	@Override
	public void installAugments() {

		resetAugments();
		for (int i = 0; i < augments.length; i++) {
			augmentStatus[i] = false;
			if (Utils.isAugmentItem(augments[i])) {
				augmentStatus[i] = installAugment(i);
			}
		}
		if (worldObj != null && ServerHelper.isServerWorld(worldObj)) {
			onInstalled();
			sendUpdatePacket(Side.CLIENT);
		}
	}

	/* ISecurable */
	@Override
	public boolean setAccess(AccessMode access) {

		this.access = access;
		sendUpdatePacket(Side.SERVER);
		return true;
	}

	@Override
	public boolean setOwnerName(String name) {

		MinecraftServer server = ServerUtils.mc();
		if (server == null) {
			return false;
		}
		if (Strings.isNullOrEmpty(name) || CoFHProps.DEFAULT_OWNER.getName().equalsIgnoreCase(name)) {
			return false;
		}
		String uuid = PreYggdrasilConverter.convertMobOwnerIfNeeded(server, name);
		if (Strings.isNullOrEmpty(uuid)) {
			return false;
		}
		return setOwner(new GameProfile(UUID.fromString(uuid), name));
	}

	@Override
	public boolean setOwner(GameProfile profile) {

		if (SecurityHelper.isDefaultUUID(owner.getId())) {
			owner = profile;
			if (!SecurityHelper.isDefaultUUID(owner.getId())) {
				if (ServerUtils.mc() != null) {
					new Thread("CoFH User Loader") {

						@Override
						public void run() {

							owner = SecurityHelper.getProfile(owner.getId(), owner.getName());
						}
					}.start();
				}
				if (inWorld) {
					markChunkDirty();
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public AccessMode getAccess() {

		return access;
	}

	@Override
	public String getOwnerName() {

		String name = owner.getName();
		if (name == null) {
			return StringHelper.localize("info.cofh.anotherplayer");
		}
		return name;
	}

	@Override
	public GameProfile getOwner() {

		return owner;
	}

	/* IWorldNameable */
	@Override
	public String getName() {

		return tileName.isEmpty() ? getTileName() : tileName;
	}

	@Override
	public boolean hasCustomName() {

		return !tileName.isEmpty();
	}

	@Override
	public ITextComponent getDisplayName() {

		return new TextComponentString(getName());
	}

}