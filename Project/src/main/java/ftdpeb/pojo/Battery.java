package ftdpeb.pojo;

import ftdpeb.singleton.GlobalData;

public class Battery extends BatteryParent{
	
	int[] lifeCyclesDueToDailyIrradiance = new int[10];
	
	public Battery(int amountOfModules) {
		super();
		this.amountOfModules = amountOfModules;
		this.totalCapacity = GlobalData.batteryModule.getCapacity() * amountOfModules;
		this.energyLevel = this.totalCapacity;	
		this.totalCost = GlobalData.batteryModule.getCost() * amountOfModules;
		this.minEnergyLevel = GlobalData.ebMinSOC * this.totalCapacity;
		
	}
	
	public Battery(Battery originalBattery) {
		super();
		this.amountOfModules = originalBattery.getAmountOfBatteryModules();
		this.totalCapacity = originalBattery.getTotalCapacity();
		this.minEnergyLevel = originalBattery.getMinEnergyLevel();
		this.energyLevel = this.totalCapacity;	
		this.totalCost = originalBattery.getTotalCost();
	}

	public Battery() {}

	public int getAmountOfBatteryModules() {
		return amountOfModules;
	}
	
	
}

