package cofh.thermalexpansion.item.tool;

import buildcraft.api.tools.IToolWrench;

import cofh.api.block.IDismantleable;
import cofh.api.item.IToolHammer;
import cofh.asm.relauncher.Strippable;
import cofh.core.item.ItemBase;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.eventhandler.Event.Result;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

@Strippable("buildcraft.api.tools.IToolWrench")
public class ItemWrench extends ItemBase implements IToolWrench, IToolHammer {

	public ItemWrench() {

		super("thermalexpansion");
		setMaxStackSize(1);
		setCreativeTab(ThermalExpansion.tabTools);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		list.add(StringHelper.getFlavorText("info.thermalexpansion.tool.wrench.0"));
		list.add(StringHelper.getFlavorText("info.thermalexpansion.tool.wrench.1"));
		list.add(StringHelper.getFlavorText("info.thermalexpansion.tool.wrench.2"));
	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		return true;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		Block block = world.getBlock(x, y, z);

		if (block == null) {
			return false;
		}
		PlayerInteractEvent event = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, x, y, z, hitSide, world);
		if (MinecraftForge.EVENT_BUS.post(event) || event.getResult() == Result.DENY || event.useBlock == Result.DENY || event.useItem == Result.DENY) {
			return false;
		}
		if (ServerHelper.isServerWorld(world) && player.isSneaking() && block instanceof IDismantleable
				&& ((IDismantleable) block).canDismantle(player, world, x, y, z)) {
			((IDismantleable) block).dismantleBlock(player, world, x, y, z, false);
			return true;
		}
		if (BlockHelper.canRotate(block)) {
			if (player.isSneaking()) {
				world.setBlockMetadataWithNotify(x, y, z, BlockHelper.rotateVanillaBlockAlt(world, block, x, y, z), 3);
				world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, block.stepSound.getBreakSound(), 1.0F, 0.6F);
			} else {
				world.setBlockMetadataWithNotify(x, y, z, BlockHelper.rotateVanillaBlock(world, block, x, y, z), 3);
				world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, block.stepSound.getBreakSound(), 1.0F, 0.8F);
			}
			return ServerHelper.isServerWorld(world);
		} else if (!player.isSneaking() && block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(hitSide))) {
			player.swingItem();
			return ServerHelper.isServerWorld(world);
		}
		return false;
	}

	@Override
	public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {

		return true;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Multimap getItemAttributeModifiers() {

		Multimap multimap = HashMultimap.create();
		multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Tool modifier", 1, 0));
		return multimap;
	}

	/* IToolHammer */
	@Override
	public boolean isUsable(ItemStack item, EntityLivingBase user, int x, int y, int z) {

		return true;
	}

	@Override
	public void toolUsed(ItemStack item, EntityLivingBase user, int x, int y, int z) {

	}

	/* IToolWrench */
	@Override
	public boolean canWrench(EntityPlayer player, int x, int y, int z) {

		return true;
	}

	@Override
	public void wrenchUsed(EntityPlayer player, int x, int y, int z) {

	}

}
