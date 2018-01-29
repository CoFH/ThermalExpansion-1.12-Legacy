package cofh.thermalexpansion.block.device;

import java.util.Arrays;
import java.util.List;

import cofh.core.util.helpers.MathHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.device.BlockDevice.Type;
import cofh.thermalexpansion.gui.client.device.GuiCatcher;
import cofh.thermalexpansion.gui.container.device.ContainerCatcher;
import cofh.thermalexpansion.init.TEItems;
import cofh.thermalexpansion.item.ItemMorb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
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
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileCatcher extends TileDeviceBase implements ITickable{

	private static final int TYPE = Type.CATCHER.getMetadata();
	
	
	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 5;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 1, 2, 3, 4 }, { 0, 1, 2, 3, 4 }, { 0, 1, 2, 3, 4 } };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, INPUT_PRIMARY, INPUT_SECONDARY, OPEN };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 0, 1, 1, 1, 1, 1 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true, false, false, false, false };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false, true, true, true, true };

		LIGHT_VALUES[TYPE] = 5;

		GameRegistry.registerTileEntity(TileCatcher.class, "thermalexpansion:device_catcher");

		config();
	}
	
	public static void config() {

		String category = "Device.Catcher";
		BlockDevice.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}
	
	private static final int TIME_CONSTANT = 20;
	private static final int CATCH_RADIUS = 5;
	
	private int inputTracker;
	private int outputTracker;
	
	private int offset;
	
	public TileCatcher() {
		super();
		inventory = new ItemStack[1+4];
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
			System.out.println("active");
			catchMobs();

			if (!redstoneControlOrDisable()) {
				isActive = false;
			}
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
	
	protected void catchMobs() {
		
		if(inventory[0].isEmpty())
			return;
		for(int i = 1; i<5; i++) {
			if(inventory[i].getCount()<inventory[i].getMaxStackSize() && (inventory[i].isEmpty() || inventory[0].getMetadata()==inventory[i].getMetadata()))
				doWork();
		}
	}
	
	protected void doWork() {
		AxisAlignedBB area = new AxisAlignedBB(pos.add(-CATCH_RADIUS, -2, -CATCH_RADIUS), pos.add(1 + CATCH_RADIUS, 1 + 2, 1 + CATCH_RADIUS));
		List<EntityLiving> mobs = world.getEntitiesWithinAABB(EntityLiving.class, area, EntitySelectors.IS_ALIVE);
		if(mobs.isEmpty())
			return;
		for(EntityLiving mob : mobs) {
			if(!ItemMorb.validMobs.contains(EntityList.getKey(mob).toString())) {
				mobs.remove(mob);
			}
		}
		if(mobs.isEmpty())
			return;
		ItemStack stack = ItemMorb.fillMorb(inventory[0].getMetadata(),mobs.get(0).serializeNBT());
		for(int i = 1; i<5; i++) {
			Entity currentMob = mobs.get(0);
			boolean spaceFound = false;
			if(inventory[i].isItemEqual(stack)) {
				inventory[i].grow(1);
				spaceFound = true;
			} else if(inventory[i].isEmpty()) {
				inventory[i] = stack.copy();
				spaceFound = true;
			}
			if(spaceFound) {
				inventory[0].grow(-1);
				BlockPos entityPos = currentMob.getPosition();
				currentMob.setDead();
				mobs.remove(currentMob);
				((WorldServer) world).spawnParticle(EnumParticleTypes.CLOUD, entityPos.getX() + 0.5, entityPos.getY() + 0.2, entityPos.getZ() + 0.5, 2, 0, 0, 0, 0.0, 0);
				break;
			}			
		}
	}
	
	protected boolean timeCheckOffset() {

		return (world.getTotalWorldTime() + offset) % TIME_CONSTANT == 0;
	}
	
	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiCatcher(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerCatcher(inventory, this);
	}
	
	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger("TrackIn");
		outputTracker = nbt.getInteger("TrackOut");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("TrackIn", inputTracker);
		nbt.setInteger("TrackOut", outputTracker);

		return nbt;
	}
	
	/* NETWORK METHODS */
	
	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return stack.getItem().equals(TEItems.itemMorb) && (stack.getTagCompound()==null || !stack.getTagCompound().hasKey("id"));
	}
	
	/* ISidedTexture */
}
