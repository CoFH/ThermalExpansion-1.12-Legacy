package cofh.thermalexpansion.item;

import cofh.api.core.ISecurable;
import cofh.api.item.IUpgradeItem;
import cofh.api.tileentity.IUpgradeable;
import cofh.core.item.ItemMulti;
import cofh.core.util.core.IInitializer;
import cofh.core.util.helpers.ChatHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static cofh.core.util.helpers.RecipeHelper.addShapedRecipe;
import static cofh.core.util.helpers.RecipeHelper.addShapelessRecipe;

public class ItemUpgrade extends ItemMulti implements IInitializer, IUpgradeItem {

	public ItemUpgrade() {

		super("thermalexpansion");

		setUnlocalizedName("upgrade");
		setCreativeTab(ThermalExpansion.tabItems);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			tooltip.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		UpgradeType type = getUpgradeType(stack);

		switch (type) {
			case INCREMENTAL:
				tooltip.add(StringHelper.getInfoText("info.thermalexpansion.upgrade.incremental.0"));
				tooltip.add(StringHelper.getNoticeText("info.thermalexpansion.upgrade.incremental.1"));
				break;
			case FULL:
				tooltip.add(StringHelper.getInfoText("info.thermalexpansion.upgrade.full.0"));
				tooltip.add(StringHelper.getNoticeText("info.thermalexpansion.upgrade.full.1"));
				break;
			case CREATIVE:
				tooltip.add(StringHelper.getInfoText("info.thermalexpansion.upgrade.creative.0"));
				tooltip.add(StringHelper.getNoticeText("info.thermalexpansion.upgrade.creative.1"));
				break;
			default:
		}
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {

		return true;
	}

	@Override
	public boolean isFull3D() {

		return true;
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {

		ItemStack stack = player.getHeldItem(hand);
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (!block.hasTileEntity(state)) {
			return EnumActionResult.PASS;
		}
		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof ISecurable && !((ISecurable) tile).canPlayerAccess(player)) {
			return EnumActionResult.PASS;
		}
		if (tile instanceof IUpgradeable) {
			if (!((IUpgradeable) tile).canUpgrade(stack)) {
				return EnumActionResult.PASS;
			}
			if (ServerHelper.isServerWorld(world)) { // Server
				if (((IUpgradeable) tile).installUpgrade(stack)) {
					if (!player.capabilities.isCreativeMode) {
						stack.shrink(1);
					}
					ChatHelper.sendIndexedChatMessageToPlayer(player, new TextComponentTranslation("chat.thermalexpansion.upgrade.install.success"));
				} else {
					ChatHelper.sendIndexedChatMessageToPlayer(player, new TextComponentTranslation("chat.thermalexpansion.upgrade.install.failure"));
				}
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;
	}

	/* IUpgradeItem */
	public UpgradeType getUpgradeType(ItemStack stack) {

		return upgradeMap.get(ItemHelper.getItemDamage(stack)).type;
	}

	public int getUpgradeLevel(ItemStack stack) {

		return upgradeMap.get(ItemHelper.getItemDamage(stack)).level;
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		upgradeIncremental = new ItemStack[4];
		for (int i = 0; i < 4; i++) {
			int level = i + 1;
			upgradeIncremental[i] = addItem(i, "incremental" + level, EnumRarity.values()[level / 2]);
			addUpgradeEntry(i, UpgradeType.INCREMENTAL, level);
		}

		upgradeFull = new ItemStack[4];
		for (int i = 1; i < 4; i++) {
			int level = i + 1;
			upgradeFull[i] = addItem(32 + i, "full" + level, EnumRarity.values()[level / 2]);
			addUpgradeEntry(32 + i, UpgradeType.FULL, level);
		}

		upgradeCreative = addItem(256, "creative", EnumRarity.EPIC);
		addUpgradeEntry(256, UpgradeType.CREATIVE, Byte.MAX_VALUE);

		ThermalExpansion.proxy.addIModelRegister(this);

		return true;
	}

	@Override
	public boolean register() {

		// @formatter:off

		addShapedRecipe(upgradeIncremental[0],
				" I ",
				"IGI",
				"DID",
				'D', "dustRedstone",
				'G', "gearBronze",
				'I', "ingotInvar"
		);
		addShapedRecipe(upgradeIncremental[1],
				" I ",
				"IGI",
				"DID",
				'D', "blockGlassHardened",
				'G', "gearSilver",
				'I', "ingotElectrum"
		);
		addShapedRecipe(upgradeIncremental[2],
				" I ",
				"IGI",
				"DID",
				'D', "dustCryotheum",
				'G', "gearElectrum",
				'I', "ingotSignalum"
		);
		addShapedRecipe(upgradeIncremental[3],
				" I ",
				"IGI",
				"DID",
				'D', "dustPyrotheum",
				'G', "gearLumium",
				'I', "ingotEnderium"
		);

		addShapelessRecipe(upgradeFull[1],
				upgradeIncremental[0],
				upgradeIncremental[1]
		);

		addShapelessRecipe(upgradeFull[2],
				upgradeFull[1],
				upgradeIncremental[2]
		);
		addShapelessRecipe(upgradeFull[2],
				upgradeIncremental[0],
				upgradeIncremental[1],
				upgradeIncremental[2]
		);

		addShapelessRecipe(upgradeFull[3],
				upgradeFull[2],
				upgradeIncremental[3]
		);
		addShapelessRecipe(upgradeFull[3],
				upgradeFull[1],
				upgradeIncremental[2],
				upgradeIncremental[3]
		);
		addShapelessRecipe(upgradeFull[3],
				upgradeIncremental[0],
				upgradeIncremental[1],
				upgradeIncremental[2],
				upgradeIncremental[3]
		);

		// @formatter:on

		return true;
	}

	/* UPGRADE ENTRY */
	public class UpgradeEntry {

		public final UpgradeType type;
		public final int level;

		UpgradeEntry(UpgradeType type, int level) {

			this.type = type;
			this.level = level;
		}
	}

	private void addUpgradeEntry(int metadata, UpgradeType type, int level) {

		upgradeMap.put(metadata, new UpgradeEntry(type, level));
	}

	private TIntObjectHashMap<UpgradeEntry> upgradeMap = new TIntObjectHashMap<>();

	/* REFERENCES */
	public static ItemStack[] upgradeIncremental;
	public static ItemStack[] upgradeFull;
	public static ItemStack upgradeCreative;

}
