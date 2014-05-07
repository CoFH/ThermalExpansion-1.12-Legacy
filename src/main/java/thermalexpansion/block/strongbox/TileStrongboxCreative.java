package thermalexpansion.block.strongbox;

import net.minecraft.item.ItemStack;
import cofh.util.ItemHelper;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileStrongboxCreative extends TileStrongbox {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileStrongboxCreative.class, "cofh.thermalexpansion.StrongboxCreative");
	}

	public TileStrongboxCreative() {

	}

	public TileStrongboxCreative(int metadata) {

		super(metadata);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		return ItemHelper.cloneStack(inventory[slot], amount);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		if (stack == null) {
			return;
		}
		inventory[slot] = stack;
		inventory[slot].stackSize = stack.getMaxStackSize();
	}

}
