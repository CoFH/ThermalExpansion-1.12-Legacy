package cofh.thermalexpansion.proxy;

import cofh.thermalexpansion.gui.container.storage.ContainerSatchel;
import cofh.thermalexpansion.gui.container.storage.ContainerSatchelFilter;
import cofh.thermalexpansion.item.ItemSatchel;
import cofh.thermalexpansion.network.PacketTEBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class EventHandler {

	public static final EventHandler INSTANCE = new EventHandler();

	@SubscribeEvent
	public void handlePlayerLoggedInEvent(PlayerLoggedInEvent event) {

		PacketTEBase.sendConfigSyncPacketToClient(event.player);
	}

	@SubscribeEvent
	public void handleEntityItemPickup(EntityItemPickupEvent event) {

		EntityPlayer player = event.getEntityPlayer();
		if (player.openContainer instanceof ContainerSatchel || player.openContainer instanceof ContainerSatchelFilter) {
			return;
		}
		InventoryPlayer inventory = player.inventory;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.getItem() instanceof ItemSatchel && ItemSatchel.onItemPickup(event, stack)) {
				event.setCanceled(true);
				return;
			}
		}
	}

}
