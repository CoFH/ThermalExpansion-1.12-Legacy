package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine.Type;
import cofh.thermalexpansion.gui.client.machine.GuiTransposer;
import cofh.thermalexpansion.gui.container.machine.ContainerTransposer;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TESounds;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager.ContainerOverride;
import cofh.thermalexpansion.util.managers.machine.TransposerManager.TransposerRecipe;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.*;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;

public class TileTransposer extends TileMachineBase {

	private static final int TYPE = Type.TRANSPOSER.getMetadata();
	public static int basePower = 20;

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 7;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 2 }, {}, { 2 }, { 0, 2 }, { 0, 2 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_PRIMARY, OUTPUT_SECONDARY, OUTPUT_ALL, OPEN, OMNI };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 3, 1, 2, 2, 2, 2 };

		ALT_SIDE_CONFIGS[TYPE] = new SideConfig();
		ALT_SIDE_CONFIGS[TYPE].numConfig = 2;
		ALT_SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 2 }, {}, { 2 }, { 0, 2 }, { 0, 2 } };
		ALT_SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, OPEN };
		ALT_SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 1, 1, 1, 1 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, false, false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false, false, true, false };

		VALID_AUGMENTS[TYPE] = new HashSet<>();

		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_SECONDARY);

		GameRegistry.registerTileEntity(TileTransposer.class, "thermalexpansion:machine_transposer");

		config();
	}

	public static void config() {

		String category = "Machine.Transposer";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy consumption (in RF/t) for a Fluid Transposer. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower, smallStorage);
	}

	private int inputTracker;
	private int outputTracker;
	private int outputTrackerFluid;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_LARGE);
	private FluidStack renderFluid = new FluidStack(FluidRegistry.WATER, 0);
	private boolean hasFluidHandler = false;

	public boolean extractMode;
	public boolean extractFlag;

	public TileTransposer() {

		super();
		inventory = new ItemStack[1 + 1 + 1 + 1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);
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
					transferHandler();
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
		if (energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		if (!inventory[2].isEmpty()) {
			ContainerOverride override = TransposerManager.getContainerOverride(inventory[1]);
			if (override == null || !inventory[2].isItemEqual(override.getOutput())) {
				return false;
			}
		}
		IFluidHandlerItem handler = inventory[1].getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

		if (handler == null) {
			return false;
		}
		if (!extractMode) {
			return tank.getFluid() != null && tank.getFluidAmount() > 0 && handler.fill(new FluidStack(tank.getFluid(), Math.min(tank.getFluidAmount(), Fluid.BUCKET_VOLUME)), false) > 0;
		} else {
			return tank.fill(handler.drain(Math.min(tank.getSpace(), Fluid.BUCKET_VOLUME), false), false) > 0;
		}
	}

	private void processStartHandler() {

		IFluidHandlerItem handler = inventory[1].getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
		IFluidTankProperties[] tankProperties = handler.getTankProperties();

		FluidStack handlerStack = tankProperties[0].getContents();
		FluidStack prevStack = renderFluid.copy();

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

		if (!FluidHelper.isFluidEqual(prevStack, renderFluid)) {
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

		IFluidHandlerItem handler = inventory[1].getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
		int filled = tank.getFluid() == null ? 0 : handler.fill(new FluidStack(tank.getFluid(), Math.min(tank.getFluidAmount(), Fluid.BUCKET_VOLUME)), true);

		IFluidTankProperties[] tankProperties = handler.getTankProperties();

		if (tankProperties == null || tankProperties.length < 1) {
			return true;
		}
		if (filled > 0) {
			tank.drain(filled, true);
			inventory[1] = handler.getContainer();
			return tankProperties[0].getContents() != null && tankProperties[0].getContents().amount >= tankProperties[0].getCapacity();
		}
		return true;
	}

	private boolean emptyHandler() {

		IFluidHandlerItem handler = inventory[1].getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
		ItemStack testStack = ItemHelper.cloneStack(inventory[1]);
		FluidStack drainStack = handler.drain(Math.min(tank.getSpace(), Fluid.BUCKET_VOLUME), true);
		int drained = drainStack == null ? 0 : drainStack.amount;

		IFluidTankProperties[] tankProperties = handler.getTankProperties();

		if (tankProperties == null || tankProperties.length < 1) {
			return true;
		}
		if (drained > 0) {
			tank.fill(drainStack, true);
			if (tankProperties[0].getContents() == null) {
				ContainerOverride override = TransposerManager.getContainerOverride(testStack);
				if (override != null) {
					int chance = override.getChance();
					if (chance >= 100 || world.rand.nextInt(secondaryChance) < chance) {
						inventory[1] = ItemHelper.cloneStack(override.getOutput());
					} else {
						inventory[1] = handler.getContainer();
					}
				} else {
					inventory[1] = handler.getContainer();
				}
				if (inventory[1].getCount() <= 0) {
					inventory[1] = ItemStack.EMPTY;
				}
				return true;
			}
			inventory[1] = handler.getContainer();
			return false;
		}
		return true;
	}

	/* STANDARD */
	@Override
	public void update() {

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

		if (inventory[0].isEmpty() || !inventory[1].isEmpty() || energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		if (!hasFluidHandler && FluidHelper.isFluidHandler(inventory[0])) {
			if (!extractMode && TransposerManager.fillRecipeExists(inventory[0], tank.getFluid())) {
				// There is a specific recipe for this! Do not use FluidHandler stuff.
			} else if (extractMode && TransposerManager.extractRecipeExists(inventory[0])) {
				// There is a specific recipe for this! Do not use FluidHandler stuff.
			} else {
				inventory[1] = ItemHelper.cloneStack(inventory[0], 1);
				inventory[0].shrink(1);

				if (inventory[0].getCount() <= 0) {
					inventory[0] = ItemStack.EMPTY;
				}
				hasFluidHandler = true;
				return false;
			}
		}
		if (!extractMode) {
			if (tank.getFluidAmount() <= 0) {
				return false;
			}
			TransposerRecipe recipe = TransposerManager.getFillRecipe(inventory[0], tank.getFluid());
			if (recipe == null || tank.getFluidAmount() < recipe.getFluid().amount) {
				return false;
			}
			if (inventory[0].getCount() < recipe.getInput().getCount()) {
				return false;
			}
			if (inventory[2].isEmpty()) {
				return true;
			}
			ItemStack output = recipe.getOutput();
			return inventory[2].isItemEqual(output) && inventory[2].getCount() + output.getCount() <= output.getMaxStackSize();
		} else {
			TransposerRecipe recipe = TransposerManager.getExtractRecipe(inventory[0]);
			if (recipe == null) {
				return false;
			}
			if (inventory[0].getCount() < recipe.getInput().getCount()) {
				return false;
			}
			if (tank.fill(recipe.getFluid(), false) != recipe.getFluid().amount) {
				return false;
			}
			if (inventory[2].isEmpty()) {
				return true;
			}
			ItemStack output = recipe.getOutput();
			return output.isEmpty() || inventory[2].isItemEqual(output) && inventory[2].getCount() + output.getCount() <= output.getMaxStackSize();
		}
	}

	@Override
	protected boolean hasValidInput() {

		if (hasFluidHandler) {
			return true;
		}
		TransposerRecipe recipe = extractMode ? TransposerManager.getExtractRecipe(inventory[1]) : TransposerManager.getFillRecipe(inventory[1], tank.getFluid());
		return recipe != null && recipe.getInput().getCount() <= inventory[1].getCount();
	}

	@Override
	protected void processStart() {

		FluidStack prevStack = renderFluid.copy();
		TransposerRecipe recipe;

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

		inventory[1] = ItemHelper.cloneStack(inventory[0], recipe.getInput().getCount());
		inventory[0].shrink(recipe.getInput().getCount());

		if (inventory[0].getCount() <= 0) {
			inventory[0] = ItemStack.EMPTY;
		}
		if (!FluidHelper.isFluidEqual(prevStack, renderFluid)) {
			sendFluidPacket();
		}
	}

	@Override
	protected void processFinish() {

		if (!extractMode) {
			TransposerRecipe recipe = TransposerManager.getFillRecipe(inventory[1], tank.getFluid());

			if (recipe == null) {
				processOff();
				return;
			}
			ItemStack output = recipe.getOutput();
			if (inventory[2].isEmpty()) {
				inventory[2] = ItemHelper.cloneStack(output);
			} else {
				inventory[2].grow(output.getCount());
			}
			inventory[1] = ItemStack.EMPTY;
			tank.drain(recipe.getFluid().amount, true);
		} else {
			TransposerRecipe recipe = TransposerManager.getExtractRecipe(inventory[1]);

			if (recipe == null) {
				processOff();
				return;
			}
			ItemStack output = recipe.getOutput();
			int recipeChance = recipe.getChance();
			if (recipeChance >= 100 || world.rand.nextInt(secondaryChance) < recipeChance) {
				if (inventory[2].isEmpty()) {
					inventory[2] = ItemHelper.cloneStack(output);
				} else {
					inventory[2].grow(output.getCount());
				}
			}
			inventory[1] = ItemStack.EMPTY;
			tank.fill(recipe.getFluid(), true);
		}
	}

	@Override
	protected void transferInput() {

		if (!getTransferIn()) {
			return;
		}
		int side;
		for (int i = inputTracker + 1; i <= inputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryInput(sideConfig.sideTypes[sideCache[side]])) {
				if (extractItem(0, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					inputTracker = side;
					break;
				}
			}
		}
	}

	@Override
	protected void transferOutput() {

		if (!getTransferOut()) {
			return;
		}
		if (extractMode) {
			transferOutputFluid();
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				if (transferItem(2, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	private void transferOutputFluid() {

		if (tank.getFluidAmount() <= 0) {
			return;
		}
		int side;
		FluidStack outputBuffer = new FluidStack(tank.getFluid(), Math.min(tank.getFluidAmount(), FLUID_TRANSFER[level]));
		for (int i = outputTrackerFluid + 1; i <= outputTrackerFluid + 6; i++) {
			side = i % 6;
			if (isSecondaryOutput(sideConfig.sideTypes[sideCache[side]])) {
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
			if (inventory[2].isEmpty()) {
				inventory[2] = ItemHelper.cloneStack(inventory[1], 1);
				inventory[1] = ItemStack.EMPTY;
				hasFluidHandler = false;
			} else {
				if (ItemHelper.itemsIdentical(inventory[1], inventory[2]) && inventory[1].getMaxStackSize() > 1 && inventory[2].getCount() + 1 <= inventory[2].getMaxStackSize()) {
					inventory[2].grow(1);
					inventory[1] = ItemStack.EMPTY;
					hasFluidHandler = false;
				}
			}
		}
		if (!hasFluidHandler && FluidHelper.isFluidHandler(inventory[0])) {
			inventory[1] = ItemHelper.cloneStack(inventory[0], 1);
			inventory[0].shrink(1);

			if (inventory[0].getCount() <= 0) {
				inventory[0] = ItemStack.EMPTY;
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

	public void setMode(boolean mode) {

		extractMode = mode;
		sendModePacket();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger("TrackIn");
		outputTracker = nbt.getInteger("TrackOut1");
		outputTrackerFluid = nbt.getInteger("TrackOut2");

		if (!inventory[1].isEmpty() && inventory[1].hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
			hasFluidHandler = true;
		}
		extractMode = nbt.getByte(CoreProps.MODE) == 1;
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
		nbt.setByte(CoreProps.MODE, extractMode ? (byte) 1 : 0);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */

	/* CLIENT -> SERVER */
	@Override
	public PacketBase getModePacket() {

		PacketBase payload = super.getModePacket();

		payload.addBool(extractMode);
		return payload;
	}

	@Override
	protected void handleModePacket(PacketBase payload) {

		super.handleModePacket(payload);

		extractMode = payload.getBool();
		extractFlag = extractMode;

		if (isActive) {
			processOff();
		}
		callNeighborTileChange();
	}

	/* SERVER -> CLIENT */
	@Override
	public PacketBase getFluidPacket() {

		PacketBase payload = super.getFluidPacket();

		payload.addFluidStack(renderFluid);
		return payload;
	}

	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

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
	public PacketBase getTilePacket() {

		PacketBase payload = super.getTilePacket();

		payload.addFluidStack(renderFluid);
		return payload;
	}

	@Override
	protected void handleFluidPacket(PacketBase payload) {

		super.handleFluidPacket(payload);

		renderFluid = payload.getFluidStack();
		callBlockUpdate();
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		extractMode = payload.getBool();
		extractFlag = payload.getBool();
		tank.setFluid(payload.getFluidStack());
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketBase payload) {

		super.handleTilePacket(payload);

		renderFluid = payload.getFluidStack();

	}

	/* IInventory */
	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		ItemStack stack = super.decrStackSize(slot, amount);

		if (ServerHelper.isServerWorld(world) && slot == 1) {
			if (isActive && (inventory[slot].isEmpty() || !hasValidInput())) {
				processOff();
				hasFluidHandler = false;
			}
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		if (ServerHelper.isServerWorld(world) && slot == 1) {
			if (isActive && !inventory[slot].isEmpty()) {
				if (stack.isEmpty() || !stack.isItemEqual(inventory[slot]) || !hasValidInput()) {
					processOff();
				}
			}
			hasFluidHandler = false;
		}
		inventory[slot] = stack;

		//		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
		//			stack.setCount(getInventoryStackLimit());
		//		}
		markChunkDirty();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot != 0 || (FluidHelper.isFluidHandler(stack) || TransposerManager.isItemValid(stack));
	}

	/* ISidedTexture */
	@Override
	@SideOnly (Side.CLIENT)
	public TextureAtlasSprite getTexture(int side, int pass) {

		if (pass == 0) {
			if (side == 0) {
				return TETextures.MACHINE_BOTTOM;
			} else if (side == 1) {
				return TETextures.MACHINE_TOP;
			}
			return side != facing ? TETextures.MACHINE_SIDE : isActive ? RenderHelper.getFluidTexture(renderFluid) : TETextures.MACHINE_FACE[TYPE];
		} else if (side < 6) {
			return side != facing ? TETextures.CONFIG[sideConfig.sideTypes[sideCache[side]]] : isActive ? TETextures.MACHINE_ACTIVE[TYPE] : TETextures.MACHINE_FACE[TYPE];
		}
		return TETextures.MACHINE_SIDE;
	}

	/* Rendering */
	@Override
	public boolean hasFluidUnderlay() {

		return true;
	}

	@Override
	public FluidStack getRenderFluid() {

		return renderFluid;
	}

	@Override
	public int getColorMask(BlockRenderLayer layer, EnumFacing side) {

		return layer == BlockRenderLayer.SOLID && side.ordinal() == facing && isActive ? renderFluid.getFluid().getColor(renderFluid) << 8 | 0xFF : super.getColorMask(layer, side);
	}

	/* ISoundSource */
	@Override
	public SoundEvent getSoundEvent() {

		return TEProps.enableSounds ? TESounds.machineTransposer : null;
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
					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, true, true) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (extractMode) {
						return 0;
					}
					if (from == null || allowInsertion(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return tank.fill(resource, doFill);
					}
					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (!extractMode && isActive) {
						return null;
					}
					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return tank.drain(resource, doDrain);
					}
					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (!extractMode && isActive) {
						return null;
					}
					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return tank.drain(maxDrain, doDrain);
					}
					return null;
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
