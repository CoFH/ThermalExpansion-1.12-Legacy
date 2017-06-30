package cofh.thermalexpansion.item;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.generation.IBakery;
import cofh.core.item.ItemMulti;
import cofh.core.util.core.IInitializer;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.render.RenderFrame;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import static cofh.lib.util.helpers.RecipeHelper.addShapedRecipe;

public class ItemFrame extends ItemMulti implements IInitializer, IBakeryProvider {

	public ItemFrame() {

		super("thermalexpansion");

		setUnlocalizedName("frame");
		setCreativeTab(ThermalExpansion.tabItems);
	}

	@SideOnly (Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {

	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	/* IBakeryItem */
	@Override
	@SideOnly (Side.CLIENT)
	public IBakery getBakery() {

		return RenderFrame.INSTANCE;
	}

	/* IInitializer */
	@Override
	public boolean preInit() {

		frameMachine = addItem(0, "frameMachine");
		// frameApparatus = addItem(32, "frameApparatus");
		frameDevice = addItem(64, "frameDevice");
		frameCell = addItem(128, "frameCell");
		// frameLight = addItem(160, "frameLight");

		ThermalExpansion.proxy.addIModelRegister(this);

		return true;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		ModelResourceLocation location = new ModelResourceLocation("thermalexpansion:frame", "frame");
		ModelLoader.setCustomModelResourceLocation(this, 0, location);
		ModelLoader.setCustomMeshDefinition(this, (stack -> location));
		ModelRegistryHelper.register(location, new CCBakeryModel(""));
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
				'I', "ingotIron"
		);
		addShapedRecipe(frameCell,
				"IGI",
				"GCG",
				"IGI",
				'C', "gearLead",
				'G', "blockGlass",
				'I', "ingotIron"
		);
//		addShapedRecipe(ItemHelper.cloneStack(frameLight, 2),
//				" Q ",
//				"G G",
//				" I ",
//				'G', BlockGlass.glassLead,
//				'I', "ingotSignalum",
//				'Q', "gemQuartz"
//		);

		// @formatter:on

		return true;
	}

	@Override
	public boolean postInit() {

		return true;
	}

	/* REFERENCES */
	public static ItemStack frameMachine;
	public static ItemStack frameApparatus;
	public static ItemStack frameDevice;
	public static ItemStack frameCell;
	public static ItemStack frameLight;

}
