package tw.org.formosa.restful;

import java.math.BigDecimal;

public class PairTracing { // PairTracing類別，主要用來讀取、設定PairTracing資料

	private int pairID;
	private int userID;
	private BigDecimal pairLongitude;
	private BigDecimal pairLatitude;
	private int tracingUserID;
	private BigDecimal tracingLongitude;
	private BigDecimal tracingLatitude;

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

	public int getTracingUserID() {
		return tracingUserID;
	}

	public void setTracingUserID(int tracingUserID) {
		this.tracingUserID = tracingUserID;
	}

	public BigDecimal getTracingLongitude() {
		return tracingLongitude;
	}

	public void setTracingLongitude(BigDecimal tracingLongitude) {
		this.tracingLongitude = tracingLongitude;
	}

	public BigDecimal getTracingLatitude() {
		return tracingLatitude;
	}

	public void setTracingLatitude(BigDecimal tracingLatitude) {
		this.tracingLatitude = tracingLatitude;
	}

	@Override
	public String toString() {
		return "{\"pairID\"=\"" + pairID + "\", \"userID\"=\"" + userID
				+ "\", \"pairLongitude\"=\"" + pairLongitude
				+ "\", \"pairLatitude\"=\"" + pairLatitude
				+ "\", \"tracingUserID\"=\"" + tracingUserID
				+ "\", \"tracingLongitude\"=\"" + tracingLongitude
				+ "\", \"tracingLatitude\"=\"" + tracingLatitude + "\"}";
	}
}
