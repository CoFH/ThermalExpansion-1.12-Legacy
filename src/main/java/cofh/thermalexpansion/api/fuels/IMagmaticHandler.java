package cofh.thermalexpansion.api.fuels;

public interface IMagmaticHandler {

	boolean addFuel(String name, int energy);

	boolean removeFuel(String name);

}
