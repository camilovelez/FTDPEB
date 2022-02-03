package ftdpeb.trees;

import java.util.ArrayList;
import java.util.List;

import ftdpeb.generalClasses.CS;
import ftdpeb.generalClasses.ESS;
import ftdpeb.generalClasses.Route;
import ftdpeb.generalClasses.Solution;
import ftdpeb.pojo.CSBasicInfo;
import ftdpeb.singleton.GlobalData;

public class CSTree extends TreeParent{
	
	List<CS> csList;

	
	public Solution SearchTree(Route route, List<CS> csListNull, double[] times, 
			int forwardSpeed, int backwardsSpeed, List<Double> departureSchedule, int currentTree) {
		
		double currentBestCost = Double.POSITIVE_INFINITY;
		
		CSBasicInfo[] csBasicInfo = new CSBasicInfo[GlobalData.actualRealNodes];
		
		for (int i = 0; i < GlobalData.actualRealNodes; i++) {
			csBasicInfo[i] = new CSBasicInfo();
		}
		
		int maxEssModules = route.getBoat().getBattery().getAmountOfBatteryModules();
		int essDelta = (int) Math.floor(maxEssModules / 2);
		while(csBasicInfo != null) {
			
			csList = new ArrayList<CS>();
			
			for(int i = 0; i < GlobalData.actualRealNodes; i++) {
				CSBasicInfo csInfo = csBasicInfo[i];
				if(csInfo.isInstalled()) {
					ESS ess = new ESS(csInfo.getEssModules());
					CS cs = new CS(GlobalData.nodes[i].getId(), csInfo.getPvArrayIndex(), ess, GlobalData.chargingSpots[csInfo.getPlug()], 
							GlobalData.csBaseCost[csInfo.getPlug()], csInfo.getType(), GlobalData.csTotalMaintenance[csInfo.getPlug()]);
					csList.add(cs);
				}
			}
			
			if((!GlobalData.csDistanceCSBound || GlobalData.multipleFunctions.CheckDistanceBetweenCSs(csList, route, GlobalData.minSpeed, GlobalData.minSpeed, GlobalData.maxPassengers)) 
					&& (!GlobalData.csCostBound || GlobalData.multipleFunctions.CompareCosts(route.getBoat().getBattery(), csList, currentBestCost))
					) {
				
				route.setCsAtport(csBasicInfo[0].isInstalled());
				GlobalData.multipleFunctions.SetHighestInstalledChargingPowerFirstSegment(csList);
				
				bestSol = ExploreNextTree(route, csList, times, forwardSpeed, backwardsSpeed, departureSchedule, currentTree, false);
				if(bestSol.isFeasible()) {
					currentBestCost = bestSol.getTotalCost();
				}
				
			}

			csBasicInfo = IncreaseCSBasicInfo(csBasicInfo,  maxEssModules, essDelta, 0);	
			
		}
		return bestSol;
	}
	

	public CSBasicInfo[] IncreaseCSBasicInfo(CSBasicInfo[] csBasicInfo, int maxEss, int essIncrement, int initialEss) {
		
		int count = 0;
		
		for(int i = 0; i < GlobalData.actualRealNodes; i++) {
			CSBasicInfo currentCSInfo = csBasicInfo[i];
			if(currentCSInfo.isInstalled() == false){
				currentCSInfo.setInstalled(true);				
				break;
			}else if(currentCSInfo.getPlug() < GlobalData.chargingPlugs - 1) {
				currentCSInfo.setPlug(currentCSInfo.getPlug() + 1);
				break;
			}else if(currentCSInfo.getType().equals("Grid")) {
				count ++;
				if(count == GlobalData.actualRealNodes) {
					return null;
				}
				currentCSInfo.setInstalled(false);
				currentCSInfo.setEssModules(initialEss);
				currentCSInfo.setPlug(0);
				currentCSInfo.setPvArrayIndex(1);
				currentCSInfo.setType("PVGrid");
			}
			else if(currentCSInfo.getType().equals("PVGrid")){
				currentCSInfo.setPlug(0);
				if(currentCSInfo.getEssModules() >= maxEss) {
					if(currentCSInfo.getPvArrayIndex() == GlobalData.pvArraysLastIndex) {
						currentCSInfo.setType("Grid");
						currentCSInfo.setEssModules(0);
						currentCSInfo.setPvArrayIndex(0);
						
					}else {
						currentCSInfo.setEssModules(initialEss);
						currentCSInfo.setPvArrayIndex(currentCSInfo.getPvArrayIndex() + 1);	
					}
					
				}else {
					currentCSInfo.setEssModules(currentCSInfo.getEssModules() + essIncrement);	
					
				}
				break;
			}
		}
		return csBasicInfo;
	}
	
	public CSBasicInfo[] IncreaseCSBasicInfoGridOnly(CSBasicInfo[] csBasicInfo) {
		
		int count = 0;
		
		for(int i = 0; i < GlobalData.actualRealNodes; i++) {
			CSBasicInfo currentCSInfo = csBasicInfo[i];
			if(currentCSInfo.isInstalled() == false){
				currentCSInfo.setInstalled(true);	
				currentCSInfo.setType("Grid");
				currentCSInfo.setPvArrayIndex(0);
				break;
			}else if(currentCSInfo.getPlug() < GlobalData.chargingPlugs - 1) {
				currentCSInfo.setPlug(currentCSInfo.getPlug() + 1);
				break;
			}else if(currentCSInfo.getType().equals("Grid")) {
				count ++;
				if(count == GlobalData.actualRealNodes) {
					return null;
				}
				currentCSInfo.setPlug(0);
				currentCSInfo.setInstalled(false);
			}
		}
		return csBasicInfo;
	}
	
	public CSBasicInfo[] IncreaseCSBasicInfoNoESS(CSBasicInfo[] csBasicInfo) {
		
		int count = 0; 
		
		for(int i = 0; i < GlobalData.actualRealNodes; i++) {
			CSBasicInfo currentCSInfo = csBasicInfo[i];
			if(currentCSInfo.isInstalled() == false){
				currentCSInfo.setInstalled(true);				
				break;
			}else if(currentCSInfo.getPlug() < GlobalData.chargingPlugs - 1) {
				currentCSInfo.setPlug(currentCSInfo.getPlug() + 1);
				break;
			}else if(currentCSInfo.getType().equals("Grid")) {
				count ++;
				if(count == GlobalData.actualRealNodes) {
					return null;
				}
				currentCSInfo.setInstalled(false);
				currentCSInfo.setPlug(0);
				currentCSInfo.setPvArrayIndex(1);
				currentCSInfo.setType("PVGrid");
			}
			else if(currentCSInfo.getType().equals("PVGrid")){
				currentCSInfo.setPlug(0);
				if(currentCSInfo.getPvArrayIndex() == GlobalData.pvArraysLastIndex) {
					currentCSInfo.setType("Grid");
					currentCSInfo.setPvArrayIndex(0);
					
				}else {
					currentCSInfo.setPvArrayIndex(currentCSInfo.getPvArrayIndex() + 1);	
				}
					
				
					
				
				break;
			}
		}
		return csBasicInfo;
	}
	


}
