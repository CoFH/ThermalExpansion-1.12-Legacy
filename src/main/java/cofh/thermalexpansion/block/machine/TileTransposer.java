package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.render.RenderHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.machine.GuiTransposer;
import cofh.thermalexpansion.gui.container.machine.ContainerTransposer;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.util.crafting.TransposerManager;
import cofh.thermalexpansion.util.crafting.TransposerManager.RecipeTransposer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class TileTransposer extends TileMachineBase {

	private static final int TYPE = BlockMachine.Type.TRANSPOSER.getMetadata();

	public static void initialize() {

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numConfig = 6;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, { 0 }, { 2 }, {}, { 2 }, { 0, 2 } };
		defaultSideConfig[TYPE].allowInsertionSide = new boolean[] { false, true, false, false, false, true };
		defaultSideConfig[TYPE].allowExtractionSide = new boolean[] { false, true, true, false, true, true };
		defaultSideConfig[TYPE].allowInsertionSlot = new boolean[] { true, false, false, false };
		defaultSideConfig[TYPE].allowExtractionSlot = new boolean[] { true, false, true, false };
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 1, 2, 3, 4, 7 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 3, 1, 2, 2, 2, 2 };

		validAugments[TYPE] = new ArrayList<String>();

		GameRegistry.registerTileEntity(TileTransposer.class, "thermalexpansion:machine_transposer");

		config();
	}

	public static void config() {

		String category = "Machine.Transposer";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setDefaultParams(20);
	}

	private int inputTracker;
	private int outputTracker;
	private int outputTrackerFluid;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_LARGE);
	private FluidStack renderFluid = new FluidStack(FluidRegistry.WATER, 0);
	private boolean hasFluidHandler = false;

	// TODO : Use this instead of reverse?
	public byte modeFlag;
	private byte mode;

	public boolean extractFlag;
	public boolean extractMode;

	public TileTransposer() {

		super();
		inventory = new ItemStack[1 + 1 + 1 + 1];
	}

	@Override
	public int getType() {

		return TYPE;
	}

	/* HANDLER */
	private void updateHandler() {

		boolean curActive = isActive;

		if (isActive) {
			processTick();

			if (processRem <= 0) {
				if (processFinishHandler()) {
					transferOutput();
					transferInput();
				}
				energyStorage.modifyEnergyStored(-processRem);

				if (!redstoneControlOrDisable() || !canStartHandler()) {
					processOff();
				} else {
					processStartHandler();
				}
			}
		} else if (redstoneControlOrDisable()) {
			if (timeCheck()) {
				transferOutput();
				transferInput();
			}
			if (timeCheckEighth() && canStartHandler()) {
				processStartHandler();
				processTick();
				isActive = true;
			}
		}
		updateIfChanged(curActive);
		chargeEnergy();
	}

	private boolean canStartHandler() {

		if (!FluidHelper.isFluidHandler(inventory[1])) {
			hasFluidHandler = false;
			return false;
		}
		if (energyStorage.getEnergyStored() < TransposerManager.DEFAULT_ENERGY) {
			return false;
		}
		IFluidHandler handler = inventory[1].getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

		if (!extractMode) {
			if (tank.getFluid() == null || tank.getFluidAmount() < Fluid.BUCKET_VOLUME) {
				return false;
			}
			return handler.fill(new FluidStack(tank.getFluid(), Fluid.BUCKET_VOLUME), false) > 0;
		} else {
			if (tank.getSpace() < Fluid.BUCKET_VOLUME) {
				return false;
			}
			FluidStack drain = handler.drain(Fluid.BUCKET_VOLUME, false);
			return tank.fill(drain, false) > 0;
		}
	}

	private void processStartHandler() {

		IFluidHandler handler = inventory[1].getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
		IFluidTankProperties[] tankProperties = handler.getTankProperties();

		FluidStack handlerStack = tankProperties[0].getContents();
		String prevID = renderFluid.getFluid().getName();

		if (!extractMode) {
			renderFluid = tank.getFluid() == null ? null : tank.getFluid().copy();
		} else {
			renderFluid = tank.getFluid() == null ? handlerStack == null ? null : handlerStack.copy() : tank.getFluid().copy();
		}
		if (renderFluid == null) {
			renderFluid = new FluidStack(FluidRegistry.WATER, 0);
		} else {
			renderFluid.amount = 0;
		}
		processMax = TransposerManager.DEFAULT_ENERGY * energyMod / ENERGY_BASE;
		processRem = processMax;

		if (!prevID.equals(renderFluid.getFluid().getName())) {
			sendFluidPacket();
		}
	}

	private boolean processFinishHandler() {

		if (!extractMode) {
			return fillHandler();
		} else {
			return emptyHandler();
		}
	}

	private boolean fillHandler() {

		IFluidHandler handler = inventory[1].getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
		int filled = tank.getFluid() == null ? 0 : handler.fill(new FluidStack(tank.getFluid(), Fluid.BUCKET_VOLUME), true);

		IFluidTankProperties[] tankProperties = handler.getTankProperties();

		if (filled > 0) {
			tank.drain(filled, true);
			if (tankProperties[0].getContents().amount >= tankProperties[0].getCapacity()) {
				extractMode = extractFlag;
				return true;
			}
			return false;
		}
		return true;
	}

	private boolean emptyHandler() {

		IFluidHandler handler = inventory[1].getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
		FluidStack drainStack = handler.drain(Fluid.BUCKET_VOLUME, true);
		int drained = drainStack == null ? 0 : drainStack.amount;

		IFluidTankProperties[] tankProperties = handler.getTankProperties();

		if (drained > 0) {
			tank.fill(drainStack, true);
			if (tankProperties[0].getContents() == null) {
				extractMode = extractFlag;
				return true;
			}
			return false;
		}
		return true;
	}

	/* STANDARD */
	@Override
	public void update() {

		if (ServerHelper.isClientWorld(worldObj)) {
			if (inventory[1] == null) {
				processRem = 0;
				hasFluidHandler = false;
			} else if (FluidHelper.isFluidHandler(inventory[1])) {
				hasFluidHandler = true;
			}
			return;
		}
		if (extractMode) {
			transferOutputFluid();
		}
		if (hasFluidHandler) {
			updateHandler();
		} else {
			super.update();
		}
	}

	@Override
	public int getLightValue() {

		return isActive ? renderFluid.getFluid().getLuminosity(renderFluid) : 0;
	}

	@Override
	protected int getMaxInputSlot() {

		// This is a hack to prevent super() logic from working.
		return -1;
	}

	@Override
	protected boolean canStart() {

		if (inventory[0] == null || energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		if (!hasFluidHandler && FluidHelper.isFluidHandler(inventory[0])) {
			inventory[1] = ItemHelper.cloneStack(inventory[0], 1);
			inventory[0].stackSize--;

			if (inventory[0].stackSize <= 0) {
				inventory[0] = null;
			}
			hasFluidHandler = true;
			return false;
		}
		if (!extractMode) {
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
			RecipeTransposer recipe = TransposerManager.getExtractRecipe(inventory[0]);

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

		if (hasFluidHandler) {
			return true;
		}
		RecipeTransposer recipe;

		if (!extractMode) {
			recipe = TransposerManager.getFillRecipe(inventory[1], tank.getFluid());
		} else {
			recipe = TransposerManager.getExtractRecipe(inventory[1]);
		}
		if (recipe == null) {
			return false;
		}
		return recipe.getInput().stackSize <= inventory[1].stackSize;
	}

	@Override
	protected void processStart() {

		String prevID = renderFluid.getFluid().getName();
		RecipeTransposer recipe;

		if (!extractMode) {
			recipe = TransposerManager.getFillRecipe(inventory[0], tank.getFluid());
			processMax = recipe.getEnergy() * energyMod / ENERGY_BASE;
			renderFluid = tank.getFluid().copy();
		} else {
			recipe = TransposerManager.getExtractRecipe(inventory[0]);
			processMax = recipe.getEnergy() * energyMod / ENERGY_BASE;
			renderFluid = recipe.getFluid().copy();
		}
		renderFluid.amount = 0;
		processRem = processMax;

		inventory[1] = ItemHelper.cloneStack(inventory[0], recipe.getInput().stackSize);
		inventory[0].stackSize -= recipe.getInput().stackSize;

		if (inventory[0].stackSize <= 0) {
			inventory[0] = null;
		}
		if (!prevID.equals(renderFluid.getFluid().getName())) {
			sendFluidPacket();
		}
	}

	@Override
	protected void processFinish() {

		if (!extractMode) {
			RecipeTransposer recipe = TransposerManager.getFillRecipe(inventory[1], tank.getFluid());

			if (recipe == null) {
				processOff();
				return;
			}
			ItemStack output = recipe.getOutput();
			if (inventory[2] == null) {
				inventory[2] = ItemHelper.cloneStack(output);
			} else {
				inventory[2].stackSize += output.stackSize;
			}
			inventory[1] = null;
			tank.drain(recipe.getFluid().amount, true);
		} else {
			RecipeTransposer recipe = TransposerManager.getExtractRecipe(inventory[1]);

			if (recipe == null) {
				processOff();
				return;
			}
			ItemStack output = recipe.getOutput();
			int recipeChance = recipe.getChance();
			if (recipeChance >= 100 || worldObj.rand.nextInt(secondaryChance) < recipeChance) {
				if (inventory[2] == null) {
					inventory[2] = ItemHelper.cloneStack(output);
				} else {
					inventory[2].stackSize += output.stackSize;
				}
			}
			inventory[1] = null;
			tank.fill(recipe.getFluid(), true);
		}
		extractMode = extractFlag;
	}

	@Override
	protected void transferInput() {

		if (!enableAutoInput) {
			return;
		}
		int side;
		for (int i = inputTracker + 1; i <= inputTracker + 6; i++) {
			side = i % 6;
			if (sideCache[side] == 1) {
				if (extractItem(0, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					inputTracker = side;
					break;
				}
			}
		}
	}

	@Override
	protected void transferOutput() {

		transferHandler();

		if (!enableAutoOutput) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;

			if (sideCache[side] == 2 || sideCache[side] == 4) {
				if (transferItem(2, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	private void transferOutputFluid() {

		if (!enableAutoOutput) {
			return;
		}
		if (tank.getFluidAmount() <= 0) {
			return;
		}
		int side;
		FluidStack outputBuffer = new FluidStack(tank.getFluid(), Math.min(tank.getFluidAmount(), FLUID_TRANSFER[level]));
		for (int i = outputTrackerFluid + 1; i <= outputTrackerFluid + 6; i++) {
			side = i % 6;

			if (sideCache[side] == 3 || sideCache[side] == 4) {
				int toDrain = FluidHelper.insertFluidIntoAdjacentFluidHandler(this, EnumFacing.VALUES[side], outputBuffer, true);

				if (toDrain > 0) {
					tank.drain(toDrain, true);
					outputTrackerFluid = side;
					break;
				}
			}
		}
	}

	private void transferHandler() {

		if (hasFluidHandler) {
			if (inventory[2] == null) {
				inventory[2] = ItemHelper.cloneStack(inventory[1], 1);
				inventory[1] = null;
				hasFluidHandler = false;
			} else {
				if (ItemHelper.itemsIdentical(inventory[1], inventory[2]) && inventory[1].getMaxStackSize() > 1 && inventory[2].stackSize + 1 <= inventory[2].getMaxStackSize()) {
					inventory[2].stackSize++;
					inventory[1] = null;
					hasFluidHandler = false;
				}
			}
		}
		if (!hasFluidHandler && FluidHelper.isFluidHandler(inventory[0])) {
			inventory[1] = ItemHelper.cloneStack(inventory[0], 1);
			inventory[0].stackSize--;

			if (inventory[0].stackSize <= 0) {
				inventory[0] = null;
			}
			hasFluidHandler = true;
		}
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
	public FluidTankCore getTank() {

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
		outputTracker = nbt.getInteger("TrackOut1");
		outputTrackerFluid = nbt.getInteger("TrackOut2");

		if (inventory[1] != null && inventory[1].hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
			hasFluidHandler = true;
		}
		extractMode = nbt.getByte("Mode") == 1;
		extractFlag = extractMode;
		tank.readFromNBT(nbt);

		if (tank.getFluid() != null) {
			renderFluid = tank.getFluid().copy();
		} else if (TransposerManager.getExtractRecipe(inventory[1]) != null) {
			renderFluid = TransposerManager.getExtractRecipe(inventory[1]).getFluid().copy();
			renderFluid.amount = 0;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut1", outputTracker);
		nbt.setInteger("TrackOut2", outputTrackerFluid);
		nbt.setByte("Mode", extractMode ? (byte) 1 : 0);
		tank.writeToNBT(nbt);
		return nbt;
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

		payload.addBool(extractMode);
		payload.addBool(extractFlag);

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

		payload.addBool(extractFlag);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		extractMode = payload.getBool();
		extractFlag = payload.getBool();
		tank.setFluid(payload.getFluidStack());
	}

	@Override
	protected void handleFluidPacket(PacketCoFHBase payload) {

		super.handleFluidPacket(payload);

		renderFluid = payload.getFluidStack();
		callBlockUpdate();
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		extractFlag = payload.getBool();
		if (!isActive) {
			extractMode = extractFlag;
		}
		markDirty();
		callNeighborTileChange();
	}

	public void setMode(boolean mode) {

		boolean lastFlag = extractFlag;
		extractFlag = mode;
		sendModePacket();
		extractFlag = lastFlag;
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
				processOff();
				extractMode = extractFlag;
			}
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		if (ServerHelper.isServerWorld(worldObj) && slot == 1) {
			if (isActive && inventory[slot] != null) {
				if (stack == null || !stack.isItemEqual(inventory[slot]) || !hasValidInput()) {
					processOff();
				}
			}
			hasFluidHandler = false;
			extractMode = extractFlag;
		}
		inventory[slot] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public void markDirty() {

		if (isActive && !hasValidInput()) {
			extractMode = extractFlag;
		}
		super.markDirty();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot != 0 || (FluidHelper.isFluidHandler(stack) || TransposerManager.isItemValid(stack));
	}

	/* ISidedTexture */
	@Override
	public TextureAtlasSprite getTexture(int side, int layer, int pass) {

		if (layer == 0) {
			if (side == 0) {
				return TETextures.MACHINE_BOTTOM;
			} else if (side == 1) {
				return TETextures.MACHINE_TOP;
			}
			return side != facing ? TETextures.MACHINE_SIDE : isActive ? RenderHelper.getFluidTexture(renderFluid) : TETextures.MACHINE_FACE[getType()];
		} else if (side < 6) {
			return side != facing ? TETextures.CONFIG[sideConfig.sideTex[sideCache[side]]] : isActive ? TETextures.MACHINE_ACTIVE[getType()] : TETextures.MACHINE_FACE[getType()];
		}
		return TETextures.MACHINE_SIDE;
	}

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return super.hasCapability(capability, from) || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler() {
				@Override
				public IFluidTankProperties[] getTankProperties() {

					FluidTankInfo info = tank.getInfo();
					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, from != null && !extractMode && sideCache[from.ordinal()] == 1, from != null && extractMode && sideCache[from.ordinal()] == 3) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (extractMode || from == null || sideCache[from.ordinal()] != 1) {
						return 0;
					}
					return tank.fill(resource, doFill);
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (!extractMode || from == null || sideCache[from.ordinal()] != 3) {
						return null;
					}
					return tank.drain(resource, doDrain);
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (!extractMode || from == null || sideCache[from.ordinal()] != 3) {
						return null;
					}
					return tank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
