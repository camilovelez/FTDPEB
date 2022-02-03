package ftdpeb.trees;

import java.util.ArrayList;
import java.util.List;

import ftdpeb.generalClasses.CS;
import ftdpeb.generalClasses.Route;
import ftdpeb.generalClasses.Solution;
import ftdpeb.singleton.GlobalData;

public class FixedScheduleTree extends TreeParent {

	public Solution SearchTree(Route route, List<CS> csList, double[] times, int forwardSpeed, int returnSpeed, 
			List<Double> nullDepartureSchedule, int currentTree, boolean checkingBestCase){

		
		int maxTrips = GlobalData.minimumTrips - 1;
		if(GlobalData.departureMaxTripsBound){
			for(int i = GlobalData.minimumTrips; i <= GlobalData.maxTrips; i++) {
				if(GlobalData.multipleFunctions.CheckTimeForAtLeastNTrips(i, route, forwardSpeed, returnSpeed, times)) {
					maxTrips = i;
				}else {
					break;
				}
			}
			
			maxTrips = Math.max(maxTrips, 1);
			
		}else{
			maxTrips = GlobalData.maxTrips;
		}

		
		double minTimeForEachRoute = GlobalData.departureTreeDeltaInHours;
		if(GlobalData.departureTimeBetweenTripsBound) {
			minTimeForEachRoute = CeiledMinTimeForEachRouteCharging(route, forwardSpeed, returnSpeed);
		}
		
		List<Double> departureSchedule = new ArrayList<Double>();
		departureSchedule.add(GlobalData.portClosingHour - minTimeForEachRoute);
		

		while(departureSchedule != null) {
			
			route.setTrips(departureSchedule.size());
			departureSchedule.add(GlobalData.portClosingHour);
			bestSol = ExploreNextTree(route, csList, times, forwardSpeed, returnSpeed, departureSchedule, currentTree, checkingBestCase);
			
			if(checkingBestCase && bestSol.isFeasible()){
				return bestSol;
			}
			departureSchedule.remove(departureSchedule.size() - 1);	
			departureSchedule = NextBranch(departureSchedule, minTimeForEachRoute, maxTrips);
			
		}
		return bestSol;
	}

	public List<Double> NextBranch(List<Double> currentBranch, double minTimeForEachRoute, int maxTrips){
		int size = currentBranch.size();
		
		
		double earliestHour = GlobalData.portOpeningHour;
		
		int tripCheckedLast = 0;
		for(int i = 0; i < size; i ++) {
			tripCheckedLast = i;
			double currentDeparture = currentBranch.get(i);
			if(currentDeparture - GlobalData.departureTreeDeltaInHours >= earliestHour) {
				currentBranch.set(i, currentDeparture - GlobalData.departureTreeDeltaInHours);
				break;
			}else if(i == size - 1) {
				if(size == maxTrips) {
					return null;
				}
				currentBranch.add(GlobalData.portClosingHour - minTimeForEachRoute);
				tripCheckedLast ++;
				break;
			}else {
				earliestHour += minTimeForEachRoute;

			}
		}
		ResetLowerIndexedTrips(currentBranch, tripCheckedLast, currentBranch.get(tripCheckedLast), minTimeForEachRoute);
		return currentBranch;		
	}

	public List<Double> ResetLowerIndexedTrips(List<Double> currentBranch, int upperIndex, double upperTripDeparture,double minTimeForEachRoute){
		for(int i = 0; i < upperIndex; i++){
			currentBranch.set(i, upperTripDeparture - minTimeForEachRoute * (upperIndex - i));
		}
		return currentBranch;
	}
	
	public double CeiledMinTimeForEachRouteCharging(Route route, int forwardSpeed, int returnSpeed) {

		double minEnergyToCharge = GlobalData.multipleFunctions.MinEnergyToRechargeForNTrips(1, route, forwardSpeed, returnSpeed);
		
		double minTime = route.getTravelTime() + minEnergyToCharge / GlobalData.highestInstalledChargingPowerFirstSegment;
		return GlobalData.departureTreeDeltaInHours * Math.ceil(minTime / GlobalData.departureTreeDeltaInHours);
	}
	
}
