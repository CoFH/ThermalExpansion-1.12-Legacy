package cofh.thermalexpansion.block.device;

import cofh.api.energy.IEnergyProvider;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.gui.client.device.GuiEnergyBuffer;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileEnergyBuffer extends TileDeviceBase implements ITickable, IEnergyProvider {

	private static final int TYPE = BlockDevice.Type.ENERGY_BUFFER.getMetadata();

	public static void initialize() {

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numConfig = 4;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, {}, {}, {} };
		defaultSideConfig[TYPE].allowInsertionSide = new boolean[] { false, true, false, true };
		defaultSideConfig[TYPE].allowExtractionSide = new boolean[] { false, false, true, true };
		defaultSideConfig[TYPE].allowInsertionSlot = new boolean[] {};
		defaultSideConfig[TYPE].allowExtractionSlot = new boolean[] {};
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 1, 4, 7 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 1, 1, 1, 1, 1, 1 };

		GameRegistry.registerTileEntity(TileItemBuffer.class, "thermalexpansion:device_energy_buffer");
	}

	private int inputTracker;
	private int outputTracker;

	public int amountInput = 500;
	public int amountOutput = 500;

	public TileEnergyBuffer() {

		super();
		energyStorage.setCapacity(400000).setMaxTransfer(20000);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void setDefaultSides() {

		sideCache = getDefaultSides();
		sideCache[facing] = 0;
		sideCache[facing ^ 1] = 2;
	}

	@Override
	public void update() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (worldObj.getTotalWorldTime() % CoreProps.TIME_CONSTANT_HALF == 0 && redstoneControlOrDisable()) {
			transferOutput();
			transferInput();
		}
	}

	protected void transferInput() {

		if (!enableAutoInput || amountInput <= 0) {
			return;
		}
		int side;
		int input = Math.min(getEnergySpace(), energyStorage.getMaxReceive());
		for (int i = inputTracker + 1; i <= inputTracker + 6; i++) {
			side = i % 6;

			if (sideCache[side] == 1) {
				int received = EnergyHelper.extractEnergyFromAdjacentEnergyProvider(this, EnumFacing.VALUES[side], input, false);

				if (received > 0) {
					energyStorage.modifyEnergyStored(received);
					inputTracker = side;
					return;
				}
			}
		}
	}

	protected void transferOutput() {

		if (!enableAutoOutput || amountOutput <= 0) {
			return;
		}
		if (energyStorage.getEnergyStored() <= 0) {
			return;
		}
		int side;
		int output = Math.min(energyStorage.getEnergyStored(), energyStorage.getMaxExtract());
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;

			if (sideCache[side] == 2) {
				int sent = EnergyHelper.insertEnergyIntoAdjacentEnergyReceiver(this, EnumFacing.VALUES[side], output, false);

				if (sent > 0) {
					energyStorage.modifyEnergyStored(-sent);
					outputTracker = side;
					return;
				}
			}
		}
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiEnergyBuffer(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger("TrackIn");
		outputTracker = nbt.getInteger("TrackOut");

		amountInput = MathHelper.clamp(nbt.getInteger("Input"), 0, 64);
		amountOutput = MathHelper.clamp(nbt.getInteger("Output"), 0, 64);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut", outputTracker);
		nbt.setInteger("Input", amountInput);
		nbt.setInteger("Output", amountOutput);
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addInt(amountInput);
		payload.addInt(amountOutput);

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addInt(amountInput);
		payload.addInt(amountOutput);

		return payload;
	}

	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addInt(MathHelper.clamp(amountInput, 0, energyStorage.getMaxReceive()));
		payload.addInt(MathHelper.clamp(amountOutput, 0, energyStorage.getMaxExtract()));

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		amountInput = payload.getInt();
		amountOutput = payload.getInt();
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		amountInput = payload.getInt();
		amountOutput = payload.getInt();
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {
			amountInput = payload.getInt();
			amountOutput = payload.getInt();
		} else {
			payload.getInt();
			payload.getInt();
		}
	}

	/* IEnergyProvider */
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {

		if (from != null && sideCache[from.ordinal()] != 2) {
			return 0;
		}
		return energyStorage.extractEnergy(maxExtract, simulate);
	}

}
