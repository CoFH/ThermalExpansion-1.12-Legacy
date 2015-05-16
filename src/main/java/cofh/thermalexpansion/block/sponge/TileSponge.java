package cofh.thermalexpansion.block.sponge;

import cofh.api.tileentity.ISidedTexture;
import cofh.core.network.PacketCoFHBase;
import cofh.core.render.IconRegistry;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.block.TEBlocks;
import cofh.thermalexpansion.block.TileTEBase;
import cofh.thermalexpansion.core.TEProps;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class TileSponge extends TileTEBase implements ISidedTexture {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileSponge.class, "thermalexpansion.Sponge");
	}

	boolean full = false;
	boolean fullOnPlace = false;
	FluidStack myFluidStack;

	public TileSponge() {

	}

	public TileSponge(int metadata) {

	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.sponge." + BlockSponge.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return BlockSponge.Types.BASIC.ordinal();
	}

	@Override
	public boolean canUpdate() {

		return false;
	}

	@Override
	public void blockBroken() {

		int i, j, k;

		Block query;
		for (i = xCoord - 1; i <= xCoord + 1; i++) {
			for (j = yCoord - 1; j <= yCoord + 1; j++) {
				for (k = zCoord - 1; k <= zCoord + 1; k++) {
					query = worldObj.getBlock(i, j, k);
					if (query == TEBlocks.blockAirBarrier) {
						worldObj.setBlockToAir(i, j, k);
					} else if (query == TEBlocks.blockSponge) {
						worldObj.scheduleBlockUpdate(i, j, k, TEBlocks.blockSponge, 1);
					}
				}
			}
		}
		for (i = xCoord - 2; i <= xCoord + 2; i += 4) {
			for (j = yCoord - 2; j <= yCoord + 2; j++) {
				for (k = zCoord - 2; k <= zCoord + 2; k++) {
					if (worldObj.getBlock(i, j, k) == TEBlocks.blockSponge) {
						worldObj.scheduleBlockUpdate(i, j, k, TEBlocks.blockSponge, 1);
					}
				}
			}
		}
		for (i = xCoord - 2; i <= xCoord + 2; i++) {
			for (j = yCoord - 2; j <= yCoord + 2; j += 4) {
				for (k = zCoord - 2; k <= zCoord + 2; k++) {
					if (worldObj.getBlock(i, j, k) == TEBlocks.blockSponge) {
						worldObj.scheduleBlockUpdate(i, j, k, TEBlocks.blockSponge, 1);
					}
				}
			}
		}
		for (i = xCoord - 2; i <= xCoord + 2; i++) {
			for (j = yCoord - 2; j <= yCoord + 2; j++) {
				for (k = zCoord - 2; k <= zCoord + 2; k += 4) {
					if (worldObj.getBlock(i, j, k) == TEBlocks.blockSponge) {
						worldObj.scheduleBlockUpdate(i, j, k, TEBlocks.blockSponge, 1);
					}
				}
			}
		}
	}

	public FluidStack getFluid() {

		return myFluidStack;
	}

	public void absorb() {

		placeAir();
	}

	public void placeAir() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (fullOnPlace) {
			return;
		}
		Block query;
		int queryMeta;
		Fluid queryFluid;
		int bucketCounter = 0;
		for (int i = xCoord - 1; i <= xCoord + 1; i++) {
			for (int j = yCoord - 1; j <= yCoord + 1; j++) {
				for (int k = zCoord - 1; k <= zCoord + 1; k++) {
					query = worldObj.getBlock(i, j, k);
					queryMeta = worldObj.getBlockMetadata(i, j, k);
					queryFluid = FluidHelper.lookupFluidForBlock(query);
					if (queryMeta == 0) {
						if (!full && queryFluid != null && queryFluid.getTemperature() < TEProps.MAGMATIC_TEMPERATURE) {
							if (myFluidStack == null) {
								myFluidStack = new FluidStack(queryFluid, 1000);
								bucketCounter = 1;
								worldObj.setBlock(i, j, k, TEBlocks.blockAirBarrier, 0, 3);
							} else if (myFluidStack.getFluid() == queryFluid) {
								bucketCounter++;
								worldObj.setBlock(i, j, k, TEBlocks.blockAirBarrier, 0, 3);
							}
						} else if (query.isAir(worldObj, i, j, k)) {
							worldObj.setBlock(i, j, k, TEBlocks.blockAirBarrier, 0, 3);
						}
					} else if (query.isAir(worldObj, i, j, k) || query.getMaterial().isLiquid()) {
						if (queryFluid != null && queryFluid.getTemperature() >= TEProps.MAGMATIC_TEMPERATURE) {
							// do nothing
						} else {
							worldObj.setBlock(i, j, k, TEBlocks.blockAirBarrier, 0, 3);
						}
					}
				}
			}
		}
		if (myFluidStack != null) {
			myFluidStack.amount = bucketCounter * 1000;
			full = true;
			sendUpdatePacket(Side.CLIENT);
		}
	}

	public void setFluid(FluidStack stack) {

		this.myFluidStack = stack;
		fullOnPlace = true;
		full = true;
	}

	/* GUI METHODS */
	@Override
	public boolean hasGui() {

		return false;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		fullOnPlace = nbt.getBoolean("PlaceFull");
		myFluidStack = FluidStack.loadFluidStackFromNBT(nbt);
		full = myFluidStack != null;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setBoolean("PlaceFull", fullOnPlace);
		if (myFluidStack != null) {
			myFluidStack.writeToNBT(nbt);
		}
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addBool(full);
		payload.addFluidStack(myFluidStack);

		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		if (!isServer) {
			full = payload.getBool();
			myFluidStack = payload.getFluidStack();
		} else {
			payload.getBool();
			payload.getFluidStack();
		}
	}

	/* ISidedTexture */
	@Override
	public IIcon getTexture(int side, int pass) {

		return IconRegistry.getIcon("Sponge", full ? getType() + 8 : getType());
	}

}
