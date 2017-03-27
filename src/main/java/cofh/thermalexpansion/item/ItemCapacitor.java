package cofh.thermalexpansion.item;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.item.IMultiModeItem;
import cofh.core.init.CoreEnchantments;
import cofh.core.item.IEnchantableItem;
import cofh.core.item.ItemMulti;
import cofh.core.util.CoreUtils;
import cofh.core.util.core.IInitializer;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.storage.TileCell;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemCapacitor extends ItemMulti implements IInitializer, IMultiModeItem, IEnergyContainerItem, IEnchantableItem {

	public ItemCapacitor() {

		super("thermalexpansion");

		setMaxStackSize(1);
		setUnlocalizedName("capacitor");
		setCreativeTab(ThermalExpansion.tabItems);
	}

	public int getSend(ItemStack stack) {

		if (!capacitorMap.containsKey(ItemHelper.getItemDamage(stack))) {
			return 0;
		}
		return capacitorMap.get(ItemHelper.getItemDamage(stack)).send;
	}

	public int getRecv(ItemStack stack) {

		if (!capacitorMap.containsKey(ItemHelper.getItemDamage(stack))) {
			return 0;
		}
		return capacitorMap.get(ItemHelper.getItemDamage(stack)).recv;
	}

	public int getCapacity(ItemStack stack) {

		if (!capacitorMap.containsKey(ItemHelper.getItemDamage(stack))) {
			return 0;
		}
		int capacity = capacitorMap.get(ItemHelper.getItemDamage(stack)).capacity;
		int enchant = EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, stack);

		return capacity + capacity * enchant / 2;
	}

	public int getBaseCapacity(int metadata) {

		if (!capacitorMap.containsKey(metadata)) {
			return 0;
		}
		return capacitorMap.get(metadata).capacity;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void getSubItems(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {

		for (int metadata : itemList) {
			list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, metadata), 0));
			list.add(EnergyHelper.setDefaultEnergyTag(new ItemStack(item, 1, metadata), getBaseCapacity(metadata)));
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isCurrentItem) {

		if (CoreUtils.isFakePlayer(entity)) {
			return;
		}
		if (slot > 8 || !isActive(stack) || isCurrentItem) {
			return;
		}
		EntityPlayer player = (EntityPlayer) entity;
		Iterable<ItemStack> equipment;

		switch (getMode(stack)) {
			case 0:
				equipment = player.getHeldEquipment();
				break;
			case 1:
				equipment = player.getArmorInventoryList();
				break;
			default:
				equipment = player.getEquipmentAndArmor();
		}
		for (ItemStack equipmentStack : equipment) {
			if (EnergyHelper.isEnergyContainerItem(equipmentStack)) {
				extractEnergy(stack, ((IEnergyContainerItem) equipmentStack.getItem()).receiveEnergy(equipmentStack, Math.min(getEnergyStored(stack), getSend(stack)), false), false);
			}
		}
	}

	@Override
	public boolean isDamaged(ItemStack stack) {

		return true;
	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public boolean isItemTool(ItemStack stack) {

		return true;
	}

	@Override
	public int getItemEnchantability() {

		return 10;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(stack, 0);
		}
		return 1D - ((double) stack.getTagCompound().getInteger("Energy") / (double) getMaxEnergyStored(stack));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {

		if (CoreUtils.isFakePlayer(player)) {
			return new ActionResult<>(EnumActionResult.FAIL, itemStack);
		}
		if (player.isSneaking()) {
			if (setActiveState(itemStack, !isActive(itemStack))) {
				if (isActive(itemStack)) {
					player.worldObj.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, SoundCategory.PLAYERS, 0.2F, 0.8F);
				} else {
					player.worldObj.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, SoundCategory.PLAYERS, 0.2F, 0.5F);
				}
			}
		}
		player.swingArm(hand);
		return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		return EnumActionResult.FAIL;
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

	/* IMultiModeItem */
	@Override
	public int getMode(ItemStack stack) {

		return !stack.hasTagCompound() ? 0 : stack.getTagCompound().getInteger("Mode");
	}

	@Override
	public boolean setMode(ItemStack stack, int mode) {

		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setInteger("Mode", mode);
		return false;
	}

	@Override
	public boolean incrMode(ItemStack stack) {

		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		int curMode = getMode(stack);
		curMode++;
		if (curMode >= getNumModes(stack)) {
			curMode = 0;
		}
		stack.getTagCompound().setInteger("Mode", curMode);
		return true;
	}

	@Override
	public boolean decrMode(ItemStack stack) {

		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		int curMode = getMode(stack);
		curMode--;
		if (curMode <= 0) {
			curMode = getNumModes(stack) - 1;
		}
		stack.getTagCompound().setInteger("Mode", curMode);
		return true;
	}

	@Override
	public int getNumModes(ItemStack stack) {

		return 3;
	}

	@Override
	public void onModeChange(EntityPlayer player, ItemStack stack) {

	}

	/* IEnergyContainerItem */
	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {

		int metadata = ItemHelper.getItemDamage(container);

		if (container.getTagCompound() == null) {
			EnergyHelper.setDefaultEnergyTag(container, 0);
		}
		int stored = container.getTagCompound().getInteger("Energy");
		int receive = Math.min(maxReceive, Math.min(getSend(container) - stored, getRecv(container)));

		if (!simulate) {
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
		int extract = Math.min(maxExtract, Math.min(stored, getSend(container)));

		if (!simulate) {
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

		return getCapacity(container);
	}

	/* IEnchantableItem */
	@Override
	public boolean canEnchant(ItemStack stack, Enchantment enchantment) {

		if (!capacitorMap.containsKey(ItemHelper.getItemDamage(stack))) {
			return false;
		}
		return capacitorMap.get(ItemHelper.getItemDamage(stack)).enchantable && enchantment == CoreEnchantments.holding;
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		capacitorBasic = addCapacitorItem(0, "standard0", TileCell.SEND[0] / 2, TileCell.RECV[0], TileCell.CAPACITY[0] / 2, EnumRarity.COMMON);
		capacitorHardened = addCapacitorItem(1, "standard1", TileCell.SEND[1] / 2, TileCell.RECV[1], TileCell.CAPACITY[1] / 2, EnumRarity.COMMON);
		capacitorReinforced = addCapacitorItem(2, "standard2", TileCell.SEND[2] / 2, TileCell.RECV[2], TileCell.CAPACITY[2] / 2, EnumRarity.UNCOMMON);
		capacitorSignalum = addCapacitorItem(3, "standard3", TileCell.SEND[3] / 2, TileCell.RECV[3], TileCell.CAPACITY[3] / 2, EnumRarity.UNCOMMON);
		capacitorResonant = addCapacitorItem(4, "standard4", TileCell.SEND[4] / 2, TileCell.RECV[4], TileCell.CAPACITY[4] / 2, EnumRarity.RARE);

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
	public class CapacitorEntry {

		public final String name;
		public final int send;
		public final int recv;
		public final int capacity;
		public final boolean enchantable;

		CapacitorEntry(String name, int send, int recv, int capacity, boolean enchantable) {

			this.name = name;
			this.send = send;
			this.recv = recv;
			this.capacity = capacity;
			this.enchantable = enchantable;
		}
	}

	private void addCapacitorEntry(int metadata, String name, int send, int recv, int capacity, boolean enchantable) {

		capacitorMap.put(metadata, new CapacitorEntry(name, send, recv, capacity, enchantable));
	}

	private ItemStack addCapacitorItem(int metadata, String name, int send, int recv, int capacity, EnumRarity rarity, boolean enchantable) {

		addCapacitorEntry(metadata, name, send, recv, capacity, enchantable);
		return addItem(metadata, name, rarity);
	}

	private ItemStack addCapacitorItem(int metadata, String name, int send, int recv, int capacity, EnumRarity rarity) {

		addCapacitorEntry(metadata, name, send, recv, capacity, true);
		return addItem(metadata, name, rarity);
	}

	private TIntObjectHashMap<CapacitorEntry> capacitorMap = new TIntObjectHashMap<>();

	/* REFERENCES */
	public static ItemStack capacitorBasic;
	public static ItemStack capacitorHardened;
	public static ItemStack capacitorReinforced;
	public static ItemStack capacitorSignalum;
	public static ItemStack capacitorResonant;

	public static ItemStack capacitorCreative;
	public static ItemStack capacitorPotato;

}
