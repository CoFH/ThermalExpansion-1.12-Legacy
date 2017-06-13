package cofh.thermalexpansion.item;

import cofh.api.item.IInventoryContainerItem;
import cofh.core.init.CoreEnchantments;
import cofh.core.item.IEnchantableItem;
import cofh.core.item.ItemMulti;
import cofh.core.util.core.IInitializer;
import cofh.lib.util.helpers.ItemHelper;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

public class ItemSatchel extends ItemMulti implements IInitializer, IInventoryContainerItem, IEnchantableItem {

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {

		return true;
	}

	@Override
	public int getItemEnchantability() {

		return 10;
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		ModelLoader.setCustomMeshDefinition(this, new SatchelMeshDefinition());

		for (Map.Entry<Integer, ItemEntry> entry : itemMap.entrySet()) {
			ModelResourceLocation texture = new ModelResourceLocation(modName + ":" + name + "_" + entry.getValue().name, "inventory");
			textureMap.put(entry.getKey(), texture);
			ModelBakery.registerItemVariants(this, texture);
		}
	}

	/* ITEM MESH DEFINITION */
	@SideOnly (Side.CLIENT)
	public class SatchelMeshDefinition implements ItemMeshDefinition {

		public ModelResourceLocation getModelLocation(ItemStack stack) {

			return textureMap.get(ItemHelper.getItemDamage(stack));
		}
	}

	/* IInventoryContainerItem */
	@Override
	public int getSizeInventory(ItemStack container) {

		return 0;
	}

	/* IEnchantableItem */
	@Override
	public boolean canEnchant(ItemStack stack, Enchantment enchantment) {

		if (!satchelMap.containsKey(ItemHelper.getItemDamage(stack))) {
			return false;
		}
		return satchelMap.get(ItemHelper.getItemDamage(stack)).enchantable && enchantment == CoreEnchantments.holding;
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		return true;
	}

	@Override
	public boolean initialize() {

		return true;
	}

	@Override
	public boolean postInit() {

		return true;
	}

	/* ENTRY */
	public class SatchelEntry {

		public final String name;
		public final int send;
		public final int recv;
		public final int capacity;
		public final boolean enchantable;

		SatchelEntry(String name, int send, int recv, int capacity, boolean enchantable) {

			this.name = name;
			this.send = send;
			this.recv = recv;
			this.capacity = capacity;
			this.enchantable = enchantable;
		}
	}

	private void addSatchelEntry(int metadata, String name, int send, int recv, int capacity, boolean enchantable) {

		satchelMap.put(metadata, new SatchelEntry(name, send, recv, capacity, enchantable));
	}

	private ItemStack addSatchelItem(int metadata, String name, int send, int recv, int capacity, EnumRarity rarity, boolean enchantable) {

		addSatchelEntry(metadata, name, send, recv, capacity, enchantable);
		return addItem(metadata, name, rarity);
	}

	private ItemStack addSatchelItem(int metadata, String name, int send, int recv, int capacity, EnumRarity rarity) {

		addSatchelEntry(metadata, name, send, recv, capacity, true);
		return addItem(metadata, name, rarity);
	}

	private TIntObjectHashMap<SatchelEntry> satchelMap = new TIntObjectHashMap<>();
	private TIntObjectHashMap<ModelResourceLocation> textureMap = new TIntObjectHashMap<>();

	/* REFERENCES */
	public static ItemStack satchelBasic;
	public static ItemStack satchelHardened;
	public static ItemStack satchelReinforced;
	public static ItemStack satchelSignalum;
	public static ItemStack satchelResonant;

	public static ItemStack satchelCreative;

}
