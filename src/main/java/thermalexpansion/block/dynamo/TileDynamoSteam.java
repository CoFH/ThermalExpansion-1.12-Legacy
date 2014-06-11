package thermalexpansion.block.dynamo;

import cofh.core.CoFHProps;
import cofh.network.CoFHPacket;
import cofh.network.CoFHTileInfoPacket;
import cofh.util.ItemHelper;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.core.TEProps;
import thermalexpansion.util.FuelHandler;
import thermalfoundation.fluid.TFFluids;

public class TileDynamoSteam extends TileDynamoBase implements IFluidHandler {

	public static void initialize() {

		guiIds[BlockDynamo.Types.STEAM.ordinal()] = ThermalExpansion.proxy.registerGui("DynamoSteam", "dynamo", true);
		GameRegistry.registerTileEntity(TileDynamoSteam.class, "thermalexpansion.DynamoSteam");
	}

	static final int STEAM_MIN = 2000;

	static int coalRF = 48000;
	static int charcoalRF = 32000;
	static int woodRF = 4500;
	static int blockCoalRF = coalRF * 9;
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

	FluidTank steamTank = new FluidTank(MAX_FLUID);
	FluidTank waterTank = new FluidTank(MAX_FLUID);

	int currentFuelRF = getItemEnergyValue(coal);
	int steamAmount = 40;

	FluidStack steam = new FluidStack(FluidRegistry.getFluid("steam"), steamAmount);

	public TileDynamoSteam() {

		super();
		inventory = new ItemStack[1];

		steamAmount = config.maxPower / 2;
		steam = new FluidStack(FluidRegistry.getFluid("steam"), steamAmount);
	}

	@Override
	public int getType() {

		return BlockDynamo.Types.STEAM.ordinal();
	}

	public static int getItemEnergyValue(ItemStack fuel) {

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

	public FluidTank getTank(int tankIndex) {

		if (tankIndex == 0) {
			return steamTank;
		}
		return waterTank;
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
		return getItemEnergyValue(inventory[0]) > 0;
	}

	@Override
	public void generate() {

		if (steamTank.getFluidAmount() >= STEAM_MIN + steamAmount) {
			int energy = calcEnergy() * energyMod;
			energyStorage.modifyEnergyStored(energy);
			steamTank.drain(energy >> 1, true);
		} else {
			if (fuelRF <= 0 && inventory[0] != null) {
				int energy = getItemEnergyValue(inventory[0]) * fuelMod / 100;
				fuelRF += energy;
				currentFuelRF = energy;
				inventory[0] = ItemHelper.consumeItem(inventory[0]);
			}
			if (steamTank.getFluidAmount() > STEAM_MIN) {
				int energy = Math.min((steamTank.getFluidAmount() - STEAM_MIN) << 1, calcEnergy());
				energyStorage.modifyEnergyStored(energy);
				steamTank.drain(energy >> 1, true);
			}
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

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getGuiPacket() {

		CoFHPacket payload = CoFHTileInfoPacket.newPacket(this);

		payload.addByte(TEProps.PacketID.GUI.ordinal());
		payload.addFluidStack(steamTank.getFluid());
		payload.addFluidStack(waterTank.getFluid());
		payload.addInt(energyStorage.getEnergyStored());
		payload.addInt(fuelRF);
		payload.addInt(currentFuelRF);

		return payload;
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(CoFHPacket payload, boolean isServer, EntityPlayer thePlayer) {

		switch (TEProps.PacketID.values()[payload.getByte()]) {
		case GUI:
			steamTank.setFluid(payload.getFluidStack());
			waterTank.setFluid(payload.getFluidStack());
			energyStorage.setEnergyStored(payload.getInt());
			fuelRF = payload.getInt();
			currentFuelRF = payload.getInt();
			return;
		default:
		}
	}

	/* GUI METHODS */
	public int getScaledDuration(int scale) {

		if (currentFuelRF <= 0) {
			currentFuelRF = coalRF;
		}
		return fuelRF * scale / currentFuelRF;
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
		steam = new FluidStack(FluidRegistry.getFluid("steam"), steamAmount);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("FuelMax", currentFuelRF);
		nbt.setTag("SteamTank", steamTank.writeToNBT(new NBTTagCompound()));
		nbt.setTag("WaterTank", waterTank.writeToNBT(new NBTTagCompound()));
	}

	/* ISidedInventory */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return side != facing ? SLOTS : TEProps.EMPTY_INVENTORY;
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (from != ForgeDirection.UNKNOWN && from.ordinal() == facing) {
			return 0;
		}
		if (resource == null) {
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

		if (resource == null || from != ForgeDirection.UNKNOWN && from.ordinal() == facing) {
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

		if (from != ForgeDirection.UNKNOWN && from.ordinal() == facing) {
			return null;
		}
		return waterTank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		return from.ordinal() != facing;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return from.ordinal() != facing;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return new FluidTankInfo[] { steamTank.getInfo(), waterTank.getInfo() };
	}

	/* IEnergyInfo */
	@Override
	public int getInfoEnergyPerTick() {

		return steamTank.getFluidAmount() > STEAM_MIN ? Math.min((steamTank.getFluidAmount() - STEAM_MIN) << 1, calcEnergy()) : 0;
	}

}
