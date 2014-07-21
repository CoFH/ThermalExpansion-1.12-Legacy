package thermalexpansion.item;

import cofh.api.item.IAugmentItem;
import cofh.item.ItemBase;
import cofh.util.StringHelper;

import gnu.trove.map.hash.THashMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import thermalexpansion.ThermalExpansion;

public class ItemAugment extends ItemBase implements IAugmentItem {

	public class AugmentEntry {

		public Map<String, Integer> augmentTypeInfo = new THashMap<String, Integer>();
	}

	Map<Integer, AugmentEntry> augmentMap = new THashMap<Integer, AugmentEntry>();

	public ItemAugment() {

		super("thermalexpansion");
		setCreativeTab(ThermalExpansion.tabItems);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {

		return StringHelper.localize("info.thermalexpansion.augment") + ": " + StringHelper.localize(getUnlocalizedName(stack) + ".name");
	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}

	}

	public void addAugmentData(int number, String augmentType, int augmentLevel) {

		if (!augmentMap.containsKey(Integer.valueOf(number))) {
			augmentMap.put(Integer.valueOf(number), new AugmentEntry());
		}
		augmentMap.get(Integer.valueOf(number)).augmentTypeInfo.put(augmentType, augmentLevel);
	}

	/* IAugmentItem */
	@Override
	public int getAugmentLevel(ItemStack stack, String type) {

		AugmentEntry entry = augmentMap.get(stack.getItemDamage());
		if (!entry.augmentTypeInfo.containsKey(type)) {
			return 0;
		}
		return entry.augmentTypeInfo.get(type);
	}

	@Override
	public Set<String> getAugmentTypes(ItemStack stack) {

		return augmentMap.get(stack.getItemDamage()).augmentTypeInfo.keySet();
	}

}
