package ftdpeb.simulation;

import java.util.List;
import java.util.Random;

import ftdpeb.generalClasses.CS;
import ftdpeb.generalClasses.Route;
import ftdpeb.singleton.GlobalData;

public class RunReplications {

	boolean feasible = false;
	int infeasibleReplications = 0;
	double averageWaitingTime = 0;
	double peopleWhoWentToThePort = 0;
	double percentageOfPeopleWhoBoard = 0;
	double totalCostEnergyFromGrid = 0;
	
	public void Run(Route route, List<CS> csList, double[] times, int forwardSpeed, 
			int returnSpeed, List<Double> departureSchedule){
		
		
		int runningThreads;
		for(int i = 0; i < GlobalData.replications; i += runningThreads) {
			runningThreads = Math.min(GlobalData.replications - i, GlobalData.pcProcessors);
			
			MonteCarlo[] monteCarloArray = new MonteCarlo[runningThreads];
			Thread[] threadArray = new Thread[runningThreads];
			for(int j = 0; j < runningThreads; j++) {
				monteCarloArray[j] = new MonteCarlo(route, csList, times, forwardSpeed, returnSpeed, 
						departureSchedule, new Random(GlobalData.random.nextInt()));
				threadArray[j] = new Thread(monteCarloArray[j]);
				threadArray[j].start();
			}
			for(int j = 0; j < runningThreads; j++) {
				try {
					threadArray[j].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(!ShouldItContinue(monteCarloArray)){
				return;
			}
		}
		
		this.averageWaitingTime /= GlobalData.replications;
		this.peopleWhoWentToThePort /= GlobalData.replications;
		this.percentageOfPeopleWhoBoard /= GlobalData.replications;
		this.totalCostEnergyFromGrid /= GlobalData.replications;
		
		this.feasible = true;
		
	}
	
	public boolean ShouldItContinue(MonteCarlo[] monteCarloArray) {
		for(MonteCarlo monteCarlo: monteCarloArray) {
			if(!monteCarlo.isFeasible()) {
				infeasibleReplications++;
				if(infeasibleReplications > GlobalData.maxInfeasibleReplications) {
					return false;
				}
			}
			this.averageWaitingTime += monteCarlo.getAverageWaitingTime();
			this.peopleWhoWentToThePort += 1.0 * monteCarlo.getPeopleWhoWentToThePort();
			this.percentageOfPeopleWhoBoard += monteCarlo.getPercentageOfPeopleWhoBoard();
			this.totalCostEnergyFromGrid += monteCarlo.getTotalCostEnergyFromGrid();
		}
		return true;
	}
	

	public boolean isFeasible() {
		return feasible;
	}

	public double getAverageWaitingTime() {
		return averageWaitingTime;
	}

	public double getPeopleWhoWentToThePort() {
		return peopleWhoWentToThePort;
	}

	public double getPercentageOfPeopleWhoBoard() {
		return percentageOfPeopleWhoBoard;
	}

	public double getTotalCostEnergyFromGrid() {
		return totalCostEnergyFromGrid;
	}

	
}
