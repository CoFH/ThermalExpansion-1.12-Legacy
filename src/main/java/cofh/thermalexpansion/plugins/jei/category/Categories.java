package cofh.thermalexpansion.plugins.jei.category;

import javax.annotation.Nonnull;

public enum Categories {
	Pulverizer(new PulverizerCategory()), PoweredFurnace(new FurnaceCategory()), Crucible(new CrucibleCategory()), Sawmill(new SawmillCategory()), Smelter(new SmelterCategory()), TransposerExtraction(new TransposerCategory(true)), TransposerFilling(new TransposerCategory(false)), Compactor(new CompactorCategory()), Refinery(new RefineryCategory());

	@Nonnull
	private final CategoryBase category;

	public CategoryBase getCategory() {

		return category;
	}

	Categories(@Nonnull CategoryBase category) {

		this.category = category;
	}

	public static Categories byId(int id) {

		return values()[id];
	}

	public static String getDefualt() {

		return values()[0].category.getUid();
	}
}
