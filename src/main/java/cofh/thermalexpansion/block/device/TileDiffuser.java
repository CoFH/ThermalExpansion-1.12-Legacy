package cofh.thermalexpansion.block.device;

import cofh.core.fluid.FluidTankCore;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.util.core.SideConfig;
import cofh.core.util.core.SlotConfig;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.device.BlockDevice.Type;
import cofh.thermalexpansion.gui.client.device.GuiDiffuser;
import cofh.thermalexpansion.gui.container.device.ContainerDiffuser;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.managers.device.DiffuserManager;
import cofh.thermalfoundation.init.TFFluids;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
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
import java.util.Arrays;
import java.util.List;

import static cofh.core.util.core.SideConfig.*;

public class TileDiffuser extends TileDeviceBase implements ITickable {

	private static final int TYPE = Type.DIFFUSER.getMetadata();

	public static void initialize() {

		SIDE_CONFIGS[TYPE] = new SideConfig();
		SIDE_CONFIGS[TYPE].numConfig = 4;
		SIDE_CONFIGS[TYPE].slotGroups = new int[][] { {}, { 0 }, { 0 }, {} };
		SIDE_CONFIGS[TYPE].sideTypes = new int[] { NONE, INPUT_ALL, INPUT_PRIMARY, INPUT_SECONDARY };
		SIDE_CONFIGS[TYPE].defaultSides = new byte[] { 0, 1, 1, 1, 1, 1 };

		SLOT_CONFIGS[TYPE] = new SlotConfig();
		SLOT_CONFIGS[TYPE].allowInsertionSlot = new boolean[] { true };
		SLOT_CONFIGS[TYPE].allowExtractionSlot = new boolean[] { false };

		GameRegistry.registerTileEntity(TileDiffuser.class, "thermalexpansion:device_diffuser");

		config();
	}

	public static void config() {

		String category = "Device.Diffuser";
		BlockDevice.enable[TYPE] = ThermalExpansion.CONFIG.get(category, "Enable", true);

		category = "Device.Diffuser";
		String comment = "If TRUE, the Decoctive Diffuser will display potion effect particles.";
		enableParticles = ThermalExpansion.CONFIG_CLIENT.get(category, "EnableParticles", enableParticles, comment);

		comment = "Adjust this value to change the area effect radius when Potion fluid is used in a Decoctive Diffuser.";
		radiusPotion = ThermalExpansion.CONFIG.getConfiguration().getInt("PotionRadius", category, radiusPotion, 2, 16, comment);

		comment = "Adjust this value to change the area effect radius when Splash Potion fluid is used in a Decoctive Diffuser.";
		radiusSplash = ThermalExpansion.CONFIG.getConfiguration().getInt("SplashPotionRadius", category, radiusSplash, 2, 16, comment);

		comment = "Adjust this value to change the area effect radius when Lingering Potion fluid is used in a Decoctive Diffuser.";
		radiusLingering = ThermalExpansion.CONFIG.getConfiguration().getInt("LingeringPotionRadius", category, radiusLingering, 2, 16, comment);
	}

	public static int radiusPotion = 4;
	public static int radiusSplash = 6;
	public static int radiusLingering = 8;

	private static final int TIME_CONSTANT = 60;
	private static final int BOOST_TIME = 15;
	private static final int FLUID_AMOUNT = 50;

	private static final int MAX_AMPLIFIER = 4;
	private static final int MAX_DURATION = 7200;

	public static boolean enableParticles = true;

	private int inputTracker;

	private int boostAmp;
	private int boostDur;
	private int boostTime;

	private FluidTankCore tank = new FluidTankCore(TEProps.MAX_FLUID_SMALL);
	private FluidStack renderFluid;

	private int offset;
	private boolean forcedCycle;

	public TileDiffuser() {

		super();
		inventory = new ItemStack[1];
		Arrays.fill(inventory, ItemStack.EMPTY);
		createAllSlots(inventory.length);

		offset = MathHelper.RANDOM.nextInt(TIME_CONSTANT);

		hasAutoInput = true;

		enableAutoInput = true;
	}

	@Override
	public int getType() {

		return TYPE;
	}

	@Override
	public boolean hasClientUpdate() {

		return true;
	}

	@Override
	public void onRedstoneUpdate() {

		boolean curActive = isActive;
		isActive = redstoneControlOrDisable();

		if (isActive && !curActive && !forcedCycle) {
			diffuse();
			offset = (int) (TIME_CONSTANT - world.getTotalWorldTime() % TIME_CONSTANT) - 1;
			forcedCycle = true;
		}
		updateIfChanged(curActive);
	}

