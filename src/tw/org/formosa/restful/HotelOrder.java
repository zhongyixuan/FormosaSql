package tw.org.formosa.restful;

import java.sql.Timestamp;

public class HotelOrder { // HotelOrder類別，主要用來讀取、設定HotelOrder資料

	private int userID;
	private int hotelOrderID;
	private String hotelOrderName;
	private String hotelUserPhone;
	private String hotelUserAddress;
	private String hotelName;
	private String hotelAddress;
	private Timestamp hotelOrderDate;
	private int hotelOrderCount;
	private String hotelUserNote;

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getHotelOrderID() {
		return hotelOrderID;
	}

	public void setHotelOrderID(int hotelOrderID) {
		this.hotelOrderID = hotelOrderID;
	}

	public String getHotelOrderName() {
		return hotelOrderName;
	}

	public void setHotelOrderName(String hotelOrderName) {
		this.hotelOrderName = hotelOrderName;
	}

	public String getHotelUserPhone() {
		return hotelUserPhone;
	}

	public void setgetHotelUserPhone(String hotelUserPhone) {
		this.hotelUserPhone = hotelUserPhone;
	}

	public String getHotelUserAddress() {
		return hotelUserAddress;
	}

	public void setHotelUserAddress(String hotelUserAddress) {
		this.hotelUserAddress = hotelUserAddress;
	}

	public String getHotelName() {
		return hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}
	
	public String getHotelAddress() {
		return hotelAddress;
	}

	public void setHotelAddress(String hotelAddress) {
		this.hotelAddress = hotelAddress;
	}

	public Timestamp getHotelOrderDate() {
		return hotelOrderDate;
	}

	public void setHotelOrderDate(Timestamp hotelOrderDate) {
		this.hotelOrderDate = hotelOrderDate;
	}

	public int getHotelOrderCount() {
		return hotelOrderCount;
	}

	public void setHotelOrderCount(int hotelOrderCount) {
		this.hotelOrderCount = hotelOrderCount;
	}

	public String getHotelUserNote() {
		return hotelUserNote;
	}

	public void setHotelUserNote(String hotelUserNote) {
		this.hotelUserNote = hotelUserNote;
	}

	@Override
	public String toString() {
		String spl[] = hotelOrderDate.toString().split(" ");
		return "{\"userID\"=\"" + userID + "\", \"hotelOrderID\"=\""
				+ hotelOrderID + "\", \"hotelOrderName\"=\"" + hotelOrderName
				+ "\", \"hotelUserPhone\"=\"" + hotelUserPhone
				+ "\", \"hotelUserAddress\"=\"" + hotelUserAddress
				+ "\", \"hotelName\"=\"" + hotelName
				+ "\", \"hotelAddress\"=\"" + hotelAddress
				+ "\", \"hotelOrderDate\"=\"" + spl[0]
				+ "\", \"hotelOrderCount\"=\"" + hotelOrderCount
				+ "\", \"hotelUserNote\"=\"" + hotelUserNote + "\"}";
	}
}
