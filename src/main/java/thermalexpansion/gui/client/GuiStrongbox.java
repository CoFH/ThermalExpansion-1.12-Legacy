package thermalexpansion.gui.client;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import thermalexpansion.block.strongbox.BlockStrongbox;
import thermalexpansion.block.strongbox.TileStrongbox;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.ContainerStrongbox;
import cofh.gui.GuiBaseAdv;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabSecurity;

public class GuiStrongbox extends GuiBaseAdv {

	static final ResourceLocation[] TEXTURE = { new ResourceLocation(TEProps.PATH_GUI_STRONGBOX + "StrongboxCreative.png"),
			new ResourceLocation(TEProps.PATH_GUI_STRONGBOX + "StrongboxBasic.png"),
			new ResourceLocation(TEProps.PATH_GUI_STRONGBOX + "StrongboxHardened.png"),
			new ResourceLocation(TEProps.PATH_GUI_STRONGBOX + "StrongboxReinforced.png"),
			new ResourceLocation(TEProps.PATH_GUI_STRONGBOX + "StrongboxResonant.png") };

	static final String INFO = "Stores things securely!\n\nCan store things that store things so you can store things while you store things.\n\nWrench while sneaking to dismantle.";
	static final String INFO_CREATIVE = "Stores something securely!\n\nAllows you to pull out infinite amounts of the item stored inside.";

	TileStrongbox myTile;
	String playerName;

	public GuiStrongbox(InventoryPlayer inventory, TileEntity entity) {

		super(new ContainerStrongbox(inventory, entity), TEXTURE[((TileStrongbox) entity).type]);
		myTile = (TileStrongbox) entity;
		name = myTile.getInventoryName();
		playerName = inventory.player.username;

		switch (BlockStrongbox.Types.values()[myTile.type]) {
		case HARDENED:
			ySize = 184;
			break;
		case REINFORCED:
			ySize = 220;
			break;
		case RESONANT:
			xSize = 230;
			ySize = 220;
			break;
		default:
			ySize = 148;
		}
	}

	@Override
	public void initGui() {

		super.initGui();

		if (myTile.type == BlockStrongbox.Types.CREATIVE.ordinal()) {
			addTab(new TabInfo(this, INFO_CREATIVE));
		} else {
			addTab(new TabInfo(this, INFO, 1));
		}
		if (TileStrongbox.enableSecurity) {
			addTab(new TabSecurity(this, myTile, playerName));
		}
	}

	@Override
	public void updateScreen() {

		super.updateScreen();

		if (!myTile.canAccess) {
			this.mc.thePlayer.closeScreen();
		}
	}

}
