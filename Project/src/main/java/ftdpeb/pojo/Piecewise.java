package ftdpeb.pojo;

public class Piecewise {
	double[] breakPoints;
	double[] chargingPowers;
	
	public Piecewise(double[] breakPoints, double[] chargingPowers) {
		this.breakPoints = breakPoints;
		this.chargingPowers = chargingPowers;
	}
	
	public double[] getBreakPoints() {
		return breakPoints;
	}


	public double[] getChargingPowers() {
		return chargingPowers;
	}
	
	public void SetPowersAccordingToTimeIntervals(double fractionOfHour) {
		int len = chargingPowers.length;
		for(int i = 0; i < len; i++) {
			chargingPowers[i] *= fractionOfHour;
		}
	}
}
