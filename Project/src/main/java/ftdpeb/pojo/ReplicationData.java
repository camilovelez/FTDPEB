package ftdpeb.pojo;

public class ReplicationData {

	double averageCost = 0;
	double sdCost = 0;
	double averageFeasibleDays = 0;
	double sdFeasibleDays = 0;
	int replications = 0;
	double cost = 0; 
	double feasibleDays = 0;
	
	public double getAverageCost() {
		return averageCost;
	}
	public double getSdCost() {
		return sdCost;
	}
	public double getAverageFeasibleDays() {
		return averageFeasibleDays;
	}
	public double getSdFeasibleDays() {
		return sdFeasibleDays;
	}
	public int getReplications() {
		return replications;
	}
	
	public double getCost() {
		return cost;
	}
	public double getFeasibleDays() {
		return feasibleDays;
	}

	public ReplicationData(double averageCost, double sdCost, double averageFeasibleDays,
			double sdFeasibleDays, int replications) {

		this.averageCost = averageCost;
		this.sdCost = sdCost;
		this.averageFeasibleDays = averageFeasibleDays;
		this.sdFeasibleDays = sdFeasibleDays;
		this.replications = replications;
	}
	public ReplicationData(double cost, double feasibleDays, int replications) {

		this.cost = cost;
		this.feasibleDays = feasibleDays;
		this.replications = replications;
	}
	
	
	
}
