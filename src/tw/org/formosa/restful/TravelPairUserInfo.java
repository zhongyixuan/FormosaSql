package tw.org.formosa.restful;

public class TravelPairUserInfo {

	private int travelPairID;
	private int userID;
	private int pairUserID;
	private String pairUserName;
	private String pairUserEMail;
	private String pairUserLine;
	private String pairUserPhone;
	private Boolean userSure;
	
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
	
	public int getPairUserID() {
		return pairUserID;
	}

	public void setPairUserID(int pairUserID) {
		this.pairUserID = pairUserID;
	}
	
	public String getPairUserName() {
		return pairUserName;
	}

	public void setPairUserName(String pairUserName) {
		this.pairUserName = pairUserName;
	}
	
	public String getPairUserEMail() {
		return pairUserEMail;
	}

	public void setPairUserEMail(String pairUserEMail) {
		this.pairUserEMail = pairUserEMail;
	}
	
	public String getPairUserLine() {
		return pairUserLine;
	}

	public void setPairUserLine(String pairUserLine) {
		this.pairUserLine = pairUserLine;
	}
	
	public String getPairUserPhone() {
		return pairUserPhone;
	}

	public void setPairUserPhone(String pairUserPhone) {
		this.pairUserPhone = pairUserPhone;
	}
	
	public Boolean getUserSure() {
		return userSure;
	}

	public void setUserSure(Boolean userSure) {
		this.userSure = userSure;
	}
	
	@Override
	public String toString() {
		return "{\"travelPairID\"=\"" + travelPairID
				+ "\", \"userID\"=\"" + userID
				+ "\", \"pairUserID\"=\"" + pairUserID
				+ "\", \"pairUserName\"=\"" + pairUserName
				+ "\", \"pairUserEMail\"=\"" + pairUserEMail
				+ "\", \"pairUserLine\"=\"" + pairUserLine
				+ "\", \"pairUserPhone\"=\"" + pairUserPhone
				+ "\", \"userSure\"=\"" + userSure + "\"}";
	}
}