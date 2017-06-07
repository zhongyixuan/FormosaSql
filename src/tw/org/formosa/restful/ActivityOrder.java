package tw.org.formosa.restful;

import java.sql.Timestamp;

public class ActivityOrder { // ActivityOrder類別，主要用來讀取、設定ActivityOrder資料

	private int userID;
	private int activityOrderID;
	private String activityID;
	private String activityOrderName;
	private String activityUserPhone;
	private String activityUserAddress;
	private String activityName;
	private Timestamp activityOrderDate;
	private int activityOrderCount;
	private String activityUserNote;

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getActivityOrderID() {
		return activityOrderID;
	}

	public void setActivityOrderID(int activityOrderID) {
		this.activityOrderID = activityOrderID;
	}

	public String getActivityID() {
		return activityID;
	}

	public void setActivityID(String activityID) {
		this.activityID = activityID;
	}

	public String getActivityOrderName() {
		return activityOrderName;
	}

	public void setActivityOrderName(String activityOrderName) {
		this.activityOrderName = activityOrderName;
	}

	public String getActivityUserPhone() {
		return activityUserPhone;
	}

	public void setActivityUserPhone(String activityUserPhone) {
		this.activityUserPhone = activityUserPhone;
	}

	public String getActivityUserAddress() {
		return activityUserAddress;
	}

	public void setActivityUserAddress(String activityUserAddress) {
		this.activityUserAddress = activityUserAddress;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public Timestamp getActivityOrderDate() {
		return activityOrderDate;
	}

	public void setActivityOrderDate(Timestamp activityOrderDate) {
		this.activityOrderDate = activityOrderDate;
	}

	public int getActivityOrderCount() {
		return activityOrderCount;
	}

	public void setActivityOrderCount(int activityOrderCount) {
		this.activityOrderCount = activityOrderCount;
	}

	public String getActivityUserNote() {
		return activityUserNote;
	}

	public void setActivityUserNote(String activityUserNote) {
		this.activityUserNote = activityUserNote;
	}

	@Override
	public String toString() {
		String spl[] = activityOrderDate.toString().split(" ");
		return "{\"userID\"=\"" + userID + "\", \"activityOrderID\"=\""
				+ activityOrderID + "\", \"activityID\"=\"" + activityID
				+ "\", \"activityOrderName\"=\"" + activityOrderName
				+ "\", \"activityUserPhone\"=\"" + activityUserPhone
				+ "\", \"activityUserAddress\"=\"" + activityUserAddress
				+ "\", \"activityName\"=\"" + activityName
				+ "\", \"activityOrderDate\"=\"" + spl[0]
				+ "\", \"activityOrderCount\"=\"" + activityOrderCount
				+ "\", \"activityUserNote\"=\"" + activityUserNote + "\"}";
	}
}
