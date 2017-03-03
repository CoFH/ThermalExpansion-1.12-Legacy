package cofh.thermalexpansion.block.dynamo;

import codechicken.lib.texture.TextureUtils;
import cofh.api.energy.IEnergyContainerItem;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoEnervation;
import cofh.thermalexpansion.gui.container.dynamo.ContainerDynamoEnervation;
import cofh.thermalfoundation.init.TFFluids;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.Map;

public class TileDynamoEnervation extends TileDynamoBase {

	private static final int TYPE = BlockDynamo.Type.ENERVATION.getMetadata();

	public static void initialize() {

		validAugments[TYPE] = new ArrayList<>();

		GameRegistry.registerTileEntity(TileDynamoEnervation.class, "thermalexpansion.dynamo_enervation");

		config();
	}

	public static void config() {

		String category = "Dynamo.Enervation";
		BlockDynamo.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setDefaultParams(40);
	}

	private int currentFuelRF = DEFAULT_ENERGY;

	public TileDynamoEnervation() {

		super();
		inventory = new ItemStack[1];
	}

	@Override
	public int getType() {

		return TYPE;
	}

	protected boolean canStart() {

		return getEnergyValue(inventory[0]) > energyConfig.maxPower;
	}

	protected void processStart() {

		if (EnergyHelper.isEnergyContainerItem(inventory[0])) {
			IEnergyContainerItem container = (IEnergyContainerItem) inventory[0].getItem();
			currentFuelRF = container.extractEnergy(inventory[0], container.getEnergyStored(inventory[0]), false);
			fuelRF += currentFuelRF;
		} else {
			currentFuelRF = getEnergyValue(inventory[0]) * energyMod / ENERGY_BASE;
			fuelRF += currentFuelRF;
			inventory[0] = ItemHelper.consumeItem(inventory[0]);
		}
	}

	@Override
	public TextureAtlasSprite getActiveIcon() {

		return TextureUtils.getTexture(TFFluids.fluidRedstone.getStill());
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
			currentFuelRF = DEFAULT_ENERGY;
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
			currentFuelRF = DEFAULT_ENERGY;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("FuelMax", currentFuelRF);
		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
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

	/* HELPERS */

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return getEnergyValue(stack) > 0;
	}

	/* ISidedInventory */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		return side.ordinal() != facing || augmentCoilDuct ? CoreProps.SINGLE_INVENTORY : CoreProps.EMPTY_INVENTORY;
	}

	/* FUEL MANAGER */
	private static TObjectIntHashMap<ComparableItemStack> fuels = new TObjectIntHashMap<>();

	private static int DEFAULT_ENERGY = 64000;

	public static boolean addFuel(ItemStack stack, int energy) {

		if (stack == null || energy < 1600 || energy > 200000000) {
			return false;
		}
		fuels.put(new ComparableItemStack(stack), energy);
		return true;
	}

	public static boolean removeFuel(ItemStack stack) {

		fuels.remove(new ComparableItemStack(stack));
		return true;
	}

	public static int getEnergyValue(ItemStack stack) {

		if (stack == null) {
			return 0;
		}
		int energy = fuels.get(new ComparableItemStack(stack));

		if (energy > 0) {
			return energy;
		}
		if (EnergyHelper.isEnergyContainerItem(stack)) {
			IEnergyContainerItem container = (IEnergyContainerItem) stack.getItem();
			return container.extractEnergy(stack, container.getEnergyStored(stack), true);
		}
		return 0;
	}

}
