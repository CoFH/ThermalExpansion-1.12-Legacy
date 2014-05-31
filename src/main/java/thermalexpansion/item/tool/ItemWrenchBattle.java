package thermalexpansion.item.tool;

import buildcraft.api.tools.IToolWrench;

import cofh.api.block.IDismantleable;
import cofh.audio.SoundBase;
import cofh.item.tool.ItemSwordAdv;
import cofh.util.BlockHelper;
import cofh.util.ServerHelper;
import cpw.mods.fml.client.FMLClientHandler;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemWrenchBattle extends ItemSwordAdv implements IToolWrench {

	public ItemWrenchBattle(Item.ToolMaterial toolMaterial) {

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

		if (ServerHelper.isServerWorld(world) && player.isSneaking() && block instanceof IDismantleable
				&& ((IDismantleable) block).canDismantle(player, world, x, y, z)) {
			((IDismantleable) block).dismantleBlock(player, world, x, y, z, false);
			return true;
		}
		if (BlockHelper.canRotate(block)) {
			int bMeta = world.getBlockMetadata(x, y, z);

			if (player.isSneaking()) {
				world.setBlockMetadataWithNotify(x, y, z, BlockHelper.rotateVanillaBlockAlt(world, block, x, y, z), 3);

				if (ServerHelper.isClientWorld(world)) {
					String soundName = block.stepSound.getBreakSound();
					FMLClientHandler.instance().getClient().getSoundHandler().playSound(new SoundBase(soundName, 1.0F, 0.6F));
				}
			} else {
				world.setBlockMetadataWithNotify(x, y, z, BlockHelper.rotateVanillaBlock(world, block, x, y, z), 3);

				if (ServerHelper.isClientWorld(world)) {
					String soundName = block.stepSound.getBreakSound();
					FMLClientHandler.instance().getClient().getSoundHandler().playSound(new SoundBase(soundName, 1.0F, 0.8F));
				}
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

	/* IToolWrench */
	@Override
	public boolean canWrench(EntityPlayer player, int x, int y, int z) {

		return true;
	}

	@Override
	public void wrenchUsed(EntityPlayer player, int x, int y, int z) {

	}

}
