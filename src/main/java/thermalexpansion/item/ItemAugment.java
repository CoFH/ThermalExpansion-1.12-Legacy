package thermalexpansion.item;

import cofh.api.item.IAugmentItem;
import cofh.item.ItemBase;
import cofh.util.ItemHelper;
import cofh.util.StringHelper;

import gnu.trove.map.hash.THashMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.machine.ItemBlockMachine;

public class ItemAugment extends ItemBase implements IAugmentItem {

	public class AugmentEntry {

		public String primaryType = "";
		public int primaryLevel = 0;
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
		String type = getPrimaryType(stack);
		list.add(StringHelper.localize("info.thermalexpansion.augment." + type));

		int level = getPrimaryLevel(stack);
		list.add(StringHelper.WHITE + StringHelper.localize("info.cofh.level") + " " + StringHelper.ROMAN_NUMERAL[level] + StringHelper.END);

		if (type.equals(TEAugments.DYNAMO_EFFICIENCY)) {
			list.add(StringHelper.BRIGHT_GREEN + "+" + TEAugments.DYNAMO_EFFICIENCY_MOD_SUM[level] / 10 + "% "
					+ StringHelper.localize("info.thermalexpansion.augment.fuelEnergy") + StringHelper.END);
		} else if (type.equals(TEAugments.DYNAMO_OUTPUT)) {
			list.add(StringHelper.BRIGHT_GREEN + "x" + TEAugments.DYNAMO_OUTPUT_MOD[level] + " "
					+ StringHelper.localize("info.thermalexpansion.augment.energyProduced") + StringHelper.END);
			list.add("x" + TEAugments.DYNAMO_OUTPUT_MOD[level] + " " + StringHelper.localize("info.thermalexpansion.augment.fuelUsed") + StringHelper.END);
			list.add(StringHelper.RED + "-" + TEAugments.DYNAMO_OUTPUT_EFFICIENCY_SUM[level] / 10 + "% "
					+ StringHelper.localize("info.thermalexpansion.augment.fuelEnergy") + StringHelper.END);
		} else if (type.equals(TEAugments.MACHINE_SECONDARY)) {
			list.add(StringHelper.BRIGHT_GREEN + "+" + TEAugments.MACHINE_SECONDARY_MOD_SUM[level] + "% "
					+ StringHelper.localize("info.thermalexpansion.augment.secondaryChance") + StringHelper.END);
			addMachineInfo(list, level);
		} else if (type.equals(TEAugments.MACHINE_SPEED)) {
			list.add(StringHelper.BRIGHT_GREEN + "x" + TEAugments.MACHINE_SPEED_PROCESS_MOD[level] + " "
					+ StringHelper.localize("info.thermalexpansion.augment.speed") + StringHelper.END);
			list.add(StringHelper.RED + "x" + TEAugments.MACHINE_SPEED_ENERGY_MOD[level] + " "
					+ StringHelper.localize("info.thermalexpansion.augment.energyUsed") + StringHelper.END);
			list.add(StringHelper.RED + "-" + TEAugments.MACHINE_SPEED_SECONDARY_MOD_SUM[level] + "% "
					+ StringHelper.localize("info.thermalexpansion.augment.secondaryChance") + StringHelper.END);
			addMachineInfo(list, level);
		}
		if (level > 1) {
			list.add(StringHelper.localize("info.thermalexpansion.augment.levels.0"));
			list.add(StringHelper.localize("info.thermalexpansion.augment.levels.1"));
		}
	}

	private void addMachineInfo(List list, int level) {

		list.add(StringHelper.localize("info.thermalexpansion.augment.machine.0") + " " + StringHelper.getRarity(level)
				+ StringHelper.localize("info.thermalexpansion." + ItemBlockMachine.NAMES[level]) + " " + StringHelper.LIGHT_GRAY
				+ StringHelper.localize("info.thermalexpansion.augment.machine.1"));
	}

	public void addAugmentData(int number, String augmentType, int augmentLevel) {

		int index = Integer.valueOf(number);

		if (!augmentMap.containsKey(index)) {
			augmentMap.put(index, new AugmentEntry());
			augmentMap.get(index).primaryType = augmentType;
			augmentMap.get(index).primaryLevel = augmentLevel;
		}
		augmentMap.get(index).augmentTypeInfo.put(augmentType, augmentLevel);
	}

	private String getPrimaryType(ItemStack stack) {

		AugmentEntry entry = augmentMap.get(ItemHelper.getItemDamage(stack));
		if (entry == null) {
			return "";
		}
		return entry.primaryType;
	}

	private int getPrimaryLevel(ItemStack stack) {

		AugmentEntry entry = augmentMap.get(ItemHelper.getItemDamage(stack));
		if (entry == null) {
			return 0;
		}
		return entry.primaryLevel;
	}

	/* IAugmentItem */
	@Override
	public int getAugmentLevel(ItemStack stack, String type) {

		AugmentEntry entry = augmentMap.get(ItemHelper.getItemDamage(stack));
		if (!entry.augmentTypeInfo.containsKey(type)) {
			return 0;
		}
		return entry.augmentTypeInfo.get(type);
	}

	@Override
	public Set<String> getAugmentTypes(ItemStack stack) {

		return augmentMap.get(ItemHelper.getItemDamage(stack)).augmentTypeInfo.keySet();
	}

}
