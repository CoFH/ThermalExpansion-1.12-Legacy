package cofh.thermalexpansion.item;

import cofh.api.core.IAugmentable;
import cofh.api.core.ISecurable;
import cofh.api.item.IAugmentItem;
import cofh.core.item.ItemMulti;
import cofh.core.util.core.IInitializer;
import cofh.core.util.helpers.ChatHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalfoundation.item.ItemFertilizer;
import cofh.thermalfoundation.item.ItemMaterial;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static cofh.core.util.helpers.RecipeHelper.addShapedRecipe;

public class ItemAugment extends ItemMulti implements IInitializer, IAugmentItem {

	public ItemAugment() {

		super("thermalexpansion");

		setUnlocalizedName("augment");
		setCreativeTab(ThermalExpansion.tabItems);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {

		return StringHelper.localize("info.thermalexpansion.augment.0") + ": " + super.getItemStackDisplayName(stack);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			tooltip.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		AugmentType type = getAugmentType(stack);
		String id = getAugmentIdentifier(stack);

		int i = 0;
		String line = "info.thermalexpansion.augment." + id + "." + i;
		while (StringHelper.canLocalize(line)) {
			tooltip.add(StringHelper.localize(line));
			i++;
			line = "info.thermalexpansion.augment." + id + "." + i;
		}
		i = 0;
		line = "info.thermalexpansion.augment." + id + ".a." + i;
		while (StringHelper.canLocalize(line)) {
			tooltip.add(StringHelper.BRIGHT_GREEN + StringHelper.localize(line));
			i++;
			line = "info.thermalexpansion.augment." + id + ".a." + i;
		}
		i = 0;
		line = "info.thermalexpansion.augment." + id + ".b." + i;
		while (StringHelper.canLocalize(line)) {
			tooltip.add(StringHelper.RED + StringHelper.localize(line));
			i++;
			line = "info.thermalexpansion.augment." + id + ".b." + i;
		}
		i = 0;
		line = "info.thermalexpansion.augment." + id + ".c." + i;
		while (StringHelper.canLocalize(line)) {
			tooltip.add(StringHelper.YELLOW + StringHelper.localize(line));
			i++;
			line = "info.thermalexpansion.augment." + id + ".c." + i;
		}
		switch (type) {
			case ADVANCED:
				// tooltip.add(StringHelper.getNoticeText("info.thermalexpansion.augment.noticeAdvanced"));
				break;
			case MODE:
				tooltip.add(StringHelper.getNoticeText("info.thermalexpansion.augment.noticeMode"));
				break;
			case ENDER:
				tooltip.add(StringHelper.getNoticeText("info.thermalexpansion.augment.noticeEnder"));
				break;
			case CREATIVE:
				tooltip.add(StringHelper.getNoticeText("info.thermalexpansion.augment.noticeCreative"));
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
		if (tile instanceof IAugmentable) {
			if (((IAugmentable) tile).getAugmentSlots().length <= 0) {
				return EnumActionResult.PASS;
			}
			if (ServerHelper.isServerWorld(world)) { // Server
				if (((IAugmentable) tile).installAugment(stack)) {
					if (!player.capabilities.isCreativeMode) {
						stack.shrink(1);
					}
					player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 0.4F, 0.8F);
					ChatHelper.sendIndexedChatMessageToPlayer(player, new TextComponentTranslation("chat.thermalfoundation.augment.install.success"));
				} else {
					ChatHelper.sendIndexedChatMessageToPlayer(player, new TextComponentTranslation("chat.thermalfoundation.augment.install.failure"));
				}
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;
	}

	/* IAugmentItem */
	@Override
	public AugmentType getAugmentType(ItemStack stack) {

		return augmentMap.get(ItemHelper.getItemDamage(stack)).type;
	}

	@Override
	public String getAugmentIdentifier(ItemStack stack) {

		return augmentMap.get(ItemHelper.getItemDamage(stack)).identifier;
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		/* MACHINES */
		machinePower = addAugmentItem(128, TEProps.MACHINE_POWER);
		machineSecondary = addAugmentItem(129, TEProps.MACHINE_SECONDARY);
		machineSecondaryNull = addAugmentItem(130, TEProps.MACHINE_SECONDARY_NULL, AugmentType.ADVANCED);

		machineFurnaceFood = addAugmentItem(256, TEProps.MACHINE_FURNACE_FOOD, AugmentType.MODE);
		machineFurnaceOre = addAugmentItem(257, TEProps.MACHINE_FURNACE_ORE, AugmentType.MODE);
		machineFurnacePyrolysis = addAugmentItem(258, TEProps.MACHINE_FURNACE_PYROLYSIS, AugmentType.MODE);

		// machinePulverizerGeode = addAugmentItem(272, TEProps.MACHINE_PULVERIZER_GEODE, AugmentType.MODE);
		machinePulverizerPetrotheum = addAugmentItem(273, TEProps.MACHINE_PULVERIZER_PETROTHEUM, AugmentType.MODE);

		machineSawmillTapper = addAugmentItem(288, TEProps.MACHINE_SAWMILL_TAPPER, AugmentType.MODE);

		machineSmelterFlux = addAugmentItem(303, TEProps.MACHINE_SMELTER_FLUX);
		machineSmelterPyrotheum = addAugmentItem(304, TEProps.MACHINE_SMELTER_PYROTHEUM, AugmentType.MODE);

		machineInsolatorFertilizer = addAugmentItem(320, TEProps.MACHINE_INSOLATOR_FERTILIZER);
		machineInsolatorTree = addAugmentItem(323, TEProps.MACHINE_INSOLATOR_TREE, AugmentType.ADVANCED);
		machineInsolatorMonoculture = addAugmentItem(324, TEProps.MACHINE_INSOLATOR_MONOCULTURE, AugmentType.MODE);

		machineCompactorMint = addAugmentItem(336, TEProps.MACHINE_COMPACTOR_MINT, AugmentType.MODE);
		machineCompactorGear = addAugmentItem(337, TEProps.MACHINE_COMPACTOR_GEAR, AugmentType.MODE);

		machineCrucibleLava = addAugmentItem(352, TEProps.MACHINE_CRUCIBLE_LAVA, AugmentType.MODE);
		// machineCrucibleAlloy = addAugmentItem(353, TEProps.MACHINE_CRUCIBLE_ALLOY, AugmentType.MODE);

		machineRefineryOil = addAugmentItem(368, TEProps.MACHINE_REFINERY_OIL, AugmentType.MODE);
		machineRefineryPotion = addAugmentItem(369, TEProps.MACHINE_REFINERY_POTION, AugmentType.MODE);

		machineChargerThroughput = addAugmentItem(400, TEProps.MACHINE_CHARGER_THROUGHPUT, AugmentType.MODE);
		machineChargerRepair = addAugmentItem(401, TEProps.MACHINE_CHARGER_REPAIR, AugmentType.MODE);
		machineChargerWireless = addAugmentItem(402, TEProps.MACHINE_CHARGER_WIRELESS, AugmentType.MODE);

		machineBrewerReagent = addAugmentItem(448, TEProps.MACHINE_BREWER_REAGENT);

		// machineEnchanterEmpowered = addAugmentItem(464, TEProps.MACHINE_ENCHANTER_EMPOWERED, AugmentType.MODE);

		machineExtruderNoWater = addAugmentItem(496, TEProps.MACHINE_EXTRUDER_NO_WATER, AugmentType.MODE);

		/* DYNAMOS */
		dynamoPower = addAugmentItem(512, TEProps.DYNAMO_POWER);
		dynamoEfficiency = addAugmentItem(513, TEProps.DYNAMO_EFFICIENCY);
		dynamoCoilDuct = addAugmentItem(514, TEProps.DYNAMO_COIL_DUCT, AugmentType.ADVANCED);
		dynamoThrottle = addAugmentItem(515, TEProps.DYNAMO_THROTTLE, AugmentType.ADVANCED);

		dynamoBoiler = addAugmentItem(576, TEProps.DYNAMO_BOILER, AugmentType.MODE);

		dynamoSteamTurbine = addAugmentItem(640, TEProps.DYNAMO_STEAM_TURBINE, AugmentType.MODE);

		dynamoMagmaticCoolant = addAugmentItem(656, TEProps.DYNAMO_MAGMATIC_COOLANT, AugmentType.MODE);

		dynamoCompressionCoolant = addAugmentItem(672, TEProps.DYNAMO_COMPRESSION_COOLANT, AugmentType.MODE);
		dynamoCompressionFuel = addAugmentItem(673, TEProps.DYNAMO_COMPRESSION_FUEL, AugmentType.MODE);

		dynamoReactantElemental = addAugmentItem(688, TEProps.DYNAMO_REACTANT_ELEMENTAL, AugmentType.MODE);

		dynamoEnervationEnchant = addAugmentItem(704, TEProps.DYNAMO_ENERVATION_ENCHANT, AugmentType.MODE);

		dynamoNumismaticGem = addAugmentItem(720, TEProps.DYNAMO_NUMISMATIC_GEM, AugmentType.MODE);

		/* AUTOMATA */
		// apparatusDepth = addAugmentItem(896, TEProps.APPARATUS_DEPTH);
		// apparatusRadius = addAugmentItem(897, TEProps.APPARATUS_RADIUS);

		ThermalExpansion.proxy.addIModelRegister(this);

		return true;
	}

	@Override
	public boolean register() {

		// @formatter:off

		/* MACHINE */
		addShapedRecipe(machinePower,
				" I ",
				"ICI",
				"YIY",
				'C', ItemMaterial.powerCoilGold,
				'I', "ingotGold",
				'Y', "dustRedstone"
		);
		addShapedRecipe(machineSecondary,
				" I ",
				"ICI",
				"YIY",
				'C', ItemMaterial.redstoneServo,
				'I', "ingotBronze",
				'Y', "blockRockwool"
		);
		addShapedRecipe(machineSecondaryNull,
				" I ",
				"ICI",
				"YIY",
				'C', Items.LAVA_BUCKET,
				'I', "nuggetInvar",
				'Y', "blockGlass"
		);

		addShapedRecipe(machineFurnaceFood,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.powerCoilElectrum,
				'G', "gearCopper",
				'I', "plateSilver",
				'X', Blocks.BRICK_BLOCK,
				'Y', "dustRedstone"
		);
		addShapedRecipe(machineFurnaceOre,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.powerCoilElectrum,
				'G', "gearBronze",
				'I', "plateInvar",
				'X', Blocks.PISTON,
				'Y', "dustPyrotheum"
		);
		addShapedRecipe(machineFurnacePyrolysis,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.redstoneServo,
				'G', "gearInvar",
				'I', "plateCopper",
				'X', Blocks.NETHER_BRICK,
				'Y', "dustCharcoal"
		);

//		addShapedRecipe(machinePulverizerGeode,
//				" G ",
//				"ICI",
//				"YXY",
//				'C', ItemMaterial.redstoneServo,
//				'G', "gearLead",
//				'I', "plateBronze",
//				'X', "gemDiamond",
//				'Y', "dustPetrotheum"
//		);
		addShapedRecipe(machinePulverizerPetrotheum,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.redstoneServo,
				'G', "gearSignalum",
				'I', "plateBronze",
				'X', "blockGlassHardened",
				'Y', "dustPetrotheum"
		);

		addShapedRecipe(machineSawmillTapper,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.redstoneServo,
				'G', "gearTin",
				'I', "plateCopper",
				'X', Items.BUCKET,
				'Y', "dustRedstone"
		);

		addShapedRecipe(machineSmelterFlux,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.redstoneServo,
				'G', "gearSilver",
				'I', "ingotTin",
				'X', "blockGlassHardened",
				'Y', ItemMaterial.crystalSlagRich
		);
		addShapedRecipe(machineSmelterPyrotheum,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.powerCoilElectrum,
				'G', "gearSignalum",
				'I', "plateNickel",
				'X', "blockGlassHardened",
				'Y', "dustPyrotheum"
		);

		addShapedRecipe(machineInsolatorFertilizer,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.redstoneServo,
				'G', "gearSilver",
				'I', "ingotTin",
				'X', "blockGlassHardened",
				'Y', ItemFertilizer.fertilizerRich
		);
		addShapedRecipe(machineInsolatorTree,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.redstoneServo,
				'G', "gearInvar",
				'I', "plateLumium",
				'X', Blocks.PISTON,
				'Y', "dustAerotheum"
		);
		addShapedRecipe(machineInsolatorMonoculture,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.powerCoilElectrum,
				'G', "gearSignalum",
				'I', "plateNickel",
				'X', Blocks.HOPPER,
				'Y', ItemFertilizer.fertilizerFlux
		);

		addShapedRecipe(machineCompactorMint,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.redstoneServo,
				'G', "gearInvar",
				'I', "plateElectrum",
				'X', "gemEmerald",
				'Y', "dustGlowstone"
		);
		addShapedRecipe(machineCompactorGear,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.redstoneServo,
				'G', "gearIron",
				'I', "plateLead",
				'X', Blocks.PISTON,
				'Y', "dustObsidian"
		);

		addShapedRecipe(machineCrucibleLava,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.powerCoilElectrum,
				'G', "gearInvar",
				'I', Blocks.PISTON,
				'X', Blocks.NETHER_BRICK,
				'Y', "dustPyrotheum"
		);
		//		addShapedRecipe(machineCrucibleAlloy,
//				" G ",
//				"ICI",
//				"YXY",
//				'C', ItemMaterial.redstoneServo,
//				'G', "gearSignalum",
//				'I', "plateInvar",
//				'X', Blocks.BRICK_BLOCK,
//				'Y', "dustCryotheum"
//		);

		addShapedRecipe(machineRefineryOil,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.powerCoilElectrum,
				'G', "gearNickel",
				'I', "blockGlassHardened",
				'X', Items.BLAZE_ROD,
				'Y', "dustRedstone"
		);
		addShapedRecipe(machineRefineryPotion,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.powerCoilElectrum,
				'G', "gearSignalum",
				'I', "plateLead",
				'X', Items.CAULDRON,
				'Y', "dustRedstone"
		);

		addShapedRecipe(machineChargerThroughput,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.powerCoilElectrum,
				'G', "gearElectrum",
				'I', "plateSilver",
				'X', "ingotLead",
				'Y', "dustRedstone"
		);
		addShapedRecipe(machineChargerRepair,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.powerCoilElectrum,
				'G', "gearIron",
				'I', "blockGlassHardened",
				'X', "blockIron",
				'Y', "dustRedstone"
		);
		addShapedRecipe(machineChargerWireless,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.powerCoilElectrum,
				'G', "gearElectrum",
				'I', "plateSilver",
				'X', "blockGlassHardened",
				'Y', "dustRedstone"
		);

		addShapedRecipe(machineBrewerReagent,
				" G ",
				"ICI",
				"YXZ",
				'C', ItemMaterial.redstoneServo,
				'G', "gearSilver",
				'I', "ingotTin",
				'X', "blockGlassHardened",
				'Y', "dustGlowstone",
				'Z', "dustRedstone"
		);

//		addShapedRecipe(machineEnchanterEmpowered,
//				" G ",
//				"ICI",
//				"YXY",
//				'C', ItemMaterial.powerCoilElectrum,
//				'G', "gearGold",
//				'I', "gemDiamond",
//				'X', "blockLapis",
//				'Y', "dustGlowstone"
//		);

		addShapedRecipe(machineExtruderNoWater,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.redstoneServo,
				'G', "gearCopper",
				'I', "plateInvar",
				'X', Blocks.PISTON,
				'Y', "dustCryotheum"
		);

		/* DYNAMO */
		addShapedRecipe(dynamoPower,
				" I ",
				"ICI",
				"YIY",
				'C', ItemMaterial.powerCoilSilver,
				'I', "ingotSilver",
				'Y', "dustRedstone"
		);
		addShapedRecipe(dynamoEfficiency,
				" I ",
				"ICI",
				"YIY",
				'C', ItemMaterial.powerCoilElectrum,
				'I', "ingotLead",
				'Y', "dustGlowstone"
		);
		addShapedRecipe(dynamoCoilDuct,
				" I ",
				"ICI",
				"YIY",
				'C', "ingotCopper",
				'I', "nuggetLead",
				'Y', "blockGlass"
		);
		addShapedRecipe(dynamoThrottle,
				" I ",
				"ICI",
				"YIY",
				'C', "ingotElectrum",
				'I', "nuggetLead",
				'Y', "blockGlass"
		);

		addShapedRecipe(dynamoBoiler,
				" G ",
				"ICI",
				"YXY",
				'C', Items.BUCKET,
				'G', "gearIron",
				'I', "plateCopper",
				'X', "blockGlassHardened",
				'Y', "dustRedstone"
		);

		addShapedRecipe(dynamoSteamTurbine,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.powerCoilElectrum,
				'G', "gearIron",
				'I', "plateCopper",
				'X', "ingotIron",
				'Y', "dustRedstone"
		);

		addShapedRecipe(dynamoMagmaticCoolant,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.powerCoilElectrum,
				'G', "gearSignalum",
				'I', "plateInvar",
				'X', "blockGlassHardened",
				'Y', "dustCryotheum"
		);

		addShapedRecipe(dynamoCompressionCoolant,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.powerCoilElectrum,
				'G', "gearInvar",
				'I', "plateTin",
				'X', "blockGlassHardened",
				'Y', "dustCryotheum"
		);
		addShapedRecipe(dynamoCompressionFuel,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.powerCoilElectrum,
				'G', "gearSignalum",
				'I', "plateCopper",
				'X', "blockGlassHardened",
				'Y', "dustPyrotheum"
		);

		addShapedRecipe(dynamoReactantElemental,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.powerCoilElectrum,
				'G', "gearSignalum",
				'I', "plateLead",
				'X', "blockGlassHardened",
				'Y', "dustAerotheum"
		);

		addShapedRecipe(dynamoEnervationEnchant,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.powerCoilElectrum,
				'G', "gearSignalum",
				'I', "plateGold",
				'X', "gemLapis",
				'Y', "dustGlowstone"
		);

		addShapedRecipe(dynamoNumismaticGem,
				" G ",
				"ICI",
				"YXY",
				'C', ItemMaterial.powerCoilElectrum,
				'G', "gearSignalum",
				'I', "plateElectrum",
				'X', "gemEmerald",
				'Y', "dustAerotheum"
		);

		/* APPARATUS */

		// @formatter:on

		return true;
	}

	/* UPGRADE ENTRY */
	public class AugmentEntry {

		public final AugmentType type;
		public final String identifier;

		AugmentEntry(AugmentType type, String identifier) {

			this.type = type;
			this.identifier = identifier;
		}
	}

	private void addAugmentEntry(int metadata, AugmentType type, String identifier) {

		augmentMap.put(metadata, new AugmentEntry(type, identifier));
	}

	private ItemStack addAugmentItem(int metadata, String name) {

		addAugmentEntry(metadata, AugmentType.BASIC, name);
		return addItem(metadata, name);
	}

	private ItemStack addAugmentItem(int metadata, String name, EnumRarity rarity) {

		addAugmentEntry(metadata, AugmentType.BASIC, name);
		return addItem(metadata, name, rarity);
	}

	private ItemStack addAugmentItem(int metadata, String name, AugmentType type) {

		EnumRarity rarity;

		switch (type) {
			case ADVANCED:
			case MODE:
				rarity = EnumRarity.UNCOMMON;
				break;
			case ENDER:
				rarity = EnumRarity.RARE;
				break;
			case CREATIVE:
				rarity = EnumRarity.EPIC;
				break;
			default:
				rarity = EnumRarity.COMMON;
		}
		return addAugmentItem(metadata, name, type, rarity);
	}

	private ItemStack addAugmentItem(int metadata, String name, AugmentType type, EnumRarity rarity) {

		addAugmentEntry(metadata, type, name);
		return addItem(metadata, name, rarity);
	}

	private TIntObjectHashMap<AugmentEntry> augmentMap = new TIntObjectHashMap<>();

	/* REFERENCES */

	/* Fluid */
	public static ItemStack fluidStorage;

	/* Energy */
	public static ItemStack energyStorage;

	/* Ender */
	public static ItemStack enderReception;
	public static ItemStack enderTransmission;

	/* Machine */
	public static ItemStack machinePower;
	public static ItemStack machineSecondary;

	public static ItemStack machineSecondaryNull;
	public static ItemStack machineEssenceCrystals;             // Experience Gathering

	public static ItemStack machineFurnaceFood;
	public static ItemStack machineFurnaceOre;
	public static ItemStack machineFurnacePyrolysis;

	public static ItemStack machinePulverizerGeode;
	public static ItemStack machinePulverizerPetrotheum;

	public static ItemStack machineSawmillTapper;

	public static ItemStack machineSmelterPyrotheum;
	public static ItemStack machineSmelterFlux;

	public static ItemStack machineInsolatorFertilizer;
	public static ItemStack machineInsolatorTree;
	public static ItemStack machineInsolatorMonoculture;

	public static ItemStack machineCompactorMint;
	public static ItemStack machineCompactorGear;

	public static ItemStack machineCrucibleLava;
	public static ItemStack machineCrucibleAlloy;

	public static ItemStack machineRefineryOil;
	public static ItemStack machineRefineryPotion;

	public static ItemStack machineChargerThroughput;
	public static ItemStack machineChargerRepair;
	public static ItemStack machineChargerWireless;

	public static ItemStack machineCentrifugeMobs;              // Enstabulation Chamber

	public static ItemStack machineBrewerReagent;

	public static ItemStack machineEnchanterEmpowered;

	public static ItemStack machineExtruderNoWater;

	/* Dynamo */
	public static ItemStack dynamoPower;
	public static ItemStack dynamoEfficiency;
	public static ItemStack dynamoCoilDuct;
	public static ItemStack dynamoThrottle;

	public static ItemStack dynamoBoiler;

	public static ItemStack dynamoSteamTurbine;

	public static ItemStack dynamoMagmaticCoolant;

	public static ItemStack dynamoCompressionCoolant;
	public static ItemStack dynamoCompressionFuel;

	public static ItemStack dynamoReactantElemental;

	public static ItemStack dynamoEnervationEnchant;

	public static ItemStack dynamoNumismaticGem;

	/* Apparatus */
	public static ItemStack apparatusDepth;
	public static ItemStack apparatusRadius;

	public static ItemStack apparatusBreakerFluid;
	public static ItemStack apparatusCollectorEntity;

}
