package ftdpeb.pojo;

public class PowerOutage {

	double initialMin;
	double finalMin;
	int day;
	public PowerOutage(double initialMin, double finalMin, int day) {
		this.initialMin = initialMin;
		this.finalMin = finalMin;
		this.day = day;
	}
	public double getInitialMin() {
		return initialMin;
	}
	public double getFinalMin() {
		return finalMin;
	}
	public int getDay() {
		return day;
	}
	
}
