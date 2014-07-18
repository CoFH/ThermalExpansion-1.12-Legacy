package thermalexpansion.gui.client.dynamo;

import cofh.gui.GuiBaseAdv;
import cofh.gui.container.IAugmentableContainer;
import cofh.gui.element.ElementEnergyStored;
import cofh.gui.element.TabAugment;
import cofh.gui.element.TabBase;
import cofh.gui.element.TabEnergy;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabRedstone;
import cofh.gui.element.TabSecurity;
import cofh.gui.element.TabTutorial;
import cofh.util.StringHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import thermalexpansion.block.dynamo.TileDynamoBase;

public abstract class GuiDynamoBase extends GuiBaseAdv {

	protected TileDynamoBase myTile;
	protected String playerName;

	public String myInfo = "";
	public String myTutorial = StringHelper.tutorialTabAugment();

	protected TabBase redstoneTab;

	public GuiDynamoBase(Container container, TileEntity tile, EntityPlayer player, ResourceLocation texture) {

		super(container, texture);

		myTile = (TileDynamoBase) tile;
		name = myTile.getInventoryName();
		playerName = player.getCommandSenderName();

		if (myTile.augmentRedstoneControl) {
			myTutorial += "\n\n" + StringHelper.tutorialTabRedstone();
		}
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementEnergyStored(this, 80, 18, myTile.getEnergyStorage()));

		redstoneTab = addTab(new TabRedstone(this, myTile));

		if (myTile.getMaxEnergyStored(ForgeDirection.UNKNOWN) > 0) {
			addTab(new TabEnergy(this, myTile, true));
		}
		addTab(new TabInfo(this, myInfo + "\n\n" + StringHelper.localize("tab.thermalexpansion.dynamo")));
		addTab(new TabTutorial(this, myTutorial));

		addTab(new TabAugment(this, (IAugmentableContainer) inventorySlots));
		if (myTile.enableSecurity() && myTile.isSecured()) {
			addTab(new TabSecurity(this, myTile, playerName));
		}
	}

	@Override
	public void updateScreen() {

		super.updateScreen();

		if (!myTile.canAccess()) {
			this.mc.thePlayer.closeScreen();
		}
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		redstoneTab.setVisible(myTile.augmentRedstoneControl);
	}

}
