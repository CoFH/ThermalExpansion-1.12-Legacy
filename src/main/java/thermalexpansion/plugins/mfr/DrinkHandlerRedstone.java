package thermalexpansion.plugins.mfr;

import cofh.util.CoreUtils;

import java.util.ArrayList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

import powercrystals.minefactoryreloaded.api.ILiquidDrinkHandler;

public class DrinkHandlerRedstone implements ILiquidDrinkHandler {

	public static DrinkHandlerRedstone instance = new DrinkHandlerRedstone();

	@Override
	public void onDrink(EntityLivingBase player) {

		ArrayList<PotionEffect> effects = new ArrayList<PotionEffect>(player.getActivePotionEffects());
		for (PotionEffect effect : effects) {
			amplifyEffect(player, effect);
		}
		player.setFire(2);

		CoreUtils.doFakeLightningBolt(player.worldObj, player.posX, player.posY, player.posZ);
		CoreUtils.doFakeExplosion(player.worldObj, player.posX, player.posY, player.posZ, false);
	}

	boolean amplifyEffect(EntityLivingBase player, PotionEffect effect) {

		if (effect == null || effect.getIsAmbient()) {
			return false;
		}
		int id = effect.getPotionID();
		int amplifier = Math.min(effect.getAmplifier() + 1, 3);
		int duration = Math.min(effect.getDuration() * 2, 9600);

		player.addPotionEffect(new PotionEffect(id, duration, amplifier));
		return true;
	}
}
