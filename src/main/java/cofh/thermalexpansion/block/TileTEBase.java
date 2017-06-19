package cofh.thermalexpansion.block;

import cofh.api.core.IPortableData;
import cofh.core.block.TileCore;
import cofh.core.fluid.FluidTankCore;
import cofh.core.network.ITileInfoPacketHandler;
import cofh.core.network.ITilePacketHandler;
import cofh.core.network.PacketCoFHBase;
import cofh.core.network.PacketHandler;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.GuiHandler;
import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileTEBase extends TileCore implements ITileInfoPacketHandler, ITilePacketHandler, IPortableData {

	protected String tileName = "";

	protected boolean setName(String name) {

		if (name.isEmpty()) {
			return false;
		}
		tileName = name;
		return true;
	}

	protected boolean readPortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		return true;
	}

	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		return true;
	}

	/* GUI METHODS */
	@Override
	public int getInvSlotCount() {

		return 0;
	}

	public int getScaledProgress(int scale) {

		return 0;
	}

	public int getScaledSpeed(int scale) {

		return 0;
	}

	public boolean hasGui() {

		return false;
	}

	@Override
	public boolean openGui(EntityPlayer player) {

		if (hasGui()) {
			player.openGui(ThermalExpansion.instance, GuiHandler.TILE_ID, worldObj, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		return false;
	}

	@Override
	public void sendGuiNetworkData(Container container, IContainerListener listener) {

		if (listener instanceof EntityPlayer) {
			PacketCoFHBase guiPacket = getGuiPacket();
			if (guiPacket != null) {
				PacketHandler.sendTo(guiPacket, (EntityPlayer) listener);
			}
		}
	}

	public FluidTankCore getTank() {

		return null;
	}

	public FluidStack getTankFluid() {

		return null;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		if (nbt.hasKey("Name")) {
			tileName = nbt.getString("Name");
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);
		nbt.setString("Version", ThermalExpansion.VERSION);

		if (!tileName.isEmpty()) {
			nbt.setString("Name", tileName);
		}
		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getTilePacket() {

		PacketCoFHBase payload = super.getTilePacket();

		payload.addString(tileName);

		return payload;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketCoFHBase payload) {

		tileName = payload.getString();
		worldObj.checkLight(pos);
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(PacketCoFHBase payload, boolean isServer, EntityPlayer thePlayer) {

		switch (TilePacketID.values()[payload.getByte()]) {
			case S_GUI:
				handleGuiPacket(payload);
				return;
			case S_FLUID:
				handleFluidPacket(payload);
				return;
			case C_ACCESS:
				handleAccessPacket(payload);
				return;
			case C_CONFIG:
				handleConfigPacket(payload);
				return;
			case C_MODE:
				handleModePacket(payload);
				return;
			default:
		}
	}

	/* IPortableData */
	@Override
	public String getDataType() {

		return getTileName();
	}

	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		if (!canPlayerAccess(player)) {
			return;
		}
		if (readPortableTagInternal(player, tag)) {
			markChunkDirty();
			sendTilePacket(Side.CLIENT);
		}
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		if (!canPlayerAccess(player)) {
			return;
		}
		writePortableTagInternal(player, tag);
	}

	/* PLUGIN METHODS */
	public void provideInfo(IProbeInfo info, EnumFacing facing, EntityPlayer player) {

		info.text("Progress: ");
		info.progress(getScaledProgress(100), 100);
	}

	/* ENERGY CONFIG */
	public static class EnergyConfig {

		public int minPower = 8;
		public int maxPower = 80;
		public int maxEnergy = 20000;
		public int minPowerLevel = maxEnergy / 10;
		public int maxPowerLevel = 9 * maxEnergy / 10;
		public int energyRamp = maxPowerLevel / maxPower;

		public EnergyConfig() {

		}

		public EnergyConfig(EnergyConfig config) {

			this.minPower = config.minPower;
			this.maxPower = config.maxPower;
			this.maxEnergy = config.maxEnergy;
			this.minPowerLevel = config.minPowerLevel;
			this.maxPowerLevel = config.maxPowerLevel;
			this.energyRamp = config.energyRamp;
		}

		public EnergyConfig copy() {

			return new EnergyConfig(this);
		}

		public boolean setDefaultParams(int basePower) {

			maxPower = basePower;
			minPower = basePower / 10;
			maxEnergy = basePower * 1000;
			maxPowerLevel = 9 * maxEnergy / 10;
			minPowerLevel = maxPowerLevel / 10;
			energyRamp = maxPowerLevel / basePower;

			return true;
		}

	}

	/* SIDE CONFIG */
	public static class SideConfig {

		/* Number of Side Configs */
		public int numConfig;

		/* Side Types - Determines Texture & Behavior */
		public int[] sideTypes;

		/* Slot Groups accessibble per Config */
		public int[][] slotGroups;

		/* Default Side configuration for freshly placed block */
		public byte[] defaultSides;
	}

	public boolean allowInsertion(int type) {

		return SIDE_INSERTION[type];
	}

	public boolean allowExtraction(int type) {

		return SIDE_EXTRACTION[type];
	}

	public boolean isPrimaryInput(int type) {

		return SIDE_INPUT_PRIMARY[type];
	}

	public boolean isSecondaryInput(int type) {

		return SIDE_INPUT_SECONDARY[type];
	}

	public boolean isPrimaryOutput(int type) {

		return SIDE_OUTPUT_PRIMARY[type];
	}

	public boolean isSecondaryOutput(int type) {

		return SIDE_OUTPUT_SECONDARY[type];
	}

	/* SIDE CONFIG HELPERS */
	public static boolean[] SIDE_INSERTION = { false, true, false, false, false, true, true, true, true };
	public static boolean[] SIDE_EXTRACTION = { false, true, true, true, true, true, true, true, true };

	public static boolean[] SIDE_INPUT_PRIMARY = { false, true, false, false, false, true, false, false, true };
	public static boolean[] SIDE_INPUT_SECONDARY = { false, true, false, false, false, false, true, false, true };
	public static boolean[] SIDE_OUTPUT_PRIMARY = { false, false, true, false, true, false, false, false, true };
	public static boolean[] SIDE_OUTPUT_SECONDARY = { false, false, false, true, true, false, false, false, true };

	/* SLOT CONFIG */
	public static class SlotConfig {

		/* Whether or not the SLOT allows input */
		public boolean[] allowInsertionSlot;

		/* Whether or not the SLOT allows extraction */
		public boolean[] allowExtractionSlot;
	}

}
