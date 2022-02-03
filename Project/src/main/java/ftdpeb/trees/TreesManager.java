package ftdpeb.trees;

import java.util.List;

import ftdpeb.generalClasses.CS;
import ftdpeb.generalClasses.Route;
import ftdpeb.generalClasses.Solution;
import ftdpeb.simulation.RunReplications;
import ftdpeb.singleton.GlobalData;

public class TreesManager {
	
	boolean feasible = false;
	double totalCostEnergyFromGrid = 0;
	double averageWaitingTime = 0;
	double peopleWhoWentToThePort = 0;
	double percentageOfPeopleWhoBoard = 0;	

	public boolean isFeasible() {
		return feasible;
	}
	
	public double getPeopleWhoWentToThePort() {
		return peopleWhoWentToThePort;
	}
	
	public double getPercentageOfPeopleWhoBoard() {
		return percentageOfPeopleWhoBoard;
	}
	
	public double getTotalEnergyCostFromGrid() {
		return totalCostEnergyFromGrid;
	}	
	
	public double getAverageWaitingTime() {
		return averageWaitingTime;
	}



	public Solution NextTree(Route route, List<CS> csList, double[] times
			, int forwardSpeed, int returnSpeed, List<Double> departureSchedule, int currentTree, boolean checkingBestCase) {
	
		if(currentTree == 3) {	
			
			RunReplications runReps = new RunReplications();
			runReps.Run(route, csList, times, forwardSpeed, returnSpeed, departureSchedule);
			this.feasible = runReps.isFeasible();
			if(this.feasible) {
				this.peopleWhoWentToThePort = runReps.getPeopleWhoWentToThePort();
				this.percentageOfPeopleWhoBoard = runReps.getPercentageOfPeopleWhoBoard();
				this.totalCostEnergyFromGrid = runReps.getTotalCostEnergyFromGrid();
				this.averageWaitingTime = runReps.getAverageWaitingTime();
			}
			
			return null;
		}
		
		int nextTree = currentTree + 1;
		Solution sol = new Solution();
		String tree = GlobalData.treesOrder[nextTree]; 
		if(tree.equals("battery")) {
			BatteryTree batTree = new BatteryTree();
			sol = batTree.SearchTree(route, csList, times, forwardSpeed, returnSpeed, departureSchedule, nextTree);
		}else if(tree.equals("cs")) {
			CSTree csTree = new CSTree();
			sol = csTree.SearchTree(route, csList, times, forwardSpeed, returnSpeed, departureSchedule, nextTree);
		}else if(tree.equals("speed")) {
			SpeedTree speedTree = new SpeedTree();
			sol = speedTree.SearchTree(route, csList, times, forwardSpeed, returnSpeed, departureSchedule, nextTree, checkingBestCase);
		}else if(tree.equals("schedules")) {
			FixedScheduleTree fixedScheduleTree = new FixedScheduleTree();
			sol = fixedScheduleTree.SearchTree(route, csList, times, forwardSpeed, returnSpeed, departureSchedule, nextTree, checkingBestCase);
			
		}		
		return sol; 
	}

}
