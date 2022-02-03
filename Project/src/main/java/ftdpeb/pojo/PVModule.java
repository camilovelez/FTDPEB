package ftdpeb.pojo;

public class PVModule {
	
	double sqrM;
	double efficiency;
	double cost;
	double peakPower;
	
	public PVModule(double sqrM, double efficiency, double peakPower, double cost) {
		this.sqrM = sqrM;
		this.efficiency = efficiency;
		this.peakPower = peakPower;
		this.cost = cost;
	}

	public double getSqrM() {
		return sqrM;
	}

	public double getEfficiency() {
		return efficiency;
	}

	public double getCost() {
		return cost;
	}
	
	public double getPeakPower() {
		return peakPower;
	}
	
	
}
