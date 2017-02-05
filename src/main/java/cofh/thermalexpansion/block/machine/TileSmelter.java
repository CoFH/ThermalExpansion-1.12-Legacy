package cofh.thermalexpansion.block.machine;

import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.machine.GuiSmelter;
import cofh.thermalexpansion.gui.container.machine.ContainerSmelter;
import cofh.thermalexpansion.util.crafting.SmelterManager;
import cofh.thermalexpansion.util.crafting.SmelterManager.RecipeSmelter;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileSmelter extends TileMachineBase {

	private static final int TYPE = BlockMachine.Type.SMELTER.getMetadata();

	public static void initialize() {

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numConfig = 8;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, { 0, 1 }, { 2, 3 }, { 4 }, { 2, 3, 4 }, { 0 }, { 1 }, { 0, 1, 2, 3, 4 } };
		defaultSideConfig[TYPE].allowInsertionSide = new boolean[] { false, true, false, false, false, true, true, true };
		defaultSideConfig[TYPE].allowExtractionSide = new boolean[] { false, true, true, true, true, false, false, true };
		defaultSideConfig[TYPE].allowInsertionSlot = new boolean[] { true, true, false, false, false, false };
		defaultSideConfig[TYPE].allowExtractionSlot = new boolean[] { true, true, true, true, true, false };
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 3, 1, 2, 2, 2, 2 };

		GameRegistry.registerTileEntity(TileSmelter.class, "thermalexpansion:machine_smelter");

		config();
	}

	public static void config() {

		String category = "Machine.Smelter";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setDefaultParams(20);
	}

	private int inputTrackerPrimary;
	private int inputTrackerSecondary;
	private int outputTrackerPrimary;
	private int outputTrackerSecondary;

	public boolean lockPrimary = false;

	/* AUGMENTS */
	public boolean augmentPyrotheum;

	public TileSmelter() {

		super();
		inventory = new ItemStack[2 + 2 + 1 + 1];
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

		if (inventory[0] == null && inventory[1] == null) {
			return false;
		}
		RecipeSmelter recipe = SmelterManager.getRecipe(inventory[0], inventory[1]);

		if (recipe == null) {
			return false;
		}
		if (SmelterManager.isRecipeReversed(inventory[0], inventory[1])) {
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

		if (!augmentSecondaryNull && secondaryItem != null && inventory[4] != null) {
			if (!inventory[4].isItemEqual(secondaryItem)) {
				return false;
			}
			if (inventory[4].stackSize + secondaryItem.stackSize > secondaryItem.getMaxStackSize()) {
				return false;
			}
		}
		final int start = 2, end = start + 2;

		int room = 0;
		for (int i = start; i < end; ++i) {
			ItemStack stack = inventory[i];
			if (stack == null) {
				return true;
			}
			if (!stack.isItemEqual(primaryItem)) {
				continue;
			}
			room += stack.getMaxStackSize() - stack.stackSize;
		}
		return room >= primaryItem.stackSize;
	}

	@Override
	protected boolean hasValidInput() {

		RecipeSmelter recipe = SmelterManager.getRecipe(inventory[0], inventory[1]);

		if (recipe == null) {
			return false;
		}
		if (SmelterManager.isRecipeReversed(inventory[0], inventory[1])) {
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

		processMax = SmelterManager.getRecipe(inventory[0], inventory[1]).getEnergy() * energyMod / ENERGY_BASE;
		processRem = processMax;
	}

	@Override
	protected void processFinish() {

		RecipeSmelter recipe = SmelterManager.getRecipe(inventory[0], inventory[1]);

		if (recipe == null) {
			isActive = false;
			wasActive = true;
			tracker.markTime(worldObj);
			processRem = 0;
			return;
		}
		ItemStack primaryItem = recipe.getPrimaryOutput();
		ItemStack secondaryItem = recipe.getSecondaryOutput();

		final int start = 2, end = start + 2;

		int outputAmt = primaryItem.stackSize;

		if (augmentPyrotheum && (inventory[0] == ItemMaterial.dustPyrotheum || inventory[1] == ItemMaterial.dustPyrotheum)) {
			if (ItemHelper.isOre(inventory[0]) || ItemHelper.isOre(inventory[1])) {
				++outputAmt;
			}
		}
		for (int i = start; i < end; ++i) {
			ItemStack stack = inventory[i];
			if (stack == null) {
				inventory[i] = ItemHelper.cloneStack(primaryItem);
				break;
			}
			if (!stack.isItemEqual(primaryItem)) {
				continue;
			}
			int add = Math.min(stack.stackSize + outputAmt, stack.getMaxStackSize()) - stack.stackSize;
			outputAmt -= add;
			stack.stackSize += add;
			if (outputAmt == 0) {
				break;
			}
		}
		if (secondaryItem != null) {
			int recipeChance = recipe.getSecondaryOutputChance();
			if (recipeChance >= 100 || worldObj.rand.nextInt(secondaryChance) < recipeChance) {
				if (inventory[4] == null) {
					inventory[4] = ItemHelper.cloneStack(secondaryItem);

					if (secondaryChance < recipeChance && worldObj.rand.nextInt(secondaryChance) < recipeChance - secondaryChance) {
						inventory[4].stackSize += secondaryItem.stackSize;
					}
				} else if (inventory[4].isItemEqual(secondaryItem)) {
					inventory[4].stackSize += secondaryItem.stackSize;

					if (secondaryChance < recipeChance && worldObj.rand.nextInt(secondaryChance) < recipeChance - secondaryChance) {
						inventory[4].stackSize += secondaryItem.stackSize;
					}
				}
				if (inventory[4].stackSize > inventory[4].getMaxStackSize()) {
					inventory[4].stackSize = inventory[4].getMaxStackSize();
				}
			}
		}
		if (SmelterManager.isRecipeReversed(inventory[0], inventory[1])) {
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
		if (inventory[2] != null || inventory[3] != null) {
			for (int i = outputTrackerPrimary + 1; i <= outputTrackerPrimary + 6; i++) {
				side = i % 6;
				if (sideCache[side] == 2 || sideCache[side] == 4) {
					if (transferItem(2, ITEM_TRANSFER[level] >> 1, EnumFacing.VALUES[side])) {
						if (!transferItem(3, ITEM_TRANSFER[level] >> 1, EnumFacing.VALUES[side])) {
							transferItem(2, ITEM_TRANSFER[level] >> 1, EnumFacing.VALUES[side]);
						}
						outputTrackerPrimary = side;
						break;
					} else if (transferItem(3, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
						outputTrackerPrimary = side;
						break;
					}
				}
			}
		}
		if (inventory[4] == null) {
			return;
		}
		for (int i = outputTrackerSecondary + 1; i <= outputTrackerSecondary + 6; i++) {
			side = i % 6;
			if (sideCache[side] == 3 || sideCache[side] == 4) {
				if (transferItem(4, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
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

		return new GuiSmelter(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerSmelter(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTrackerPrimary = nbt.getInteger("TrackIn1");
		inputTrackerSecondary = nbt.getInteger("TrackIn2");
		outputTrackerPrimary = nbt.getInteger("TrackOut1");
		outputTrackerSecondary = nbt.getInteger("TrackOut2");
		lockPrimary = nbt.getBoolean("SlotLock");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn1", inputTrackerPrimary);
		nbt.setInteger("TrackIn2", inputTrackerSecondary);
		nbt.setInteger("TrackOut1", outputTrackerPrimary);
		nbt.setInteger("TrackOut2", outputTrackerSecondary);
		nbt.setBoolean("SlotLock", lockPrimary);
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addBool(lockPrimary);

		return payload;
	}

	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addBool(lockPrimary);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		lockPrimary = payload.getBool();
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		lockPrimary = payload.getBool();
		markDirty();
		callNeighborTileChange();
	}

	public void setMode(boolean mode) {

		boolean lastMode = lockPrimary;
		lockPrimary = mode;
		sendModePacket();
		lockPrimary = lastMode;
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		if (lockPrimary) {
			if (slot == 0) {
				return SmelterManager.isItemFlux(stack);
			}
			if (slot == 1) {
				return !SmelterManager.isItemFlux(stack) && SmelterManager.isItemValid(stack);
			}
		}
		return slot > 1 || SmelterManager.isItemValid(stack);
	}

}
