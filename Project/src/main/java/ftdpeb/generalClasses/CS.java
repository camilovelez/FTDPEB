package ftdpeb.generalClasses;

import java.util.List;

import ftdpeb.pojo.*;
import ftdpeb.singleton.*;

import java.util.ArrayList;

public class CS extends Node{

	private ESS ess;
	private double totalCost;
	private ChargingSpot chargingSpot;
	private double costWithoutSelfInfrastructure;
	private int amountOfPVModules;
	private String csType;
	private List<ChargingService> chargingServicesList = new ArrayList<ChargingService>();
	private double pvArea;
	private double maintenanceCost;

	
	public CS (int id) {super(id);}
	
	public CS (CS originalCS) {
		super(originalCS.getId());
		this.ess = new ESS(originalCS.getEss());
		this.chargingSpot = originalCS.chargingSpot;
		this.amountOfPVModules = originalCS.amountOfPVModules;
		this.csType = originalCS.csType;
		this.totalCost = originalCS.totalCost;
		if(originalCS.getChargingServicesList().size() > 0) {
			ChargingService lastCharServ = originalCS.getChargingServicesList().get(originalCS.getChargingServicesList().size() - 1);
			ChargingService charserv = new ChargingService(lastCharServ.getArrivalHour(), lastCharServ.getDepartureHour(), 
					lastCharServ.getChargedEnergy(), lastCharServ.getEssEnergyAtDeparture(), lastCharServ.getEnergyTakenFromGrid()); 
			this.chargingServicesList.add(charserv);
		}else {
			this.chargingServicesList = new ArrayList<ChargingService>();
		}
		this.maintenanceCost = originalCS.maintenanceCost;
		this.pvArea = originalCS.pvArea;
	}


	public CS(int id, int pvArrayIndex, ESS ess, ChargingSpot chargingSpot,
			double costWithoutSelfInfrastructure, String csType, double maintenanceCost) {
		super(id);
		int amountOfPVModules = GlobalData.pvArrays[pvArrayIndex] * GlobalData.pvModulesPerArray;
		this.ess = ess;
		this.totalCost = costWithoutSelfInfrastructure + amountOfPVModules * GlobalData.pvModule.getCost() 
			+ ess.getTotalCost()  + maintenanceCost + GlobalData.converterCosts[pvArrayIndex];
		
		this.chargingSpot = chargingSpot;
		this.costWithoutSelfInfrastructure = costWithoutSelfInfrastructure;
		this.amountOfPVModules = amountOfPVModules;
		this.pvArea = amountOfPVModules * GlobalData.pvModule.getSqrM();
		this.csType = csType;
		this.maintenanceCost = maintenanceCost;
	}
	
	public CS(int pvArrays, ESS ess, ChargingSpot chargingSpot,	double costWithoutSelfInfrastructure,
			double maintenanceCost, double converterCost) {
		super(0);
		int amountOfPVModules = pvArrays * GlobalData.pvModulesPerArray;
		this.ess = ess;
		this.totalCost = costWithoutSelfInfrastructure + amountOfPVModules * GlobalData.pvModule.getCost() 
			+ ess.getTotalCost()  + maintenanceCost + converterCost;

		this.chargingSpot = chargingSpot;
		this.costWithoutSelfInfrastructure = costWithoutSelfInfrastructure;
		this.amountOfPVModules = amountOfPVModules;
		this.pvArea = amountOfPVModules * GlobalData.pvModule.getSqrM();
		this.csType = "PVGrid";
		this.maintenanceCost = maintenanceCost;
	}
	
	
	public double getPvArea() {
		return pvArea;
	}

