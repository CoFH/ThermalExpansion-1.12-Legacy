package cofh.thermalexpansion.block.dynamo;

import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.render.TextureHelper;
import cofh.core.util.core.EnergyConfig;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.EnergyHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoEnervation;
import cofh.thermalexpansion.gui.container.dynamo.ContainerDynamoEnervation;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.managers.dynamo.EnervationManager;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.HashSet;

public class TileDynamoEnervation extends TileDynamoBase {

	protected static final EnergyConfig ENERGY_CONFIG = new EnergyConfig();
	protected static final HashSet<String> VALID_AUGMENTS = new HashSet<>();

	public static boolean enable = true;
	public static int basePower = 40;

	public static void initialize() {

		VALID_AUGMENTS.addAll(VALID_AUGMENTS_BASE);

		VALID_AUGMENTS.add(TEProps.DYNAMO_ENERVATION_ENCHANT);

		GameRegistry.registerTileEntity(TileDynamoEnervation.class, "thermalexpansion:dynamo_enervation");

		config();
	}

	public static void config() {

		String category = "Dynamo.Enervation";
		enable = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy generation (in RF/t) for an Enervation Dynamo. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);
		ENERGY_CONFIG.setDefaultParams(basePower, smallStorage);
	}

	/* AUGMENTS */
	protected boolean augmentEnchant;

	public TileDynamoEnervation() {

		super();
		inventory = new ItemStack[1];
		Arrays.fill(inventory, ItemStack.EMPTY);
	}

	@Override
	protected String getTileName() {

		return "tile.thermalexpansion.dynamo.enervation.name";
	}

	@Override
	protected EnergyConfig getEnergyConfig() {

		return ENERGY_CONFIG;
	}

	@Override
	protected HashSet<String> getValidAugments() {

		return VALID_AUGMENTS;
	}

	@Override
	protected boolean canStart() {

		if (augmentEnchant) {
			return !EnchantmentHelper.getEnchantments(inventory[0]).isEmpty();
		}
		return EnervationManager.getFuelEnergy(inventory[0]) > 0;
	}

	@Override
	protected void processStart() {

		if (augmentEnchant) {
			maxFuelRF = EnervationManager.getEnchantEnergy(inventory[0]) * energyMod / ENERGY_BASE;
			fuelRF += maxFuelRF;
			inventory[0] = ItemStack.EMPTY;
		} else if (EnergyHelper.isEnergyContainerItem(inventory[0])) {
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
	@SideOnly (Side.CLIENT)
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

	@Override
	public int getFuelEnergy(ItemStack stack) {

		return (augmentEnchant ? EnervationManager.getEnchantEnergy(stack) : EnervationManager.getFuelEnergy(stack)) * energyMod / ENERGY_BASE;
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

		augmentEnchant = false;
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentEnchant && TEProps.DYNAMO_ENERVATION_ENCHANT.equals(id)) {
			augmentEnchant = true;
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

		return augmentEnchant ? !EnchantmentHelper.getEnchantments(stack).isEmpty() : EnervationManager.getFuelEnergy(stack) > 0;
	}

	/* ISidedInventory */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		return side == null || side.ordinal() != facing || augmentCoilDuct ? CoreProps.SINGLE_INVENTORY : CoreProps.EMPTY_INVENTORY;
	}

}
