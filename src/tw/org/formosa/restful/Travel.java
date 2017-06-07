package tw.org.formosa.restful;

import java.sql.Timestamp;

public class Travel { // Travel���O�A�D�n�Ψ�Ū���B�]�wTravel���

	private int travelID;
	private int userID;
	private String travelName;
	private Timestamp travelDate;
	private int travelDays;

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

	public String getTravelName() {
		return travelName;
	}

	public void setTravelName(String travelName) {
		this.travelName = travelName;
	}

	public Timestamp getTravelDate() {
		return travelDate;
	}

	public void setTravelDate(Timestamp travelDate) {
		this.travelDate = travelDate;
	}

	public int getTravelDays() {
		return travelDays;
	}

	public void setTravelDays(int travelDays) {
		this.travelDays = travelDays;
	}

	@Override
	public String toString() {
		String spl[] = travelDate.toString().split(" ");
		return "{\"travelID\"=\"" + travelID + "\", \"userID\"=\"" + userID
				+ "\", \"travelName\"=\"" + travelName
				+ "\", \"travelDate\"=\"" + spl[0] + "\", \"travelDays\"=\""
				+ travelDays + "\"}";
	}
}
