package cofh.thermalexpansion.block.plate;

import cofh.lib.util.helpers.EnergyHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class TilePlateCharger extends TilePlatePoweredBase {

	protected static final int CHARGE_RATE = 1000;

	protected long worldTime;
	protected int chargeLeft;

	public TilePlateCharger() {

		super(BlockPlate.Types.POWERED_SIGNAL, 32000);
	}

	@Override
	public void onEntityCollidedWithBlock(Entity theEntity) {

		if (!(theEntity instanceof EntityLivingBase)) {
			return;
		}

		if (worldTime != theEntity.worldObj.getTotalWorldTime()) {
			worldTime = theEntity.worldObj.getTotalWorldTime();
			chargeLeft = Math.min(CHARGE_RATE, storage.getEnergyStored());
		} else if (chargeLeft <= 0) {
			return;
		}

		EntityLivingBase ent = (EntityLivingBase) theEntity;

		for (int i = 0; i < 5; ++i) {
			int ins = EnergyHelper.insertEnergyIntoContainer(ent.getEquipmentInSlot(i), chargeLeft, false);
			chargeLeft -= storage.extractEnergy(ins, false);
		}
	}

}
