package cofh.thermalexpansion.block.apparatus;

import cofh.api.core.IAccelerable;
import cofh.api.item.IAugmentItem.AugmentType;
import cofh.api.tileentity.IInventoryConnection;
import cofh.core.entity.FakePlayerCore;
import cofh.core.init.CoreProps;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.BlockHelper;
import cofh.core.util.helpers.InventoryHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.thermalexpansion.block.TilePowered;
import cofh.thermalexpansion.block.apparatus.BlockApparatus.Type;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.util.Utils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashSet;
import java.util.LinkedList;

public abstract class TileApparatusBase extends TilePowered implements IAccelerable, IInventoryConnection, ITickable {

	public static final SideConfig[] SIDE_CONFIGS = new SideConfig[Type.values().length];
	public static final SlotConfig[] SLOT_CONFIGS = new SlotConfig[Type.values().length];
	public static final EnergyConfig[] ENERGY_CONFIGS = new EnergyConfig[Type.values().length];
	public static final HashSet[] VALID_AUGMENTS = new HashSet[Type.values().length];
	public static final int[] LIGHT_VALUES = new int[Type.values().length];

	private static boolean enableSecurity = true;

	protected static final HashSet<String> VALID_AUGMENTS_BASE = new HashSet<>();

	static {
		VALID_AUGMENTS_BASE.add(TEProps.APPARATUS_DEPTH);
		VALID_AUGMENTS_BASE.add(TEProps.APPARATUS_RADIUS);
	}

	public static void config() {

		String comment = "Enable this to allow for Apparatus to be securable.";
		// enableSecurity = ThermalExpansion.CONFIG.get("Apparatus", "Securable", true, comment);
	}

	int processMax;
	int processRem;
	boolean hasModeAugment;

	FakePlayerCore fakePlayer;
	LinkedList<ItemStack> stuffedItems = new LinkedList<>();

	EnergyConfig energyConfig;

	int depth = 0;
	int radius = 0;

	/* AUGMENTS */

	@Override
	public void onLoad() {

		if (ServerHelper.isServerWorld(world)) {
			fakePlayer = new FakePlayerCore((WorldServer) world);
		}
	}

	public TileApparatusBase() {

		sideConfig = SIDE_CONFIGS[this.getType()];
		slotConfig = SLOT_CONFIGS[this.getType()];
		enableAutoOutput = true;
		setDefaultSides();
	}

	@Override
	public String getTileName() {

		return "tile.thermalexpansion.apparatus." + Type.byMetadata(getType()).getName() + ".name";
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity;
	}

	@Override
	public boolean sendRedstoneUpdates() {

		return true;
	}

	@Override
	public void setDefaultSides() {

		sideCache = getDefaultSides();
		sideCache[facing] = 0;
		sideCache[facing ^ 1] = 1;
	}

	@Override
	public void update() {

		if (world.getTotalWorldTime() % CoreProps.TIME_CONSTANT_HALF == 0 && redstoneControlOrDisable()) {
			if (!isStuffingEmpty()) {
				outputBuffer();
			}
			if (isStuffingEmpty()) {
				activate();
			}
		}
		chargeEnergy();
	}

	protected void activate() {

	}

	protected boolean isStuffingEmpty() {

		return stuffedItems.isEmpty();
	}

	protected boolean outputBuffer() {

		if (getTransferOut()) {
			for (int i = 0; i < 6; i++) {
				if (sideCache[i] == 1) {
					EnumFacing side = EnumFacing.VALUES[i];
					TileEntity curTile = BlockHelper.getAdjacentTileEntity(this, side);
					/* Add to Adjacent Inventory */
					if (Utils.isAccessibleOutput(curTile, side)) {
						LinkedList<ItemStack> newStuffed = new LinkedList<>();
						for (ItemStack curItem : stuffedItems) {
							if (curItem.isEmpty()) {
								curItem = ItemStack.EMPTY;
							} else {
								curItem = InventoryHelper.addToInventory(curTile, side, curItem);
							}
							if (!curItem.isEmpty()) {
								newStuffed.add(curItem);
							}
						}
						stuffedItems = newStuffed;
					}
				}
			}
		}
		return isStuffingEmpty();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		NBTTagList list = nbt.getTagList("StuffedInv", 10);
		stuffedItems.clear();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			stuffedItems.add(new ItemStack(compound));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		NBTTagList list = new NBTTagList();
		for (ItemStack item : stuffedItems) {
			if (!item.isEmpty()) {
				NBTTagCompound compound = new NBTTagCompound();
				item.writeToNBT(compound);
				list.appendTag(compound);
			}
		}
		nbt.setTag("StuffedInv", list);
		return nbt;
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		radius = 0;
		depth = 0;
	}

	@Override
	protected void postAugmentInstall() {

		// calculate energy cost
	}

	@Override
	protected boolean isValidAugment(AugmentType type, String id) {

		if (type == AugmentType.CREATIVE && !isCreative) {
			return false;
		}
		if (type == AugmentType.MODE && hasModeAugment) {
			return false;
		}
		return VALID_AUGMENTS_BASE.contains(id) || /* VALID_AUGMENTS[getType()].contains(id) ||*/ super.isValidAugment(type, id);
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (TEProps.APPARATUS_DEPTH.equals(id)) {
			depth++;
			return true;
		}
		if (TEProps.APPARATUS_RADIUS.equals(id)) {
			radius++;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* IAccelerable */
	@Override
	public int updateAccelerable() {

		return 0;
	}

	/* IInventoryConnection */
	@Override
	public ConnectionType canConnectInventory(EnumFacing from) {

		if (from != null && from.ordinal() != facing && sideCache[from.ordinal()] == 1) {
			return ConnectionType.FORCE;
		} else {
			return ConnectionType.DEFAULT;
		}
	}

	/* IReconfigurableFacing */
	@Override
	public boolean allowYAxisFacing() {

		return true;
	}

	@Override
	public boolean setFacing(int side, boolean alternate) {

		if (side < 0 || side > 5) {
			return false;
		}
		facing = (byte) side;
		sideCache[facing] = 0;
		sideCache[facing ^ 1] = 1;
		markChunkDirty();
		sendTilePacket(Side.CLIENT);
		return true;
	}

	/* ISidedTexture */
	@Override
	public int getNumPasses() {

		return 2;
	}

	@Override
	public TextureAtlasSprite getTexture(int side, int pass) {

		if (pass == 0) {
			return side != facing ? TETextures.APPARATUS_SIDE : redstoneControlOrDisable() ? TETextures.APPARATUS_ACTIVE[getType()] : TETextures.APPARATUS_FACE[getType()];
		} else if (side < 6) {
			return TETextures.CONFIG[sideConfig.sideTypes[sideCache[side]]];
		}
		return TETextures.APPARATUS_SIDE;
	}

}
