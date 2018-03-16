package cofh.thermalexpansion.plugins;

import cofh.thermalexpansion.util.managers.machine.BrewerManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;

public class PluginExtraAlchemy extends PluginTEBase {

	public static final String MOD_ID = "extraalchemy";
	public static final String MOD_NAME = "Extra Alchemy";

	public PluginExtraAlchemy() {

		super(MOD_ID, MOD_NAME);
	}

	@Override
	public void registerDelegate() {

		PotionType fuse = getPotionType("fuse", "normal");
		PotionType fuseStrong = getPotionType("fuse", "strong");
		PotionType fuseQuick = getPotionType("fuse", "quick");

		PotionType recall = getPotionType("recall", "normal");
		PotionType recallStrong = getPotionType("recall", "strong");
		PotionType recallLong = getPotionType("recall", "long");

		PotionType sinking = getPotionType("sinking", "normal");
		PotionType sinkingStrong = getPotionType("sinking", "strong");
		PotionType sinkingLong = getPotionType("sinking", "long");

		PotionType dislocation = getPotionType("dislocation", "normal");
		PotionType dislocationStrong = getPotionType("dislocation", "strong");
		PotionType dislocationLong = getPotionType("dislocation", "long");

		PotionType magnetism = getPotionType("magnetism", "normal");
		PotionType magnetismStrong = getPotionType("magnetism", "strong");
		PotionType magnetismLong = getPotionType("magnetism", "long");

		PotionType pyper = getPotionType("pyper", "normal");
		PotionType pyperStrong = getPotionType("pyper", "strong");
		PotionType pyperLong = getPotionType("pyper", "long");

		PotionType pacifism = getPotionType("pacifism", "normal");
		PotionType pacifismStrong = getPotionType("pacifism", "strong");
		PotionType pacifismLong = getPotionType("pacifism", "long");

		PotionType crumbling = getPotionType("crumbling", "normal");
		PotionType crumblingStrong = getPotionType("crumbling", "strong");
		PotionType crumblingLong = getPotionType("crumbling", "long");

		PotionType photosynthesis = getPotionType("photosynthesis", "normal");
		PotionType photosynthesisStrong = getPotionType("photosynthesis", "strong");
		PotionType photosynthesisLong = getPotionType("photosynthesis", "long");

		PotionType hurry = getPotionType("hurry", "normal");
		PotionType hurryStrong = getPotionType("hurry", "strong");
		PotionType hurryLong = getPotionType("hurry", "long");

		PotionType reincarnation = getPotionType("reincarnation", "normal");
		PotionType reincarnationStrong = getPotionType("reincarnation", "strong");
		PotionType reincarnationLong = getPotionType("reincarnation", "long");

		PotionType combustion = getPotionType("combustion", "normal");
		PotionType combustionStrong = getPotionType("combustion", "strong");
		PotionType combustionLong = getPotionType("combustion", "long");

		PotionType learning = getPotionType("learning", "normal");
		PotionType learningStrong = getPotionType("learning", "strong");
		PotionType learningLong = getPotionType("learning", "long");

		PotionType gravity = getPotionType("gravity", "normal");
		PotionType gravityStrong = getPotionType("gravity", "strong");
		PotionType gravityLong = getPotionType("gravity", "long");

		PotionType leech = getPotionType("leech", "normal");
		PotionType leechStrong = getPotionType("leech", "strong");
		PotionType leechLong = getPotionType("leech", "long");

		PotionType sails = getPotionType("sails", "normal");
		PotionType sailsStrong = getPotionType("sails", "strong");
		PotionType sailsLong = getPotionType("sails", "long");

		PotionType beheading = getPotionType("beheading", "normal");
		PotionType beheadingStrong = getPotionType("beheading", "strong");
		PotionType beheadingLong = getPotionType("beheading", "long");

		PotionType freezing = getPotionType("freezing", "");
		PotionType concentration = getPotionType("concentration", "normal");
		PotionType returnNormal = getPotionType("return", "normal");
		PotionType cheatDeath = getPotionType("cheat_death", "normal");
		PotionType charged = getPotionType("charged", "normal");
		PotionType charged2 = getPotionType("charged2", "normal");
		PotionType detection = getPotionType("detection", "normal");
		PotionType dispel = getPotionType("dispel", "normal");

		/* BREWER */
		{
			ItemStack redstone = new ItemStack(Items.REDSTONE);
			ItemStack glowstone = new ItemStack(Items.GLOWSTONE_DUST);

			BrewerManager.addDefaultPotionRecipes(PotionTypes.AWKWARD, new ItemStack(Items.FIREWORK_CHARGE), fuse);
			BrewerManager.addDefaultPotionRecipes(fuse, redstone, fuseQuick);
			BrewerManager.addDefaultPotionRecipes(fuse, glowstone, fuseStrong);

			BrewerManager.addDefaultPotionRecipes(PotionTypes.SLOWNESS, new ItemStack(Items.ENDER_EYE), recall);
			BrewerManager.addDefaultPotionRecipes(recall, redstone, recallLong);
			BrewerManager.addDefaultPotionRecipes(recall, glowstone, recallStrong);

			BrewerManager.addDefaultPotionRecipes(PotionTypes.WATER_BREATHING, new ItemStack(Items.CLAY_BALL), sinking);
			BrewerManager.addDefaultPotionRecipes(sinking, redstone, sinkingLong);
			BrewerManager.addDefaultPotionRecipes(sinking, glowstone, sinkingStrong);

			BrewerManager.addDefaultPotionRecipes(PotionTypes.MUNDANE, new ItemStack(Items.CHORUS_FRUIT), dislocation);
			BrewerManager.addDefaultPotionRecipes(dislocation, redstone, dislocationLong);
			BrewerManager.addDefaultPotionRecipes(dislocation, glowstone, dislocationStrong);

			BrewerManager.addDefaultPotionRecipes(PotionTypes.AWKWARD, new ItemStack(Items.IRON_INGOT), magnetism);
			BrewerManager.addDefaultPotionRecipes(magnetism, redstone, magnetismLong);
			BrewerManager.addDefaultPotionRecipes(magnetism, glowstone, magnetismStrong);

			BrewerManager.addDefaultPotionRecipes(PotionTypes.AWKWARD, new ItemStack(Items.WHEAT), pyper);
			BrewerManager.addDefaultPotionRecipes(pyper, redstone, pyperLong);
			BrewerManager.addDefaultPotionRecipes(pyper, glowstone, pyperStrong);

			BrewerManager.addDefaultPotionRecipes(PotionTypes.STRONG_HARMING, new ItemStack(Items.GOLDEN_APPLE), pacifism);
			BrewerManager.addDefaultPotionRecipes(pacifism, redstone, pacifismLong);
			BrewerManager.addDefaultPotionRecipes(pacifism, glowstone, pacifismStrong);

			BrewerManager.addDefaultPotionRecipes(PotionTypes.THICK, new ItemStack(Items.FLINT), crumbling);
			BrewerManager.addDefaultPotionRecipes(crumbling, redstone, crumblingLong);
			BrewerManager.addDefaultPotionRecipes(crumbling, glowstone, crumblingStrong);

			BrewerManager.addDefaultPotionRecipes(PotionTypes.AWKWARD, new ItemStack(Items.BEETROOT_SEEDS), photosynthesis);
			BrewerManager.addDefaultPotionRecipes(photosynthesis, redstone, photosynthesisLong);
			BrewerManager.addDefaultPotionRecipes(photosynthesis, glowstone, photosynthesisStrong);

			BrewerManager.addDefaultPotionRecipes(charged2, new ItemStack(Items.COOKIE), hurry);
			BrewerManager.addDefaultPotionRecipes(hurry, redstone, hurryLong);
			BrewerManager.addDefaultPotionRecipes(hurry, glowstone, hurryStrong);

			BrewerManager.addDefaultPotionRecipes(PotionTypes.AWKWARD, new ItemStack(Items.LEATHER), reincarnation);
			BrewerManager.addDefaultPotionRecipes(reincarnation, redstone, reincarnationLong);
			BrewerManager.addDefaultPotionRecipes(reincarnation, glowstone, reincarnationStrong);

			BrewerManager.addDefaultPotionRecipes(PotionTypes.MUNDANE, new ItemStack(Blocks.COAL_BLOCK), combustion);
			BrewerManager.addDefaultPotionRecipes(combustion, redstone, combustionLong);
			BrewerManager.addDefaultPotionRecipes(combustion, glowstone, combustionStrong);

			BrewerManager.addDefaultPotionRecipes(PotionTypes.THICK, new ItemStack(Blocks.LAPIS_BLOCK), learning);
			BrewerManager.addDefaultPotionRecipes(learning, redstone, learningLong);
			BrewerManager.addDefaultPotionRecipes(learning, glowstone, learningStrong);

			BrewerManager.addDefaultPotionRecipes(PotionTypes.THICK, new ItemStack(Items.NETHERBRICK), gravity);
			BrewerManager.addDefaultPotionRecipes(gravity, redstone, gravityLong);
			BrewerManager.addDefaultPotionRecipes(gravity, glowstone, gravityStrong);

			BrewerManager.addDefaultPotionRecipes(PotionTypes.HARMING, new ItemStack(Items.SPECKLED_MELON), leech);
			BrewerManager.addDefaultPotionRecipes(leech, redstone, leechLong);
			BrewerManager.addDefaultPotionRecipes(leech, glowstone, leechStrong);

			BrewerManager.addDefaultPotionRecipes(PotionTypes.LONG_SWIFTNESS, new ItemStack(Items.FISH), sails);
			BrewerManager.addDefaultPotionRecipes(sails, redstone, sailsLong);
			BrewerManager.addDefaultPotionRecipes(sails, glowstone, sailsStrong);

			BrewerManager.addDefaultPotionRecipes(charged2, new ItemStack(Items.ROTTEN_FLESH), beheading);
			BrewerManager.addDefaultPotionRecipes(beheading, redstone, beheadingLong);
			BrewerManager.addDefaultPotionRecipes(beheading, glowstone, beheadingStrong);

			BrewerManager.addDefaultPotionRecipes(PotionTypes.AWKWARD, new ItemStack(Items.SNOWBALL), freezing);
			BrewerManager.addDefaultPotionRecipes(PotionTypes.AWKWARD, new ItemStack(Items.EGG), concentration);
			BrewerManager.addDefaultPotionRecipes(PotionTypes.AWKWARD, new ItemStack(Items.PRISMARINE_SHARD), returnNormal);
			BrewerManager.addDefaultPotionRecipes(charged2, new ItemStack(Items.GOLDEN_APPLE), cheatDeath);
			BrewerManager.addDefaultPotionRecipes(PotionTypes.THICK, new ItemStack(Items.GOLD_INGOT), charged);
			BrewerManager.addDefaultPotionRecipes(charged, new ItemStack(Items.PRISMARINE_CRYSTALS), charged2);
			BrewerManager.addDefaultPotionRecipes(PotionTypes.MUNDANE, new ItemStack(Items.SPIDER_EYE), detection);
			BrewerManager.addDefaultPotionRecipes(learningStrong, new ItemStack(Items.POISONOUS_POTATO), dispel);
		}
	}

}
