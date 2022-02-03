package ftdpeb.generalClasses;

import ftdpeb.pojo.BatteryParent;
import ftdpeb.singleton.GlobalData;

public class ESS extends BatteryParent{

	
	double nominalEnergyLevel;
	boolean charging = false;
	
	public ESS(ESS originalESS) {
		this.totalCapacity = originalESS.totalCapacity;
		this.energyLevel = originalESS.energyLevel;
		this.amountOfModules = originalESS.amountOfModules;
		this.totalCost = originalESS.totalCost;
		this.nominalEnergyLevel = GlobalData.essNominalSoC * totalCapacity;
		this.minEnergyLevel = GlobalData.essMinSoC * totalCapacity; 
	}
	
	public ESS(double estimatedCapacity) {
		super();
		this.amountOfModules = (int) Math.round(estimatedCapacity / GlobalData.essModule.getCapacity());
		this.totalCapacity = GlobalData.essModule.getCapacity() * amountOfModules;
		this.energyLevel = this.totalCapacity;
		this.totalCost = GlobalData.essModule.getCost() * amountOfModules;
		this.nominalEnergyLevel = GlobalData.essNominalSoC * this.totalCapacity;
		this.minEnergyLevel = GlobalData.essMinSoC * this.totalCapacity;
	}
	
	public ESS(double estimatedCapacity, boolean slowChargingCS) {
		super();
		this.amountOfModules = (int) Math.ceil(estimatedCapacity / GlobalData.essModule.getCapacity());
		this.totalCapacity = GlobalData.essModule.getCapacity() * amountOfModules;
		this.energyLevel = this.totalCapacity;
		this.totalCost = GlobalData.essModule.getCost() * amountOfModules;
		this.nominalEnergyLevel = GlobalData.essNominalSoC * this.totalCapacity;
		this.minEnergyLevel = GlobalData.essMinSoC * this.totalCapacity;
	}


	public double getNominalEnergyLevel() {
		return nominalEnergyLevel;
	}
	
	public int getAmountOfModules() {
		return amountOfModules;
	}
	
	public void addEnergy(double energy) {
		this.energyLevel += energy;
		this.energyLevel = Math.min(this.totalCapacity, this.energyLevel);
	}
	
	public void substractEnergy(double energy) {
		this.energyLevel -= energy;
	}
	
	public double[] EnergyManagement(int power, double generatedEnergy, double demandedEnergy) {
		double[] sol = new double[2];
		sol[1] = 1;
		if(generatedEnergy >= demandedEnergy) {
			sol[0] = demandedEnergy;
			this.addEnergy(generatedEnergy - demandedEnergy);
			CheckIfItCharged();
			return sol;
		}
		if(this.charging) {
			sol[0] = generatedEnergy;
			sol[1] = generatedEnergy / demandedEnergy;
		}else {
			double suppliableEnergy = this.getCurrentAvailableEnergy() + generatedEnergy;
			double suppliedEnergy = Math.min(demandedEnergy, suppliableEnergy);
			this.substractEnergy(suppliedEnergy - generatedEnergy);
			sol[0] = suppliedEnergy;
			sol[1] = suppliedEnergy / demandedEnergy;
			if(this.energyLevel <= this.minEnergyLevel) {
				this.charging = true;			
			}
		}
		return sol;
	}
	
	public void CheckIfItCharged() {
		if(this.energyLevel >= this.nominalEnergyLevel) {
			this.charging = false;
		}
	}
}
