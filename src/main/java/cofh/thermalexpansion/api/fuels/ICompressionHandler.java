package cofh.thermalexpansion.api.fuels;

public interface ICompressionHandler {

	public boolean addFuel(String name, int energy);

	public boolean addCoolant(String name, int cooling);

	public boolean removeFuel(String name);

	public boolean removeCoolant(String name);

}
