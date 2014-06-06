package thermalexpansion.block.machine;

import cofh.network.CoFHPacket;
import cofh.render.IconRegistry;
import cofh.render.RenderHelper;
import cofh.util.FluidHelper;
import cofh.util.ServerHelper;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.player.EntityPlayer;
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
import thermalexpansion.util.crafting.CrucibleManager;
import thermalexpansion.util.crafting.CrucibleManager.RecipeCrucible;

public class TileCrucible extends TileMachineEnergized implements IFluidHandler {

	public static final int TYPE = BlockMachine.Types.CRUCIBLE.ordinal();

	public static void initialize() {

		sideData[TYPE] = new SideConfig();
		sideData[TYPE].numGroup = 3;
		sideData[TYPE].slotGroups = new int[][] { {}, { 0 }, {} };
		sideData[TYPE].allowInsertion = new boolean[] { false, true, false };
		sideData[TYPE].allowExtraction = new boolean[] { false, false, false };
		sideData[TYPE].sideTex = new int[] { 0, 1, 4 };

		energyData[TYPE] = new EnergyConfig();
		energyData[TYPE].setParams(40, 400, 400000);

		guiIds[TYPE] = ThermalExpansion.proxy.registerGui("Crucible", "machine", true);
		GameRegistry.registerTileEntity(TileCrucible.class, "thermalexpansion.Crucible");
	}

	FluidStack renderFluid = new FluidStack(FluidRegistry.LAVA, 0);
	FluidTank tank = new FluidTank(MAX_FLUID_LARGE);
	FluidStack outputBuffer;
	int outputTrackerFluid;

	public TileCrucible() {

		super();

		sideCache = new byte[] { 1, 1, 2, 2, 2, 2 };
		inventory = new ItemStack[1 + 1];
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public boolean canStart() {

		if (inventory[0] == null) {
			return false;
		}
		RecipeCrucible recipe = CrucibleManager.getRecipe(inventory[0]);

		if (recipe == null || energyStorage.getEnergyStored() < recipe.getEnergy()) {
			return false;
		}
		if (inventory[0].stackSize < recipe.getInput().stackSize) {
			return false;
		}
		FluidStack output = recipe.getOutput();
		return tank.fill(output, false) == output.amount;
	}

	@Override
	protected boolean hasValidInput() {

		RecipeCrucible recipe = CrucibleManager.getRecipe(inventory[0]);
		return recipe == null ? false : recipe.getInput().stackSize <= inventory[0].stackSize;
	}

	@Override
	protected void processStart() {

		processMax = CrucibleManager.getRecipe(inventory[0]).getEnergy();
		processRem = processMax;

		int prevID = renderFluid.fluidID;
		renderFluid = CrucibleManager.getRecipe(inventory[0]).getOutput();
		renderFluid.amount = 0;

		if (prevID != renderFluid.fluidID) {
			sendFluidPacket();
		}
	}

	@Override
	protected void processFinish() {

		tank.fill(CrucibleManager.getRecipe(inventory[0]).getOutput(), true);
		inventory[0].stackSize--;

		if (inventory[0].stackSize <= 0) {
			inventory[0] = null;
		}
	}

	protected void transferFluid() {

		if (!upgradeAutoTransfer) {
			return;
		}
		if (tank.getFluidAmount() <= 0) {
			return;
		}
		int side;
		outputBuffer = new FluidStack(tank.getFluid(), Math.min(tank.getFluidAmount(), RATE));
		for (int i = outputTrackerFluid + 1; i <= outputTrackerFluid + 6; i++) {
			side = i % 6;

			if (sideCache[side] == 2) {
				int toDrain = FluidHelper.insertFluidIntoAdjacentFluidHandler(this, side, outputBuffer, true);

				if (toDrain > 0) {
					tank.drain(toDrain, true);
					outputTrackerFluid = side;
					break;
				}
			}
		}
	}

	@Override
	public void updateEntity() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		transferFluid();

		super.updateEntity();
	}

	@Override
	public boolean canAcceptItem(ItemStack stack, int slot, int side) {

		return slot == 0 ? CrucibleManager.recipeExists(stack) : true;
	}

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();

		payload.addFluidStack(renderFluid);
		return payload;
	}

	@Override
	public CoFHPacket getGuiCoFHPacket() {

		CoFHPacket payload = super.getGuiCoFHPacket();

		if (tank.getFluid() == null) {
			payload.addFluidStack(renderFluid);
		} else {
			payload.addFluidStack(tank.getFluid());
		}
		return payload;
	}

	@Override
	public CoFHPacket getFluidCoFHPacket() {

		CoFHPacket payload = super.getFluidCoFHPacket();

		payload.addFluidStack(renderFluid);

		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (ServerHelper.isClientWorld(worldObj)) {
			renderFluid = payload.getFluidStack();
		} else {
			payload.getFluidStack();
		}
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(CoFHPacket payload, boolean isServer, EntityPlayer thePlayer) {

		switch (TEProps.PacketID.values()[payload.getByte()]) {
		case GUI:
			isActive = payload.getBool();
			processMax = payload.getInt();
			processRem = payload.getInt();
			energyStorage.setEnergyStored(payload.getInt());
			tank.setFluid(payload.getFluidStack());
			return;
		case FLUID:
			renderFluid = payload.getFluidStack();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			return;
		default:
		}
	}

	/* GUI METHODS */
	public FluidTank getTank() {

		return tank;
	}

	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		outputTrackerFluid = nbt.getInteger("Tracker");
		tank.readFromNBT(nbt);

		if (tank.getFluid() != null) {
			renderFluid = tank.getFluid();
		} else if (CrucibleManager.getRecipe(inventory[0]) != null) {
			renderFluid = CrucibleManager.getRecipe(inventory[0]).getOutput();
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("Tracker", outputTrackerFluid);
		tank.writeToNBT(nbt);
	}

	/* ISidedBlockTexture */
	@Override
	public IIcon getBlockTexture(int side, int pass) {

		if (pass == 0) {
			if (side == 0) {
				return IconRegistry.getIcon("MachineBottom");
			} else if (side == 1) {
				return IconRegistry.getIcon("MachineTop");
			}
			return side != facing ? IconRegistry.getIcon("MachineSide") : isActive ? RenderHelper.getFluidTexture(renderFluid) : IconRegistry.getIcon(
					"MachineFace", getType());
		} else {
			return side != facing ? IconRegistry.getIcon(TEProps.textureSelection, sideData[getType()].sideTex[sideCache[side]]) : isActive ? IconRegistry
					.getIcon("MachineActive", getType()) : IconRegistry.getIcon("MachineFace", getType());
		}
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		if (from == ForgeDirection.UNKNOWN || sideCache[from.ordinal()] != 2) {
			return null;
		}
		if (resource == null || !resource.isFluidEqual(tank.getFluid())) {
			return null;
		}
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		if (from != ForgeDirection.UNKNOWN && sideCache[from.ordinal()] != 2) {
			return null;
		}
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return new FluidTankInfo[] { tank.getInfo() };
	}

}