	@Override
	public void update() {

		if (!timeCheckOffset()) {
			return;
		}
		forcedCycle = false;

		if (ServerHelper.isClientWorld(world)) {
			if (!enableParticles) {
				return;
			}
			if (isActive) {
				diffuseClient();
			}
			return;
		}
		transferInput();
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

	protected void diffuseClient() {

		if (renderFluid == null) {
			return;
		}
		int radius = TFFluids.isSplashPotion(renderFluid) ? radiusSplash : TFFluids.isLingeringPotion(renderFluid) ? radiusLingering : radiusPotion;

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

		if (tank.getFluidAmount() < FLUID_AMOUNT) {
			if (renderFluid != null) {
				renderFluid = null;
				sendFluidPacket();
			}
			return;
		}
		if (boostTime <= 0) {
			boostAmp = 0;
			boostDur = 0;
			if (DiffuserManager.isValidReagent(inventory[0])) {
				boostAmp = DiffuserManager.getReagentAmplifier(inventory[0]);
				boostDur = DiffuserManager.getReagentDuration(inventory[0]);
				boostTime = BOOST_TIME - 1;

				inventory[0].shrink(1);
				if (inventory[0].getCount() <= 0) {
					inventory[0] = ItemStack.EMPTY;
				}
			}
		} else {
			boostTime--;
		}
		FluidStack potionFluid = getTankFluid();

		if (!FluidHelper.isFluidEqual(potionFluid, renderFluid)) {
			renderFluid = new FluidStack(potionFluid, 0);
			sendFluidPacket();
		}
		int radius = TFFluids.isSplashPotion(potionFluid) ? radiusSplash : TFFluids.isLingeringPotion(potionFluid) ? radiusLingering : radiusPotion;

		AxisAlignedBB area = new AxisAlignedBB(pos.add(-radius, 1 - radius, -radius), pos.add(1 + radius, radius, 1 + radius));
		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, area);
		tank.drain(FLUID_AMOUNT, true);

		if (entities.isEmpty()) {
			return;
		}
		List<PotionEffect> effects = PotionUtils.getEffectsFromTag(potionFluid.tag);

		for (EntityLivingBase entity : entities) {
			if (entity.canBeHitWithPotion()) {
				for (PotionEffect effect : effects) {
					Potion potion = effect.getPotion();

					if (potion.isInstant()) {
						potion.affectEntity(null, null, entity, effect.getAmplifier() + boostAmp, 0.5D);
					} else {
						entity.addPotionEffect(new PotionEffect(potion, Math.min((effect.getDuration() / 4) * (1 + boostDur), MAX_DURATION), Math.min(effect.getAmplifier() + boostAmp, MAX_AMPLIFIER), effect.getIsAmbient(), effect.doesShowParticles()));
					}
				}
			}
		}
	}

	protected boolean timeCheckOffset() {

		return (world.getTotalWorldTime() + offset) % TIME_CONSTANT == 0;
	}

	/* GUI METHODS */
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiDiffuser(inventory, this);
	}

	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerDiffuser(inventory, this);
	}

	@Override
	public int getScaledSpeed(int scale) {

		if (!isActive) {
			return 0;
		}
		return MathHelper.round(scale * boostTime / BOOST_TIME);
	}

	@Override
	public FluidTankCore getTank() {

		return tank;
	}

	@Override
	public FluidStack getTankFluid() {

		return tank.getFluid();
	}

	public int getBoostAmp() {

		return boostAmp;
	}

	public int getBoostDur() {

		return boostDur;
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		inputTracker = nbt.getInteger(CoreProps.TRACK_IN);
		tank.readFromNBT(nbt);

		boostAmp = nbt.getInteger("BoostAmp");
		boostDur = nbt.getInteger("BoostDur");
		boostTime = nbt.getInteger("BoostTime");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger(CoreProps.TRACK_IN, inputTracker);
		tank.writeToNBT(nbt);

		nbt.setInteger("BoostAmp", boostAmp);
		nbt.setInteger("BoostDur", boostDur);
		nbt.setInteger("BoostTime", boostTime);

		return nbt;
	}

	/* NETWORK METHODS */

	/* SERVER -> CLIENT */
	@Override
	public PacketBase getFluidPacket() {

		PacketBase payload = super.getFluidPacket();

		payload.addFluidStack(renderFluid);

		return payload;
	}

	@Override
	public PacketBase getGuiPacket() {

		PacketBase payload = super.getGuiPacket();

		payload.addInt(boostAmp);
		payload.addInt(boostDur);
		payload.addInt(boostTime);
		payload.addFluidStack(tank.getFluid());

		return payload;
	}

	@Override
	public PacketBase getTilePacket() {

		PacketBase payload = super.getTilePacket();

		payload.addInt(offset);

		if (tank.getFluid() == null) {
			payload.addFluidStack(renderFluid);
		} else {
			payload.addFluidStack(tank.getFluid());
		}
		return payload;
	}

	@Override
	protected void handleFluidPacket(PacketBase payload) {

		super.handleFluidPacket(payload);

		renderFluid = payload.getFluidStack();

		callBlockUpdate();
	}

	@Override
	protected void handleGuiPacket(PacketBase payload) {

		super.handleGuiPacket(payload);

		boostAmp = payload.getInt();
		boostDur = payload.getInt();
		boostTime = payload.getInt();
		tank.setFluid(payload.getFluidStack());
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void handleTilePacket(PacketBase payload) {

		super.handleTilePacket(payload);

		offset = payload.getInt();
		renderFluid = payload.getFluidStack();
	}

	/* HELPERS */
	protected static boolean isValidPotion(FluidStack stack) {

		return stack != null && (TFFluids.isPotion(stack) || TFFluids.isSplashPotion(stack) || TFFluids.isLingeringPotion(stack));
	}

	/* IInventory */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return DiffuserManager.isValidReagent(stack);
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

					if (isValidPotion(resource) && (from == null || allowInsertion(sideConfig.sideTypes[sideCache[from.ordinal()]]))) {
						return tank.fill(resource, doFill);
					}
					return 0;
				}

				@Nullable
				@Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {

					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return tank.drain(resource, doDrain);
					}
					return null;
				}

				@Nullable
				@Override
				public FluidStack drain(int maxDrain, boolean doDrain) {

					if (from == null || allowExtraction(sideConfig.sideTypes[sideCache[from.ordinal()]])) {
						return tank.drain(maxDrain, doDrain);
					}
					return null;
				}
			});
		}
		return super.getCapability(capability, from);
	}

}
