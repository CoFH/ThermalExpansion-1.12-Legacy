package cofh.thermalexpansion.item.tool;

import cofh.api.item.IMultiModeItem;
import cofh.core.util.KeyBindingMultiMode;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

public abstract class ItemToolBase extends Item implements IMultiModeItem {

	public String modName = "thermalexpansion";
	public final String itemName;

	public ItemToolBase(String name) {

		super();
		this.itemName = name;
		setMaxStackSize(1);
		setCreativeTab(ThermalExpansion.tabTools);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		addInformationDelegate(stack, player, list, check);

		if (getNumModes(stack) > 0) {
			list.add(StringHelper.YELLOW + StringHelper.ITALIC + StringHelper.localize("info.cofh.press") + " "
					+ Keyboard.getKeyName(KeyBindingMultiMode.instance.getKey()) + " " + StringHelper.localize("info.cofh.modeChange") + StringHelper.END);
		}
	}

	protected void addInformationDelegate(ItemStack stack, EntityPlayer player, List<String> list, boolean check) {

		list.add(StringHelper.getInfoText("info.thermalexpansion.tool." + itemName));
	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		return false;
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {

		return SecurityHelper.isSecure(stack);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return new StringBuilder().append(getUnlocalizedName()).append('.').append(itemName).toString();
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack stack) {

		if (SecurityHelper.isSecure(stack)) {
			location.invulnerable = true;
			location.isImmuneToFire = true;
			((EntityItem) location).lifespan = Integer.MAX_VALUE;
		}
		return null;
	}

	@Override
	public Item setUnlocalizedName(String name) {

		GameRegistry.registerItem(this, name);
		name = modName + "." + name;
		return super.setUnlocalizedName(name);
	}

	public Item setUnlocalizedName(String textureName, String registrationName) {

		GameRegistry.registerItem(this, registrationName);
		textureName = modName + "." + textureName;
		return super.setUnlocalizedName(textureName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir) {

		this.itemIcon = ir.registerIcon(modName + ":" + getUnlocalizedName().replace("item." + modName + ".", "") + "/" + StringHelper.titleCase(itemName));
	}

	/* IMultiModeItem */
	@Override
	public int getMode(ItemStack stack) {

		return stack.stackTagCompound == null ? 0 : stack.stackTagCompound.getInteger("Mode");
	}

	@Override
	public boolean setMode(ItemStack stack, int mode) {

		if (stack.stackTagCompound == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.stackTagCompound.setInteger("Mode", mode);
		return false;
	}

	@Override
	public boolean incrMode(ItemStack stack) {

		if (stack.stackTagCompound == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		int curMode = getMode(stack);
		curMode++;
		if (curMode >= getNumModes(stack)) {
			curMode = 0;
		}
		stack.stackTagCompound.setInteger("Mode", curMode);
		return true;
	}

	@Override
	public boolean decrMode(ItemStack stack) {

		if (stack.stackTagCompound == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		int curMode = getMode(stack);
		curMode--;
		if (curMode <= 0) {
			curMode = getNumModes(stack) - 1;
		}
		stack.stackTagCompound.setInteger("Mode", curMode);
		return true;
	}

	@Override
	public int getNumModes(ItemStack stack) {

		return 0;
	}

	@Override
	public void onModeChange(EntityPlayer player, ItemStack stack) {

	}

}
