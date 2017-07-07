package cofh.thermalexpansion.block.machine;

import cofh.core.fluid.FluidTankCore;
import cofh.core.gui.container.ICustomInventory;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.helpers.AugmentHelper;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.machine.BlockMachine.Type;
import cofh.thermalexpansion.gui.client.machine.GuiExtruder;
import cofh.thermalexpansion.gui.container.machine.ContainerExtruder;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
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

	public static ItemStack ANDESITE;
	public static ItemStack DIORITE;
	public static ItemStack GRANITE;

	public static void initialize() {

		processItems = new ItemStack[3];

		processItems[0] = new ItemStack(Blocks.COBBLESTONE);
		processItems[1] = new ItemStack(Blocks.STONE);
		processItems[2] = new ItemStack(Blocks.OBSIDIAN);

		GRANITE = new ItemStack(Blocks.STONE, 1, 1);
		DIORITE = new ItemStack(Blocks.STONE, 1, 3);
		ANDESITE = new ItemStack(Blocks.STONE, 1, 5);

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, {}, { 0 }, { 0 }, { 0 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { 0, 1, 4, 7, 8 };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { true, false };

		VALID_AUGMENTS[TYPE] = new HashSet<>();
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_EXTRUDER_NO_WATER);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_EXTRUDER_ANDESITE);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_EXTRUDER_DIORITE);
		VALID_AUGMENTS[TYPE].add(TEProps.MACHINE_EXTRUDER_GRANITE);

		LIGHT_VALUES[TYPE] = 14;

		GameRegistry.registerTileEntity(TileExtruder.class, "thermalexpansion:machine_extruder");

		config();
	}

	public static void config() {

		String category = "Machine.Extruder";
		BlockMachine.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		ENERGY_CONFIGS[TYPE] = new EnergyConfig();
		ENERGY_CONFIGS[TYPE].setDefaultParams(basePower);
	}

	private static int[] processLava = { 0, 0, 1000 };
	private static int[] processWater = { 0, 1000, 1000 };
	private static int[] processEnergy = { 400, 800, 1600 };
	private static ItemStack[] processItems;

	private int outputTracker;
	private byte curSelection;
	private byte prevSelection;

	private ItemStack[] outputItems = new ItemStack[3];
	private FluidTankCore hotTank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	private FluidTankCore coldTank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);

	/* AUGMENTS */
	protected boolean augmentNoWater;
	protected boolean augmentAndesite;
	protected boolean augmentDiorite;
	protected boolean augmentGranite;

	protected boolean flagNoWater;

	public TileExtruder() {

		super();
		inventory = new ItemStack[1 + 1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);

		for (int i = 0; i < 3; i++) {
			outputItems[i] = processItems[i].copy();
		}
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
		if (!inventory[0].isItemEqual(outputItems[curSelection])) {
			return false;
		}
		return inventory[0].getCount() + outputItems[curSelection].getCount() <= outputItems[prevSelection].getMaxStackSize();
	}

	@Override
	protected boolean canFinish() {

		return processRem <= 0;
	}

	@Override
	protected void processStart() {

		processMax = processEnergy[curSelection] * energyMod / ENERGY_BASE;
		processRem = processMax;
		prevSelection = curSelection;
	}

	@Override
	protected void processFinish() {

		if (inventory[0].isEmpty()) {
			inventory[0] = outputItems[prevSelection].copy();
		} else {
			inventory[0].grow(outputItems[prevSelection].getCount());
		}
		hotTank.drain(processLava[prevSelection], true);

		if (!augmentNoWater) {
			coldTank.drain(processWater[prevSelection], true);
		}
		prevSelection = curSelection;
	}

	@Override
	protected void transferOutput() {

		if (!enableAutoOutput) {
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
		if (tag.hasKey("Sel")) {
			curSelection = tag.getByte("Sel");
			if (!isActive) {
				prevSelection = curSelection;
			}
		}
		return true;
	}

	@Override
	protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag) {

		if (!super.writePortableTagInternal(player, tag)) {
			return false;
		}
		tag.setByte("Sel", curSelection);
		return true;
	}

	@Override
	protected void setLevelFlags() {

		super.setLevelFlags();

		hasAutoInput = false;
		enableAutoInput = false;
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

	public int getCurSelection() {

		return curSelection;
	}

	public int getPrevSelection() {

		return prevSelection;
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

	public void setMode(int selection) {

		byte lastSelection = curSelection;
		curSelection = (byte) selection;
		sendModePacket();
		curSelection = lastSelection;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		outputTracker = nbt.getInteger("TrackOut");
		prevSelection = nbt.getByte("Prev");
		curSelection = nbt.getByte("Sel");

		hotTank.readFromNBT(nbt.getCompoundTag("HotTank"));
		coldTank.readFromNBT(nbt.getCompoundTag("ColdTank"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackOut", outputTracker);
		nbt.setByte("Prev", prevSelection);
		nbt.setByte("Sel", curSelection);

		nbt.setTag("HotTank", hotTank.writeToNBT(new NBTTagCompound()));
		nbt.setTag("ColdTank", coldTank.writeToNBT(new NBTTagCompound()));
		return nbt;
	}

	/* NETWORK METHODS */

	/* CLIENT -> SERVER */
	@Override
	public PacketCoFHBase getModePacket() {

		PacketCoFHBase payload = super.getModePacket();

		payload.addByte(curSelection);

		return payload;
	}

	@Override
	protected void handleModePacket(PacketCoFHBase payload) {

		super.handleModePacket(payload);

		curSelection = payload.getByte();

		if (!isActive) {
			prevSelection = curSelection;
		}
		callNeighborTileChange();
	}

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getTilePacket() {

		PacketCoFHBase payload = super.getTilePacket();

		payload.addBool(augmentNoWater);
		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addByte(curSelection);
		payload.addByte(prevSelection);
		payload.addInt(hotTank.getFluidAmount());
		payload.addInt(coldTank.getFluidAmount());

		payload.addBool(augmentNoWater);

		return payload;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketCoFHBase payload) {

		super.handleTilePacket(payload);

		augmentNoWater = payload.getBool();
		flagNoWater = augmentNoWater;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		curSelection = payload.getByte();
		prevSelection = payload.getByte();
		hotTank.getFluid().amount = payload.getInt();
		coldTank.getFluid().amount = payload.getInt();

		augmentNoWater = payload.getBool();
		flagNoWater = augmentNoWater;
	}

	/* HELPERS */
	@Override
	protected void preAugmentInstall() {

		super.preAugmentInstall();

		outputItems[1] = processItems[1].copy();

		if (world != null && ServerHelper.isServerWorld(world)) {
			flagNoWater = augmentNoWater;
		}
		augmentNoWater = false;
		augmentGranite = false;
		augmentDiorite = false;
		augmentAndesite = false;
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
	protected boolean installAugmentToSlot(int slot) {

		String id = AugmentHelper.getAugmentIdentifier(augments[slot]);

		if (!augmentNoWater && TEProps.MACHINE_EXTRUDER_NO_WATER.equals(id)) {
			augmentNoWater = true;
			return true;
		}
		if (!augmentGranite && TEProps.MACHINE_EXTRUDER_GRANITE.equals(id)) {
			outputItems[1] = GRANITE.copy();
			augmentGranite = true;
			hasModeAugment = true;
			return true;
		}
		if (!augmentDiorite && TEProps.MACHINE_EXTRUDER_DIORITE.equals(id)) {
			outputItems[1] = DIORITE.copy();
			augmentDiorite = true;
			hasModeAugment = true;
			return true;
		}
		if (!augmentAndesite && TEProps.MACHINE_EXTRUDER_ANDESITE.equals(id)) {
			outputItems[1] = ANDESITE.copy();
			augmentAndesite = true;
			hasModeAugment = true;
			return true;
		}
		return super.installAugmentToSlot(slot);
	}

	/* ICustomInventory */
	@Override
	public ItemStack[] getInventorySlots(int inventoryIndex) {

		return outputItems;
	}

	@Override
	public int getSlotStackLimit(int slotIndex) {

		return 64;
	}

	@Override
	public void onSlotUpdate() {

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

					if (from != null && !allowInsertion(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return 0;
					}
					if (resource.getFluid() == FluidRegistry.LAVA) {
						return hotTank.fill(resource, doFill);
					} else if (resource.getFluid() == FluidRegistry.WATER) {
						return coldTank.fill(resource, doFill);
					}
					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					return null;
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
