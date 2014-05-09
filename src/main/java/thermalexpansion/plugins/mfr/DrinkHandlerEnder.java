package thermalexpansion.plugins.mfr;

import cofh.util.CoreUtils;
import cofh.util.ServerHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class DrinkHandlerEnder implements ILiquidDrinkHandler {

	public static DrinkHandlerEnder instance = new DrinkHandlerEnder();

	@Override
	public void onDrink(EntityPlayer player) {

		if (ServerHelper.isClientWorld(player.worldObj)) {
			return;
		}
		int x2 = (int) (player.posX - MFRPlugin.strawEnderRange + player.worldObj.rand.nextInt(MFRPlugin.strawEnderRange * 2));
		int y2 = (int) (player.posY + player.worldObj.rand.nextInt(8));
		int z2 = (int) (player.posZ - MFRPlugin.strawEnderRange + player.worldObj.rand.nextInt(MFRPlugin.strawEnderRange * 2));

		if (!player.worldObj.getBlock(x2, y2, z2).getMaterial().isSolid()) {
			CoreUtils.teleportEntityTo(player, x2, y2, z2);
			player.playSound("portal.trigger", 1.0F, 1.0F);
			player.addPotionEffect(new PotionEffect(Potion.confusion.id, 15 * 20, 0));
			player.addPotionEffect(new PotionEffect(Potion.invisibility.id, 15 * 20, 0));
		} else {
			for (int i = 0; i < 1 + player.worldObj.rand.nextInt(3); i++) {
				CoreUtils.dropItemStackIntoWorld(new ItemStack(Items.ender_pearl), player.worldObj, player.posX, player.posY, player.posZ);
			}
		}
	}

}
