package thermalexpansion.util;

import net.minecraft.util.DamageSource;

public class DamageSources {

	private DamageSources() {

	}

	public static class DamageSourcePyrotheum extends DamageSource {

		protected DamageSourcePyrotheum() {

			super("pyrotheum");
			this.setDamageBypassesArmor();
			this.isFireDamage();
		}
	}

	public static class DamageSourceCryotheum extends DamageSource {

		protected DamageSourceCryotheum() {

			super("cryotheum");
			this.setDamageBypassesArmor();
		}
	}

	public static class DamageSourceMana extends DamageSource {

		protected DamageSourceMana() {

			super("mana");
			this.setDamageBypassesArmor();
			this.isMagicDamage();
		}
	}

	public static final DamageSourcePyrotheum pyrotheum = new DamageSourcePyrotheum();
	public static final DamageSourceCryotheum cryotheum = new DamageSourceCryotheum();
	public static final DamageSourceMana mana = new DamageSourceMana();

}
