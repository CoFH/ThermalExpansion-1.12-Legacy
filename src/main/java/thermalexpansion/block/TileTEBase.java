package thermalexpansion.block;

import cofh.block.TileCoFHBase;

import net.minecraft.nbt.NBTTagCompound;

import thermalexpansion.ThermalExpansion;

public abstract class TileTEBase extends TileCoFHBase {

	public static class EnergyConfig {

		public int minPower = 8;
		public int maxPower = 80;
		public int maxEnergy = 40000;
		public int minPowerLevel = 9 * maxEnergy / 10;
		public int maxPowerLevel = 1 * maxEnergy / 10;
		public int energyRamp = minPowerLevel / maxPower;

		public EnergyConfig() {

		}

		public EnergyConfig(EnergyConfig config) {

			this.minPower = config.minPower;
			this.maxPower = config.maxPower;
			this.maxEnergy = config.maxEnergy;
			this.minPowerLevel = config.minPowerLevel;
			this.maxPowerLevel = config.maxPowerLevel;
			this.energyRamp = config.energyRamp;
		}

		public EnergyConfig copy() {

			return new EnergyConfig(this);
		}

		public boolean setParams(int minPower, int maxPower, int maxEnergy) {

			if (minPower <= 0 || maxPower <= 0 || maxEnergy <= 0) {
				return false;
			}
			this.minPower = minPower;
			this.maxPower = maxPower;
			this.maxEnergy = maxEnergy;
			this.maxPowerLevel = maxEnergy * 8 / 10;
			this.energyRamp = maxPowerLevel / maxPower;
			this.minPowerLevel = minPower * energyRamp;

			return true;
		}

		public boolean setParamsPower(int maxPower) {

			return setParams(maxPower / 4, maxPower, maxPower * 1200);
		}

		public boolean setParamsEnergy(int maxEnergy) {

			return setParams(maxEnergy / 4800, maxEnergy / 1200, maxEnergy);
		}

	}

	/* NBT METHODS */
	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);
		nbt.setString("Version", ThermalExpansion.version);
	}

}
