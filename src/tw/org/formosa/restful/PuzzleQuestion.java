package tw.org.formosa.restful;

import java.math.BigDecimal;

public class PuzzleQuestion { // PuzzleQuestion類別，主要用來讀取、設定PuzzleQuestion資料

	private int puzzleQuestionID;
	private String puzzleQuestionName;
	private String county;
	private String puzzleGetAttractionName;
	private BigDecimal attractionLongitude;
	private BigDecimal attractionLatitude;
	private int level;
	private String image;

	public int getPuzzleQuestionID() {
		return puzzleQuestionID;
	}

	public void setPuzzleQuestionID(int puzzleQuestionID) {
		this.puzzleQuestionID = puzzleQuestionID;
	}

	public String getPuzzleQuestionName() {
		return puzzleQuestionName;
	}

	public void setPuzzleQuestionName(String puzzleQuestionName) {
		this.puzzleQuestionName = puzzleQuestionName;
	}
	
	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}
	
	public String getPuzzleGetAttractionName() {
		return puzzleGetAttractionName;
	}

	public void setPuzzleGetAttractionName(String puzzleGetAttractionName) {
		this.puzzleGetAttractionName = puzzleGetAttractionName;
	}
	
	public BigDecimal getAttractionLongitude() {
		return attractionLongitude;
	}

	public void setAttractionLongitude(BigDecimal attractionLongitude) {
		this.attractionLongitude = attractionLongitude;
	}

	public BigDecimal getAttractionLatitude() {
		return attractionLatitude;
	}

	public void setAttractionLatitude(BigDecimal attractionLatitude) {
		this.attractionLatitude = attractionLatitude;
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	

	@Override
	public String toString() {
		return "{\"puzzleQuestionID\"=\"" + puzzleQuestionID
				+ "\", \"puzzleQuestionName\"=\"" + puzzleQuestionName
				+ "\", \"county\"=\"" + county 
				+ "\", \"puzzleGetAttractionName\"=\"" + puzzleGetAttractionName 
				+ "\", \"attractionLongitude\"=\"" + attractionLongitude 
				+ "\", \"attractionLatitude\"=\"" + attractionLatitude 
				+ "\", \"level\"=\"" + level
				+ "\", \"image\"=\"" + image+ "\"}";
	}
}
