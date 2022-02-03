package ftdpeb.simulation;

public class Event implements  Comparable<Event> {
	double executionHour;
	String action; 
	int routeId;
	int nodeId;
	
	
	
	public Event(double executionHour, String action, int routeId, int nodeId) {
		this.executionHour = executionHour;
		this.action = action;
		this.routeId = routeId;
		this.nodeId = nodeId;
	}
	
	public Event() {}
	
	
	public double getExecutionHour() {
		return executionHour;
	}
	public void setExecutionHour(double executionHour) {
		this.executionHour = executionHour;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getRouteId() {
		return routeId;
	}
	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}
	public int getNodeId() {
		return nodeId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	
	 @Override
	 public int compareTo(Event e) {
        return new Double(getExecutionHour()).compareTo( e.getExecutionHour());
    }

}
