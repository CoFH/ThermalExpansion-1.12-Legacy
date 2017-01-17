package cofh.thermalexpansion.block.simple;

import codechicken.lib.item.ItemStackRegistry;
import cofh.api.core.IInitializer;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.item.TEItems;
import net.minecraft.block.BlockColored;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class BlockRockwool extends BlockColored implements IInitializer {

	public BlockRockwool() {

		super(new Material(MapColor.CLOTH));
		setHardness(0.8F);
		setResistance(10.0F);
		setSoundType(SoundType.CLOTH);
		setCreativeTab(ThermalExpansion.tabBlocks);
		setUnlocalizedName("thermalexpansion.rockwool");
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		return true;
	}

	@Override
	public boolean initialize() {

		rockWool = new ItemStack(this);

		ItemStackRegistry.registerCustomItemStack("clothRock", rockWool);
		OreDictionary.registerOre("blockClothRock", new ItemStack(this, 1, OreDictionary.WILDCARD_VALUE));

		return true;
	}

	@Override
	public boolean postInit() {

		GameRegistry.addSmelting(TEItems.slag, new ItemStack(this, 1, DEFAULT_META), 0.0F);
		for (int i = 0; i < 16; i++) {
			GameRegistry.addRecipe(new ItemStack(this, 8, i), "###", "#D#", "###", '#', this, 'D', new ItemStack(Items.DYE, 1, 15 - i));
		}
		return true;
	}

	public static int DEFAULT_META = 8;

	public static ItemStack rockWool;

}
