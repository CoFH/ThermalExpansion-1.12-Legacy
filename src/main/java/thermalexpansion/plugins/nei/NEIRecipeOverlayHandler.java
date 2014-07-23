package thermalexpansion.plugins.nei;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.recipe.IRecipeHandler;
import cofh.gui.GuiBase;
import cofh.network.PacketCoFHBase;
import cofh.network.PacketHandler;
import cofh.network.PacketTileInfo;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;

import thermalexpansion.block.device.TileWorkbench;
import thermalexpansion.gui.client.device.GuiWorkbench;

public class NEIRecipeOverlayHandler implements IOverlayHandler {

	public int xOffset = 19;
	public int yOffset = 13;

	public NEIRecipeOverlayHandler() {

	}

	public NEIRecipeOverlayHandler(int x, int y) {

		xOffset = x;
		yOffset = y;
	}

	@Override
	public void overlayRecipe(GuiContainer firstGui, IRecipeHandler recipe, int recipeIndex, boolean shift) {

		if (firstGui instanceof GuiWorkbench) {
			for (Object curObj : firstGui.inventorySlots.inventorySlots) {
				Slot curSlot = (Slot) curObj;
				curSlot.putStack(null);
			}
			PacketCoFHBase payload = PacketTileInfo.newPacket(((GuiWorkbench) firstGui).myTile);
			payload.addByte(TileWorkbench.PacketInfoID.NEI_SUP.ordinal());
			boolean foundSlots = false;
			List<PositionedStack> item = recipe.getIngredientStacks(recipeIndex);
			for (PositionedStack curItem : item) {
				for (Object curObj : firstGui.inventorySlots.inventorySlots) {
					Slot curSlot = (Slot) curObj;

					if (curSlot.xDisplayPosition == curItem.relx + xOffset && curSlot.yDisplayPosition == curItem.rely + yOffset) {
						curSlot.putStack(curItem.item.copy());
						foundSlots = true;
						payload.addByte(curSlot.getSlotIndex());
						payload.addItemStack(curItem.item);
						break;
					}
				}
			}
			payload.addByte(-1);
			((GuiBase) firstGui).overlayRecipe();

			if (foundSlots) {
				PacketHandler.sendToServer(payload);
			}
		}
	}

}
