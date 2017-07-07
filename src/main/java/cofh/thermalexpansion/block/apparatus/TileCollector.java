package cofh.thermalexpansion.block.apparatus;

import cofh.api.tileentity.IInventoryConnection;
import cofh.core.util.RegistrySocial;
import cofh.core.util.helpers.SecurityHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.apparatus.BlockApparatus.Type;
import cofh.thermalexpansion.gui.client.apparatus.GuiCollector;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TileCollector extends TileApparatusBase implements IInventoryConnection, ITickable {

	private static final int TYPE = Type.COLLECTOR.getMetadata();
	public static final float[] DEFAULT_DROP_CHANCES = new float[] { 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F };

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 2;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, {} };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { 0, 4 };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 0, 0, 0, 0, 0, 0 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] {};
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] {};

		GameRegistry.registerTileEntity(TileCollector.class, "thermalexpansion:apparatus_collector");

		// config();
	}

	public static void config() {

		String category = "Apparatus.Collector";
		BlockApparatus.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}

	private boolean ignoreTeam = true;
	private boolean ignoreFriends = true;
	private boolean ignoreOwner = true;

	/* AUGMENTS */
	protected boolean augmentEntityCollection;

	public TileCollector() {

		super();
		inventory = new ItemStack[1];
		Arrays.fill(inventory, ItemStack.EMPTY);

		radius = 1;
		depth = 1;
	}

	@Override
	public int getType() {

		return TYPE;
	}

	protected void activate() {

		collectItemsInArea();
	}

	private void collectItemsInArea() {

		AxisAlignedBB area;
		switch (facing) {
			case 0:
				area = new AxisAlignedBB(pos.add(-radius, -1 - depth, -radius), pos.add(1 + radius, 0, 1 + radius));
				break;
			case 1:
				area = new AxisAlignedBB(pos.add(-radius, 1, -radius), pos.add(1 + radius, 2 + depth, 1 + radius));
				break;
			case 2:
				area = new AxisAlignedBB(pos.add(-radius, -radius, -1 - depth), pos.add(1 + radius, 1 + radius, 0));
				break;
			case 3:
				area = new AxisAlignedBB(pos.add(-radius, -radius, 1), pos.add(1 + radius, 1 + radius, 2 + depth));
				break;
			case 4:
				area = new AxisAlignedBB(pos.add(-1 - depth, -radius, -radius), pos.add(0, 1 + radius, 1 + radius));
				break;
			default:
				area = new AxisAlignedBB(pos.add(1, -radius, -radius), pos.add(2 + depth, 1 + radius, 1 + radius));
				break;
		}
		List<EntityItem> entityItems = world.getEntitiesWithinAABB(EntityItem.class, area);
		for (EntityItem entityItem : entityItems) {
			if (entityItem.isDead || entityItem.getItem().getCount() <= 0) {
				continue;
			}
			stuffedItems.add(entityItem.getItem());
			entityItem.world.removeEntity(entityItem);
		}
		if (augmentEntityCollection) {
			List<EntityLivingBase> entityLiving = world.getEntitiesWithinAABB(EntityLivingBase.class, area);
			for (EntityLivingBase entity : entityLiving) {
				float[] dropChances = DEFAULT_DROP_CHANCES;
				if (entity instanceof EntityLiving) {
					EntityLiving living = ((EntityLiving) entity);
					dropChances = new float[] { living.inventoryHandsDropChances[0], living.inventoryHandsDropChances[1], living.inventoryArmorDropChances[0], living.inventoryArmorDropChances[1], living.inventoryArmorDropChances[2], living.inventoryArmorDropChances[3] };
				} else if (isSecured() && entity instanceof EntityPlayer) {
					if (doNotCollectItemsFrom((EntityPlayer) entity)) {
						continue;
					}
				}
				for (int i = 0; i < 6; i++) {
					EntityEquipmentSlot slot = EntityEquipmentSlot.values()[i];
					ItemStack equipmentInSlot = entity.getItemStackFromSlot(slot);
					if (!equipmentInSlot.isEmpty() && dropChances[i] >= 1.0F) {
						stuffedItems.add(equipmentInSlot);
						entity.setItemStackToSlot(slot, ItemStack.EMPTY);
					}
				}
			}
		}
	}

	private boolean doNotCollectItemsFrom(EntityPlayer player) {

		String name = player.getName();

		UUID ownerID = owner.getId();
		UUID otherID = SecurityHelper.getID(player);
		if (ownerID.equals(otherID)) {
			return ignoreOwner;
		}
		return ignoreFriends && RegistrySocial.playerHasAccess(name, owner);
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiCollector(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
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

	/* IInventoryConnection */
	@Override
	public ConnectionType canConnectInventory(EnumFacing from) {

		if (from != null && from.ordinal() != facing && sideCache[from.ordinal()] == 1) {
			return ConnectionType.FORCE;
		} else {
			return ConnectionType.DEFAULT;
		}
	}

}
