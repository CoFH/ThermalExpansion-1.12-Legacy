package cofh.thermalexpansion.item.tool;

import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.util.BlockUtils;

import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ServerHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;


import net.minecraft.block.Block;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

public class ItemPump extends ItemEnergyContainerBase {

	//IIcon fillIcon;
	//IIcon drainIcon;

	static final int INPUT = 0;
	public static final int OUTPUT = 1;

	public ItemPump() {

		super("pump");
		//setTextureName("thermalexpansion:tool/Pump");

		energyPerUse = 200;
	}

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return EnumActionResult.SUCCESS;
    }

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

		if (!player.capabilities.isCreativeMode && extractEnergy(stack, energyPerUse, true) != energyPerUse) {
			return false;
		}
		RayTraceResult traceResult = RayTracer.retrace(player, getMode(stack) == INPUT);

		if (traceResult != null && world.isBlockModifiable(player, traceResult.getBlockPos())) {
			TileEntity tile = world.getTileEntity(traceResult.getBlockPos());
			FluidStack resource;
			boolean success = false;

			if (getMode(stack) == INPUT && player.canPlayerEdit(traceResult.getBlockPos(), traceResult.sideHit, stack)) {
				if (FluidHelper.isFluidHandler(tile)) {
					if (ServerHelper.isServerWorld(world)) {
						IFluidHandler handler = (IFluidHandler) tile;
						resource = handler.drain(traceResult.sideHit, FluidContainerRegistry.BUCKET_VOLUME, false);

						if (resource == null) {
							resource = handler.drain(null, FluidContainerRegistry.BUCKET_VOLUME, false);
							handler.drain(null, fillFluidContainerItems(resource, player.inventory, true), true);
						} else {
							handler.drain(traceResult.sideHit, fillFluidContainerItems(resource, player.inventory, true), true);
						}
					}
					success = true;
				} else {
					resource = FluidHelper.getFluidFromWorld(world, traceResult.getBlockPos(), false);
					if (canFillFluidContainerItems(resource, player.inventory)) {
						if (ServerHelper.isServerWorld(world)) {
							fillFluidContainerItems(resource, player.inventory, true);
							world.setBlockToAir(traceResult.getBlockPos());
						}
						success = true;
					}
				}
			} else if (getMode(stack) == OUTPUT) {
				if (FluidHelper.isFluidHandler(tile)) {
					IFluidHandler handler = (IFluidHandler) tile;
					FluidTankInfo[] tankInfo = handler.getTankInfo(traceResult.sideHit);

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
							if (ServerHelper.isServerWorld(world)) {
								IFluidContainerItem containerItem = (IFluidContainerItem) container.getItem();
								FluidStack fillStack = new FluidStack(containerItem.getFluid(container), FluidContainerRegistry.BUCKET_VOLUME);

								if (handler.fill(traceResult.sideHit, fillStack, false) > 0) {
									containerItem.drain(container, handler.fill(traceResult.sideHit, fillStack, true), true);
								} else {
									containerItem.drain(container, handler.fill(null, fillStack, true), true);
								}
							}
							success = true;
						}
					} else {
						tankInfo = handler.getTankInfo(null);
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
								if (ServerHelper.isServerWorld(world)) {
									IFluidContainerItem containerItem = (IFluidContainerItem) container.getItem();
									containerItem.drain(container, handler.fill(null, new FluidStack(containerItem.getFluid(container),
											FluidContainerRegistry.BUCKET_VOLUME), true), true);
								}
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

						if (fluid.getName().equals("water")) {
							block = Blocks.FLOWING_WATER;
						} else if (fluid.getName().equals("lava")) {
							block = Blocks.FLOWING_LAVA;
						}
						if (block != null) {
                            BlockPos offsetPos = traceResult.getBlockPos().offset(traceResult.sideHit);
							IBlockState worldState = world.getBlockState(offsetPos);

							if (worldState.getBlock().getMetaFromState(worldState) == 0 && worldState.getMaterial() instanceof MaterialLiquid) {
								// do not replace source blocks
							} else {
								if (world.isAirBlock(offsetPos) || worldState.getMaterial().isReplaceable()
										|| worldState.getBlock() == Blocks.SNOW_LAYER) {
									if (ServerHelper.isServerWorld(world)) {
										world.setBlockState(offsetPos, block.getDefaultState(), 3);
                                        BlockUtils.fireBlockUpdate(world, offsetPos);
										containerItem.drain(container, FluidContainerRegistry.BUCKET_VOLUME, true);
									}
									success = true;
								}
							}
						}
					}
				}
			}
			if (success) {
				player.swingArm(hand);
				if (ServerHelper.isServerWorld(world)) {
					player.openContainer.detectAndSendChanges();
					((EntityPlayerMP) player).updateCraftingInventory(player.openContainer, player.openContainer.getInventory());

					if (!player.capabilities.isCreativeMode) {
						extractEnergy(stack, energyPerUse, false);
					}
				}
				return true;
			}
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
	public boolean canItemEditBlocks() {

		return true;
	}

	//@Override
	//public IIcon getIconIndex(ItemStack stack) {
	//	return getIcon(stack, 0);
	//}

	//@Override
	//public IIcon getIcon(ItemStack stack, int pass) {
	//	return getMode(stack) == INPUT ? this.fillIcon : this.drainIcon;
	//}

	//@Override
	//@SideOnly(Side.CLIENT)
	//public void registerIcons(IIconRegister ir) {
	//	this.fillIcon = ir.registerIcon(this.getIconString() + "_Input");
	//	this.drainIcon = ir.registerIcon(this.getIconString() + "_Output");
	//}

	/* IMultiModeItem */
	@Override
	public int getNumModes(ItemStack stack) {

		return 2;
	}

}
