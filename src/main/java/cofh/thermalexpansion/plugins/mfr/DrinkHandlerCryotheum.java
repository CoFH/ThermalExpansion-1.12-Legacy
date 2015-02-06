package cofh.thermalexpansion.plugins.mfr;

import cofh.lib.util.helpers.DamageHelper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

import powercrystals.minefactoryreloaded.api.ILiquidDrinkHandler;

public class DrinkHandlerCryotheum implements ILiquidDrinkHandler {

	public static DrinkHandlerCryotheum instance = new DrinkHandlerCryotheum();

	@Override
	public void onDrink(EntityLivingBase player) {

		player.attackEntityFrom(new InternalCryotheumDamage(), 15);
		player.extinguish();
		player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 480 * 20, 0));
	}

	protected class InternalCryotheumDamage extends DamageSource {

		public InternalCryotheumDamage() {

			super(DamageHelper.cryotheum.damageType);
			this.setDamageBypassesArmor();
			this.setMagicDamage();
			this.setDifficultyScaled();
		}
	}

}
