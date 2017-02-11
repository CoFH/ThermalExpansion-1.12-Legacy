package cofh.thermalexpansion.block.machine;

import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.machine.GuiCompactor;
import cofh.thermalexpansion.gui.container.machine.ContainerCompactor;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.crafting.CompactorManager;
import cofh.thermalexpansion.util.crafting.CompactorManager.Mode;
import cofh.thermalexpansion.util.crafting.CompactorManager.RecipeCompactor;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;

public class TileCompactor extends TileMachineBase {

	private static final int TYPE = BlockMachine.Type.COMPACTOR.getMetadata();
	private static final Mode[] VALUES = new Mode[3];

	public static void initialize() {

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numConfig = 4;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1 }, { 0, 1 } };
		defaultSideConfig[TYPE].allowInsertionSide = new boolean[] { false, true, false, true };
		defaultSideConfig[TYPE].allowExtractionSide = new boolean[] { false, true, true, true };
		defaultSideConfig[TYPE].allowInsertionSlot = new boolean[] { true, false, false };
		defaultSideConfig[TYPE].allowExtractionSlot = new boolean[] { true, true, false };
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 1, 4, 7 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		validAugments[TYPE] = new ArrayList<String>();
		validAugments[TYPE].add(TEProps.MACHINE_COMPACTOR_MINT);

		VALUES[0] = Mode.PRESS;
		VALUES[1] = Mode.STORAGE;
		VALUES[2] = Mode.MINT;

		GameRegistry.registerTileEntity(TileCompactor.class, "thermalexpansion:machine_compactor");

		config();
	}

	public static void config() {

		String category = "Machine.Compactor";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setDefaultParams(20);
	}

	private int inputTracker;
	private int outputTracker;

	public byte modeFlag;
	private byte mode;

	public TileCompactor() {

		super();
		inventory = new ItemStack[1 + 1 + 1];
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
		RecipeCompactor recipe = CompactorManager.getRecipe(inventory[0], VALUES[mode]);

		if (recipe == null) {
			return false;
		}
		ItemStack output = recipe.getOutput();

		return inventory[1] == null || inventory[1].isItemEqual(output) && inventory[1].stackSize + output.stackSize <= output.getMaxStackSize();
	}

	@Override
	protected boolean hasValidInput() {

		RecipeCompactor recipe = CompactorManager.getRecipe(inventory[0], Mode.values()[mode]);

		return recipe != null && recipe.getInput().stackSize <= inventory[0].stackSize;
	}

	@Override
	protected void processStart() {

		processMax = CompactorManager.getRecipe(inventory[0], VALUES[mode]).getEnergy() * energyMod / ENERGY_BASE;
		processRem = processMax;
	}

	@Override
	protected void processFinish() {

		RecipeCompactor recipe = CompactorManager.getRecipe(inventory[0], VALUES[mode]);

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
		if (inventory[1] == null) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (sideCache[side] == 2) {
				if (transferItem(1, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiCompactor(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerCompactor(inventory, this);
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addByte(mode);
		payload.addByte(modeFlag);

		return payload;
	}

	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addByte(modeFlag);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		mode = payload.getByte();
		modeFlag = payload.getByte();
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		modeFlag = payload.getByte();
		if (!isActive) {
			mode = modeFlag;
		}
		markDirty();
		callNeighborTileChange();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger("TrackIn");
		outputTracker = nbt.getInteger("TrackOut");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut", outputTracker);
		return nbt;
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot != 0 || CompactorManager.isItemValid(stack);
	}

}
