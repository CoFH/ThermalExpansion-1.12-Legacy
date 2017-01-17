package cofh.thermalexpansion.gui.client.machine;

import cofh.lib.gui.element.ElementBase;
import cofh.lib.gui.element.ElementFluidTank;
import cofh.thermalexpansion.core.TEProps;
import cofh.thermalexpansion.gui.client.GuiAugmentableBase;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiAccumulator extends GuiAugmentableBase {

	static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_MACHINE + "accumulator.png");

	ElementBase slotOutput;

	public GuiAccumulator(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerTEBase(inventory, tile), tile, inventory.player, TEXTURE);

		generateInfo("tab.thermalexpansion.machine.accumulator", 3);
	}

	@Override
	public void initGui() {

		super.initGui();

		slotOutput = addElement(new ElementSlotOverlay(this, 152, 9).setSlotInfo(3, 3, 2));

		addElement(new ElementFluidTank(this, 152, 9, myTile.getTank()).setAlwaysShow(true));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		slotOutput.setVisible(myTile.hasSide(1));
	}

}
