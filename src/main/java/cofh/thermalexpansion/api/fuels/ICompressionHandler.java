package cofh.thermalexpansion.api.fuels;

public interface ICompressionHandler {

	boolean addFuel(String name, int energy);

	boolean addCoolant(String name, int cooling);

	boolean removeFuel(String name);

	boolean removeCoolant(String name);

}
