package cofh.thermalexpansion.util;

import cofh.core.util.helpers.InventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class FilterItemWrapper implements IInventory {

    private ItemStack stack;
    private Filter filter;
    private boolean dirty = false;

    public FilterItemWrapper(ItemStack stack, int size) {

        this.stack = stack;
        this.filter = new Filter(size);
        filter.deserializeNBT(stack.getTagCompound().getCompoundTag("Filter"));
        markDirty();
    }

    public Filter getFilter() {

        return filter;
    }

    public ItemStack getFilterStack() {

        return stack;
    }

    public Item getFilterItem() {

        return stack.getItem();
    }

    /* IInventory */
    @Override
    public int getSizeInventory() {

        return filter.getSize();
    }

    @Override
    public boolean isEmpty() {

        return InventoryHelper.isEmpty(filter.getItems());
    }

    @Override
    public ItemStack getStackInSlot(int index) {

        return filter.getSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {

        if(count > 0) {
            filter.setSlot(index, ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {

        return decrStackSize(index, 1);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {

        filter.setSlot(index, stack);
    }

    @Override
    public int getInventoryStackLimit() {

        return 1;
    }

    @Override
    public void markDirty() {

        stack.setTagInfo("Filter", filter.serializeNBT());
        dirty = true;
    }

    public boolean getDirty() {

        boolean r = dirty;
        dirty = false;
        return r;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {

        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

        markDirty();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {

        return true;
    }

    @Override
    public int getField(int id) {

        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {

        return 0;
    }

    @Override
    public void clear() {

        filter.clear();
    }

    @Override
    public String getName() {

        return stack.getDisplayName();
    }

    @Override
    public boolean hasCustomName() {

        return stack.hasDisplayName();
    }

    @Override
    public ITextComponent getDisplayName() {

        return new TextComponentTranslation(stack.getDisplayName());
    }
}
