package cofh.thermalexpansion.block.ender;

import cofh.api.tileentity.ISecurable;
import cofh.core.render.IconRegistry;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.RedstoneControlHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.block.simple.BlockFrame;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.util.crafting.TECraftingHandler;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class BlockEnder extends BlockTEBase {

	public BlockEnder() {

		super(Material.iron);
		setHardness(15.0F);
		setResistance(2000.0F);
		setBlockName("thermalexpansion.ender");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		return new TileTesseract();
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {

		if (enable) {
			list.add(ItemBlockEnder.setDefaultTag(new ItemStack(item, 1, 0)));
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {

		if (world.isRemote) {
			return;
		}

		TileEntity aTile = world.getTileEntity(x, y, z);

		if (aTile instanceof TileTesseract) {
			TileTesseract tile = (TileTesseract) world.getTileEntity(x, y, z);

			tile.setInvName(ItemHelper.getNameFromItemStack(stack));

			if (SecurityHelper.isSecure(stack)) {
				GameProfile stackOwner = SecurityHelper.getOwner(stack);

				if (((ISecurable) tile).setOwner(stackOwner)) {
					;
				} else if (living instanceof ICommandSender) {
					tile.setOwnerName(living.getCommandSenderName());
				}
				tile.setAccessQuick(SecurityHelper.getAccess(stack));
			}
			if (RedstoneControlHelper.hasRSControl(stack)) {
				tile.setControl(RedstoneControlHelper.getControl(stack));
			}
			tile.onNeighborBlockChange();

			if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("Frequency")) {
				if (ServerHelper.isServerWorld(world)) {
					tile.removeFromRegistry();
				}
				tile.modeItem = stack.stackTagCompound.getByte("ModeItems");
				tile.modeFluid = stack.stackTagCompound.getByte("ModeFluid");
				tile.modeEnergy = stack.stackTagCompound.getByte("ModeEnergy");

				tile.frequency = stack.stackTagCompound.getInteger("Frequency");
				tile.isActive = tile.frequency != -1;

				if (ServerHelper.isServerWorld(world)) {
					tile.addToRegistry();
					tile.sendDescPacket();
				}
			}
		} else {
			super.onBlockPlacedBy(world, x, y, z, living, stack);
		}
	}

	@Override
	public int getRenderBlockPass() {

		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {

		return TEProps.renderIdEnder;
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
	public IIcon getIcon(int side, int metadata) {

		return IconRegistry.getIcon("Tesseract");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		IconRegistry.addIcon("Tesseract", "thermalexpansion:tesseract/Tesseract", ir);
		IconRegistry.addIcon("TesseractInner", "thermalexpansion:tesseract/Tesseract_Inner", ir);
		IconRegistry.addIcon("TesseractActive", "thermalexpansion:tesseract/Tesseract_Active", ir);
		IconRegistry.addIcon("TesseractInnerActive", "thermalexpansion:tesseract/Tesseract_Inner_Active", ir);
		IconRegistry.addIcon("SkyEnder", "thermalexpansion:tesseract/Sky_Ender", ir);
	}

	@Override
	public NBTTagCompound getItemStackTag(World world, int x, int y, int z) {

		NBTTagCompound tag = super.getItemStackTag(world, x, y, z);
		TileTesseract tile = (TileTesseract) world.getTileEntity(x, y, z);

		if (tile != null) {
			if (tag == null) {
				tag = new NBTTagCompound();
			}
			tag.setInteger("Frequency", tile.frequency);

			tag.setByte("ModeItems", tile.modeItem);
			tag.setByte("ModeFluid", tile.modeFluid);
			tag.setByte("ModeEnergy", tile.modeEnergy);
		}
		return tag;
	}

	/* IDismantleable */
	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnDrops) {

		TileTesseract tile = (TileTesseract) world.getTileEntity(x, y, z);
		if (tile != null) {
			tile.removeFromRegistry();
			tile.inventory = new ItemStack[0];
		}
		return super.dismantleBlock(player, getItemStackTag(world, x, y, z), world, x, y, z, returnDrops, false);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		TileTesseract.initialize();

		tesseract = new ItemStack(this, 1, 0);

		ItemBlockEnder.setDefaultTag(tesseract);

		return true;
	}

	@Override
	public boolean postInit() {

		if (recipe) {
			GameRegistry.addRecipe(new ShapedOreRecipe(tesseract, new Object[] { "BIB", "ICI", "BIB", 'C', BlockFrame.frameTesseractFull, 'I', "ingotSilver",
					'B', "ingotBronze" }));
		}
		TECraftingHandler.addSecureRecipe(tesseract);

		return true;
	}

	public static boolean enable = true;
	public static boolean recipe = true;

	static {
		String category = "Ender";
		recipe = ThermalExpansion.config.get(category + ".Tesseract", "Recipe.Enable", true);

		boolean blockEnable = ThermalExpansion.config.get(category + ".Tesseract", "Show.Block", true,
				"If FALSE, hides the Tesseract, if the recipe is ALSO disabled.");
		boolean frameEnable = ThermalExpansion.config.get(category + ".Tesseract", "Show.Frame", true,
				"If FALSE, hides the Tesseract Frames, if their recipes are ALSO disabled.");
		boolean frameRecipe = ThermalExpansion.config.get(category + ".Tesseract", "Recipe.Frame", true,
				"If FALSE, disables the Tesseract Frames recipes, if Tesseracts are ALSO disabled.");

		if (!recipe) {
			enable = blockEnable;

			BlockFrame.recipe[BlockFrame.Types.TESSERACT_EMPTY.ordinal()] = frameRecipe;
			BlockFrame.recipe[BlockFrame.Types.TESSERACT_FULL.ordinal()] = frameRecipe;

			if (!frameRecipe) {
				BlockFrame.enable[BlockFrame.Types.TESSERACT_EMPTY.ordinal()] = frameEnable;
				BlockFrame.enable[BlockFrame.Types.TESSERACT_FULL.ordinal()] = frameEnable;
			}
		}
	}

	public static ItemStack tesseract;

}
