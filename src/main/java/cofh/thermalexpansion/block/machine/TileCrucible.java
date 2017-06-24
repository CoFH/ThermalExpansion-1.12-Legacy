package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.RenderHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.machine.GuiCrucible;
import cofh.thermalexpansion.gui.container.machine.ContainerCrucible;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TESounds;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager.CrucibleRecipe;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;

public class TileCrucible extends TileMachineBase {

	private static final int TYPE = BlockMachine.Type.CRUCIBLE.getMetadata();
	public static int basePower = 50;

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, {}, { 0 }, { 0 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { 0, 1, 4, 7, 8 };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false, false };

		VALID_AUGMENTS[TYPE] = new HashSet<>();

		LIGHT_VALUES[TYPE] = 14;

		GameRegistry.registerTileEntity(TileCrucible.class, "thermalexpansion:machine_crucible");

		config();
	}

	public static void config() {

		String category = "Machine.Crucible";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower);
	}

	private int inputTracker;
	private int outputTrackerFluid;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_LARGE);
	private FluidStack renderFluid = new FluidStack(FluidRegistry.LAVA, 0);

	public TileCrucible() {

		super();
		inventory = new ItemStack[1 + 1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void update() {

		if (ServerHelper.isClientWorld(world)) {
			return;
		}
		transferOutputFluid();

		super.update();
	}

	@Override
	public int getLightValue() {

		return isActive ? renderFluid.getFluid().getLuminosity(renderFluid) : 0;
	}

	@Override
	protected boolean canStart() {

		if (inventory[0].isEmpty() || energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		CrucibleRecipe recipe = CrucibleManager.getRecipe(inventory[0]);

		if (recipe == null) {
			return false;
		}
		if (inventory[0].getCount() < recipe.getInput().getCount()) {
			return false;
		}
		FluidStack output = recipe.getOutput();
		return tank.fill(output, false) == output.amount;
	}

	@Override
	protected boolean hasValidInput() {

		CrucibleRecipe recipe = CrucibleManager.getRecipe(inventory[0]);
		return recipe != null && recipe.getInput().getCount() <= inventory[0].getCount();
	}

	@Override
	protected void processStart() {

		processMax = CrucibleManager.getRecipe(inventory[0]).getEnergy() * energyMod / ENERGY_BASE;
		processRem = processMax;

		String prevID = renderFluid.getFluid().getName();
		renderFluid = CrucibleManager.getRecipe(inventory[0]).getOutput().copy();
		renderFluid.amount = 0;

		if (!prevID.equals(renderFluid.getFluid().getName())) {
			sendFluidPacket();
		}
	}

	@Override
	protected void processFinish() {

		CrucibleRecipe recipe = CrucibleManager.getRecipe(inventory[0]);

		if (recipe == null) {
			processOff();
			return;
		}
		tank.fill(recipe.getOutput(), true);
		inventory[0].shrink(1);

		if (inventory[0].getCount() <= 0) {
			inventory[0] = ItemStack.EMPTY;
		}
	}

	@Override
	protected void transferInput() {

		if (!enableAutoInput) {
			return;
		}
		int side;
		for (int i = inputTracker + 1; i <= inputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryInput(sideConfig.sideTypes[sideCache[side]])) {
				if (extractItem(0, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					inputTracker = side;
					break;
				}
			}
		}
	}

	private void transferOutputFluid() {

		if (!enableAutoOutput || tank.getFluidAmount() <= 0) {
			return;
		}
		int side;
		FluidStack output = new FluidStack(tank.getFluid(), Math.min(tank.getFluidAmount(), FLUID_TRANSFER[level]));
		for (int i = outputTrackerFluid + 1; i <= outputTrackerFluid + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				int toDrain = FluidHelper.insertFluidIntoAdjacentFluidHandler(this, EnumFacing.VALUES[side], output, true);
				if (toDrain > 0) {
					tank.drain(toDrain, true);
					outputTrackerFluid = side;
					break;
				}
			}
		}
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiCrucible(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerCrucible(inventory, this);
	}

	@Override
	public FluidTankCore getTank() {

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

		inputTracker = nbt.getInteger("TrackIn");
		outputTrackerFluid = nbt.getInteger("TrackOut");
		tank.readFromNBT(nbt);

		if (tank.getFluid() != null) {
			renderFluid = tank.getFluid().copy();
		} else if (CrucibleManager.getRecipe(inventory[0]) != null) {
			renderFluid = CrucibleManager.getRecipe(inventory[0]).getOutput().copy();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut", outputTrackerFluid);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getFluidPacket() {

		PacketCoFHBase payload = super.getFluidPacket();

		payload.addFluidStack(renderFluid);

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		if (tank.getFluid() == null) {
			payload.addFluidStack(renderFluid);
		} else {
			payload.addFluidStack(tank.getFluid());
		}
		return payload;
	}

	@Override
	public PacketCoFHBase getTilePacket() {

		PacketCoFHBase payload = super.getTilePacket();

		payload.addFluidStack(renderFluid);

		return payload;
	}

	@Override
	protected void handleFluidPacket(PacketCoFHBase payload) {

		super.handleFluidPacket(payload);

		renderFluid = payload.getFluidStack();

		callBlockUpdate();
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		tank.setFluid(payload.getFluidStack());
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketCoFHBase payload) {

		super.handleTilePacket(payload);

		renderFluid = payload.getFluidStack();
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return slot != 0 || CrucibleManager.recipeExists(stack);
	}

	/* ISidedTexture */
	@Override
	public TextureAtlasSprite getTexture(int side, int pass) {

		if (pass == 0) {
			if (side == 0) {
				return TETextures.MACHINE_BOTTOM;
			} else if (side == 1) {
				return TETextures.MACHINE_TOP;
			}
			return side != facing ? TETextures.MACHINE_SIDE : isActive ? RenderHelper.getFluidTexture(renderFluid) : TETextures.MACHINE_FACE[TYPE];
		} else if (side < 6) {
			return side != facing ? TETextures.CONFIG[sideConfig.sideTypes[sideCache[side]]] : isActive ? TETextures.MACHINE_ACTIVE[TYPE] : TETextures.MACHINE_FACE[TYPE];
		}
		return TETextures.MACHINE_SIDE;
	}

	/* ISoundSource */
	@Override
	public SoundEvent getSoundEvent() {

		return TESounds.MACHINE_CRUCIBLE;
	}

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return super.hasCapability(capability, from) || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler() {

				@Override
				public IFluidTankProperties[] getTankProperties() {

					FluidTankInfo info = tank.getInfo();
					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, false, true) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (from != null && !allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return null;
					}
					if (resource == null || !resource.isFluidEqual(tank.getFluid())) {
						return null;
					}
					return tank.drain(resource.amount, doDrain);
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (from != null && !allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return null;
					}
					return tank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
