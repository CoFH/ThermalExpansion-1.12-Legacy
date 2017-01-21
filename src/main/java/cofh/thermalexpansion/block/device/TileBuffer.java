package cofh.thermalexpansion.block.device;

import cofh.core.CoFHProps;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.gui.client.device.GuiBuffer;
import cofh.thermalexpansion.gui.container.device.ContainerBuffer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class TileBuffer extends TileDeviceBase implements ITickable {

	private static final int TYPE = BlockDevice.Type.BUFFER.getMetadata();

	public static void initialize() {

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numConfig = 4;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, { 0, 1, 2, 3, 4, 5, 6, 7, 8 }, { 0, 1, 2, 3, 4, 5, 6, 7, 8 }, { 0, 1, 2, 3, 4, 5, 6, 7, 8 } };
		defaultSideConfig[TYPE].allowInsertionSide = new boolean[] { false, true, false, true };
		defaultSideConfig[TYPE].allowExtractionSide = new boolean[] { false, false, true, true };
		defaultSideConfig[TYPE].allowInsertionSlot = new boolean[] { true, true, true, true, true, true, true, true, true };
		defaultSideConfig[TYPE].allowExtractionSlot = new boolean[] { true, true, true, true, true, true, true, true, true };
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 1, 4, 7 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 1, 1, 1, 1, 1, 1 };

		GameRegistry.registerTileEntity(TileBuffer.class, "thermalexpansion:buffer");
	}

	int inputTracker;
	int outputTracker;

	public int quantityInput = 1;
	public int quantityOutput = 1;

	public boolean enableInput = true;
	public boolean enableOutput = true;

	public TileBuffer() {

		super();
		inventory = new ItemStack[9];
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
		if (worldObj.getTotalWorldTime() % CoFHProps.TIME_CONSTANT_HALF == 0 && redstoneControlOrDisable()) {
			transferOutput();
			transferInput();
		}
	}

	protected void transferInput() {

		if (!enableInput || quantityInput <= 0) {
			return;
		}
		int side;
		for (int i = inputTracker + 1; i <= inputTracker + 6; i++) {
			side = i % 6;
			if (sideCache[side] == 1) {
				for (int j = 0; j < inventory.length; j++) {
					if (extractItem(j, quantityInput, EnumFacing.VALUES[side])) {
						inputTracker = side;
						return;
					}
				}
			}
		}
	}

	protected void transferOutput() {

		if (!enableOutput || quantityOutput <= 0) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (sideCache[side] == 2) {
				for (int j = inventory.length - 1; j >= 0; j--) {
					if (transferItem(j, quantityOutput, EnumFacing.VALUES[side])) {
						outputTracker = side;
						return;
					}
				}
			}
		}
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiBuffer(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerBuffer(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger("TrackIn");
		outputTracker = nbt.getInteger("TrackOut");

		quantityInput = MathHelper.clamp(nbt.getInteger("Input"), 0, 64);
		quantityOutput = MathHelper.clamp(nbt.getInteger("Output"), 0, 64);
		enableInput = nbt.getBoolean("EnableIn");
		enableOutput = nbt.getBoolean("EnableOut");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut", outputTracker);
		nbt.setInteger("Input", quantityInput);
		nbt.setInteger("Output", quantityOutput);
		nbt.setBoolean("EnableIn", enableInput);
		nbt.setBoolean("EnableOut", enableOutput);
		return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addInt(quantityInput);
		payload.addInt(quantityOutput);
		payload.addBool(enableInput);
		payload.addBool(enableOutput);

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addInt(quantityInput);
		payload.addInt(quantityOutput);
		payload.addBool(enableInput);
		payload.addBool(enableOutput);

		return payload;
	}

	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addInt(MathHelper.clamp(quantityInput, 0, 64));
		payload.addInt(MathHelper.clamp(quantityOutput, 0, 64));
		payload.addBool(enableInput);
		payload.addBool(enableOutput);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		quantityInput = payload.getInt();
		quantityOutput = payload.getInt();
		enableInput = payload.getBool();
		enableOutput = payload.getBool();
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		quantityInput = payload.getInt();
		quantityOutput = payload.getInt();
		enableInput = payload.getBool();
		enableOutput = payload.getBool();
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {
			quantityInput = payload.getInt();
			quantityOutput = payload.getInt();
			enableInput = payload.getBool();
			enableOutput = payload.getBool();
		} else {
			payload.getInt();
			payload.getInt();
			payload.getBool();
			payload.getBool();
		}
	}

	/* IReconfigurableFacing */
	@Override
	public boolean setFacing(int side) {

		if (side < 0 || side > 5) {
			return false;
		}
		facing = (byte) side;
		sideCache[facing] = 0;
		markDirty();
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

}
