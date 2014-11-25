package thermalexpansion.block.light;

import cofh.api.tileentity.ITileInfo;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.ForgeDirection;

import thermalexpansion.block.TileTEBase;

public class TileLight extends TileTEBase implements ITileInfo {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileLight.class, "thermalexpansion.Light");
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

		return "tile.thermalexpansion.light.name";
	}

	@Override
	public int getType() {

		return 0;
	}

	@Override
	public int getLightValue() {

		return getInternalLight();
	}

	public int getInternalLight() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return lightValue;
		}
		switch (mode) {
		case 0:
			return isPowered ? 15 : 0;
		case 1:
			return isPowered ? 0 : 15;
		case 2:
			return inputPower;
		case 3:
			return 15 - inputPower;
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
			markDirty();
			sendUpdatePacket(Side.CLIENT);
		}
	}

	@Override
	public boolean onWrench(EntityPlayer player, int bSide) {

		mode = (byte) (++mode % 6);
		sendUpdatePacket(Side.CLIENT);
		player.addChatMessage(new ChatComponentText(StringHelper.localize("chat.thermalexpansion.light." + mode)));
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

		if (color == this.color) {
			return false;
		}
		this.modified = true;
		this.color = color;
		setRenderColor();
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	public boolean setRenderColor() {

		if (ServerHelper.isServerWorld(worldObj)) {
			return false;
		}
		int colorMod = 10 + getInternalLight() / 3;
		int red = (color >> 16 & 0xFF) * colorMod / 15;
		int green = (color >> 8 & 0xFF) * colorMod / 15;
		int blue = (color & 0xFF) * colorMod / 15;

		renderColor = (red << 24) + (green << 16) + (blue << 8) + 0xFF;
		return true;
	}

	/* GUI METHODS */
	@Override
	public boolean hasGui() {

		return false;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addBool(modified);
		payload.addInt(color);
		payload.addByte(mode);
		payload.addByte(getInternalLight());
		payload.addBool(isPowered);
		payload.addByte(inputPower);

		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		modified = payload.getBool();
		color = payload.getInt();

		if (!isServer) {
			mode = payload.getByte();
			lightValue = payload.getByte();
			isPowered = payload.getBool();
			inputPower = payload.getByte();
			setRenderColor();
		}
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

	/* IPortableData */
	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		mode = tag.getByte("Mode");

		if (tag.hasKey("Color")) {
			setColor(tag.getInteger("Color"));
		}
	}

	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		tag.setByte("Mode", mode);

		if (modified) {
			tag.setInteger("Color", color);
		}
	}

	/* ITileInfo */
	@Override
	public void getTileInfo(List<IChatComponent> info, ForgeDirection side, EntityPlayer player, boolean debug) {

		if (debug) {
			return;
		}
		info.add(new ChatComponentText(StringHelper.localize("chat.thermalexpansion.light." + mode)));
	}

}
