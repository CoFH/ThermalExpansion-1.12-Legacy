package cofh.thermalexpansion.item.tool;

import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

public class ItemPump extends ItemEnergyContainerBase {

	IIcon fillIcon;
	IIcon drainIcon;

	static final int INPUT = 0;
	static final int OUTPUT = 1;

	public ItemPump() {

		super("pump");
		setTextureName("thermalexpansion:tool/Pump");

		energyPerUse = 200;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		return true;
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

		if (!player.capabilities.isCreativeMode && extractEnergy(stack, energyPerUse, true) != energyPerUse) {
			return false;
		}
		MovingObjectPosition pos = BlockHelper.getCurrentMovingObjectPosition(player, getMode(stack) == INPUT);

		if (pos != null) {
			if (ServerHelper.isServerWorld(world)) {
				TileEntity tile = world.getTileEntity(pos.blockX, pos.blockY, pos.blockZ);
				ForgeDirection fd = ForgeDirection.values()[pos.sideHit];
				FluidStack resource;
				boolean success = false;

				if (getMode(stack) == INPUT) {
					if (FluidHelper.isFluidHandler(tile)) {
						IFluidHandler handler = (IFluidHandler) tile;
						resource = handler.drain(fd, FluidContainerRegistry.BUCKET_VOLUME, false);

						if (resource == null) {
							resource = handler.drain(ForgeDirection.UNKNOWN, FluidContainerRegistry.BUCKET_VOLUME, false);
							handler.drain(ForgeDirection.UNKNOWN, fillFluidContainerItems(resource, player.inventory, true), true);
						} else {
							handler.drain(fd, fillFluidContainerItems(resource, player.inventory, true), true);
						}
						success = true;
					} else {
						resource = FluidHelper.getFluidFromWorld(world, pos.blockX, pos.blockY, pos.blockZ, false);
						if (canFillFluidContainerItems(resource, player.inventory)) {
							fillFluidContainerItems(resource, player.inventory, true);
							world.setBlockToAir(pos.blockX, pos.blockY, pos.blockZ);
							success = true;
						}
					}
				} else if (getMode(stack) == OUTPUT) {
					if (FluidHelper.isFluidHandler(tile)) {
						IFluidHandler handler = (IFluidHandler) tile;
						FluidTankInfo[] tankInfo = handler.getTankInfo(fd);

						if (tankInfo != null) {
							ItemStack container = null;
							for (int i = 0; i < tankInfo.length; i++) {
								resource = tankInfo[i].fluid;
								container = findDrainContainerItem(resource, FluidContainerRegistry.BUCKET_VOLUME, player.inventory);
								if (container != null) {
									break;
								}
							}
							if (container != null) {
								IFluidContainerItem containerItem = (IFluidContainerItem) container.getItem();
								FluidStack fillStack = new FluidStack(containerItem.getFluid(container), FluidContainerRegistry.BUCKET_VOLUME);

								if (handler.fill(fd, fillStack, false) > 0) {
									containerItem.drain(container, handler.fill(fd, fillStack, true), true);
								} else {
									containerItem.drain(container, handler.fill(ForgeDirection.UNKNOWN, fillStack, true), true);
								}
								success = true;
							}
						} else {
							tankInfo = handler.getTankInfo(ForgeDirection.UNKNOWN);
							if (tankInfo != null) {
								ItemStack container = null;
								for (int i = 0; i < tankInfo.length; i++) {
									resource = tankInfo[i].fluid;
									container = findDrainContainerItem(resource, FluidContainerRegistry.BUCKET_VOLUME, player.inventory);
									if (container != null) {
										break;
									}
								}
								if (container != null) {
									IFluidContainerItem containerItem = (IFluidContainerItem) container.getItem();
									containerItem.drain(container, handler.fill(ForgeDirection.UNKNOWN, new FluidStack(containerItem.getFluid(container),
											FluidContainerRegistry.BUCKET_VOLUME), true), true);
									success = true;
								}
							}
						}
					} else {
						Block block = null;
						ItemStack container = null;
						container = findDrainContainerItem(null, FluidContainerRegistry.BUCKET_VOLUME, player.inventory);

						if (container != null) {
							IFluidContainerItem containerItem = (IFluidContainerItem) container.getItem();
							Fluid fluid = containerItem.getFluid(container).getFluid();
							block = fluid.getBlock();

							if (fluid.getName() == "water") {
								block = Blocks.flowing_water;
							} else if (fluid.getName() == "lava") {
								block = Blocks.flowing_lava;
							}
							if (block != null) {
								int[] coords = BlockHelper.getAdjacentCoordinatesForSide(pos);
								Block worldBlock = world.getBlock(coords[0], coords[1], coords[2]);

								if (world.getBlockMetadata(coords[0], coords[1], coords[2]) == 0 && worldBlock.getMaterial() instanceof MaterialLiquid) {
									// do not replace source blocks
								} else {
									if (world.isAirBlock(coords[0], coords[1], coords[2]) || worldBlock.getMaterial().isReplaceable()
											|| worldBlock == Blocks.snow_layer) {
										world.setBlock(coords[0], coords[1], coords[2], block, 0, 3);
										world.markBlockForUpdate(coords[0], coords[1], coords[2]);
										containerItem.drain(container, FluidContainerRegistry.BUCKET_VOLUME, true);
										success = true;
									}
								}
							}
						}
					}
				}
				if (success) {
					player.openContainer.detectAndSendChanges();
					((EntityPlayerMP) player).sendContainerAndContentsToPlayer(player.openContainer, player.openContainer.getInventory());

					if (!player.capabilities.isCreativeMode) {
						extractEnergy(stack, energyPerUse, false);
					}
					return true;
				}
			}
			player.swingItem();
		}
		return false;
	}

