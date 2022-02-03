package ftdpeb.trees;

import java.util.ArrayList;
import java.util.List;

import ftdpeb.generalClasses.CS;
import ftdpeb.generalClasses.ESS;
import ftdpeb.generalClasses.Route;
import ftdpeb.generalClasses.Solution;
import ftdpeb.pojo.Battery;
import ftdpeb.singleton.GlobalData;

public class BatteryTree extends TreeParent{
	
	public Solution SearchTree(Route originalRoute, List<CS> csList, double[] times, 
			int forwardSpeed, int backwardsSpeed, List<Double> departureSchedule, int currentTree) {
		
		Route route = null;
		
		int minBatIndex = 0;
		
		if(GlobalData.batteryBestCSCaseBound) {
			for(int bat: GlobalData.batteryCapacities) {
				route = new Route(originalRoute);
				
				Battery battery = new Battery(bat);
				route.getBoat().setBattery(battery);
				GlobalData.multipleFunctions.DefineChargingPowersAndCosts(bat);
				if(BestCaseScenario(route, GlobalData.actualRealNodes)) {
					break;
				}
				minBatIndex ++;
					
			}
		}
		if(minBatIndex >= GlobalData.batteryCapacities.length){
			System.out.println("No feasible solutions were found");
			System.exit(0);
		}
		
		bestSol.SetFakeCost();
		
		double currentBestCost = Double.POSITIVE_INFINITY;
		int batCaps = GlobalData.batteryCapacities.length;
		
		for(int i = minBatIndex; i < batCaps; i++) {
			route = new Route(originalRoute);
			
			int bat = GlobalData.batteryCapacities[i];
			
			Battery battery = new Battery(bat);
			route.getBoat().setBattery(battery);
			
			if((!GlobalData.batteryCostBound || GlobalData.multipleFunctions.CompareCosts(route.getBoat().getBattery(), currentBestCost))) {
				GlobalData.multipleFunctions.DefineChargingPowersAndCosts(bat);
				bestSol = ExploreNextTree(route, csList, times, forwardSpeed, backwardsSpeed, departureSchedule, currentTree, false);
				if(bestSol.isFeasible()) {
					
					currentBestCost = bestSol.getTotalCost();
				}

			}
		}

		
		return bestSol;
	}

	public boolean BestCaseScenario(Route route, int nodes){

		List<CS> csList = new ArrayList<CS>();
		ESS ess = new ESS(route.getBoat().getBattery().getAmountOfBatteryModules());
		for(int i = 0; i < nodes; i++){
			csList.add(new CS(i, GlobalData.pvArraysLastIndex, 
					ess, GlobalData.chargingSpots[1], 0, "PVGrid", 0));
		}
		GlobalData.highestInstalledChargingPowerFirstSegment = route.getBoat().getBattery().getTotalCapacity();
		return CheckingBestCase(route, csList, null, 0, 0, null, 1);

	}
	
	protected boolean CheckingBestCase(Route route, List<CS> csList, double[] times
			, int forwardSpeed, int returnSpeed, List<Double> departureSchedule, int currentTree) {
		Solution sol = treesManager.NextTree(route, csList, times, forwardSpeed, returnSpeed, departureSchedule, currentTree, true);
		return sol.isFeasible();
	}
	
	

}
