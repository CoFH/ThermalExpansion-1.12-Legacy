package thermalexpansion.block.machine;

import cofh.network.CoFHPacket;
import cofh.util.MathHelper;
import cofh.util.ServerHelper;
import cofh.util.fluid.FluidTankAdv;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.player.EntityPlayer;
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

import thermalexpansion.ThermalExpansion;
import thermalexpansion.core.TEProps;

public class TileExtruder extends TileMachineBase implements IFluidHandler {

	public static final int TYPE = BlockMachine.Types.EXTRUDER.ordinal();

	public static void initialize() {

		processItems[0] = new ItemStack(Blocks.cobblestone);
		processItems[1] = new ItemStack(Blocks.stone);
		processItems[2] = new ItemStack(Blocks.obsidian);

		String category = "tweak.crafting";

		processLava[0] = MathHelper.clampI(ThermalExpansion.config.get(category, "Extruder.Cobblestone.Lava", processLava[0]), 0, MAX_FLUID_SMALL);
		processLava[1] = MathHelper.clampI(ThermalExpansion.config.get(category, "Extruder.Stone.Lava", processLava[1]), 0, MAX_FLUID_SMALL);
		processLava[2] = MathHelper.clampI(ThermalExpansion.config.get(category, "Extruder.Obsidian.Lava", processLava[2]), 0, MAX_FLUID_SMALL);

		processWater[0] = MathHelper.clampI(ThermalExpansion.config.get(category, "Extruder.Cobblestone.Water", processWater[0]), 0, MAX_FLUID_SMALL);
		processWater[1] = MathHelper.clampI(ThermalExpansion.config.get(category, "Extruder.Stone.Water", processWater[1]), 0, MAX_FLUID_SMALL);
		processWater[2] = MathHelper.clampI(ThermalExpansion.config.get(category, "Extruder.Obsidian.Water", processWater[2]), 0, MAX_FLUID_SMALL);

		processTime[0] = MathHelper.clampI(ThermalExpansion.config.get(category, "Extruder.Cobblestone.Time", processTime[0]), 4, 72000);
		processTime[1] = MathHelper.clampI(ThermalExpansion.config.get(category, "Extruder.Stone.Time", processTime[1]), 4, 72000);
		processTime[2] = MathHelper.clampI(ThermalExpansion.config.get(category, "Extruder.Obsidian.Time", processTime[2]), 4, 72000);

		sideData[TYPE] = new SideConfig();
		sideData[TYPE].numGroup = 3;
		sideData[TYPE].slotGroups = new int[][] { {}, {}, { 0 } };
		sideData[TYPE].allowInsertion = new boolean[] { false, true, false };
		sideData[TYPE].allowExtraction = new boolean[] { false, false, true };
		sideData[TYPE].sideTex = new int[] { 0, 1, 4 };

		guiIds[TYPE] = ThermalExpansion.proxy.registerGui("Extruder", "machine", true);
		GameRegistry.registerTileEntity(TileExtruder.class, "thermalexpansion.Extruder");
	}

	public static int[] processLava = { 0, 0, 1000 };
	public static int[] processWater = { 0, 1000, 1000 };
	public static int[] processTime = { 40, 80, 120 };

	static ItemStack[] processItems = new ItemStack[3];

	FluidStack hotRenderFluid = new FluidStack(FluidRegistry.LAVA, 0);
	FluidStack coldRenderFluid = new FluidStack(FluidRegistry.WATER, 0);

	FluidTankAdv hotTank = new FluidTankAdv(MAX_FLUID_SMALL);
	FluidTankAdv coldTank = new FluidTankAdv(MAX_FLUID_SMALL);

	byte curSelection;
	byte prevSelection;

	int outputTracker;

	public TileExtruder() {

		sideCache = new byte[] { 2, 2, 1, 1, 1, 1 };
		inventory = new ItemStack[4];

		inventory[1] = processItems[0];
		inventory[2] = processItems[1];
		inventory[3] = processItems[2];
	}

	@Override
	public int getType() {

		return TYPE;
	}

	public boolean canStart() {

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

	public boolean canFinish() {

		return processRem <= 0;
	}

	protected void processStart() {

		processMax = processTime[curSelection];
		processRem = processMax;
		prevSelection = curSelection;
	}

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

	protected void transferProducts() {

		if (!upgradeAutoTransfer) {
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

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();

		payload.addFluidStack(hotRenderFluid);
		payload.addFluidStack(coldRenderFluid);
		return payload;
	}

	@Override
	public CoFHPacket getGuiCoFHPacket() {

		CoFHPacket payload = super.getGuiCoFHPacket();

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
	public CoFHPacket getFluidCoFHPacket() {

		CoFHPacket payload = super.getFluidCoFHPacket();

		payload.addFluidStack(hotRenderFluid);
		payload.addFluidStack(coldRenderFluid);

		return payload;
	}

	@Override
	public CoFHPacket getModeCoFHPacket() {

		CoFHPacket payload = super.getModeCoFHPacket();

		payload.addByte(curSelection);

		return payload;
	}

	public void setMode(int i) {

		byte lastSelection = curSelection;
		curSelection = (byte) i;

		if (ServerHelper.isClientWorld(worldObj)) {
			sendModePacket();
		}
		curSelection = lastSelection;
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(CoFHPacket payload, boolean isServer, EntityPlayer thePlayer) {

		switch (TEProps.PacketID.values()[payload.getByte()]) {
		case GUI:
			isActive = payload.getBool();
			processMax = payload.getInt();
			processRem = payload.getInt();
			curSelection = payload.getByte();
			prevSelection = payload.getByte();
			hotTank.setFluid(payload.getFluidStack());
			coldTank.setFluid(payload.getFluidStack());
			return;
		case FLUID:
			hotRenderFluid = payload.getFluidStack();
			coldRenderFluid = payload.getFluidStack();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			return;
		case MODE:
			curSelection = payload.getByte();

			if (!isActive) {
				prevSelection = curSelection;
			}
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
			return;
		default:
		}
	}

	/* GUI METHODS */
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

	/* IInventory */
	@Override
	public int getSizeInventory() {

		return inventory.length - 3;
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
