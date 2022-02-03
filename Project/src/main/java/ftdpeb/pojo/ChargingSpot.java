package ftdpeb.pojo;

public class ChargingSpot {
	
	Piecewise piecewise;
	int protocol;
	
	public ChargingSpot(Piecewise piecewise, int protocol) {
		this.piecewise = piecewise;
		this.protocol = protocol;
	}

	public Piecewise getPiecewise() {
		return piecewise;
	}

	public int getProtocol() {
		return protocol;
	}
	public ChargingSpot() {}
	
	public void SetPowersAccordingToTimeIntervals(double fractionOfHour) {
		this.piecewise.SetPowersAccordingToTimeIntervals(fractionOfHour);
	}
	
}
