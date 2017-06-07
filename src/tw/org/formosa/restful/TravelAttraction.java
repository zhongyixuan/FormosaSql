package tw.org.formosa.restful;

import java.sql.Timestamp;

public class TravelAttraction { // TravelAttraction���O�A�D�n�Ψ�Ū���B�]�wTravelAttraction���

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
