package ftdpeb.trees;

import java.util.List;

import ftdpeb.generalClasses.CS;
import ftdpeb.generalClasses.Infrastructure;
import ftdpeb.generalClasses.MultipleFunctions;
import ftdpeb.generalClasses.Route;
import ftdpeb.generalClasses.Solution;
import ftdpeb.simulation.EventManager;
import ftdpeb.singleton.GlobalData;

public class TreeParent {
	
	MultipleFunctions multipleFunctions = GlobalData.multipleFunctions;
	EventManager eventManager = new EventManager();
	TreesManager treesManager = new TreesManager();
	Infrastructure bestInfrastructure = new Infrastructure();
	
	boolean feasible = false;

	public boolean isFeasible() {
		return feasible;
	}

	
	protected Solution bestSol = new Solution();

	
	public Solution ExploreNextTree(Route route, List<CS> csList, double[] times
			, int forwardSpeed, int returnSpeed, List<Double> departureSchedule, int currentTree, boolean checkingBestCase) {
		
		Solution sol = treesManager.NextTree(route, csList, times, forwardSpeed, returnSpeed, departureSchedule, currentTree, checkingBestCase);
		if(sol == null) {
			this.feasible = treesManager.isFeasible();
			if(this.feasible) {
				bestSol = CompareSolutions(route, csList, bestSol, forwardSpeed, returnSpeed, departureSchedule, treesManager);
				
			}
			
		}else if(sol.getTotalCost() < bestSol.getTotalCost()) {
			bestSol = new Solution(sol);
		}
		return bestSol;
	}
	
	Solution CompareSolutions(Route route, List<CS> csList, Solution bestSol, int forwardSpeed, 
			int returnSpeed, List<Double> departureSchedule, TreesManager treesManager) {
		double gridEnergyCost = treesManager.getTotalEnergyCostFromGrid();

		
		
		Infrastructure currentInfrastructure = new Infrastructure(csList);
		double currentInvestmentCost = multipleFunctions.GetInvestmentCost(route, currentInfrastructure); 
		double currentCost = currentInvestmentCost + gridEnergyCost;
		if(currentCost < bestSol.getTotalCost()) {				
			this.feasible = treesManager.isFeasible();
			double averageWaitingTime = treesManager.getAverageWaitingTime();
			double peopleWhoWentToThePort = treesManager.getPeopleWhoWentToThePort(); 
			double percentageOfPeopleWhoBoard = treesManager.getPercentageOfPeopleWhoBoard();
			boolean[] openedCS = new boolean[GlobalData.numberOfNodes];
			bestInfrastructure = new Infrastructure(currentInfrastructure);
			Route routeInCheapestIter = new Route(route);
	
			for(CS cs : bestInfrastructure.getCsList()) {
				openedCS[cs.getId()] = true;
			}
			bestSol = new Solution(openedCS, bestInfrastructure, routeInCheapestIter, currentInvestmentCost, 
					gridEnergyCost,	forwardSpeed, returnSpeed, departureSchedule, averageWaitingTime, peopleWhoWentToThePort, 
					percentageOfPeopleWhoBoard);
		}
		return bestSol; 
	}
	
}