	public ESS getEss() {
		return ess;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public ChargingSpot getChargingSpot() {
		return chargingSpot;
	}

	public double getCostWithoutSelfInfrastructure() {
		return costWithoutSelfInfrastructure;
	}

	public int getAmountOfPVModules() {
		return amountOfPVModules;
	}


	public String getcsType() {
		return csType;
	}

	public List<ChargingService> getChargingServicesList() {
		return chargingServicesList;
	}

	
	public double RechargeTime(double arrivalTime, double energyToCharge){
		return 0;
	}

	public void ClearChargingServices() {
		if(this.getId() == 0) {
			int size = this.chargingServicesList.size();
			if(size > 0 && this.chargingServicesList.get(size - 1).getDepartureHour() > 24) {
				this.chargingServicesList.get(size - 1).setDepartureHour(this.chargingServicesList.get(size - 1).getDepartureHour() - 24);
				return;
			}
		}
		this.chargingServicesList.clear();	
	}
	
	public void ClearChargingServicesSlowCS() {
		this.chargingServicesList.clear();	
	}
	public double GeneratedEnergy(int indexByMinute, int day){
//		if(GlobalData.lowIrradianceAlways) {
//		indexByMinute = Math.min(indexByMinute, 47);
//		int index = day * GlobalData.dataPerDay + indexByMinute;
//		return GlobalData.lowIrrandiancePerPeriod[index] * GlobalData.pvModule.getEfficiency() * this.pvArea;
//	}

		int index = day * GlobalData.dataPerDay + indexByMinute;
		return GlobalData.solarEnergyPerSqrM[index] * GlobalData.pvModule.getEfficiency() * this.pvArea;
    }
	
	public void InitialCSEnergy(double finalChargingMinute, int day){
        
		double initialMinute = 0;      
        double energy = this.ess.getEnergyLevel();
        
        if(this.chargingServicesList.size() > 0) {        	
        	ChargingService charServ = this.chargingServicesList.get(chargingServicesList.size() - 1);
			initialMinute = charServ.getDepartureHour() * 60;   
	        energy = charServ.getEssEnergyAtDeparture();
		}
		
        int irradianceDataPeriod = GlobalData.irradianceDataPeriod;

        double initialMod = initialMinute % irradianceDataPeriod; 
        double finalMod = finalChargingMinute % irradianceDataPeriod;
        
        double initialFraction = 1 - initialMod / irradianceDataPeriod;
        double finalFraction = finalMod / irradianceDataPeriod;
        double chargedEnergy = 0;
        
        
        int initialPoint = (int) (initialMinute - initialMod);
        int initialIndex = (int) (initialPoint / irradianceDataPeriod);
        energy += (GeneratedEnergy(initialIndex, day) * initialFraction);
        
        int finalPoint = (int) (finalChargingMinute - finalMod);
        int finalIndex = (int) (finalPoint / irradianceDataPeriod);
        energy += (finalFraction * GeneratedEnergy(finalIndex, day));
        
        for(int i = initialPoint + irradianceDataPeriod; i < finalPoint; i += irradianceDataPeriod){
        	int indexInIrradianceArray = (int) (i / irradianceDataPeriod);
        	chargedEnergy = GeneratedEnergy(indexInIrradianceArray, day);
            energy += chargedEnergy;
            if(energy > this.ess.getTotalCapacity()) {
            	break;
            }
        }
        
        
        energy = Math.min(energy, this.ess.getTotalCapacity());
        this.ess.setEnergyLevel(energy);

    }
	
	public double EstimateAvailableHour(double arrivalHour) {
		double availableHour = arrivalHour;
		
		if(this.chargingServicesList.size() > 0) {  
			ChargingService lastOne = this.chargingServicesList.get(chargingServicesList.size() -1 );		
				
			availableHour = Math.max(lastOne.getDepartureHour(), availableHour);
		}
		
		return availableHour;
	}
	
	public ChargingService ChargingProcess(int boatId, double vehicleArrivalHour, double energyToCharge, int day, 
			double maxDepartureHour, Battery ebBattery, List<PowerOutage>[] outagesPerCS) {

		if(this.csType.equals("PVGrid")) {
			if(this.ess.getTotalCapacity() > 0) {
				return CSOperationPVGrid(boatId, vehicleArrivalHour, energyToCharge, day, maxDepartureHour,
						ebBattery, outagesPerCS);
			}
			return CSOperationPVGridNoESS(boatId, vehicleArrivalHour, energyToCharge, day, maxDepartureHour, 
					ebBattery, outagesPerCS);
		}else if(this.csType.equals("Grid")) {
			return CSOperationGrid(boatId, vehicleArrivalHour, energyToCharge, day, maxDepartureHour, 
					ebBattery, outagesPerCS);
		}
	 	return null;
	}
	
		
	
	public ChargingService CSOperationPVGrid(int boatId, double vehicleArrivalHour, double energyToCharge, 
			int day, double maxDepartureHour, Battery ebBattery, List<PowerOutage>[] outagesPerCS) {
		
		double evEnergy = ebBattery.getEnergyLevel();
		double batteryCapacity = ebBattery.getTotalCapacity();
		
		double vehicleArrivalMinute = vehicleArrivalHour * 60;
		int intInitial = (int) vehicleArrivalMinute;
		
        this.InitialCSEnergy(vehicleArrivalMinute, day);
        
        this.ess.CheckIfItCharged();
        
        double generatedEnergy = 0;
        double preliminarChargedEnergyEV = 0;
        double elapsedTime = vehicleArrivalMinute;
        
        double energyFromGrid = 0;
        int intMinute = intInitial;
        double initialEVEnergy = evEnergy;
        double evEnergyAtDeparture = energyToCharge + evEnergy;
        
        int currentSegment = 0;
        double energyPercentage = evEnergy / batteryCapacity;
        double preliminarUsableFractionOfTimeInterval;
        int indexInIrradianceArray;
        double percentage;
        double usableFractionOfTimeInterval;
        double demandedEnergy;
        double[] energyManagement;
        double suppliedEnergy;

        for(int i = 0; i < GlobalData.numberOfBreakPoints - 1; i++) {
        	if(energyPercentage >= this.chargingSpot.getPiecewise().getBreakPoints()[i] && energyPercentage <= this.chargingSpot.getPiecewise().getBreakPoints()[i + 1]) {
        		currentSegment = i;
        		break;
        	}	
        }
        double maxDepartureMin = maxDepartureHour * 60;
        while(evEnergy < evEnergyAtDeparture - GlobalData.epsilon && elapsedTime < maxDepartureMin - GlobalData.epsilon){
        	preliminarUsableFractionOfTimeInterval = (intMinute + GlobalData.irradianceDataPeriod - elapsedTime) / GlobalData.irradianceDataPeriod;
            
        	if(preliminarUsableFractionOfTimeInterval * GlobalData.irradianceDataPeriod + elapsedTime > maxDepartureMin) {
            	preliminarUsableFractionOfTimeInterval = (maxDepartureMin - elapsedTime) / GlobalData.irradianceDataPeriod;
            }
            indexInIrradianceArray = (int) (elapsedTime / GlobalData.irradianceDataPeriod);

            if(intMinute > maxDepartureMin) {
            	return null;
            }
            generatedEnergy = GeneratedEnergy(indexInIrradianceArray, day);
            
            preliminarChargedEnergyEV = this.chargingSpot.getPiecewise().getChargingPowers()[currentSegment] * preliminarUsableFractionOfTimeInterval;

            percentage = BreakPointChangePercentage(evEnergy, preliminarChargedEnergyEV, currentSegment, batteryCapacity);

            usableFractionOfTimeInterval = preliminarUsableFractionOfTimeInterval * percentage;	
            
            if(evEnergy + usableFractionOfTimeInterval * this.chargingSpot.getPiecewise().getChargingPowers()[currentSegment] > evEnergyAtDeparture) {
            	usableFractionOfTimeInterval *= (evEnergyAtDeparture - evEnergy) / (usableFractionOfTimeInterval * this.chargingSpot.getPiecewise().getChargingPowers()[currentSegment]);
            }
            
        	generatedEnergy *= usableFractionOfTimeInterval;
        	demandedEnergy = this.chargingSpot.getPiecewise().getChargingPowers()[currentSegment] * usableFractionOfTimeInterval;
        	energyManagement = this.ess.EnergyManagement(this.chargingSpot.getProtocol(), generatedEnergy, demandedEnergy);
            
            double initialMinForGridEnergy = elapsedTime + usableFractionOfTimeInterval 
            		* energyManagement[1] * GlobalData.irradianceDataPeriod;            

            elapsedTime += usableFractionOfTimeInterval * GlobalData.irradianceDataPeriod;
            
            double fractionOfIntervalWithGridEnergy = GlobalData.multipleFunctions.FractionOfTimeWithGridEnergy
            		(initialMinForGridEnergy, elapsedTime, this.getId(), day, outagesPerCS);
        			
            double energyFromGridOnThisIteration = (demandedEnergy - energyManagement[0]) * fractionOfIntervalWithGridEnergy;
        	energyFromGrid += energyFromGridOnThisIteration;
        	
        	suppliedEnergy = energyFromGridOnThisIteration + energyManagement[0];
            evEnergy += suppliedEnergy;
            
            intMinute = (int) elapsedTime;
            currentSegment = GetCurrentPWSegment(batteryCapacity, evEnergy);

        }
        double chargedEnergy = evEnergy - initialEVEnergy;
        elapsedTime /= 60;
        
    	ChargingService charServ = new ChargingService(boatId, this.getId(), vehicleArrivalHour, elapsedTime, 
    			chargedEnergy, this.ess.getEnergyLevel(), energyFromGrid);
        this.chargingServicesList.add(charServ);
        return charServ;
		
	}
	
	public ChargingService CSOperationPVGridNoESS(int boatId, double vehicleArrivalHour, double energyToCharge, 
			int day, double maxDepartureHour, Battery ebBattery, List<PowerOutage>[] outagesPerCS) {
		
		double evEnergy = ebBattery.getEnergyLevel();
		double batteryCapacity = ebBattery.getTotalCapacity();
		
		double vehicleArrivalMinute = vehicleArrivalHour * 60;
		int intInitial = (int) vehicleArrivalMinute;
	
        double generatedEnergy = 0;
        double preliminarChargedEnergyEV = 0;
        double elapsedTime = vehicleArrivalMinute;
        
        double energyFromGrid = 0;
        int intMinute = intInitial;
        double initialEVEnergy = evEnergy;
        double evEnergyAtDeparture = energyToCharge + evEnergy;
        
        int currentSegment = 0;
        double energyPercentage = evEnergy / batteryCapacity;
        double preliminarUsableFractionOfTimeInterval;
        int indexInIrradianceArray;
        double percentage;
        double usableFractionOfTimeInterval;


        for(int i = 0; i < GlobalData.numberOfBreakPoints - 1; i++) {
        	if(energyPercentage >= this.chargingSpot.getPiecewise().getBreakPoints()[i] && energyPercentage <= this.chargingSpot.getPiecewise().getBreakPoints()[i + 1]) {
        		currentSegment = i;
        		break;
        	}	
        }
        double maxDepartureMin = maxDepartureHour * 60;
        while(evEnergy < evEnergyAtDeparture - GlobalData.epsilon && elapsedTime < maxDepartureMin - GlobalData.epsilon){
        	preliminarUsableFractionOfTimeInterval = (intMinute + GlobalData.irradianceDataPeriod - elapsedTime) / GlobalData.irradianceDataPeriod;
            if(preliminarUsableFractionOfTimeInterval * GlobalData.irradianceDataPeriod + elapsedTime > maxDepartureMin) {
            	preliminarUsableFractionOfTimeInterval = (maxDepartureMin - elapsedTime) / GlobalData.irradianceDataPeriod;
            }
            indexInIrradianceArray = (int) (elapsedTime / GlobalData.irradianceDataPeriod);

            if(intMinute > maxDepartureMin) {
            	return null;
            }
            generatedEnergy = GeneratedEnergy(indexInIrradianceArray, day);
       
            preliminarChargedEnergyEV = this.chargingSpot.getPiecewise().getChargingPowers()[currentSegment] * preliminarUsableFractionOfTimeInterval;
            
            
            percentage = BreakPointChangePercentage(evEnergy, preliminarChargedEnergyEV, currentSegment, batteryCapacity);

            usableFractionOfTimeInterval = preliminarUsableFractionOfTimeInterval * percentage;	
            
            if(evEnergy + usableFractionOfTimeInterval * this.chargingSpot.getPiecewise().getChargingPowers()[currentSegment] > evEnergyAtDeparture) {
            	usableFractionOfTimeInterval *= (evEnergyAtDeparture - evEnergy) / (usableFractionOfTimeInterval * this.chargingSpot.getPiecewise().getChargingPowers()[currentSegment]);
            }
            
        	generatedEnergy *= usableFractionOfTimeInterval;
        	double demandedEnergy = this.chargingSpot.getPiecewise().getChargingPowers()[currentSegment] * usableFractionOfTimeInterval;
            
            
            double fractionOfEnergySuppliableByPV = Math.min(1, generatedEnergy / demandedEnergy);
            
            double initialMinForGridEnergy = elapsedTime + usableFractionOfTimeInterval 
            		* fractionOfEnergySuppliableByPV * GlobalData.irradianceDataPeriod;            

            elapsedTime += usableFractionOfTimeInterval * GlobalData.irradianceDataPeriod;
            
            double fractionOfIntervalWithGridEnergy = GlobalData.multipleFunctions.FractionOfTimeWithGridEnergy
            		(initialMinForGridEnergy, elapsedTime, this.getId(), day, outagesPerCS);
            
            double energyFromGridOnThisIteration = Math.max((demandedEnergy - generatedEnergy) * fractionOfIntervalWithGridEnergy, 0);
        	energyFromGrid += energyFromGridOnThisIteration;

            evEnergy += Math.min(demandedEnergy, energyFromGrid + generatedEnergy);

            intMinute = (int) elapsedTime;
            currentSegment = GetCurrentPWSegment(batteryCapacity, evEnergy);

        }
        double chargedEnergy = evEnergy - initialEVEnergy;
        elapsedTime /= 60;
        
    	ChargingService charServ = new ChargingService(boatId, this.getId(), vehicleArrivalHour, elapsedTime, 
    			chargedEnergy, 0, energyFromGrid);
        this.chargingServicesList.add(charServ);
        return charServ;
		
	}
	
	public ChargingService CSOperationGrid(int boatId, double vehicleArrivalHour, double energyToCharge, int day,
			double maxDepartureHour, Battery ebBattery, List<PowerOutage>[] outagesPerCS) {
		
		double evEnergy = ebBattery.getEnergyLevel();
		double batteryCapacity = ebBattery.getTotalCapacity();
        
		double elapsedTime = vehicleArrivalHour;

		double departureEnergy = evEnergy + energyToCharge;

        double initialEVEnergy = evEnergy;
        
        int initialSegment = GetCurrentPWSegment(batteryCapacity, evEnergy);

        
        int finalSegment = GetCurrentPWSegment(batteryCapacity, departureEnergy);

        for(int i = initialSegment; i <= finalSegment; i++) {
    		double energyAtNextBPOrDesiredOne = Math.min(departureEnergy, 
    				this.chargingSpot.getPiecewise().getBreakPoints()[i + 1] * batteryCapacity);
    		double elapsedTimePerIteration = (energyAtNextBPOrDesiredOne  - evEnergy) 
    				/ (this.chargingSpot.getPiecewise().getChargingPowers()[i] * GlobalData.nullifyTimeAdjustmentFactor);
        	if(elapsedTimePerIteration + elapsedTime >= maxDepartureHour) {
        		
        		double fractionOfIntervalWithGridEnergy = GlobalData.multipleFunctions.FractionOfTimeWithGridEnergy
                		(elapsedTime * 60, maxDepartureHour * 60, this.getId(), day, outagesPerCS);
	
        		evEnergy += this.chargingSpot.getPiecewise().getChargingPowers()[i] * (maxDepartureHour - elapsedTime) 
        				* GlobalData.nullifyTimeAdjustmentFactor * fractionOfIntervalWithGridEnergy;
        		
        		elapsedTime = maxDepartureHour;
        		break;
        	}else {
        		double initialHourCurrentIter = elapsedTime;
        		elapsedTime += elapsedTimePerIteration;
        		double fractionOfIntervalWithGridEnergy = GlobalData.multipleFunctions.FractionOfTimeWithGridEnergy
                		(initialHourCurrentIter * 60, elapsedTime * 60, this.getId(), day, outagesPerCS);
            	evEnergy = energyAtNextBPOrDesiredOne * fractionOfIntervalWithGridEnergy;
            	if(fractionOfIntervalWithGridEnergy < 1) {
            		i --;
            	}
        	}	
        }
    	
    	double chargedEnergy = evEnergy - initialEVEnergy;
		
        elapsedTime = Math.min(elapsedTime, maxDepartureHour);
        ChargingService charServ = new ChargingService(boatId, this.getId(), vehicleArrivalHour, elapsedTime, 
    			chargedEnergy, this.ess.getEnergyLevel(), chargedEnergy);

        return charServ;
	}

	
	public double BreakPointChangePercentage(double evEnergy, double preliminarChargedEnergyEV, int currentSegment, double batteryCapacity) {
		
		double percentage = 1;
		double preliminarSOC = (evEnergy + preliminarChargedEnergyEV) / batteryCapacity;
		if(preliminarSOC > this.chargingSpot.getPiecewise().getBreakPoints()[currentSegment + 1]) {
			percentage = (this.chargingSpot.getPiecewise().getBreakPoints()[currentSegment + 1] * batteryCapacity - evEnergy) 
					/ this.chargingSpot.getPiecewise().getChargingPowers()[currentSegment];
			
		}
		return percentage;
	}
	
	public int GetCurrentPWSegment(double batteryCapacity, double evEnergy) {
		double soc = (evEnergy + GlobalData.epsilon) / batteryCapacity;
		for(int i = 0; i < GlobalData.numberOfChargingSegments; i++) {
			if(soc < this.chargingSpot.getPiecewise().getBreakPoints()[i + 1]) {
				return i;
			}
		}
		return GlobalData.numberOfChargingSegments - 1;
	}

}
