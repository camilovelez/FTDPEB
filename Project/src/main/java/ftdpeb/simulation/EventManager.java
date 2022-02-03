package ftdpeb.simulation;

import java.util.List;
import java.util.Random;

import ftdpeb.generalClasses.CS;
import ftdpeb.generalClasses.Route;
import ftdpeb.generalClasses.TripInfo;
import ftdpeb.pojo.ChargingService;
import ftdpeb.pojo.PowerOutage;
import ftdpeb.singleton.GlobalData;

public class EventManager {	
	
	boolean feasible;
	boolean charged = false;
	double energyFromGrid;
	double totalPassengerWaitingTime = 0;
	int totalPassengersWhoBoard = 0;
	int peopleWhoWentToThePort = 0;
	
	public int getPeopleWhoWentToThePort() {
		return peopleWhoWentToThePort;
	}

	public int getTotalPassengersWhoBoard() {
		return totalPassengersWhoBoard;
	}
	
	public double getTotalPassengerWaitingTime() {
		return totalPassengerWaitingTime;
	}

	public boolean isFeasible() {
		return feasible;
	}

	public double getEnergyFromGrid() {
		return energyFromGrid;
	}
	

	public void CheckFeasibility(Route route, List<CS> csList, double[] times, int day,	int forwardSpeed, 
			int returnSpeed, List<Double> departureSchedule, boolean csAtPort, Random rnd, List<PowerOutage>[] outagesPerCS) {
		
		this.energyFromGrid = 0;
		this.feasible = false;
		Event event;
		this.totalPassengerWaitingTime = 0;
		this.totalPassengersWhoBoard = 0;
		
		
		
		route.addArrivalEnergyPerNode(route.getBoat().getBattery().getEnergyLevel());
		route.addArrivalHourPerNode(0);
		
		List<Double> dailyPassengerArrivalMainPort = GlobalData.multipleFunctions.DailyPassengerArrival(true, rnd);
		List<Double> dailyPassengerArrivalMidNode = GlobalData.multipleFunctions.DailyPassengerArrival(false, rnd);
		
		this.peopleWhoWentToThePort = dailyPassengerArrivalMainPort.size() + dailyPassengerArrivalMidNode.size();
		
		TripInfo tripInfoMainPort = new TripInfo(dailyPassengerArrivalMainPort);
		
		TripInfo tripInfoMidNode = new TripInfo(dailyPassengerArrivalMidNode);
		
		for (int trip = 0; trip < route.getTrips(); trip++) {
			double initialEnergyLevel = route.getBoat().getBattery().getEnergyLevel();
			if(csAtPort) {
				CS cs = csList.get(0);
				
				double currentEnergy = route.getBoat().getBattery().getEnergyLevel();
	
				double energyToCharge = route.getBoat().getBattery().getTotalCapacity() - currentEnergy;
				
				ChargingService charServ = cs.ChargingProcess(route.getId(), route.getArrivalHourPerNode().get(route.getArrivalHourPerNode().size() - 1), 
						energyToCharge, day, departureSchedule.get(trip), route.getBoat().getBattery(), outagesPerCS);
				
				initialEnergyLevel = currentEnergy + charServ.getChargedEnergy();
				this.energyFromGrid += charServ.getEnergyTakenFromGrid();
				route.getBoat().getBattery().setEnergyLevel(initialEnergyLevel);
			}
			
			
			
			tripInfoMainPort.SetTripInformation(departureSchedule.get(trip), route, csList, forwardSpeed, false);

			
			route.setCurrentCumulativeChargingTime(0);
			double maxRouteTime = departureSchedule.get(trip + 1) - tripInfoMainPort.getDepartureHour();
			if(maxRouteTime + GlobalData.epsilon < route.getTravelTime()) {
				return;
			}
			route.setMaxTotalChargingTime(maxRouteTime - route.getTravelTime());

			double[] consumptions = GlobalData.multipleFunctions.CalculateConsumption(forwardSpeed, returnSpeed, 
					route.getBoat().getBattery().getAmountOfBatteryModules(), tripInfoMainPort.getPassengers());

			event = new Event(tripInfoMainPort.getDepartureHour(), "depart from node", route.getId(), GlobalData.nodesToVisit[0]);
			
			
			
			double energyAtNextNode = route.getBoat().getBattery().getCurrentAvailableEnergy() - consumptions[0];
			
			if(energyAtNextNode + GlobalData.epsilon < 0) {
				this.feasible = false;
				return;
			}
			
			route.addDepartureEnergyPerNode(initialEnergyLevel);
			route.addDepartureHourPerNode(tripInfoMainPort.getDepartureHour());
			
			while(!event.getAction().equals("finish route")) {
				event = ProcessEvent(event, route, csList, times, consumptions, day, outagesPerCS);
				if(event == null) {
					this.feasible = false;
					return;
				}else if(event.getNodeId() ==  GlobalData.midNode) {
					if(event.getAction().equals("arrive at node")) {
						tripInfoMidNode.SetTripInformation(event.getExecutionHour(), route, csList, returnSpeed, true);
						consumptions = GlobalData.multipleFunctions.CalculateConsumption(forwardSpeed, returnSpeed, 
								route.getBoat().getBattery().getAmountOfBatteryModules(), tripInfoMidNode.getPassengers());	
					}else if(event.getAction().equals("depart from node") && this.charged) {
						tripInfoMidNode.UpdateWaitingTimes(event.getExecutionHour());
					}
					
				}
			}
			this.totalPassengerWaitingTime += tripInfoMainPort.getPassengerWaitingTime() + tripInfoMidNode.getPassengerWaitingTime();
			this.totalPassengersWhoBoard += tripInfoMainPort.getPassengers() + tripInfoMidNode.getPassengers();
		}
		double averageWaitingTime = this.totalPassengerWaitingTime / this.totalPassengersWhoBoard;
		double percentageOfPeopleWhoBoard  = 1.0 * this.totalPassengersWhoBoard / this.peopleWhoWentToThePort;

		if(averageWaitingTime > GlobalData.maxAveragePassengerWaitingTime + GlobalData.epsilon
				|| percentageOfPeopleWhoBoard + GlobalData.epsilon < GlobalData.minPercentageOfPeopleToTransport) {
			return;
		}
		
		this.feasible = true;
	}
	
	
	
