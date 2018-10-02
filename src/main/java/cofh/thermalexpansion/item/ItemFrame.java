package cofh.thermalexpansion.item;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.generation.IBakery;
import cofh.core.item.ItemMulti;
import cofh.core.util.core.IInitializer;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.storage.BlockCell;
import cofh.thermalexpansion.render.BakeryFrame;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static cofh.core.util.helpers.RecipeHelper.addShapedRecipe;

public class ItemFrame extends ItemMulti implements IInitializer, IBakeryProvider {

	public ItemFrame() {

		super("thermalexpansion");

		setUnlocalizedName("frame");
		setCreativeTab(ThermalExpansion.tabItems);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	/* IBakeryProvider */
	@Override
	@SideOnly (Side.CLIENT)
	public IBakery getBakery() {

		return BakeryFrame.INSTANCE;
	}

	/* IModelRegister */
	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		ModelResourceLocation location = new ModelResourceLocation("thermalexpansion:frame", "frame");
		ModelLoader.setCustomModelResourceLocation(this, 0, location);
		ModelLoader.setCustomMeshDefinition(this, (stack -> location));
		ModelRegistryHelper.register(location, new CCBakeryModel());
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		ForgeRegistries.ITEMS.register(setRegistryName("frame"));
		ThermalExpansion.proxy.addIModelRegister(this);

		frameMachine = addItem(MACHINE, "frameMachine");
		// frameApparatus = addItem(32, "frameApparatus");
		frameDevice = addItem(DEVICE, "frameDevice");
		frameCell0 = addItem(CELL, "frameCell");
		frameCell1 = addItem(CELL + 1, "frameCell1");
		frameCell2 = addItem(CELL + 2, "frameCell2", EnumRarity.UNCOMMON);
		frameCell3 = addItem(CELL + 3, "frameCell3", EnumRarity.UNCOMMON);
		frameCell4 = addItem(CELL + 4, "frameCell4", EnumRarity.RARE);
		frameCell2Filled = addItem(CELL + 2 + 16, "frameCell2Filled", EnumRarity.UNCOMMON);
		frameCell3Filled = addItem(CELL + 3 + 16, "frameCell3Filled", EnumRarity.UNCOMMON);
		frameCell4Filled = addItem(CELL + 4 + 16, "frameCell4Filled", EnumRarity.RARE);
		// frameLight = addItem(160, "frameLight");

		return true;
	}

	@Override
	public boolean initialize() {

		// @formatter:off
		addShapedRecipe(frameMachine,
				"IGI",
				"GCG",
				"IGI",
				'C', "gearTin",
				'G', "blockGlass",
				'I', "ingotIron"
		);
//		addShapedRecipe(frameApparatus,
//				"IGI",
//				"GCG",
//				"IGI",
//				'C', "gearBronze",
//				'G', "blockGlass",
//				'I', "ingotIron"
//		));
		addShapedRecipe(frameDevice,
				"IGI",
				"GCG",
				"IGI",
				'C', "gearCopper",
				'G', "blockGlass",
				'I', "ingotTin"
		);
		addShapedRecipe(frameCell0,
				"IGI",
				"GCG",
				"IGI",
				'C', "gearLead",
				'G', "blockGlass",
				'I', "ingotIron"
		);

		if (BlockCell.enableClassicRecipes) {
			addShapedRecipe(frameCell1,
				" I ",
				"ICI",
				" I ",
				'C', frameCell0,
				'I', "ingotInvar"
			);
			addShapedRecipe(frameCell2,
				"IGI",
				"GCG",
				"IGI",
				'C', "gemDiamond",
				'G', "blockGlassHardened",
				'I', "ingotElectrum"
			);
			addShapedRecipe(frameCell3,
				" I ",
				"ICI",
				" I ",
				'C', frameCell2,
				'I', "ingotSignalum"
			);
			addShapedRecipe(frameCell3Filled,
				" I ",
				"ICI",
				" I ",
				'C', frameCell2Filled,
				'I', "ingotSignalum"
			);
			addShapedRecipe(frameCell4,
				" I ",
				"ICI",
				" I ",
				'C', frameCell3,
				'I', "ingotEnderium"
			);
			addShapedRecipe(frameCell4Filled,
				" I ",
				"ICI",
				" I ",
				'C', frameCell3Filled,
				'I', "ingotEnderium"
			);
		}

//		addShapedRecipe(ItemHelper.cloneStack(frameLight, 2),
//				" Q ",
//				"G G",
//				" I ",
//				'G', "blockGlassHardened",
//				'I', "ingotSignalum",
//				'Q', "gemQuartz"
//		);
		// @formatter:on
		return true;
	}

	public static final int MACHINE = 0;
	public static final int DEVICE = 64;
	public static final int CELL = 128;
	public static final int LIGHT = 160;

	/* REFERENCES */
	public static ItemStack frameMachine;
	public static ItemStack frameApparatus;
	public static ItemStack frameDevice;
	public static ItemStack frameCell0;
	public static ItemStack frameCell1;
	public static ItemStack frameCell2;
	public static ItemStack frameCell3;
	public static ItemStack frameCell4;
	public static ItemStack frameCell2Filled;
	public static ItemStack frameCell3Filled;
	public static ItemStack frameCell4Filled;
	public static ItemStack frameLight;

}
