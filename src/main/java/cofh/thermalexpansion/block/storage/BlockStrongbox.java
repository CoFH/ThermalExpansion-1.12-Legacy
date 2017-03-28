package cofh.thermalexpansion.block.storage;

import cofh.thermalexpansion.block.BlockTEBase;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

import static cofh.lib.util.helpers.ItemHelper.ShapedRecipe;
import static cofh.lib.util.helpers.ItemHelper.addRecipe;

public class BlockStrongbox extends BlockTEBase {

	public BlockStrongbox() {

		super(Material.IRON);

		setUnlocalizedName("strongbox");

		setHardness(15.0F);
		setResistance(25.0F);
		setDefaultState(getBlockState().getBaseState());
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void getSubBlocks(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {

		if (enable) {
			if (TEProps.creativeTabShowAllLevels) {
				for (int j = 0; j < 5; j++) {
					list.add(itemBlock.setDefaultTag(new ItemStack(item, 1, 0), j));
				}
			} else {
				list.add(itemBlock.setDefaultTag(new ItemStack(item, 1, 0), TEProps.creativeTabLevel));
			}
			if (TEProps.creativeTabShowCreative) {
				list.add(itemBlock.setCreativeTag(new ItemStack(item, 1, 0), 4));
			}
		}
	}

	/* ITileEntityProvider */
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		// return new TileStrongbox();
		return null;
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		this.setRegistryName("strongbox");
		GameRegistry.register(this);

		itemBlock = new ItemBlockStrongbox(this);
		itemBlock.setRegistryName(this.getRegistryName());
		GameRegistry.register(itemBlock);

		return true;
	}

	@Override
	public boolean initialize() {

		TileStrongbox.initialize();

		strongbox = new ItemStack[5];

		for (int i = 0; i < 5; i++) {
			strongbox[i] = itemBlock.setDefaultTag(new ItemStack(this), i);
		}
		addRecipes();

		return true;
	}

	@Override
	public boolean postInit() {

		return true;
	}

	/* HELPERS */
	private void addRecipes() {

		// @formatter:off
		if (enable) {
			addRecipe(ShapedRecipe(strongbox[0],
					" I ",
					"ICI",
					" I ",
					'C', "chestWood",
					'I', "ingotTin"
			));
		}
		// @formatter:on
	}

	public static boolean enable;

	/* REFERENCES */
	public static ItemStack strongbox[];
	public static ItemBlockStrongbox itemBlock;

}
