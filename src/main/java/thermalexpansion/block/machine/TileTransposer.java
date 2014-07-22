package thermalexpansion.block.machine;

import cofh.network.CoFHPacket;
import cofh.render.IconRegistry;
import cofh.render.RenderHelper;
import cofh.util.FluidHelper;
import cofh.util.ItemHelper;
import cofh.util.MathHelper;
import cofh.util.ServerHelper;
import cofh.util.fluid.FluidTankAdv;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.client.machine.GuiTransposer;
import thermalexpansion.gui.container.machine.ContainerTransposer;
import thermalexpansion.util.crafting.TransposerManager;
import thermalexpansion.util.crafting.TransposerManager.RecipeTransposer;

public class TileTransposer extends TileMachineBase implements IFluidHandler {

	static final int TYPE = BlockMachine.Types.TRANSPOSER.ordinal();

	public static void initialize() {

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numGroup = 5;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1 }, {}, { 1 } };
		defaultSideConfig[TYPE].allowInsertion = new boolean[] { false, true, false, false, false };
		defaultSideConfig[TYPE].allowExtraction = new boolean[] { false, true, true, false, true };
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 1, 2, 3, 4 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 3, 1, 2, 2, 2, 2 };

		int maxPower = MathHelper.clampI(ThermalExpansion.config.get("block.tweak", "Machine.Transposer.BasePower", 40), 10, 500);
		ThermalExpansion.config.set("block.tweak", "Machine.Transposer.BasePower", maxPower);
		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setParamsPower(maxPower);

		GameRegistry.registerTileEntity(TileTransposer.class, "thermalexpansion.Transposer");
	}

	int outputTracker;
	int outputTrackerFluid;
	FluidStack outputBuffer;
	FluidStack renderFluid = new FluidStack(FluidRegistry.WATER, 0);
	FluidTankAdv tank = new FluidTankAdv(TEProps.MAX_FLUID_LARGE);
	IFluidContainerItem containerItem = null;

	public boolean reverseFlag;
	public boolean reverse;

	public TileTransposer() {

		super();

		inventory = new ItemStack[1 + 1 + 1];
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void updateEntity() {

		if (ServerHelper.isClientWorld(worldObj)) {
			if (inventory[0] == null) {
				processRem = 0;
			}
			return;
		}
		if (containerItem == null) {
			if (FluidHelper.isFluidContainerItem(inventory[0])) {
				updateContainerItem();
			}
		}
		if (reverse) {
			transferFluid();
		}
		if (containerItem != null) {
			boolean curActive = isActive;
			processContainerItem();
			updateIfChanged(curActive);
			chargeEnergy();
		} else {
			super.updateEntity();
		}
	}

	@Override
	protected boolean canStart() {

		if (inventory[0] == null) {
			return false;
		}
		if (!reverse) {
			if (tank.getFluidAmount() <= 0) {
				return false;
			}
			RecipeTransposer recipe = TransposerManager.getFillRecipe(inventory[0], tank.getFluid());

			if (recipe == null || tank.getFluidAmount() < recipe.getFluid().amount || energyStorage.getEnergyStored() < recipe.getEnergy()) {
				return false;
			}
			if (inventory[0].stackSize < recipe.getInput().stackSize) {
				return false;
			}
			if (inventory[1] == null) {
				return true;
			}
			ItemStack output = recipe.getOutput();

			if (!inventory[1].isItemEqual(output)) {
				return false;
			}
			int result = inventory[1].stackSize + output.stackSize;
			return inventory[1].stackSize + output.stackSize <= output.getMaxStackSize();
		} else {
			RecipeTransposer recipe = TransposerManager.getExtractionRecipe(inventory[0]);

			if (recipe == null || energyStorage.getEnergyStored() < recipe.getEnergy()) {
				return false;
			}
			if (inventory[0].stackSize < recipe.getInput().stackSize) {
				return false;
			}
			if (tank.fill(recipe.getFluid(), false) != recipe.getFluid().amount) {
				return false;
			}
			if (inventory[1] == null) {
				return true;
			}
			ItemStack output = recipe.getOutput();

			if (output == null) {
				return true;
			}
			if (!inventory[1].isItemEqual(output)) {
				return false;
			}
			return inventory[1].stackSize + output.stackSize <= output.getMaxStackSize();
		}
	}

	@Override
	protected boolean hasValidInput() {

		if (containerItem != null) {
			return true;
		}
		RecipeTransposer recipe;

		if (!reverse) {
			recipe = TransposerManager.getFillRecipe(inventory[0], tank.getFluid());
		} else {
			recipe = TransposerManager.getExtractionRecipe(inventory[0]);
		}
		if (recipe == null) {
			return false;
		}
		return recipe.getInput().stackSize <= inventory[0].stackSize;
	}

	@Override
	protected void processStart() {

		int prevID = renderFluid.fluidID;

		if (!reverse) {
			processMax = TransposerManager.getFillRecipe(inventory[0], tank.getFluid()).getEnergy();
			renderFluid = tank.getFluid().copy();
		} else {
			processMax = TransposerManager.getExtractionRecipe(inventory[0]).getEnergy();
			renderFluid = TransposerManager.getExtractionRecipe(inventory[0]).getFluid();
		}
		renderFluid.amount = 0;
		processRem = processMax;

		if (prevID != renderFluid.fluidID) {
			sendFluidPacket();
		}
	}

	@Override
	protected void processFinish() {

		if (!reverse) {
			RecipeTransposer recipe = TransposerManager.getFillRecipe(inventory[0], tank.getFluid());
			ItemStack output = recipe.getOutput();

			if (inventory[1] == null) {
				inventory[1] = output;
			} else {
				inventory[1].stackSize += output.stackSize;
			}
			inventory[0].stackSize--;

			if (inventory[0].stackSize <= 0) {
				inventory[0] = null;
			}
			tank.drain(recipe.getFluid().amount, true);
		} else {
			RecipeTransposer recipe = TransposerManager.getExtractionRecipe(inventory[0]);
			ItemStack output = recipe.getOutput();

			if (worldObj.rand.nextInt(secondaryChance) < recipe.getChance()) {
				if (inventory[1] == null) {
					inventory[1] = output;
				} else {
					inventory[1].stackSize += output.stackSize;
				}
			}
			inventory[0].stackSize--;

			if (inventory[0].stackSize <= 0) {
				inventory[0] = null;
			}
			tank.fill(recipe.getFluid(), true);
		}
		reverse = reverseFlag;
	}

	protected void transferFluid() {

		if (!augmentAutoTransfer) {
			return;
		}
		if (tank.getFluidAmount() <= 0) {
			return;
		}
		int side;
		outputBuffer = new FluidStack(tank.getFluid(), Math.min(tank.getFluidAmount(), RATE));
		for (int i = outputTrackerFluid + 1; i <= outputTrackerFluid + 6; i++) {
			side = i % 6;

			if (sideCache[side] == 3 || sideCache[side] == 4) {
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
	protected void transferProducts() {

		if (!augmentAutoTransfer) {
			return;
		}
		if (inventory[1] == null) {
			if (containerItem != null) {
				inventory[1] = ItemHelper.cloneStack(inventory[0], 1);
				inventory[0] = ItemHelper.consumeItem(inventory[0]);
				containerItem = null;
			} else {
				return;
			}
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;

			if (sideCache[side] == 2 || sideCache[side] == 4) {
				if (transferItem(1, 4, side)) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	protected void processContainerItem() {

		if (isActive) {
			int energy = Math.min(energyStorage.getEnergyStored(), calcEnergy() * energyMod);

			if (!reverse) {
				updateContainerFill(energy * processMod);
			} else {
				updateContainerEmpty(energy * processMod);
			}
			if (!redstoneControlOrDisable()) {
				isActive = false;
				wasActive = true;
				tracker.markTime(worldObj);
			} else {
				if (containerItem == null) {
					if (FluidHelper.isFluidContainerItem(inventory[0])) {
						updateContainerItem();
						isActive = true;
					} else {
						isActive = false;
						wasActive = true;
						tracker.markTime(worldObj);
					}
				}
			}
		} else if (redstoneControlOrDisable()) {
			if (timeCheck()) {
				transferProducts();
			}
			if (containerItem == null) {
				if (FluidHelper.isFluidContainerItem(inventory[0])) {
					updateContainerItem();
				}
			}
			if (containerItem != null) {
				isActive = true;
			}
		}
	}

	protected void updateContainerItem() {

		FluidStack containerStack = FluidHelper.getFluidStackFromContainerItem(inventory[0]);
		int prevID = renderFluid.fluidID;

		if (!reverse) {
			if (containerStack == null || FluidHelper.isFluidEqual(containerStack, tank.getFluid())) {
				containerItem = (IFluidContainerItem) inventory[0].getItem();
				renderFluid = tank.getFluid() == null ? null : tank.getFluid().copy();
			}
		} else {
			if (containerStack != null && tank.getFluid() == null || FluidHelper.isFluidEqual(containerStack, tank.getFluid())) {
				containerItem = (IFluidContainerItem) inventory[0].getItem();
				renderFluid = containerStack;
			}
		}
		if (renderFluid == null) {
			renderFluid = new FluidStack(FluidRegistry.WATER, 0);
		} else {
			renderFluid.amount = 0;
		}
		if (containerItem != null) {
			processMax = 192;
			processRem = 1;

			if (prevID != renderFluid.fluidID) {
				sendFluidPacket();
			}
		}
	}

	protected void updateContainerFill(int energy) {

		if (energy <= 0) {
			return;
		}
		int amount = Math.min(tank.getFluidAmount(), energy);
		int filled = tank.getFluid() == null ? 0 : containerItem.fill(inventory[0], new FluidStack(tank.getFluid(), amount), true);

		if (filled == 0) {
			processRem = 0;
			reverse = reverseFlag;
			transferProducts();

			if (!redstoneControlOrDisable()) {
				isActive = false;
				wasActive = true;
				tracker.markTime(worldObj);
			}
		} else {
			tank.drain(filled, true);
			energyStorage.modifyEnergyStored(-amount);
		}
	}

	protected void updateContainerEmpty(int energy) {

		if (energy <= 0) {
			return;
		}
		FluidStack drained = null;
		int amount = Math.min(tank.getSpace(), energy);

		if (amount > 0) {
			drained = containerItem.drain(inventory[0], amount, true);
		}
		if (drained == null) {
			processRem = 0;
			reverse = reverseFlag;
			transferProducts();

			if (!redstoneControlOrDisable()) {
				isActive = false;
				wasActive = true;
				tracker.markTime(worldObj);
			}
		} else {
			tank.fill(drained, true);
			energyStorage.modifyEnergyStored(-drained.amount);
		}
	}

	@Override
	public boolean isItemValid(ItemStack stack, int slot, int side) {

		return slot == 0 ? FluidHelper.isFluidContainerItem(stack) || TransposerManager.isItemValid(stack) : true;
	}

	/* GUI METHODS */
	@Override
	public GuiContainer getGuiClient(InventoryPlayer inventory) {

		return new GuiTransposer(inventory, this);
	}

	@Override
	public Container getGuiServer(InventoryPlayer inventory) {

		return new ContainerTransposer(inventory, this);
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

		outputTracker = nbt.getInteger("Tracker1");
		outputTrackerFluid = nbt.getInteger("Tracker2");
		reverse = nbt.getBoolean("Rev");
		reverseFlag = reverse;
		tank.readFromNBT(nbt);

		if (tank.getFluid() != null) {
			renderFluid = tank.getFluid();
		} else if (TransposerManager.getExtractionRecipe(inventory[0]) != null) {
			renderFluid = TransposerManager.getExtractionRecipe(inventory[0]).getFluid();
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("Tracker1", outputTracker);
		nbt.setInteger("Tracker2", outputTrackerFluid);
		nbt.setBoolean("Rev", reverse);
		tank.writeToNBT(nbt);
	}

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();

		payload.addFluidStack(renderFluid);
		return payload;
	}

	@Override
	public CoFHPacket getGuiPacket() {

		CoFHPacket payload = super.getGuiPacket();

		payload.addBool(reverse);
		payload.addBool(reverseFlag);

		if (tank.getFluid() == null) {
			payload.addFluidStack(renderFluid);
		} else {
			payload.addFluidStack(tank.getFluid());
		}
		return payload;
	}

	@Override
	public CoFHPacket getFluidPacket() {

		CoFHPacket payload = super.getFluidPacket();

		payload.addFluidStack(renderFluid);

		return payload;
	}

	@Override
	public CoFHPacket getModePacket() {

		CoFHPacket payload = super.getModePacket();

		payload.addBool(reverseFlag);

		return payload;
	}

	@Override
	protected void handleGuiPacket(CoFHPacket payload) {

		super.handleGuiPacket(payload);

		reverse = payload.getBool();
		reverseFlag = payload.getBool();
		tank.setFluid(payload.getFluidStack());
	}

	@Override
	protected void handleFluidPacket(CoFHPacket payload) {

		super.handleFluidPacket(payload);

		renderFluid = payload.getFluidStack();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	protected void handleModePacket(CoFHPacket payload) {

		super.handleModePacket(payload);

		reverseFlag = payload.getBool();
		if (!isActive) {
			reverse = reverseFlag;
		}
		callNeighborTileChange();
	}

	public void setMode(boolean mode) {

		boolean lastFlag = reverseFlag;
		reverseFlag = mode;
		sendModePacket();
		reverseFlag = lastFlag;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {
			renderFluid = payload.getFluidStack();
		} else {
			payload.getFluidStack();
		}
	}

	/* IInventory */
	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		ItemStack stack = super.decrStackSize(slot, amount);

		if (ServerHelper.isServerWorld(worldObj) && slot <= getMaxInputSlot()) {
			if (isActive && (inventory[slot] == null || !hasValidInput())) {
				isActive = false;
				wasActive = true;
				tracker.markTime(worldObj);
				processRem = 0;
				containerItem = null;
				reverse = reverseFlag;
			}
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		if (ServerHelper.isServerWorld(worldObj) && slot <= getMaxInputSlot()) {
			if (isActive && inventory[slot] != null) {
				if (stack == null || !stack.isItemEqual(inventory[slot]) || !hasValidInput()) {
					isActive = false;
					wasActive = true;
					tracker.markTime(worldObj);
					processRem = 0;
				}
			}
			containerItem = null;
			reverse = reverseFlag;
		}
		inventory[slot] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public void markDirty() {

		if (isActive && !hasValidInput()) {
			isActive = false;
			wasActive = true;
			tracker.markTime(worldObj);
			processRem = 0;
			containerItem = null;
			reverse = reverseFlag;
		}
		super.markDirty();
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (reverse || from == ForgeDirection.UNKNOWN || sideCache[from.ordinal()] != 1) {
			return 0;
		}
		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		if (!reverse || from == ForgeDirection.UNKNOWN || sideCache[from.ordinal()] != 3) {
			return null;
		}
		return tank.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		if (!reverse || from == ForgeDirection.UNKNOWN || sideCache[from.ordinal()] != 3) {
			return null;
		}
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		if (from == ForgeDirection.UNKNOWN) {
			return false;
		}
		return !reverse && sideCache[from.ordinal()] == 1;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		if (from == ForgeDirection.UNKNOWN) {
			return false;
		}
		return reverse && sideCache[from.ordinal()] == 3;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return new FluidTankInfo[] { tank.getInfo() };
	}

	/* ISidedTexture */
	@Override
	public IIcon getTexture(int side, int pass) {

		if (pass == 0) {
			if (side == 0) {
				return IconRegistry.getIcon("MachineBottom");
			} else if (side == 1) {
				return IconRegistry.getIcon("MachineTop");
			}
			return side != facing ? IconRegistry.getIcon("MachineSide") : isActive ? RenderHelper.getFluidTexture(renderFluid) : IconRegistry.getIcon(
					"MachineFace", getType());
		} else {
			return side != facing ? IconRegistry.getIcon(TEProps.textureSelection, sideConfig.sideTex[sideCache[side]]) : isActive ? IconRegistry.getIcon(
					"MachineActive", getType()) : IconRegistry.getIcon("MachineFace", getType());
		}
	}

}
