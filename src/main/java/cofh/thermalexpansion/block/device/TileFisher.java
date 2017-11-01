package cofh.thermalexpansion.block.device;

import cofh.core.network.PacketCoFHBase;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.device.BlockDevice.Type;
import cofh.thermalexpansion.gui.client.device.GuiFisher;
import cofh.thermalexpansion.gui.container.device.ContainerFisher;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.util.managers.FisherManager;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Arrays;

public class TileFisher extends TileDeviceBase implements ITickable {

	private static final int TYPE = Type.FISHER.getMetadata();

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1, 2, 3, 4 }, { 0, 1, 2, 3, 4 }, { 0, 1, 2, 3, 4 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { 0, 1, 4, 7, 8 };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, false, false, false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false, true, true, true, true };

		GameRegistry.registerTileEntity(TileFisher.class, "thermalexpansion:device_fisher");

		config();
	}

	public static void config() {

		String category = "Device.Fisher";
		BlockDevice.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}

	private static final int TARGET_WATER[] = { 10, 20, 30 };
	private static final int TIME_CONSTANT = 7200;
	private static final int BOOST_TIME = 8;

	private int targetWater = -1;
	private int timeConstant = TIME_CONSTANT;
	private boolean isOcean;
	private boolean isRiver;
	private boolean isRaining;

	private int inputTracker;
	private int outputTracker;

	private int boostMult;
	private int boostTime;

	private int offset;

	public TileFisher() {

		super();

		inventory = new ItemStack[5];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);

		offset = MathHelper.RANDOM.nextInt(TIME_CONSTANT);

		hasAutoInput = true;
		hasAutoOutput = true;

		enableAutoInput = true;
		enableAutoOutput = true;
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void blockPlaced() {

		super.blockPlaced();

		if (redstoneControlOrDisable() && targetWater >= TARGET_WATER[0]) {
			isActive = true;
			sendTilePacket(Side.CLIENT);
		}
	}

	@Override
	public void onNeighborBlockChange() {

		super.onNeighborBlockChange();
		updateValidity();
	}

	@Override
	public void update() {

		if (!timeCheckOffset()) {
			return;
		}
		transferInput();

		boolean curActive = isActive;
		updateValidity();

		if (isActive) {
			if (targetWater >= TARGET_WATER[0]) {
				if (boostTime > 0) {
					for (int i = 0; i < boostMult; i++) {
						catchFish();
					}
					boostTime--;
				} else {
					boostMult = FisherManager.getBaitMultiplier(inventory[0]);
					if (boostMult > 0) {
						for (int i = 0; i < boostMult; i++) {
							catchFish();
						}
						boostTime = BOOST_TIME - 1;
						inventory[0].shrink(1);
						if (inventory[0].getCount() <= 0) {
							inventory[0] = ItemStack.EMPTY;
						}
					} else {
						catchFish();
					}
				}
			}
			if (targetWater < TARGET_WATER[0] || !redstoneControlOrDisable()) {
				isActive = false;
			}
		} else if (targetWater >= TARGET_WATER[0] && redstoneControlOrDisable()) {
			isActive = true;
		}
		transferOutput();

		updateIfChanged(curActive);
	}

	protected void updateValidity() {

		if (ServerHelper.isClientWorld(world)) {
			return;
		}
		int adjacentSources = 0;
		targetWater = 0;

		if (isWater(world.getBlockState(pos.down()))) {
			adjacentSources++;
		}
		if (isWater(world.getBlockState(pos.west()))) {
			adjacentSources++;
		}
		if (isWater(world.getBlockState(pos.east()))) {
			adjacentSources++;
		}
		if (isWater(world.getBlockState(pos.north()))) {
			adjacentSources++;
		}
		if (isWater(world.getBlockState(pos.south()))) {
			adjacentSources++;
		}
		if (adjacentSources < 2) {
			return;
		}
		Iterable<BlockPos> area = BlockPos.getAllInBox(pos.add(-2, -1, -2), pos.add(2, 0, 2));

		for (BlockPos query : area) {
			if (isWater(world.getBlockState(query))) {
				targetWater++;
			}
		}
		isOcean = BiomeDictionary.hasType(world.getBiome(pos), BiomeDictionary.Type.OCEAN);
		isRiver = BiomeDictionary.hasType(world.getBiome(pos), BiomeDictionary.Type.RIVER);
		isRaining = world.isRainingAt(pos);
		timeConstant = getTimeConstant();
	}

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

	protected void transferOutput() {

		if (!enableAutoOutput) {
			return;
		}
		int side;
		boolean foundOutput = false;
		for (int i = outputTracker + 1; i <= outputTracker + 6; i++) {
			side = i % 6;
			if (isPrimaryOutput(sideConfig.sideTypes[sideCache[side]])) {
				for (int j = 1; j < 5; j++) {
					if (transferItem(j, ITEM_TRANSFER[level], EnumFacing.VALUES[side])) {
						foundOutput = true;
					}
				}
				if (foundOutput) {
					outputTracker = side;
					break;
				}
			}
		}
	}

	protected void catchFish() {

		ItemStack fish = FisherManager.getFish();
		for (int j = 1; j < 5; j++) {
			if (inventory[j].isEmpty()) {
				inventory[j] = ItemHelper.cloneStack(fish);
				break;
			} else if (inventory[j].getCount() < inventory[j].getMaxStackSize() && ItemHelper.itemsIdentical(inventory[j], fish)) {
				inventory[j].grow(1);
				break;
			}
		}
	}

	protected static boolean isWater(IBlockState state) {

		return (state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.FLOWING_WATER) && state.getValue(BlockLiquid.LEVEL) == 0;
	}

	protected boolean timeCheckOffset() {

		return (world.getTotalWorldTime() + offset) % timeConstant == 0;
	}

	protected int getTimeConstant() {

		int constant = TIME_CONSTANT;

		if (isOcean) {
			constant /= 3;
		} else if (isRiver) {
			constant /= 2;
		}
		if (targetWater >= TARGET_WATER[2]) {
			return constant / (isRaining ? 4 : 3);
		} else if (targetWater >= TARGET_WATER[1]) {
			return constant / (isRaining ? 3 : 2);
		}
		return constant / (isRaining ? 2 : 1);
	}

	public int getBoostMult() {

		return boostMult;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiFisher(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerFisher(inventory, this);
	}

	@Override
	public int getScaledSpeed(int scale) {

		if (!isActive) {
			return 0;
		}
		return MathHelper.round(scale * boostTime / BOOST_TIME);
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		targetWater = nbt.getInteger("Water");

		inputTracker = nbt.getInteger("TrackIn");
		outputTracker = nbt.getInteger("TrackOut");

		boostMult = nbt.getInteger("BoostMult");
		boostTime = nbt.getInteger("BoostTime");

		timeConstant = nbt.getInteger("TimeConstant");

		if (timeConstant <= 0) {
			timeConstant = TIME_CONSTANT;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("Water", targetWater);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut", outputTracker);

		nbt.setInteger("BoostMult", boostMult);
		nbt.setInteger("BoostTime", boostTime);

		nbt.setInteger("TimeConstant", timeConstant);

		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addInt(boostTime);
		payload.addInt(boostMult);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		boostTime = payload.getInt();
		boostMult = payload.getInt();
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return FisherManager.isValidBait(stack);
	}

	/* ISidedTexture */
	@Override
	public TextureAtlasSprite getTexture(int side, int pass) {

		if (pass == 0) {
			if (side == 0) {
				return TETextures.DEVICE_BOTTOM;
			} else if (side == 1) {
				return TETextures.DEVICE_TOP;
			}
			return side != facing ? TETextures.DEVICE_SIDE : isActive ? RenderHelper.getFluidTexture(FluidRegistry.WATER) : TETextures.DEVICE_FACE[TYPE];
		} else if (side < 6) {
			return side != facing ? TETextures.CONFIG[sideConfig.sideTypes[sideCache[side]]] : isActive ? TETextures.DEVICE_ACTIVE[TYPE] : TETextures.DEVICE_FACE[TYPE];
		}
		return TETextures.DEVICE_SIDE;
	}

}
