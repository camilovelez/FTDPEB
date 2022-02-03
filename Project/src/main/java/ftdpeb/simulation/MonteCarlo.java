package ftdpeb.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ftdpeb.generalClasses.CS;
import ftdpeb.generalClasses.Route;
import ftdpeb.pojo.Battery;
import ftdpeb.pojo.ChargingService;
import ftdpeb.pojo.PowerOutage;
import ftdpeb.singleton.GlobalData;

public class MonteCarlo implements Runnable {
	
	boolean feasible = false;
	double feasibleDays = 0;
	double averageWaitingTime = 0;
	double totalPassengerWaitingTime = 0;
	int[] passengersWhoBoardPerYear;
	int peopleWhoWentToThePort = 0;
	double percentageOfPeopleWhoBoard = 0;
	double totalCostEnergyFromGrid = 0;
	double[] yearlyEnergyFromGrid;

	
	
	public double getFeasibleDaysAsDouble() {
		return feasibleDays;
	}


	public double getTotalCostEnergyFromGrid() {
		return totalCostEnergyFromGrid;
	}


	public int getPeopleWhoWentToThePort() {
		return peopleWhoWentToThePort;
	}

	public double getPercentageOfPeopleWhoBoard() {
		return percentageOfPeopleWhoBoard;
	}

	public boolean isFeasible() {
		return feasible;
	}
	
	public double getAverageWaitingTime() {
		return averageWaitingTime;
	}
	

	Route route;
	List<CS> csList;
	double[] times;
	int forwardSpeed;
	int returnSpeed;
	List<Double> departureSchedule;
	Random rnd;
	List<PowerOutage>[] outagesPerCS;
	
	public MonteCarlo(Route route, List<CS> csList, double[] times, int forwardSpeed, 
			int returnSpeed, List<Double> departureSchedule, Random rnd){
		this.route = new Route(route);
		this.csList = GlobalData.multipleFunctions.CopyCSList(csList);
		this.times = times;
		this.forwardSpeed = forwardSpeed;
		this.returnSpeed = returnSpeed;
		this.rnd = rnd;
		this.departureSchedule = departureSchedule;
		
		this.outagesPerCS = new ArrayList[GlobalData.actualRealNodes];
        
        for(int i = 0; i < GlobalData.actualRealNodes; i ++) {
			outagesPerCS[i] = new ArrayList<PowerOutage>();
		}
	}
	
	@Override
	public void run(){
		double infeasible = 0.0;
		
		this.feasible = false;

		Route copyOfRoute = new Route(this.route);
		List<CS> copyOfCSList = GlobalData.multipleFunctions.CopyCSList(this.csList);
			
		int day = GlobalData.firstDayToEvaluate;
		
		boolean csAtPort = copyOfRoute.getCsAtport();
		
		this.passengersWhoBoardPerYear = new int[GlobalData.totalYears];
		this.yearlyEnergyFromGrid = new double[GlobalData.totalYears];
		EventManager eventManager = new EventManager();

		for(int year = 0; year < GlobalData.totalYears; year ++) {
			
			for(CS cs:copyOfCSList) {
				this.outagesPerCS[cs.getId()] = YearlyOutages(cs.getId(), day, this.rnd);
			}
	    	for(int dayInYear = 0; dayInYear < GlobalData.daysPerYear; dayInYear++) {
	    		
	    		copyOfRoute.ClearLists();
	    		
	    		for(CS a: copyOfCSList) {
	    			a.ClearChargingServices();
	    		}
	    		
	    		int dayForIrradiance = day;
	    		
	    		eventManager.CheckFeasibility(copyOfRoute, copyOfCSList, this.times, dayForIrradiance, this.forwardSpeed, 
	    				this.returnSpeed, this.departureSchedule, csAtPort, this.rnd, this.outagesPerCS);
	    		this.feasibleDays ++;
	    		if(!eventManager.isFeasible()) {
	    			infeasible ++;
	    			this.feasibleDays --;
	    			if(infeasible > GlobalData.maxInfeasibleDays) {
		    			return;
		    		}
	    		}

	    		this.peopleWhoWentToThePort += eventManager.getPeopleWhoWentToThePort();
	    		this.yearlyEnergyFromGrid[year] += eventManager.getEnergyFromGrid();
				this.passengersWhoBoardPerYear[year] += eventManager.getTotalPassengersWhoBoard();
				this.totalPassengerWaitingTime += eventManager.getTotalPassengerWaitingTime();	
				
				if(day < GlobalData.indexOfLastDay) {

					this.InterDayCharging(copyOfRoute, copyOfCSList, csAtPort, dayForIrradiance, year);
					
				}
				DeleteDailyOutages(day, copyOfCSList);
				day ++;	    			
	        }
	    	this.totalCostEnergyFromGrid += this.yearlyEnergyFromGrid[year] * GlobalData.energyPricesWithCO2[year];
		}

		
		double totalPassengersWhoBoard = 0;
		for(int passengers: this.passengersWhoBoardPerYear) {
			totalPassengersWhoBoard += passengers;
		}
		this.averageWaitingTime = totalPassengerWaitingTime / totalPassengersWhoBoard;
		this.percentageOfPeopleWhoBoard  = 1.0 * totalPassengersWhoBoard / peopleWhoWentToThePort;

        this.feasible = true;
    }

