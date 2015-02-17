package cofh.thermalexpansion.block.machine;

import cofh.core.util.CoreUtils;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.client.machine.GuiInsolator;
import cofh.thermalexpansion.gui.container.machine.ContainerInsolator;
import cofh.thermalexpansion.util.crafting.InsolatorManager;
import cofh.thermalexpansion.util.crafting.InsolatorManager.RecipeInsolator;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileInsolator extends TileMachineBase implements IFluidHandler {

	static final int TYPE = BlockMachine.Types.INSOLATOR.ordinal();

	public static void initialize() {

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numGroup = 7;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, { 0, 1 }, { 2 }, { 3 }, { 2, 3 }, { 0 }, { 1 } };
		defaultSideConfig[TYPE].allowInsertion = new boolean[] { false, true, false, false, false, true, true };
		defaultSideConfig[TYPE].allowExtraction = new boolean[] { false, true, true, true, true, true, true };
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 1, 2, 3, 4, 5, 6 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 3, 1, 2, 2, 2, 2 };

		String category = "Machine.Insolator";
		int basePower = MathHelper.clampI(ThermalExpansion.config.get(category, "BasePower", 20), 10, 500);
		ThermalExpansion.config.set(category, "BasePower", basePower);
		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setParamsPower(basePower);

		sounds[TYPE] = CoreUtils.getSoundName(ThermalExpansion.modId, "blockMachineInsolator");

		GameRegistry.registerTileEntity(TileInsolator.class, "thermalexpansion.Insolator");
	}

	int outputTrackerPrimary;
	int outputTrackerSecondary;

	FluidTankAdv tank = new FluidTankAdv(TEProps.MAX_FLUID_LARGE);

	public TileInsolator() {

		super();

		inventory = new ItemStack[2 + 1 + 1 + 1];
		tank.setLock(FluidRegistry.WATER);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void updateEntity() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		boolean curActive = isActive;

		if (isActive) {
			if (processRem > 0) {
				int energy = calcEnergy();
				energyStorage.modifyEnergyStored(-energy * energyMod);
				processRem -= energy * processMod;
				tank.drain(energy * processMod / 10, true);
			}
			if (canFinish()) {
				processFinish();
				transferProducts();
				energyStorage.modifyEnergyStored(-processRem * energyMod / processMod);

				if (!redstoneControlOrDisable() || !canStart()) {
					isActive = false;
					wasActive = true;
					tracker.markTime(worldObj);
				} else {
					processStart();
				}
			}
		} else if (redstoneControlOrDisable()) {
			if (timeCheck()) {
				transferProducts();
			}
			if (timeCheckEighth() && canStart()) {
				processStart();
				int energy = calcEnergy();
				energyStorage.modifyEnergyStored(-energy * energyMod);
				processRem -= energy * processMod;
				tank.drain(energy * processMod / 10, true);
				isActive = true;
			}
		}
		updateIfChanged(curActive);
		chargeEnergy();
	}

	@Override
	public int getMaxInputSlot() {

		return 1;
	}

	@Override
	protected boolean canStart() {

		if (inventory[0] == null && inventory[1] == null) {
			return false;
		}
		RecipeInsolator recipe = InsolatorManager.getRecipe(inventory[0], inventory[1]);

		if (recipe == null || energyStorage.getEnergyStored() < recipe.getEnergy() * energyMod / processMod || tank.getFluidAmount() < recipe.getEnergy() / 10) {
			return false;
		}
		if (InsolatorManager.isRecipeReversed(inventory[0], inventory[1])) {
			if (recipe.getPrimaryInput().stackSize > inventory[1].stackSize || recipe.getSecondaryInput().stackSize > inventory[0].stackSize) {
				return false;
			}
		} else {
			if (recipe.getPrimaryInput().stackSize > inventory[0].stackSize || recipe.getSecondaryInput().stackSize > inventory[1].stackSize) {
				return false;
			}
		}
		ItemStack primaryItem = recipe.getPrimaryOutput();
		ItemStack secondaryItem = recipe.getSecondaryOutput();

		if (secondaryItem != null && inventory[3] != null) {
			if (!inventory[3].isItemEqual(secondaryItem)) {
				return false;
			}
			if (inventory[3].stackSize + secondaryItem.stackSize > secondaryItem.getMaxStackSize()) {
				return false;
			}
		}
		if (inventory[2] == null) {
			return true;
		}
		if (!inventory[2].isItemEqual(primaryItem)) {
			return false;
		}
		return inventory[2].stackSize + primaryItem.stackSize <= primaryItem.getMaxStackSize();
	}

	@Override
	protected boolean hasValidInput() {

		RecipeInsolator recipe = InsolatorManager.getRecipe(inventory[0], inventory[1]);

		if (recipe == null) {
			return false;
		}
		if (InsolatorManager.isRecipeReversed(inventory[0], inventory[1])) {
			if (recipe.getPrimaryInput().stackSize > inventory[1].stackSize || recipe.getSecondaryInput().stackSize > inventory[0].stackSize) {
				return false;
			}
		} else {
			if (recipe.getPrimaryInput().stackSize > inventory[0].stackSize || recipe.getSecondaryInput().stackSize > inventory[1].stackSize) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void processStart() {

		processMax = InsolatorManager.getRecipe(inventory[0], inventory[1]).getEnergy();
		processRem = processMax;
	}

	@Override
	protected void processFinish() {

		RecipeInsolator recipe = InsolatorManager.getRecipe(inventory[0], inventory[1]);
		ItemStack primaryItem = recipe.getPrimaryOutput();
		ItemStack secondaryItem = recipe.getSecondaryOutput();

		if (inventory[2] == null) {
			inventory[2] = primaryItem;
		} else {
			inventory[2].stackSize += primaryItem.stackSize;
		}
		if (secondaryItem != null) {
			int recipeChance = recipe.getSecondaryOutputChance();
			if (recipeChance >= 100 || worldObj.rand.nextInt(secondaryChance) < recipeChance) {
				if (inventory[3] == null) {
					inventory[3] = secondaryItem;
				} else {
					inventory[3].stackSize += secondaryItem.stackSize;
				}
				if (secondaryChance < recipeChance && inventory[3].stackSize + secondaryItem.stackSize <= secondaryItem.getMaxStackSize()
						&& worldObj.rand.nextInt(secondaryChance) < recipeChance - secondaryChance) {
					inventory[3].stackSize += secondaryItem.stackSize;
				}
			}
		}
		if (InsolatorManager.isRecipeReversed(inventory[0], inventory[1])) {
			inventory[1].stackSize -= recipe.getPrimaryInput().stackSize;
			inventory[0].stackSize -= recipe.getSecondaryInput().stackSize;
		} else {
			inventory[0].stackSize -= recipe.getPrimaryInput().stackSize;
			inventory[1].stackSize -= recipe.getSecondaryInput().stackSize;
		}
		if (inventory[0].stackSize <= 0) {
			inventory[0] = null;
		}
		if (inventory[1].stackSize <= 0) {
			inventory[1] = null;
		}
	}

	@Override
	protected void transferProducts() {

		if (!augmentAutoTransfer) {
			return;
		}
		int side;
		if (inventory[2] != null) {
			for (int i = outputTrackerPrimary + 1; i <= outputTrackerPrimary + 6; i++) {
				side = i % 6;

				if (sideCache[side] == 2 || sideCache[side] == 4) {
					if (transferItem(2, 8, side)) {
						outputTrackerPrimary = side;
						break;
					}
				}
			}
		}
		if (inventory[3] == null) {
			return;
		}
		for (int i = outputTrackerSecondary + 1; i <= outputTrackerSecondary + 6; i++) {
			side = i % 6;

			if (sideCache[side] == 3 || sideCache[side] == 4) {
				if (transferItem(3, 8, side)) {
					outputTrackerSecondary = side;
					break;
				}
			}
		}
	}

	@Override
	public boolean isItemValid(ItemStack stack, int slot, int side) {

		return slot == 0 ? InsolatorManager.isItemValid(stack) : true;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiInsolator(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerInsolator(inventory, this);
	}

	@Override
	public void receiveGuiNetworkData(int i, int j) {

		if (tank.getFluid() == null) {
			tank.setFluid(new FluidStack(FluidRegistry.WATER, j));
		} else {
			tank.getFluid().amount = j;
		}
	}

	@Override
	public void sendGuiNetworkData(Container container, ICrafting player) {

		super.sendGuiNetworkData(container, player);

		player.sendProgressBarUpdate(container, 0, tank.getFluidAmount());
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

		outputTrackerPrimary = nbt.getInteger("Tracker1");
		outputTrackerSecondary = nbt.getInteger("Tracker2");
		tank.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("Tracker1", outputTrackerPrimary);
		nbt.setInteger("Tracker2", outputTrackerSecondary);
		tank.writeToNBT(nbt);
	}

	/* NETWORK METHODS */
	// TODO: Add these if Insolator changes over to something else.
	// @Override
	// public CoFHPacket getGuiPacket() {
	//
	// CoFHPacket payload = super.getGuiPacket();
	//
	// return payload;
	// }
	//
	// @Override
	// protected void handleGuiPacket(CoFHPacket payload) {
	//
	// }

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return new FluidTankInfo[] { tank.getInfo() };
	}

}
