package cofh.thermalexpansion.item;

import codechicken.lib.item.ItemMultiType;
import codechicken.lib.util.SoundUtils;
import cofh.api.energy.IEnergyContainerItem;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class ItemCapacitor extends ItemMultiType implements IEnergyContainerItem {

	public ItemCapacitor() {

		super(ThermalExpansion.tabTools, "thermalexpansion:capacitor");
//		setMaxDamage(1);
		setMaxStackSize(1);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {

		if (ENABLE[0]) {
			list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, Types.CREATIVE.ordinal()), CAPACITY[Types.CREATIVE.ordinal()]));
		}
		list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, Types.POTATO.ordinal()), CAPACITY[Types.POTATO.ordinal()]));
		for (int i = 2; i < Types.values().length; i++) {
			list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, i), 0));
			list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, i), CAPACITY[i]));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack item) {

		return "item.thermalexpansion.capacitor." + NAMES[ItemHelper.getItemDamage(item)];
	}

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) &&
                (slotChanged || !ItemHelper.areItemStacksEqualIgnoreTags(oldStack, newStack, "Energy"));
    }

    @Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean check) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (stack.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(stack, 0);
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		if (ItemHelper.getItemDamage(stack) == Types.CREATIVE.ordinal()) {
			list.add(StringHelper.localize("info.cofh.charge") + ": 1.21G RF");
			list.add(StringHelper.localize("info.cofh.send") + "/" + StringHelper.localize("info.cofh.receive") + ": " + SEND[Types.CREATIVE.ordinal()]
					+ " RF/t");
		} else {
			list.add(StringHelper.localize("info.cofh.charge") + ": " + StringHelper.getScaledNumber(stack.getTagCompound().getInteger("Energy")) + " / "
					+ StringHelper.getScaledNumber(CAPACITY[ItemHelper.getItemDamage(stack)]) + " RF");
			list.add(StringHelper.localize("info.cofh.send") + "/" + StringHelper.localize("info.cofh.receive") + ": " + SEND[ItemHelper.getItemDamage(stack)]
					+ "/" + RECEIVE[ItemHelper.getItemDamage(stack)] + " RF/t");
		}
		if (isActive(stack)) {
			list.add(StringHelper.getInfoText("info.thermalexpansion.capacitor.2"));
			list.add(StringHelper.getInfoText("info.thermalexpansion.capacitor.4"));
			list.add(StringHelper.getDeactivationText("info.thermalexpansion.capacitor.3"));
		} else {
			list.add(StringHelper.getInfoText("info.thermalexpansion.capacitor.0"));
			list.add(StringHelper.getInfoText("info.thermalexpansion.capacitor.4"));
			list.add(StringHelper.getActivationText("info.thermalexpansion.capacitor.1"));
		}
		if (ItemHelper.getItemDamage(stack) == Types.POTATO.ordinal()) {
			list.add(StringHelper.getFlavorText("info.thermalexpansion.capacitor.potato"));
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isCurrentItem) {
		//This is not ideal but stack.setItem is broken
		if (stack.getItemDamage() == Types.POTATO.ordinal() && getEnergyStored(stack) == 0) {
			((EntityPlayer) entity).inventory.setInventorySlotContents(slot, new ItemStack(Items.BAKED_POTATO));
		}

		if (slot > 8 || !isActive(stack) || isCurrentItem) {
			return;
		}
		InventoryPlayer playerInv = ((EntityPlayer) entity).inventory;
		IEnergyContainerItem containerItem;
		int toSend = Math.min(getEnergyStored(stack), SEND[ItemHelper.getItemDamage(stack)]);

		ItemStack currentItem = playerInv.getCurrentItem();

		if (EnergyHelper.isEnergyContainerItem(currentItem)) {
			containerItem = (IEnergyContainerItem) currentItem.getItem();
			extractEnergy(stack, containerItem.receiveEnergy(currentItem, toSend, false), false);
		}
	}

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
		if (CoreUtils.isFakePlayer(player)) {
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStack);
		}
		if (player.isSneaking()) {
			if (setActiveState(itemStack, !isActive(itemStack))) {
				if (isActive(itemStack)) {
                    SoundUtils.playSoundAt(player, SoundCategory.NEUTRAL, SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, 0.2F, 0.8F);
				} else {
                    SoundUtils.playSoundAt(player, SoundCategory.NEUTRAL, SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, 0.2F, 0.5F);
				}
			}
		}
		player.swingArm(hand);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
	}

	@Override
	public boolean hasEffect(ItemStack stack) {

		return isActive(stack);
	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public boolean isItemTool(ItemStack stack) {

		return false;
	}

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return EnumActionResult.FAIL;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;//super.showDurabilityBar(stack);
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			return 1;
		}
		return 1D - ((double) getEnergyStored(stack) / (double) CAPACITY[ItemHelper.getItemDamage(stack)]);
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return super.getMaxDamage(stack);
	}

	/* IEnergyContainerItem */
	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {

		int metadata = ItemHelper.getItemDamage(container);
		if (metadata <= Types.POTATO.ordinal()) {
			return 0;
		}
		if (container.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		int stored = container.getTagCompound().getInteger("Energy");
		int receive = Math.min(maxReceive, Math.min(CAPACITY[metadata] - stored, RECEIVE[metadata]));

		if (!simulate && container.getItemDamage() != Types.CREATIVE.ordinal()) {
			stored += receive;
			container.getTagCompound().setInteger("Energy", stored);
		}
		return receive;
	}

	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {

		if (container.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		int stored = container.getTagCompound().getInteger("Energy");
		int extract = Math.min(maxExtract, Math.min(stored, SEND[ItemHelper.getItemDamage(container)]));

		if (!simulate && container.getItemDamage() != Types.CREATIVE.ordinal()) {
			stored -= extract;
			container.getTagCompound().setInteger("Energy", stored);
		}
		return extract;
	}

	@Override
	public int getEnergyStored(ItemStack container) {

		if (container.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		return container.getTagCompound().getInteger("Energy");
	}

	@Override
	public int getMaxEnergyStored(ItemStack container) {

		return CAPACITY[container.getItemDamage()];
	}

	/* HELPERS */
	public boolean isActive(ItemStack stack) {

		return stack.getTagCompound() != null && stack.getTagCompound().getBoolean("Active");
	}

	public boolean setActiveState(ItemStack stack, boolean state) {

		if (getEnergyStored(stack) > 0) {
			stack.getTagCompound().setBoolean("Active", state);
			return true;
		}
		stack.getTagCompound().setBoolean("Active", false);
		return false;
	}

	public enum Types {
		CREATIVE, POTATO, BASIC, HARDENED, REINFORCED, RESONANT
	}

	public static final String[] NAMES = { "creative", "potato", "basic", "hardened", "reinforced", "resonant" };

	public static boolean[] ENABLE = { true, true, true, true, true, true };
	public static int[] SEND = { 100000, 160, 80, 400, 4000, 16000 };
	public static int[] RECEIVE = { 0, 0, 200, 800, 8000, 32000 };
	public static int[] CAPACITY = { 100000, 32000, 80000, 400000, 4000000, 20000000 };

	static {
		String category2 = "Item.Capacitor.";
		String category = category2 + StringHelper.titleCase(NAMES[0]);
		ENABLE[0] = ThermalExpansion.config.get(category, "Enable", ENABLE[0]);

		for (int i = 1; i < Types.values().length; i++) {
			category = category2 + StringHelper.titleCase(NAMES[i]);
			ENABLE[i] = ThermalExpansion.config.get(category, "Recipe", ENABLE[i]);
		}

		category = category2 + StringHelper.titleCase(NAMES[5]);
		CAPACITY[5] = MathHelper.clamp(ThermalExpansion.config.get(category, "Capacity", CAPACITY[5]), CAPACITY[5] / 10, 1000000 * 1000);
		SEND[5] = MathHelper.clamp(ThermalExpansion.config.get(category, "Send", SEND[5]), SEND[5] / 10, SEND[5] * 1000);
		RECEIVE[5] = MathHelper.clamp(ThermalExpansion.config.get(category, "Receive", RECEIVE[5]), RECEIVE[5] / 10, RECEIVE[4] * 1000);

		category = category2 + StringHelper.titleCase(NAMES[4]);
		CAPACITY[4] = MathHelper.clamp(ThermalExpansion.config.get(category, "Capacity", CAPACITY[4]), CAPACITY[4] / 10, CAPACITY[5]);
		SEND[4] = MathHelper.clamp(ThermalExpansion.config.get(category, "Send", SEND[4]), SEND[4] / 10, SEND[4] * 1000);
		RECEIVE[4] = MathHelper.clamp(ThermalExpansion.config.get(category, "Receive", RECEIVE[4]), RECEIVE[4] / 10, RECEIVE[4] * 1000);

		category = category2 + StringHelper.titleCase(NAMES[3]);
		CAPACITY[3] = MathHelper.clamp(ThermalExpansion.config.get(category, "Capacity", CAPACITY[3]), CAPACITY[3] / 10, CAPACITY[4]);
		SEND[3] = MathHelper.clamp(ThermalExpansion.config.get(category, "Send", SEND[3]), SEND[3] / 10, SEND[3] * 1000);
		RECEIVE[3] = MathHelper.clamp(ThermalExpansion.config.get(category, "Receive", RECEIVE[3]), RECEIVE[3] / 10, RECEIVE[3] * 1000);

		category = category2 + StringHelper.titleCase(NAMES[2]);
		CAPACITY[2] = MathHelper.clamp(ThermalExpansion.config.get(category, "Capacity", CAPACITY[2]), CAPACITY[2] / 10, CAPACITY[3]);
		RECEIVE[2] = MathHelper.clamp(ThermalExpansion.config.get(category, "Receive", RECEIVE[2]), RECEIVE[2] / 10, RECEIVE[2] * 1000);
		SEND[2] = MathHelper.clamp(ThermalExpansion.config.get(category, "Send", SEND[2]), SEND[2] / 10, SEND[2] * 1000);

		category = category2 + StringHelper.titleCase(NAMES[1]);
		CAPACITY[1] = MathHelper.clamp(ThermalExpansion.config.get(category, "Capacity", CAPACITY[1]), CAPACITY[1] / 10, CAPACITY[2]);
		SEND[1] = MathHelper.clamp(ThermalExpansion.config.get(category, "Send", SEND[1]), SEND[1] / 10, SEND[1] * 1000);
		// RECEIVE[1] = MathHelper.clamp(ThermalExpansion.config.get(category, "Receive", RECEIVE[1]), RECEIVE[1] / 10, RECEIVE[1] * 1000);

		category = category2 + StringHelper.titleCase(NAMES[0]);
		SEND[0] = MathHelper.clamp(ThermalExpansion.config.get(category, "Send", SEND[0]), SEND[0] / 10, SEND[0] * 1000);
		CAPACITY[0] = SEND[0];
	}

}
