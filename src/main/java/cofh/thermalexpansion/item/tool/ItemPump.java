package cofh.thermalexpansion.item.tool;

import cofh.api.item.IMultiModeItem;
import cofh.core.util.KeyBindingMultiMode;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

import org.lwjgl.input.Keyboard;

public class ItemPump extends ItemEnergyContainerBase implements IMultiModeItem {

	IIcon fillIcon;
	IIcon ejectIcon;

	int FILL = 0;
	int EJECT = 1;

	public ItemPump() {

		super("pump");
		setMaxDamage(1);
		setMaxStackSize(1);
		setCreativeTab(ThermalExpansion.tabTools);
		setTextureName("thermalexpansion:tools/Pump");

		energyPerUse = 200;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		super.addInformation(stack, player, list, check);
		list.add(StringHelper.YELLOW + StringHelper.ITALIC + StringHelper.localize("info.cofh.press") + " "
				+ Keyboard.getKeyName(KeyBindingMultiMode.instance.getKey()) + " " + StringHelper.localize("info.cofh.modeChange") + StringHelper.END);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		return true;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

		PlayerInteractEvent event = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, x, y, z, hitSide, world);
		if (MinecraftForge.EVENT_BUS.post(event) || event.getResult() == Result.DENY || event.useBlock == Result.DENY || event.useItem == Result.DENY) {
			return false;
		}
		if (!player.capabilities.isCreativeMode && extractEnergy(stack, energyPerUse, true) != energyPerUse) {
			return false;
		}
		MovingObjectPosition pos = BlockHelper.getCurrentMovingObjectPosition(player, getMode(stack) == FILL);

		if (pos != null) {
			if (ServerHelper.isServerWorld(world)) {
				TileEntity tile = world.getTileEntity(pos.blockX, pos.blockY, pos.blockZ);
				ForgeDirection fd = ForgeDirection.values()[pos.sideHit];
				FluidStack resource;
				boolean success = false;

				if (getMode(stack) == FILL) {
					if (FluidHelper.isFluidHandler(tile)) {
						IFluidHandler handler = (IFluidHandler) tile;
						resource = handler.drain(fd, FluidContainerRegistry.BUCKET_VOLUME, false);
						handler.drain(fd, fillFluidContainerItems(resource, player.inventory, true), true);
						success = true;
					} else {
						resource = FluidHelper.getFluidFromWorld(world, pos.blockX, pos.blockY, pos.blockZ, false);
						if (canFillFluidContainerItems(resource, player.inventory)) {
							fillFluidContainerItems(resource, player.inventory, true);
							world.setBlockToAir(pos.blockX, pos.blockY, pos.blockZ);
							success = true;
						}
					}
				} else if (getMode(stack) == EJECT) {
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
								containerItem.drain(container,
										handler.fill(fd, new FluidStack(containerItem.getFluid(container), FluidContainerRegistry.BUCKET_VOLUME), true), true);
								success = true;
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
	public IIcon getIcon(ItemStack stack, int pass) {

		return getMode(stack) == FILL ? this.fillIcon : this.ejectIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir) {

		this.fillIcon = ir.registerIcon(this.getIconString() + "_Input");
		this.ejectIcon = ir.registerIcon(this.getIconString() + "_Output");
	}

	/* IMultiModeItem */
	@Override
	public int getMode(ItemStack stack) {

		return stack.stackTagCompound == null ? 0 : stack.stackTagCompound.getInteger("Mode");
	}

	@Override
	public boolean setMode(ItemStack stack, int mode) {

		if (stack.stackTagCompound == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.stackTagCompound.setInteger("Mode", mode);
		return false;
	}

	@Override
	public boolean incrMode(ItemStack stack) {

		if (stack.stackTagCompound == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		int curMode = getMode(stack);
		curMode++;
		if (curMode >= getNumModes(stack)) {
			curMode = 0;
		}
		stack.stackTagCompound.setInteger("Mode", curMode);
		return true;
	}

	@Override
	public boolean decrMode(ItemStack stack) {

		if (stack.stackTagCompound == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		int curMode = getMode(stack);
		curMode--;
		if (curMode <= 0) {
			curMode = getNumModes(stack) - 1;
		}
		stack.stackTagCompound.setInteger("Mode", curMode);
		return true;
	}

	@Override
	public int getNumModes(ItemStack stack) {

		return 2;
	}

	@Override
	public void onModeChange(EntityPlayer player, ItemStack stack) {

	}

}
