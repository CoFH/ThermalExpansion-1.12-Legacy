package cofh.thermalexpansion.block.automaton;

import cofh.api.item.IAugmentItem.AugmentType;
import cofh.api.tileentity.IAccelerable;
import cofh.api.tileentity.IInventoryConnection;
import cofh.core.entity.CoFHFakePlayer;
import cofh.core.init.CoreProps;
import cofh.lib.util.helpers.AugmentHelper;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.InventoryHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TilePowered;
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

import java.util.ArrayList;
import java.util.LinkedList;

public abstract class TileAutomatonBase extends TilePowered implements IAccelerable, IInventoryConnection, ITickable {

	public static final SideConfig[] SIDE_CONFIGS = new SideConfig[BlockAutomaton.Type.values().length];
	public static final SlotConfig[] SLOT_CONFIGS = new SlotConfig[BlockAutomaton.Type.values().length];
	public static final EnergyConfig[] ENERGY_CONFIGS = new EnergyConfig[BlockAutomaton.Type.values().length];
	public static final ArrayList<String>[] VALID_AUGMENTS = new ArrayList[BlockAutomaton.Type.values().length];
	public static final int[] LIGHT_VALUES = new int[BlockAutomaton.Type.values().length];

	private static boolean enableSecurity = true;

	protected static final ArrayList<String> VALID_AUGMENTS_BASE = new ArrayList<String>();

	static {
		VALID_AUGMENTS_BASE.add(TEProps.AUTOMATON_DEPTH);
		VALID_AUGMENTS_BASE.add(TEProps.AUTOMATON_RADIUS);
	}

	public static void config() {

		String comment = "Enable this to allow for Automata to be securable.";
		enableSecurity = ThermalExpansion.CONFIG.get("Security", "Automaton.Securable", true, comment);
	}

	int processMax;
	int processRem;
	boolean wasActive;
	boolean hasModeAugment;

	CoFHFakePlayer fakePlayer;
	LinkedList<ItemStack> stuffedItems = new LinkedList<ItemStack>();

	EnergyConfig energyConfig;

	int depth = 0;
	int radius = 0;

	/* AUGMENTS */

	@Override
	public void onLoad() {

		if (ServerHelper.isServerWorld(worldObj)) {
			fakePlayer = new CoFHFakePlayer((WorldServer) worldObj);
		}
	}

	public TileAutomatonBase() {

		sideConfig = SIDE_CONFIGS[this.getType()];
		enableAutoOutput = true;
		setDefaultSides();
	}

	@Override
	public String getTileName() {

		return "tile.thermalexpansion.automaton." + BlockAutomaton.Type.byMetadata(getType()).getName() + ".name";
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

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (worldObj.getTotalWorldTime() % CoreProps.TIME_CONSTANT_HALF == 0 && redstoneControlOrDisable()) {
			if (!isEmpty()) {
				outputBuffer();
			}
			if (isEmpty()) {
				activate();
			}
		}
		chargeEnergy();
	}

	protected void activate() {

	}

	protected boolean isEmpty() {

		return stuffedItems.isEmpty();
	}

	protected boolean outputBuffer() {

		if (enableAutoOutput) {
			for (int i = 0; i < 6; i++) {
				if (sideCache[i] == 1) {
					EnumFacing side = EnumFacing.VALUES[i];
					TileEntity curTile = BlockHelper.getAdjacentTileEntity(this, side);
					/* Add to Adjacent Inventory */
					if (Utils.isAccessibleOutput(curTile, side)) {
						LinkedList<ItemStack> newStuffed = new LinkedList<ItemStack>();
						for (ItemStack curItem : stuffedItems) {
							if (curItem == null || curItem.getItem() == null) {
								curItem = null;
							} else {
								curItem = InventoryHelper.addToInsertion(curTile, side, curItem);
							}
							if (curItem != null) {
								newStuffed.add(curItem);
							}
						}
						stuffedItems = newStuffed;
					}
				}
			}
		}
		return isEmpty();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		NBTTagList list = nbt.getTagList("StuffedInv", 10);
		stuffedItems.clear();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			stuffedItems.add(ItemStack.loadItemStackFromNBT(compound));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		NBTTagList list = new NBTTagList();
		for (int i = 0; i < stuffedItems.size(); i++) {
			if (stuffedItems.get(i) != null) {
				NBTTagCompound compound = new NBTTagCompound();
				stuffedItems.get(i).writeToNBT(compound);
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

		if (TEProps.AUTOMATON_DEPTH.equals(id)) {
			depth++;
			return true;
		}
		if (TEProps.AUTOMATON_RADIUS.equals(id)) {
			radius++;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* IAccelerable */
	@Override
	public void updateAccelerable() {

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
	public boolean setFacing(int side) {

		if (side < 0 || side > 5) {
			return false;
		}
		facing = (byte) side;
		sideCache[facing] = 0;
		sideCache[facing ^ 1] = 1;
		markDirty();
		sendTilePacket(Side.CLIENT);
		return true;
	}

	/* ISidedTexture */
	@Override
	public int getNumLayers() {

		return 2;
	}

	@Override
	public int getNumPasses(int layer) {

		return 1;
	}

	@Override
	public TextureAtlasSprite getTexture(int side, int layer, int pass) {

		if (layer == 0) {
			return side != facing ? TETextures.AUTOMATON_SIDE : redstoneControlOrDisable() ? TETextures.AUTOMATON_ACTIVE[getType()] : TETextures.AUTOMATON_FACE[getType()];
		} else if (side < 6) {
			return TETextures.CONFIG[sideConfig.sideTex[sideCache[side]]];
		}
		return TETextures.AUTOMATON_SIDE;
	}

}
