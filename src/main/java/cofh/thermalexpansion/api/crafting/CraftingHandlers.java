package cofh.thermalexpansion.api.crafting;

/**
 * This class allows for advanced Crafting Handler interfacing. Use it only if you absolutely know what you are doing and have a specific need. Otherwise, IMC
 * is the safest and most reliable way to interface BY FAR. If Thermal Expansion is not present, these fields can and WILL be NULL.
 *
 * The methods which are valid for each of these can be found in their respective source files.
 *
 * @author King Lemming
 *
 */
public class CraftingHandlers {

	private CraftingHandlers() {

	}

	public static IFurnaceHandler furnace;
	public static IPulverizerHandler pulverizer;
	public static ISawmillHandler sawmill;
	public static ISmelterHandler smelter;
	public static ICrucibleHandler crucible;
	public static ITransposerHandler transposer;
	public static IChargerHandler charger;
	public static IInsolatorHandler insolator;

}
