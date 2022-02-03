package ftdpeb.generalClasses;

import java.util.ArrayList;
import java.util.List;

import ftdpeb.singleton.GlobalData;

public class TripInfo {
	int passengers;
	double departureHour;
	double passengerWaitingTime;
	List<Double> passengerArrivalTimes = new ArrayList<Double>();
	
	public TripInfo(List<Double> passengerArrivalTimes) {
		this.passengers = 0;
		this.departureHour = 0;
		this.passengerWaitingTime = 0;
		for(double arrival: passengerArrivalTimes) {
			this.passengerArrivalTimes.add(arrival);
		}
	}
	
	public void SetTripInformation(double departureHour, Route route, List<CS>csList, int speed, boolean midPoint) {
		this.departureHour = departureHour;
		int maxPassengers = MaximumPassengersDueToConsumptions(route, csList, speed, midPoint);
		this.passengers = Math.min(PassengersWhoArrived(), maxPassengers);
		BoardPassengers();
	}
	
	public void SetTripInformation(double departureHour) {
		this.departureHour = departureHour;
		this.passengers = GlobalData.maxPassengers;
	}
	
	public int getPassengers() {
		return passengers;
	}

	public double getDepartureHour() {
		return departureHour;
	}

	public double getPassengerWaitingTime() {
		return passengerWaitingTime;
	}

	public void UpdateWaitingTimes(double timeWhenItFinishedCharging) {
		this.passengerWaitingTime += passengers * (timeWhenItFinishedCharging - this.departureHour);
	}
	
	public void BoardPassengers() {
		this.passengerWaitingTime = 0;
		
		for(int i = 0; i < this.passengers; i++) {
			this.passengerWaitingTime += this.departureHour - this.passengerArrivalTimes.get(0);
			this.passengerArrivalTimes.remove(0);
			
		}
			
	}
	public int MaximumPassengersDueToConsumptions(Route route, List<CS> csList, int speed, boolean midPoint) {
		int startingNode = 0;
		int lastNode = GlobalData.midNode;
		if(midPoint) {
			startingNode = GlobalData.midNode;
			lastNode = GlobalData.segments;
		}
	
		double ebAvaliableEnergy = route.getBoat().getBattery().getCurrentAvailableEnergy();
		int ebBatteryModules = route.getBoat().getBattery().getAmountOfBatteryModules();
		int maxPassengers = 1;
	
		for(int passengers = 2; passengers <= GlobalData.maxPassengers; passengers++) {
			double consumptionSinceLastCS = 0;
			for(int node = startingNode; node < lastNode; node ++) {
				consumptionSinceLastCS += GlobalData.consumptions[ebBatteryModules][speed][passengers][GlobalData.nodesToVisit[node]];
				if(consumptionSinceLastCS > ebAvaliableEnergy) {
					return maxPassengers;
				}
				int followingNodeId = GlobalData.multipleFunctions.ActualNodeId(GlobalData.nodesToVisit[node + 1]);
				CS cs = GlobalData.multipleFunctions.FindCSByID(csList, followingNodeId);
				if(cs != null) {
					break;
				}
			}
			maxPassengers++;
		}
		return maxPassengers;
		
	}
	
	public int PassengersWhoArrived() {
		int passengers = 0;

		for(double passengerArrivalHour: this.passengerArrivalTimes) {
			if(passengerArrivalHour <= this.departureHour) {
				passengers ++;
			}else {
				break;
			}
		}
		return passengers;
	}

}
