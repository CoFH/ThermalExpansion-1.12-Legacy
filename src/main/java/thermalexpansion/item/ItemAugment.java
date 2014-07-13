package thermalexpansion.item;

import cofh.api.item.IAugmentItem;
import cofh.item.ItemBase;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;

public class ItemAugment extends ItemBase implements IAugmentItem {

	public class AugmentEntry {

		public Map augmentTypeMap = new THashMap<String, Integer>();
	}

	List<AugmentEntry> augmentList = new ArrayList<AugmentEntry>();

	public ItemAugment() {

		super("thermalexpansion");
	}

	/* IAugmentItem */
	@Override
	public int getAugmentLevel(ItemStack stack, String type) {

		return (Integer) augmentList.get(stack.getItemDamage()).augmentTypeMap.get(type);
	}

	@Override
	public Set<String> getAugmentTypes(ItemStack stack) {

		return augmentList.get(stack.getItemDamage()).augmentTypeMap.keySet();
	}

}
