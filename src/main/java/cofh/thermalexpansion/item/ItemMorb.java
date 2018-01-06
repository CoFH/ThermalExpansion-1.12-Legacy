package cofh.thermalexpansion.item;

import cofh.core.init.CoreProps;
import cofh.core.item.ItemMulti;
import cofh.core.render.IModelRegister;
import cofh.core.util.ConfigHandler;
import cofh.core.util.CoreUtils;
import cofh.core.util.core.IInitializer;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.entity.projectile.EntityMorb;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.BehaviorMorbDispense;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.set.hash.THashSet;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

import static cofh.core.util.helpers.RecipeHelper.addShapelessRecipe;

public class ItemMorb extends ItemMulti implements IInitializer, IModelRegister {

	public static ItemStack setTag(ItemStack container, String entityId) {

		if (entityId != null && !entityId.isEmpty()) {
			container.setTagCompound(new NBTTagCompound());
			container.getTagCompound().setString("id", entityId);
			container.getTagCompound().setBoolean(GENERIC, true);
		}
		return container;
	}

	public ItemMorb() {

		super("thermalexpansion");

		setUnlocalizedName("morb");
		setCreativeTab(ThermalExpansion.tabMorbs);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			tooltip.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		tooltip.add(StringHelper.localize("info.thermalexpansion.morb.a.0"));

		if (ItemHelper.getItemDamage(stack) > 0) {
			tooltip.add(StringHelper.getNoticeText("info.thermalexpansion.morb.a.1"));
		}
		if (stack.getTagCompound() == null) {
			tooltip.add(StringHelper.localize("info.thermalexpansion.morb.b.0"));
		} else {
			if (stack.getTagCompound().hasKey(GENERIC)) {
				tooltip.add(StringHelper.getNoticeText("info.thermalexpansion.morb.b.2"));
			}
			tooltip.add(StringHelper.localize("info.thermalexpansion.morb.b.1"));
		}
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (TEProps.creativeTabHideMorbs) {
			return;
		}
		if (isInCreativeTab(tab)) {
			items.add(new ItemStack(this, 1, 0));
			items.add(new ItemStack(this, 1, 1));
			items.addAll(morbList);
		}
	}

	// TODO: Remove eventually.
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {

		if (stack.getItemDamage() > 1) {
			stack.setItemDamage(1);
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {

		String entityName = "info.cofh.empty";
		String openParen = " (";
		String closeParen = StringHelper.END + ")";

		if (stack.getTagCompound() != null) {
			String entityId = stack.getTagCompound().getString("id");
			EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityId));
			if (entry != null) {
				entityName = "entity." + entry.getName() + ".name";
			}
		}
		return super.getItemStackDisplayName(stack) + openParen + StringHelper.localize(entityName) + closeParen;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		ItemStack stack = player.getHeldItem(hand);

		// TODO: Remove eventually.
		if (stack.getItemDamage() > 1) {
			stack.setItemDamage(1);
		}
		NBTTagCompound nbt = new NBTTagCompound();
		if (stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		if (!player.capabilities.isCreativeMode) {
			stack.shrink(1);
		}
		world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F));

