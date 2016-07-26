package cofh.thermalexpansion.block.plate;

import cofh.core.RegistrySocial;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.thermalexpansion.gui.client.plate.GuiPlateCharge;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class TilePlateCharge extends TilePlatePoweredBase {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateCharge.class, "cofh.thermalexpansion.PlateCharger");
	}

	protected static final int CHARGE_RATE = 1000;

	protected long worldTime;
	protected int chargeLeft;

	public boolean chargeItems = true;

	public TilePlateCharge() {

		super(BlockPlate.Types.CHARGE, 80000);
		storage.setMaxTransfer(CHARGE_RATE);
		filterSecure = true;
	}

	@Override
	public void onEntityCollidedWithBlock(Entity entity) {

		if (entity.worldObj.isRemote) {
			return;
		}
		Class<? extends Entity> comp = EntityLivingBase.class;
		if (!getAccess().isPublic()) {
			comp = EntityPlayer.class;
		}

		if (!comp.isInstance(entity) && !(chargeItems && entity instanceof EntityItem)) {
			return;
		}

		if (worldTime != entity.worldObj.getTotalWorldTime()) {
			worldTime = entity.worldObj.getTotalWorldTime();
			chargeLeft = Math.min(CHARGE_RATE, storage.getEnergyStored());
		}

		if (chargeLeft <= 0) {
			return;
		}

		l: if (filterSecure && !getAccess().isPublic()) {
			o: if (entity instanceof EntityItem) {
				String name = ((EntityItem) entity).func_145800_j();
				if (name == null) {
					break o;
				}
				if (getAccess().isRestricted() && RegistrySocial.playerHasAccess(name, getOwner())) {
					break l;
				}
				GameProfile i = MinecraftServer.getServer().func_152358_ax().func_152655_a(name);
				if (i != null && getOwner().getId().equals(i.getId())) {
					break l;
				}
			} else if (canPlayerAccess((EntityPlayer) entity)) {
				break l;
			}
			return;
		}

		if (entity instanceof EntityItem) {
			ItemStack item = ((EntityItem) entity).getEntityItem();
			if (chargeItem(item)) {
				((EntityItem) entity).age = 0;
			}
			((EntityItem) entity).setEntityItemStack(item);
		} else {
			for (int i = 0; i < 5; ++i) {
				chargeItem(((EntityLivingBase) entity).getEquipmentInSlot(i));
			}
		}
	}

	protected boolean chargeItem(ItemStack item) {

		int ins = EnergyHelper.insertEnergyIntoContainer(item, chargeLeft, false);
		chargeLeft -= storage.extractEnergy(ins, false);
		return ins > 0;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiPlateCharge(inventory, this);
	}

	@Override
	public ContainerTEBase getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this, false, false);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		chargeItems = nbt.getBoolean("chargeItems");

		if (!nbt.hasKey("FilterSecure")) {
			filterSecure = true;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setBoolean("chargeItems", chargeItems);
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addBool(chargeItems);

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addBool(chargeItems);

		return payload;
	}

	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addBool(chargeItems);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		chargeItems = payload.getBool();
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		chargeItems = payload.getBool();

		sendDescPacket();
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {
			chargeItems = payload.getBool();
		} else {
			payload.getBool();
		}
	}

}
