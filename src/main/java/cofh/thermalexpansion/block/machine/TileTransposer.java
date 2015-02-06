package cofh.thermalexpansion.block.machine;

import cofh.CoFHCore;
import cofh.core.network.PacketCoFHBase;
import cofh.core.render.IconRegistry;
import cofh.core.util.CoreUtils;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.render.RenderHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.mod.updater.ModVersion;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.client.machine.GuiTransposer;
import cofh.thermalexpansion.gui.container.machine.ContainerTransposer;
import cofh.thermalexpansion.util.crafting.TransposerManager;
import cofh.thermalexpansion.util.crafting.TransposerManager.RecipeTransposer;
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
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;


public class TileTransposer extends TileMachineBase implements IFluidHandler {

	static final int TYPE = BlockMachine.Types.TRANSPOSER.ordinal();

	public static void initialize() {

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numGroup = 5;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, { 0 }, { 2 }, {}, { 2 } };
		defaultSideConfig[TYPE].allowInsertion = new boolean[] { false, true, false, false, false };
		defaultSideConfig[TYPE].allowExtraction = new boolean[] { false, true, true, false, true };
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 1, 2, 3, 4 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 3, 1, 2, 2, 2, 2 };

		int maxPower = MathHelper.clampI(ThermalExpansion.config.get("block.tweak", "Machine.Transposer.BasePower", 40), 10, 500);
		ThermalExpansion.config.set("block.tweak", "Machine.Transposer.BasePower", maxPower);
		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setParamsPower(maxPower);

		sounds[TYPE] = CoreUtils.getSoundName(ThermalExpansion.modId, "blockMachineTransposer");
		enableSound[TYPE] = CoFHCore.configClient.get("sound", "Machine.Transposer", true);

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

		inventory = new ItemStack[1 + 1 + 1 + 1];
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void updateEntity() {

		if (ServerHelper.isClientWorld(worldObj)) {
			if (inventory[1] == null) {
				processRem = 0;
				containerItem = null;
			} else if (FluidHelper.isFluidContainerItem(inventory[1])) {
				containerItem = (IFluidContainerItem) inventory[1].getItem();
			}
			return;
		}
		if (containerItem == null) {
			if (FluidHelper.isFluidContainerItem(inventory[1])) {
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
	protected int getMaxInputSlot() {

		// This is a hack to prevent super() logic from working.
		return -1;
	}

	@Override
	protected boolean canStart() {

		if (inventory[0] == null) {
			return false;
		}
		if (FluidHelper.isFluidContainerItem(inventory[0])) {
			inventory[1] = ItemHelper.cloneStack(inventory[0], 1);
			inventory[0].stackSize--;

			if (inventory[0].stackSize <= 0) {
				inventory[0] = null;
			}
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
			if (inventory[2] == null) {
				return true;
			}
			ItemStack output = recipe.getOutput();

			if (!inventory[2].isItemEqual(output)) {
				return false;
			}
			int result = inventory[2].stackSize + output.stackSize;
			return result <= output.getMaxStackSize();
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
			if (inventory[2] == null) {
				return true;
			}
			ItemStack output = recipe.getOutput();

			if (output == null) {
				return true;
			}
			if (!inventory[2].isItemEqual(output)) {
				return false;
			}
			return inventory[2].stackSize + output.stackSize <= output.getMaxStackSize();
		}
	}

	@Override
	protected boolean hasValidInput() {

		if (containerItem != null) {
			return true;
		}
		RecipeTransposer recipe;

		if (!reverse) {
			recipe = TransposerManager.getFillRecipe(inventory[1], tank.getFluid());
		} else {
			recipe = TransposerManager.getExtractionRecipe(inventory[1]);
		}
		if (recipe == null) {
			return false;
		}
		return recipe.getInput().stackSize <= inventory[1].stackSize;
	}

	@Override
	protected void processStart() {

		int prevID = renderFluid.fluidID;
		RecipeTransposer recipe;

		if (!reverse) {
			recipe = TransposerManager.getFillRecipe(inventory[0], tank.getFluid());
			processMax = recipe.getEnergy();
			renderFluid = tank.getFluid().copy();
		} else {
			recipe = TransposerManager.getExtractionRecipe(inventory[0]);
			processMax = recipe.getEnergy();
			renderFluid = recipe.getFluid();
		}
		renderFluid.amount = 0;
		processRem = processMax;

		inventory[1] = ItemHelper.cloneStack(inventory[0], recipe.getInput().stackSize);
		inventory[0].stackSize -= recipe.getInput().stackSize;

		if (inventory[0].stackSize <= 0) {
			inventory[0] = null;
		}
		if (prevID != renderFluid.fluidID) {
			sendFluidPacket();
		}
	}

	@Override
	protected void processFinish() {

		if (!reverse) {
			RecipeTransposer recipe = TransposerManager.getFillRecipe(inventory[1], tank.getFluid());
			ItemStack output = recipe.getOutput();

			if (inventory[2] == null) {
				inventory[2] = output;
			} else {
				inventory[2].stackSize += output.stackSize;
			}
			inventory[1] = null;
			tank.drain(recipe.getFluid().amount, true);
		} else {
			RecipeTransposer recipe = TransposerManager.getExtractionRecipe(inventory[1]);
			ItemStack output = recipe.getOutput();

			int recipeChance = recipe.getChance();
			if (recipeChance >= 100 || worldObj.rand.nextInt(secondaryChance) < recipeChance) {
				if (inventory[2] == null) {
					inventory[2] = output;
				} else {
					inventory[2].stackSize += output.stackSize;
				}
			}
			inventory[1] = null;
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
		if (containerItem != null) {
			if (inventory[2] == null) {
				inventory[2] = ItemHelper.cloneStack(inventory[1], 1);
				inventory[1] = null;
				containerItem = null;
			} else {
				if (inventory[1].getMaxStackSize() > 1 && ItemHelper.itemsIdentical(inventory[1], inventory[2])
						&& inventory[2].stackSize + 1 <= inventory[2].getMaxStackSize()) {
					inventory[2].stackSize++;
					inventory[1] = null;
					containerItem = null;
				}
			}
		}
		if (containerItem == null && FluidHelper.isFluidContainerItem(inventory[0])) {
			inventory[1] = ItemHelper.cloneStack(inventory[0], 1);
			inventory[0].stackSize--;

			if (inventory[0].stackSize <= 0) {
				inventory[0] = null;
			}
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;

			if (sideCache[side] == 2 || sideCache[side] == 4) {
				if (transferItem(2, 4, side)) {
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
					if (FluidHelper.isFluidContainerItem(inventory[1])) {
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
				if (FluidHelper.isFluidContainerItem(inventory[1])) {
					updateContainerItem();
				}
			}
			if (containerItem != null) {
				isActive = true;
			}
		}
	}

	protected void updateContainerItem() {

		containerItem = (IFluidContainerItem) inventory[1].getItem();
		FluidStack containerStack = FluidHelper.getFluidStackFromContainerItem(inventory[1]);
		int prevID = renderFluid.fluidID;

		if (!reverse) {
			renderFluid = tank.getFluid() == null ? null : tank.getFluid().copy();
		} else {
			renderFluid = tank.getFluid() == null ? containerStack == null ? null : containerStack.copy() : tank.getFluid().copy();
		}
		if (renderFluid == null) {
			renderFluid = new FluidStack(FluidRegistry.WATER, 0);
		} else {
			renderFluid.amount = 0;
		}
		if (!reverse) {
			processMax = containerItem.getCapacity(inventory[1])
					- (containerItem.getFluid(inventory[1]) == null ? 0 : containerItem.getFluid(inventory[1]).amount);
			processRem = processMax;
		} else {
			processMax = containerItem.getFluid(inventory[1]) == null ? 0 : containerItem.getFluid(inventory[1]).amount;
			processRem = processMax;
		}
		if (prevID != renderFluid.fluidID) {
			sendFluidPacket();
		}
	}

	protected void updateContainerFill(int energy) {

		if (energy <= 0) {
			return;
		}
		int amount = Math.min(tank.getFluidAmount(), energy);
		int filled = tank.getFluid() == null ? 0 : containerItem.fill(inventory[1], new FluidStack(tank.getFluid(), amount), true);
		processRem -= filled;
		tank.drain(filled, true);
		energyStorage.modifyEnergyStored(-filled);

		if (containerItem.getFluid(inventory[1]).amount >= containerItem.getCapacity(inventory[1])) {
			transferProducts();
			reverse = reverseFlag;

			if (!redstoneControlOrDisable()) {
				isActive = false;
				wasActive = true;
				tracker.markTime(worldObj);
			}
		}
	}

	protected void updateContainerEmpty(int energy) {

		if (energy <= 0) {
			return;
		}
		int amount = Math.min(tank.getSpace(), energy);
		FluidStack drainStack = containerItem.drain(inventory[1], amount, true);
		int drained = drainStack == null ? 0 : drainStack.amount;
		processRem -= drained;
		tank.fill(drainStack, true);
		energyStorage.modifyEnergyStored(-drained);

		if (processRem <= 0) {
			transferProducts();
			reverse = reverseFlag;

			if (!redstoneControlOrDisable()) {
				isActive = false;
				wasActive = true;
				tracker.markTime(worldObj);
			}
		}
	}

	@Override
	public boolean isItemValid(ItemStack stack, int slot, int side) {

		return slot == 0 ? FluidHelper.isFluidContainerItem(stack) || TransposerManager.isItemValid(stack) : true;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiTransposer(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

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

		// TODO:
		/** PATCH LOGIC for B9 Slot Addition - to be removed in RELEASE */
		String version = nbt.getString("Version");

		if (new ModVersion("", version).compareTo(new ModVersion("", "1.7.10R4.0.0B9")) < 0) {
			inventory[3] = ItemHelper.cloneStack(inventory[2]);
			inventory[2] = ItemHelper.cloneStack(inventory[1]);
			inventory[1] = null;

			if (inventory[0] != null) {
				inventory[1] = ItemHelper.cloneStack(inventory[0], 1);
				inventory[0].stackSize--;

				if (inventory[0].stackSize <= 0) {
					inventory[0] = null;
				}
			}
		}

		if (tank.getFluid() != null) {
			renderFluid = tank.getFluid();
		} else if (TransposerManager.getExtractionRecipe(inventory[1]) != null) {
			renderFluid = TransposerManager.getExtractionRecipe(inventory[1]).getFluid();
			renderFluid.amount = 0;
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
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addFluidStack(renderFluid);
		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

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
	public PacketCoFHBase getFluidPacket() {

		PacketCoFHBase payload = super.getFluidPacket();

		payload.addFluidStack(renderFluid);

		return payload;
	}

	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addBool(reverseFlag);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		reverse = payload.getBool();
		reverseFlag = payload.getBool();
		tank.setFluid(payload.getFluidStack());
	}

	@Override
	protected void handleFluidPacket(PacketCoFHBase payload) {

		super.handleFluidPacket(payload);

		renderFluid = payload.getFluidStack();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

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
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

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

		if (ServerHelper.isServerWorld(worldObj) && slot == 1) {
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

		if (ServerHelper.isServerWorld(worldObj) && slot == 1) {
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
