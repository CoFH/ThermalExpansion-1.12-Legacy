package cofh.thermalexpansion.block.machine;

import cofh.core.network.PacketCoFHBase;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.inventory.InventoryCraftingFalse;
import cofh.lib.util.helpers.InventoryHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.client.machine.GuiAssembler;
import cofh.thermalexpansion.gui.container.machine.ContainerAssembler;
import cofh.thermalexpansion.util.SchematicHelper;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileAssembler extends TileMachineBase implements IFluidHandler {

	static final int TYPE = BlockMachine.Types.ASSEMBLER.ordinal();

	public static void initialize() {

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numConfig = 5;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, { 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 }, { 1 },
				{ 3, 4, 5, 6, 7, 8, 9, 10, 11 }, { 12, 13, 14, 15, 16, 17, 18, 19, 20 } };
		defaultSideConfig[TYPE].allowInsertionSide = new boolean[] { false, true, false, true, true };
		defaultSideConfig[TYPE].allowExtractionSide = new boolean[] { false, true, true, true, true };
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 1, 4, 5, 6 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		String category = "Machine.Assembler";
		int basePower = MathHelper.clampI(ThermalExpansion.config.get(category, "BasePower", 20), 10, 500);
		ThermalExpansion.config.set(category, "BasePower", basePower);
		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setParamsPower(basePower);

		GameRegistry.registerTileEntity(TileAssembler.class, "thermalexpansion.Assembler");
	}

	public static final int PROCESS_ENERGY = 20;

	private boolean needsCache = true;
	private boolean needsCraft = false;

	int outputTracker;
	FluidTankAdv tank = new FluidTankAdv(TEProps.MAX_FLUID_LARGE);
	InventoryCrafting crafting = new InventoryCraftingFalse(3, 3);
	ItemStack recipeOutput;

	FluidStack[] filledContainer = new FluidStack[9];
	ItemStack[] recipeSlot = new ItemStack[9];
	String[] recipeOre = new String[9];

	public TileAssembler() {

		super();

		inventory = new ItemStack[1 + 1 + 1 + 18];
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

		if (redstoneControlOrDisable()) {
			if (needsCraft) {
				updateOutput();
			}
			if (timeCheck()) {
				transferProducts();
			}
		} else {
			if (isActive) {
				wasActive = true;
			}
			isActive = false;
		}
		updateIfChanged(curActive);
		chargeEnergy();
	}

	@Override
	protected void transferProducts() {

		if (!augmentAutoTransfer) {
			return;
		}
		if (inventory[1] == null) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;

			if (sideCache[side] == 2) {
				if (transferItem(1, AUTO_EJECT[level], side)) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	@Override
	protected void onLevelChange() {

		super.onLevelChange();

		tank.setCapacity(TEProps.MAX_FLUID_LARGE * FLUID_CAPACITY[level]);
	}

	@Override
	public int getChargeSlot() {

		return 2;
	}

	public boolean canCreate(ItemStack recipe) {

		return recipe != null
				&& (inventory[1] == null || recipe.isItemEqual(inventory[1]) && inventory[1].stackSize + recipe.stackSize <= recipe.getMaxStackSize());
	}

	public boolean createItem() {

		if (energyStorage.getEnergyStored() < PROCESS_ENERGY) {
			return false;
		}
		ItemStack[] invCopy = InventoryHelper.cloneInventory(inventory);
		FluidStack fluidCopy = null;

		if (tank.getFluid() != null) {
			fluidCopy = tank.getFluid().copy();
		}
		boolean found = false;
		for (int i = 0; i < 9; i++) {
			if (fluidCopy != null) {
				if (fluidCopy.isFluidEqual(filledContainer[i])) {
					if (fluidCopy.amount >= filledContainer[i].amount) {
						fluidCopy.amount -= filledContainer[i].amount;
						crafting.setInventorySlotContents(i, recipeSlot[i].copy());
						continue; // Go to the next item in the schematic
					}
				}
			}
			if (recipeSlot[i] != null) {
				for (int j = 2; j < invCopy.length; j++) {
					if (invCopy[j] != null && ItemHelper.craftingEquivalent(invCopy[j], recipeSlot[i], recipeOre[i], recipeOutput)) {
						crafting.setInventorySlotContents(i, invCopy[j].copy());
						invCopy[j].stackSize--;

						if (invCopy[j].getItem().hasContainerItem(invCopy[j])) {
							ItemStack containerStack = invCopy[j].getItem().getContainerItem(invCopy[j]);

							if (containerStack == null) {
								// this is absolutely stupid and nobody should ever make a container item where this gets called
							} else {
								if (containerStack.isItemStackDamageable() && containerStack.getItemDamage() > containerStack.getMaxDamage()) {
									containerStack = null;
								}
								if (containerStack != null
										&& (!invCopy[j].getItem().doesContainerItemLeaveCraftingGrid(invCopy[j]) || !InventoryHelper.addItemStackToInventory(
												invCopy, containerStack, 3))) {
									if (invCopy[j].stackSize <= 0) {
										invCopy[j] = containerStack;
										if (containerStack.stackSize <= 0) {
											invCopy[j].stackSize = 1;
										}
									} else {
										return false;
									}
								}
							}
						}
						if (invCopy[j].stackSize <= 0) {
							invCopy[j] = null;
						}
						found = true;
						break;
					}
				}
				if (!found) {
					return false;
				}

				found = false;
			} else {
				crafting.setInventorySlotContents(i, null);
			}
		}
		// Update the inventories since we can make it.
		inventory = invCopy;

		if (fluidCopy == null || fluidCopy.amount <= 0) {
			fluidCopy = null;
		}
		tank.setFluid(fluidCopy);
		energyStorage.modifyEnergyStored(-PROCESS_ENERGY);
		return true;
	}

	public void updateOutput() {

		if (inventory[0] != null) {
			if (needsCache) {
				recipeOutput = SchematicHelper.getOutput(inventory[0], worldObj);
				for (int i = 0; i < 9; i++) {
					recipeSlot[i] = SchematicHelper.getSchematicSlot(inventory[0], i);
					filledContainer[i] = FluidContainerRegistry.getFluidForFilledItem(recipeSlot[i]);
					recipeOre[i] = SchematicHelper.getSchematicOreSlot(inventory[0], i);
				}
				needsCache = false;
			}
			if (recipeOutput == null) {
				isActive = false;
				return;
			}
			if (canCreate(recipeOutput)) {
				if (createItem()) {
					recipeOutput = ItemHelper.findMatchingRecipe(crafting, worldObj);
					if (recipeOutput != null) {
						if (inventory[1] == null) {
							inventory[1] = recipeOutput.copy();
						} else {
							inventory[1].stackSize += recipeOutput.stackSize;
						}
						transferProducts();
						isActive = true;
					}
				} else {
					if (energyStorage.getEnergyStored() >= PROCESS_ENERGY) {
						needsCraft = false;
					}
					wasActive = true;
					isActive = false;
					return;
				}
			} else {
				if (isActive) {
					wasActive = true;
				}
				isActive = false;
			}
		}
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiAssembler(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerAssembler(inventory, this);
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
		outputTracker = nbt.getInteger("Output");
		needsCraft = true;
		needsCache = true;
		tank.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);
		nbt.setInteger("Output", outputTracker);
		tank.writeToNBT(nbt);
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();
		payload.addFluidStack(getTankFluid());
		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);
		tank.setFluid(payload.getFluidStack());
	}

	/* IInventory */
	@Override
	public void markDirty() {

		needsCraft = true;
		// needsCache = true;
		super.markDirty();
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		needsCraft = true;
		needsCache = needsCache ? true : slot == 0;
		return super.decrStackSize(slot, amount);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		needsCraft = true;
		needsCache = needsCache ? true : slot == 0;

		inventory[slot] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (from == ForgeDirection.UNKNOWN || !sideConfig.allowInsertionSide[sideCache[from.ordinal()]]) {
			return 0;
		}
		int filled = tank.fill(resource, doFill);

		if (doFill && filled > 0) {
			needsCraft = true;
		}
		return filled;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		if (from == ForgeDirection.UNKNOWN || !sideConfig.allowExtractionSide[sideCache[from.ordinal()]]) {
			return null;
		}
		if (resource == null || !resource.isFluidEqual(tank.getFluid())) {
			return null;
		}
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		if (from == ForgeDirection.UNKNOWN || !sideConfig.allowExtractionSide[sideCache[from.ordinal()]]) {
			return null;
		}
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		return true;
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
