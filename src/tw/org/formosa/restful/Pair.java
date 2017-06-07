package tw.org.formosa.restful;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;

public class Pair { // Pair類別，主要用來讀取、設定Pair資料

	private int pairID;
	private int userID;
	private String shopName;
	private String productName;
	private int productPrice;
	private String preferentialType;
	private String pairAddress;
	private String userFeature;
	private Timestamp pairTime;
	private Time waitTime;
	private BigDecimal pairLongitude;
	private BigDecimal pairLatitude;
	private Boolean paired;

	public int getPairID() {
		return pairID;
	}

	public void setPairID(int pairID) {
		this.pairID = pairID;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(int productPrice) {
		this.productPrice = productPrice;
	}
	
	public String getPreferentialType() {
		return preferentialType;
	}

	public void setPreferentialType(String preferentialType) {
		this.preferentialType = preferentialType;
	}

	public String getPairAddress() {
		return pairAddress;
	}

	public void setPairAddress(String pairAddress) {
		this.pairAddress = pairAddress;
	}

	public String getUserFeature() {
		return userFeature;
	}

	public void setUserFeature(String userFeature) {
		this.userFeature = userFeature;
	}

	public Timestamp getPairTime() {
		return pairTime;
	}

	public void setPairTime(Timestamp pairTime) {
		this.pairTime = pairTime;
	}

	public Time getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(Time waitTime) {
		this.waitTime = waitTime;
	}

	public BigDecimal getPairLongitude() {
		return pairLongitude;
	}

	public void setPairLongitude(BigDecimal pairLongitude) {
		this.pairLongitude = pairLongitude;
	}

	public BigDecimal getPairLatitude() {
		return pairLatitude;
	}

	public void setPairLatitude(BigDecimal pairLatitude) {
		this.pairLatitude = pairLatitude;
	}
	
	public Boolean getPaired() {
		return paired;
	}

	public void setPaired(Boolean paired) {
		this.paired = paired;
	}

	@Override
	public String toString() {
		String spl[] = pairTime.toString().split(" ");
		return "{\"pairID\"=\"" + pairID + "\", \"userID\"=\"" + userID
				+ "\", \"shopName\"=\"" + shopName + "\", \"productName\"=\""
				+ productName + "\", \"productPrice\"=\"" + productPrice
				+ "\", \"preferentialType\"=\"" + preferentialType
				+ "\", \"pairAddress\"=\"" + pairAddress
				+ "\", \"userFeature\"=\"" + userFeature
				+ "\", \"pairTime\"=\"" + pairTime + "\", \"waitTime\"=\""
				+ waitTime + "\", \"pairLongitude\"=\"" + pairLongitude
				+ "\", \"pairLatitude\"=\"" + pairLatitude
				+ "\", \"paired\"=\"" + paired + "\"}";
	}
}
