package ftdpeb.pojo;

public class TripSchedule {
	int day;
	String trip;
	double initialEnergy;
	double energyDelta;
	double finalEnergy;
	double initialTime;
	double taskTime;
	double finalTime;
	int nodeId;
	int passengers;
	double pvEnergy;
	String task;
	String journey;
	
	
	
	public TripSchedule(int day, String trip, double initialEnergy, double energyDelta, double finalEnergy,
			double initialTime, double taskTime, double finalTime, int nodeId, int passengers, double pvEnergy
			, String task, String journey) {
		super();
		this.day = day;
		this.trip = trip;
		this.initialEnergy = initialEnergy;
		this.energyDelta = energyDelta;
		this.finalEnergy = finalEnergy;
		this.initialTime = initialTime;
		this.taskTime = taskTime;
		this.finalTime = finalTime;
		this.nodeId = nodeId;
		this.passengers = passengers;
		this.pvEnergy = pvEnergy;
		this.task = task;
		this.journey = journey;
	}
	
	public int getDay() {
		return day;
	}
	public String getTrip() {
		return trip;
	}
	public double getInitialEnergy() {
		return initialEnergy;
	}
	public double getEnergyDelta() {
		return energyDelta;
	}
	public double getFinalEnergy() {
		return finalEnergy;
	}
	public double getInitialTime() {
		return initialTime;
	}
	public double getTaskTime() {
		return taskTime;
	}
	public double getFinalTime() {
		return finalTime;
	}
	public int getNodeId() {
		return nodeId;
	}
	public int getPassengers() {
		return passengers;
	}

	public double getPvEnergy() {
		return pvEnergy;
	}

	public String getTask() {
		return task;
	}

	public String getJourney() {
		return journey;
	}
	
	
	
}
