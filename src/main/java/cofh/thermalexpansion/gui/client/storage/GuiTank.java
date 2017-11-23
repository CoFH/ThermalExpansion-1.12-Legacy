package cofh.thermalexpansion.gui.client.storage;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementButton;
import cofh.core.gui.element.ElementFluidTank;
import cofh.core.gui.element.tab.TabBase;
import cofh.core.gui.element.tab.TabInfo;
import cofh.core.gui.element.tab.TabRedstoneControl;
import cofh.core.gui.element.tab.TabSecurity;
import cofh.core.util.helpers.SecurityHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.storage.TileTank;
import cofh.thermalexpansion.gui.container.ContainerTEBase;
import cofh.thermalexpansion.init.TEProps;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.UUID;

public class GuiTank extends GuiContainerCore {

	public static final String TEX_PATH = TEProps.PATH_GUI_STORAGE + "tank.png";
	public static final ResourceLocation TEXTURE = new ResourceLocation(TEX_PATH);

	protected TileTank baseTile;
	protected UUID playerName;

	protected TabBase redstoneTab;
	protected TabBase securityTab;

	private ElementButton output;
	private ElementButton lock;

	public GuiTank(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerTEBase(inventory, tile), TEXTURE);

		baseTile = (TileTank) tile;
		name = baseTile.getName();
		playerName = SecurityHelper.getID(inventory.player);

		generateInfo("tab.thermalexpansion.storage.tank");
	}

	@Override
	public void initGui() {

		super.initGui();

		// Right Side
		redstoneTab = addTab(new TabRedstoneControl(this, baseTile));

		// Left Side
		securityTab = addTab(new TabSecurity(this, baseTile, playerName));
		securityTab.setVisible(baseTile.enableSecurity() && baseTile.isSecured());

		if (!myInfo.isEmpty()) {
			addTab(new TabInfo(this, myInfo));
		}
		addElement(new ElementFluidTank(this, 80, 19, baseTile.getTank()).setMedium().setGauge(0).setAlwaysShow(true).setInfinite(baseTile.isCreative));

		output = new ElementButton(this, 35, 41, "Output", 176, 0, 176, 16, 176, 32, 16, 16, TEX_PATH).setToolTipLocalized(true);
		lock = new ElementButton(this, 125, 41, "Lock", 176, 48, 176, 64, 176, 80, 16, 16, TEX_PATH).setToolTipLocalized(true);

		addElement(output);
		addElement(lock);
	}

	@Override
	public void updateScreen() {

		super.updateScreen();

		if (!baseTile.canAccess()) {
			this.mc.player.closeScreen();
		}
		redstoneTab.setVisible(baseTile.hasRedstoneControl());
		securityTab.setVisible(baseTile.enableSecurity() && baseTile.isSecured());
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		if (baseTile.enableAutoOutput) {
			output.setToolTip(StringHelper.localize("gui.cofh.transferOutEnabled"));
			output.setSheetX(176);
			output.setHoverX(176);
		} else {
			output.setToolTip(StringHelper.localize("gui.cofh.transferOutDisabled"));
			output.setSheetX(192);
			output.setHoverX(192);
		}

		if (baseTile.getTankFluid() == null) {
			lock.setDisabled();
		} else {
			lock.setActive();
		}
		if (baseTile.isLocked()) {
			String color = StringHelper.WHITE;
			FluidStack fluid = baseTile.getTankFluid();
			if (fluid.getFluid().getRarity() == EnumRarity.UNCOMMON) {
				color = StringHelper.YELLOW;
			} else if (fluid.getFluid().getRarity() == EnumRarity.RARE) {
				color = StringHelper.BRIGHT_BLUE;
			} else if (fluid.getFluid().getRarity() == EnumRarity.EPIC) {
				color = StringHelper.PINK;
			}
			lock.setToolTip(StringHelper.localize("info.cofh.locked") + ": " + color + StringHelper.localize(fluid.getFluid().getLocalizedName(fluid)) + StringHelper.END);
			lock.setSheetX(176);
			lock.setHoverX(176);
		} else {
			lock.setToolTip(StringHelper.localize("info.cofh.unlocked"));
			lock.setSheetX(192);
			lock.setHoverX(192);
		}
	}

	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton) {

		if (buttonName.equalsIgnoreCase("Output")) {
			if (baseTile.setTransferOut(!baseTile.getTransferOut())) {
				playClickSound(baseTile.getTransferOut() ? 0.8F : 0.4F);
			}
		} else if (buttonName.equalsIgnoreCase("Lock")) {
			baseTile.toggleLock();
			playClickSound(baseTile.isLocked() ? 0.8F : 0.4F);

			baseTile.sendModePacket();
			baseTile.toggleLock();
		}
	}

}
