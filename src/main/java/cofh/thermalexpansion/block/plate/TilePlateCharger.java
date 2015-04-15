package cofh.thermalexpansion.block.plate;

import cofh.core.util.SocialRegistry;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.thermalexpansion.gui.client.plate.GuiPlateCharger;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class TilePlateCharger extends TilePlatePoweredBase {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateCharger.class, "cofh.thermalexpansion.PlateCharger");
	}

	protected static final int CHARGE_RATE = 1000;

	protected long worldTime;
	protected int chargeLeft;

	public TilePlateCharger() {

		super(BlockPlate.Types.POWERED_SIGNAL, 32000);
		storage.setMaxTransfer(CHARGE_RATE);
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiPlateCharger(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this, false, false);
	}

	@Override
	public void onEntityCollidedWithBlock(Entity theEntity) {

		if (theEntity.worldObj.isRemote) {
			return;
		}

		Class<? extends Entity> comp = EntityLivingBase.class;
		if (!getAccess().isPublic()) {
			comp = EntityPlayer.class;
		}

		if (!comp.isInstance(theEntity) && !(theEntity instanceof EntityItem)) {
			return;
		}

		if (worldTime != theEntity.worldObj.getTotalWorldTime()) {
			worldTime = theEntity.worldObj.getTotalWorldTime();
			chargeLeft = Math.min(CHARGE_RATE, storage.getEnergyStored());
		}
		if (chargeLeft <= 0) {
			return;
		}

		l: if (!getAccess().isPublic()) {
			o: if (theEntity instanceof EntityItem) {
				String name = ((EntityItem) theEntity).func_145800_j();
				if (name == null) {
					break o;
				}
				if (getAccess().isRestricted() && SocialRegistry.playerHasAccess(name, getOwner())) {
					break l;
				}
				GameProfile i = MinecraftServer.getServer().func_152358_ax().func_152655_a(name);
				if (getOwner().getId().equals(i.getId())) {
					break l;
				}
			} else if (canPlayerAccess((EntityPlayer) theEntity)) {
				break l;
			}
			return;
		}

		if (theEntity instanceof EntityItem) {
			ItemStack item = ((EntityItem) theEntity).getEntityItem();
			if (chargeItem(item)) {
				((EntityItem) theEntity).age = 0;
			}
			((EntityItem) theEntity).setEntityItemStack(item);
		} else {
			for (int i = 0; i < 5; ++i) {
				chargeItem(((EntityLivingBase) theEntity).getEquipmentInSlot(i));
			}
		}
	}

	protected boolean chargeItem(ItemStack item) {

		int ins = EnergyHelper.insertEnergyIntoContainer(item, chargeLeft, false);
		chargeLeft -= storage.extractEnergy(ins, false);
		return ins > 0;
	}

}
