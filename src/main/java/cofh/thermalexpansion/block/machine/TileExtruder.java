package cofh.thermalexpansion.block.machine;

import cofh.api.core.ICustomInventory;
import cofh.api.item.IAugmentItem;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine.Types;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.client.machine.GuiExtruder;
import cofh.thermalexpansion.gui.container.machine.ContainerExtruder;
import cofh.thermalexpansion.item.TEAugments;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileExtruder extends TileMachineBase implements ICustomInventory, IFluidHandler {

	public byte processLevel;

	public static void initialize() {

		int type = BlockMachine.Types.EXTRUDER.ordinal();

		processItems = new ItemStack[3];

		processItems[0] = new ItemStack(Blocks.cobblestone);
		processItems[1] = new ItemStack(Blocks.stone);
		processItems[2] = new ItemStack(Blocks.obsidian);

		String category = "RecipeManagers.Extruder.Recipes";

		processLava[0] = MathHelper.clamp(ThermalExpansion.config.get(category, "Cobblestone.Lava", processLava[0]), 0, TEProps.MAX_FLUID_SMALL);
		processLava[1] = MathHelper.clamp(ThermalExpansion.config.get(category, "Stone.Lava", processLava[1]), 0, TEProps.MAX_FLUID_SMALL);
		processLava[2] = MathHelper.clamp(ThermalExpansion.config.get(category, "Obsidian.Lava", processLava[2]), 0, TEProps.MAX_FLUID_SMALL);

		processWater[0][0] = MathHelper.clamp(ThermalExpansion.config.get(category, "Cobblestone.Water", processWater[0][0]), 0, TEProps.MAX_FLUID_SMALL);
		processWater[0][1] = MathHelper.clamp(ThermalExpansion.config.get(category, "Stone.Water", processWater[0][1]), 0, TEProps.MAX_FLUID_SMALL);
		processWater[0][2] = MathHelper.clamp(ThermalExpansion.config.get(category, "Obsidian.Water", processWater[0][2]), 0, TEProps.MAX_FLUID_SMALL);

		for (int i = 1; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				processWater[i][j] = processWater[i - 1][j] / 2;
			}
		}

		ThermalExpansion.config.removeProperty(category, "Cobblestone.Time");
		ThermalExpansion.config.removeProperty(category, "Stone.Time");
		ThermalExpansion.config.removeProperty(category, "Obsidian.Time");

		defaultSideConfig[type] = new SideConfig();
		defaultSideConfig[type].numConfig = 4;
		defaultSideConfig[type].slotGroups = new int[][] { {}, {}, { 0 }, { 0 } };
		defaultSideConfig[type].allowInsertionSide = new boolean[] { false, true, false, true };
		defaultSideConfig[type].allowExtractionSide = new boolean[] { false, false, true, true };
		defaultSideConfig[type].allowInsertionSlot = new boolean[] { false, false };
		defaultSideConfig[type].allowExtractionSlot = new boolean[] { true, false };
		defaultSideConfig[type].sideTex = new int[] { 0, 1, 4, 7 };
		defaultSideConfig[type].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		defaultEnergyConfig[type] = new EnergyConfig();
		defaultEnergyConfig[type].setParamsPower(0);

		GameRegistry.registerTileEntity(TileExtruder.class, "thermalexpansion.Extruder");
	}

	static int[] processLava = { 0, 0, 1000 };
	static int[][] processWater = { { 0, 1000, 1000 }, { 0, 500, 500 }, { 0, 250, 250 }, { 0, 125, 125 } };
	static int[][] processTime = { { 40, 80, 120 }, { 40, 80, 60 }, { 40, 80, 30 }, { 40, 80, 15 } };
	static ItemStack[] processItems = new ItemStack[3];

	ItemStack[] outputItems = new ItemStack[3];

	int outputTracker;
	byte curSelection;
	byte prevSelection;
	FluidStack hotRenderFluid = new FluidStack(FluidRegistry.LAVA, 0);
	FluidStack coldRenderFluid = new FluidStack(FluidRegistry.WATER, 0);
	FluidTankAdv hotTank = new FluidTankAdv(TEProps.MAX_FLUID_SMALL);
	FluidTankAdv coldTank = new FluidTankAdv(TEProps.MAX_FLUID_SMALL);

	public TileExtruder() {

		super(Types.EXTRUDER);
		inventory = new ItemStack[1];

		for (int i = 0; i < 3; i++) {
			outputItems[i] = processItems[i].copy();
		}
	}

	@Override
	public void updateEntity() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		boolean curActive = isActive;

		if (isActive) {
			if (processRem > 0) {
				processRem -= processMod;
			}
			if (canFinish()) {
				processFinish();
				transferOutput();
				processRem = processMax;

				if (!redstoneControlOrDisable() || !canStart()) {
					isActive = false;
					wasActive = true;
					tracker.markTime(worldObj);
				} else {
					processStart();
				}
			}
		} else if (redstoneControlOrDisable()) {
			if (timeCheck()) {
				transferOutput();
			}
			if (timeCheckEighth() && canStart()) {
				processStart();
				processRem -= processMod;
				isActive = true;
			}
		}
		updateIfChanged(curActive);
	}

	@Override
	protected boolean canStart() {

		if (hotTank.getFluidAmount() < Math.max(FluidContainerRegistry.BUCKET_VOLUME / 8, processLava[curSelection])
				|| coldTank.getFluidAmount() < Math.max(FluidContainerRegistry.BUCKET_VOLUME / 8, processWater[processLevel][curSelection])) {
			return false;
		}
		if (inventory[0] == null) {
			return true;
		}
		if (!inventory[0].isItemEqual(outputItems[curSelection])) {
			return false;
		}
		return inventory[0].stackSize != outputItems[curSelection].getMaxStackSize();
	}

	@Override
	protected boolean canFinish() {

		return processRem <= 0;
	}

	@Override
	protected void processStart() {

		processMax = processTime[processLevel][curSelection];
		processRem = processMax;
		prevSelection = curSelection;
	}

	@Override
	protected void processFinish() {

		int maxCreate = Math.min(
				outputItems[prevSelection].stackSize,
				Math.min(hotTank.getFluidAmount() / Math.max(1, processLava[prevSelection]),
						coldTank.getFluidAmount() / Math.max(1, processWater[processLevel][prevSelection])));

		if (inventory[0] == null) {
			inventory[0] = ItemHelper.cloneStack(outputItems[prevSelection], maxCreate);
		} else {
			inventory[0].stackSize += maxCreate;
			int maxStack = inventory[0].getMaxStackSize();
			if (inventory[0].stackSize > maxStack) {
				maxCreate -= inventory[0].stackSize - maxStack;
				inventory[0].stackSize = maxStack;
			}
		}
		hotTank.drain(processLava[prevSelection] * maxCreate, true);
		coldTank.drain(processWater[processLevel][prevSelection] * maxCreate, true);
		prevSelection = curSelection;
	}

	@Override
	protected void transferOutput() {

		if (!augmentAutoOutput) {
			return;
		}
		if (inventory[0] == null) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;

			if (sideCache[side] == 2) {
				if (transferItem(0, AUTO_TRANSFER[level], side)) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	@Override
	protected void onLevelChange() {

		super.onLevelChange();

		hotTank.setCapacity(TEProps.MAX_FLUID_SMALL * FLUID_CAPACITY[level]);
		coldTank.setCapacity(TEProps.MAX_FLUID_SMALL * FLUID_CAPACITY[level]);
	}

	@Override
	protected boolean readPortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		if (!super.readPortableTagInternal(player, tag)) {
			return false;
		}
		if (tag.hasKey("Sel")) {
			curSelection = tag.getByte("Sel");
			if (!isActive) {
				prevSelection = curSelection;
			}
		}
		return true;
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		if (!super.writePortableTagInternal(player, tag)) {
			return false;
		}
		tag.setByte("Sel", curSelection);
		return true;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiExtruder(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerExtruder(inventory, this);
	}

	public int getCurSelection() {

		return curSelection;
	}

	public int getPrevSelection() {

		return prevSelection;
	}

	public FluidTankAdv getTank(int tankIndex) {

		if (tankIndex == 0) {
			return hotTank;
		}
		return coldTank;
	}

	public FluidStack getTankFluid(int tankIndex) {

		if (tankIndex == 0) {
			return hotTank.getFluid();
		}
		return coldTank.getFluid();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		outputTracker = nbt.getInteger("Tracker");
		prevSelection = nbt.getByte("Prev");
		curSelection = nbt.getByte("Sel");

		hotTank.readFromNBT(nbt.getCompoundTag("HotTank"));
		coldTank.readFromNBT(nbt.getCompoundTag("ColdTank"));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("Tracker", outputTracker);
		nbt.setByte("Prev", prevSelection);
		nbt.setByte("Sel", curSelection);

		nbt.setTag("HotTank", hotTank.writeToNBT(new NBTTagCompound()));
		nbt.setTag("ColdTank", coldTank.writeToNBT(new NBTTagCompound()));
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();
		payload.addByte(curSelection);
		payload.addByte(prevSelection);

		if (hotTank.getFluid() == null) {
			payload.addFluidStack(hotRenderFluid);
		} else {
			payload.addFluidStack(hotTank.getFluid());
		}
		if (coldTank.getFluid() == null) {
			payload.addFluidStack(coldRenderFluid);
		} else {
			payload.addFluidStack(coldTank.getFluid());
		}
		payload.addByte(processLevel);
		return payload;
	}

	@Override
	public PacketCoFHBase getFluidPacket() {

		PacketCoFHBase payload = super.getFluidPacket();
		payload.addFluidStack(hotRenderFluid);
		payload.addFluidStack(coldRenderFluid);
		return payload;
	}

	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();
		payload.addByte(curSelection);
		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		curSelection = payload.getByte();
		prevSelection = payload.getByte();
		hotTank.setFluid(payload.getFluidStack());
		coldTank.setFluid(payload.getFluidStack());

		byte tempLevel = processLevel;

		processLevel = payload.getByte();

		if (tempLevel != processLevel) {
			for (int i = 0; i < 3; i++) {
				outputItems[i].stackSize = TEAugments.MACHINE_EXTRUDER_PROCESS_MOD[i][processLevel];
			}
		}
	}

	@Override
	protected void handleFluidPacket(PacketCoFHBase payload) {

		super.handleFluidPacket(payload);
		hotRenderFluid = payload.getFluidStack();
		coldRenderFluid = payload.getFluidStack();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);
		curSelection = payload.getByte();
		if (!isActive) {
			prevSelection = curSelection;
		}
	}

	public void setMode(int i) {

		byte lastSelection = curSelection;
		curSelection = (byte) i;
		sendModePacket();
		curSelection = lastSelection;
	}

	/* AUGMENT HELPERS */
	@Override
	protected boolean installAugment(int slot) {

		IAugmentItem augmentItem = (IAugmentItem) augments[slot].getItem();
		boolean installed = false;

		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.MACHINE_SPEED) > 0) {
			return false;
		}
		if (augmentItem.getAugmentLevel(augments[slot], TEAugments.MACHINE_EXTRUDER_BOOST) > 0) {
			int augLevel = augmentItem.getAugmentLevel(augments[slot], TEAugments.MACHINE_EXTRUDER_BOOST);

			if (augLevel > level) {
				return false;
			}
			if (hasDuplicateAugment(TEAugments.MACHINE_EXTRUDER_BOOST, augLevel, slot)) {
				return false;
			}
			if (hasAugmentChain(TEAugments.MACHINE_EXTRUDER_BOOST, augLevel)) {
				processLevel = (byte) Math.max(augLevel, processLevel);
				for (int i = 0; i < 3; i++) {
					outputItems[i].stackSize = TEAugments.MACHINE_EXTRUDER_PROCESS_MOD[i][processLevel];
				}
			} else {
				return false;
			}
			installed = true;
		}
		return installed ? true : super.installAugment(slot);
	}

	@Override
	protected void onInstalled() {

		super.onInstalled();

		for (int i = 0; i < 3; i++) {
			outputItems[i].stackSize = TEAugments.MACHINE_EXTRUDER_PROCESS_MOD[i][processLevel];
		}
	}

	@Override
	protected void resetAugments() {

		super.resetAugments();

		processLevel = 0;
		for (int i = 0; i < 3; i++) {
			outputItems[i].stackSize = 1;
		}
	}

	/* ICustomInventory */
	@Override
	public ItemStack[] getInventorySlots(int inventoryIndex) {

		return outputItems;
	}

	@Override
	public int getSlotStackLimit(int slotIndex) {

		return 64;
	}

	@Override
	public void onSlotUpdate() {

		markDirty();
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if (from != ForgeDirection.UNKNOWN && sideCache[from.ordinal()] != 1) {
			return 0;
		}
		if (resource.getFluid() == FluidRegistry.LAVA) {
			return hotTank.fill(resource, doFill);
		} else if (resource.getFluid() == FluidRegistry.WATER) {
			return coldTank.fill(resource, doFill);
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {

		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {

		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return new FluidTankInfo[] { hotTank.getInfo(), coldTank.getInfo() };
	}

}
