package cofh.thermalexpansion.api.fuels;

/**
 * This class allows for advanced Fuel Handler interfacing. Use it only if you absolutely know what you are doing and have a specific need. Otherwise, IMC is the safest and most reliable way to interface BY FAR. If Thermal Expansion is not present, these fields can and WILL be NULL.
 *
 * The methods which are valid for each of these can be found in their respective source files.
 *
 * @author King Lemming
 */
public class FuelHandlers {

	private FuelHandlers() {

	}

	public static ISteamHandler steam;
	public static IMagmaticHandler magmatic;
	public static ICompressionHandler compression;
	public static IReactantHandler reactant;
	public static IEnervationHandler enervation;

}
