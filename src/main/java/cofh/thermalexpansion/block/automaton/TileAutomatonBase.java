package cofh.thermalexpansion.block.automaton;

import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.block.TilePowered;
import cofh.thermalexpansion.init.TETextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;

public abstract class TileAutomatonBase extends TilePowered implements ITickable {

	protected static final SideConfig[] defaultSideConfig = new SideConfig[BlockAutomaton.Type.values().length];
	private static boolean enableSecurity = true;

	public static void config() {

		String comment = "Enable this to allow for Automatons to be securable.";
		enableSecurity = ThermalExpansion.CONFIG.get("Security", "Automaton.All.Securable", true, comment);
	}

	public TileAutomatonBase() {

		sideConfig = defaultSideConfig[this.getType()];
		setDefaultSides();
	}

	@Override
	public String getTileName() {

		return "tile.thermalexpansion.automaton." + BlockAutomaton.Type.byMetadata(getType()).getName() + ".name";
	}

	@Override
	public boolean enableSecurity() {

		return enableSecurity;
	}

	@Override
	public boolean sendRedstoneUpdates() {

		return true;
	}

	@Override
	public void update() {

	}

	/* IReconfigurableFacing */
	@Override
	public boolean allowYAxisFacing() {

		return true;
	}

	@Override
	public boolean setFacing(int side) {

		if (side < 0 || side > 5) {
			return false;
		}
		facing = (byte) side;
		sideCache[facing] = 0;
		sideCache[facing ^ 1] = 1;
		markDirty();
		sendUpdatePacket(Side.CLIENT);
		return true;
	}

	/* ISidedTexture */
	@Override
	public TextureAtlasSprite getTexture(int side, int pass) {

		if (pass == 0) {
			return side != facing ? TETextures.AUTOMATON_SIDE : redstoneControlOrDisable() ? TETextures.AUTOMATON_ACTIVE[getType()] : TETextures.AUTOMATON_FACE[getType()];
		} else if (side < 6) {
			return TETextures.CONFIG[sideConfig.sideTex[sideCache[side]]];
		}
		return TETextures.AUTOMATON_SIDE;
	}

}
