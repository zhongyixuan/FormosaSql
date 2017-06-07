package tw.org.formosa.restful;

import java.math.BigDecimal;

public class Store { // Store類別，主要用來讀取、設定合作商家資料

	private int storeID;
	private String storeName;
	private String storeType;
	private String storeAddress;
	private BigDecimal storeLongitude;
	private BigDecimal storeLatitude;

	public int getStoreID() {
		return storeID;
	}

	public void setStoreID(int storeID) {
		this.storeID = storeID;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getStoreType() {
		return storeType;
	}

	public void setStoreType(String storeType) {
		this.storeType = storeType;
	}

	public String getStoreAddress() {
		return storeAddress;
	}

	public void setStoreAddress(String storeAddress) {
		this.storeAddress = storeAddress;
	}

	public BigDecimal getStoreLongitude() {
		return storeLongitude;
	}

	public void setStoreLongitude(BigDecimal storeLongitude) {
		this.storeLongitude = storeLongitude;
	}

	public BigDecimal getStoreLatitude() {
		return storeLatitude;
	}

	public void setStoreLatitude(BigDecimal storeLatitude) {
		this.storeLatitude = storeLatitude;
	}

	@Override
	public String toString() {
		return "{\"storeID\"=\"" + storeID + "\", \"storeName\"=\"" + storeName
				+ "\", \"storeType\"=\"" + storeType
				+ "\", \"storeAddress\"=\"" + storeAddress
				+ "\", \"storeLongitude\"=\"" + storeLongitude
				+ "\", \"storeLatitude\"=\"" + storeLatitude + "\"}";
	}
}
