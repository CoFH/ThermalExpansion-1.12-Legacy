package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.inventory.InventoryCraftingFalse;
import cofh.core.network.PacketBase;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine.Type;
import cofh.thermalexpansion.gui.client.machine.GuiCrafter;
import cofh.thermalexpansion.gui.container.machine.ContainerCrafter;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Arrays;
import java.util.HashSet;

public class TileCrafter extends TileMachineBase {

	private static final int TYPE = Type.CRAFTER.getMetadata();
	public static int basePower = 20;

	public static final int DEFAULT_ENERGY = 400;

	public static final int SLOT_OUTPUT = 18;
	public static final int SLOT_CRAFTING_START = 20;

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 }, { 18 }, { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 }, { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_ALL, OPEN, OMNI };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false };

		VALID_AUGMENTS[TYPE] = new HashSet<>();

		GameRegistry.registerTileEntity(TileCrafter.class, "thermalexpansion:machine_crafter");

		config();
	}

	public static void config() {

		String category = "Machine.Crafter";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy consumption (in RF/t) for a Cyclic Assembler. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower, smallStorage);
	}

	private int inputTracker;
	private int outputTracker;

	private InventoryCraftingFalse craftMatrix = new InventoryCraftingFalse(3, 3);
	private InventoryCraftResult craftResult = new InventoryCraftResult();

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_LARGE);

	public TileCrafter() {

		super();
		inventory = new ItemStack[18 + 1 + 1 + 9 + 1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length - 9 - 1);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public int getChargeSlot() {

		return inventory.length - 1 - 9 - 1;
	}

	@Override
	protected boolean canStart() {

		if (energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		//		if (inventory[SLOT_SCHEMATIC].isEmpty() || energyStorage.getEnergyStored() <= 0) {
		//			return false;
		//		}
		//		IRecipe recipe = ItemHelper.getCraftingRecipe(craftMatrix, world);
		//
		//		if (recipe == null) {
		//			return false;
		//		}

		return true;
	}

	@Override
	protected boolean hasValidInput() {

		//		PulverizerRecipe recipe = PulverizerManager.getRecipe(inventory[0]);
		//		return recipe != null && recipe.getInput().getCount() <= inventory[0].getCount();
		return true;
	}

	@Override
	protected void processStart() {

		processMax = DEFAULT_ENERGY;
		processRem = processMax;
	}

	@Override
	protected void processFinish() {

		//		IRecipe recipe = ItemHelper.getCraftingRecipe(craftMatrix, world);
		//
		//		if (recipe == null) {
		//			processOff();
		//			return;
		//		}
		//		ItemStack output = recipe.getCraftingResult(craftMatrix);
		ItemStack output = new ItemStack(Blocks.MAGMA);

		if (inventory[SLOT_OUTPUT].isEmpty()) {
			inventory[SLOT_OUTPUT] = ItemHelper.cloneStack(output);
		} else {
			inventory[SLOT_OUTPUT].grow(output.getCount());
		}
	}

	@Override
	protected void transferInput() {

		if (!enableAutoInput) {
			return;
		}
		//		int side;
		//		for (int i = inputTracker + 1; i <= inputTracker + 6; i++) {
		//			side = i % 6;
		//			if (isPrimaryInput(sideConfig.sideTypes[sideCache[side]])) {
		//				if (extractItem(0, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
		//					inputTracker = side;
		//					break;
		//				}
		//			}
		//		}
	}

	@Override
	protected void transferOutput() {

		if (!enableAutoOutput) {
			return;
		}
		if (inventory[SLOT_OUTPUT].isEmpty()) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				if (transferItem(SLOT_OUTPUT, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiCrafter(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerCrafter(inventory, this);
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
		outputTracker = nbt.getInteger("TrackOut");
		tank.readFromNBT(nbt);

		setRecipe();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut", outputTracker);
		tank.writeToNBT(nbt);
		return nbt;
	}

	private void setRecipe() {

		for (int i = 0; i < 9; i++) {
			craftMatrix.setInventorySlotContents(i, inventory[i + SLOT_CRAFTING_START]);
		}
		ItemStack stack = ItemStack.EMPTY;
		IRecipe recipe = CraftingManager.findMatchingRecipe(craftMatrix, world);

		if (recipe != null) {
			craftResult.setRecipeUsed(recipe);
			stack = recipe.getCraftingResult(craftMatrix);
			System.out.println("woohoo!");
		}
		craftResult.setInventorySlotContents(0, stack);
		inventory[SLOT_CRAFTING_START + 9] = craftResult.getStackInSlot(0);
	}

	/* NETWORK METHODS */

	/* CLIENT -> SERVER */
	@Override
	public PacketBase getModePacket() {

		PacketBase payload = super.getModePacket();

		for (int i = SLOT_CRAFTING_START; i < SLOT_CRAFTING_START + 9; i++) {
			payload.addItemStack(inventory[i]);
		}
		return payload;
	}

	@Override
	protected void handleModePacket(PacketBase payload) {

		super.handleModePacket(payload);

		for (int i = SLOT_CRAFTING_START; i < SLOT_CRAFTING_START + 9; i++) {
			inventory[i] = payload.getItemStack();
		}
		setRecipe();
		markChunkDirty();
	}

	/* SERVER -> CLIENT */
	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addFluidStack(getTankFluid());
		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		tank.setFluid(payload.getFluidStack());
	}

	/* CAPABILITIES */
	//	@Override
	//	public boolean hasCapability(Capability<?> capability, EnumFacing from) {
	//
	//		return super.hasCapability(capability, from) || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	//	}
	//
	//	@Override
	//	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {
	//
	//		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
	//			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler() {
	//
	//				@Override
	//				public IFluidTankProperties[] getTankProperties() {
	//
	//					FluidTankInfo info = tank.getInfo();
	//					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, true, true) };
	//				}
	//
	//				@Override
	//				public int fill(FluidStack resource, boolean doFill) {
	//
	//					return tank.fill(resource, doFill);
	//				}
	//
	//				@Nullable
	//				@Override
	//				public FluidStack drain(FluidStack resource, boolean doDrain) {
	//
	//					if (isActive) {
	//						return null;
	//					}
	//					return tank.drain(resource, doDrain);
	//				}
	//
	//				@Nullable
	//				@Override
	//				public FluidStack drain(int maxDrain, boolean doDrain) {
	//
	//					if (isActive) {
	//						return null;
	//					}
	//					return tank.drain(maxDrain, doDrain);
	//				}
	//			});
	//		}
	//		return super.getCapability(capability, from);
	//	}

}
