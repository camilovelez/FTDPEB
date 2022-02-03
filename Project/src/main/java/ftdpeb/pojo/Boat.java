package ftdpeb.pojo;

public class Boat {
	
	double totalCost;
	Battery battery;

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public Battery getBattery() {
		return battery;
	}

	public void setBattery(Battery battery) {
		this.battery = new Battery(battery.getAmountOfBatteryModules());
		this.totalCost = battery.getTotalCost();
	}

	public Boat(Battery battery) {
		this.battery = new Battery(battery);
		this.totalCost = battery.getTotalCost();
	}
	
	public void CalculateCost() {
		this.totalCost = battery.getTotalCost();
	}

}
