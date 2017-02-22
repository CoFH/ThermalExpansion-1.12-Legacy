package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.AugmentHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.machine.GuiPulverizer;
import cofh.thermalexpansion.gui.container.machine.ContainerPulverizer;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.crafting.PulverizerManager;
import cofh.thermalexpansion.util.crafting.PulverizerManager.RecipePulverizer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;

public class TilePulverizer extends TileMachineBase {

	private static final int TYPE = BlockMachine.Type.PULVERIZER.getMetadata();

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 6;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1, 2 }, { 3 }, { 1, 2, 3 }, { 0, 1, 2, 3 } };
		SIDE_CONFIGS[TYPE].allowInsertionSide = new boolean[] { false, true, false, false, false, true };
		SIDE_CONFIGS[TYPE].allowExtractionSide = new boolean[] { false, true, true, true, true, true };
		SIDE_CONFIGS[TYPE].sideTex = new int[] { 0, 1, 2, 3, 4, 7 };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 3, 1, 2, 2, 2, 2 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, false, false, false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { true, true, true, true, false };

		VALID_AUGMENTS[TYPE] = new ArrayList<String>();
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_PULVERIZER_GEODE);

		LIGHT_VALUES[TYPE] = 4;

		GameRegistry.registerTileEntity(TilePulverizer.class, "thermalexpansion:machine_pulverizer");

		config();
	}

	public static void config() {

		String category = "Machine.Pulverizer";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(20);
	}

	private int inputTracker;
	private int outputTrackerPrimary;
	private int outputTrackerSecondary;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);

	/* AUGMENTS */
	protected boolean augmentGeode;

	public TilePulverizer() {

		super();
		inventory = new ItemStack[1 + 1 + 1 + 1];
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	protected boolean canStart() {

		if (inventory[0] == null || energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		RecipePulverizer recipe = PulverizerManager.getRecipe(inventory[0]);

		if (recipe == null) {
			return false;
		}
		if (inventory[0].stackSize < recipe.getInput().stackSize) {
			return false;
		}
		if (recipe == null) {
			return false;
		}
		if (inventory[0].stackSize < recipe.getInput().stackSize) {
			return false;
		}
		ItemStack primaryItem = recipe.getPrimaryOutput();
		ItemStack secondaryItem = recipe.getSecondaryOutput();

		if (!augmentSecondaryNull && secondaryItem != null && inventory[2] != null) {
			if (!inventory[2].isItemEqual(secondaryItem)) {
				return false;
			}
			if (inventory[2].stackSize + secondaryItem.stackSize > secondaryItem.getMaxStackSize()) {
				return false;
			}
		}
		return inventory[1] == null || inventory[1].isItemEqual(primaryItem) && inventory[1].stackSize + primaryItem.stackSize <= primaryItem.getMaxStackSize();
	}

	@Override
	protected boolean hasValidInput() {

		RecipePulverizer recipe = PulverizerManager.getRecipe(inventory[0]);
		return recipe != null && recipe.getInput().stackSize <= inventory[0].stackSize;
	}

	@Override
	protected void processStart() {

		processMax = PulverizerManager.getRecipe(inventory[0]).getEnergy() * energyMod / ENERGY_BASE;
		processRem = processMax;
	}

	@Override
	protected void processFinish() {

		RecipePulverizer recipe = PulverizerManager.getRecipe(inventory[0]);

		if (recipe == null) {
			processOff();
			return;
		}
		ItemStack primaryItem = recipe.getPrimaryOutput();
		ItemStack secondaryItem = recipe.getSecondaryOutput();

		if (inventory[1] == null) {
			inventory[1] = ItemHelper.cloneStack(primaryItem);
		} else {
			inventory[1].stackSize += primaryItem.stackSize;
		}
		if (secondaryItem != null) {
			int recipeChance = recipe.getSecondaryOutputChance();
			if (recipeChance >= 100 || worldObj.rand.nextInt(secondaryChance) < recipeChance) {
				if (inventory[2] == null) {
					inventory[2] = ItemHelper.cloneStack(secondaryItem);
					if (secondaryChance < recipeChance && worldObj.rand.nextInt(secondaryChance) < recipeChance - secondaryChance) {
						inventory[2].stackSize += secondaryItem.stackSize;
					}
				} else if (inventory[2].isItemEqual(secondaryItem)) {
					inventory[2].stackSize += secondaryItem.stackSize;
					if (secondaryChance < recipeChance && worldObj.rand.nextInt(secondaryChance) < recipeChance - secondaryChance) {
						inventory[2].stackSize += secondaryItem.stackSize;
					}
				}
				if (inventory[2].stackSize > inventory[2].getMaxStackSize()) {
					inventory[2].stackSize = inventory[2].getMaxStackSize();
				}
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

		if (!enableAutoOutput) {
			return;
		}
		int side;
		if (inventory[1] != null) {
			for (int i = outputTrackerPrimary + 1; i <= outputTrackerPrimary + 6; i++) {
				side = i % 6;
				if (sideCache[side] == 2 || sideCache[side] == 4) {
					if (transferItem(1, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
						outputTrackerPrimary = side;
						break;
					}
				}
			}
		}
		if (inventory[2] == null) {
			return;
		}
		for (int i = outputTrackerSecondary + 1; i <= outputTrackerSecondary + 6; i++) {
			side = i % 6;
			if (sideCache[side] == 3 || sideCache[side] == 4) {
				if (transferItem(2, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					outputTrackerSecondary = side;
					break;
				}
			}
		}
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiPulverizer(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerPulverizer(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger("TrackIn");
		outputTrackerPrimary = nbt.getInteger("TrackOut1");
		outputTrackerSecondary = nbt.getInteger("TrackOut2");
		tank.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut1", outputTrackerPrimary);
		nbt.setInteger("TrackOut2", outputTrackerSecondary);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		//		payload.addBool(augmentTapper);
		payload.addFluidStack(tank.getFluid());
		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		//		augmentTapper = payload.getBool();
		//		flagTapper = augmentTapper;
		tank.setFluid(payload.getFluidStack());
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		augmentGeode = false;
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentGeode && TEProps.MACHINE_PULVERIZER_GEODE.equals(id)) {
			augmentGeode = true;
			hasModeAugment = true;
			energyMod += 25;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot != 0 || PulverizerManager.recipeExists(stack);
	}

}
