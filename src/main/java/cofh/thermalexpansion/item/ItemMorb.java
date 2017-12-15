package cofh.thermalexpansion.item;

import cofh.core.init.CoreProps;
import cofh.core.item.ItemCore;
import cofh.core.render.IModelRegister;
import cofh.core.util.ConfigHandler;
import cofh.core.util.CoreUtils;
import cofh.core.util.DefaultedHashMap;
import cofh.core.util.core.IInitializer;
import cofh.core.util.helpers.ServerHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.entity.projectile.EntityMorb;
import cofh.thermalexpansion.init.TEItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemMorb extends ItemCore implements IInitializer, IModelRegister, IItemColor {

    public ItemMorb() {

        super("thermalexpansion");

        setUnlocalizedName("morb");
        setCreativeTab(ThermalExpansion.tabMorbs);
    }

    @Override
    public Item setUnlocalizedName(String name) {

        ForgeRegistries.ITEMS.register(setRegistryName(name));
        this.name = name;
        name = modName + "." + name;
        return super.setUnlocalizedName(name);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {

        String entityName = "info.cofh.empty";
        String openParen = " (";
        String closeParen = StringHelper.END + ")";

        if (stack.getTagCompound() != null) {
            String entityId = stack.getTagCompound().getString("id");
            EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityId));
            if(entry != null) {
                entityName = "entity." + entry.getName() +".name";
            }
        }
        return super.getItemStackDisplayName(stack) + openParen + StringHelper.localize(entityName) + closeParen;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

        if (isInCreativeTab(tab)) {
            items.add(new ItemStack(this));

            for (int i = 0; i < morbList.size(); i++) {
                items.add(morbList.get(i));
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

        ItemStack stack = player.getHeldItem(hand);
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }

        world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F));

        NBTTagCompound tag = new NBTTagCompound();
        if(stack.hasTagCompound()) {
            tag = stack.getTagCompound();
        }

        if (ServerHelper.isServerWorld(world)) {
            EntityMorb morb = new EntityMorb(world, player, tag);
            morb.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
            world.spawnEntity(morb);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    public static void parseMorbs() {

        if(!ItemMorb.enable) {
            return;
        }

        List<String> list = Arrays.asList(blacklist);

        for(ResourceLocation name : EntityList.getEntityNameList()) {
            Class<? extends Entity> clazz = EntityList.getClass(name);

            if(clazz == null || !EntityLiving.class.isAssignableFrom(clazz)) {
                continue;
            }

            if(list.contains(name.toString())) {
                continue;
            }

            addMorb(name.toString());
        }
        CONFIG_MORBS.cleanUp(false, true);
    }

    /* HELPERS */
    public static void addMorb(String entityId) {

        ItemStack stack = new ItemStack(TEItems.itemMorb);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("id", entityId);
        stack.setTagCompound(tag);

        morbList.add(stack);
        morbMap.put(entityId, stack);
    }

    public static void dropMorb(NBTTagCompound entityData, World world, BlockPos pos) {

        ItemStack stack = new ItemStack(TEItems.itemMorb);
        if(entityData != null) {
            stack.setTagCompound(entityData);
        }
        CoreUtils.dropItemStackIntoWorldWithVelocity(stack, world, pos);
    }

    /* IItemColor */
    @Override
    public int colorMultiplier(ItemStack stack, int tintIndex) {

        EntityList.EntityEggInfo info = null;

        if(stack.hasTagCompound()) {
            ResourceLocation id = new ResourceLocation(stack.getTagCompound().getString("id"));
            info = EntityList.ENTITY_EGGS.get(id);
        }

        if(info != null) {
            switch(tintIndex) {
                case 0: return info.primaryColor;
                case 1: return info.secondaryColor;
            }
        }

        return 0xFFFFFF;
    }

    /* IModelRegister */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels() {

        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    /* IInitializer */
    @Override
    public boolean initialize() {

        config();

        ThermalExpansion.proxy.addIModelRegister(this);

        return true;
    }

    @Override
    public boolean register() {

        if (!enable) {
            return false;
        }

        return true;
    }

    private static void config() {

        CONFIG_MORBS.setConfiguration(new Configuration(new File(CoreProps.configDir, "cofh/" + ThermalExpansion.MOD_ID + "/morbs.cfg"), true));

        String category = "General";
        String comment = "If TRUE, the recipe for Morbs is enabled.";
        enable = CONFIG_MORBS.getConfiguration().getBoolean("EnableRecipe", category, enable, comment);

        category = "Blacklist";
        comment = "List of entities that are not allowed to be captured.";
        blacklist = CONFIG_MORBS.getConfiguration().getStringList("Blacklist", category, blacklist, comment);
    }

    public static final ConfigHandler CONFIG_MORBS = new ConfigHandler(ThermalExpansion.VERSION);

    public static ArrayList<ItemStack> morbList = new ArrayList<>();
    public static Map<String, ItemStack> morbMap = new DefaultedHashMap<String, ItemStack>(ItemStack.EMPTY);

    public static String[] blacklist = new String[] { "minecraft:wither", "minecraft:ender_dragon" };
    public static boolean enable = true;
}
