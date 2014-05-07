package thermalexpansion.plugins.mfr;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

public class DrinkHandlerCryotheum implements ILiquidDrinkHandler {

	public static DrinkHandlerCryotheum instance = new DrinkHandlerCryotheum();

	@Override
	public void onDrink(EntityPlayer player) {

		player.attackEntityFrom(new InternalCryotheumDamage(), 15);
		player.extinguish();
		player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 480 * 20, 0));
	}

	protected class InternalCryotheumDamage extends DamageSource {

		public InternalCryotheumDamage() {

			super(DamageSource.magic.damageType);
			this.setDamageBypassesArmor();
			this.setMagicDamage();
			this.setDifficultyScaled();
		}
	}

}
