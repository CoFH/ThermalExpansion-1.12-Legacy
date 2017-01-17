package cofh.thermalexpansion.block.sponge;

import cofh.api.tileentity.ISidedTexture;
import cofh.core.network.PacketCoFHBase;
import cofh.core.render.IconRegistry;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.block.TEBlocks;
import cofh.thermalexpansion.block.TileTEBase;
import cofh.thermalexpansion.core.TEProps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

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
	public void blockBroken() {

		IBlockState query;
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					BlockPos offsetPos = getPos().add(x, y, z);
					query = worldObj.getBlockState(offsetPos);
					if (query.getBlock() == TEBlocks.blockAirBarrier) {
						worldObj.setBlockToAir(offsetPos);
					} else if (query.getBlock() == TEBlocks.blockSponge) {
						worldObj.scheduleBlockUpdate(offsetPos, TEBlocks.blockSponge, 1, 0);
					}
				}
			}
		}
		for (int x = -2; x <= 2; x += 4) {
			for (int y = -2; y <= 2; y++) {
				for (int z = -2; z <= 2; z++) {
					BlockPos offsetPos = getPos().add(x, y, z);
					query = worldObj.getBlockState(offsetPos);
					if (query.getBlock() == TEBlocks.blockSponge) {
						worldObj.scheduleBlockUpdate(offsetPos, TEBlocks.blockSponge, 1, 0);
					}
				}
			}
		}
		for (int x = -2; x <= 2; x++) {
			for (int y = -2; y <= 2; y += 4) {
				for (int z = -2; z <= 2; z++) {
					BlockPos offsetPos = getPos().add(x, y, z);
					query = worldObj.getBlockState(offsetPos);
					if (query.getBlock() == TEBlocks.blockSponge) {
						worldObj.scheduleBlockUpdate(offsetPos, TEBlocks.blockSponge, 1, 0);
					}
				}
			}
		}
		for (int x = -2; x <= 2; x++) {
			for (int y = -2; y <= 2; y++) {
				for (int z = -2; z <= 2; z += 4) {
					BlockPos offsetPos = getPos().add(x, y, z);
					query = worldObj.getBlockState(offsetPos);
					if (query.getBlock() == TEBlocks.blockSponge) {
						worldObj.scheduleBlockUpdate(offsetPos, TEBlocks.blockSponge, 1, 0);
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
		IBlockState query;
		int queryMeta;
		Fluid queryFluid;
		int bucketCounter = 0;
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					BlockPos offsetPos = getPos().add(x, y, z);

					query = worldObj.getBlockState(offsetPos);
					queryMeta = query.getBlock().getMetaFromState(query);
					queryFluid = FluidHelper.lookupFluidForBlock(query.getBlock());
					if (queryMeta == 0) {
						if (!full && queryFluid != null && queryFluid.getTemperature() < TEProps.MAGMATIC_TEMPERATURE) {
							if (myFluidStack == null) {
								myFluidStack = new FluidStack(queryFluid, 1000);
								bucketCounter = 1;
								worldObj.setBlockState(offsetPos, TEBlocks.blockAirBarrier.getDefaultState(), 3);
							} else if (myFluidStack.getFluid() == queryFluid) {
								bucketCounter++;
								worldObj.setBlockState(offsetPos, TEBlocks.blockAirBarrier.getDefaultState(), 3);
							}
						} else if (query.getBlock().isAir(query, worldObj, offsetPos)) {
							worldObj.setBlockState(offsetPos, TEBlocks.blockAirBarrier.getDefaultState(), 3);
						}
					} else if (query.getBlock().isAir(query, worldObj, offsetPos) || query.getMaterial().isLiquid()) {
						if (queryFluid != null && queryFluid.getTemperature() >= TEProps.MAGMATIC_TEMPERATURE) {
							// do nothing
						} else {
							worldObj.setBlockState(offsetPos, TEBlocks.blockAirBarrier.getDefaultState(), 3);
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
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setBoolean("PlaceFull", fullOnPlace);
		if (myFluidStack != null) {
			myFluidStack.writeToNBT(nbt);
		}
		return nbt;
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
	public TextureAtlasSprite getTexture(int side, int pass) {

		return IconRegistry.getIcon("Sponge", full ? getType() + 8 : getType());
	}

}
