package cofh.thermalexpansion.block.light;

import cofh.api.tileentity.ITileInfo;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.TileTEBase;
import cofh.thermalexpansion.gui.client.GuiLight;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TileLight extends TileTEBase implements ITileInfo {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileLight.class, "thermalexpansion.Light");
	}

	// 0 = OFF DEFAULT, 1 = ON DEFAULT, 2 = OFF SCALED, 3 = ON SCALED, 4 = OFF, 5 = ON
	public byte mode;
	public boolean modified;
	public boolean isPowered;
	public byte inputPower;
	public boolean dim;
	public byte style;
	public byte alignment;

	int lightValue;
	public int color = 0xFFFFFF;
	int renderColor = 0xAAAAAAFF;


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

		return dim ? 0 : getInternalLight();
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
	public void blockPlaced() {

		updateLighting();
	}

	@Override
	public void onNeighborBlockChange() {

		boolean wasPowered = isPowered;
		byte oldPower = inputPower;
		isPowered = worldObj.isBlockPowered(getPos());
		inputPower = (byte) worldObj.getStrongPower(getPos());

		if (wasPowered != isPowered || oldPower != inputPower) {
			markDirty();
			if (!dim) {
				updateLighting();
			}
			sendUpdatePacket(Side.CLIENT);
		}
	}

	@Override
	public boolean onWrench(EntityPlayer player, int bSide) {

		// TODO: axis sensitive rotation
		switch (style) {
		case 0:
			mode = (byte) (++mode % 6);
			player.addChatMessage(new TextComponentString(StringHelper.localize("chat.thermalexpansion.light." + mode)));
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			if (bSide >> 1 == (alignment & 7) >> 1) {
				alignment ^= 8;
			}
			break;
		case 5:
			break;
		}
		sendUpdatePacket(Side.CLIENT);
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

	@Override
	protected boolean readPortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		mode = tag.getByte("Mode");
		dim = tag.getBoolean("Dim");

		if (tag.hasKey("Color")) {
			setColor(tag.getInteger("Color"));
		}
		updateLighting();
		return true;
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		tag.setByte("Mode", mode);
		tag.setBoolean("Dim", dim);

		if (modified) {
			tag.setInteger("Color", color);
		}
		return true;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiLight(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this, false, false);
	}

	@Override
	public boolean hasGui() {

		return true;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addBool(modified);
		if (modified) {
			payload.addInt(color);
		}
		payload.addByte(mode);
		payload.addBool(dim);
		payload.addByte(style);
		if (style != 0) {
			payload.addByte(alignment);
		}
		if (ServerHelper.isServerWorld(worldObj)) {
			payload.addByte(getInternalLight());
		}
		return payload;
	}

	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		resetColor();

		return payload;
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		resetColor();
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		modified = payload.getBool();

		if (modified) {
			setColor(payload.getInt());
		}
		mode = payload.getByte();
		dim = payload.getBool();
		style = payload.getByte();
		if (style != 0) {
			alignment = payload.getByte();
		}
		if (!isServer) {
			lightValue = payload.getByte();
			setRenderColor();
		}
		updateLighting();
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

		dim = nbt.getBoolean("Dim");

		style = nbt.getByte("Style");
		alignment = nbt.getByte("Align");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setBoolean("Modified", modified);
		nbt.setByte("Mode", mode);
		nbt.setInteger("Color", color);

		nbt.setBoolean("Powered", isPowered);
		nbt.setByte("Signal", inputPower);

		nbt.setBoolean("Dim", dim);

		nbt.setByte("Style", style);
		nbt.setByte("Align", alignment);
        return nbt;
	}

	/* ITileInfo */
	@Override
	public void getTileInfo(List<ITextComponent> info, EnumFacing side, EntityPlayer player, boolean debug) {

		if (debug) {
			info.add(new TextComponentString("Dim: " + dim));
			info.add(new TextComponentString("Alignment: " + alignment));
		}
		info.add(new TextComponentTranslation("chat.thermalexpansion.light." + mode));
	}

}
