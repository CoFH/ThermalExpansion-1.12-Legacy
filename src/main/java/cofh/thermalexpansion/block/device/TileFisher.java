package cofh.thermalexpansion.block.device;

import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.device.GuiFisher;
import cofh.thermalexpansion.gui.container.device.ContainerFisher;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileFisher extends TileDeviceBase implements ITickable {

	private static final int TYPE = BlockDevice.Type.FISHER.getMetadata();

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { 0, 1, 4, 7, 8 };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, false, false, false, false, false, false, false, false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { true, true, true, true, true, true, true, true, true, true };

		LIGHT_VALUES[TYPE] = 3;

		GameRegistry.registerTileEntity(TileFisher.class, "thermalexpansion:device_fisher");

		config();
	}

	public static void config() {

		String category = "Device.Fisher";
		BlockDevice.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}

	private static final int TIME_CONSTANT = 4800;
	private static final int BOOST_TIME = 16;

	private boolean cached;

	private int inputTracker;
	private int outputTracker;

	private int boostMult;
	private int boostTime;

	private int offset;

	public TileFisher() {

		super();
		inventory = new ItemStack[10];
		createAllSlots(inventory.length);

		offset = MathHelper.RANDOM.nextInt(TIME_CONSTANT);

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
	public void blockPlaced() {

		//		if (validTree && redstoneControlOrDisable()) {
		//			isActive = true;
		//			sendTilePacket(Side.CLIENT);
		//		}
	}

	@Override
	public void onNeighborBlockChange() {

		super.onNeighborBlockChange();
		updateAdjacentHandlers();
	}

	@Override
	public void update() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (!timeCheckOffset()) {
			return;
		}

	}

	protected void transferInput() {

		if (!enableAutoInput) {
			return;
		}
		int side;
		for (int i = inputTracker + 1; i <= inputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryInput(sideConfig.sideTypes[sideCache[side]])) {
				if (extractItem(0, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					inputTracker = side;
					break;
				}
			}
		}
	}

	protected void updateAdjacentHandlers() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
	}

	protected boolean timeCheckOffset() {

		return (worldObj.getTotalWorldTime() + offset) % TIME_CONSTANT == 0;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiFisher(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerFisher(inventory, this);
	}

	@Override
	public int getScaledSpeed(int scale) {

		if (!isActive) {
			return 0;
		}
		return MathHelper.round(scale * boostTime / BOOST_TIME);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger("TrackIn");
		outputTracker = nbt.getInteger("TrackOut");

		boostMult = nbt.getInteger("BoostMult");
		boostTime = nbt.getInteger("BoostTime");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut", outputTracker);

		nbt.setInteger("BoostMult", boostMult);
		nbt.setInteger("BoostTime", boostTime);

		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addInt(boostTime);
		payload.addInt(boostMult);

		return payload;
	}

	@Override
	public PacketCoFHBase getTilePacket() {

		PacketCoFHBase payload = super.getTilePacket();

		// payload.addBool(validTree);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		boostTime = payload.getInt();
		boostMult = payload.getInt();
	}

	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		// validTree = payload.getBool();
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return true;
		// return TapperManager.getFertilizerMultiplier(stack) > 0;
	}

}
