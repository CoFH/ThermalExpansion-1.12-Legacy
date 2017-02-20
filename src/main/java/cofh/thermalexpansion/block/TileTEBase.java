package cofh.thermalexpansion.block;

import cofh.api.tileentity.IPortableData;
import cofh.core.block.TileCore;
import cofh.core.fluid.FluidTankCore;
import cofh.core.network.ITileInfoPacketHandler;
import cofh.core.network.ITilePacketHandler;
import cofh.core.network.PacketCoFHBase;
import cofh.core.network.PacketHandler;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;

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
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();
		payload.addString(tileName);
		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		if (ServerHelper.isClientWorld(worldObj)) {
			tileName = payload.getString();
			worldObj.checkLight(pos);
		} else {
			payload.getString();
		}
	}

	/* ITileInfoPacketHandler */
	@Override
	public void handleTileInfoPacket(PacketCoFHBase payload, boolean isServer, EntityPlayer thePlayer) {

		switch (TilePacketID.values()[payload.getByte()]) {
			case GUI:
				handleGuiPacket(payload);
				return;
			case FLUID:
				handleFluidPacket(payload);
				return;
			case MODE:
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
			markDirty();
			sendUpdatePacket(Side.CLIENT);
		}
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		if (!canPlayerAccess(player)) {
			return;
		}
		if (writePortableTagInternal(player, tag)) {

		}

	}
	
	public void provideInfo(IProbeInfoWrapper wrapper, EnumFacing facing, EntityPlayer player) {
        	wrapper.text("Progress:");
        	wrapper.progress(getScaledProgress(100), 100);
	}


	/* ENERGY CONFIG */
	public static class EnergyConfig {

		public int minPower = 8;
		public int maxPower = 80;
		public int maxEnergy = 20000;
		public int minPowerLevel = 1 * maxEnergy / 10;
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

		/* Slot Groups accessibble per Config */
		public int[][] slotGroups;

		/* Whether or not the SIDE allows insertion */
		public boolean[] allowInsertionSide;

		/* Whether or not the SIDE allows extraction */
		public boolean[] allowExtractionSide;

		/* Whether or not the SLOT allows input */
		public boolean[] allowInsertionSlot;

		/* Whether or not the SLOT allows extraction */
		public boolean[] allowExtractionSlot;

		/* Config Textures to use on Sides */
		public int[] sideTex;

		/* Default Side configuration for freshly placed block */
		public byte[] defaultSides;
	}

}
