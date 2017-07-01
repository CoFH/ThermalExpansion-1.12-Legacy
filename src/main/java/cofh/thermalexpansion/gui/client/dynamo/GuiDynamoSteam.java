package cofh.thermalexpansion.gui.client.dynamo;

import cofh.core.gui.element.ElementDualScaled;
import cofh.core.gui.element.ElementFluidTank;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.gui.container.dynamo.ContainerDynamoSteam;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiDynamoSteam extends GuiDynamoBase {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DYNAMO + "steam.png");
	public static final ResourceLocation STEAM_TEXTURE = new ResourceLocation("thermalfoundation:blocks/fluid/steam_still");

	private ElementDualScaled duration;

	public GuiDynamoSteam(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerDynamoSteam(inventory, tile), tile, inventory.player, TEXTURE);

		myInfo = StringHelper.localize("tab.thermalexpansion.dynamo.steam.0") + "\n\n" + StringHelper.localize("tab.thermalexpansion.dynamo.steam.1");
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementFluidTank(this, 8, 9, baseTile.getTank(0)).setFluidTextureOverride(RenderHelper.getTexture(STEAM_TEXTURE)));
		addElement(new ElementFluidTank(this, 152, 9, baseTile.getTank(1)));
		duration = (ElementDualScaled) addElement(new ElementDualScaled(this, 115, 35).setSize(16, 16).setTexture(TEX_FLAME, 32, 16));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		duration.setQuantity(baseTile.getScaledDuration(SPEED));
	}

}
