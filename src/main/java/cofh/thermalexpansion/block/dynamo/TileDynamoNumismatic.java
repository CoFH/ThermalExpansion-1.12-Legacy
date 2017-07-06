package cofh.thermalexpansion.block.dynamo;

import cofh.core.init.CoreProps;
import cofh.core.network.PacketCoFHBase;
import cofh.core.render.TextureHelper;
import cofh.core.util.helpers.EnergyHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoNumismatic;
import cofh.thermalexpansion.gui.container.dynamo.ContainerDynamoNumismatic;
import cofh.thermalexpansion.util.managers.dynamo.NumismaticManager;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Arrays;
import java.util.HashSet;

public class TileDynamoNumismatic extends TileDynamoBase {

	private static final int TYPE = BlockDynamo.Type.NUMISMATIC.getMetadata();
	public static int basePower = 40;

	public static void initialize() {

		VALID_AUGMENTS[TYPE] = new HashSet<>();

		GameRegistry.registerTileEntity(TileDynamoNumismatic.class, "thermalexpansion:dynamo_numismatic");

		config();
	}

	public static void config() {

		String category = "Dynamo.Numismatic";
		BlockDynamo.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		DEFAULT_ENERGY_CONFIG[TYPE] = new EnergyConfig();
		DEFAULT_ENERGY_CONFIG[TYPE].setDefaultParams(basePower);
	}

	private int currentFuelRF = 0;

	public TileDynamoNumismatic() {

		super();
		inventory = new ItemStack[1];
		Arrays.fill(inventory, ItemStack.EMPTY);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	protected boolean canStart() {

		return NumismaticManager.getFuelEnergy(inventory[0]) > energyConfig.maxPower;
	}

	protected void processStart() {

		currentFuelRF = NumismaticManager.getFuelEnergy(inventory[0]) * energyMod / ENERGY_BASE;
		fuelRF += currentFuelRF;
		inventory[0] = ItemHelper.consumeItem(inventory[0]);
	}

	@Override
	public TextureAtlasSprite getActiveIcon() {

		return TextureHelper.getTexture(TFFluids.fluidMana.getStill());
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiDynamoNumismatic(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerDynamoNumismatic(inventory, this);
	}

	@Override
	public int getScaledDuration(int scale) {

		if (currentFuelRF <= 0) {
			currentFuelRF = Math.max(fuelRF, NumismaticManager.DEFAULT_ENERGY);
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
			currentFuelRF = Math.max(fuelRF, NumismaticManager.DEFAULT_ENERGY);
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

		return NumismaticManager.getFuelEnergy(stack) > 0;
	}

	/* ISidedInventory */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		return side == null || side.ordinal() != facing || augmentCoilDuct ? CoreProps.SINGLE_INVENTORY : CoreProps.EMPTY_INVENTORY;
	}

}
