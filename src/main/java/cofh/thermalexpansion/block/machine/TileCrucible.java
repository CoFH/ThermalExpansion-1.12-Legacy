package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketBase;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.RenderHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine.Type;
import cofh.thermalexpansion.gui.client.machine.GuiCrucible;
import cofh.thermalexpansion.gui.container.machine.ContainerCrucible;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TESounds;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager.CrucibleRecipe;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;

public class TileCrucible extends TileMachineBase {

	private static final int TYPE = Type.CRUCIBLE.getMetadata();
	public static int basePower = 50;

	public static final int LAVA_MULTIPLIER = 10;
	public static final int LAVA_ENERGY_MOD = 0;

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, {}, { 0 }, { 0 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_ALL, OPEN, OMNI };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		ALT_SIDE_CONFIGS[TYPE] = new SideConfig();
		ALT_SIDE_CONFIGS[TYPE].numConfig = 2;
		ALT_SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, {}, { 0 }, { 0 } };
		ALT_SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, OPEN };
		ALT_SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 1, 1, 1, 1 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false, false };

		VALID_AUGMENTS[TYPE] = new HashSet<>();
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_CRUCIBLE_LAVA);

		LIGHT_VALUES[TYPE] = 14;

		GameRegistry.registerTileEntity(TileCrucible.class, "thermalexpansion:machine_crucible");

		config();
	}

	public static void config() {

		String category = "Machine.Crucible";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy consumption (in RF/t) for a Magma Crucible. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower, smallStorage);
	}

	private CrucibleRecipe curRecipe;
	private int inputTracker;
	private int outputTrackerFluid;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_LARGE);
	private FluidStack renderFluid = new FluidStack(FluidRegistry.LAVA, 0);

	/* AUGMENTS */
	protected boolean augmentLava;

	public TileCrucible() {

		super();
		inventory = new ItemStack[1 + 1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public int getLightValue() {

		return isActive ? renderFluid.getFluid().getLuminosity(renderFluid) : 0;
	}

	@Override
	protected int calcEnergy() {

		if (augmentLava) {
			return Math.min(super.calcEnergy() * LAVA_MULTIPLIER, energyStorage.getEnergyStored());
		}
		return super.calcEnergy();
	}

	@Override
	protected boolean canStart() {

		if (inventory[0].isEmpty() || energyStorage.getEnergyStored() <= 0 || tank.getSpace() <= 0) {
			return false;
		}
		if (augmentLava && !CrucibleManager.isLava(inventory[0])) {
			return false;
		}
		getRecipe();

		if (curRecipe == null) {
			return false;
		}
		if (inventory[0].getCount() < curRecipe.getInput().getCount()) {
			return false;
		}
		FluidStack output = curRecipe.getOutput();
		return tank.fill(output, false) == output.amount;
	}

	@Override
	protected boolean hasValidInput() {

		if (augmentLava && !CrucibleManager.isLava(inventory[0])) {
			return false;
		}
		if (curRecipe == null) {
			getRecipe();
		}
		if (curRecipe == null) {
			return false;
		}
		return curRecipe.getInput().getCount() <= inventory[0].getCount();
	}

	@Override
	protected void clearRecipe() {

		curRecipe = null;
	}

	@Override
	protected void getRecipe() {

		curRecipe = CrucibleManager.getRecipe(inventory[0]);
	}

	@Override
	protected void processStart() {

		processMax = curRecipe.getEnergy() * energyMod / ENERGY_BASE;
		processRem = processMax;

		FluidStack prevStack = renderFluid.copy();
		renderFluid = curRecipe.getOutput().copy();
		renderFluid.amount = 0;

		if (!FluidHelper.isFluidEqual(prevStack, renderFluid)) {
			sendFluidPacket();
		}
	}

	@Override
	protected void processFinish() {

		if (curRecipe == null) {
			getRecipe();
		}
		if (curRecipe == null) {
			processOff();
			return;
		}
		tank.fill(curRecipe.getOutput(), true);
		inventory[0].shrink(1);

		if (inventory[0].getCount() <= 0) {
			inventory[0] = ItemStack.EMPTY;
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
		transferOutputFluid();
	}

	private void transferOutputFluid() {

		if (tank.getFluidAmount() <= 0) {
			return;
		}
		int side;
		FluidStack output = new FluidStack(tank.getFluid(), Math.min(tank.getFluidAmount(), FLUID_TRANSFER[level]));
		for (int i = outputTrackerFluid + 1; i <= outputTrackerFluid + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				int toDrain = FluidHelper.insertFluidIntoAdjacentFluidHandler(this, EnumFacing.VALUES[side], output, true);
				if (toDrain > 0) {
					tank.drain(toDrain, true);
					outputTrackerFluid = side;
					break;
				}
			}
		}
	}

	@Override
	public void update() {

		if (timeCheckEighth()) {
			transferOutput();
		}
		super.update();
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
	public FluidTankCore getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	public boolean augmentLava() {

		return augmentLava;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger("TrackIn");
		outputTrackerFluid = nbt.getInteger("TrackOut");
		tank.readFromNBT(nbt);

		getRecipe();
		if (tank.getFluid() != null) {
			renderFluid = tank.getFluid().copy();
		} else if (curRecipe != null) {
			renderFluid = curRecipe.getOutput().copy();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut", outputTrackerFluid);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */

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

		tank.setFluid(payload.getFluidStack());
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketBase payload) {

		super.handleTilePacket(payload);

		renderFluid = payload.getFluidStack();
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		augmentLava = false;
	}

	@Override
	protected void postAugmentInstall() {

		super.postAugmentInstall();

		if (augmentLava) {
			energyStorage.setMaxTransfer(energyConfig.maxPower * 4 * LAVA_MULTIPLIER);
		}
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentLava && TEProps.MACHINE_CRUCIBLE_LAVA.equals(id)) {
			augmentLava = true;
			hasModeAugment = true;
			energyMod += LAVA_ENERGY_MOD;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* IEnergyInfo */
	@Override
	public int getInfoMaxEnergyPerTick() {

		return augmentLava ? energyConfig.maxPower * LAVA_MULTIPLIER : energyConfig.maxPower;
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot != 0 || (augmentLava ? CrucibleManager.isLava(stack) : CrucibleManager.recipeExists(stack));
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

	/* RENDERING */
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

		return TESounds.machineCrucible;
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
					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, false, true) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return tank.drain(resource, doDrain);
					}
					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

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
