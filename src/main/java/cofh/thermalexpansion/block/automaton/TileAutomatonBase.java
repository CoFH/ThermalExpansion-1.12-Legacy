package cofh.thermalexpansion.block.automaton;

import cofh.api.tileentity.IInventoryConnection;
import cofh.core.entity.CoFHFakePlayer;
import cofh.core.init.CoreProps;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.InventoryHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TilePowered;
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

import java.util.LinkedList;

public abstract class TileAutomatonBase extends TilePowered implements IInventoryConnection, ITickable {

	protected static final SideConfig[] defaultSideConfig = new SideConfig[BlockAutomaton.Type.values().length];
	private static boolean enableSecurity = true;

	public static void config() {

		String comment = "Enable this to allow for Automata to be securable.";
		enableSecurity = ThermalExpansion.CONFIG.get("Security", "Automaton.Securable", true, comment);
	}

	CoFHFakePlayer fakePlayer;
	LinkedList<ItemStack> stuffedItems = new LinkedList<ItemStack>();

	int radius = 0;
	int depth = 0;

	/* AUGMENTS */

	@Override
	public void onLoad() {

		if (ServerHelper.isServerWorld(worldObj)) {
			fakePlayer = new CoFHFakePlayer((WorldServer) worldObj);
		}
	}

	public TileAutomatonBase() {

		sideConfig = defaultSideConfig[this.getType()];
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
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	/* ISidedTexture */
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
