package thermalexpansion.plugins.mfr;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import powercrystals.minefactoryreloaded.api.ILiquidDrinkHandler;

public class DrinkHandlerGlowstone implements ILiquidDrinkHandler {

	public static DrinkHandlerGlowstone instance = new DrinkHandlerGlowstone();

	@Override
	public void onDrink(EntityLivingBase player) {

		player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 60 * 20, 2));
		player.addPotionEffect(new PotionEffect(Potion.jump.id, 60 * 20, 2));
		player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 60 * 20, 0));
	}

}
