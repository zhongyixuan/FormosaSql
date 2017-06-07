package tw.org.formosa.restful;

public class TravelPair {
	
	private int travelPairID;
	private int userID;
	private int travelID;
	private Boolean paired;
	
	public int getTravelPairID() {
		return travelPairID;
	}

	public void setTravelPairID(int travelPairID) {
		this.travelPairID = travelPairID;
	}
	
	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}
	
	public int getTravelID() {
		return travelID;
	}

	public void setTravelID(int travelID) {
		this.travelID = travelID;
	}
	
	public Boolean getPaired() {
		return paired;
	}

	public void setPaired(Boolean paired) {
		this.paired = paired;
	}
	
	@Override
	public String toString() {
		return "{\"travelPairID\"=\"" + travelPairID + "\", \"userID\"=\"" + userID
				+ "\", \"travelID\"=\"" + travelID + "\", \"paired\"=\""
				+ paired + "\"}";
	}
}
