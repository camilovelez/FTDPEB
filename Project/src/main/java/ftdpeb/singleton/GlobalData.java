package ftdpeb.singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
 
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paranamer.ParanamerModule;

import ftdpeb.generalClasses.MultipleFunctions;
import ftdpeb.pojo.*;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class GlobalData{
	
	JSONParser jsonParser = new JSONParser();

	public static double epsilon = 1e-4;
	public static List<TripSchedule> tripScheduleList = new ArrayList<TripSchedule>();

	public static double [][] timeBetweenNodes;
	public static int pcProcessors = Runtime.getRuntime().availableProcessors();
	public static boolean checking = false;
	
	public static double ebMinSOC;
	
	public static double essNominalSoC;
	public static double essMinSoC;
	
	public static int numberOfNodes;
	
	public static double[] distances;
	public static double distanceToMidPoint;
	public static double[][][][] consumptions;
	public static double[][] travelTimes;
	
	public static int[] batteryCapacities;
	
	public static int[] pvArrays;
	public static int pvArraysLastIndex;
			
	public static Node[] nodes;
	public static int midNode;
	public static int actualRealNodes;
	public static int segments;
	public static int[] nodesToVisit;
	
	public static BatteryModule batteryModule;
	public static ESSModule essModule;
	public static PVModule pvModule;
	public static int pvModulesPerArray;
	public static double[] converterCosts;

	public static double[] solarEnergyPerSqrM;
	public static int irradianceDataPeriod;
	public static double fractionOfHour;
	public static int daysPerYear;
	public static int dataPerDay;
	
	public static int startingYearInTimeSeries;
	public static int finalYearInTimeSeries;
	public static int firstDayToEvaluate;
	public static int totalDays;
	public static int totalYears;
	public static int firstYearInTimeSeries;
	public static int indexOfLastDay;
	
	
	public static int [] speeds; 
	public static int minSpeed;
	
	public static double csCostIntercept;
	public static double csCostSlope;
	
	public static double riverSpeed;
	public static int flow;	
	
	public static ChargingSpot[] chargingSpots;
	public static double nullifyTimeAdjustmentFactor;
	public static double highestInstalledChargingPowerFirstSegment;
	public static int chargingPlugs = 2;
	public static double[] pwSlopeRates;
	public static double[] breakPoints;

	public static double[] csBaseCost;
	public static double[] csTotalMaintenance;

	public static double[] energyPricesWithCO2;
	
	public static String[] treesOrder = new String[] {
		"battery",
		"cs",
		"speed",
		"schedules"
	};
	
	public static double[] passengerMuMainPort;
	public static double[] passengerMuMidPort;
	public static double[] outageDurationMuMinutes;
	public static double[] timeToFollowingOutageMuMinutes;
	public static double lastPassengerArrivalHour;
	
	public static int[] muTimeWindows;
	
	public static int maxPassengers;
	public static int maxTrips;
	public static int minimumTrips;
	public static double departureTreeDeltaInHours;
	public static double portClosingHour;
	public static double portOpeningHour;
	
	public static double minPercentageOfPeopleToTransport;
	public static double maxAveragePassengerWaitingTime;
	public static int maxInfeasibleDays;
	public static int csTypesLength = 2;
	
	public static boolean batteryCostBound;
	public static boolean batteryBestCSCaseBound;
	public static boolean csCostBound;
	public static boolean csDistanceCSBound;
	public static boolean speedDistanceCSBound;
	public static boolean speedTripsTimeBound;
	public static boolean departureMaxTripsBound;
	public static boolean departureTimeBetweenTripsBound;
	
	public static Random random;
	
	public static int replications;
	public static int maxInfeasibleReplications;
	
	public static int numberOfBreakPoints;
	public static int numberOfChargingSegments;

	public static MultipleFunctions multipleFunctions = new MultipleFunctions();
	
	public static boolean lowIrradianceAlways;
	public static double[] lowIrrandiancePerPeriod;
	public static int daysWithLowIrradiance;
	
	
	public GlobalData() {}
	
	public static void ReadJson(String fileName) {
        JSONParser jsonParser = new JSONParser();
         
        try (FileReader reader = new FileReader(fileName))
        {
            String json = jsonParser.parse(reader).toString();
            
            JSONObject data = new JSONObject(json);

            parseData(data);
 
        } catch (FileNotFoundException e) {
        	System.out.println(e);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private static void parseData(JSONObject data){
		try {
			
			random = new Random(data.getInt("seed"));

			maxPassengers = data.getInt("maxPassengers");
			maxAveragePassengerWaitingTime = data.getDouble("maxAveragePassengerWaitingTime");
			minPercentageOfPeopleToTransport = data.getDouble("minPercentageOfPeopleToTransport");
			maxTrips = data.getInt("maxTrips");
			minimumTrips = data.getInt("minimumTrips");

			pvModulesPerArray = data.getInt("pvModulesPerArray");
			
			csCostIntercept = data.getDouble("csCostIntercept");
			csCostSlope = data.getDouble("csCostSlope");
			
	        replications = data.getInt("replications");
	        maxInfeasibleReplications = (int) (data.getDouble("replicationsSignificance") * replications);
	        
	        
	        essMinSoC = data.getDouble("essMinSoC");
	        essNominalSoC = data.getDouble("essNominalSoC");
	        
	        ebMinSOC = data.getDouble("ebMinSOC");
						
			batteryCostBound = data.getBoolean("batteryCostBound");
			batteryBestCSCaseBound = data.getBoolean("batteryBestCSCaseBound");
			csCostBound = data.getBoolean("csCostBound");
			csDistanceCSBound = data.getBoolean("csDistanceCSBound");
			speedDistanceCSBound = data.getBoolean("speedDistanceCSBound");
			speedTripsTimeBound = data.getBoolean("speedTripsTimeBound");
			departureMaxTripsBound = data.getBoolean("departureMaxTripsBound");
			departureTimeBetweenTripsBound = data.getBoolean("departureTimeBetweenTripsBound");
	        
	        irradianceDataPeriod = data.getInt("irradianceDataPeriod");
	        
	        riverSpeed = data.getDouble("riverSpeed");
	        flow = 1;
	        if(!data.getBoolean("riverFlowsInFavorDuringFirstHalf")) {
	        	flow = -1;
	        }
	        
	        departureTreeDeltaInHours = data.getInt("departureTreeDeltaInMin") / 60.0;
			portClosingHour = data.getInt("portClosingHour");
			portOpeningHour = data.getInt("portOpeningHour");			
	        
	        JSONArray arr = data.getJSONArray("muTimeWindows");
	        int timeWindows = arr.length();
	        muTimeWindows = new int[timeWindows];
	        for(int i = 0; i < timeWindows; i++) {
	        	muTimeWindows[i] = arr.getInt(i);
	        }

	        arr = data.getJSONArray("batteryCapacities");
	        int batCaps = arr.length();
	        batteryCapacities = new int[batCaps];
	        for(int i = 0; i < batCaps; i++) {
	        	batteryCapacities[i] = arr.getInt(i);
	        }
	        
	        arr = data.getJSONArray("pvArrays");
	        int pvAs = arr.length();
	        pvArrays = new int[pvAs + 1];
	        for(int i = 0; i < pvAs; i++) {
	        	pvArrays[i + 1] = arr.getInt(i);
	        }
	        pvArraysLastIndex = pvAs;
	        
	        arr = data.getJSONArray("speeds");
	        int sps = arr.length();
	        speeds = new int[sps];
	        for(int i = 0; i < sps; i++) {
	        	speeds[i] = arr.getInt(i);
	        }
	        minSpeed = speeds[0];
	        
	        numberOfNodes = data.getInt("nodes");
	        segments = numberOfNodes - 1;
	        nodesToVisit = new int[numberOfNodes];
	        for(int i = 1; i < numberOfNodes; i++) {
	        	nodesToVisit[i] = i;
	        }
	        
	        actualRealNodes = (numberOfNodes + 1) / 2;
	        nodes = new Node[actualRealNodes];
	        for(int i = 0; i < actualRealNodes; i++) {
	        	nodes[i] = new Node(i);
	        }
	        midNode = actualRealNodes - 1;
	        
	        
	        
	        arr = data.getJSONArray("solarIrradiance");
	        fractionOfHour = irradianceDataPeriod / 60.0;
	        int irradianceData = arr.length();
	        solarEnergyPerSqrM = new double[irradianceData];
	        for(int i = 0; i < irradianceData; i++) {
	        	solarEnergyPerSqrM[i] = arr.getDouble(i) * fractionOfHour;
	        }
	        
	        arr = data.getJSONArray("passengerMuMainPort");
	        int mus = arr.length();
	        passengerMuMainPort = new double[mus];
	        for(int i = 0; i < mus; i++) {
	        	passengerMuMainPort[i] = arr.getDouble(i);
	        }
	        
	        arr = data.getJSONArray("passengerMuMidPort");
	        passengerMuMidPort = new double[mus];
	        for(int i = 0; i < mus; i++) {
	        	passengerMuMidPort[i] = arr.getDouble(i);
	        }
	        
	        arr = data.getJSONArray("outageDurationMuMinutes");
	        int candSpots = arr.length();
	        outageDurationMuMinutes = new double[candSpots];
	        for(int i = 0; i < candSpots; i++) {
	        	outageDurationMuMinutes[i] = arr.getDouble(i);
	        }
	        
	        arr = data.getJSONArray("timeToFollowingOutageMuMinutes");
	        timeToFollowingOutageMuMinutes = new double[candSpots];
	        for(int i = 0; i < candSpots; i++) {
	        	timeToFollowingOutageMuMinutes[i] = arr.getDouble(i);
	        }
	        
	        
	        lastPassengerArrivalHour = muTimeWindows[timeWindows - 1];

	        dataPerDay = (int) (24 / fractionOfHour);
	        daysPerYear = data.getInt("daysPerYear");
	        
	        
	        int startingYear = data.getInt("startingYear");
	        int finalYear = data.getInt("finalYear");
	        firstYearInTimeSeries = data.getInt("firstYearInTimeSeries");
	        
	        
        	startingYearInTimeSeries = startingYear - firstYearInTimeSeries;
        	finalYearInTimeSeries = finalYear - firstYearInTimeSeries;
	        firstDayToEvaluate = 365 * startingYearInTimeSeries;
	        
	        totalYears = finalYearInTimeSeries + 1 - startingYearInTimeSeries;
	        totalDays = totalYears * daysPerYear;
	        indexOfLastDay = firstDayToEvaluate + totalDays - 1;
	        
	        maxInfeasibleDays = (int) (totalDays * data.getDouble("significance"));
	        
	        arr = data.getJSONArray("csAnnualMaintenance");
	        csTotalMaintenance = new double[arr.length()];
	        for(int i = 0; i< arr.length(); i++) {
	        	csTotalMaintenance[i] = arr.getDouble(i) * totalYears;
	        }

	        double usdPerkWhDueToCO2 = data.getDouble("usdPerkWhDueToCO2");
	        
			arr = data.getJSONArray("energyPrices");
			energyPricesWithCO2 = new double[11];
	        for(int i = 0; i < 11; i++) {
	        	energyPricesWithCO2[i] = arr.getDouble(i) + usdPerkWhDueToCO2;
	        }

	        
	        arr = data.getJSONArray("distances");
	        int dists = arr.length();
	        distances = new double[dists];
	        for(int i = 0; i < dists; i++) {
	        	distances[i] = arr.getDouble(i);
	        }
	        
	        distanceToMidPoint = distances[actualRealNodes - 1];
	        
	        ObjectMapper objectMapper = new ObjectMapper();
	        objectMapper.registerModule(new ParanamerModule());
	        
	        arr = data.getJSONArray("baseChargingPower");
	        int pwSegments = arr.length();
	        double[] baseChargingPower = new double[pwSegments];
	        for(int i = 0; i < pwSegments; i++) {
	        	baseChargingPower[i] = arr.getDouble(i);
	        }
	        
	        pwSlopeRates = new double[pwSegments];
	        pwSlopeRates[0] = 1;
	        for(int i = 1; i < pwSegments; i++) {
	        	pwSlopeRates[i] = baseChargingPower[i] / baseChargingPower[0]; 
	        }
	        
	        arr = data.getJSONArray("breakPoints");
	        int totalBreakpoints = arr.length();
	        breakPoints = new double[totalBreakpoints];
	        for(int i = 0; i < totalBreakpoints; i++) {
	        	breakPoints[i] = arr.getDouble(i);
	        }

	        
	        numberOfBreakPoints = totalBreakpoints;
	        numberOfChargingSegments = pwSegments;

	        
	        batteryModule = objectMapper.readValue(data.getJSONObject("batteryModule").toString(), BatteryModule.class);
	        
	        essModule = objectMapper.readValue(data.getJSONObject("essModule").toString(), ESSModule.class);
	        
	        pvModule = objectMapper.readValue(data.getJSONObject("pvModule").toString(), PVModule.class);

	        
	        arr = data.getJSONArray("consumptionArray");
	        int consumptionArrayLength = arr.length();
	        double[] consumptionArray = new double[consumptionArrayLength];
	        for(int i = 0; i < consumptionArrayLength; i++) {
	        	consumptionArray[i] = arr.getDouble(i);
	        }
	        int consumptionGenerationMinBattery = data.getInt("consumptionGenerationMinBattery");
	        int consumptionGenerationMaxBattery = data.getInt("consumptionGenerationMaxBattery");
	        
	        int consumptionGenerationMinSpeed = data.getInt("consumptionGenerationMinSpeed");
	        int consumptionGenerationMaxSpeed = data.getInt("consumptionGenerationMaxSpeed");
	        
	        int consumptionGenerationMinPassengers = data.getInt("consumptionGenerationMinPassengers");
	        int consumptionGenerationMaxPassengers = data.getInt("consumptionGenerationMaxPassengers");


	        SetConsumptions(consumptionArray, consumptionGenerationMinBattery, consumptionGenerationMaxBattery,
	        		consumptionGenerationMinSpeed, consumptionGenerationMaxSpeed, consumptionGenerationMinPassengers,
	        		consumptionGenerationMaxPassengers);
	        
	        SetTimes(speeds[0], speeds[sps - 1]);
	        nullifyTimeAdjustmentFactor = 60 / GlobalData.irradianceDataPeriod;
	        
	        double converterCost = data.getDouble("converterCost");
	        int panelsPerConverter = (int) Math.floor(data.getDouble("converterInputPower") / pvModule.getPeakPower());
	        SetConverterCosts(converterCost, panelsPerConverter);
			
			lowIrradianceAlways = data.getBoolean("lowIrradianceAlways");
			if(lowIrradianceAlways) {
				arr = data.getJSONArray("lowIrradianceData");
		        int lowIrradianceDataLength = arr.length();
		        lowIrrandiancePerPeriod = new double[lowIrradianceDataLength];
		        for(int i = 0; i < lowIrradianceDataLength; i++) {
		        	lowIrrandiancePerPeriod[i] = arr.getDouble(i) * fractionOfHour;
		        }
		        daysWithLowIrradiance = lowIrradianceDataLength / dataPerDay;
			}

		}catch (Exception e) {
			
			System.out.println(e);
		}
        
    }

	
	public static void SetConsumptions(double[] consumptionArray, int consumptionGenerationMinBattery, int consumptionGenerationMaxBattery,
			int consumptionGenerationMinSpeed, int consumptionGenerationMaxSpeed, int consumptionGenerationMinPassengers,
    		int consumptionGenerationMaxPassengers) {
		
		consumptions = new double[consumptionGenerationMaxBattery + 1][consumptionGenerationMaxSpeed + 1]
				[consumptionGenerationMaxPassengers + 1][segments];

		int currentIndex = 0;
		
		for(int bat = consumptionGenerationMinBattery; bat <= consumptionGenerationMaxBattery; bat++) {
			for(int speed = consumptionGenerationMinSpeed; speed <= consumptionGenerationMaxSpeed; speed++) {
				for(int passenger = consumptionGenerationMinPassengers; passenger <= consumptionGenerationMaxPassengers; passenger++) {
					for(int segment = 0; segment < segments; segment++) {
						consumptions[bat][speed][passenger][segment] = consumptionArray[currentIndex];
						currentIndex++;
					}
					
				}
			}
	    }
	}
	
	public static void SetTimes(int minSpeed, int maxSpeed) {
		travelTimes = new double[maxSpeed + 1][segments];
		for(int speed = minSpeed; speed <= maxSpeed; speed++) {
			
			for(int i = 0; i < midNode; i++) {
				travelTimes[speed][i] = (distances[i + 1] - distances[i]) / (speed + riverSpeed * flow);
			}
			for(int i = midNode; i < segments; i++) {
				travelTimes[speed][i] = (distances[i + 1] - distances[i]) / (speed - riverSpeed * flow);
			}
				
			
			
	    }
	}
	
	public static void SetConverterCosts(double converterCost, int panelsPerConverter) {
		int pvArraysLength = pvArrays.length;
		converterCosts = new double[pvArraysLength];
		for(int i = 1; i < pvArraysLength; i++) {
			converterCosts[i] = converterCost * pvArrays[i] * pvModulesPerArray / panelsPerConverter;
		}
		
	}



}
