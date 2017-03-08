package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.AugmentHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.machine.GuiInsolator;
import cofh.thermalexpansion.gui.container.machine.ContainerInsolator;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.crafting.InsolatorManager;
import cofh.thermalexpansion.util.crafting.InsolatorManager.RecipeInsolator;
import cofh.thermalexpansion.util.crafting.InsolatorManager.Substrate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

import javax.annotation.Nullable;
import java.util.ArrayList;

public class TileInsolator extends TileMachineBase {

	private static final int TYPE = BlockMachine.Type.INSOLATOR.getMetadata();
	public static int basePower = 20;

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 8;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0, 1 }, { 2 }, { 3 }, { 2, 3 }, { 0 }, { 1 }, { 0, 1, 2, 3 } };
		SIDE_CONFIGS[TYPE].allowInsertionSide = new boolean[] { false, true, false, false, false, true, true, true };
		SIDE_CONFIGS[TYPE].allowExtractionSide = new boolean[] { false, true, true, true, true, false, false, true };
		SIDE_CONFIGS[TYPE].sideTex = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 3, 1, 2, 2, 2, 2 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, true, false, false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { true, true, true, true, false };

		VALID_AUGMENTS[TYPE] = new ArrayList<>();
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_INSOLATOR_MYCELIUM);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_INSOLATOR_NETHER);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_INSOLATOR_END);

		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_SECONDARY);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_SECONDARY_NULL);

		LIGHT_VALUES[TYPE] = 14;

		GameRegistry.registerTileEntity(TileInsolator.class, "thermalexpansion:machine_insolator");

		config();
	}

	public static void config() {

		String category = "Machine.Insolator";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower);
	}

	private int inputTrackerPrimary;
	private int inputTrackerSecondary;
	private int outputTrackerPrimary;
	private int outputTrackerSecondary;

	public boolean lockPrimary = false;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_LARGE);

	/* AUGMENTS */
	protected boolean augmentMycelium;
	protected boolean augmentNether;
	protected boolean augmentEnd;

	public TileInsolator() {

		super();
		inventory = new ItemStack[2 + 1 + 1 + 1];
		createAllSlots(inventory.length);
		tank.setLock(FluidRegistry.WATER);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public int getMaxInputSlot() {

		return 1;
	}

	@Override
	protected boolean canStart() {

		if (inventory[0] == null || inventory[1] == null || energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		RecipeInsolator recipe = InsolatorManager.getRecipe(inventory[0], inventory[1]);

		if (recipe == null || tank.getFluidAmount() < recipe.getEnergy() / 10) {
			return false;
		}
		Substrate substrate = recipe.getSubstrate();
		if (substrate != Substrate.STANDARD) {
			if (substrate == Substrate.MYCELIUM && !augmentMycelium) {
				return false;
			} else if (substrate == Substrate.NETHER && !augmentNether) {
				return false;
			} else if (substrate == Substrate.END && !augmentEnd) {
				return false;
			}
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
			if (!augmentSecondaryNull && !inventory[3].isItemEqual(secondaryItem)) {
				return false;
			}
			if (!augmentSecondaryNull && inventory[3].stackSize + secondaryItem.stackSize > secondaryItem.getMaxStackSize()) {
				return false;
			}
		}
		return inventory[2] == null || inventory[2].isItemEqual(primaryItem) && inventory[2].stackSize + primaryItem.stackSize <= primaryItem.getMaxStackSize();
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

		processMax = InsolatorManager.getRecipe(inventory[0], inventory[1]).getEnergy() * energyMod / ENERGY_BASE;
		processRem = processMax;
	}

	@Override
	protected void processFinish() {

		RecipeInsolator recipe = InsolatorManager.getRecipe(inventory[0], inventory[1]);

		if (recipe == null) {
			processOff();
			return;
		}
		ItemStack primaryItem = recipe.getPrimaryOutput();
		ItemStack secondaryItem = recipe.getSecondaryOutput();
		if (inventory[2] == null) {
			inventory[2] = ItemHelper.cloneStack(primaryItem);
		} else {
			inventory[2].stackSize += primaryItem.stackSize;
		}
		if (secondaryItem != null) {
			int modifiedChance = secondaryChance;

			int recipeChance = recipe.getSecondaryOutputChance();
			if (recipeChance >= 100 || worldObj.rand.nextInt(modifiedChance) < recipeChance) {
				if (inventory[3] == null) {
					inventory[3] = ItemHelper.cloneStack(secondaryItem);

					if (worldObj.rand.nextInt(SECONDARY_BASE) < recipeChance - modifiedChance) {
						inventory[3].stackSize += secondaryItem.stackSize;
					}
				} else if (inventory[3].isItemEqual(secondaryItem)) {
					inventory[3].stackSize += secondaryItem.stackSize;

					if (worldObj.rand.nextInt(SECONDARY_BASE) < recipeChance - modifiedChance) {
						inventory[3].stackSize += secondaryItem.stackSize;
					}
				}
				if (inventory[3].stackSize > inventory[3].getMaxStackSize()) {
					inventory[3].stackSize = inventory[3].getMaxStackSize();
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
	protected int processTick() {

		int energy = calcEnergy();
		energyStorage.modifyEnergyStored(-energy);
		processRem -= energy;
		tank.drain(energy / 10, true);

		return energy;
	}

	@Override
	protected void transferInput() {

		if (!enableAutoInput) {
			return;
		}
		int side;
		for (int i = inputTrackerPrimary + 1; i <= inputTrackerPrimary + 6; i++) {
			side = i % 6;
			if (sideCache[side] == 1 || sideCache[side] == 5) {
				if (extractItem(0, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					inputTrackerPrimary = side;
					break;
				}
			}
		}
		for (int i = inputTrackerPrimary + 1; i <= inputTrackerPrimary + 6; i++) {
			side = i % 6;
			if (sideCache[side] == 1 || sideCache[side] == 6) {
				if (extractItem(1, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					inputTrackerSecondary = side;
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
		int side;
		if (inventory[2] != null) {
			for (int i = outputTrackerPrimary + 1; i <= outputTrackerPrimary + 6; i++) {
				side = i % 6;

				if (sideCache[side] == 2 || sideCache[side] == 4) {
					if (transferItem(2, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
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
				if (transferItem(3, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					outputTrackerSecondary = side;
					break;
				}
			}
		}
	}

	@Override
	protected boolean readPortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		if (!super.readPortableTagInternal(player, tag)) {
			return false;
		}
		lockPrimary = tag.getBoolean("SlotLock");
		return true;
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		if (!super.writePortableTagInternal(player, tag)) {
			return false;
		}
		tag.setBoolean("SlotLock", lockPrimary);
		return true;
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
	public FluidTankCore getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	public void setMode(boolean mode) {

		boolean lastMode = lockPrimary;
		lockPrimary = mode;
		sendModePacket();
		lockPrimary = lastMode;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTrackerPrimary = nbt.getInteger("TrackIn1");
		inputTrackerSecondary = nbt.getInteger("TrackIn2");
		outputTrackerPrimary = nbt.getInteger("Tracker1");
		outputTrackerSecondary = nbt.getInteger("Tracker2");
		lockPrimary = nbt.getBoolean("SlotLock");
		tank.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn1", inputTrackerPrimary);
		nbt.setInteger("TrackIn2", inputTrackerSecondary);
		nbt.setInteger("Tracker1", outputTrackerPrimary);
		nbt.setInteger("Tracker2", outputTrackerSecondary);
		nbt.setBoolean("SlotLock", lockPrimary);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */

	/* CLIENT -> SERVER */
	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addBool(lockPrimary);

		return payload;
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		lockPrimary = payload.getBool();

		callNeighborTileChange();
	}

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addBool(lockPrimary);
		payload.addInt(tank.getFluidAmount());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		lockPrimary = payload.getBool();
		tank.getFluid().amount = payload.getInt();
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		augmentMycelium = false;
		augmentNether = false;
		augmentEnd = false;
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentMycelium && TEProps.MACHINE_INSOLATOR_MYCELIUM.equals(id)) {
			augmentMycelium = true;
			hasModeAugment = true;
			return true;
		}
		if (!augmentNether && TEProps.MACHINE_INSOLATOR_NETHER.equals(id)) {
			augmentNether = true;
			hasModeAugment = true;
			return true;
		}
		if (!augmentEnd && TEProps.MACHINE_INSOLATOR_END.equals(id)) {
			augmentEnd = true;
			hasModeAugment = true;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		if (lockPrimary) {
			if (slot == 0) {
				return InsolatorManager.isItemFertilizer(stack);
			}
			if (slot == 1) {
				return !InsolatorManager.isItemFertilizer(stack) && InsolatorManager.isItemValid(stack);
			}
		}
		return slot > 1 || InsolatorManager.isItemValid(stack);
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
					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, true, false) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (from != null && sideCache[from.ordinal()] == 0) {
						return 0;
					}
					return tank.fill(resource, doFill);
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					return null;
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
