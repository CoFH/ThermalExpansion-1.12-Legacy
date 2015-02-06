package cofh.thermalexpansion.block.machine;

import cofh.api.core.ICustomInventory;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.client.machine.GuiExtruder;
import cofh.thermalexpansion.gui.container.machine.ContainerExtruder;
import cpw.mods.fml.common.registry.GameRegistry;

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

	static final int TYPE = BlockMachine.Types.EXTRUDER.ordinal();

	public static void initialize() {

		processItems[0] = new ItemStack(Blocks.cobblestone);
		processItems[1] = new ItemStack(Blocks.stone);
		processItems[2] = new ItemStack(Blocks.obsidian);

		String category = "tweak.crafting";

		processLava[0] = MathHelper.clampI(ThermalExpansion.config.get(category, "Extruder.Cobblestone.Lava", processLava[0]), 0, TEProps.MAX_FLUID_SMALL);
		processLava[1] = MathHelper.clampI(ThermalExpansion.config.get(category, "Extruder.Stone.Lava", processLava[1]), 0, TEProps.MAX_FLUID_SMALL);
		processLava[2] = MathHelper.clampI(ThermalExpansion.config.get(category, "Extruder.Obsidian.Lava", processLava[2]), 0, TEProps.MAX_FLUID_SMALL);

		processWater[0] = MathHelper.clampI(ThermalExpansion.config.get(category, "Extruder.Cobblestone.Water", processWater[0]), 0, TEProps.MAX_FLUID_SMALL);
		processWater[1] = MathHelper.clampI(ThermalExpansion.config.get(category, "Extruder.Stone.Water", processWater[1]), 0, TEProps.MAX_FLUID_SMALL);
		processWater[2] = MathHelper.clampI(ThermalExpansion.config.get(category, "Extruder.Obsidian.Water", processWater[2]), 0, TEProps.MAX_FLUID_SMALL);

		processTime[0] = MathHelper.clampI(ThermalExpansion.config.get(category, "Extruder.Cobblestone.Time", processTime[0]), 4, 72000);
		processTime[1] = MathHelper.clampI(ThermalExpansion.config.get(category, "Extruder.Stone.Time", processTime[1]), 4, 72000);
		processTime[2] = MathHelper.clampI(ThermalExpansion.config.get(category, "Extruder.Obsidian.Time", processTime[2]), 4, 72000);

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numGroup = 3;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, {}, { 0 } };
		defaultSideConfig[TYPE].allowInsertion = new boolean[] { false, true, false };
		defaultSideConfig[TYPE].allowExtraction = new boolean[] { false, false, true };
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 1, 4 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		defaultEnergyConfig[TYPE] = new EnergyConfig();
		defaultEnergyConfig[TYPE].setParamsPower(0);

		GameRegistry.registerTileEntity(TileExtruder.class, "thermalexpansion.Extruder");
	}

	static int[] processLava = { 0, 0, 1000 };
	static int[] processWater = { 0, 1000, 1000 };
	static int[] processTime = { 40, 80, 120 };
	static ItemStack[] processItems = new ItemStack[3];

	int outputTracker;
	byte curSelection;
	byte prevSelection;
	FluidStack hotRenderFluid = new FluidStack(FluidRegistry.LAVA, 0);
	FluidStack coldRenderFluid = new FluidStack(FluidRegistry.WATER, 0);
	FluidTankAdv hotTank = new FluidTankAdv(TEProps.MAX_FLUID_SMALL);
	FluidTankAdv coldTank = new FluidTankAdv(TEProps.MAX_FLUID_SMALL);

	public TileExtruder() {

		super();

		inventory = new ItemStack[1];
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void updateEntity() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		boolean curActive = isActive;

		if (isActive) {
			if (processRem > 0) {
				processRem--;
			} else if (canFinish()) {
				processFinish();
				transferProducts();
				processRem = processMax;

				if (redstoneControlOrDisable() && canStart()) {
					processStart();
				} else {
					isActive = false;
					wasActive = true;
					tracker.markTime(worldObj);
				}
			}
		} else if (redstoneControlOrDisable()) {
			if (timeCheck()) {
				transferProducts();
			}
			if (timeCheckEighth() && canStart()) {
				processStart();
				processRem--;
				isActive = true;
			}
		}
		updateIfChanged(curActive);
	}

	@Override
	protected boolean canStart() {

		if (hotTank.getFluidAmount() < FluidContainerRegistry.BUCKET_VOLUME || coldTank.getFluidAmount() < FluidContainerRegistry.BUCKET_VOLUME) {
			return false;
		}
		if (inventory[0] == null) {
			return true;
		}
		if (!inventory[0].isItemEqual(processItems[curSelection])) {
			return false;
		}
		return inventory[0].stackSize + processItems[curSelection].stackSize <= processItems[curSelection].getMaxStackSize();
	}

	@Override
	protected boolean canFinish() {

		return processRem <= 0;
	}

	@Override
	protected void processStart() {

		processMax = processTime[curSelection];
		processRem = processMax;
		prevSelection = curSelection;
	}

	@Override
	protected void processFinish() {

		if (inventory[0] == null) {
			inventory[0] = processItems[prevSelection].copy();
		} else {
			inventory[0].stackSize += processItems[prevSelection].stackSize;
		}
		hotTank.drain(processLava[prevSelection], true);
		coldTank.drain(processWater[prevSelection], true);
		prevSelection = curSelection;
	}

	@Override
	protected void transferProducts() {

		if (!augmentAutoTransfer) {
			return;
		}
		if (inventory[0] == null) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;

			if (sideCache[side] == 2) {
				if (transferItem(0, 4, side)) {
					outputTracker = side;
					break;
				}
			}
		}
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
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();
		payload.addFluidStack(hotRenderFluid);
		payload.addFluidStack(coldRenderFluid);
		return payload;
	}

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

	/* ICustomInventory */
	@Override
	public ItemStack[] getInventorySlots(int inventoryIndex) {

		return processItems;
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
