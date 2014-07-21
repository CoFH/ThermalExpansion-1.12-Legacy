package thermalexpansion.block.dynamo;

import cofh.network.CoFHPacket;
import cofh.util.ItemHelper;
import cofh.util.MathHelper;
import cofh.util.fluid.FluidTankAdv;
import cpw.mods.fml.common.registry.GameRegistry;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.client.dynamo.GuiDynamoReactant;
import thermalexpansion.gui.container.dynamo.ContainerDynamoReactant;

public class TileDynamoReactant extends TileDynamoBase implements IFluidHandler {

	static final int TYPE = BlockDynamo.Types.REACTANT.ordinal();

	public static void initialize() {

		int maxPower = MathHelper.clampI(ThermalExpansion.config.get("block.tweak", "Dynamo.Reactant.BasePower", 80), 10, 160);
		ThermalExpansion.config.set("block.tweak", "Dynamo.Reactant.BasePower", maxPower);
		maxPower /= 10;
		maxPower *= 10;
		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setParamsDefault(maxPower);

		GameRegistry.registerTileEntity(TileDynamoReactant.class, "thermalexpansion.DynamoReactant");
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

	static TMap fuels = new THashMap<Fluid, Integer>();

	FluidTankAdv tank = new FluidTankAdv(MAX_FLUID);

	FluidStack renderFluid = new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME);
	int reactantRF;
	int currentReactantRF;
	int reactantMod = FUEL_MOD;

	public TileDynamoReactant() {

		super();
		inventory = new ItemStack[1];
	}

	public static int getFuelEnergy(FluidStack stack) {

		return stack == null ? 0 : (Integer) fuels.get(stack.getFluid());
	}

	public static int getReactantEnergy(ItemStack reactant) {

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

	public static int getReactantMod(ItemStack reactant) {

		if (reactant == null) {
			return 0;
		}
		return FUEL_MOD;
	}

	public static boolean isValidFuel(FluidStack stack) {

		return stack == null ? false : fuels.containsKey(stack.getFluid());
	}

	public static boolean registerFuel(Fluid fluid, int energy) {

		if (fluid == null || energy < 10000) {
			return false;
		}
		fuels.put(fluid, energy / 20);
		return true;
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	protected boolean canGenerate() {

		if (fuelRF > 0) {
			return reactantRF > 0 || getReactantEnergy(inventory[0]) > 0;
		}
		if (reactantRF > 0) {
			return tank.getFluidAmount() >= 50;
		}
		return tank.getFluidAmount() >= 50 && getReactantEnergy(inventory[0]) > 0;
	}

	@Override
	protected void generate() {

		int energy;

		if (fuelRF <= 0) {
			fuelRF = getFuelEnergy(tank.getFluid()) * reactantMod / FUEL_MOD * fuelMod / FUEL_MOD;
			tank.drain(50, true);
		}
		if (reactantRF <= 0) {
			energy = getReactantEnergy(inventory[0]) * fuelMod / FUEL_MOD;
			reactantMod = getReactantMod(inventory[0]);
			reactantRF += energy;
			currentReactantRF = energy;
			inventory[0] = ItemHelper.consumeItem(inventory[0]);
		}
		energy = calcEnergy() * energyMod;
		energyStorage.modifyEnergyStored(energy);
		fuelRF -= energy;
		reactantRF -= energy;
	}

	@Override
	public IIcon getActiveIcon() {

		return renderFluid.getFluid().getIcon();
	}

	/* GUI METHODS */
	@Override
	public GuiContainer getGuiClient(InventoryPlayer inventory) {

		return new GuiDynamoReactant(inventory, this);
	}

	@Override
	public Container getGuiServer(InventoryPlayer inventory) {

		return new ContainerDynamoReactant(inventory, this);
	}

	@Override
	public int getScaledDuration(int scale) {

		if (currentReactantRF <= 0) {
			currentReactantRF = sugarRF;
		}
		return reactantRF * scale / currentReactantRF;
	}

	@Override
	public FluidTankAdv getTank(int tankIndex) {

		return tank;
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

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();

		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	public CoFHPacket getGuiPacket() {

		CoFHPacket payload = super.getGuiPacket();

		payload.addFluidStack(tank.getFluid());
		payload.addInt(reactantRF);
		payload.addInt(currentReactantRF);

		return payload;
	}

	@Override
	protected void handleGuiPacket(CoFHPacket payload) {

		super.handleGuiPacket(payload);

		tank.setFluid(payload.getFluidStack());
		reactantRF = payload.getInt();
		currentReactantRF = payload.getInt();
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

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (resource == null || from.ordinal() == facing && !augmentCoilDuct) {
			return 0;
		}
		if (isValidFuel(resource)) {
			return tank.fill(resource, doFill);
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		if (resource == null || !augmentCoilDuct) {
			return null;
		}
		if (isValidFuel(resource)) {
			return tank.drain(resource.amount, doDrain);
		}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		if (!augmentCoilDuct) {
			return null;
		}
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return new FluidTankInfo[] { tank.getInfo() };
	}

	/* ISidedInventory */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return side != facing || augmentCoilDuct ? SLOTS : TEProps.EMPTY_INVENTORY;
	}

}
