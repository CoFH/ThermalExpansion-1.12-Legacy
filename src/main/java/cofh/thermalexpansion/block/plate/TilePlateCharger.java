package cofh.thermalexpansion.block.plate;

import cofh.core.RegistrySocial;
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
	public ContainerTEBase getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this, false, false);
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

		if (!comp.isInstance(entity) && !(entity instanceof EntityItem)) {
			return;
		}

		if (worldTime != entity.worldObj.getTotalWorldTime()) {
			worldTime = entity.worldObj.getTotalWorldTime();
			chargeLeft = Math.min(CHARGE_RATE, storage.getEnergyStored());
		}
		if (chargeLeft <= 0) {
			return;
		}

		l: if (!getAccess().isPublic()) {
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

}
