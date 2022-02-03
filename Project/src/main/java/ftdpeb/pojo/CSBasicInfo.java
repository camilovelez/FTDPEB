package ftdpeb.pojo;

public class CSBasicInfo {

	boolean installed = false;
	int pvArrayIndex = 1;
	int essModules = 0;
	int plug = 0;
	String type = "PVGrid";
	
	public boolean isInstalled() {
		return installed;
	}
	public int getPvArrayIndex() {
		return pvArrayIndex;
	}
		
	public int getPlug() {
		return plug;
	}
	public String getType() {
		return type;
	}
	
	public int getEssModules() {
		return essModules;
	}

	public void setEssModules(int essModules) {
		this.essModules = essModules;
	}

	public void setInstalled(boolean installed) {
		this.installed = installed;
	}
	public void setPvArrayIndex(int pvArrayIndex) {
		this.pvArrayIndex = pvArrayIndex;
	}
	public void setPlug(int plug) {
		this.plug = plug;
	}
	public void setType(String type) {
		this.type = type;
	}
	public CSBasicInfo() {
		this.installed = false;
		this.pvArrayIndex = 1;
		this.essModules = 0;
		this.plug = 0;
		this.type = "PVGrid";
	}

	
}
