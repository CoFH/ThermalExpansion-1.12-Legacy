package cofh.thermalexpansion.block.dynamo;

import cofh.core.CoFHProps;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoSteam;
import cofh.thermalexpansion.gui.container.dynamo.ContainerDynamoSteam;
import cofh.thermalexpansion.util.FuelHandler;
import cofh.thermalfoundation.fluid.TFFluids;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileDynamoSteam extends TileDynamoBase implements IFluidHandler {

	static final int TYPE = BlockDynamo.Types.STEAM.ordinal();

	public static void initialize() {

		int maxPower = MathHelper.clampI(ThermalExpansion.config.get("block.tweak", "Dynamo.Steam.BasePower", 80), 10, 160);
		ThermalExpansion.config.set("block.tweak", "Dynamo.Steam.BasePower", maxPower);
		maxPower /= 10;
		maxPower *= 10;
		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setParamsDefault(maxPower);

		GameRegistry.registerTileEntity(TileDynamoSteam.class, "thermalexpansion.DynamoSteam");
	}

	static final int STEAM_MIN = 2000;

	static int coalRF = 48000;
	static int charcoalRF = 32000;
	static int woodRF = 4500;
	static int blockCoalRF = coalRF * 10;
	static int otherRF = woodRF / 3;

	static ItemStack coal = new ItemStack(Items.coal, 1, 0);
	static ItemStack charcoal = new ItemStack(Items.coal, 1, 1);
	static ItemStack blockCoal = new ItemStack(Blocks.coal_block);

	static {
		String category = "fuels.steam";
		coalRF = FuelHandler.configFuels.get(category, "coal", coalRF);
		charcoalRF = FuelHandler.configFuels.get(category, "charcoal", charcoalRF);
		woodRF = FuelHandler.configFuels.get(category, "wood", woodRF);
		blockCoalRF = coalRF * 10;
		otherRF = woodRF / 3;
	}

	public static int getEnergyValue(ItemStack fuel) {

		if (fuel == null) {
			return 0;
		}
		if (fuel.isItemEqual(coal)) {
			return coalRF;
		}
		if (fuel.isItemEqual(charcoal)) {
			return charcoalRF;
		}
		if (fuel.isItemEqual(blockCoal)) {
			return blockCoalRF;
		}
		Item item = fuel.getItem();

		if (fuel.getItem() instanceof ItemBlock && ((ItemBlock) item).field_150939_a.getMaterial() == Material.wood) {
			return woodRF;
		}
		if (item == Items.stick || item instanceof ItemBlock && ((ItemBlock) item).field_150939_a == Blocks.sapling) {
			return otherRF;
		}
		return GameRegistry.getFuelValue(fuel) * CoFHProps.RF_PER_MJ * 3 / 2;
	}

	FluidTankAdv steamTank = new FluidTankAdv(MAX_FLUID);
	FluidTankAdv waterTank = new FluidTankAdv(MAX_FLUID);

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
	public IIcon getActiveIcon() {

		return TFFluids.fluidSteam.getIcon();
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
	public FluidTankAdv getTank(int tankIndex) {

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
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("FuelMax", currentFuelRF);
		nbt.setTag("SteamTank", steamTank.writeToNBT(new NBTTagCompound()));
		nbt.setTag("WaterTank", waterTank.writeToNBT(new NBTTagCompound()));
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

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (resource == null || from.ordinal() == facing && !augmentCoilDuct) {
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

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		if (resource == null || !augmentCoilDuct) {
			return null;
		}
		if (resource.getFluid() == steam.getFluid()) {
			return steamTank.drain(resource.amount, doDrain);
		}
		if (resource.getFluid() == FluidRegistry.WATER) {
			return waterTank.drain(resource.amount, doDrain);
		}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		if (!augmentCoilDuct) {
			return null;
		}
		return waterTank.drain(maxDrain, doDrain);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return new FluidTankInfo[] { steamTank.getInfo(), waterTank.getInfo() };
	}

	/* ISidedInventory */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return side != facing || augmentCoilDuct ? SLOTS : CoFHProps.EMPTY_INVENTORY;
	}

}
