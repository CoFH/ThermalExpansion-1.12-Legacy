package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.util.core.EnergyConfig;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.RenderHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine.Type;
import cofh.thermalexpansion.gui.client.machine.GuiRefinery;
import cofh.thermalexpansion.gui.container.machine.ContainerRefinery;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.util.managers.machine.RefineryManager;
import cofh.thermalexpansion.util.managers.machine.RefineryManager.RefineryRecipe;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
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

import static cofh.core.util.core.SideConfig.*;

public class TileRefinery extends TileMachineBase {

	private static final int TYPE = Type.REFINERY.getMetadata();
	public static int basePower = 20;

	public static final int SPEC_ENERGY_MOD = 100;
	public static final int SPEC_FLUID_BOOST = 50;

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 7;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, {}, {}, { 0 }, { 0 }, { 0 }, { 0 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_PRIMARY, OUTPUT_SECONDARY, OUTPUT_ALL, OPEN, OMNI };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 2, 3, 3, 3, 3 };

		ALT_SIDE_CONFIGS[TYPE] = new SideConfig();
		ALT_SIDE_CONFIGS[TYPE].numConfig = 2;
		ALT_SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, {}, {}, { 0 }, { 0 }, { 0 }, { 0 } };
		ALT_SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, OPEN };
		ALT_SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 1, 1, 1, 1 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { true, false };

		VALID_AUGMENTS[TYPE] = new HashSet<>();
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_REFINERY_FOSSIL);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_REFINERY_POTION);

		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_SECONDARY);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_SECONDARY_NULL);

		GameRegistry.registerTileEntity(TileRefinery.class, "thermalexpansion:machine_refinery");

		config();
	}

	public static void config() {

		String category = "Machine.Refinery";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy consumption (in RF/t) for a Fractionating Still. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower, smallStorage);
	}

	private RefineryRecipe curRecipe;
	private int outputTracker;
	private int outputTrackerFluid;

	private FluidTankCore inputTank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	private FluidTankCore outputTank = new FluidTankCore(TEProps.MAX_FLUID_LARGE);
	private FluidStack renderFluid = new FluidStack(FluidRegistry.LAVA, 0);

	/* AUGMENTS */
	protected boolean augmentFossil;
	protected boolean augmentBio;
	protected boolean augmentPotion;

	public TileRefinery() {

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
	protected int getMaxInputSlot() {

		// This is a hack to prevent super() logic from working.
		return -1;
	}

	@Override
	protected boolean canStart() {

		if (energyStorage.getEnergyStored() <= 0 || outputTank.getSpace() <= 0) {
			return false;
		}
		getRecipe();

		if (curRecipe == null) {
			return false;
		}
		if (inputTank.getFluidAmount() < curRecipe.getInput().amount) {
			return false;
		}
		FluidStack outputFluid = curRecipe.getOutputFluid();
		ItemStack outputItem = curRecipe.getOutputItem();

		if (!outputItem.isEmpty() && !inventory[0].isEmpty()) {
			if (!augmentSecondaryNull) {
				if (!inventory[0].isItemEqual(outputItem)) {
					return false;
				}
				if (inventory[0].getCount() + outputItem.getCount() > outputItem.getMaxStackSize()) {
					return false;
				}
			}
		}
		return outputTank.fill(outputFluid, false) == outputFluid.amount;
	}

	@Override
	protected boolean hasValidInput() {

		if (curRecipe == null) {
			getRecipe();
		}
		return curRecipe != null;
	}

	@Override
	protected void clearRecipe() {

		curRecipe = null;
	}

	@Override
	protected void getRecipe() {

		curRecipe = augmentPotion ? RefineryManager.getRecipePotion(inputTank.getFluid()) : RefineryManager.getRecipe(inputTank.getFluid());
	}

	@Override
	protected void processStart() {

		processMax = curRecipe.getEnergy() * energyMod / ENERGY_BASE;
		processRem = processMax;

		FluidStack prevStack = renderFluid.copy();
		renderFluid = inputTank.getFluid().copy();
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
		if (augmentFossil && RefineryManager.isFossilFuel(curRecipe.getInput()) || augmentBio && RefineryManager.isBioFuel(curRecipe.getInput())) {
			outputTank.fill(new FluidStack(curRecipe.getOutputFluid(), curRecipe.getOutputFluid().amount + SPEC_FLUID_BOOST), true);
		} else {
			outputTank.fill(curRecipe.getOutputFluid(), true);
		}
		ItemStack outputItem = curRecipe.getOutputItem();

		if (!outputItem.isEmpty()) {
			int modifiedChance = secondaryChance;

			int recipeChance = curRecipe.getChance();
			if (recipeChance >= 100 || world.rand.nextInt(modifiedChance) < recipeChance) {
				if (inventory[0].isEmpty()) {
					inventory[0] = ItemHelper.cloneStack(outputItem);

					if (recipeChance > modifiedChance && world.rand.nextInt(SECONDARY_BASE) < recipeChance - modifiedChance) {
						inventory[0].grow(outputItem.getCount());
					}
				} else if (inventory[0].isItemEqual(outputItem)) {
					inventory[0].grow(outputItem.getCount());

					if (recipeChance > modifiedChance && world.rand.nextInt(SECONDARY_BASE) < recipeChance - modifiedChance) {
						inventory[0].grow(outputItem.getCount());
					}
				}
				if (inventory[0].getCount() > inventory[0].getMaxStackSize()) {
					inventory[0].setCount(inventory[0].getMaxStackSize());
				}
			}
		}
		inputTank.drain(curRecipe.getInput().amount, true);
	}

	@Override
	protected void transferOutput() {

		if (!getTransferOut()) {
			return;
		}
		transferOutputFluid();

		if (inventory[0].isEmpty()) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (isSecondaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				if (transferItem(0, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	private void transferOutputFluid() {

		if (!getTransferOut()) {
			return;
		}
		if (outputTank.getFluidAmount() <= 0) {
			return;
		}
		int side;
		FluidStack output = new FluidStack(outputTank.getFluid(), Math.min(outputTank.getFluidAmount(), FLUID_TRANSFER[level]));
		for (int i = outputTrackerFluid + 1; i <= outputTrackerFluid + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				int toDrain = FluidHelper.insertFluidIntoAdjacentFluidHandler(this, EnumFacing.VALUES[side], output, true);
				if (toDrain > 0) {
					outputTank.drain(toDrain, true);
					outputTrackerFluid = side;
					break;
				}
			}
		}
	}

	@Override
	public void update() {

		if (timeCheckEighth()) {
			transferOutputFluid();
		}
		super.update();
	}

	@Override
	protected void setLevelFlags() {

		super.setLevelFlags();

		hasAutoInput = false;
		enableAutoInput = false;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiRefinery(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerRefinery(inventory, this);
	}

	public FluidTankCore getTank(int tankIndex) {

		if (tankIndex == 0) {
			return inputTank;
		}
		return outputTank;
	}

	public FluidStack getTankFluid(int tankIndex) {

		if (tankIndex == 0) {
			return inputTank.getFluid();
		}
		return outputTank.getFluid();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		outputTracker = nbt.getInteger(CoreProps.TRACK_OUT);
		outputTrackerFluid = nbt.getInteger(CoreProps.TRACK_OUT_2);

		inputTank.readFromNBT(nbt.getCompoundTag("TankIn"));
		outputTank.readFromNBT(nbt.getCompoundTag("TankOut"));

		if (inputTank.getFluid() != null) {
			renderFluid = inputTank.getFluid().copy();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger(CoreProps.TRACK_OUT, outputTracker);
		nbt.setInteger(CoreProps.TRACK_OUT_2, outputTrackerFluid);

		nbt.setTag("TankIn", inputTank.writeToNBT(new NBTTagCompound()));
		nbt.setTag("TankOut", outputTank.writeToNBT(new NBTTagCompound()));
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketBase getTilePacket() {

		PacketBase payload = super.getTilePacket();
		payload.addFluidStack(renderFluid);
		return payload;
	}

	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();
		if (inputTank.getFluid() == null) {
			payload.addFluidStack(renderFluid);
		} else {
			payload.addFluidStack(inputTank.getFluid());
		}
		payload.addFluidStack(outputTank.getFluid());
		return payload;
	}

	@Override
	public PacketBase getFluidPacket() {

		PacketBase payload = super.getFluidPacket();
		payload.addFluidStack(renderFluid);
		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);
		inputTank.setFluid(payload.getFluidStack());
		outputTank.setFluid(payload.getFluidStack());
	}

	@Override
	protected void handleFluidPacket(PacketBase payload) {

		super.handleFluidPacket(payload);
		renderFluid = payload.getFluidStack();
		callBlockUpdate();
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

		augmentFossil = false;
		augmentBio = false;
		augmentPotion = false;
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentFossil && TEProps.MACHINE_REFINERY_FOSSIL.equals(id)) {
			augmentFossil = true;
			hasModeAugment = true;
			energyMod += SPEC_ENERGY_MOD;
			return true;
		}
		if (!augmentBio && TEProps.MACHINE_REFINERY_BIO.equals(id)) {
			augmentBio = true;
			hasModeAugment = true;
			energyMod += SPEC_ENERGY_MOD;
			return true;
		}
		if (!augmentPotion && TEProps.MACHINE_REFINERY_POTION.equals(id)) {
			augmentPotion = true;
			hasModeAugment = true;
			return true;
		}
		return super.installAugmentToSlot(slot);
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

					FluidTankInfo inputInfo = inputTank.getInfo();
					FluidTankInfo outputInfo = outputTank.getInfo();
					return new IFluidTankProperties[] { new FluidTankProperties(inputInfo.fluid, inputInfo.capacity, true, false), new FluidTankProperties(outputInfo.fluid, outputInfo.capacity, false, true) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (from == null || allowInsertion(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						if (augmentPotion) {
							if (!RefineryManager.recipeExistsPotion(resource)) {
								return 0;
							}
						} else if (!RefineryManager.recipeExists(resource)) {
							return 0;
						}
						return inputTank.fill(resource, doFill);
					}
					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						FluidStack ret = outputTank.drain(resource, doDrain);
						return ret != null ? ret : isActive ? null : inputTank.drain(resource, doDrain);
					}
					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						FluidStack ret = outputTank.drain(maxDrain, doDrain);
						return ret != null ? ret : isActive ? null : inputTank.drain(maxDrain, doDrain);
					}
					return null;
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
