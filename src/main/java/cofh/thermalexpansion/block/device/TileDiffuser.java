package cofh.thermalexpansion.block.device;

import cofh.core.fluid.FluidTankCore;
import cofh.core.gui.GuiContainerCore;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.device.BlockDevice.Type;
import cofh.thermalexpansion.gui.client.device.GuiDiffuser;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class TileDiffuser extends TileDeviceBase implements ITickable {

	private static final int TYPE = Type.DIFFUSER.getMetadata();
	public static int fluidAmount = 50;

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 2;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, {} };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { 0, 1 };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 0, 1, 1, 1, 1, 1 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] {};
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] {};

		LIGHT_VALUES[TYPE] = 5;

		GameRegistry.registerTileEntity(TileDiffuser.class, "thermalexpansion:device_diffuser");

		config();
	}

	public static void config() {

		String category = "Device.Diffuser";
		BlockDevice.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);
	}

	private static final int TIME_CONSTANT = 60;
	private static final int RADIUS_POTION = 3;
	private static final int RADIUS_SPLASH = 4;
	private static final int RADIUS_LINGERING = 5;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	FluidStack renderFluid;

	private int offset;

	public TileDiffuser() {

		super();

		offset = MathHelper.RANDOM.nextInt(TIME_CONSTANT);
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public void update() {

		if (ServerHelper.isClientWorld(world)) {

			if (!timeCheckOffset()) {
				return;
			}
			if (isActive) {
				diffuseClient();
			}
			return;
		}
		if (!timeCheckOffset()) {
			return;
		}
		boolean curActive = isActive;

		if (isActive) {
			diffuse();

			if (!redstoneControlOrDisable()) {
				isActive = false;
			}
		} else if (redstoneControlOrDisable()) {
			isActive = true;
		}
		updateIfChanged(curActive);
	}

	protected void diffuseClient() {

		if (renderFluid == null) {
			return;
		}
		int radius = isSplashPotion(renderFluid) ? RADIUS_SPLASH : isLingeringPotion(renderFluid) ? RADIUS_LINGERING : RADIUS_POTION;

		List<PotionEffect> effects = PotionUtils.getEffectsFromTag(renderFluid.tag);
		int color = PotionUtils.getPotionColorFromEffectList(effects);

		int x = pos.getX();
		float y = pos.getY() + 0.5F;
		int z = pos.getZ();

		for (int i = x - radius; i <= x + radius; i++) {
			for (int k = z - radius; k <= z + radius; k++) {
				world.spawnAlwaysVisibleParticle(EnumParticleTypes.SPELL_MOB.getParticleID(), i + world.rand.nextFloat(), y, k + world.rand.nextFloat(), (color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F);
			}
		}
	}

	protected void diffuse() {

		if (tank.getFluidAmount() < fluidAmount) {
			return;
		}
		FluidStack potionFluid = getTankFluid();

		if (!FluidHelper.isFluidEqual(potionFluid, renderFluid)) {
			renderFluid = new FluidStack(potionFluid, 0);
			sendFluidPacket();
		}
		int radius = isSplashPotion(potionFluid) ? RADIUS_SPLASH : isLingeringPotion(potionFluid) ? RADIUS_LINGERING : RADIUS_POTION;

		AxisAlignedBB area = new AxisAlignedBB(pos.add(-radius, 1 - radius, -radius), pos.add(1 + radius, radius, 1 + radius));
		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, area);
		tank.drain(fluidAmount, true);

		if (entities.isEmpty()) {
			return;
		}
		List<PotionEffect> effects = PotionUtils.getEffectsFromTag(potionFluid.tag);

		for (EntityLivingBase entity : entities) {
			if (entity.canBeHitWithPotion()) {
				for (PotionEffect effect : effects) {
					Potion potion = effect.getPotion();

					if (potion.isInstant()) {
						potion.affectEntity(null, null, entity, effect.getAmplifier(), 0.5D);
					} else {
						entity.addPotionEffect(new PotionEffect(potion, effect.getDuration() / 4, effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles()));
					}
				}
			}
		}
	}

	protected boolean timeCheckOffset() {

		return (world.getTotalWorldTime() + offset) % TIME_CONSTANT == 0;
	}

	protected static boolean isSplashPotion(FluidStack stack) {

		return stack != null && stack.getFluid() == TFFluids.fluidPotionSplash;
	}

	protected static boolean isLingeringPotion(FluidStack stack) {

		return stack != null && stack.getFluid() == TFFluids.fluidPotionLingering;
	}

	protected static boolean isValidPotion(FluidStack stack) {

		return stack != null && (stack.getFluid() == TFFluids.fluidPotion || stack.getFluid() == TFFluids.fluidPotionSplash || stack.getFluid() == TFFluids.fluidPotionLingering);
	}

	/* GUI METHODS */
	@Override
	public int getScaledSpeed(int scale) {

		return isActive ? GuiContainerCore.SPEED : 0;
	}

	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiDiffuser(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerTEBase(inventory, this);
	}

	@Override
	public FluidTankCore getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		tank.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		tank.writeToNBT(nbt);
		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketCoFHBase getFluidPacket() {

		PacketCoFHBase payload = super.getFluidPacket();

		payload.addFluidStack(renderFluid);

		return payload;
	}

	@Override
	public PacketCoFHBase getGuiPacket() {

		PacketCoFHBase payload = super.getGuiPacket();

		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	public PacketCoFHBase getTilePacket() {

		PacketCoFHBase payload = super.getTilePacket();

		payload.addInt(offset);

		if (tank.getFluid() == null) {
			payload.addFluidStack(renderFluid);
		} else {
			payload.addFluidStack(tank.getFluid());
		}
		return payload;
	}

	@Override
	protected void handleFluidPacket(PacketCoFHBase payload) {

		super.handleFluidPacket(payload);

		renderFluid = payload.getFluidStack();

		callBlockUpdate();
	}

	@Override
	protected void handleGuiPacket(PacketCoFHBase payload) {

		super.handleGuiPacket(payload);

		tank.setFluid(payload.getFluidStack());
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketCoFHBase payload) {

		super.handleTilePacket(payload);

		offset = payload.getInt();
		renderFluid = payload.getFluidStack();
	}

	/* CAPABILITIES */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return super.hasCapability(capability, from) || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new IFluidHandler() {

				@Override
				public IFluidTankProperties[] getTankProperties() {

					FluidTankInfo info = tank.getInfo();
					return new IFluidTankProperties[] { new FluidTankProperties(info.fluid, info.capacity, true, true) };
				}

				@Override
				public int fill(FluidStack resource, boolean doFill) {

					if (from != null && !allowInsertion(sideConfig.sideTypes[sideCache[from.ordinal()]]) || !isValidPotion(resource)) {
						return 0;
					}
					return tank.fill(resource, doFill);
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (from != null && !allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return null;
					}
					return tank.drain(resource, doDrain);
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (from != null && !allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return null;
					}
					return tank.drain(maxDrain, doDrain);
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
