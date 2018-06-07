package cofh.thermalexpansion.block.light;

import cofh.api.tileentity.ITileInfo;
import cofh.core.block.TileNameable;
import cofh.core.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

public class TileLightBase extends TileNameable implements ITileInfo {

	public static void config() {

	}

	// 0 = OFF DEFAULT, 1 = ON DEFAULT, 2 = OFF SCALED, 3 = ON SCALED, 4 = OFF, 5 = ON
	public byte mode;

	public int powerLevel;
	public boolean isPowered;

	int lightValue;
	public int color = -1;
	int renderColor = 0xAAAAAAFF;

	@Override
	protected Object getMod() {

		return ThermalExpansion.instance;
	}

	@Override
	protected String getModVersion() {

		return ThermalExpansion.VERSION;
	}

	@Override
	protected String getTileName() {

		return "tile.thermalexpansion.light.name";
	}

	@Override
	public int getLightValue() {

		return getInternalLight();
	}

	public int getInternalLight() {

		if (ServerHelper.isClientWorld(world)) {
			return lightValue;
		}
		switch (mode) {
			case 0:
				return isPowered ? 15 : 0;
			case 1:
				return isPowered ? 0 : 15;
			case 2:
				return powerLevel;
			case 3:
				return 15 - powerLevel;
			case 5:
				return 15;
		}
		return 0;
	}

	@Override
	public void blockPlaced() {

		onNeighborBlockChange();

		sendTilePacket(Side.CLIENT);
	}

	@Override
	public void onNeighborBlockChange() {

		boolean wasPowered = isPowered;
		int curLevel = powerLevel;
		powerLevel = world.isBlockIndirectlyGettingPowered(pos);
		isPowered = powerLevel > 0;

		if (wasPowered != isPowered || curLevel != powerLevel) {
			updateLighting();
			sendTilePacket(Side.CLIENT);
		}
	}

	public int getColorMultiplier() {

		return renderColor;
	}

	public boolean resetColor() {

		color = -1;
		sendTilePacket(Side.CLIENT);
		return true;
	}

	public boolean setColor(int color) {

		if (color == this.color) {
			return false;
		}
		this.color = color;
		markChunkDirty();
		setRenderColor();
		sendTilePacket(Side.CLIENT);
		return true;
	}

	public boolean setRenderColor() {

		if (ServerHelper.isServerWorld(world)) {
			return false;
		}
		int colorMod = 10 + getInternalLight() / 3;
		int red = (color >> 16 & 0xFF) * colorMod / 15;
		int green = (color >> 8 & 0xFF) * colorMod / 15;
		int blue = (color & 0xFF) * colorMod / 15;

		renderColor = (red << 24) + (green << 16) + (blue << 8) + 0xFF;
		return true;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		mode = nbt.getByte("Mode");
		color = nbt.getInteger("Color");

		NBTTagCompound rsTag = nbt.getCompoundTag("RS");
		isPowered = rsTag.getBoolean("Power");
		powerLevel = rsTag.getByte("Level");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Mode", mode);
		nbt.setInteger("Color", color);

		NBTTagCompound rsTag = new NBTTagCompound();
		rsTag.setBoolean("Power", isPowered);
		rsTag.setByte("Level", (byte) powerLevel);
		nbt.setTag("RS", rsTag);

		return nbt;
	}

	@Override
	protected boolean readPortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		mode = tag.getByte("Mode");
		setColor(tag.getInteger("Color"));

		updateLighting();
		return true;
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		tag.setByte("Mode", mode);
		tag.setInteger("Color", color);

		return true;
	}

	/* ITileInfo */
	@Override
	public void getTileInfo(List<ITextComponent> info, EnumFacing side, EntityPlayer player, boolean debug) {

	}

}
