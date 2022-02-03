package ftdpeb.generalClasses;

import java.util.ArrayList;
import java.util.List;

import ftdpeb.pojo.*;
import ftdpeb.singleton.GlobalData;

public class Route{
	
	int id;
	List<Double> arrivalHourPerNode;
	List<Double> departureHourPerNode;
	List<Double> arrivalEnergyPerNode;
	List<Double> departureEnergyPerNode;
	Boat boat;
	double maxTotalChargingTime = 0;
	double currentCumulativeChargingTime = 0;
	double travelTime = 0;
	int trips = 0;



	boolean csAtPort = true;

	public Route(int id, Boat boat) {
		this.id = id;
		this.boat = new Boat(boat.getBattery());
		this.arrivalHourPerNode = new ArrayList<Double>();
		this.departureHourPerNode = new ArrayList<Double>();
		this.arrivalEnergyPerNode = new ArrayList<Double>();
		this.departureEnergyPerNode = new ArrayList<Double>();
	}
	
	public Route(Route originalRoute) {
		int size = originalRoute.getArrivalEnergyPerNode().size();
		this.arrivalHourPerNode = new ArrayList<Double>();
		this.departureHourPerNode = new ArrayList<Double>();
		this.arrivalEnergyPerNode = new ArrayList<Double>();
		this.departureEnergyPerNode = new ArrayList<Double>();
		for(int i = 0; i < size; i++) {
			this.arrivalEnergyPerNode.set(i, originalRoute.arrivalHourPerNode.get(i));
			this.departureHourPerNode.set(i, originalRoute.departureHourPerNode.get(i));
			this.arrivalEnergyPerNode.set(i, originalRoute.arrivalEnergyPerNode.get(i));
			this.departureEnergyPerNode.set(i, originalRoute.departureEnergyPerNode.get(i));
		}

		this.maxTotalChargingTime = originalRoute.maxTotalChargingTime;
		this.currentCumulativeChargingTime = 0;
		this.id = originalRoute.id;
		this.boat = new Boat(originalRoute.getBoat().getBattery());
		this.currentCumulativeChargingTime = originalRoute.currentCumulativeChargingTime;
		this.travelTime = originalRoute.getTravelTime();
		this.csAtPort = originalRoute.getCsAtport();
		this.trips = originalRoute.getTrips();
	}
	
	public void ClearLists() {
		this.arrivalEnergyPerNode.clear();
		this.arrivalHourPerNode.clear();
		this.departureEnergyPerNode.clear();
		this.departureHourPerNode.clear();
	}

	public double getTravelTime() {
		return travelTime;
	}

	public void setTravelTime(double travelTime) {
		this.travelTime = travelTime;
	}

	
	public void setId(int id) {
		this.id = id;
	}
	
	public double getCurrentCumulativeChargingTime() {
		return currentCumulativeChargingTime;
	}
	
	public void setCurrentCumulativeChargingTime(double time) {
		this.currentCumulativeChargingTime = time;
	}

	public void addCurrentCumulativeChargingTime(double chargingTime) {
		this.currentCumulativeChargingTime += chargingTime;
	}

	public double getMaxTotalChargingTime() {
		return maxTotalChargingTime;
	}
	
	public void setMaxTotalChargingTime(double time) {
		this.maxTotalChargingTime = time;
	}

	public List<Double>  getArrivalHourPerNode() {
		return arrivalHourPerNode;
	}

	public void setArrivalHourPerNode(int index, double hour) {
		this.arrivalHourPerNode.set(index, hour);
	}

	public void addArrivalHourPerNode(double hour) {
		this.arrivalHourPerNode.add(hour);
	}


	public List<Double>  getDepartureHourPerNode() {
		return departureHourPerNode;
	}
	
	public void setDepartureHourPerNode(int index, double hour) {
		this.departureHourPerNode.set(index, hour);
	}
	
	public void addDepartureHourPerNode(double hour) {
		this.departureHourPerNode.add(hour);
	}



	public List<Double>  getArrivalEnergyPerNode() {
		return arrivalEnergyPerNode;
	}
	
	public void setArrivalEnergyPerNode(int index, double energy) {
		this.arrivalEnergyPerNode.set(index, energy);
	}
	
	public void addArrivalEnergyPerNode(double energy) {
		this.arrivalEnergyPerNode.add(energy);
	}



	public List<Double> getDepartureEnergyPerNode() {
		return departureEnergyPerNode;
	}
	
	public void setDepartureEnergyPerNode(int index, double energy) {
		this.departureEnergyPerNode.set(index, energy);
	}
	
	public void addDepartureEnergyPerNode(double energy) {
		this.departureEnergyPerNode.add(energy);
	}

	public int getId() {
		return id;
	}

	public Boat getBoat() {
		return boat;
	}
	
	public boolean getCsAtport() {
		return csAtPort;
	}

	public void setCsAtport(boolean csAtport) {
		this.csAtPort = csAtport;
	}
	
	public int getTrips() {
		return trips;
	}

	public void setTrips(int trips) {
		this.trips = trips;
	}
	
	

	

	public double ConsumptionBetweenNodes(int initialNode, int finalNode, double[][] consumptions) {
		double totalConsumption = 0;
		int currentNode = initialNode;
		int nextNode;
		while (currentNode != finalNode) {
			nextNode = GlobalData.nodesToVisit[currentNode + 1];
			totalConsumption += consumptions[currentNode][nextNode];
			currentNode = nextNode;
		}
		
		return totalConsumption;
	}
	

}
