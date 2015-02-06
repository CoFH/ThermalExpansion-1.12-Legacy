package cofh.thermalexpansion.block.dynamo;

import cofh.api.energy.IEnergyContainerItem;
import cofh.core.CoFHProps;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoEnervation;
import cofh.thermalexpansion.gui.container.dynamo.ContainerDynamoEnervation;
import cofh.thermalexpansion.util.FuelHandler;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import thermalfoundation.fluid.TFFluids;

public class TileDynamoEnervation extends TileDynamoBase {

	static final int TYPE = BlockDynamo.Types.ENERVATION.ordinal();

	public static void initialize() {

		int maxPower = MathHelper.clampI(ThermalExpansion.config.get("block.tweak", "Dynamo.Enervation.BasePower", 80), 10, 160);
		ThermalExpansion.config.set("block.tweak", "Dynamo.Enervation.BasePower", maxPower);
		maxPower /= 10;
		maxPower *= 10;
		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setParamsDefault(maxPower);

		GameRegistry.registerTileEntity(TileDynamoEnervation.class, "thermalexpansion.DynamoEnervation");
	}

	static int redstoneRF = 64000;
	static int blockRedstoneRF = redstoneRF * 10;

	static ItemStack redstone = new ItemStack(Items.redstone);
	static ItemStack blockRedstone = new ItemStack(Blocks.redstone_block);

	static {
		String category = "fuels.enervation";
		redstoneRF = FuelHandler.configFuels.get(category, "redstone", redstoneRF);
		blockRedstoneRF = redstoneRF * 10;
	}

	public static int getEnergyValue(ItemStack fuel) {

		if (fuel == null) {
			return 0;
		}
		if (fuel.isItemEqual(redstone)) {
			return redstoneRF;
		}
		if (fuel.isItemEqual(blockRedstone)) {
			return blockRedstoneRF;
		}
		if (EnergyHelper.isEnergyContainerItem(fuel)) {
			IEnergyContainerItem container = (IEnergyContainerItem) fuel.getItem();
			return container.extractEnergy(fuel, container.getEnergyStored(fuel), true);
		}
		return 0;
	}

	int currentFuelRF = getEnergyValue(redstone);

	public TileDynamoEnervation() {

		super();
		inventory = new ItemStack[1];
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	protected boolean canGenerate() {

		if (fuelRF > 0) {
			return true;
		}
		return getEnergyValue(inventory[0]) > 0;
	}

	@Override
	protected void generate() {

		int energy;

		if (fuelRF <= 0) {
			if (EnergyHelper.isEnergyContainerItem(inventory[0])) {
				IEnergyContainerItem container = (IEnergyContainerItem) inventory[0].getItem();
				fuelRF += container.extractEnergy(inventory[0], container.getEnergyStored(inventory[0]), false);
				currentFuelRF = redstoneRF;
			} else {
				energy = getEnergyValue(inventory[0]) * fuelMod / FUEL_MOD;
				fuelRF += energy;
				currentFuelRF = energy;
				inventory[0] = ItemHelper.consumeItem(inventory[0]);
			}
		}
		energy = Math.min(fuelRF, calcEnergy() * energyMod);
		energyStorage.modifyEnergyStored(energy);
		fuelRF -= energy;
	}

	@Override
	public IIcon getActiveIcon() {

		return TFFluids.fluidRedstone.getIcon();
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiDynamoEnervation(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerDynamoEnervation(inventory, this);
	}

	@Override
	public int getScaledDuration(int scale) {

		if (currentFuelRF <= 0) {
			currentFuelRF = redstoneRF;
		} else if (EnergyHelper.isEnergyContainerItem(inventory[0])) {
			return scale;
		}
		return fuelRF * scale / currentFuelRF;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		currentFuelRF = nbt.getInteger("FuelMax");

		if (currentFuelRF <= 0) {
			currentFuelRF = redstoneRF;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("FuelMax", currentFuelRF);
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addInt(currentFuelRF);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		currentFuelRF = payload.getInt();
	}

	/* IEnergyInfo */
	@Override
	public int getInfoEnergyPerTick() {

		return Math.min(getEnergyValue(inventory[0]), calcEnergy() * energyMod);
	}

	/* ISidedInventory */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return side != facing || augmentCoilDuct ? SLOTS : CoFHProps.EMPTY_INVENTORY;
	}

}