	protected ItemStack findDrainContainerItem(FluidStack fluid, int amount, IInventory inventory) {

		ItemStack retStack = null;

		if (fluid == null) {
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				if (FluidHelper.isFluidContainerItem(inventory.getStackInSlot(i))) {
					IFluidContainerItem containerItem = (IFluidContainerItem) inventory.getStackInSlot(i).getItem();
					FluidStack containerFluid = containerItem.drain(inventory.getStackInSlot(i), amount, false);
					if (containerFluid != null && containerFluid.amount >= amount) {
						retStack = inventory.getStackInSlot(i);
						break;
					}
				}
			}
		} else {
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				if (FluidHelper.isFluidContainerItem(inventory.getStackInSlot(i))) {
					IFluidContainerItem containerItem = (IFluidContainerItem) inventory.getStackInSlot(i).getItem();
					FluidStack containerFluid = containerItem.drain(inventory.getStackInSlot(i), amount, false);
					if (containerFluid != null && FluidHelper.isFluidEqual(fluid, containerFluid) && containerFluid.amount >= amount) {
						retStack = inventory.getStackInSlot(i);
						break;
					}
				}
			}
		}
		return retStack;
	}

	protected boolean canFillFluidContainerItems(FluidStack resource, IInventory inventory) {

		if (resource == null) {
			return false;
		}
		return fillFluidContainerItems(resource, inventory, false) >= resource.amount;
	}

	protected int fillFluidContainerItems(FluidStack resource, IInventory inventory, boolean doFill) {

		if (resource == null) {
			return 0;
		}
		int amount = resource.amount;

		for (int i = 0; i < inventory.getSizeInventory() && resource.amount > 0; i++) {
			resource.amount -= FluidHelper.fillFluidContainerItem(inventory.getStackInSlot(i), resource, doFill);
		}
		int filled = amount - resource.amount;
		resource.amount = amount;
		return filled;
	}

	@Override
	public IIcon getIconIndex(ItemStack stack) {

		return getIcon(stack, 0);
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {

		return getMode(stack) == INPUT ? this.fillIcon : this.drainIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir) {

		this.fillIcon = ir.registerIcon(this.getIconString() + "_Input");
		this.drainIcon = ir.registerIcon(this.getIconString() + "_Output");
	}

	/* IMultiModeItem */
	@Override
	public int getNumModes(ItemStack stack) {

		return 2;
	}

}
