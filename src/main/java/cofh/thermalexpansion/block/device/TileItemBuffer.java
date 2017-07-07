package cofh.thermalexpansion.block.device;

import cofh.core.init.CoreProps;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.device.BlockDevice.Type;
import cofh.thermalexpansion.gui.client.device.GuiItemBuffer;
import cofh.thermalexpansion.gui.container.device.ContainerItemBuffer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;

public class TileItemBuffer extends TileDeviceBase implements ITickable {

	private static final int TYPE = Type.ITEM_BUFFER.getMetadata();

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0, 1, 2, 3, 4, 5, 6, 7, 8 }, { 0, 1, 2, 3, 4, 5, 6, 7, 8 }, { 0, 1, 2, 3, 4, 5, 6, 7, 8 }, { 0, 1, 2, 3, 4, 5, 6, 7, 8 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { 0, 1, 4, 7, 8 };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 2, 1, 1, 1, 1, 1 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, true, true, true, true, true, true, true, true };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { true, true, true, true, true, true, true, true, true };

		GameRegistry.registerTileEntity(TileItemBuffer.class, "thermalexpansion:device_item_buffer");

		config();
	}

	public static void config() {

		String category = "Device.ItemBuffer";
		BlockDevice.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}

	private int inputTracker;
	private int outputTracker;

	public int amountInput = 4;
	public int amountOutput = 4;

	public TileItemBuffer() {

		super();
		inventory = new ItemStack[9];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);

		hasAutoInput = true;
		hasAutoOutput = true;

		enableAutoInput = true;
		enableAutoOutput = true;
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void update() {

		if (ServerHelper.isClientWorld(world)) {
			return;
		}
		if (world.getTotalWorldTime() % CoreProps.TIME_CONSTANT_HALF != 0) {
			return;
		}
		boolean curActive = isActive;

		if (isActive) {
			transferOutput();
			transferInput();

			if (!redstoneControlOrDisable()) {
				isActive = false;
			}
		} else if (redstoneControlOrDisable()) {
			isActive = true;
		}
		updateIfChanged(curActive);
	}

	protected void transferInput() {

		if (!enableAutoInput || amountInput <= 0) {
			return;
		}
		int side;
		for (int i = inputTracker + 1; i <= inputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryInput(sideConfig.sideTypes[sideCache[side]])) {
				for (int j = 0; j < inventory.length; j++) {
					if (extractItem(j, amountInput, EnumFacing.VALUES[side])) {
						inputTracker = side;
						return;
					}
				}
			}
		}
	}

	protected void transferOutput() {

		if (!enableAutoOutput || amountOutput <= 0) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				for (int j = inventory.length - 1; j >= 0; j--) {
					if (transferItem(j, amountOutput, EnumFacing.VALUES[side])) {
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

		return new GuiItemBuffer(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerItemBuffer(inventory, this);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger("TrackIn");
		outputTracker = nbt.getInteger("TrackOut");

		amountInput = MathHelper.clamp(nbt.getInteger("AmountIn"), 0, 64);
		amountOutput = MathHelper.clamp(nbt.getInteger("AmountOut"), 0, 64);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut", outputTracker);

		nbt.setInteger("AmountIn", amountInput);
		nbt.setInteger("AmountOut", amountOutput);
		return nbt;
	}

	/* NETWORK METHODS */

	/* CLIENT -> SERVER */
	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addInt(MathHelper.clamp(amountInput, 0, 64));
		payload.addInt(MathHelper.clamp(amountOutput, 0, 64));

		return payload;
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		amountInput = payload.getInt();
		amountOutput = payload.getInt();
	}

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addInt(amountInput);
		payload.addInt(amountOutput);

		return payload;
	}

	@Override
	public PacketCoFHBase getTilePacket() {

		PacketCoFHBase payload = super.getTilePacket();

		payload.addInt(amountInput);
		payload.addInt(amountOutput);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		amountInput = payload.getInt();
		amountOutput = payload.getInt();
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketCoFHBase payload) {

		super.handleTilePacket(payload);

		amountInput = payload.getInt();
		amountOutput = payload.getInt();
	}

}
