package thermalexpansion.block.light;

import cofh.core.render.IconRegistry;
import cofh.lib.util.helpers.ColorHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.BlockTEBase;
import thermalexpansion.block.simple.BlockFrame;
import thermalexpansion.core.TEProps;
import thermalexpansion.util.crafting.TransposerManager;
import thermalfoundation.fluid.TFFluids;

public class BlockLight extends BlockTEBase {

	public BlockLight() {

		super(Material.glass);
		setHardness(3.0F);
		setResistance(150.0F);
		setStepSound(soundTypeGlass);
		setBlockName("thermalexpansion.light");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		return new TileLight();
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {

		for (int i = 0; i < Types.values().length; i++) {
			if (enable[i]) {
				list.add(new ItemStack(item, 1, i));
			}
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {

		if (stack.stackTagCompound != null) {
			TileLight tile = (TileLight) world.getTileEntity(x, y, z);

			tile.modified = true;
			tile.setColor(stack.stackTagCompound.getInteger("Color"));
		}
		super.onBlockPlacedBy(world, x, y, z, living, stack);
	}

	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {

	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int bSide, float hitX, float hitY, float hitZ) {

		TileLight theTile = (TileLight) world.getTileEntity(x, y, z);

		if (ItemHelper.isPlayerHoldingItem(Items.dye, player)) {
			if (ServerHelper.isServerWorld(world)) {
				theTile.setColor(ColorHelper.getDyeColor(player.getCurrentEquippedItem().getItemDamage()));

				if (!player.capabilities.isCreativeMode) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemHelper.consumeItem(player.getCurrentEquippedItem()));
				}
				world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "note.harp", 0.5F, 1.0F);
			}
			return true;
		}
		if (ItemHelper.isPlayerHoldingItem(Items.glowstone_dust, player)) {
			if (ServerHelper.isServerWorld(world)) {
				theTile.resetColor();

				if (!player.capabilities.isCreativeMode) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemHelper.consumeItem(player.getCurrentEquippedItem()));
				}
				world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.orb", 0.25F, 1.0F);
			}
			return true;
		}
		return super.onBlockActivated(world, x, y, z, player, bSide, hitX, hitY, hitZ);
	}

	@Override
	public boolean shouldCheckWeakPower(IBlockAccess world, int x, int y, int z, int side) {

		return true;
	}

	@Override
	public int getRenderBlockPass() {

		return 1;
	}

	@Override
	public int getRenderType() {

		return TEProps.renderIdLight;
	}

	@Override
	public boolean canRenderInPass(int pass) {

		renderPass = pass;
		return pass < 2;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		IconRegistry.addIcon("Light0", "thermalexpansion:light/Illuminator_Frame", ir);
		IconRegistry.addIcon("Light1", "thermalexpansion:light/Lamp_Effect", ir);
		IconRegistry.addIcon("LightEffect", "thermalexpansion:light/Illuminator_Effect", ir);
		IconRegistry.addIcon("LightHalo", "thermalexpansion:light/Lamp_Halo", ir);
	}

	@Override
	public NBTTagCompound getItemStackTag(World world, int x, int y, int z) {

		NBTTagCompound tag = super.getItemStackTag(world, x, y, z);
		TileLight tile = (TileLight) world.getTileEntity(x, y, z);
		if (tile != null && tile.modified) {
			tag = new NBTTagCompound();
			tag.setInteger("Color", tile.color);
		}
		return tag;
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		TileLight.initialize();

		illuminator = new ItemStack(this, 1, 0);
		lampBasic = new ItemStack(this, 1, 1);

		GameRegistry.registerCustomItemStack("illuminator", illuminator);
		GameRegistry.registerCustomItemStack("lampBasic", lampBasic);

		return true;
	}

	@Override
	public boolean postInit() {

		if (enable[Types.ILLUMINATOR.ordinal()]) {
			TransposerManager.addTEFillRecipe(2000, BlockFrame.frameIlluminator, illuminator, new FluidStack(TFFluids.fluidGlowstone, 1000), false);
		}
		if (enable[Types.LAMP_BASIC.ordinal()]) {
			GameRegistry.addRecipe(new ShapedOreRecipe(lampBasic, new Object[] { " L ", "GLG", " S ", 'G', "blockGlassHardened", 'L', "dustLumium", 'S',
					"ingotSignalum" }));
		}
		return true;
	}

	public static enum Types {
		ILLUMINATOR, LAMP_BASIC
	}

	public static final String[] NAMES = { "illuminator", "lampBasic" };
	public static boolean[] enable = new boolean[Types.values().length];

	static {
		String category = "block.feature";
		enable[Types.ILLUMINATOR.ordinal()] = ThermalExpansion.config.get(category, "Light.Illuminator", true);
		enable[Types.LAMP_BASIC.ordinal()] = ThermalExpansion.config.get(category, "Light.LampBasic", true);
	}

	public static ItemStack illuminator;
	public static ItemStack lampBasic;

}
