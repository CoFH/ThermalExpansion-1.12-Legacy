package thermalexpansion.plugins.mfr;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class DrinkHandlerCoal implements ILiquidDrinkHandler {

	public static DrinkHandlerCoal instance = new DrinkHandlerCoal();

	@Override
	public void onDrink(EntityPlayer player) {

		player.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 60 * 20, 0));
	}

}
