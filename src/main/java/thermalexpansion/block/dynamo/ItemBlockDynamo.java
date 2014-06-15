package thermalexpansion.block.dynamo;

import cofh.util.StringHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockDynamo extends ItemBlock {

	public ItemBlockDynamo(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	public String getItemStackDisplayName(ItemStack item) {

		return StringHelper.localize(getUnlocalizedName(item));
	}

	@Override
	public String getUnlocalizedName(ItemStack item) {

		return "tile.thermalexpansion.dynamo." + BlockDynamo.NAMES[item.getItemDamage()] + ".name";
	}

	@Override
	public int getMetadata(int i) {

		return i;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForInfo());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		list.add(StringHelper.localize("info.thermalexpansion.dynamo.generate"));

		switch (BlockDynamo.Types.values()[stack.getItemDamage()]) {
		case STEAM:
			list.add(StringHelper.localize("info.thermalexpansion.dynamo.steam1"));
			list.add(StringHelper.getInfoText("info.thermalexpansion.dynamo.steam2"));
			break;
		case MAGMATIC:
			list.add(StringHelper.localize("info.thermalexpansion.dynamo.magmatic"));
			break;
		case COMPRESSION:
			list.add(StringHelper.localize("info.thermalexpansion.dynamo.compression"));
			break;
		case REACTANT:
			list.add(StringHelper.localize("info.thermalexpansion.dynamo.reactant"));
			break;
		case ENERVATION:
			list.add(StringHelper.localize("info.thermalexpansion.dynamo.infusion"));
			break;
		}
	}

}