	public void InterDayCharging(Route route, List<CS> copyOfCSList, boolean csAtPort, int day, int year) {
		
		boolean chargePortCS = false;
		
		if(csAtPort) {
			double lastArrivalTime = route.getArrivalHourPerNode().get(route.getArrivalHourPerNode().size() - 1);
			
			Battery ebBattery = route.getBoat().getBattery();
			double energyToCharge = ebBattery.getTotalCapacity() - ebBattery.getEnergyLevel();
			
			CS csPort = copyOfCSList.get(0);
			ChargingService charServ = csPort.ChargingProcess(route.getId(), lastArrivalTime, energyToCharge, 
					day, 24, ebBattery, this.outagesPerCS);
			
			chargePortCS = (csPort.getEss().getTotalCapacity() > 0 && csPort.getChargingServicesList()
					.get(copyOfCSList.get(0).getChargingServicesList().size() - 1).getDepartureHour() < 24);
			
			route.addDepartureHourPerNode(charServ.getDepartureHour());
			this.yearlyEnergyFromGrid[year] += charServ.getEnergyTakenFromGrid();

			
			double energyLevel = route.getBoat().getBattery().getEnergyLevel() + charServ.getChargedEnergy();
			route.getBoat().getBattery().setEnergyLevel(energyLevel);
		}
		
		 
	
		for(CS cs:copyOfCSList) {
			if(cs.getEss().getTotalCapacity() > 0 && (!(cs.getId() == 0) || chargePortCS)) {
				cs.InitialCSEnergy(24 * 60, day);
			}
			
		}

	}
	
	
	public List<PowerOutage> YearlyOutages(int csId, int day, Random rnd){
		List<PowerOutage> outages = new ArrayList<PowerOutage>();
		double min = 0;
		while(min < 525600) {
			min += (- GlobalData.timeToFollowingOutageMuMinutes[csId] * Math.log(rnd.nextDouble()));
			int dayInYear = (int) (min / 60 / 24 ) ;
			double initialMin = min - (dayInYear * 24 * 60);
			double finalMin = initialMin + (- GlobalData.outageDurationMuMinutes[csId] * Math.log(rnd.nextDouble()));
			outages.add(new PowerOutage(initialMin, finalMin, day + dayInYear));
		}
		return outages;
		
	}
	
	public void DeleteDailyOutages(int day, List<CS> csList) {
		for(CS cs:csList) {
			while(true) {
				if(day == this.outagesPerCS[cs.getId()].get(0).getDay()) {
					this.outagesPerCS[cs.getId()].remove(0);
				}else {
					break;
				}
			}
		}
	}
	
}