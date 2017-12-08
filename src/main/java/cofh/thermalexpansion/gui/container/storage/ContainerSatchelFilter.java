package cofh.thermalexpansion.gui.container.storage;

import cofh.api.core.ISecurable;
import cofh.core.gui.container.ContainerCore;
import cofh.core.util.CoreUtils;
import cofh.core.util.helpers.SecurityHelper;
import cofh.thermalexpansion.gui.slot.SlotFilter;
import cofh.thermalexpansion.item.ItemSatchel;
import cofh.thermalexpansion.network.PacketTEBase;
import cofh.thermalexpansion.util.FilterItemWrapper;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

public class ContainerSatchelFilter extends ContainerCore implements ISecurable {

    private FilterItemWrapper filterWrapper;
    private EntityPlayer player;
    private int filterIndex;
    private boolean valid = true;

    public ContainerSatchelFilter(ItemStack stack, InventoryPlayer inventory) {

        filterWrapper = new FilterItemWrapper(stack, ItemSatchel.getFilterSize(stack) + 7);
        filterIndex = inventory.currentItem;
        player = inventory.player;

        bindPlayerInventory(inventory);
        addFilterSlots();
    }

    private void addFilterSlots() {

        int x0 = 7;
        int y0 = 21;

        for(int i = 0; i <= ItemSatchel.getLevel(filterWrapper.getFilterStack()); i++) {
            for(int j = 0; j < 7; j++) {
                addSlotToContainer(new SlotFilter(filterWrapper, 7 * i + j, x0 + (18 * j), y0 + (18 * i)));
            }
        }
    }

    public ItemStack getFilterStack() {

        return filterWrapper.getFilterStack();
    }

    public void setFlag(int flag, boolean value) {

        filterWrapper.getFilter().setFlag(flag, value);
        if(CoreUtils.isClient()) {
            PacketTEBase.sendFilterPacketToServer(flag, value);
        }

        filterWrapper.markDirty();
    }

    public boolean getFlag(int flag) {

        return filterWrapper.getFilter().getFlag(flag);
    }

    @Override
    public void detectAndSendChanges() {

        ItemStack item = player.inventory.mainInventory.get(filterIndex);
        if (item.isEmpty() || item.getItem() != filterWrapper.getFilterItem()) {
            valid = false;
            return;
        }
        super.detectAndSendChanges();
    }

    public void onSlotChanged() {

        ItemStack item = player.inventory.mainInventory.get(filterIndex);
        if (valid && !item.isEmpty() && item.getItem() == filterWrapper.getFilterItem()) {
            player.inventory.mainInventory.set(filterIndex, filterWrapper.getFilterStack());
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {

        onSlotChanged();
        if (filterWrapper.getDirty() && !valid) {
            player.inventory.setItemStack(ItemStack.EMPTY);
        }
        return valid;
    }

    @Override
    protected int getPlayerInventoryVerticalOffset() {

        return 133;
    }

    @Override
    protected int getSizeInventory() {
        return 0;
    }

    /* ISecurable */
    @Override
    public boolean setAccess(ISecurable.AccessMode access) {

        if (SecurityHelper.setAccess(getFilterStack(), access)) {
            onSlotChanged();

            if (CoreUtils.isClient()) {
                PacketTEBase.sendSecurityPacketToServer(this);
            }
            return true;
        }
        return false;
    }

    @Override
    public ISecurable.AccessMode getAccess() {

        return SecurityHelper.getAccess(getFilterStack());
    }

    @Override
    public String getOwnerName() {

        return SecurityHelper.getOwnerName(getFilterStack());
    }

    @Override
    public GameProfile getOwner() {

        return SecurityHelper.getOwner(getFilterStack());
    }

    @Override
    public boolean canPlayerAccess(EntityPlayer player) {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setOwnerName(String name) {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setOwner(GameProfile name) {

        throw new UnsupportedOperationException();
    }
}
