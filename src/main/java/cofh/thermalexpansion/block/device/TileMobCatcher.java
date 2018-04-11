package cofh.thermalexpansion.block.device;

import cofh.core.network.PacketBase;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.RenderHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.device.BlockDevice.Type;
import cofh.thermalexpansion.gui.client.device.GuiMobCatcher;
import cofh.thermalexpansion.gui.container.device.ContainerMobCatcher;
import cofh.thermalexpansion.init.TEItems;
import cofh.thermalexpansion.init.TETextures;
import cofh.thermalexpansion.item.ItemMorb;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TileMobCatcher extends TileDeviceBase implements ITickable {

	private static final int TYPE = Type.MOB_CATCHER.getMetadata();

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1, 2, 3, 4 }, { 0, 1, 2, 3, 4 }, { 0, 1, 2, 3, 4 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, OUTPUT_ALL, OPEN, OMNI };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, false, false, false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false, true, true, true, true };

		GameRegistry.registerTileEntity(TileMobCatcher.class, "thermalexpansion:device_mob_catcher");

		config();
	}

	public static void config() {

		String category = "Device.MobCatcher";
		BlockDevice.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		String comment = "Adjust this value to change the capture radius for the Creature Encaptulator.";
		radius = ThermalExpansion.CONFIG.getConfiguration().getInt("Radius", category, radius, 2, 16, comment);
	}

	public static int radius = 4;

	private static final byte MODE_ALL = 0;
	private static final byte MODE_HOSTILE = 1;
	private static final byte MODE_FRIENDLY = 2;
	private static final int TIME_CONSTANT = 60;

	private int inputTracker;
	private int outputTracker;

	public byte mode;
	public byte modeFlag;

	private int offset;

	public TileMobCatcher() {

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
	public void update() {

		if (!timeCheckOffset()) {
			return;
		}
		transferOutput();
		transferInput();

		boolean curActive = isActive;

		if (isActive) {
			captureMobs();
			if (!redstoneControlOrDisable()) {
				isActive = false;
			}
		} else if (redstoneControlOrDisable()) {
			isActive = true;
		}
		updateIfChanged(curActive);
	}

	protected void transferInput() {

		if (!getTransferIn()) {
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

		if (!getTransferOut()) {
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

	protected void captureMobs() {

		if (inventory[0].isEmpty() || !inventory[0].getItem().equals(TEItems.itemMorb)) {
			return;
		}
		AxisAlignedBB area = new AxisAlignedBB(pos.add(-radius, 1 - radius, -radius), pos.add(1 + radius, radius, 1 + radius));
		List<EntityLiving> mobs = world.getEntitiesWithinAABB(EntityLiving.class, area, EntitySelectors.IS_STANDALONE);
		mobs.removeIf(mob -> mob instanceof EntityTameable && ((EntityTameable) mob).isTamed());

		if (mode == MODE_HOSTILE) {
			mobs.removeIf(mob -> !(mob instanceof IMob));
		} else if (mode == MODE_FRIENDLY) {
			mobs.removeIf(mob -> mob instanceof IMob);
		}
		if (mobs.isEmpty()) {
			return;
		}
		int type = ItemHelper.getItemDamage(inventory[0]);

		for (int i = 1; i < 5; i++) {
			if (inventory[i].isEmpty()) {
				capture(mobs, i, type);
			}
			if (inventory[0].isEmpty()) {
				break;
			}
		}
	}

	protected void capture(List<EntityLiving> mobs, int slot, int type) {

		for (Iterator<EntityLiving> iterator = mobs.iterator(); iterator.hasNext(); ) {
			EntityLiving mob = iterator.next();
			if (ItemMorb.validMobs.contains(EntityList.getKey(mob).toString())) {
				inventory[slot] = ItemMorb.getMorb(type, mob.serializeNBT());
				inventory[0].grow(-1);

				BlockPos mobPos = mob.getPosition();
				((WorldServer) world).spawnParticle(EnumParticleTypes.CLOUD, mobPos.getX() + 0.5, mobPos.getY() + 0.2, mobPos.getZ() + 0.5, 2, 0, 0, 0, 0.0, 0);
				mob.setDead();

				iterator.remove();
				return;
			}
		}
	}

	protected boolean timeCheckOffset() {

		return (world.getTotalWorldTime() + offset) % TIME_CONSTANT == 0;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiMobCatcher(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerMobCatcher(inventory, this);
	}

	public void toggleMode() {

		mode++;
		if (mode > 2) {
			mode = 0;
		}
		sendModePacket();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger("TrackIn");
		outputTracker = nbt.getInteger("TrackOut");
		mode = nbt.getByte("Mode");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut", outputTracker);
		nbt.setByte("Mode", mode);

		return nbt;
	}

	/* CLIENT -> SERVER */
	@Override
	public PacketBase getModePacket() {

		PacketBase payload = super.getModePacket();

		payload.addByte(mode);

		return payload;
	}

	@Override
	protected void handleModePacket(PacketBase payload) {

		super.handleModePacket(payload);

		mode = payload.getByte();
		modeFlag = mode;

		callNeighborTileChange();
	}

	/* SERVER -> CLIENT */
	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addByte(mode);

		return payload;
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		mode = payload.getByte();
		modeFlag = mode;
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return stack.getItem().equals(TEItems.itemMorb);
	}

	/* ISidedTexture */
	@Override
	@SideOnly (Side.CLIENT)
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
