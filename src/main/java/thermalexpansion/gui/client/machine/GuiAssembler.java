package thermalexpansion.gui.client.machine;

import cofh.core.CoFHProps;
import cofh.gui.GuiBaseAdv;
import cofh.gui.element.ElementBase;
import cofh.gui.element.ElementEnergyStored;
import cofh.gui.element.ElementFluidTank;
import cofh.gui.element.TabConfiguration;
import cofh.gui.element.TabEnergy;
import cofh.gui.element.TabInfo;
import cofh.gui.element.TabRedstone;
import cofh.gui.element.TabTutorial;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.block.machine.TileAssembler;
import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.ISetSchematic;
import thermalexpansion.gui.container.machine.ContainerAssembler;
import thermalexpansion.gui.element.ElementSlotOverlay;
import thermalexpansion.gui.element.TabSchematic;

public class GuiAssembler extends GuiBaseAdv {

	static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_MACHINE + "Assembler.png");
	static final String INFO = "Crafts recipes saved in Schematics.\n\nUses 20 Redstone Flux per Item.\n\nNo, it won't craft that.";

	TileAssembler myTile;

	ElementBase slotInput;
	ElementBase slotOutput;
	ElementBase slotInputFluid;

	public GuiAssembler(InventoryPlayer inventory, TileEntity theTile) {

		super(new ContainerAssembler(inventory, theTile), TEXTURE);
		myTile = (TileAssembler) theTile;
		name = myTile.getInventoryName();
		ySize = 205;
	}

	@Override
	public void initGui() {

		super.initGui();

		slotInput = addElement(new ElementSlotOverlay(this, 8, 74).setSlotInfo(0, 4, 2));
		slotInputFluid = addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(0, 3, 2));
		slotOutput = addElement(new ElementSlotOverlay(this, 112, 31).setSlotInfo(3, 1, 2));

		addElement(new ElementEnergyStored(this, 8, 8, myTile.getEnergyStorage()));
		addElement(new ElementFluidTank(this, 152, 9, myTile.getTank()).setGauge(1));

		addTab(new TabEnergy(this, myTile, false));
		addTab(new TabRedstone(this, myTile));
		addTab(new TabConfiguration(this, myTile));
		addTab(new TabInfo(this, INFO));
		addTab(new TabTutorial(this, CoFHProps.tutorialTabRedstone + "\n\n" + CoFHProps.tutorialTabConfiguration + "\n\n" + CoFHProps.tutorialTabFluxRequired));
		addTab(new TabSchematic(this, (ISetSchematic) inventorySlots));

		ISetSchematic schematicSlots = (ISetSchematic) inventorySlots;
		for (int i = 0; i < schematicSlots.getCraftingSlots().length; i++) {
			schematicSlots.getCraftingSlots()[i].xDisplayPosition = -guiLeft - 16;
			schematicSlots.getCraftingSlots()[i].yDisplayPosition = -guiTop - 16;
		}
		schematicSlots.getResultSlot().xDisplayPosition = -guiLeft - 16;
		schematicSlots.getResultSlot().yDisplayPosition = -guiTop - 16;
	}

	@Override
	protected void updateElementInformation() {

		slotInput.setVisible(myTile.hasSide(1));
		slotInputFluid.setVisible(myTile.hasSide(1));
		slotOutput.setVisible(myTile.hasSide(2));
	}

}
