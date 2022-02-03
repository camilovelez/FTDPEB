package ftdpeb.generalClasses;

import java.util.ArrayList;
import java.util.List;

public class Infrastructure {

	private List<CS> csList = new ArrayList<CS>();
	private double totalInfrastructureCost;
	
	
	public Infrastructure() {}
	
	public Infrastructure(List<CS> csList) {
		this.csList = csList;
		double cost = 0;
		for(CS cs: csList) {
			cost += cs.getTotalCost();
		}
		this.totalInfrastructureCost = cost;
	}
	
	public Infrastructure(Infrastructure infra) {
		this.csList = infra.csList;
		this.totalInfrastructureCost = infra.totalInfrastructureCost;
	}

	public List<CS> getCsList() {
		return csList;
	}

	public void setCsList(List<CS> csList) {
		this.csList = csList;
	}

	public double getTotalInfrastructureCost() {
		return totalInfrastructureCost;
	}

	public void setTotalInfrastructureCost(double totalInfrastructureCost) {
		this.totalInfrastructureCost = totalInfrastructureCost;
	}
	
	public void CalculateTotalCost() {
		double cost = 0;
		for(CS cs: csList) {
			cost += cs.getTotalCost();
		}
		this.totalInfrastructureCost = cost;
	}
	
}
