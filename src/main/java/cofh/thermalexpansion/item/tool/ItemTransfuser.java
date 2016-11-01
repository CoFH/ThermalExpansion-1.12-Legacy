package cofh.thermalexpansion.item.tool;

import codechicken.lib.raytracer.RayTracer;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.item.TEItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemTransfuser extends ItemToolBase {

	//IIcon extractIcon;
	//IIcon transferIcon;

	static final int INPUT = 0;
	static final int OUTPUT = 1;

	static final int TRANSFER = 100000;

	public ItemTransfuser() {

		super("transfuser");
		//setTextureName("thermalexpansion:tool/Transfuser");
	}

	// @Override
	// public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
	//
	// MovingObjectPosition blockPos = BlockHelper.getCurrentMovingObjectPosition(player, true);
	// if (blockPos != null && blockPos.sideHit >= 0) {
	// player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
	// }
	// return stack;
	// }
	//
	// @Override
	// public int getMaxItemUseDuration(ItemStack stack) {
	//
	// return Short.MAX_VALUE;
	// }
	//
	// @Override
	// public EnumAction getItemUseAction(ItemStack stack) {
	//
	// return EnumAction.bow;
	// }
	//
	// @Override
	// public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
	//
	// MovingObjectPosition pos = BlockHelper.getCurrentMovingObjectPosition(player, getMode(stack) == INPUT);
	//
	// if (pos == null || pos.sideHit < 0) {
	// player.setItemInUse(null, 0);
	// } else {
	// player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
	//
	// if (ServerHelper.isClientWorld(player.worldObj)) {
	// return;
	// }
	// TileEntity tile = player.worldObj.getTileEntity(pos.blockX, pos.blockY, pos.blockZ);
	// ForgeDirection fd = ForgeDirection.values()[pos.sideHit];
	// int energy;
	// boolean success = false;
	//
	// if (getMode(stack) == INPUT) {
	// if (EnergyHelper.isEnergyProviderFromSide(tile, fd)) {
	// IEnergyProvider provider = (IEnergyProvider) tile;
	// energy = provider.extractEnergy(fd, TRANSFER, true);
	// provider.extractEnergy(fd, chargeEnergyContainerItems(energy, player.inventory, false), false);
	// success = true;
	// }
	// } else if (getMode(stack) == OUTPUT) {
	// if (EnergyHelper.isEnergyReceiverFromSide(tile, fd)) {
	// IEnergyReceiver receiver = (IEnergyReceiver) tile;
	// ItemStack container = null;
	// container = findTransferContainerItem(player.inventory);
	// if (container != null) {
	// IEnergyContainerItem containerItem = (IEnergyContainerItem) container.getItem();
	// int toSend = containerItem.extractEnergy(container, TRANSFER, true);
	// containerItem.extractEnergy(container, receiver.receiveEnergy(fd, toSend, false), false);
	// success = true;
	// }
	// }
	// }
	// if (success) {
	// player.openContainer.detectAndSendChanges();
	// ((EntityPlayerMP) player).sendContainerAndContentsToPlayer(player.openContainer, player.openContainer.getInventory());
	// }
	// }
	// }
	//
	// @Override
	// public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {
	//
	// return false;
	// }
	//
	// @Override
	// public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {
	//
	// return true;
	// }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		boolean r = doItemUse(stack, world, player, hand);
		if (r) { // HACK: forge is fucking stupid with this method
			ServerHelper.sendItemUsePacket(world, pos, side, hand, hitX, hitY, hitZ);
		}
		return r ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
	}

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		boolean success = doItemUse(stack, world, player, hand);
		return new ActionResult<ItemStack>(success ? EnumActionResult.SUCCESS : EnumActionResult.PASS, stack);
	}

	public boolean doItemUse(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		RayTraceResult traceResult = RayTracer.retrace(player, getMode(stack) == INPUT);

		if (traceResult != null) {
			if (ServerHelper.isServerWorld(world)) {
				TileEntity tile = world.getTileEntity(traceResult.getBlockPos());
				int energy;
				boolean success = false;

				if (getMode(stack) == INPUT) {
					if (EnergyHelper.isEnergyProviderFromSide(tile, traceResult.sideHit)) {
						IEnergyProvider provider = (IEnergyProvider) tile;
						energy = provider.extractEnergy(traceResult.sideHit, TRANSFER, true);

						if (energy <= 0) {
							energy = provider.extractEnergy(null, TRANSFER, true);
							provider.extractEnergy(null, chargeEnergyContainerItems(energy, player.inventory, false), false);
						} else {
							provider.extractEnergy(traceResult.sideHit, chargeEnergyContainerItems(energy, player.inventory, false), false);
						}
						success = true;
					}
				} else if (getMode(stack) == OUTPUT) {
					if (EnergyHelper.isEnergyReceiverFromSide(tile, traceResult.sideHit)) {
						IEnergyReceiver receiver = (IEnergyReceiver) tile;
						ItemStack container = null;
						container = findTransferContainerItem(player.inventory);
						if (container != null) {
							IEnergyContainerItem containerItem = (IEnergyContainerItem) container.getItem();
							int toSend = containerItem.extractEnergy(container, TRANSFER, true);

							if (receiver.receiveEnergy(traceResult.sideHit, toSend, true) > 0) {
								containerItem.extractEnergy(container, receiver.receiveEnergy(traceResult.sideHit, toSend, false), false);
							} else {
								containerItem.extractEnergy(container, receiver.receiveEnergy(null, toSend, false), false);
							}
							success = true;
						}
					}
				}
				if (success) {
					player.openContainer.detectAndSendChanges();
					((EntityPlayerMP) player).updateCraftingInventory(player.openContainer, player.openContainer.getInventory());
					return true;
				}
			}
			player.swingArm(hand);
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

	//@Override
	//public IIcon getIconIndex(ItemStack stack) {
	//	return getIcon(stack, 0);
	//}

	//@Override
	//public IIcon getIcon(ItemStack stack, int pass) {
	//	return getMode(stack) == INPUT ? this.extractIcon : this.transferIcon;
	//}

	//@Override
	//@SideOnly(Side.CLIENT)
	//public void registerIcons(IIconRegister ir) {
	//	this.extractIcon = ir.registerIcon(this.getIconString() + "_Input");
	//	this.transferIcon = ir.registerIcon(this.getIconString() + "_Output");
	//}

	/* IMultiModeItem */
	@Override
	public int getNumModes(ItemStack stack) {

		return 2;
	}

	public void registerModelVariants() {

		ModelResourceLocation location = new ModelResourceLocation(ThermalExpansion.modId + ":tool", "type=transfuserinput");
		ModelLoader.setCustomModelResourceLocation(TEItems.itemTransfuser, 0, location);
	}
}
