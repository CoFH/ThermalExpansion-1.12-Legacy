package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.helpers.AugmentHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.machine.GuiFurnace;
import cofh.thermalexpansion.gui.container.machine.ContainerFurnace;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.crafting.FurnaceManager;
import cofh.thermalexpansion.util.crafting.FurnaceManager.RecipeFurnace;
import cofh.thermalexpansion.util.crafting.TapperManager;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class TileFurnace extends TileMachineBase {

	private static final int TYPE = BlockMachine.Type.FURNACE.getMetadata();
	public static int basePower = 20;
	public static int fluidFactor = 10;

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1 }, { 0, 1 }, { 0, 1 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { 0, 1, 4, 7, 8 };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { true, true, false };

		VALID_AUGMENTS[TYPE] = new ArrayList<>();
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_FURNACE_FOOD);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_FURNACE_ORE);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_FURNACE_PYROLYSIS);

		LIGHT_VALUES[TYPE] = 14;

		GameRegistry.registerTileEntity(TileFurnace.class, "thermalexpansion:machine_furnace");

		config();
	}

	public static void config() {

		String category = "Machine.Furnace";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower);
	}

	private int inputTracker;
	private int outputTracker;
	private int outputTrackerFluid;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);

	/* AUGMENTS */
	protected boolean augmentFood;
	protected boolean augmentOre;
	protected boolean augmentPyrolysis;
	protected boolean flagPyrolysis;

	public TileFurnace() {

		super();
		inventory = new ItemStack[1 + 1 + 1];
		createAllSlots(inventory.length);
		tank.setLock(TFFluids.fluidCreosote);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void update() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (augmentPyrolysis) {
			transferOutputFluid();
		}
		super.update();
	}

	@Override
	protected int calcEnergy() {

		if (augmentPyrolysis) {
			return Math.min(energyConfig.minPower, energyStorage.getEnergyStored());
		}
		return super.calcEnergy();
	}

	@Override
	protected boolean canStart() {

		if (inventory[0] == null || energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		if (augmentFood && !FurnaceManager.isFood(inventory[0]) || augmentOre && !FurnaceManager.isOre(inventory[0])) {
			return false;
		}
		RecipeFurnace recipe = augmentPyrolysis ? FurnaceManager.getRecipePyrolysis(inventory[0]) : FurnaceManager.getRecipe(inventory[0]);

		if (recipe == null) {
			return false;
		}
		if (inventory[0].stackSize < recipe.getInput().stackSize) {
			return false;
		}
		ItemStack output = recipe.getOutput();

		return inventory[1] == null || inventory[1].isItemEqual(output) && inventory[1].stackSize + output.stackSize <= output.getMaxStackSize();
	}

	@Override
	protected boolean hasValidInput() {

		RecipeFurnace recipe = FurnaceManager.getRecipe(inventory[0]);

		if (augmentFood && !FurnaceManager.isFood(inventory[0]) || augmentOre && !FurnaceManager.isOre(inventory[0])) {
			return false;
		}
		return recipe != null && recipe.getInput().stackSize <= inventory[0].stackSize;
	}

	@Override
	protected void processStart() {

		processMax = augmentPyrolysis ? FurnaceManager.getRecipePyrolysis(inventory[0]).getEnergy() * energyMod / ENERGY_BASE : FurnaceManager.getRecipe(inventory[0]).getEnergy() * energyMod / ENERGY_BASE;
		processRem = processMax;
	}

	@Override
	protected void processFinish() {

		RecipeFurnace recipe = augmentPyrolysis ? FurnaceManager.getRecipePyrolysis(inventory[0]) : FurnaceManager.getRecipe(inventory[0]);

		if (recipe == null) {
			processOff();
			return;
		}
		ItemStack output = recipe.getOutput();
		if (inventory[1] == null) {
			inventory[1] = ItemHelper.cloneStack(output);
		} else {
			inventory[1].stackSize += output.stackSize;
		}
		if (augmentPyrolysis) {
			tank.fill(new FluidStack(TFFluids.fluidCreosote, recipe.getEnergy() / fluidFactor), true);
		} else {
			if ((augmentFood && FurnaceManager.isFood(inventory[0]) || augmentOre && FurnaceManager.isOre(inventory[0])) && inventory[1].stackSize < inventory[1].getMaxStackSize()) {
				inventory[1].stackSize += output.stackSize;
			}
		}
		inventory[0].stackSize -= recipe.getInput().stackSize;

		if (inventory[0].stackSize <= 0) {
			inventory[0] = null;
		}
	}

	@Override
	protected void transferInput() {

		if (!enableAutoInput) {
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

		if (!enableAutoOutput) {
			return;
		}
		if (inventory[1] == null) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				if (transferItem(1, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	private void transferOutputFluid() {

		if (!enableAutoOutput || tank.getFluidAmount() <= 0) {
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

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiFurnace(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerFurnace(inventory, this);
	}

	@Override
	public FluidTankCore getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	public boolean augmentFood() {

		return augmentFood;
	}

	public boolean augmentOre() {

		return augmentOre;
	}

	public boolean augmentPyrolysis() {

		return augmentPyrolysis && flagPyrolysis;
	}

	public boolean fluidArrow() {

		return augmentPyrolysis && TapperManager.mappingExists(inventory[0]);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger("TrackIn");
		outputTracker = nbt.getInteger("TrackOut");
		outputTrackerFluid = nbt.getInteger("TrackOutFluid");
		tank.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut", outputTracker);
		nbt.setInteger("TrackoutFluid", outputTrackerFluid);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addBool(augmentPyrolysis);
		payload.addFluidStack(tank.getFluid());
		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		augmentPyrolysis = payload.getBool();
		flagPyrolysis = augmentPyrolysis;
		tank.setFluid(payload.getFluidStack());
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		augmentFood = false;
		augmentOre = false;
		augmentPyrolysis = false;
	}

	@Override
	protected void postAugmentInstall() {

		super.postAugmentInstall();

		if (!augmentPyrolysis) {
			tank.setFluid(null);
		}
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentFood && TEProps.MACHINE_FURNACE_FOOD.equals(id)) {
			augmentFood = true;
			hasModeAugment = true;
			energyMod += 50;
			return true;
		}
		if (!augmentOre && TEProps.MACHINE_FURNACE_ORE.equals(id)) {
			augmentOre = true;
			hasModeAugment = true;
			energyMod += 50;
			return true;
		}
		if (!augmentPyrolysis && TEProps.MACHINE_FURNACE_PYROLYSIS.equals(id)) {
			augmentPyrolysis = true;
			hasModeAugment = true;
			energyMod += 50;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	@Override
	public int getInfoMaxEnergyPerTick() {

		return augmentPyrolysis ? energyConfig.minPower : energyConfig.maxPower;
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot != 0 || (augmentFood ? FurnaceManager.isFood(stack) : augmentOre ? FurnaceManager.isOre(stack) : augmentPyrolysis ? FurnaceManager.recipeExistsPyrolysis(stack) : FurnaceManager.recipeExists(stack));
	}

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return super.hasCapability(capability, from) || augmentPyrolysis && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (augmentPyrolysis && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler() {
				@Override
				public IFluidTankProperties[] getTankProperties() {

					FluidTankInfo info = tank.getInfo();
					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, true, false) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (from != null && sideCache[from.ordinal()] < 2) {
						return null;
					}
					if (resource == null || !resource.isFluidEqual(tank.getFluid())) {
						return null;
					}
					return tank.drain(resource.amount, doDrain);
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (from != null && sideCache[from.ordinal()] < 2) {
						return null;
					}
					return tank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
