package ftdpeb.pojo;

public class BatteryModule {

	double capacity;
	double cost;
	
	public BatteryModule(double capacity, double weight, double cost) {
		super();
		this.capacity = capacity;
		this.cost = cost;
	}

	public double getCapacity() {
		return capacity;
	}

	public double getCost() {
		return cost;
	}
	
	
}
