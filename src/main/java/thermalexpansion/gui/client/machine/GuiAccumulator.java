package thermalexpansion.gui.client.machine;

import cofh.core.CoFHProps;
import cofh.gui.element.ElementBase;
import cofh.gui.element.ElementFluidTank;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.ContainerTEBase;
import thermalexpansion.gui.element.ElementSlotOverlay;

public class GuiAccumulator extends GuiMachineBase {

	static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_MACHINE + "Accumulator.png");
	static final String INFO = "Extracts water from its surroundings.\n\nPlace in a pool of water to speed this up.\n\nDon't drown.";

	ElementBase slotOutput;

	public GuiAccumulator(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerTEBase(inventory, tile), tile, inventory.player, TEXTURE);

		myInfo = INFO;
		myTutorial = CoFHProps.tutorialTabRedstone + "\n\n" + CoFHProps.tutorialTabConfiguration;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotOutput = addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(3, 3, 2));

		addElement(new ElementFluidTank(this, 152, 9, myTile.getTank()));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotOutput.setVisible(myTile.hasSide(1));
	}

}
