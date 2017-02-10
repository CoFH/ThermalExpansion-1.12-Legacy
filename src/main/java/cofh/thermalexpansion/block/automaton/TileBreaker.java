package cofh.thermalexpansion.block.automaton;

import cofh.api.tileentity.IInventoryConnection;
import cofh.core.entity.CoFHFakePlayer;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.gui.client.automaton.GuiBreaker;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileBreaker extends TileAutomatonBase implements IInventoryConnection, ITickable {

	private static final int TYPE = BlockAutomaton.Type.BREAKER.getMetadata();

	public static void initialize() {

		defaultSideConfig[TYPE] = new SideConfig();
		defaultSideConfig[TYPE].numConfig = 2;
		defaultSideConfig[TYPE].slotGroups = new int[][] { {}, {} };
		defaultSideConfig[TYPE].allowInsertionSide = new boolean[] { false, false };
		defaultSideConfig[TYPE].allowExtractionSide = new boolean[] { false, false };
		defaultSideConfig[TYPE].allowInsertionSlot = new boolean[] {};
		defaultSideConfig[TYPE].allowExtractionSlot = new boolean[] {};
		defaultSideConfig[TYPE].sideTex = new int[] { 0, 4 };
		defaultSideConfig[TYPE].defaultSides = new byte[] { 0, 0, 0, 0, 0, 0 };

		GameRegistry.registerTileEntity(TileBreaker.class, "thermalexpansion:automaton_breaker");

		config();
	}

	public static void config() {

		String category = "Automaton.Breaker";
		BlockAutomaton.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}

	/* AUGMENTS */
	protected boolean augmentFluid;

	public TileBreaker() {

		super();
		inventory = new ItemStack[1];

		radius = 0;
		depth = 0;
	}

	@Override
	public int getType() {

		return TYPE;
	}

	protected void activate() {

		breakBlocksInArea();
	}

	private void breakBlocksInArea() {

		Iterable<BlockPos> area;

		switch (facing) {
			case 0:
				area = BlockPos.getAllInBox(pos.add(-radius, -1 - depth, -radius), pos.add(radius, -1, radius));
				break;
			case 1:
				area = BlockPos.getAllInBox(pos.add(-radius, 1, -radius), pos.add(radius, 1 + depth, radius));
				break;
			case 2:
				area = BlockPos.getAllInBox(pos.add(-radius, -radius, -1 - depth), pos.add(radius, radius, -1));
				break;
			case 3:
				area = BlockPos.getAllInBox(pos.add(-radius, -radius, 1), pos.add(radius, radius, 1 + depth));
				break;
			case 4:
				area = BlockPos.getAllInBox(pos.add(-1 - depth, -radius, -radius), pos.add(-1, radius, radius));
				break;
			default:
				area = BlockPos.getAllInBox(pos.add(1, -radius, -radius), pos.add(1 + depth, radius, radius));
				break;
		}
		for (BlockPos target : area) {
			if (augmentFluid) {
				FluidStack stack = augmentFluid ? FluidHelper.getFluidFromWorld(worldObj, target, true) : null;
				if (stack != null) {
					for (int i = 0; i < 6 && stack.amount > 0; i++) {
						if (sideCache[i] == 1) {
							stack.amount -= FluidHelper.insertFluidIntoAdjacentFluidHandler(this, EnumFacing.VALUES[i], stack, true);
						}
					}
					worldObj.setBlockToAir(target);
					continue;
				}
			}
			if (CoFHFakePlayer.isBlockBreakable(fakePlayer, worldObj, target)) {
				IBlockState state = worldObj.getBlockState(target);
				stuffedItems.addAll(BlockHelper.breakBlock(worldObj, fakePlayer, target, state, 0, true, false));
			}
		}
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiBreaker(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
	}

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return super.hasCapability(capability, from) || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(EmptyFluidHandler.INSTANCE);
		}
		return super.getCapability(capability, facing);
	}

}
