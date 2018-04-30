package cofh.thermalexpansion.gui.client.dynamo;

import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.gui.element.ElementDualScaled;
import cofh.core.gui.element.ElementFluidTank;
import cofh.core.gui.element.ElementSimple;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.dynamo.TileDynamoMagmatic;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiDynamoMagmatic extends GuiDynamoBase {

	public static final String TEX_PATH = TEProps.PATH_GUI_DYNAMO + "magmatic.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	private TileDynamoMagmatic myTile;

	private ElementSimple tankBackground;
	private ElementFluidTank tank;

	public GuiDynamoMagmatic(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerTileAugmentable(inventory, tile), tile, inventory.player, TEXTURE);

		myInfo = StringHelper.localize("tab.thermalexpansion.dynamo.magmatic.0");

		myTile = (TileDynamoMagmatic) tile;
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementFluidTank(this, 152, 9, baseTile.getTank(0)));
		duration = (ElementDualScaled) addElement(new ElementDualScaled(this, 115, 35).setSize(16, 16).setTexture(TEX_FLAME, 32, 16));

		tankBackground = (ElementSimple) addElement(new ElementSimple(this, 7, 8).setTextureOffsets(176, 0).setSize(18, 62).setTexture(TEX_PATH, 256, 256));
		tank = (ElementFluidTank) addElement(new ElementFluidTank(this, 8, 9, baseTile.getTank(1)));

		tankBackground.setVisible(myTile.showCoolantTank());
		tank.setVisible(myTile.showCoolantTank());
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		tankBackground.setVisible(myTile.showCoolantTank());
		tank.setVisible(myTile.showCoolantTank());
	}

}
