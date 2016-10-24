package cofh.thermalexpansion.block.device;

import cofh.api.energy.EnergyStorage;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.thermalexpansion.block.TileAugmentable;
import cofh.thermalexpansion.core.TEProps;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileChunkLoader extends TileAugmentable implements IFluidHandler {

	static final int TYPE = 0;
	static SideConfig defaultSideConfig = new SideConfig();

	FluidTankAdv tank = new FluidTankAdv(TEProps.MAX_FLUID_SMALL);
	EnergyStorage energyStorage = new EnergyStorage(400000);
	Ticket ticket;
	byte radius;

	public TileChunkLoader() {

	}

	@Override
	public String getName() {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getType() {

		// TODO Auto-generated method stub
		return 0;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return null;
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return null;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		return super.writeToNBT(nbt);
	}

	/* IFluidHandler */
	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {

		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {

		return tank.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {

		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {

		return true;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {

		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {

		return new FluidTankInfo[] { tank.getInfo() };
	}

	/* IEnergyInfo */
	@Override
	public int getInfoEnergyPerTick() {

		// return calcEnergy() * energyMod;
		return 0;
	}

	@Override
	public int getInfoMaxEnergyPerTick() {

		// return energyConfig.maxPower * energyMod;
		return 0;
	}

	/* ISidedTexture */
	@Override
	public TextureAtlasSprite getTexture(int side, int pass) {

		// TODO Auto-generated method stub
		return null;
	}

}
