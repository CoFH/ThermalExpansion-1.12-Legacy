package thermalexpansion.block.lamp;

import cofh.block.ITileInfo;
import cofh.network.ITilePacketHandler;
import cofh.util.ServerHelper;
import cofh.util.StringHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import thermalexpansion.block.TileTEBase;

public class TileLamp extends TileTEBase implements ITilePacketHandler, ITileInfo {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileLamp.class, "cofh.thermalexpansion.Lamp");
	}

	// 0 = OFF DEFAULT, 1 = ON DEFAULT, 2 = OFF SCALED, 3 = ON SCALED, 4 = OFF, 5 = ON
	public boolean modified;
	byte mode;
	int lightValue;
	int color = 0xFFFFFF;
	int renderColor = 0xAAAAAAFF;

	public boolean isPowered;
	public byte inputPower;

	@Override
	public boolean canUpdate() {

		return false;
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.lamp.name";
	}

	@Override
	public int getType() {

		return 0;
	}

	@Override
	public int getLightValue() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return lightValue;
		}
		switch (mode) {
		case 0:
			return isPowered ? 15 : 0;
		case 1:
			return isPowered ? 0 : 15;
		case 2:
			return worldObj.getBlockPowerInput(xCoord, yCoord, zCoord);
		case 3:
			return 15 - worldObj.getBlockPowerInput(xCoord, yCoord, zCoord);
		case 5:
			return 15;
		}
		return 0;
	}

	@Override
	public void onNeighborBlockChange() {

		boolean wasPowered = isPowered;
		byte oldPower = inputPower;
		isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		inputPower = (byte) worldObj.getBlockPowerInput(xCoord, yCoord, zCoord);

		if (wasPowered != isPowered || oldPower != inputPower) {
			sendUpdatePacket(Side.CLIENT);
		}
	}

	@Override
	public boolean onWrench(EntityPlayer player, int bSide) {

		mode = (byte) (++mode % 6);
		sendUpdatePacket(Side.CLIENT);

		player.addChatMessage(StringHelper.localize("message.thermalexpansion.lamp" + mode));
		return true;
	}

	public int getColorMultiplier() {

		return renderColor;
	}

	public boolean resetColor() {

		if (modified == false) {
			return false;
		}
		modified = false;
		color = 0xFFFFFF;
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	public boolean setColor(int color) {

		this.modified = true;
		this.color = color;
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	/* NETWORK METHODS */
	@Override
	public Payload getDescriptionPayload() {

		Payload payload = super.getDescriptionPayload();

		payload.addBool(modified);
		payload.addInt(color);
		payload.addByte(mode);
		payload.addByte(getLightValue());
		payload.addBool(isPowered);
		payload.addByte(inputPower);
		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(Payload payload) {

		modified = payload.getBool();
		color = payload.getInt();

		if (ServerHelper.isClientWorld(worldObj)) {
			mode = payload.getByte();
			lightValue = payload.getByte();

			int colorMod = 10 + getLightValue() / 3;
			int red = (color >> 16 & 0xFF) * colorMod / 15;
			int green = (color >> 8 & 0xFF) * colorMod / 15;
			int blue = (color & 0xFF) * colorMod / 15;

			renderColor = (red << 24) + (green << 16) + (blue << 8) + 0xFF;

			isPowered = payload.getBool();
			inputPower = payload.getByte();

			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		worldObj.updateAllLightTypes(xCoord, yCoord, zCoord);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		modified = nbt.getBoolean("Modified");
		mode = nbt.getByte("Mode");
		color = nbt.getInteger("Color");

		isPowered = nbt.getBoolean("Powered");
		inputPower = nbt.getByte("Signal");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setBoolean("Modified", modified);
		nbt.setByte("Mode", mode);
		nbt.setInteger("Color", color);

		nbt.setBoolean("Powered", isPowered);
		nbt.setByte("Signal", inputPower);
	}

	/* ITileInfo */
	@Override
	public void getTileInfo(List<String> info, ForgeDirection side, EntityPlayer player, boolean debug) {

		if (debug) {
			return;
		}
		info.add(StringHelper.localize("message.thermalexpansion.lamp" + mode));
	}

}
