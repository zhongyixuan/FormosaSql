package tw.org.formosa.restful;

public class Collection { // Collection���O�A�D�n�Ψ�Ū���B�]�wCollection���

	private int userID;
	private int collectionID;
	private String attractionID;
	private String attractionName;
	private String county;

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getCollectionID() {
		return collectionID;
	}

	public void setCollectionID(int collectionID) {
		this.collectionID = collectionID;
	}

	public String getAttractionID() {
		return attractionID;
	}

	public void setAttractionID(String attractionID) {
		this.attractionID = attractionID;
	}
	
	public String getAttractionName() {
		return attractionName;
	}

	public void setAttractionName(String attractionName) {
		this.attractionName = attractionName;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	@Override
	public String toString() {
		return "{\"userID\"=\"" + userID + "\", \"collectionID\"=\""
				+ collectionID
				+ "\", \"attractionID\"=\"" + attractionID
				+ "\", \"attractionName\"=\"" + attractionName
				+ "\", \"county\"=\"" + county + "\"}";
	}
}