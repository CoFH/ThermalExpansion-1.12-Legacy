package thermalexpansion.gui.client.machine;

import cofh.gui.GuiBaseAdv;
import cofh.gui.container.IAugmentableContainer;
import cofh.gui.element.TabAugment;
import cofh.gui.element.TabBase;
import cofh.gui.element.TabConfiguration;
import cofh.gui.element.TabEnergy;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabRedstone;
import cofh.gui.element.TabSecurity;
import cofh.gui.element.TabTutorial;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import thermalexpansion.block.machine.TileMachineBase;

public abstract class GuiMachineBase extends GuiBaseAdv {

	protected TileMachineBase myTile;
	protected String playerName;

	public String myInfo = "";
	public String myTutorial = "";

	protected TabBase redstoneTab;
	protected TabBase configTab;

	public GuiMachineBase(Container container, TileEntity tile, EntityPlayer player, ResourceLocation texture) {

		super(container, texture);

		myTile = (TileMachineBase) tile;
		name = myTile.getInventoryName();
		playerName = player.getCommandSenderName();
	}

	@Override
	public void initGui() {

		super.initGui();

		redstoneTab = addTab(new TabRedstone(this, myTile));
		configTab = addTab(new TabConfiguration(this, myTile));

		if (myTile.getMaxEnergyStored(ForgeDirection.UNKNOWN) > 0) {
			addTab(new TabEnergy(this, myTile, false));
		}
		addTab(new TabInfo(this, myInfo));
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

		redstoneTab.setVisible(myTile.augmentRSControl);
		configTab.setVisible(myTile.augmentReconfigSides);
	}

}
