package ftdpeb.generalClasses;

import java.util.ArrayList;
import java.util.List;

import ftdpeb.singleton.GlobalData;

public class Solution {
	
	private boolean[] openedCS;
	private Infrastructure infrastructure = null;
	private Route route;
	private double totalCost = Double.POSITIVE_INFINITY;
	private double investmentCost = 0;
	private double gridEnergyCost = 0;
	private int forwardSpeed = 0;
	private int returnSpeed = 0;
	private List<Double> schedule = new ArrayList<Double>();
	private boolean feasible = false;
	private double averageWaitingTime;
	double peopleWhoWentToThePort = 0;
	double percentageOfPeopleWhoBoard = 0;
	
	public Solution() {	}

	public Solution(boolean[] openedCS, Infrastructure infrastructure, Route route, double investmentCost, 
			double gridEnergyCost, int forwardSpeed, int returnSpeed, List<Double> schedules, double averageWaitingTime, 
			double peopleWhoWentToThePort, double percentageOfPeopleWhoBoard) {
		this.openedCS = openedCS;
		this.infrastructure = infrastructure;
		this.route = new Route(route);
		this.investmentCost = investmentCost;
		this.gridEnergyCost = gridEnergyCost;
		this.forwardSpeed = forwardSpeed;
		this.returnSpeed = returnSpeed;
		this.schedule.clear();
		for(double departure: schedules) {
			this.schedule.add(departure);
		}
		if(infrastructure != null) {
			this.feasible = true;
		}
		this.averageWaitingTime = averageWaitingTime;
		this.totalCost = investmentCost + gridEnergyCost;
		this.peopleWhoWentToThePort = peopleWhoWentToThePort;
		this.percentageOfPeopleWhoBoard = percentageOfPeopleWhoBoard;
		
	}

	public Solution(Solution sol) {
		int openedCSLength = sol.openedCS.length;
		this.openedCS = new boolean[openedCSLength];
		for(int i = 0; i < openedCSLength; i++) {
			this.openedCS[i] = sol.openedCS[i];
		}
		this.route = new Route(sol.route);
		
		this.infrastructure = new Infrastructure(sol.infrastructure);
		this.investmentCost = sol.investmentCost;
		this.gridEnergyCost = sol.gridEnergyCost;
		this.totalCost = sol.totalCost;
		this.forwardSpeed = sol.forwardSpeed;
		this.returnSpeed = sol.returnSpeed;
		this.schedule.clear();
		for(double departure: sol.schedule) {
			this.schedule.add(departure);
		}
		this.feasible = sol.feasible;
		this.averageWaitingTime = sol.averageWaitingTime;
		this.peopleWhoWentToThePort = sol.peopleWhoWentToThePort;
		this.percentageOfPeopleWhoBoard = sol.percentageOfPeopleWhoBoard;

	}
	
	public boolean isFeasible() {
		return feasible;
	}

	public boolean[] getOpenedCS() {
		return openedCS;
	}

	public Infrastructure getInfrastructure() {
		return infrastructure;
	}

	public Route getRoute() {
		return route;
	}

	public double getTotalCost() {
		return totalCost;
	}
	
	public void CalculateTotalCost() {
		this.totalCost = this.infrastructure.getTotalInfrastructureCost();
	}
	
	public void SetFakeCost() {
		this.totalCost = Double.POSITIVE_INFINITY;
	}

	public int getForwardSpeed() {
		return forwardSpeed;
	}

	public int getReturnSpeed() {
		return returnSpeed;
	}

	public List<Double> getSchedule() {
		return schedule;
	}
	
	
	public double getInvestmentCost() {
		return investmentCost;
	}


	public double getGridEnergyCost() {
		return gridEnergyCost;
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


	public void CheckLastSchedule() {
		int scheduleSize = this.schedule.size();
		if(this.schedule.get(scheduleSize - 1) >= GlobalData.portClosingHour) {
			this.schedule.remove(scheduleSize - 1);
		}
	}
	
}
