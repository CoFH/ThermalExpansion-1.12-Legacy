package cofh.thermalexpansion.block.device;

import cofh.api.energy.IEnergyReceiver;
import cofh.lib.util.helpers.BlockHelper;
import cofh.thermalexpansion.gui.client.device.GuiExtender;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileExtender extends TileDeviceBase {

	private static final int TYPE = BlockDevice.Type.EXTENDER.getMetadata();

	public static void initialize() {

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numConfig = 2;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, {} };
		defaultSideConfig[TYPE].allowInsertionSide = new boolean[] { false, true };
		defaultSideConfig[TYPE].allowExtractionSide = new boolean[] { false, true };
		defaultSideConfig[TYPE].allowInsertionSlot = new boolean[] {};
		defaultSideConfig[TYPE].allowExtractionSlot = new boolean[] {};
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 7 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 1, 1, 1, 1, 1, 1 };

		GameRegistry.registerTileEntity(TileExtender.class, "thermalexpansion:device_extender");
	}

	private TileEntity targetTile;
	private IEnergyReceiver targetReceiver;

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void onNeighborBlockChange() {

		super.onNeighborBlockChange();
		updateTarget();
	}

	public void onNeighborTileChange(BlockPos pos) {

		super.onNeighborTileChange(pos);
		updateTarget();
	}

	private void updateTarget() {

		targetTile = BlockHelper.getAdjacentTileEntity(this, EnumFacing.VALUES[facing]);

		if (targetTile instanceof TileExtender) {
			targetTile = null;
			return;
		}
		if (targetTile instanceof IEnergyReceiver) {
			targetReceiver = (IEnergyReceiver) targetTile;
		}
		markDirty();
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiExtender(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
	}

	/* IReconfigurableFacing */
	@Override
	public boolean rotateBlock() {

		boolean ret = super.rotateBlock();
		updateTarget();
		return ret;
	}

	/* IEnergyReceiver */
	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {

		return sideCache[from.ordinal()] == 1 && targetReceiver != null ? targetReceiver.receiveEnergy(EnumFacing.VALUES[facing ^ 1], maxReceive, simulate) : 0;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {

		return sideCache[from.ordinal()] == 1 && targetReceiver != null ? targetReceiver.getEnergyStored(EnumFacing.VALUES[facing ^ 1]) : 0;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {

		return sideCache[from.ordinal()] == 1 && targetReceiver != null ? targetReceiver.getMaxEnergyStored(EnumFacing.VALUES[facing ^ 1]) : 0;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {

		return sideCache[from.ordinal()] == 1 && targetReceiver != null && targetReceiver.canConnectEnergy(EnumFacing.VALUES[facing ^ 1]);
	}

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return sideCache[from.ordinal()] == 1 && targetTile != null && targetTile.hasCapability(capability, EnumFacing.VALUES[facing ^ 1]);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		return sideCache[from.ordinal()] == 1 && targetTile != null ? targetTile.getCapability(capability, EnumFacing.VALUES[facing ^ 1]) : super.getCapability(capability, from);
	}

}
