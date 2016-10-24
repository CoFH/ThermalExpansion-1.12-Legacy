package cofh.thermalexpansion.block.ender;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;

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
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEnder extends BlockTEBase {

	public BlockEnder() {

		super(Material.IRON);
		setHardness(15.0F);
		setResistance(2000.0F);
		setUnlocalizedName("thermalexpansion.ender");
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
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack) {
		if (world.isRemote) {
			return;
		}

		TileEntity aTile = world.getTileEntity(pos);

		if (aTile instanceof TileTesseract) {
			TileTesseract tile = (TileTesseract) world.getTileEntity(pos);

			tile.setInvName(ItemHelper.getNameFromItemStack(stack));

			if (SecurityHelper.isSecure(stack)) {
				GameProfile stackOwner = SecurityHelper.getOwner(stack);

				if (tile.setOwner(stackOwner)) {
                } else if (living instanceof ICommandSender) {
					tile.setOwnerName(living.getName());
				}
				tile.setAccessQuick(SecurityHelper.getAccess(stack));
			}
			if (RedstoneControlHelper.hasRSControl(stack)) {
				tile.setControl(RedstoneControlHelper.getControl(stack));
			}
			tile.onNeighborBlockChange();

			if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("Frequency")) {
				if (ServerHelper.isServerWorld(world)) {
					tile.removeFromRegistry();
				}
				tile.modeItem = stack.getTagCompound().getByte("ModeItems");
				tile.modeFluid = stack.getTagCompound().getByte("ModeFluid");
				tile.modeEnergy = stack.getTagCompound().getByte("ModeEnergy");

				tile.frequency = stack.getTagCompound().getInteger("Frequency");
				tile.isActive = tile.frequency != -1;

				if (ServerHelper.isServerWorld(world)) {
					tile.addToRegistry();
					tile.sendDescPacket();
				}
			}
		} else {
			super.onBlockPlacedBy(world, pos, state, living, stack);
		}
	}

	//@Override
	public int getRenderBlockPass() {

		return 1;
	}

	//@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {

		return TEProps.renderIdEnder;
	}

	//@Override
	public boolean canRenderInPass(int pass) {

		renderPass = pass;
		return pass < 2;
	}

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}

	/*@Override
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
	}*/

	@Override
	public NBTTagCompound getItemStackTag(IBlockAccess world, BlockPos pos) {

		NBTTagCompound tag = super.getItemStackTag(world, pos);
		TileTesseract tile = (TileTesseract) world.getTileEntity(pos);

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
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, BlockPos pos, boolean returnDrops) {

		TileTesseract tile = (TileTesseract) world.getTileEntity(pos);
		if (tile != null) {
			tile.removeFromRegistry();
			tile.inventory = new ItemStack[0];
		}
		return super.dismantleBlock(player, getItemStackTag(world, pos), world, pos, returnDrops, false);
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
			GameRegistry.addRecipe(ShapedRecipe(tesseract, "BIB", "ICI", "BIB", 'C', BlockFrame.frameTesseractFull, 'I', "ingotSilver", 'B', "ingotBronze"));
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
