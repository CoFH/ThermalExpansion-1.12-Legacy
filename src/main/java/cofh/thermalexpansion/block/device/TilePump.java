package cofh.thermalexpansion.block.device;

import cofh.api.energy.EnergyStorage;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TileAugmentable;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.client.machine.GuiTransposer;
import cofh.thermalexpansion.gui.container.machine.ContainerTransposer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;

public class TilePump extends TileAugmentable {

	static final int TYPE = BlockDevice.Types.EXTENDER.ordinal();
	static SideConfig defaultSideConfig = new SideConfig();

	public static void initialize() {

		defaultSideConfig = new SideConfig();
		defaultSideConfig.numConfig = 2;
		defaultSideConfig.slotGroups = new int[][] { {}, {} };
		defaultSideConfig.allowInsertionSide = new boolean[] { false, false };
		defaultSideConfig.allowExtractionSide = new boolean[] { false, false };
		defaultSideConfig.allowInsertionSlot = new boolean[] {};
		defaultSideConfig.allowExtractionSlot = new boolean[] {};
		defaultSideConfig.sideTex = new int[] { 0, 4 };
		defaultSideConfig.defaultSides = new byte[] { 0, 0, 1, 1, 1, 1 };

		GameRegistry.registerTileEntity(TilePump.class, "thermalexpansion.Pump");
		configure();
	}

	public static void configure() {

		String comment = "Enable this to allow for Pumps to be securable.";
		enableSecurity = ThermalExpansion.config.get("Security", "Device.Pump.Securable", enableSecurity, comment);
	}

	public static boolean enableSecurity = true;

	int outputTracker;
	FluidTankAdv tank = new FluidTankAdv(TEProps.MAX_FLUID_LARGE);

	public boolean reverse;

	public TilePump() {

		sideConfig = defaultSideConfig;
		sideCache = new byte[] { 0, 0, 1, 1, 1, 1 };
		energyStorage = new EnergyStorage(0);
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.device." + BlockDevice.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return BlockDevice.Types.EXTENDER.ordinal();
	}

	/*@Override
	public void updateEntity() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
	}*/

	@Override
	public void invalidate() {

		super.invalidate();
	}

	@Override
	public void validate() {

		super.validate();
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiTransposer(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTransposer(inventory, this);
	}

	@Override
	public FluidTankAdv getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		outputTracker = nbt.getInteger("Tracker");
		reverse = nbt.getBoolean("Rev");
		tank.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("Tracker", outputTracker);
		nbt.setBoolean("Rev", reverse);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* IReconfigurableSides */
	@Override
	public int getNumConfig(int side) {

		return 3;
	}

	/* ISidedTexture */
	@Override
	public TextureAtlasSprite getTexture(int side, int pass) {

		return null;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {

		return super.hasCapability(capability, facing) || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler() {
				@Override
				public IFluidTankProperties[] getTankProperties() {

					FluidTankInfo info = tank.getInfo();
					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, from != null && !reverse && sideCache[from.ordinal()] == 1, from != null && reverse && sideCache[from.ordinal()] == 2) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (!reverse || from == null || sideCache[from.ordinal()] != 1) {
						return 0;
					}
					return tank.fill(resource, doFill);
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (reverse || from == null || sideCache[from.ordinal()] != 2) {
						return null;
					}
					return tank.drain(resource, doDrain);
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (reverse || from == null || sideCache[from.ordinal()] != 2) {
						return null;
					}
					return tank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
