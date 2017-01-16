package cofh.thermalexpansion.block.dynamo;

import codechicken.lib.texture.TextureUtils;
import cofh.api.energy.IEnergyContainerItem;
import cofh.core.CoFHProps;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoEnervation;
import cofh.thermalexpansion.gui.container.dynamo.ContainerDynamoEnervation;
import cofh.thermalexpansion.util.FuelManager;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.registry.GameRegistry;

import gnu.trove.map.hash.THashMap;

import java.util.Map;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileDynamoEnervation extends TileDynamoBase {

	static final int TYPE = BlockDynamo.Types.ENERVATION.ordinal();

	public static void initialize() {

		GameRegistry.registerTileEntity(TileDynamoEnervation.class, "thermalexpansion.DynamoEnervation");
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
				energy = container.extractEnergy(inventory[0], container.getEnergyStored(inventory[0]), false);
				fuelRF += energy;
				currentFuelRF = energy;
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
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("FuelMax", currentFuelRF);
        return nbt;
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

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return getEnergyValue(stack) > 0;
	}

	/* ISidedInventory */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		return side.ordinal() != facing || augmentCoilDuct ? SLOTS : CoFHProps.EMPTY_INVENTORY;
	}

	/* FUEL MANAGER */
	static int redstoneRF = 64000;
	static int blockRedstoneRF = redstoneRF * 10;

	static ItemStack redstone = new ItemStack(Items.REDSTONE);
	static ItemStack blockRedstone = new ItemStack(Blocks.REDSTONE_BLOCK);

	static Map<ComparableItemStack, Integer> fuels = new THashMap<ComparableItemStack, Integer>();

	static {
		String category = "Fuels.Enervation";
		redstoneRF = FuelManager.configFuels.get(category, "redstone", redstoneRF);
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

}