		if (ServerHelper.isServerWorld(world)) {
			EntityMorb morb = new EntityMorb(world, player, (byte) ItemHelper.getItemDamage(stack), nbt);
			morb.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
			world.spawnEntity(morb);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	/* HELPERS */
	public static void addMorb(ItemStack morb, String entityId) {

		setTag(morb, entityId);
		morbList.add(morb);
		validMobs.add(entityId);
	}

	public static void dropMorb(int type, NBTTagCompound nbt, World world, BlockPos pos) {

		ItemStack stack = type == 0 ? morbStandard.copy() : morbReusable.copy();

		if (nbt != null && validMobs.contains(nbt.getString("id"))) {
			if (nbt.hasKey(GENERIC)) {
				stack = setTag(stack, nbt.getString("id"));
			} else {
				nbt.removeTag("Pos");
				nbt.removeTag("Motion");
				nbt.removeTag("Rotation");
				nbt.removeTag("FallDistance");
				nbt.removeTag("Fire");
				nbt.removeTag("Air");
				nbt.removeTag("OnGround");
				nbt.removeTag("Dimension");
				// nbt.removeTag("Invulnerable");
				nbt.removeTag("PortalCooldown");
				nbt.removeTag("UUIDMost");
				nbt.removeTag("UUIDLeast");

				nbt.removeTag("Leashed");
				nbt.removeTag("Leash");

				stack.setTagCompound(nbt);
			}
		}
		CoreUtils.dropItemStackIntoWorldWithVelocity(stack, world, pos);
	}

	public static void parseMorbs() {

		List<String> list = Arrays.asList(blacklist);

		for (ResourceLocation name : EntityList.getEntityNameList()) {
			Class<? extends Entity> clazz = EntityList.getClass(name);
			if (clazz == null || !EntityLiving.class.isAssignableFrom(clazz)) {
				continue;
			}
			if (list.contains(name.toString()) || !EntityList.ENTITY_EGGS.containsKey(name)) {
				continue;
			}
			addMorb(ItemHelper.cloneStack(morbStandard), name.toString());
		}
		CONFIG_MORBS.cleanUp(false, true);
	}

	public static ItemStack getGenericMorb(ItemStack stack) {

		NBTTagCompound nbt = stack.getTagCompound();

		if (nbt != null && validMobs.contains(nbt.getString("id"))) {
			return setTag(stack, nbt.getString("id"));
		}
		return ItemStack.EMPTY;
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		ModelLoader.setCustomMeshDefinition(this, stack -> new ModelResourceLocation(getRegistryName(), String.format("full=%s,type=%s", stack.getTagCompound() != null && stack.getTagCompound().hasKey("id") ? 1 : 0, itemMap.get(ItemHelper.getItemDamage(stack)).name)));

		for (Map.Entry<Integer, ItemEntry> entry : itemMap.entrySet()) {
			for (int i = 0; i < 2; i++) {
				ModelBakery.registerItemVariants(this, new ModelResourceLocation(getRegistryName(), String.format("full=%s,type=%s", i, entry.getValue().name)));
			}
		}
	}

	/* IItemColor */
	public int colorMultiplier(ItemStack stack, int tintIndex) {

		EntityList.EntityEggInfo info = null;

		if (stack.hasTagCompound()) {
			ResourceLocation id = new ResourceLocation(stack.getTagCompound().getString("id"));
			info = EntityList.ENTITY_EGGS.get(id);
		}
		if (info != null) {
			switch (tintIndex) {
				case 1:
					return info.primaryColor;
				case 2:
					return info.secondaryColor;
			}
		}
		return 0xFFFFFF;
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		config();

		morbStandard = addItem(0, "standard");
		morbReusable = addItem(1, "reusable", EnumRarity.UNCOMMON);

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, new BehaviorMorbDispense());

		ThermalExpansion.proxy.addIModelRegister(this);

		return true;
	}

	@Override
	public boolean register() {

		if (!enable) {
			return false;
		}
		ItemStack morbStack = ItemHelper.cloneStack(morbStandard, 8);

		addShapelessRecipe(morbStack, Blocks.SOUL_SAND, "crystalSlag", "slimeball", "enderpearl");
		addShapelessRecipe(morbReusable, morbStandard, "nuggetSignalum", "nuggetSignalum", "nuggetSignalum");

		addShapelessRecipe(morbStack, Blocks.SOUL_SAND, "crystalSlag", ItemMaterial.globRosin, "enderpearl");

		return true;
	}

	private static void config() {

		CONFIG_MORBS.setConfiguration(new Configuration(new File(CoreProps.configDir, "cofh/" + ThermalExpansion.MOD_ID + "/morbs.cfg"), true));

		String category = "General";
		String comment = "If TRUE, the recipes for Morbs are enabled. Setting this to FALSE means that you actively dislike fun things and/or Pokemon tributes.";
		enable = CONFIG_MORBS.getConfiguration().getBoolean("EnableRecipe", category, enable, comment);

		category = "Blacklist";
		comment = "List of entities that are not allowed to be placed in Morbs. Mobs without spawn eggs are automatically disallowed.";
		blacklist = CONFIG_MORBS.getConfiguration().getStringList("Blacklist", category, blacklist, comment);
	}

	public static final ConfigHandler CONFIG_MORBS = new ConfigHandler(ThermalExpansion.VERSION);

	public static ArrayList<ItemStack> morbList = new ArrayList<>();
	public static Set<String> validMobs = new THashSet<>();

	public static String[] blacklist = new String[] {};
	public static boolean enable = true;

	public static final String GENERIC = "Generic";
	public static final int REUSE_CHANCE = 25;

	/* REFERENCES */
	public static ItemStack morbStandard;
	public static ItemStack morbReusable;

}
