package cofh.thermalexpansion.block.plate;

import codechicken.lib.util.BlockUtils;
import codechicken.lib.util.SoundUtils;
import cofh.api.tileentity.IRedstoneControl;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.gui.client.plate.GuiPlateTranslocate;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.network.PacketTEBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class TilePlateTranslocate extends TilePlateBase implements IRedstoneControl {

	public static void initialize() {

		GameRegistry.registerTileEntity(TilePlateTranslocate.class, "cofh.thermalexpansion.PlateTranslocate");
	}

	public static final byte MIN_DISTANCE = 0;
	public static final byte MAX_DISTANCE = 16;

	public TilePlateTranslocate() {

		super(BlockPlate.Types.TRANSLOCATE);
	}

	public byte distance = 16;

	protected boolean isPowered;

	protected ControlMode rsMode = ControlMode.DISABLED;

	@Override
	public void onEntityCollidedWithBlock(Entity entity) {

		if (!redstoneControlOrDisable()) {
			return;
		}

		int[] v = getVector(distance);
		double x = pos.getX() + v[0] + .5;
		double y = pos.getY() + v[1] + .125;
		double z = pos.getZ() + v[2] + .5;
		if (!(entity instanceof EntityLivingBase) && entity.getEntityBoundingBox() == null) {
			x = entity.posX + v[0];
			y = entity.posY + v[1];
			z = entity.posZ + v[2];
		}

		int x2 = pos.getX() + v[0];
		int y2 = pos.getY() + v[1];
		int z2 = pos.getZ() + v[2];

		IBlockState state = worldObj.getBlockState(new BlockPos(x2, y2, z2));
		if (!(state.isOpaqueCube() || state.getMaterial().isSolid())) {
			if (entity instanceof EntityLivingBase) {
				if (worldObj.isRemote) {
					return;
				}
				CoreUtils.teleportEntityTo((EntityLivingBase) entity, x, y, z, true);
			} else {
				entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
                SoundUtils.playSoundAt(entity, SoundCategory.BLOCKS, SoundEvents.ENTITY_ENDERMEN_TELEPORT, 0.5F, 1.0F);
			}
		}
	}

	@Override
	protected boolean readPortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		distance = tag.getByte("Dist");
		NBTTagCompound rsTag = tag.getCompoundTag("RS");

		rsMode = ControlMode.values()[rsTag.getByte("Mode")];
		return true;
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		tag.setByte("Dist", distance);
		NBTTagCompound rsTag = new NBTTagCompound();

		rsTag.setByte("Mode", (byte) rsMode.ordinal());
		tag.setTag("RS", rsTag);
		return true;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiPlateTranslocate(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this, false, false);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		distance = nbt.getByte("Dist");
		NBTTagCompound rsTag = nbt.getCompoundTag("RS");

		isPowered = rsTag.getBoolean("Power");
		rsMode = ControlMode.values()[rsTag.getByte("Mode")];
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setByte("Dist", distance);
		NBTTagCompound rsTag = new NBTTagCompound();

		rsTag.setBoolean("Power", isPowered);
		rsTag.setByte("Mode", (byte) rsMode.ordinal());
		nbt.setTag("RS", rsTag);
        return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addByte(distance);

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addByte(distance);

		return payload;
	}

	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addByte(MathHelper.clamp(distance, MIN_DISTANCE, MAX_DISTANCE));

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		distance = payload.getByte();
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		byte newDist = payload.getByte();

		if (newDist != distance) {
			distance = newDist;
		}
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {
			distance = payload.getByte();
		} else {
			payload.getByte();
		}
	}

	@Override
	public void onNeighborBlockChange() {

		setPowered(worldObj.isBlockPowered(getPos()));
	}

	public final boolean redstoneControlOrDisable() {

		return rsMode.isDisabled() || isPowered == rsMode.getState();
	}

	/* IRedstoneControl */
	@Override
	public final void setPowered(boolean powered) {

		boolean wasPowered = isPowered;
		isPowered = powered;
		if (wasPowered != isPowered) {
			if (ServerHelper.isClientWorld(worldObj)) {
                BlockUtils.fireBlockUpdate(worldObj,getPos());
			} else {
				PacketTEBase.sendRSPowerUpdatePacketToClients(this, worldObj, getPos());
			}
		}
	}

	@Override
	public final boolean isPowered() {

		return isPowered;
	}

	@Override
	public final void setControl(ControlMode control) {

		rsMode = control;
		if (ServerHelper.isClientWorld(worldObj)) {
			PacketTEBase.sendRSConfigUpdatePacketToServer(this, getPos());
		} else {
			sendUpdatePacket(Side.CLIENT);
			boolean powered = isPowered;
			isPowered = !powered;
			setPowered(powered);
		}
	}

	@Override
	public final ControlMode getControl() {

		return rsMode;
	}

}
