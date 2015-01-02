package thermalexpansion.plugins.tcon;

import cpw.mods.fml.common.event.FMLInterModComms;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

public class TConPlugin {

	public static void preInit() {

	}

	public static void initialize() {

		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("FluidName", "pyrotheum");
		tag.setInteger("Temperature", 4000);
		tag.setInteger("Duration", 100);
		FMLInterModComms.sendMessage("TConstruct", "addSmelteryFuel", tag);

		tag = new NBTTagCompound();
		tag.setInteger("Id", 1020);
		tag.setString("Name", "Invar");
		tag.setInteger("Durability", 450);
		tag.setInteger("MiningSpeed", 700);
		tag.setInteger("HarvestLevel", 2);
		tag.setInteger("Attack", 2);
		tag.setFloat("HandleModifier", 1.4f);
		tag.setInteger("Reinforced", 1);
		tag.setFloat("Bow_ProjectileSpeed", 4.7f);
		tag.setInteger("Bow_DrawSpeed", 47);
		tag.setFloat("Projectile_Mass", 3f);
		tag.setFloat("Projectile_Fragility", 0.7f);
		tag.setString("Style", EnumChatFormatting.GRAY.toString());
		tag.setInteger("Color", 0xFFDCE1DE);
		FMLInterModComms.sendMessage("TConstruct", "addMaterial", tag);

		tag = new NBTTagCompound();
		tag.setString("FluidName", "invar.molten");
		tag.setInteger("MaterialId", 1020);
		FMLInterModComms.sendMessage("TConstruct", "addPartCastingMaterial", tag);

		tag = new NBTTagCompound();
		tag.setInteger("Id", 1021);
		tag.setString("Name", "Nickel");
		tag.setInteger("Durability", 750);
		tag.setInteger("MiningSpeed", 1100);
		tag.setInteger("HarvestLevel", 2);
		tag.setInteger("Attack", 2);
		tag.setFloat("HandleModifier", 1.35f);
		tag.setInteger("Reinforced", 1);
		tag.setString("Style", EnumChatFormatting.YELLOW.toString());
		tag.setInteger("Color", 0xFFFFFFDE);
		FMLInterModComms.sendMessage("TConstruct", "addMaterial", tag);

		tag = new NBTTagCompound();
		tag.setString("FluidName", "nickel.molten");
		tag.setInteger("MaterialId", 1021);
		FMLInterModComms.sendMessage("TConstruct", "addPartCastingMaterial", tag);

		tag = new NBTTagCompound();
		tag.setInteger("Id", 1022);
		tag.setString("Name", "Lead");
		tag.setInteger("Durability", 250);
		tag.setInteger("MiningSpeed", 900);
		tag.setInteger("HarvestLevel", 2);
		tag.setInteger("Attack", 2);
		tag.setFloat("HandleModifier", 1.2f);
		tag.setInteger("Stonebound", 1);
		tag.setString("Style", EnumChatFormatting.DARK_PURPLE.toString());
		tag.setInteger("Color", 0xFF7380A7);
		FMLInterModComms.sendMessage("TConstruct", "addMaterial", tag);

		tag = new NBTTagCompound();
		tag.setString("FluidName", "lead.molten");
		tag.setInteger("MaterialId", 1022);
		FMLInterModComms.sendMessage("TConstruct", "addPartCastingMaterial", tag);

		tag = new NBTTagCompound();
		tag.setInteger("Id", 1023);
		tag.setString("Name", "Silver");
		tag.setInteger("Durability", 80);
		tag.setInteger("MiningSpeed", 1200);
		tag.setInteger("HarvestLevel", 2);
		tag.setInteger("Attack", 3);
		tag.setFloat("HandleModifier", 1.3f);
		tag.setString("Style", EnumChatFormatting.AQUA.toString());
		tag.setInteger("Color", 0xFFD9EEEB);
		FMLInterModComms.sendMessage("TConstruct", "addMaterial", tag);

		tag = new NBTTagCompound();
		tag.setString("FluidName", "silver.molten");
		tag.setInteger("MaterialId", 1023);
		FMLInterModComms.sendMessage("TConstruct", "addPartCastingMaterial", tag);

		tag = new NBTTagCompound();
		tag.setInteger("Id", 1024);
		tag.setString("Name", "Platinum");
		tag.setInteger("Durability", 1050);
		tag.setInteger("MiningSpeed", 1400);
		tag.setInteger("HarvestLevel", 2);
		tag.setInteger("Attack", 5);
		tag.setFloat("HandleModifier", 1.5f);
		tag.setInteger("Reinforced", 2);
		tag.setString("Style", EnumChatFormatting.AQUA.toString());
		tag.setInteger("Color", 0xFF6FE1ED);
		FMLInterModComms.sendMessage("TConstruct", "addMaterial", tag);

		tag = new NBTTagCompound();
		tag.setString("FluidName", "platinum.molten");
		tag.setInteger("MaterialId", 1024);
		FMLInterModComms.sendMessage("TConstruct", "addPartCastingMaterial", tag);

		tag = new NBTTagCompound();
		tag.setInteger("Id", 1025);
		tag.setString("Name", "Electrum");
		tag.setInteger("Durability", 90);
		tag.setInteger("MiningSpeed", 1700);
		tag.setInteger("HarvestLevel", 2);
		tag.setInteger("Attack", 2);
		tag.setFloat("HandleModifier", 1.4f);
		tag.setInteger("Reinforced", 1);
		tag.setInteger("Stonebound", 1);
		tag.setString("Style", EnumChatFormatting.AQUA.toString());
		tag.setInteger("Color", 0xFFEEE155);
		FMLInterModComms.sendMessage("TConstruct", "addMaterial", tag);

		tag = new NBTTagCompound();
		tag.setString("FluidName", "electrum.molten");
		tag.setInteger("MaterialId", 1025);
		FMLInterModComms.sendMessage("TConstruct", "addPartCastingMaterial", tag);
	}

	public static void postInit() {

	}
}
