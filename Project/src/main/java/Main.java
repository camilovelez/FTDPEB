import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONObject;
import org.json.simple.JSONArray;

import ftdpeb.branchAndBound.Search;
import ftdpeb.generalClasses.*;
import ftdpeb.pojo.*;
import ftdpeb.singleton.*;

import org.json.JSONException;
public class Main {

	public static void main(String[] args) {

		
		Path currentPath = Paths.get(System.getProperty("user.dir"));
//		String instanceName = args[0];
		String instanceName = "SmallInstance.json";
		Path filePath = Paths.get(currentPath.toString(), "ConfigurationFiles", instanceName);

		
		GlobalData.ReadJson(filePath.toString());
		
		Battery bat = new Battery();		
		Boat boat = new Boat(bat);
		
		Route route = new Route(0, boat);
		
		double tIni = System.currentTimeMillis();	
		Search search = new Search();
		Solution solution = search.PerformSearch(route);
		if(solution.isFeasible()) {
			solution.CheckLastSchedule();
		}
		double executionTime = (System.currentTimeMillis() - tIni) / 1000;		


		JSONObject solutionJSON = new JSONObject();
		try {				
			solutionJSON.put("isFeasible", solution.isFeasible());
			
			if(solution.isFeasible()) {
				solutionJSON.put("cost", solution.getTotalCost());
				solutionJSON.put("investmentCost", solution.getInvestmentCost());
				solutionJSON.put("gridEnergyCost", solution.getGridEnergyCost());
				
				solutionJSON.put("averageWaitingTime", solution.getAverageWaitingTime());
				solutionJSON.put("percentageOfPeopleWhoBoard", solution.getPercentageOfPeopleWhoBoard());
				
				solutionJSON.put("ebSelectedBattery", solution.getRoute().getBoat().getBattery().getTotalCapacity());
				
				int csSize = solution.getInfrastructure().getCsList().size();

				JSONArray csList = new JSONArray();
				if(csSize > 0) {
					for(CS cs : solution.getInfrastructure().getCsList()) {
						JSONObject csJSON = new JSONObject();
						csJSON.put("id", cs.getId());
						csJSON.put("type", cs.getcsType());
						csJSON.put("pvArrays", cs.getAmountOfPVModules() / GlobalData.pvModulesPerArray);
						csJSON.put("essCapacity", cs.getEss().getTotalCapacity());
						csJSON.put("chargingPower", cs.getChargingSpot().getProtocol());
						csList.add(csJSON);
					}	
				}
				solutionJSON.put("csList", csList);
				
				solutionJSON.put("forwardSpeed", solution.getForwardSpeed());
				solutionJSON.put("returnSpeed", solution.getReturnSpeed());
				
				JSONArray scheduleList = new JSONArray();
				for(double schedule : solution.getSchedule()) {
					JSONObject sch = new JSONObject();
					sch.put("departure", schedule);
					
					scheduleList.add(sch);
				}
				solutionJSON.put("scheduleList", scheduleList);
				
				
			}
			
			Path solPath = Paths.get(currentPath.toString(), "solutions", "solution" + instanceName);
			try (FileWriter file = new FileWriter(solPath.toString())) {
				 
	            file.write(solutionJSON.toString());
	            file.flush();
	 
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

	}
	
}