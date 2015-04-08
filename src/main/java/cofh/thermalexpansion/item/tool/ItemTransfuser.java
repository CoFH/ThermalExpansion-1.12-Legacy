package cofh.thermalexpansion.item.tool;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemTransfuser extends ItemToolBase {

	IIcon extractIcon;
	IIcon transferIcon;

	static final int INPUT = 0;
	static final int OUTPUT = 1;

	static final int TRANSFER = 100000;

	public ItemTransfuser() {

		super("transfuser");
		setMaxDamage(1);
		setMaxStackSize(1);
		setCreativeTab(ThermalExpansion.tabTools);
		setTextureName("thermalexpansion:tools/Transfuser");
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		return false;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		boolean r = doItemUse(stack, world, player);
		if (r) { // HACK: forge is fucking stupid with this method
			ServerHelper.sendItemUsePacket(stack, player, world, x, y, z, hitSide, hitX, hitY, hitZ);
		}
		return r;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

		doItemUse(stack, world, player);
		return stack;
	}

	public boolean doItemUse(ItemStack stack, World world, EntityPlayer player) {

		MovingObjectPosition pos = BlockHelper.getCurrentMovingObjectPosition(player, getMode(stack) == INPUT);

		if (pos != null) {
			if (ServerHelper.isServerWorld(world)) {
				TileEntity tile = world.getTileEntity(pos.blockX, pos.blockY, pos.blockZ);
				ForgeDirection fd = ForgeDirection.values()[pos.sideHit];
				int energy;
				boolean success = false;

				if (getMode(stack) == INPUT) {
					if (EnergyHelper.isEnergyProviderFromSide(tile, fd)) {
						IEnergyProvider provider = (IEnergyProvider) tile;
						energy = provider.extractEnergy(fd, TRANSFER, true);
						provider.extractEnergy(fd, chargeEnergyContainerItems(energy, player.inventory, false), false);
						success = true;
					}
				} else if (getMode(stack) == OUTPUT) {
					if (EnergyHelper.isEnergyReceiverFromSide(tile, fd)) {
						IEnergyReceiver receiver = (IEnergyReceiver) tile;
						ItemStack container = null;
						container = findTransferContainerItem(player.inventory);
						if (container != null) {
							IEnergyContainerItem containerItem = (IEnergyContainerItem) container.getItem();
							int toSend = containerItem.extractEnergy(container, TRANSFER, true);
							containerItem.extractEnergy(container, receiver.receiveEnergy(fd, toSend, false), false);
							success = true;
						}
					}
				}
				if (success) {
					player.openContainer.detectAndSendChanges();
					((EntityPlayerMP) player).sendContainerAndContentsToPlayer(player.openContainer, player.openContainer.getInventory());
					return true;
				}
			}
			player.swingItem();
		}
		return false;
	}

	protected ItemStack findTransferContainerItem(IInventory inventory) {

		ItemStack retStack = null;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (EnergyHelper.isEnergyContainerItem(inventory.getStackInSlot(i))) {
				IEnergyContainerItem containerItem = (IEnergyContainerItem) inventory.getStackInSlot(i).getItem();
				int energy = containerItem.extractEnergy(inventory.getStackInSlot(i), TRANSFER, true);
				if (energy > 0) {
					retStack = inventory.getStackInSlot(i);
					break;
				}
			}
		}
		return retStack;
	}

	protected int chargeEnergyContainerItems(int energy, IInventory inventory, boolean simulate) {

		if (energy <= 0) {
			return 0;
		}
		int amount = energy;

		for (int i = 0; i < inventory.getSizeInventory() && energy > 0; i++) {
			energy -= EnergyHelper.insertEnergyIntoContainer(inventory.getStackInSlot(i), energy, simulate);
		}
		int charged = amount - energy;
		return charged;
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {

		return getMode(stack) == INPUT ? this.extractIcon : this.transferIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir) {

		this.extractIcon = ir.registerIcon(this.getIconString() + "_Input");
		this.transferIcon = ir.registerIcon(this.getIconString() + "_Output");
	}

	/* IMultiModeItem */
	@Override
	public int getNumModes(ItemStack stack) {

		return 2;
	}

}
