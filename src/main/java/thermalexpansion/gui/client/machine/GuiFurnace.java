package thermalexpansion.gui.client.machine;

import cofh.core.CoFHProps;
import cofh.gui.GuiBaseAdv;
import cofh.gui.container.IAugmentableContainer;
import cofh.gui.element.ElementBase;
import cofh.gui.element.ElementDualScaled;
import cofh.gui.element.ElementEnergyStored;
import cofh.gui.element.TabAugment;
import cofh.gui.element.TabBase;
import cofh.gui.element.TabConfiguration;
import cofh.gui.element.TabEnergy;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabRedstone;
import cofh.gui.element.TabTutorial;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.block.machine.TileFurnace;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.machine.ContainerFurnace;
import thermalexpansion.gui.element.ElementSlotOverlay;

public class GuiFurnace extends GuiBaseAdv {

	public static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_MACHINE + "Furnace.png");
	static final String INFO = "Smelt things using Redstone Flux.\n\nUses very little energy to cook food.\n\nOm nom nom.";

	TileFurnace myTile;

	ElementBase slotInput;
	ElementBase slotOutput;
	ElementDualScaled progress;
	ElementDualScaled speed;

	TabBase redstoneTab;
	TabBase configTab;

	public GuiFurnace(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerFurnace(inventory, theTile), TEXTURE);
		myTile = (TileFurnace) theTile;
		name = myTile.getInventoryName();
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 56, 26).setSlotInfo(0, 0, 2));
		slotOutput = addElement(new ElementSlotOverlay(this, 112, 31).setSlotInfo(3, 1, 2));

		addElement(new ElementEnergyStored(this, 8, 8, myTile.getEnergyStorage()));
		progress = (ElementDualScaled) addElement(new ElementDualScaled(this, 79, 34).setMode(1).setSize(24, 16).setTexture(TEX_ARROW_RIGHT, 48, 16));
		speed = (ElementDualScaled) addElement(new ElementDualScaled(this, 56, 44).setSize(16, 16).setTexture(TEX_FLAME, 32, 16));

		addTab(new TabEnergy(this, myTile, false));
		redstoneTab = addTab(new TabRedstone(this, myTile));
		configTab = addTab(new TabConfiguration(this, myTile));

		addTab(new TabInfo(this, INFO));
		addTab(new TabTutorial(this, CoFHProps.tutorialTabRedstone + "\n\n" + CoFHProps.tutorialTabConfiguration + "\n\n" + CoFHProps.tutorialTabFluxRequired));
		addTab(new TabAugment(this, (IAugmentableContainer) inventorySlots));
	}

	@Override
	protected void updateElementInformation() {

		slotInput.setVisible(myTile.hasSide(1));
		slotOutput.setVisible(myTile.hasSide(2));

		redstoneTab.setVisible(myTile.augmentRSControl);
		configTab.setVisible(myTile.augmentReconfigSides);

		progress.setQuantity(myTile.getScaledProgress(PROGRESS));
		speed.setQuantity(myTile.getScaledSpeed(SPEED));
	}

}
