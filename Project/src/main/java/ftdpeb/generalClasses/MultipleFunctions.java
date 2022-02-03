package ftdpeb.generalClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ftdpeb.pojo.Battery;
import ftdpeb.pojo.ChargingSpot;
import ftdpeb.pojo.Piecewise;
import ftdpeb.pojo.PowerOutage;
import ftdpeb.pojo.TripSchedule;
import ftdpeb.singleton.GlobalData;

public class MultipleFunctions {

	
	public int ActualNodeId(int nonDuplicatedId) {
		int duplicatedId = nonDuplicatedId;
		if(nonDuplicatedId > GlobalData.midNode) {
			duplicatedId = (GlobalData.midNode) * 2 - nonDuplicatedId;
		}
		return duplicatedId;
	}
	
	public List<CS> CopyCSList(List<CS> csList) {
		int listSize = csList.size();
		List<CS> newCSs = new ArrayList<CS>();
		for(int i = 0; i < listSize; i++) {
			newCSs.add(new CS(csList.get(i)));
		}
		return newCSs;
	}
	
	public double GetInvestmentCost(Route route, Infrastructure infrastructure) {
		infrastructure.CalculateTotalCost();
		double cost = infrastructure.getTotalInfrastructureCost();
		route.getBoat().CalculateCost();
		cost += route.getBoat().getTotalCost();
		
		return cost;
	}
	
	public double PartialEnergy(Route route, int currentNode, double[][] consumptions) {
		double partial = route.ConsumptionBetweenNodes(currentNode, 
				GlobalData.nodesToVisit[GlobalData.numberOfNodes - 1], consumptions);
		partial += route.getBoat().getBattery().getMinEnergyLevel();
		return Math.min(route.getBoat().getBattery().getTotalCapacity(), partial);
	}
	
	
	
	public double TimeToFollowingPassenger(double arrivingHour, boolean mainPort, Random rnd) {
		int currentSegment = 0;
		int segments = GlobalData.muTimeWindows.length - 1;
		for(int i = 0; i < segments; i++) {
			if(arrivingHour >= GlobalData.muTimeWindows[i] && arrivingHour < GlobalData.muTimeWindows[i + 1]) {
				currentSegment = i;
				break;
			}
		}
		
		if(!mainPort) {
			return - GlobalData.passengerMuMidPort[currentSegment] * Math.log(rnd.nextDouble());
		}
		return - GlobalData.passengerMuMainPort[currentSegment] * Math.log(rnd.nextDouble());
	}
	
	public double FractionOfTimeWithGridEnergy(double initialMin, double finalMin, int csId, int day, List<PowerOutage>[] outagesPerCS) {
		if(finalMin == initialMin) {
			return 1;
		}
		List<PowerOutage> outages = outagesPerCS[csId];
		double interval = finalMin - initialMin;
		for(PowerOutage outage: outages) {

			if(day == outage.getDay()) {
				double intercept = Math.min(outage.getFinalMin(), finalMin) - Math.max(outage.getInitialMin(), initialMin);
				if(intercept > 0) {
					interval -= intercept; 
				}
			}else {
				break;
			}
		}
		return Math.max(0, interval / (finalMin - initialMin));
	}
	
	public double FindTravelTime(double[] times) {
		double travelTime = 0;
		
		for(double time: times) {
			travelTime += time;
		}
		return travelTime;
	}
	
	public double CeiledMinTimeForEachRoute(double[] times) {
		double travelTime = FindTravelTime(times);
		return GlobalData.departureTreeDeltaInHours * Math.ceil(travelTime / GlobalData.departureTreeDeltaInHours);
	}
	
	public double[] CalculateTimes(int forwardSpeed, int returnSpeed){
		double[] times = new double[GlobalData.segments];
		for(int i = 0; i <GlobalData.midNode; i++) {
			times[i] = GlobalData.travelTimes[forwardSpeed][i];
		}
		for(int i =GlobalData.midNode; i < GlobalData.segments; i++) {
			times[i] = GlobalData.travelTimes[returnSpeed][i];
		}
		return times;
	}
	
