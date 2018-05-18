package cofh.thermalexpansion.block.dynamo;

import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.util.core.EnergyConfig;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.dynamo.BlockDynamo.Type;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoNumismatic;
import cofh.thermalexpansion.gui.container.dynamo.ContainerDynamoNumismatic;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.util.managers.dynamo.NumismaticManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.HashSet;

public class TileDynamoNumismatic extends TileDynamoBase {

	private static final int TYPE = Type.NUMISMATIC.getMetadata();
	public static int basePower = 40;

	public static void initialize() {

		VALID_AUGMENTS[TYPE] = new HashSet<>();
		VALID_AUGMENTS[TYPE].add(TEProps.DYNAMO_NUMISMATIC_GEM);

		GameRegistry.registerTileEntity(TileDynamoNumismatic.class, "thermalexpansion:dynamo_numismatic");

		config();
	}

	public static void config() {

		String category = "Dynamo.Numismatic";
		BlockDynamo.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy generation (in RF/t) for a Numismatic Dynamo. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower, smallStorage);
	}

	/* AUGMENTS */
	protected boolean augmentGem;

	public TileDynamoNumismatic() {

		super();
		inventory = new ItemStack[1];
		Arrays.fill(inventory, ItemStack.EMPTY);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	protected boolean canStart() {

		if (augmentGem) {
			return NumismaticManager.getGemFuelEnergy(inventory[0]) > energyConfig.maxPower;
		}
		return NumismaticManager.getFuelEnergy(inventory[0]) > energyConfig.maxPower;
	}

	@Override
	protected void processStart() {

		if (augmentGem) {
			maxFuelRF = NumismaticManager.getGemFuelEnergy(inventory[0]) * energyMod / ENERGY_BASE;
		} else {
			maxFuelRF = NumismaticManager.getFuelEnergy(inventory[0]) * energyMod / ENERGY_BASE;
		}
		fuelRF += maxFuelRF;
		inventory[0] = ItemHelper.consumeItem(inventory[0]);
	}

	@Override
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getBaseUnderlayTexture() {

		return TETextures.PORTAL_UNDERLAY;
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

		if (maxFuelRF <= 0) {
			maxFuelRF = Math.max(fuelRF, NumismaticManager.DEFAULT_ENERGY);
		}
		return fuelRF * scale / maxFuelRF;
	}

	@Override
	public int getFuelEnergy(ItemStack stack) {

		return (augmentGem ? NumismaticManager.getGemFuelEnergy(stack) : NumismaticManager.getFuelEnergy(stack)) * energyMod / ENERGY_BASE;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		maxFuelRF = nbt.getInteger("FuelMax");

		if (maxFuelRF <= 0) {
			maxFuelRF = Math.max(fuelRF, NumismaticManager.DEFAULT_ENERGY);
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
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addInt(maxFuelRF);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		maxFuelRF = payload.getInt();
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		augmentGem = false;
	}

	@Override
	protected void postAugmentInstall() {

		super.postAugmentInstall();
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentGem && TEProps.DYNAMO_NUMISMATIC_GEM.equals(id)) {
			augmentGem = true;
			hasModeAugment = true;
			energyConfig.setDefaultParams(energyConfig.maxPower + 4 * getBasePower(this.level), smallStorage);
			energyMod += 25;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return augmentGem ? NumismaticManager.getGemFuelEnergy(stack) > 0 : NumismaticManager.getFuelEnergy(stack) > 0;
	}

	/* ISidedInventory */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		return side == null || side.ordinal() != facing || augmentCoilDuct ? CoreProps.SINGLE_INVENTORY : CoreProps.EMPTY_INVENTORY;
	}

}
