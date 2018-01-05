package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.network.PacketBase;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.RenderHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine.Type;
import cofh.thermalexpansion.gui.client.machine.GuiEnchanter;
import cofh.thermalexpansion.gui.container.machine.ContainerEnchanter;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.util.managers.machine.EnchanterManager;
import cofh.thermalexpansion.util.managers.machine.EnchanterManager.EnchanterRecipe;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
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
import java.util.Arrays;
import java.util.HashSet;

public class TileEnchanter extends TileMachineBase {

	private static final int TYPE = Type.ENCHANTER.getMetadata();
	public static int basePower = 20;

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 7;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0, 1 }, { 2 }, { 0 }, { 1 }, { 0, 1, 2 }, { 0, 1, 2 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_ALL, INPUT_PRIMARY, INPUT_SECONDARY, OPEN, OMNI };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, true, false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false, false, true, false };

		VALID_AUGMENTS[TYPE] = new HashSet<>();
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_ENCHANTER_EMPOWERED);

		LIGHT_VALUES[TYPE] = 12;

		GameRegistry.registerTileEntity(TileEnchanter.class, "thermalexpansion:machine_enchanter");

		config();
	}

	public static void config() {

		String category = "Machine.Enchanter";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy consumption (in RF/t) for an Arcane Ensorcellator. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower, smallStorage);
	}

	private int inputTrackerPrimary;
	private int inputTrackerSecondary;
	private int outputTracker;

	public boolean lockPrimary = true;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_LARGE);

	/* AUGMENTS */
	protected boolean augmentEmpowered;

	public TileEnchanter() {

		super();
		inventory = new ItemStack[2 + 1 + 1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);
		tank.setLock(TFFluids.fluidExperience);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public int getMaxInputSlot() {

		return 1;
	}

	@Override
	protected boolean canStart() {

		if (inventory[0].isEmpty() || inventory[1].isEmpty() || energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		EnchanterRecipe recipe = EnchanterManager.getRecipe(inventory[1], inventory[0]);

		if (recipe == null || tank.getFluidAmount() < recipe.getExperience()) {
			return false;
		}
		if (recipe.getType() == EnchanterManager.Type.EMPOWERED && !augmentEmpowered) {
			return false;
		}
		if (EnchanterManager.isRecipeReversed(inventory[0], inventory[1])) {
			if (recipe.getPrimaryInput().getCount() > inventory[1].getCount() || recipe.getSecondaryInput().getCount() > inventory[0].getCount()) {
				return false;
			}
		} else {
			if (recipe.getPrimaryInput().getCount() > inventory[0].getCount() || recipe.getSecondaryInput().getCount() > inventory[1].getCount()) {
				return false;
			}
		}
		ItemStack output = recipe.getOutput();

		return inventory[2].isEmpty() || inventory[2].isItemEqual(output) && inventory[2].getCount() + output.getCount() <= output.getMaxStackSize();
	}

	@Override
	protected boolean hasValidInput() {

		EnchanterRecipe recipe = EnchanterManager.getRecipe(inventory[1], inventory[0]);

		if (recipe == null) {
			return false;
		}
		if (EnchanterManager.isRecipeReversed(inventory[0], inventory[1])) {
			if (recipe.getPrimaryInput().getCount() > inventory[1].getCount() || recipe.getSecondaryInput().getCount() > inventory[0].getCount()) {
				return false;
			}
		} else {
			if (recipe.getPrimaryInput().getCount() > inventory[0].getCount() || recipe.getSecondaryInput().getCount() > inventory[1].getCount()) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void processStart() {

		processMax = EnchanterManager.getRecipe(inventory[1], inventory[0]).getEnergy() * energyMod / ENERGY_BASE;
		processRem = processMax;
	}

	@Override
	protected void processFinish() {

		EnchanterRecipe recipe = EnchanterManager.getRecipe(inventory[1], inventory[0]);

		if (recipe == null) {
			processOff();
			return;
		}
		tank.drain(recipe.getExperience(), true);
		ItemStack primaryItem = recipe.getOutput();
		if (inventory[2].isEmpty()) {
			inventory[2] = ItemHelper.cloneStack(primaryItem);
		} else {
			inventory[2].grow(primaryItem.getCount());
		}
		if (EnchanterManager.isRecipeReversed(inventory[0], inventory[1])) {
			inventory[1].shrink(recipe.getPrimaryInput().getCount());
			inventory[0].shrink(recipe.getSecondaryInput().getCount());
		} else {
			inventory[0].shrink(recipe.getPrimaryInput().getCount());
			inventory[1].shrink(recipe.getSecondaryInput().getCount());
		}
		if (inventory[0].getCount() <= 0) {
			inventory[0] = ItemStack.EMPTY;
		}
		if (inventory[1].getCount() <= 0) {
			inventory[1] = ItemStack.EMPTY;
		}
	}

	@Override
	protected void transferInput() {

		if (!enableAutoInput) {
			return;
		}
		int side;
		for (int i = inputTrackerPrimary + 1; i <= inputTrackerPrimary + 6; i++) {
			side = i % 6;
			if (isPrimaryInput(sideConfig.sideTypes[sideCache[side]])) {
				if (extractItem(0, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					inputTrackerPrimary = side;
					break;
				}
			}
		}
		for (int i = inputTrackerSecondary + 1; i <= inputTrackerSecondary + 6; i++) {
			side = i % 6;
			if (isSecondaryInput(sideConfig.sideTypes[sideCache[side]])) {
				if (extractItem(1, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					inputTrackerSecondary = side;
					break;
				}
			}
		}
	}

	@Override
	protected void transferOutput() {

		if (!enableAutoOutput) {
			return;
		}
		if (inventory[2].isEmpty()) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				if (transferItem(2, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	@Override
	protected boolean readPortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		if (!super.readPortableTagInternal(player, tag)) {
			return false;
		}
		lockPrimary = tag.getBoolean("SlotLock");
		return true;
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		if (!super.writePortableTagInternal(player, tag)) {
			return false;
		}
		tag.setBoolean("SlotLock", lockPrimary);
		return true;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiEnchanter(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerEnchanter(inventory, this);
	}

	@Override
	public FluidTankCore getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	public void setMode(boolean mode) {

		boolean lastMode = lockPrimary;
		lockPrimary = mode;
		sendModePacket();
		lockPrimary = lastMode;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTrackerPrimary = nbt.getInteger("TrackIn1");
		inputTrackerSecondary = nbt.getInteger("TrackIn2");
		outputTracker = nbt.getInteger("TrackOut");
		lockPrimary = nbt.getBoolean("SlotLock");
		tank.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn1", inputTrackerPrimary);
		nbt.setInteger("TrackIn2", inputTrackerSecondary);
		nbt.setInteger("TrackOut", outputTracker);
		nbt.setBoolean("SlotLock", lockPrimary);
		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */

	/* CLIENT -> SERVER */
	@Override
	public PacketBase getModePacket() {

		PacketBase payload = super.getModePacket();

		payload.addBool(lockPrimary);

		return payload;
	}

	@Override
	protected void handleModePacket(PacketBase payload) {

		super.handleModePacket(payload);

		lockPrimary = payload.getBool();

		callNeighborTileChange();
	}

	/* SERVER -> CLIENT */
	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addBool(lockPrimary);
		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		lockPrimary = payload.getBool();
		tank.setFluid(payload.getFluidStack());
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		augmentEmpowered = false;
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentEmpowered && TEProps.MACHINE_ENCHANTER_EMPOWERED.equals(id)) {
			augmentEmpowered = true;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		if (lockPrimary) {
			if (slot == 0) {
				return EnchanterManager.isItemArcana(stack);
			}
			if (slot == 1) {
				return !EnchanterManager.isItemArcana(stack) && EnchanterManager.isItemValid(stack);
			}
		}
		return slot > 1 || EnchanterManager.isItemValid(stack);
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
			return side != facing ? TETextures.MACHINE_SIDE : isActive ? RenderHelper.getFluidTexture(TFFluids.fluidExperience) : TETextures.MACHINE_FACE[TYPE];
		} else if (side < 6) {
			return side != facing ? TETextures.CONFIG[sideConfig.sideTypes[sideCache[side]]] : isActive ? TETextures.MACHINE_ACTIVE[TYPE] : TETextures.MACHINE_FACE[TYPE];
		}
		return TETextures.MACHINE_SIDE;
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
					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, true, false) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (from == null || allowInsertion(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return tank.fill(resource, doFill);
					}
					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (isActive) {
						return null;
					}
					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return tank.drain(resource, doDrain);
					}
					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (isActive) {
						return null;
					}
					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return tank.drain(maxDrain, doDrain);
					}
					return null;
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