	public void WriteTripSchedule(int day, int nodeId, double initialEnergy, double energyDelta, double finalEnergy,
			double initialTime, double taskTime, double finalTime, int csId, int passengers, int tripId, double pvEnergy,
			String task) {
		String trip = "" + tripId;
		String origin = NodeName(ActualNodeId(nodeId - 1));
		String destiny = NodeName(ActualNodeId(Math.max(0, nodeId)));
		String journey;
		if(nodeId == -2) {
			trip = "between days";
		}else if(nodeId == -1) {
			trip = "between routes";
		}else if(nodeId < GlobalData.midNode) {
			trip += " outward";
		}else {
			csId = ActualNodeId(nodeId);
			trip += " return";
		}
		if(energyDelta > 0) {
			journey = destiny;
		}else {
			journey = origin + " - " + destiny;
		}
		 

		GlobalData.tripScheduleList.add(new TripSchedule(day, trip, initialEnergy, energyDelta, finalEnergy, initialTime, 
				taskTime, finalTime, csId, passengers, pvEnergy, task, journey));
		
	}
	
	private String NodeName(int node) {
		if(node == 0) {
			return "M";
		}else if(node == 1) {
			return "T";
		}else if(node == 2) {
			return "P";
		}else {
			return "error";
		}
	}
	
	public double[] CalculateConsumption(int forwardSpeed, int returnSpeed, int amountOfBatteryModules, int passengers) {
		
		double[] consumptions = new double[GlobalData.segments];
		
		for(int i = 0; i <GlobalData.midNode; i ++) {
			consumptions[i] = GlobalData.consumptions[amountOfBatteryModules][forwardSpeed][passengers][i];
		}
		for(int i = GlobalData.midNode; i < GlobalData.segments; i ++) {
			consumptions[i] = GlobalData.consumptions[amountOfBatteryModules][returnSpeed][passengers][i];
		}
		return consumptions;
	}	
	
	public List<Double> DailyPassengerArrival(boolean mainPort, Random rnd){
		double time = GlobalData.portOpeningHour;		
		List<Double> sol = new ArrayList<Double>();
		
		while(time <= GlobalData.lastPassengerArrivalHour) {
			sol.add(time);
			time += TimeToFollowingPassenger(time, mainPort, rnd);
		}
		return sol;
	}
	
	public boolean ChargingOperationRequired(Route route, List<CS> csList, int currentNode, double[] consumptions) {
		return route.getBoat().getBattery().getCurrentAvailableEnergy() + GlobalData.epsilon < EnergyToNextCS(csList, currentNode, consumptions);		
	}
	
	public double TargetEnergyToNextCS(List<CS> csList, Route route, int currentNode, double[] consumptions) {
		double energy = route.getBoat().getBattery().getMinEnergyLevel() + 
				EnergyToNextCS(csList, currentNode, consumptions);		
		return Math.min(route.getBoat().getBattery().getTotalCapacity(), energy);
	}
	public double EnergyToNextCS(List<CS> csList, int currentNode, double[] consumptions) {
		
		int[] nodesToVisit = GlobalData.nodesToVisit;
		double energy = 0;
		for(int i = nodesToVisit[currentNode]; i < GlobalData.numberOfNodes - 1; i ++) {
			int followingNodeId = ActualNodeId(nodesToVisit[i + 1]);
			energy += consumptions[nodesToVisit[i]];
			CS cs = FindCSByID(csList, followingNodeId);

			if(cs != null) {
				break;
			}
		}
		
		return energy;
	}
	
	public boolean CheckDistanceBetweenCSs(List<CS> csList, Route route, int forwardSpeed, int returnSpeed, int passengers) {

		double ebAvaliableEnergy = route.getBoat().getBattery().getCurrentAvailableEnergy();
		
		int[] nodesToVisit = GlobalData.nodesToVisit;
		int ebBatteryNodes = route.getBoat().getBattery().getAmountOfBatteryModules();
		double[] consumptions = CalculateConsumption(forwardSpeed, returnSpeed, ebBatteryNodes, passengers);
		
		double consumptionSinceLastCS = 0;
		
		for(int i = 0; i < GlobalData.numberOfNodes - 1; i ++) {
			consumptionSinceLastCS += consumptions[nodesToVisit[i]];
			if(consumptionSinceLastCS - GlobalData.epsilon > ebAvaliableEnergy) {
				return false;
			}
			int followingNodeId = ActualNodeId(nodesToVisit[i + 1]);
			CS cs = FindCSByID(csList, followingNodeId); 

			if(cs != null) {
				consumptionSinceLastCS = 0;
			}
		}
		return true;
		
	}

