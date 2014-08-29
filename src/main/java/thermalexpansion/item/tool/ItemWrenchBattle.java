package thermalexpansion.item.tool;

import buildcraft.api.tools.IToolWrench;

import cofh.api.block.IDismantleable;
import cofh.api.item.IToolHammer;
import cofh.asm.relauncher.Strippable;
import cofh.core.item.tool.ItemSwordAdv;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.common.eventhandler.Event.Result;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

@Strippable("buildcraft.api.tools.IToolWrench")
public class ItemWrenchBattle extends ItemSwordAdv implements IToolWrench, IToolHammer {

	public ItemWrenchBattle(ToolMaterial toolMaterial) {

		super(toolMaterial);
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase entity, EntityLivingBase player) {

		stack.damageItem(1, player);
		entity.rotationYaw += 90;
		entity.rotationYaw %= 360;
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
		} else if (!player.isSneaking() && block != null && block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(hitSide))) {
			player.swingItem();
			return ServerHelper.isServerWorld(world);
		}
		return false;
	}

	@Override
	public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {

		return true;
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
