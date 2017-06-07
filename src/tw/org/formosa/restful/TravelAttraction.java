package tw.org.formosa.restful;

import java.sql.Timestamp;

public class TravelAttraction { // TravelAttraction類別，主要用來讀取、設定TravelAttraction資料

	private int travelID;
	private String attractionName;
	private Timestamp dayDate;

	public int getTravelID() {
		return travelID;
	}

	public void setTravelID(int travelID) {
		this.travelID = travelID;
	}

	public String getAttractionName() {
		return attractionName;
	}

	public void setAttractionName(String attractionName) {
		this.attractionName = attractionName;
	}
	
	public Timestamp getDayDate() {
		return dayDate;
	}

	public void setDayDate(Timestamp dayDate) {
		this.dayDate = dayDate;
	}

	@Override
	public String toString() {
		String spl[] = dayDate.toString().split(" ");
		return "{\"travelID\"=\"" + travelID + "\", \"attractionName\"=\""
				+ attractionName
				+ "\", \"dayDate\"=\"" + dayDate + "\"}";
	}
}
