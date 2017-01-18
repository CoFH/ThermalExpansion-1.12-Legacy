package cofh.thermalexpansion.block.dynamo;

import codechicken.lib.texture.TextureUtils;
import cofh.core.CoFHProps;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.fluid.FluidTankCore;
import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoSteam;
import cofh.thermalexpansion.gui.container.dynamo.ContainerDynamoSteam;
import cofh.thermalexpansion.util.FuelManager;
import cofh.thermalfoundation.init.TFFluids;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;

public class TileDynamoSteam extends TileDynamoBase {

	static final int TYPE = BlockDynamo.Types.STEAM.ordinal();

	public static void initialize() {

		GameRegistry.registerTileEntity(TileDynamoSteam.class, "thermalexpansion.DynamoSteam");
	}

	static final int STEAM_MIN = 2000;

	FluidTankCore steamTank = new FluidTankCore(MAX_FLUID);
	FluidTankCore waterTank = new FluidTankCore(MAX_FLUID);

	int currentFuelRF = getEnergyValue(coal);
	int steamAmount = defaultEnergyConfig[TYPE].maxPower / 2;

	FluidStack steam = new FluidStack(FluidRegistry.getFluid("steam"), steamAmount);

	public TileDynamoSteam() {

		super();
		inventory = new ItemStack[1];
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	protected boolean canGenerate() {

		if (steamTank.getFluidAmount() > STEAM_MIN) {
			return true;
		}
		if (waterTank.getFluidAmount() < config.maxPower) {
			return false;
		}
		if (fuelRF > 0) {
			return true;
		}
		return getEnergyValue(inventory[0]) > 0;
	}

	@Override
	public void attenuate() {

		if (timeCheck()) {
			fuelRF -= 10;

			if (fuelRF < 0) {
				fuelRF = 0;
			}
			steamTank.drain(config.minPower, true);
		}
	}

	@Override
	public void generate() {

		if (steamTank.getFluidAmount() >= STEAM_MIN + steamAmount * energyMod) {
			int energy = calcEnergy() * energyMod;
			energyStorage.modifyEnergyStored(energy);
			steamTank.drain(energy >> 1, true);
		} else {
			if (fuelRF <= 0 && inventory[0] != null) {
				int energy = getEnergyValue(inventory[0]) * fuelMod / FUEL_MOD;
				fuelRF += energy;
				currentFuelRF = energy;
				inventory[0] = ItemHelper.consumeItem(inventory[0]);
			}
			if (fuelRF > 0) {
				int filled = steamTank.fill(steam, true);
				fuelRF -= filled << 1;
				if (timeCheck()) {
					waterTank.drain(filled, true);
				}
			}
			if (steamTank.getFluidAmount() > STEAM_MIN) {
				int energy = Math.min((steamTank.getFluidAmount() - STEAM_MIN) << 1, calcEnergy());
				energy *= energyMod;
				energyStorage.modifyEnergyStored(energy);
				steamTank.drain(energy >> 1, true);
			}
			return;
		}
		if (fuelRF > 0) {
			int filled = steamTank.fill(steam, true);
			fuelRF -= filled << 1;
			if (timeCheck()) {
				waterTank.drain(filled, true);
			}
		}
	}

	@Override
	public TextureAtlasSprite getActiveIcon() {

		return TextureUtils.getTexture(TFFluids.fluidSteam.getStill());
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiDynamoSteam(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerDynamoSteam(inventory, this);
	}

	@Override
	public int getScaledDuration(int scale) {

		if (currentFuelRF <= 0) {
			currentFuelRF = coalRF;
		}
		return fuelRF * scale / currentFuelRF;
	}

	@Override
	public FluidTankCore getTank(int tankIndex) {

		if (tankIndex == 0) {
			return steamTank;
		}
		return waterTank;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		currentFuelRF = nbt.getInteger("FuelMax");
		steamTank.readFromNBT(nbt.getCompoundTag("SteamTank"));
		waterTank.readFromNBT(nbt.getCompoundTag("WaterTank"));

		if (currentFuelRF <= 0) {
			currentFuelRF = coalRF;
		}
		steam.amount = steamAmount * energyMod;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("FuelMax", currentFuelRF);
		nbt.setTag("SteamTank", steamTank.writeToNBT(new NBTTagCompound()));
		nbt.setTag("WaterTank", waterTank.writeToNBT(new NBTTagCompound()));
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addInt(currentFuelRF);
		payload.addFluidStack(steamTank.getFluid());
		payload.addFluidStack(waterTank.getFluid());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		currentFuelRF = payload.getInt();
		steamTank.setFluid(payload.getFluidStack());
		waterTank.setFluid(payload.getFluidStack());
	}

	/* AUGMENT HELPERS */
	@Override
	protected void onInstalled() {

		super.onInstalled();
		steam.amount = steamAmount * energyMod;
	}

	@Override
	protected void resetAugments() {

		super.resetAugments();
		steam.amount = steamAmount;
	}

	/* IEnergyInfo */
	@Override
	public int getInfoEnergyPerTick() {

		return steamTank.getFluidAmount() >= STEAM_MIN ? calcEnergy() * energyMod : 0;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {

		return super.hasCapability(capability, facing) || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler() {
				@Override
				public IFluidTankProperties[] getTankProperties() {

					return FluidTankProperties.convert(new FluidTankInfo[] { steamTank.getInfo(), waterTank.getInfo() });
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (resource == null || (from != null && from.ordinal() == facing && !augmentCoilDuct)) {
						return 0;
					}
					if (resource.getFluid() == steam.getFluid()) {
						return steamTank.fill(resource, doFill);
					}
					if (resource.getFluid() == FluidRegistry.WATER) {
						return waterTank.fill(resource, doFill);
					}
					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (resource == null || from == null || !augmentCoilDuct && from.ordinal() == facing) {
						return null;
					}
					if (resource.getFluid() == FluidRegistry.WATER) {
						return waterTank.drain(resource.amount, doDrain);
					}
					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (!augmentCoilDuct && from.ordinal() == facing) {
						return null;
					}
					return waterTank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, from);
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
	static int coalRF = 48000;
	static int charcoalRF = 32000;
	static int woodRF = 4500;
	static int blockCoalRF = coalRF * 10;
	static int otherRF = woodRF / 3;

	static ItemStack coal = new ItemStack(Items.COAL, 1, 0);
	static ItemStack charcoal = new ItemStack(Items.COAL, 1, 1);
	static ItemStack blockCoal = new ItemStack(Blocks.COAL_BLOCK);

	static TObjectIntHashMap<ComparableItemStack> fuels = new TObjectIntHashMap<ComparableItemStack>();

	static {
		String category = "Fuels.Steam";
		FuelManager.configFuels.getCategory(category).setComment("You can adjust fuel values for the Steam Dynamo in this section. New fuels cannot be added at this time.");
		coalRF = FuelManager.configFuels.get(category, "coal", coalRF);
		charcoalRF = FuelManager.configFuels.get(category, "charcoal", charcoalRF);
		woodRF = FuelManager.configFuels.get(category, "wood", woodRF);
		blockCoalRF = coalRF * 10;
		otherRF = woodRF / 3;
	}

	public static boolean addFuel(ItemStack stack, int energy) {

		if (stack == null || energy < 640 || energy > 200000000) {
			return false;
		}
		fuels.put(new ComparableItemStack(stack), energy);
		return true;
	}

	public static int getEnergyValue(ItemStack stack) {

		if (stack == null) {
			return 0;
		}
		if (stack.isItemEqual(coal)) {
			return coalRF;
		}
		if (stack.isItemEqual(charcoal)) {
			return charcoalRF;
		}
		if (stack.isItemEqual(blockCoal)) {
			return blockCoalRF;
		}
		Item item = stack.getItem();

		if (stack.getItem() instanceof ItemBlock && ((ItemBlock) item).block.blockMaterial == Material.WOOD) {
			return woodRF;
		}
		if (item == Items.STICK || item instanceof ItemBlock && ((ItemBlock) item).block == Blocks.SAPLING) {
			return otherRF;
		}
		return GameRegistry.getFuelValue(stack) * CoFHProps.RF_PER_MJ * 3 / 2;
	}

}
