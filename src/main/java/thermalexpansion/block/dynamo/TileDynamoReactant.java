package thermalexpansion.block.dynamo;

import cofh.network.CoFHPacket;
import cofh.network.CoFHTileInfoPacket;
import cofh.util.ItemHelper;
import cpw.mods.fml.common.registry.GameRegistry;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.core.TEProps;

public class TileDynamoReactant extends TileDynamoBase implements IFluidHandler {

	public static void initialize() {

		guiIds[BlockDynamo.Types.REACTANT.ordinal()] = ThermalExpansion.proxy.registerGui("DynamoReactant", "dynamo", true);
		GameRegistry.registerTileEntity(TileDynamoReactant.class, "cofh.thermalexpansion.DynamoReactant");
	}

	static int sugarRF = 16000;
	static int gunpowderRF = 160000;
	static int blazePowderRF = 640000;
	static int ghastTearRF = 1600000;
	static int netherStarRF = 6400000;

	static ItemStack sugar = new ItemStack(Items.sugar, 1, 0);
	static ItemStack gunpowder = new ItemStack(Items.gunpowder, 1, 0);
	static ItemStack blazePowder = new ItemStack(Items.blaze_powder, 1, 0);
	static ItemStack ghastTear = new ItemStack(Items.ghast_tear, 1, 0);
	static ItemStack netherStar = new ItemStack(Items.nether_star, 1, 0);

	static Map fuels = new HashMap<Fluid, Integer>();

	FluidTank tank = new FluidTank(MAX_FLUID);

	FluidStack renderFluid = new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME);
	int reactantRF;
	int currentReactantRF;

	public TileDynamoReactant() {

		super();
		inventory = new ItemStack[1];
	}

	@Override
	public int getType() {

		return BlockDynamo.Types.REACTANT.ordinal();
	}

	public static boolean registerFuel(Fluid fluid, int energy) {

		if (fluid == null || energy <= 10000) {
			return false;
		}
		fuels.put(fluid, energy / 100);
		return true;
	}

	public static int getFuelEnergy(FluidStack stack) {

		return stack == null ? 0 : (Integer) fuels.get(stack.getFluid());
	}

	public static boolean isValidFuel(FluidStack stack) {

		return stack == null ? false : fuels.containsKey(stack.getFluid());
	}

	public static int getItemEnergyValue(ItemStack reactant) {

		if (reactant == null) {
			return 0;
		}
		if (reactant.isItemEqual(sugar)) {
			return sugarRF;
		}
		if (reactant.isItemEqual(gunpowder)) {
			return gunpowderRF;
		}
		if (reactant.isItemEqual(blazePowder)) {
			return blazePowderRF;
		}
		if (reactant.isItemEqual(ghastTear)) {
			return ghastTearRF;
		}
		if (reactant.isItemEqual(netherStar)) {
			return netherStarRF;
		}
		return 0;
	}

	public FluidTank getTank(int tankIndex) {

		return tank;
	}

	@Override
	protected boolean canGenerate() {

		if (fuelRF > 0) {
			return reactantRF > 0 || getItemEnergyValue(inventory[0]) > 0;
		}
		if (reactantRF > 0) {
			return tank.getFluidAmount() >= 10;
		}
		return tank.getFluidAmount() >= 10 && getItemEnergyValue(inventory[0]) > 0;
	}

	@Override
	protected void generate() {

		int energy;

		if (fuelRF <= 0) {
			fuelRF = getFuelEnergy(tank.getFluid());
			tank.drain(10, true);
		}
		if (reactantRF <= 0) {
			energy = getItemEnergyValue(inventory[0]);
			reactantRF += energy;
			currentReactantRF = energy;
			inventory[0] = ItemHelper.consumeItem(inventory[0]);
		}
		energy = calcEnergy();
		energyStorage.modifyEnergyStored(energy);
		fuelRF -= energy;
		reactantRF -= energy;
	}

	@Override
	public IIcon getActiveIcon() {

		return renderFluid.getFluid().getIcon();
	}

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();

		payload.addFluidStack(tank.getFluid());
		return payload;
	}

	@Override
	public CoFHPacket getGuiPacket() {

		CoFHPacket payload = CoFHTileInfoPacket.newPacket(this);

		payload.addByte(TEProps.PacketID.GUI.ordinal());
		payload.addFluidStack(tank.getFluid());
		payload.addInt(energyStorage.getEnergyStored());
		payload.addInt(reactantRF);
		payload.addInt(currentReactantRF);

		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		renderFluid = payload.getFluidStack();

		if (renderFluid == null) {
			renderFluid = new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME);
		}
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(CoFHPacket payload, boolean isServer, EntityPlayer thePlayer) {

		switch (TEProps.PacketID.values()[payload.getByte()]) {
		case GUI:
			tank.setFluid(payload.getFluidStack());
			energyStorage.setEnergyStored(payload.getInt());
			reactantRF = payload.getInt();
			currentReactantRF = payload.getInt();
			return;
		default:
		}
	}

	/* GUI METHODS */
	public int getScaledDuration(int scale) {

		if (currentReactantRF <= 0) {
			currentReactantRF = sugarRF;
		}
		return reactantRF * scale / currentReactantRF;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		currentReactantRF = nbt.getInteger("ReactMax");
		reactantRF = nbt.getInteger("React");
		tank.readFromNBT(nbt);

		if (!isValidFuel(tank.getFluid())) {
			tank.setFluid(null);
		}
		if (tank.getFluid() != null) {
			renderFluid = tank.getFluid();
		}
		if (currentReactantRF <= 0) {
			currentReactantRF = sugarRF;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("ReactMax", currentReactantRF);
		nbt.setInteger("React", reactantRF);
		tank.writeToNBT(nbt);
	}

	/* ISidedInventory */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return side != facing ? SLOTS : TEProps.EMPTY_INVENTORY;
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (resource == null || from != ForgeDirection.UNKNOWN && from.ordinal() == facing) {
			return 0;
		}
		if (isValidFuel(resource)) {
			return tank.fill(resource, doFill);
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		if (resource == null || from != ForgeDirection.UNKNOWN && from.ordinal() == facing) {
			return null;
		}
		if (isValidFuel(resource)) {
			return tank.drain(resource.amount, doDrain);
		}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		return tank.drain(maxDrain, doDrain);
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

		return new FluidTankInfo[] { tank.getInfo() };
	}

}
