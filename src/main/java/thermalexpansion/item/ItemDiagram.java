package thermalexpansion.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import cofh.item.ItemBase;
import cofh.util.StringHelper;

public class ItemDiagram extends ItemBase {

	public ItemDiagram() {

		super("thermalexpansion");
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {

		return StringHelper.localize(getUnlocalizedName(stack)) + SchematicHelper.getOutputName(stack);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		return SchematicHelper.getOutputName(stack).equalsIgnoreCase("") ? EnumRarity.common : EnumRarity.uncommon;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		SchematicHelper.addSchematicInformation(list, stack);
	}

}
