package ftdpeb.trees;

import java.util.List;

import ftdpeb.generalClasses.CS;
import ftdpeb.generalClasses.Route;
import ftdpeb.generalClasses.Solution;
import ftdpeb.singleton.GlobalData;

public class SpeedTree extends TreeParent{
	
			
	public Solution SearchTree(Route route, List<CS> csList, double[] timesNull
			,int forwardSpeedNull, int returnSpeedNull, List<Double> departureSchedule, int currentTree, boolean checkingBestCase) {
		
		double[] times;
		
		for(int forwardSpeed: GlobalData.speeds) {
			for(int returnSpeed: GlobalData.speeds) {
				times = GlobalData.multipleFunctions.CalculateTimes(forwardSpeed, returnSpeed);
				route.setTravelTime(GlobalData.multipleFunctions.FindTravelTime(times));
			

				if((!GlobalData.speedDistanceCSBound || GlobalData.multipleFunctions.CheckDistanceBetweenCSs(csList, route, forwardSpeed, returnSpeed, GlobalData.maxPassengers))
						&& (!GlobalData.speedTripsTimeBound || GlobalData.multipleFunctions.CheckTimeForAtLeastNTrips(GlobalData.minimumTrips, route, forwardSpeed, returnSpeed, times))){
					bestSol = ExploreNextTree(route, csList, times, forwardSpeed, returnSpeed, departureSchedule, currentTree, checkingBestCase);
					if(checkingBestCase && bestSol.isFeasible()){
						return bestSol;
					}
				}
			}
		}		
		return bestSol;
	}

}
