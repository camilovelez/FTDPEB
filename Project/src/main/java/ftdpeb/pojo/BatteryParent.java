package ftdpeb.pojo;

public class BatteryParent {
	
	protected double totalCapacity;
	protected double energyLevel = 0;
	protected double totalCost;
	protected int amountOfModules;
	protected double minEnergyLevel;	
	
	public double getTotalCapacity() {
		return totalCapacity;
	}

	public void setTotalCapacity(double totalCapacity) {
		this.totalCapacity = totalCapacity;
	}
	
	public double getEnergyLevel() {
		return energyLevel;
	}
	
	public double getMinEnergyLevel() {
		return minEnergyLevel;
	}

	public void setEnergyLevel(double energy) {
		this.energyLevel = energy;
	}
	
	public double getTotalCost() {
		return totalCost;
	}

	public int getAmountOfModules() {
		return amountOfModules;
	}

	public void setAmountOfModules(int amountOfModules) {
		this.amountOfModules = amountOfModules;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public void setMinEnergyLevel(double minEnergyLevel) {
		this.minEnergyLevel = minEnergyLevel;
	}
	
	public double getCurrentAvailableEnergy() {
		return this.energyLevel - this.minEnergyLevel;
	}	
	
}
