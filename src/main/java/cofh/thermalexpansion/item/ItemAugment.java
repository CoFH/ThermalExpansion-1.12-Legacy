package cofh.thermalexpansion.item;

import cofh.api.item.IAugmentItem;
import cofh.core.item.ItemBase;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.ItemBlockMachine;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class ItemAugment extends ItemBase implements IAugmentItem {

	public class AugmentEntry {

		public String primaryType = "";
		public int primaryLevel = 0;
		public int numInfo = 1;
		public TObjectIntHashMap<String> augmentTypeInfo = new TObjectIntHashMap<String>();
	}

	TIntObjectHashMap<AugmentEntry> augmentMap = new TIntObjectHashMap<AugmentEntry>();

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
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean check) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		boolean augmentChain = true;
		String type = getPrimaryType(stack);
		list.add(StringHelper.localize("info.thermalexpansion.augment." + type));

		int level = getPrimaryLevel(stack);
		list.add(StringHelper.WHITE + StringHelper.localize("info.cofh.level") + " " + StringHelper.ROMAN_NUMERAL[level] + StringHelper.END);

		int numInfo = getNumInfo(stack);
		for (int i = 0; i < numInfo; i++) {
			list.add(StringHelper.BRIGHT_GREEN + StringHelper.localize("info.thermalexpansion.augment." + type + "." + i) + StringHelper.END);
		}

		/* DYNAMO THROTTLE */
		if (type.equals(TEAugments.DYNAMO_THROTTLE)) {
			augmentChain = false;
			list.add(StringHelper.getNoticeText("info.thermalexpansion.augment.requireRS"));
		}
		/* DYNAMO EFFICIENCY */
		else if (type.equals(TEAugments.DYNAMO_EFFICIENCY)) {
			list.add(StringHelper.BRIGHT_GREEN + "+" + TEAugments.DYNAMO_EFFICIENCY_MOD_SUM[level] + "% "
					+ StringHelper.localize("info.thermalexpansion.augment.fuelEnergy") + StringHelper.END);

		}
		/* DYNAMO OUTPUT */
		else if (type.equals(TEAugments.DYNAMO_OUTPUT)) {
			list.add(StringHelper.BRIGHT_GREEN + "x" + TEAugments.DYNAMO_OUTPUT_MOD[level] + " "
					+ StringHelper.localize("info.thermalexpansion.augment.energyProduced") + StringHelper.END);
			list.add("x" + TEAugments.DYNAMO_OUTPUT_MOD[level] + " " + StringHelper.localize("info.thermalexpansion.augment.fuelConsumed") + StringHelper.END);
			list.add(StringHelper.RED + "-" + TEAugments.DYNAMO_OUTPUT_EFFICIENCY_SUM[level] + "% "
					+ StringHelper.localize("info.thermalexpansion.augment.fuelEnergy") + StringHelper.END);

		}
		/* MACHINE SECONDARY */
		else if (type.equals(TEAugments.MACHINE_SECONDARY)) {
			list.add(StringHelper.BRIGHT_GREEN + "+" + TEAugments.MACHINE_SECONDARY_MOD_TOOLTIP[level] + "% "
					+ StringHelper.localize("info.thermalexpansion.augment.secondaryChance") + StringHelper.END);
			addMachineInfo(list, level);

		}
		/* MACHINE SPEED */
		else if (type.equals(TEAugments.MACHINE_SPEED)) {
			list.add(StringHelper.BRIGHT_GREEN + "x" + TEAugments.MACHINE_SPEED_PROCESS_MOD[level] + " "
					+ StringHelper.localize("info.thermalexpansion.augment.speed") + StringHelper.END);
			list.add(StringHelper.RED + "+" + TEAugments.MACHINE_SPEED_ENERGY_MOD_TOOLTIP[level] + "% "
					+ StringHelper.localize("info.thermalexpansion.augment.energyUsed") + StringHelper.END);
			list.add(StringHelper.YELLOW + "(x" + TEAugments.MACHINE_SPEED_ENERGY_MOD[level] + " RF/t)" + StringHelper.END);
			// list.add(StringHelper.RED + "-" + TEAugments.MACHINE_SPEED_SECONDARY_MOD_TOOLTIP[level] + "% " TODO: May bring this back, not sure.
			// + StringHelper.localize("info.thermalexpansion.augment.secondaryChance") + StringHelper.END);
			addMachineInfo(list, level);

		}
		/* MACHINE - FURNACE */
		else if (type.equals(TEAugments.MACHINE_FURNACE_FOOD)) {
			list.add(StringHelper.BRIGHT_GREEN + "-50% " + StringHelper.localize("info.thermalexpansion.augment.energyUsed"));
			list.add(StringHelper.RED + StringHelper.localize("info.thermalexpansion.augment.machineFurnaceFood.1") + StringHelper.END);

		}
		/* MACHINE - EXTRUDER */
		else if (type.equals(TEAugments.MACHINE_EXTRUDER_BOOST)) {
			list.add(StringHelper.BRIGHT_GREEN + StringHelper.localize("info.thermalexpansion.augment.upTo") + " "
					+ TEAugments.MACHINE_EXTRUDER_PROCESS_MOD[0][level] + " " + Blocks.COBBLESTONE.getLocalizedName() + " "
					+ StringHelper.localize("info.thermalexpansion.augment.perOperation") + StringHelper.END);
			list.add(StringHelper.BRIGHT_GREEN + StringHelper.localize("info.thermalexpansion.augment.upTo") + " "
					+ TEAugments.MACHINE_EXTRUDER_PROCESS_MOD[1][level] + " " + Blocks.STONE.getLocalizedName() + " "
					+ StringHelper.localize("info.thermalexpansion.augment.perOperation") + StringHelper.END);
			list.add(StringHelper.BRIGHT_GREEN + StringHelper.localize("info.thermalexpansion.augment.upTo") + " "
					+ TEAugments.MACHINE_EXTRUDER_PROCESS_MOD[2][level] + " " + Blocks.OBSIDIAN.getLocalizedName() + " "
					+ StringHelper.localize("info.thermalexpansion.augment.perOperation") + StringHelper.END);
			list.add(StringHelper.BRIGHT_GREEN + "-" + (1000 - TEAugments.MACHINE_EXTRUDER_WATER_MOD[level]) / 10D + "% "
					+ StringHelper.localize("info.thermalexpansion.augment.waterConsumed") + StringHelper.END);
			addMachineInfo(list, level);
		}
		if (level > 1 && augmentChain) {
			list.add(StringHelper.getNoticeText("info.thermalexpansion.augment.levels.0"));
			list.add(StringHelper.getNoticeText("info.thermalexpansion.augment.levels.1"));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addMachineInfo(List list, int level) {

		list.add(StringHelper.localize("info.thermalexpansion.augment.machine.0") + " " + getRarity(level)
				+ StringHelper.localize("info.thermalexpansion." + ItemBlockMachine.NAMES[level]) + " " + StringHelper.LIGHT_GRAY
				+ StringHelper.localize("info.thermalexpansion.augment.machine.1"));
	}

	public void addAugmentData(int number, String augmentType, int augmentLevel) {

		addAugmentData(number, augmentType, augmentLevel, 1);
	}

	public void addAugmentData(int number, String augmentType, int augmentLevel, int numInfo) {

		int index = number;

		if (!augmentMap.containsKey(index)) {
			augmentMap.put(index, new AugmentEntry());
			augmentMap.get(index).primaryType = augmentType;
			augmentMap.get(index).primaryLevel = augmentLevel;
			augmentMap.get(index).numInfo = numInfo;
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

	private int getNumInfo(ItemStack stack) {

		AugmentEntry entry = augmentMap.get(ItemHelper.getItemDamage(stack));
		if (entry == null) {
			return 0;
		}
		return entry.numInfo;
	}

	public String getRarity(int level) {

		switch (level) {
		case 2:
			return StringHelper.YELLOW;
		case 3:
			return StringHelper.BRIGHT_BLUE;
		default:
			return StringHelper.WHITE;
		}
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
