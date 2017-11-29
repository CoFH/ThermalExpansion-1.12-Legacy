package cofh.thermalexpansion.block.dynamo;

import cofh.core.init.CoreProps;
import cofh.core.network.PacketCoFHBase;
import cofh.core.render.TextureHelper;
import cofh.core.util.helpers.EnergyHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.dynamo.BlockDynamo.Type;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoEnervation;
import cofh.thermalexpansion.gui.container.dynamo.ContainerDynamoEnervation;
import cofh.thermalexpansion.util.managers.dynamo.EnervationManager;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Arrays;
import java.util.HashSet;

public class TileDynamoEnervation extends TileDynamoBase {

	private static final int TYPE = Type.ENERVATION.getMetadata();
	public static int basePower = 40;

	public static void initialize() {

		VALID_AUGMENTS[TYPE] = new HashSet<>();

		GameRegistry.registerTileEntity(TileDynamoEnervation.class, "thermalexpansion:dynamo_enervation");

		config();
	}

	public static void config() {

		String category = "Dynamo.Enervation";
		BlockDynamo.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy generation (in RF/t) for an Enervation Dynamo. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower, smallStorage);
	}

	public TileDynamoEnervation() {

		super();
		inventory = new ItemStack[1];
		Arrays.fill(inventory, ItemStack.EMPTY);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	protected boolean canStart() {

		return EnervationManager.getFuelEnergy(inventory[0]) > energyConfig.maxPower;
	}

	protected void processStart() {

		if (EnergyHelper.isEnergyContainerItem(inventory[0])) {
			IEnergyContainerItem container = (IEnergyContainerItem) inventory[0].getItem();
			maxFuelRF = container.extractEnergy(inventory[0], container.getEnergyStored(inventory[0]), false);
			fuelRF += maxFuelRF;
		} else {
			maxFuelRF = EnervationManager.getFuelEnergy(inventory[0]) * energyMod / ENERGY_BASE;
			fuelRF += maxFuelRF;
			inventory[0] = ItemHelper.consumeItem(inventory[0]);
		}
	}

	@Override
	public TextureAtlasSprite getBaseUnderlayTexture() {

		return TextureHelper.getTexture(TFFluids.fluidRedstone.getStill());
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

		if (maxFuelRF <= 0) {
			maxFuelRF = Math.max(fuelRF, EnervationManager.DEFAULT_ENERGY);
		} else if (EnergyHelper.isEnergyContainerItem(inventory[0])) {
			return scale;
		}
		return fuelRF * scale / maxFuelRF;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		maxFuelRF = nbt.getInteger("FuelMax");

		if (maxFuelRF <= 0) {
			maxFuelRF = Math.max(fuelRF, EnervationManager.DEFAULT_ENERGY);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("FuelMax", maxFuelRF);
		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addInt(maxFuelRF);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		maxFuelRF = payload.getInt();
	}

	/* HELPERS */

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return EnervationManager.getFuelEnergy(stack) > 0;
	}

	/* ISidedInventory */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		return side == null || side.ordinal() != facing || augmentCoilDuct ? CoreProps.SINGLE_INVENTORY : CoreProps.EMPTY_INVENTORY;
	}

}
