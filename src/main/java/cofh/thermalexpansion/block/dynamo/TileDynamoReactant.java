package cofh.thermalexpansion.block.dynamo;

import codechicken.lib.texture.TextureUtils;
import cofh.core.CoFHProps;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.inventory.ComparableItemStack;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.gui.client.dynamo.GuiDynamoReactant;
import cofh.thermalexpansion.gui.container.dynamo.ContainerDynamoReactant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;

import gnu.trove.map.hash.TObjectIntHashMap;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import javax.annotation.Nullable;

public class TileDynamoReactant extends TileDynamoBase {

	static final int TYPE = BlockDynamo.Types.REACTANT.ordinal();

	public static void initialize() {

		GameRegistry.registerTileEntity(TileDynamoReactant.class, "thermalexpansion.DynamoReactant");
	}

	FluidTankAdv tank = new FluidTankAdv(MAX_FLUID);

	FluidStack renderFluid = new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME);
	int reactantRF;
	int currentReactantRF;
	int reactantMod = FUEL_MOD;

	public TileDynamoReactant() {

		super();
		inventory = new ItemStack[1];
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	protected boolean canGenerate() {

		if (fuelRF > 0) {
			return reactantRF > 0 || getReactantEnergy(inventory[0]) > 0;
		}
		if (reactantRF > 0) {
			return tank.getFluidAmount() >= 50;
		}
		return tank.getFluidAmount() >= 50 && getReactantEnergy(inventory[0]) > 0;
	}

	@Override
	protected void generate() {

		int energy;

		if (fuelRF <= 0) {
			fuelRF = getFuelEnergy(tank.getFluid()) * reactantMod / FUEL_MOD * fuelMod / FUEL_MOD;
			tank.drain(50, true);
		}
		if (reactantRF <= 0) {
			energy = (getReactantEnergy(inventory[0]) / FUEL_MOD) * fuelMod;
			reactantMod = getReactantMod(inventory[0]);
			reactantRF += energy;
			currentReactantRF = energy;
			inventory[0] = ItemHelper.consumeItem(inventory[0]);
		}
		energy = calcEnergy() * energyMod;
		energyStorage.modifyEnergyStored(energy);
		fuelRF -= energy;
		reactantRF -= energy;
	}

	@Override
	public TextureAtlasSprite getActiveIcon() {

		return TextureUtils.getTexture(renderFluid.getFluid().getStill());
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiDynamoReactant(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerDynamoReactant(inventory, this);
	}

	@Override
	public int getScaledDuration(int scale) {

		if (currentReactantRF <= 0) {
			currentReactantRF = sugarRF;
		}
		return reactantRF * scale / currentReactantRF;
	}

	@Override
	public FluidTankAdv getTank(int tankIndex) {

		return tank;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		currentReactantRF = nbt.getInteger("ReactMax");
		reactantRF = nbt.getInteger("React");
		tank.readFromNBT(nbt);

		if (!isValidFuel(tank.getFluid())) {
			tank.setFluid(null);
		}
		if (tank.getFluid() != null) {
			renderFluid = tank.getFluid();
		}
		if (currentReactantRF <= 0) {
			currentReactantRF = sugarRF;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("ReactMax", currentReactantRF);
		nbt.setInteger("React", reactantRF);
		tank.writeToNBT(nbt);
        return nbt;
	}

	/* NETWORK METHODS */
	@Override
	public PacketCoFHBase getPacket() {

		PacketCoFHBase payload = super.getPacket();

		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addFluidStack(tank.getFluid());
		payload.addInt(reactantRF);
		payload.addInt(currentReactantRF);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		tank.setFluid(payload.getFluidStack());
		reactantRF = payload.getInt();
		currentReactantRF = payload.getInt();
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(PacketCoFHBase payload, boolean isServer) {

		super.handleTilePacket(payload, isServer);

		renderFluid = payload.getFluidStack();
		if (renderFluid == null) {
			renderFluid = new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME);
		}
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
                    return FluidTankProperties.convert(new FluidTankInfo[] { tank.getInfo() });
                }

                @Override
                public int fill(FluidStack resource, boolean doFill) {
                    if (resource == null || (from != null && from.ordinal() == facing && !augmentCoilDuct)) {
                        return 0;
                    }
                    if (isValidFuel(resource)) {
                        return tank.fill(resource, doFill);
                    }
                    return 0;
                }

                @Nullable
                @Override
                public FluidStack drain(FluidStack resource, boolean doDrain) {
                    if (resource == null || !augmentCoilDuct && from.ordinal() == facing) {
                        return null;
                    }
                    if (resource.equals(tank.getFluid())) {
                        return tank.drain(resource.amount, doDrain);
                    }
                    return null;
                }

                @Nullable
                @Override
                public FluidStack drain(int maxDrain, boolean doDrain) {
                    if (!augmentCoilDuct && from.ordinal() == facing) {
                        return null;
                    }
                    return tank.drain(maxDrain, doDrain);
                }
            });
        }
        return super.getCapability(capability, from);
    }

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return getReactantEnergy(stack) > 0;
	}

	/* ISidedInventory */
	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		return side.ordinal() != facing || augmentCoilDuct ? SLOTS : CoFHProps.EMPTY_INVENTORY;
	}

	/* FUEL MANAGER */
	static int sugarRF = 16000;
	static int gunpowderRF = 160000;
	static int blazePowderRF = 640000;
	static int ghastTearRF = 1600000;
	static int netherStarRF = 6400000;

	static TObjectIntHashMap<Fluid> fuels = new TObjectIntHashMap<Fluid>();
	static TObjectIntHashMap<ComparableItemStack> reactants = new TObjectIntHashMap<ComparableItemStack>();

	static {
		addReactant(new ItemStack(Items.SUGAR, 1, 0), 16000);
		addReactant(new ItemStack(Items.GUNPOWDER, 1, 0), 160000);
		addReactant(new ItemStack(Items.BLAZE_POWDER, 1, 0), 640000);
		addReactant(new ItemStack(Items.GHAST_TEAR, 1, 0), 1600000);
		addReactant(new ItemStack(Items.NETHER_STAR, 1, 0), 6400000);
	}

	public static boolean isValidFuel(FluidStack stack) {

		return stack != null && fuels.containsKey(stack.getFluid());
	}

	public static boolean isValidReactant(ItemStack stack) {

		return stack != null && reactants.containsKey(new ComparableItemStack(stack));
	}

	public static boolean addFuel(Fluid fluid, int energy) {

		if (fluid == null || energy < 10000 || energy > 200000000) {
			return false;
		}
		fuels.put(fluid, energy / 20);
		return true;
	}

	public static boolean addReactant(ItemStack stack, int energy) {

		if (stack == null || energy < 10000 || energy > 200000000) {
			return false;
		}
		reactants.put(new ComparableItemStack(stack), energy);
		return true;
	}

	public static boolean removeFuel(Fluid fluid) {

		fuels.remove(fluid);
		return true;
	}

	public static boolean removeReactant(ItemStack stack) {

		reactants.remove(stack);
		return true;
	}

	public static int getFuelEnergy(FluidStack stack) {

		return stack == null ? 0 : fuels.get(stack.getFluid());
	}

	public static int getReactantEnergy(ItemStack stack) {

		return stack == null ? 0 : reactants.get(new ComparableItemStack(stack));
	}

	public static int getReactantMod(ItemStack stack) {

		if (stack == null) {
			return 0;
		}
		return FUEL_MOD;
	}

}
