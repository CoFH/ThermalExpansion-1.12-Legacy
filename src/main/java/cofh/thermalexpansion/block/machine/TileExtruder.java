package cofh.thermalexpansion.block.machine;

import cofh.api.item.IAugmentItem.AugmentType;
import cofh.core.fluid.FluidTankCore;
import cofh.core.gui.container.ICustomInventory;
import cofh.core.network.PacketBase;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine.Type;
import cofh.thermalexpansion.gui.client.machine.GuiExtruder;
import cofh.thermalexpansion.gui.container.machine.ContainerExtruder;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.util.managers.machine.ExtruderManager;
import cofh.thermalexpansion.util.managers.machine.ExtruderManager.ExtruderRecipe;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
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

public class TileExtruder extends TileMachineBase implements ICustomInventory {

	private static final int TYPE = Type.EXTRUDER.getMetadata();
	public static int basePower = 20;

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, {}, { 0 }, { 0 }, { 0 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_ALL, OPEN, OMNI };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { true, false };

		VALID_AUGMENTS[TYPE] = new HashSet<>();
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_EXTRUDER_NO_WATER);

		LIGHT_VALUES[TYPE] = 14;

		GameRegistry.registerTileEntity(TileExtruder.class, "thermalexpansion:machine_extruder");

		config();
	}

	public static void config() {

		String category = "Machine.Extruder";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the Energy consumption (in RF/t) for an Igneous Extruder. This base value will scale with block level and Augments.";
		basePower = ThermalExpansion.CONFIG.getConfiguration().getInt("BasePower", category, basePower, MIN_BASE_POWER, MAX_BASE_POWER, comment);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower, smallStorage);
	}

	private ItemStack[] outputItem = new ItemStack[2];
	private int outputTracker;
	private int index = 0;
	private byte direction = 0;

	private FluidTankCore hotTank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	private FluidTankCore coldTank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);

	/* AUGMENTS */
	protected boolean augmentNoWater;

	protected boolean flagNoWater;

	public TileExtruder() {

		super();
		inventory = new ItemStack[1 + 1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);

		outputItem[0] = ExtruderManager.getOutput(index);
		outputItem[1] = outputItem[0].copy();
		hotTank.setLock(FluidRegistry.LAVA);
		coldTank.setLock(FluidRegistry.WATER);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	protected int getMaxInputSlot() {

		// This is a hack to prevent super() logic from working.
		return -1;
	}

	@Override
	protected boolean canStart() {

		ExtruderRecipe recipe = ExtruderManager.getRecipe(outputItem[0]);

		if (recipe == null) {
			return false;
		}
		if (hotTank.getFluidAmount() < Fluid.BUCKET_VOLUME) {
			return false;
		}
		if (!augmentNoWater && coldTank.getFluidAmount() < Fluid.BUCKET_VOLUME) {
			return false;
		}
		if (energyStorage.getEnergyStored() <= 0) {
			return false;
		}
		if (inventory[0].isEmpty()) {
			return true;
		}
		if (!inventory[0].isItemEqual(outputItem[0])) {
			return false;
		}
		return inventory[0].getCount() + outputItem[0].getCount() <= outputItem[0].getMaxStackSize();
	}

	@Override
	protected boolean canFinish() {

		return processRem <= 0;
	}

	@Override
	protected void processStart() {

		processMax = ExtruderManager.getRecipe(outputItem[0]).getEnergy() * energyMod / ENERGY_BASE;
		processRem = processMax;
		outputItem[1] = outputItem[0].copy();
	}

	@Override
	protected void processFinish() {

		ExtruderRecipe recipe = ExtruderManager.getRecipe(outputItem[1]);

		if (recipe == null) {
			processOff();
			return;
		}
		ItemStack output = recipe.getOutput();
		if (inventory[0].isEmpty()) {
			inventory[0] = ItemHelper.cloneStack(output);
		} else {
			inventory[0].grow(output.getCount());
		}
		hotTank.drain(recipe.getInputHot().amount, true);

		if (!augmentNoWater) {
			coldTank.drain(recipe.getInputCold().amount, true);
		}
		outputItem[1] = outputItem[0].copy();
	}

	@Override
	protected void transferOutput() {

		if (!getTransferOut()) {
			return;
		}
		if (inventory[0].isEmpty()) {
			return;
		}
		int side;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				if (transferItem(0, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
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
		if (tag.hasKey("OutputItem", 10)) {
			index = ExtruderManager.getIndex(new ItemStack(tag.getCompoundTag("OutputItem")));
			outputItem[0] = ExtruderManager.getOutput(index);
			if (!isActive) {
				outputItem[1] = outputItem[0].copy();
			}
		}
		return true;
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		if (!super.writePortableTagInternal(player, tag)) {
			return false;
		}
		tag.setTag("OutputItem", outputItem[0].writeToNBT(new NBTTagCompound()));
		return true;
	}

	@Override
	protected void setLevelFlags() {

		super.setLevelFlags();

		hasAutoInput = false;
		enableAutoInput = false;
	}

	private void setOutput() {

		if (index >= ExtruderManager.getOutputListSize()) {
			index = 0;
		} else if (index < 0) {
			index = ExtruderManager.getOutputListSize() - 1;
		}
		outputItem[0] = ExtruderManager.getOutput(index);
		if (!isActive) {
			outputItem[1] = outputItem[0].copy();
		}
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiExtruder(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerExtruder(inventory, this);
	}

	public FluidTankCore getTank(int tankIndex) {

		if (tankIndex == 0) {
			return hotTank;
		}
		return coldTank;
	}

	public FluidStack getTankFluid(int tankIndex) {

		if (tankIndex == 0) {
			return hotTank.getFluid();
		}
		return coldTank.getFluid();
	}

	public boolean augmentNoWater() {

		return augmentNoWater && flagNoWater;
	}

	public void setMode(byte direction) {

		this.direction = direction;
		sendModePacket();
		this.direction = 0;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		outputTracker = nbt.getInteger("TrackOut");

		if (nbt.hasKey("OutputItem", 10)) {
			index = ExtruderManager.getIndex(new ItemStack(nbt.getCompoundTag("OutputItem")));
		}
		outputItem[0] = ExtruderManager.getOutput(index);
		outputItem[1] = outputItem[0].copy();

		hotTank.readFromNBT(nbt.getCompoundTag("HotTank"));
		coldTank.readFromNBT(nbt.getCompoundTag("ColdTank"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackOut", outputTracker);

		nbt.setTag("OutputItem", outputItem[0].writeToNBT(new NBTTagCompound()));
		nbt.setTag("HotTank", hotTank.writeToNBT(new NBTTagCompound()));
		nbt.setTag("ColdTank", coldTank.writeToNBT(new NBTTagCompound()));
		return nbt;
	}

	/* NETWORK METHODS */

	/* CLIENT -> SERVER */
	@Override
	public PacketBase getModePacket() {

		PacketBase payload = super.getModePacket();

		payload.addByte(direction);

		return payload;
	}

	@Override
	protected void handleModePacket(PacketBase payload) {

		super.handleModePacket(payload);

		direction = payload.getByte();
		index += direction;
		setOutput();
		direction = 0;
	}

	/* SERVER -> CLIENT */
	@Override
	public PacketBase getTilePacket() {

		PacketBase payload = super.getTilePacket();

		payload.addBool(augmentNoWater);
		return payload;
	}

	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addFluidStack(hotTank.getFluid());
		payload.addFluidStack(coldTank.getFluid());

		payload.addBool(augmentNoWater);

		return payload;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketBase payload) {

		super.handleTilePacket(payload);

		augmentNoWater = payload.getBool();
		flagNoWater = augmentNoWater;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		hotTank.setFluid(payload.getFluidStack());
		coldTank.setFluid(payload.getFluidStack());

		augmentNoWater = payload.getBool();
		flagNoWater = augmentNoWater;
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		if (world != null && ServerHelper.isServerWorld(world)) {
			flagNoWater = augmentNoWater;
		}
		augmentNoWater = false;
	}

	@Override
	protected void postAugmentInstall() {

		super.postAugmentInstall();

		if (!augmentNoWater && isActive && coldTank.getFluidAmount() < Fluid.BUCKET_VOLUME) {
			processOff();
		}
		if (world != null && ServerHelper.isServerWorld(world) && flagNoWater != augmentNoWater) {
			sendTilePacket(Side.CLIENT);
		}
	}

	@Override
	protected boolean isValidAugment(AugmentType type, String id) {

		if (augmentNoWater && TEProps.MACHINE_EXTRUDER_NO_WATER.equals(id)) {
			return false;
		}
		return super.isValidAugment(type, id);
	}

	@Override
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentNoWater && TEProps.MACHINE_EXTRUDER_NO_WATER.equals(id)) {
			augmentNoWater = true;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* ICustomInventory */
	@Override
	public ItemStack[] getInventorySlots(int inventoryIndex) {

		return outputItem;
	}

	@Override
	public int getSlotStackLimit(int slotIndex) {

		return 64;
	}

	@Override
	public void onSlotUpdate(int slotIndex) {

		markChunkDirty();
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
			return side != facing ? TETextures.MACHINE_SIDE : isActive ? augmentNoWater ? RenderHelper.getFluidTexture(FluidRegistry.LAVA) : TETextures.MACHINE_ACTIVE_EXTRUDER_UNDERLAY : TETextures.MACHINE_FACE[TYPE];
		} else if (side < 6) {
			return side != facing ? TETextures.CONFIG[sideConfig.sideTypes[sideCache[side]]] : isActive ? TETextures.MACHINE_ACTIVE[TYPE] : TETextures.MACHINE_FACE[TYPE];
		}
		return TETextures.MACHINE_SIDE;
	}

	/* RENDERING */
	@Override
	public boolean hasFluidUnderlay() {

		return true;
	}

	@Override
	public FluidStack getRenderFluid() {

		return null; // hasFluidUnderlay returning true and this returning null is special cased for this, to use the sprite name instead of the render fluid hashcode.
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

					FluidTankInfo hotInfo = hotTank.getInfo();
					FluidTankInfo coldInfo = coldTank.getInfo();
					return new IFluidTankProperties[] { new FluidTankProperties(hotInfo.fluid, hotInfo.capacity, true, false), new FluidTankProperties(coldInfo.fluid, coldInfo.capacity, true, false) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (from == null || allowInsertion(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						int ret = hotTank.fill(resource, doFill);
						return ret > 0 ? ret : coldTank.fill(resource, doFill);
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
						FluidStack ret = hotTank.drain(resource, doDrain);
						return ret != null ? ret : coldTank.drain(resource, doDrain);
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
						FluidStack ret = hotTank.drain(maxDrain, doDrain);
						return ret != null ? ret : coldTank.drain(maxDrain, doDrain);
					}
					return null;
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
