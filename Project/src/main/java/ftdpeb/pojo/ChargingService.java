package ftdpeb.pojo;

public class ChargingService {
	
	int boatId;
	int csId;
	double arrivalHour;
	double departureHour;
	double chargedEnergy;
	double essEnergyAtDeparture;
	double pvGenerationDuringCharge;
	double energyTakenFromGrid;
	
	public ChargingService() {}
	
	public ChargingService(double arrivalHour, double departureHour, double chargedEnergy, 
			double essEnergyAtDeparture, double energyTakenFromGrid){
		this.arrivalHour = arrivalHour;
		this.departureHour = departureHour;
		this.chargedEnergy = chargedEnergy;
		this.essEnergyAtDeparture = essEnergyAtDeparture;
		this.energyTakenFromGrid = energyTakenFromGrid;
	}
	
	public ChargingService(int boatId, int csId, double arrivalHour, double departureHour, double chargedEnergy, 
			double essEnergyAtDeparture, double energyTakenFromGrid){
		this.boatId = boatId;
		this.csId = csId;
		this.arrivalHour = arrivalHour;
		this.departureHour = departureHour;
		this.chargedEnergy = chargedEnergy;
		this.essEnergyAtDeparture = essEnergyAtDeparture;
		this.energyTakenFromGrid = energyTakenFromGrid;
	}

	public double getDepartureHour() {
		return departureHour;
	}

	public void setDepartureHour(double departureHour) {
		this.departureHour = departureHour;
	}

	public double getEssEnergyAtDeparture() {
		return essEnergyAtDeparture;
	}

	public void setEssEnergyAtDeparture(double essEnergyAtDeparture) {
		this.essEnergyAtDeparture = essEnergyAtDeparture;
	}

	public double getPvGenerationDuringCharge() {
		return pvGenerationDuringCharge;
	}

	public void setPvGenerationDuringCharge(double pvGenerationDuringCharge) {
		this.pvGenerationDuringCharge = pvGenerationDuringCharge;
	}

	public double getEnergyTakenFromGrid() {
		return energyTakenFromGrid;
	}

	public void setEnergyTakenFromGrid(double energyTakenFromGrid) {
		this.energyTakenFromGrid = energyTakenFromGrid;
	}

	public int getBoatId() {
		return boatId;
	}

	public int getCsId() {
		return csId;
	}

	public double getArrivalHour() {
		return arrivalHour;
	}
	
	public void setArrivalHour(double arrivalHour) {
		this.arrivalHour = arrivalHour;
	}

	public double getChargedEnergy() {
		return chargedEnergy;
	}
	
	
	
}