	public Event ProcessEvent(Event event, Route route, List<CS> csList, double[] times, 
			double[] consumptions, int day, List<PowerOutage>[] outagesPerCS){
		
		Event newEvent = new Event(); 
		this.charged = false;
		int currentIndex = event.getNodeId();
		
		if(event.getAction().equals("depart from node")) {
			int currentNode = GlobalData.nodesToVisit[currentIndex];
			int nextNode = GlobalData.nodesToVisit[currentIndex +1];
			double arrivalHour = event.getExecutionHour() + times[currentNode];
			
			newEvent = new Event(arrivalHour, "arrive at node", route.getId(), nextNode);			
			route.addArrivalHourPerNode(arrivalHour);
			
			double energyLevel = route.getBoat().getBattery().getEnergyLevel() - consumptions[currentNode];
			
			route.addArrivalEnergyPerNode(energyLevel);
			route.getBoat().getBattery().setEnergyLevel(energyLevel);
			
		}else if(event.getAction().equals("arrive at node")) {
			
			if(currentIndex < GlobalData.segments) {
				int currentNode = GlobalData.nodesToVisit[currentIndex];
				double energyAtNextNode = route.getBoat().getBattery().getCurrentAvailableEnergy() - consumptions[currentNode];
				
				int actualNodeId = GlobalData.multipleFunctions.ActualNodeId(event.getNodeId());

				CS cs = GlobalData.multipleFunctions.FindCSByID(csList, actualNodeId);
				
				if(cs != null && GlobalData.multipleFunctions.ChargingOperationRequired(route, csList, currentNode, consumptions)) {
					newEvent = new Event(event.getExecutionHour(), "start charging", route.getId(), currentNode);					
				}else if(energyAtNextNode + GlobalData.epsilon < 0) {
					return null;
				}else {
					newEvent = new Event(event.getExecutionHour(), "depart from node", route.getId(), event.getNodeId());
					route.addDepartureHourPerNode(event.getExecutionHour());
					route.addDepartureEnergyPerNode(route.getBoat().getBattery().getEnergyLevel());
								
				}
			}else {
				newEvent = new Event(event.getExecutionHour(), "finish route", route.getId(), event.getNodeId());
			}
			
		}else if(event.getAction().equals("start charging")){
			int currentNode = GlobalData.nodesToVisit[currentIndex];
			double targetEnergy = GlobalData.multipleFunctions.TargetEnergyToNextCS(csList, route, currentNode, consumptions);
			double energyToCharge = targetEnergy - route.getBoat().getBattery().getEnergyLevel();
			int actualNodeId = GlobalData.multipleFunctions.ActualNodeId(event.getNodeId());
			
			CS cs = GlobalData.multipleFunctions.FindCSByID(csList, actualNodeId);
			
			double maxDepartureHour = event.getExecutionHour() + route.getMaxTotalChargingTime() - route.getCurrentCumulativeChargingTime();
			
			ChargingService charServ = cs.ChargingProcess(route.getId(), event.getExecutionHour(), energyToCharge, 
					day, maxDepartureHour, route.getBoat().getBattery(), outagesPerCS);
			route.addDepartureHourPerNode(charServ.getDepartureHour());
			double energyLevel = route.getBoat().getBattery().getEnergyLevel() + charServ.getChargedEnergy();
			this.energyFromGrid += charServ.getEnergyTakenFromGrid();

			route.addDepartureEnergyPerNode(energyLevel);
			route.getBoat().getBattery().setEnergyLevel(energyLevel);
			
			double timeSpentAtCS = route.getDepartureHourPerNode().get(route.getDepartureHourPerNode().size() - 1) 
					- route.getArrivalHourPerNode().get(route.getArrivalHourPerNode().size() - 1);
			route.addCurrentCumulativeChargingTime(timeSpentAtCS);
			
			double energyAtNextNode = route.getBoat().getBattery().getCurrentAvailableEnergy() - consumptions[currentNode];
			
			this.charged = true;
			
			if(energyAtNextNode + GlobalData.epsilon < 0) {
				return null;
			}else {
				newEvent = new Event(charServ.getDepartureHour(), "depart from node", route.getId(), event.getNodeId());

			}
		}
		
		return newEvent;
	}


}

