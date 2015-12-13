package cofh.thermalexpansion.block.machine;

import cofh.core.network.PacketCoFHBase;
import cofh.core.render.IconRegistry;
import cofh.core.util.CoreUtils;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.render.RenderHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine.Types;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.client.machine.GuiCrucible;
import cofh.thermalexpansion.gui.container.machine.ContainerCrucible;
import cofh.thermalexpansion.util.crafting.CrucibleManager;
import cofh.thermalexpansion.util.crafting.CrucibleManager.RecipeCrucible;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileCrucible extends TileMachineBase implements IFluidHandler {

	public static void initialize() {

		int type = BlockMachine.Types.CRUCIBLE.ordinal();

		defaultSideConfig[type] = new SideConfig();
		defaultSideConfig[type].numConfig = 4;
		defaultSideConfig[type].slotGroups = new int[][] { {}, { 0 }, {}, { 0 } };
		defaultSideConfig[type].allowInsertionSide = new boolean[] { false, true, false, true };
		defaultSideConfig[type].allowExtractionSide = new boolean[] { false, true, false, true };
		defaultSideConfig[type].allowInsertionSlot = new boolean[] { true, false };
		defaultSideConfig[type].allowExtractionSlot = new boolean[] { true, false };
		defaultSideConfig[type].sideTex = new int[] { 0, 1, 4, 7 };
		defaultSideConfig[type].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		String category = "Machine.Crucible";
		int basePower = MathHelper.clamp(ThermalExpansion.config.get(category, "BasePower", 400), 10, 500);
		ThermalExpansion.config.set(category, "BasePower", basePower);
		defaultEnergyConfig[type] = new EnergyConfig();
		defaultEnergyConfig[type].setParams(basePower / 10, basePower, Math.max(400000, basePower * 1000));

		sounds[type] = CoreUtils.getSoundName(ThermalExpansion.modId, "blockMachineCrucible");

		GameRegistry.registerTileEntity(TileCrucible.class, "thermalexpansion.Crucible");
	}

	int inputTracker;
	int outputTrackerFluid;

	FluidTankAdv tank = new FluidTankAdv(TEProps.MAX_FLUID_LARGE);
	FluidStack outputBuffer;
	FluidStack renderFluid = new FluidStack(FluidRegistry.LAVA, 0);

	public TileCrucible() {

		super(Types.CRUCIBLE);
		inventory = new ItemStack[1 + 1];
	}

	@Override
	protected boolean canStart() {

		if (inventory[0] == null) {
			return false;
		}
		RecipeCrucible recipe = CrucibleManager.getRecipe(inventory[0]);

		if (recipe == null || energyStorage.getEnergyStored() < recipe.getEnergy() * energyMod / processMod) {
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

		int prevID = renderFluid.getFluidID();
		renderFluid = CrucibleManager.getRecipe(inventory[0]).getOutput();
		renderFluid.amount = 0;

		if (prevID != renderFluid.getFluidID()) {
			sendFluidPacket();
		}
	}

	@Override
	protected void processFinish() {

		RecipeCrucible recipe = CrucibleManager.getRecipe(inventory[0]);

		if (recipe == null) {
			isActive = false;
			wasActive = true;
			tracker.markTime(worldObj);
			processRem = 0;
			return;
		}
		tank.fill(recipe.getOutput(), true);
		inventory[0].stackSize--;

		if (inventory[0].stackSize <= 0) {
			inventory[0] = null;
		}
	}

	@Override
	protected void transferInput() {

		if (!augmentAutoInput) {
			return;
		}
		int side;
		for (int i = inputTracker + 1; i <= inputTracker + 6; i++) {
			side = i % 6;
			if (sideCache[side] == 1) {
				if (extractItem(0, AUTO_TRANSFER[level], side)) {
					inputTracker = side;
					break;
				}
			}
		}
	}

	protected void transferOutputFluid() {

		if (!augmentAutoOutput) {
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
		transferOutputFluid();

		super.updateEntity();
	}

	@Override
	protected void onLevelChange() {

		super.onLevelChange();

		tank.setCapacity(TEProps.MAX_FLUID_LARGE * FLUID_CAPACITY[level]);
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiCrucible(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerCrucible(inventory, this);
	}

	@Override
	public FluidTankAdv getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger("TrackIn");
		outputTrackerFluid = nbt.getInteger("TrackOut");
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

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut", outputTrackerFluid);
		tank.writeToNBT(nbt);
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();
		payload.addFluidStack(renderFluid);
		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();
		if (tank.getFluid() == null) {
			payload.addFluidStack(renderFluid);
		} else {
			payload.addFluidStack(tank.getFluid());
		}
		return payload;
	}

	@Override
	public PacketCoFHBase getFluidPacket() {

		PacketCoFHBase payload = super.getFluidPacket();
		payload.addFluidStack(renderFluid);
		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);
		tank.setFluid(payload.getFluidStack());
	}

	@Override
	protected void handleFluidPacket(PacketCoFHBase payload) {

		super.handleFluidPacket(payload);
		renderFluid = payload.getFluidStack();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot == 0 ? CrucibleManager.recipeExists(stack) : true;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {
			renderFluid = payload.getFluidStack();
		} else {
			payload.getFluidStack();
		}
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		if (from != ForgeDirection.UNKNOWN && sideCache[from.ordinal()] != 2) {
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

		// return sideCache[from.ordinal()] == 2;
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		// if (sideCache[from.ordinal()] != 2) {
		// return CoFHProps.EMPTY_TANK_INFO;
		// }
		return new FluidTankInfo[] { tank.getInfo() };
	}

	/* ISidedTexture */
	@Override
	public IIcon getTexture(int side, int pass) {

		if (pass == 0) {
			if (side == 0) {
				return BlockMachine.machineBottom;
			} else if (side == 1) {
				return BlockMachine.machineTop;
			}
			return side != facing ? BlockMachine.machineSide : isActive ? RenderHelper.getFluidTexture(renderFluid) : BlockMachine.machineFace[type];
		} else {
			return side != facing ? IconRegistry.getIcon(TEProps.textureSelection, sideConfig.sideTex[sideCache[side]])
					: isActive ? BlockMachine.machineActive[type] : BlockMachine.machineFace[type];
		}
	}

}
