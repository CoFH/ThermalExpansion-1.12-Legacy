package cofh.thermalexpansion.item.tool;

import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.util.SoundUtils;
import codechicken.lib.vec.Vector3;
import cofh.api.block.IDismantleable;
import cofh.api.item.IToolHammer;
import cofh.asm.relauncher.Implementable;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.item.TEItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

@Implementable("buildcraft.api.tools.IToolWrench")
public class ItemWrench extends ItemToolBase implements IToolHammer {

	public ItemWrench() {

		super("wrench");
		setHasSubtypes(true);
		setHarvestLevel("wrench", 1);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean check) {

		list.add(StringHelper.getFlavorText("info.thermalexpansion.tool.wrench.0"));
		list.add(StringHelper.getFlavorText("info.thermalexpansion.tool.wrench.1"));
		list.add(StringHelper.getFlavorText("info.thermalexpansion.tool.wrench.2"));

		if (ItemHelper.getItemDamage(stack) == 1) {
			list.add(StringHelper.getInfoText("info.thermalexpansion.tool.wrench.3"));
		}
	}

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return EnumActionResult.SUCCESS;
    }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (state == null) {
			return EnumActionResult.PASS;
		}
        RayTraceResult traceResult = RayTracer.retrace(player);
		PlayerInteractEvent event = new PlayerInteractEvent.RightClickBlock(player, hand, stack, pos, side, traceResult.hitVec);
		if (MinecraftForge.EVENT_BUS.post(event) || event.getResult() == Result.DENY ) {
			return EnumActionResult.PASS;
		}
		if (ServerHelper.isServerWorld(world) && player.isSneaking() && block instanceof IDismantleable
				&& ((IDismantleable) block).canDismantle(player, world, pos)) {
			((IDismantleable) block).dismantleBlock(player, world, pos, false);
			// TODO: Changeover.
			// ArrayList<ItemStack> drops = ((IDismantleable) block).dismantleBlock(player, world, x, y, z, true);
			// for (ItemStack drop : drops) {
			// player.inventory.addItemStackToInventory(drop);
			// ((EntityPlayerMP) player).sendContainerAndContentsToPlayer(player.inventoryContainer, player.inventoryContainer.getInventory());
			// }
			return EnumActionResult.SUCCESS;
		}
		if (BlockHelper.canRotate(block)) {
            if (player.isSneaking()) {
                world.setBlockState(pos, BlockHelper.rotateVanillaBlockAlt(world, state, pos), 3);
                SoundUtils.playSoundAt(new Vector3(pos).add(0.5), world, SoundCategory.BLOCKS, block.getSoundType(state, world, pos, player).getBreakSound(), 1.0F, 0.6F);
            } else {
                world.setBlockState(pos, BlockHelper.rotateVanillaBlock(world, state, pos), 3);
                SoundUtils.playSoundAt(new Vector3(pos).add(0.5), world, SoundCategory.BLOCKS, block.getSoundType(state, world, pos, player).getBreakSound(), 1.0F, 0.8F);
            }
			return ServerHelper.isServerWorld(world) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
		} else if (!player.isSneaking() && block.rotateBlock(world, pos, side)) {
			player.swingArm(hand);
			return ServerHelper.isServerWorld(world) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
		}
		return EnumActionResult.PASS;
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {

		return true;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Multimap getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap multimap = HashMultimap.create();
        if (slot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 1, 0));
        }
		return multimap;
	}

	/* IToolHammer */
	@Override
	public boolean isUsable(ItemStack item, EntityLivingBase user, int x, int y, int z) {

		return true;
	}

	@Override
	public void toolUsed(ItemStack item, EntityLivingBase user, int x, int y, int z) {

	}

	/* IToolWrench */
	public boolean canWrench(EntityPlayer player, int x, int y, int z) {

		return true;
	}

	public void wrenchUsed(EntityPlayer player, int x, int y, int z) {

	}

	public void registerModelVariants() {

		ModelResourceLocation location = new ModelResourceLocation(ThermalExpansion.modId + ":tool", "type=wrench");
		ModelLoader.setCustomModelResourceLocation(TEItems.itemWrench, 0, location);
	}
}
