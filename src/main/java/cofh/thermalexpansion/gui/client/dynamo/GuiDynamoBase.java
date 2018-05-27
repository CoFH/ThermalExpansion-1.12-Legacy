package cofh.thermalexpansion.gui.client.dynamo;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.container.IAugmentableContainer;
import cofh.core.gui.element.ElementDualScaled;
import cofh.core.gui.element.ElementEnergyStored;
import cofh.core.gui.element.tab.*;
import cofh.core.util.helpers.SecurityHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.block.dynamo.TileDynamoBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

public abstract class GuiDynamoBase extends GuiContainerCore {

	protected TileDynamoBase baseTile;
	protected UUID playerName;

	protected TabBase energyTab;
	protected TabSteam steamTab;

	protected TabBase augmentTab;
	protected TabBase redstoneTab;
	protected TabBase securityTab;

	protected ElementDualScaled duration;

	public GuiDynamoBase(Container container, TileEntity tile, EntityPlayer player, ResourceLocation texture) {

		super(container, texture);

		baseTile = (TileDynamoBase) tile;
		name = baseTile.getName();
		playerName = SecurityHelper.getID(player);
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementEnergyStored(this, 80, 18, baseTile.getEnergyStorage()));

		// Right Side
		steamTab = (TabSteam) addTab(new TabSteam(this, baseTile, baseTile.isSteamProducer()));
		steamTab.setVisible(baseTile.showSteamTab());

		augmentTab = addTab(new TabAugment(this, (IAugmentableContainer) inventorySlots));

		redstoneTab = addTab(new TabRedstoneControl(this, baseTile));
		redstoneTab.setVisible(baseTile.hasRedstoneControl());

		// Left Side
		securityTab = addTab(new TabSecurity(this, baseTile, playerName));
		securityTab.setVisible(baseTile.enableSecurity() && baseTile.isSecured());

		energyTab = addTab(new TabEnergy(this, baseTile, true).displayStored(!baseTile.smallStorage()));
		energyTab.setVisible(baseTile.showEnergyTab());

		if (!myInfo.isEmpty()) {
			addTab(new TabInfo(this, myInfo + "\n\n" + StringHelper.localize("tab.thermalexpansion.dynamo.0")));
		}
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void onGuiClosed() {

		MinecraftForge.EVENT_BUS.unregister(this);
		super.onGuiClosed();
	}

	@Override
	public void updateScreen() {

		super.updateScreen();

		if (!baseTile.canAccess()) {
			this.mc.player.closeScreen();
		}
		redstoneTab.setVisible(baseTile.hasRedstoneControl());
		securityTab.setVisible(baseTile.enableSecurity() && baseTile.isSecured());
		energyTab.setVisible(baseTile.showEnergyTab());
		steamTab.setVisible(baseTile.showSteamTab());
		steamTab.setProducer(baseTile.isSteamProducer());
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		duration.setQuantity(baseTile.getScaledDuration(SPEED));
	}

	@SubscribeEvent
	public void handleItemTooltipEvent(ItemTooltipEvent event) {

		if (baseTile.isSteamProducer()) {
			return;
		}
		int energy = baseTile.getFuelEnergy(event.getItemStack());
		if (energy > 0) {
			event.getToolTip().add(StringHelper.BRIGHT_GREEN + StringHelper.localize("info.cofh.energy") + ": " + StringHelper.getScaledNumber(energy) + " RF" + StringHelper.END);
		}
	}

}