	public double MinEnergyToRechargeForNTrips(int nTrips, Route route, int forwardSpeed, int returnSpeed) {
		
		double[] consumptions = CalculateConsumption(forwardSpeed, returnSpeed, 
				route.getBoat().getBattery().getAmountOfBatteryModules(), 1);
		
		double requiredEnergy = 0;
		
		double ebAvaliableEnergy = route.getBoat().getBattery().getCurrentAvailableEnergy();
		
		for(int i = 0; i < GlobalData.numberOfNodes - 1; i ++) {
			requiredEnergy += consumptions[GlobalData.nodesToVisit[i]];
		}
		requiredEnergy *= nTrips;
		return Math.max(0, requiredEnergy - ebAvaliableEnergy); 
	}
	
	public boolean CheckTimeForAtLeastNTrips(int nTrips, Route route, int forwardSpeed, int returnSpeed, double[] times) {
		
		double requiredEnergyToCharge = MinEnergyToRechargeForNTrips(nTrips, route, forwardSpeed, returnSpeed);
		double minTimeToCharge = requiredEnergyToCharge / GlobalData.highestInstalledChargingPowerFirstSegment;
		double estimatedTime = route.getTravelTime() * nTrips + minTimeToCharge;
		
		return estimatedTime <= GlobalData.portClosingHour - GlobalData.portOpeningHour; 
	}
	
	
	public boolean CompareCosts(Battery ebBattery, double currentBestCost) {
		return ebBattery.getTotalCost() < currentBestCost;		
	}
	
	public boolean CompareCosts(Battery ebBattery, List<CS> csList, double currentBestCost) {
		double cost = ebBattery.getTotalCost();
		for(CS cs: csList) {
			cost += cs.getTotalCost();
		}
		return cost < currentBestCost;
	}
	
	public CS FindCSByID(List<CS> csList, int id) {
		for(CS cs: csList) {
			if(cs.getId() == id) {
				return cs;
			}
		}
		return null;
		
	}
	
	public void DefineChargingPowersAndCosts(int powerAccordingToBatteryCapacity) {
		GlobalData.chargingSpots = new ChargingSpot[GlobalData.chargingPlugs];
		for(int i = 0; i < GlobalData.chargingPlugs; i++) {
			double[] chargingPowers = new double[GlobalData.numberOfChargingSegments];
			for(int j = 0; j < GlobalData.numberOfChargingSegments; j++) {
				chargingPowers[j] = powerAccordingToBatteryCapacity * GlobalData.pwSlopeRates[j] * (i + 1) / GlobalData.chargingPlugs;
			}
			Piecewise pw = new Piecewise(GlobalData.breakPoints, chargingPowers);
			
			GlobalData.chargingSpots[i] = new ChargingSpot(pw, i);
			GlobalData.chargingSpots[i].SetPowersAccordingToTimeIntervals(GlobalData.fractionOfHour);
		}
		
		GlobalData.csBaseCost = new double[GlobalData.chargingPlugs];
		for(int i = 0; i < GlobalData.chargingPlugs; i ++) {
			double power = GlobalData.chargingSpots[i].getPiecewise().getChargingPowers()[0] * GlobalData.nullifyTimeAdjustmentFactor;
			GlobalData.csBaseCost[i] = power * GlobalData.csCostSlope + GlobalData.csCostIntercept;
		}
	}
	public void SetHighestInstalledChargingPowerFirstSegment(List<CS> csList) {
		double highestInstalledChargingPowerFirstSegment = GlobalData.epsilon;
		for(CS cs: csList) {
			highestInstalledChargingPowerFirstSegment = Math.max(highestInstalledChargingPowerFirstSegment,
					cs.getChargingSpot().getPiecewise().getChargingPowers()[0]);
		}
		highestInstalledChargingPowerFirstSegment *= GlobalData.nullifyTimeAdjustmentFactor;
		GlobalData.highestInstalledChargingPowerFirstSegment = highestInstalledChargingPowerFirstSegment;
	}
	
}
