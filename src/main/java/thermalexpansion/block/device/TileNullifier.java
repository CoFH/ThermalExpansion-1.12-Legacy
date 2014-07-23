package thermalexpansion.block.device;

import cofh.render.IconRegistry;
import cofh.render.RenderHelper;
import cofh.util.FluidHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.TileAugmentable;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.client.device.GuiNullifier;
import thermalexpansion.gui.container.device.ContainerNullifier;

public class TileNullifier extends TileAugmentable implements IFluidHandler {

	static final int TYPE = BlockDevice.Types.NULLIFIER.ordinal();
	static SideConfig defaultSideConfig = new SideConfig();

	public static void initialize() {

		defaultSideConfig = new SideConfig();
		defaultSideConfig.numGroup = 2;
		defaultSideConfig.slotGroups = new int[][] { {}, {}, {} };
		defaultSideConfig.allowInsertion = new boolean[] { false, false, false };
		defaultSideConfig.allowExtraction = new boolean[] { false, false, false };
		defaultSideConfig.sideTex = new int[] { 0, 1, 4 };
		defaultSideConfig.defaultSides = new byte[] { 0, 0, 0, 0, 0, 0 };

		GameRegistry.registerTileEntity(TileNullifier.class, "thermalexpansion.Nullifier");
		configure();
	}

	public static void configure() {

		String comment = "Enable this to allow for Nullifiers to be securable. (Default: true)";
		enableSecurity = ThermalExpansion.config.get("security", "Device.Nullifier.Securable", enableSecurity, comment);
	}

	public static boolean enableSecurity = true;

	protected static final int[] SLOTS = { 0 };
	protected static final Fluid renderFluid = FluidRegistry.LAVA;

	public TileNullifier() {

		sideConfig = defaultSideConfig;

		inventory = new ItemStack[1];
	}

	@Override
	public boolean canUpdate() {

		return false;
	}

	@Override
	public void setDefaultSides() {

		sideCache = getDefaultSides();
		sideCache[facing] = 1;
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.device." + BlockDevice.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public int getLightValue() {

		return FluidHelper.getFluidLuminosity(renderFluid);
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity;
	}

	@Override
	public boolean sendRedstoneUpdates() {

		return true;
	}

	protected boolean isSideAccessible(int side) {

		return sideCache[side] == 1 && redstoneControlOrDisable();
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiNullifier(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerNullifier(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readInventoryFromNBT(NBTTagCompound nbt) {

	}

	@Override
	public void writeInventoryToNBT(NBTTagCompound nbt) {

	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		return isSideAccessible(from.ordinal()) ? resource.amount : 0;
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

		return null;
	}

	/* IInventory */
	@Override
	public ItemStack getStackInSlot(int slot) {

		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		if (inventory[slot] == null) {
			return null;
		}
		if (inventory[slot].stackSize <= amount) {
			amount = inventory[slot].stackSize;
		}
		ItemStack stack = inventory[slot].splitStack(amount);

		if (inventory[slot].stackSize <= 0) {
			inventory[slot] = null;
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {

		if (inventory[slot] == null) {
			return null;
		}
		ItemStack stack = inventory[slot];
		inventory[slot] = null;
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		if (slot == 0) {
			return;
		}
		inventory[slot] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
	}

	/* IReconfigurableFacing */
	@Override
	public boolean allowYAxisFacing() {

		return true;
	}

	@Override
	public boolean setFacing(int side) {

		if (side < 0 || side > 5) {
			return false;
		}
		facing = (byte) side;
		sideCache[facing] = 1;
		markDirty();
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	/* IReconfigurableSides */
	@Override
	public int getNumConfig(int side) {

		return 2;
	}

	/* ISidedTexture */
	@Override
	public IIcon getTexture(int side, int pass) {

		if (pass == 0) {
			return side != facing ? IconRegistry.getIcon("DeviceSide") : redstoneControlOrDisable() ? RenderHelper.getFluidTexture(renderFluid) : IconRegistry
					.getIcon("DeviceFace", getType());
		} else if (side < 6) {
			return side != facing ? IconRegistry.getIcon(TEProps.textureSelection, sideConfig.sideTex[sideCache[side]])
					: redstoneControlOrDisable() ? IconRegistry.getIcon("DeviceActive", getType()) : IconRegistry.getIcon("DeviceFace", getType());
		}
		return IconRegistry.getIcon("DeviceSide");
	}

	/* ISidedInventory */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return isSideAccessible(side) ? SLOTS : TEProps.EMPTY_INVENTORY;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {

		return isSideAccessible(side);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {

		return false;
	}

}
