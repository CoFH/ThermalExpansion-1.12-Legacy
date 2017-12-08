package cofh.thermalexpansion.proxy;

import cofh.core.util.helpers.InventoryHelper;
import cofh.thermalexpansion.item.ItemSatchel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class EventHandler {

	public static final EventHandler INSTANCE = new EventHandler();

	@SubscribeEvent
	public void handlePlayerLoggedInEvent(PlayerLoggedInEvent event) {

		// PacketTEBase.sendConfigSyncPacketToClient(event.player);
	}

	@SubscribeEvent
	public void handleItemPickup(EntityItemPickupEvent event) {

		if(event.isCanceled()) {
			return;
		}

		InventoryPlayer inventory = event.getEntityPlayer().inventory;
		for(int i = 0; i < inventory.getSizeInventory(); i++) {

			ItemStack stack = inventory.getStackInSlot(i);
			if(stack.getItem() instanceof ItemSatchel) {
				ItemSatchel.onItemPickup(event, stack);
				return;
			}
		}
	}

}
