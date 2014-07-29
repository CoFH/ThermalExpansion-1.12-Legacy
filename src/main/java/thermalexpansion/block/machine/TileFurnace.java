package thermalexpansion.block.machine;

import cofh.CoFHCore;
import cofh.util.CoreUtils;
import cofh.util.MathHelper;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.gui.client.machine.GuiFurnace;
import thermalexpansion.gui.container.machine.ContainerFurnace;
import thermalexpansion.util.crafting.FurnaceManager;
import thermalexpansion.util.crafting.FurnaceManager.RecipeFurnace;

public class TileFurnace extends TileMachineBase {

	static final int TYPE = BlockMachine.Types.FURNACE.ordinal();

	public static void initialize() {

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numGroup = 3;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1 } };
		defaultSideConfig[TYPE].allowInsertion = new boolean[] { false, true, false };
		defaultSideConfig[TYPE].allowExtraction = new boolean[] { false, true, true };
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 1, 4 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		int maxPower = MathHelper.clampI(ThermalExpansion.config.get("block.tweak", "Machine.Furnace.BasePower", 20), 10, 500);
		ThermalExpansion.config.set("block.tweak", "Machine.Furnace.BasePower", maxPower);
		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setParamsPower(maxPower);

		sounds[TYPE] = CoreUtils.getSoundName(ThermalExpansion.modId, "blockMachineFurnace");
		enableSound[TYPE] = CoFHCore.configClient.get("sound", "Machine.Furnace", true);

		GameRegistry.registerTileEntity(TileFurnace.class, "thermalexpansion.Furnace");
	}

	int outputTracker;

	public TileFurnace() {

		super();

		inventory = new ItemStack[1 + 1 + 1];
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	protected boolean canStart() {

		if (inventory[0] == null) {
			return false;
		}
		RecipeFurnace recipe = FurnaceManager.getRecipe(inventory[0]);

		if (recipe == null || energyStorage.getEnergyStored() < recipe.getEnergy() * energyMod / processMod) {
			return false;
		}
		ItemStack output = recipe.getOutput();

		if (output == null) {
			return false;
		}
		if (inventory[1] == null) {
			return true;
		}
		if (!inventory[1].isItemEqual(output)) {
			return false;
		}
		return inventory[1].stackSize + output.stackSize <= output.getMaxStackSize();
	}

	@Override
	protected boolean hasValidInput() {

		RecipeFurnace recipe = FurnaceManager.getRecipe(inventory[0]);
		return recipe == null ? false : recipe.getInput().stackSize <= inventory[0].stackSize;
	}

	@Override
	protected void processStart() {

		processMax = FurnaceManager.getRecipe(inventory[0]).getEnergy();
		processRem = processMax;
	}

	@Override
	protected void processFinish() {

		ItemStack output = FurnaceManager.getRecipe(inventory[0]).getOutput();
		if (inventory[1] == null) {
			inventory[1] = output;
		} else {
			inventory[1].stackSize += output.stackSize;
		}
		inventory[0].stackSize--;

		if (inventory[0].stackSize <= 0) {
			inventory[0] = null;
		}
	}

	@Override
	protected void transferProducts() {

		if (!augmentAutoTransfer) {
			return;
		}
		if (inventory[1] == null) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;

			if (sideCache[side] == 2) {
				if (transferItem(1, 4, side)) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	@Override
	public boolean isItemValid(ItemStack stack, int slot, int side) {

		return slot == 0 ? FurnaceManager.recipeExists(stack) : true;
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

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		outputTracker = nbt.getInteger("Tracker");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("Tracker", outputTracker);
	}

}
