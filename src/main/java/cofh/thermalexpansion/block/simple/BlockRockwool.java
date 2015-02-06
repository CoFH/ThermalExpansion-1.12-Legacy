package cofh.thermalexpansion.block.simple;

import cofh.api.core.IInitializer;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.item.TEItems;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;


public class BlockRockwool extends Block implements IInitializer {

	public BlockRockwool() {

		super(new Material(MapColor.clothColor));
		setHardness(0.8F);
		setResistance(10.0F);
		setStepSound(soundTypeCloth);
		setCreativeTab(ThermalExpansion.tabBlocks);
		setBlockName("thermalexpansion.rockwool");
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {

		for (int i = 0; i < 16; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public int damageDropped(int i) {

		return i;
	}

	@Override
	public IIcon getIcon(int side, int metadata) {

		return Blocks.wool.getIcon(side, metadata);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		return true;
	}

	@Override
	public boolean initialize() {

		rockWool = new ItemStack(this);

		GameRegistry.registerCustomItemStack("clothRock", rockWool);

		OreDictionary.registerOre("blockClothRock", new ItemStack(this, 1, OreDictionary.WILDCARD_VALUE));

		return true;
	}

	@Override
	public boolean postInit() {

		GameRegistry.addSmelting(TEItems.slag, new ItemStack(this, 1, 8), 0.0F);
		for (int i = 0; i < 16; i++) {
			GameRegistry.addRecipe(new ItemStack(this, 8, i), new Object[] { "###", "#D#", "###", '#', this, 'D', new ItemStack(Items.dye, 1, 15 - i) });
		}
		return true;
	}

	public static ItemStack rockWool;

}
