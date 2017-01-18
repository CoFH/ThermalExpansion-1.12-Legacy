package cofh.thermalexpansion.block.device;

import cofh.api.energy.EnergyStorage;
import cofh.core.util.fluid.FluidTankCore;
import cofh.thermalexpansion.block.TileAugmentable;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class TileChunkLoader extends TileAugmentable {

	static final int TYPE = 0;
	static SideConfig defaultSideConfig = new SideConfig();

	FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
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

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {

		return super.hasCapability(capability, facing) || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler() {
				@Override
				public IFluidTankProperties[] getTankProperties() {

					return FluidTankProperties.convert(new FluidTankInfo[] { tank.getInfo() });
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					return tank.fill(resource, doFill);
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					return tank.drain(resource, doDrain);
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					return tank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, facing);
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
