package tw.org.formosa.restful;

public class Puzzle { //Puzzle類別，主要用來讀取、設定Puzzle資料
	
	private int userID;
	private int puzzleQuestionID;
	private String puzzleGetAttractionName;
	private String county;
	private int level;
	
	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}
	
	public int getPuzzleQuestionID() {
		return puzzleQuestionID;
	}

	public void setPuzzleQuestionID(int puzzleQuestionID) {
		this.puzzleQuestionID = puzzleQuestionID;
	}
	
	public String getPuzzleGetAttractionName() {
		return puzzleGetAttractionName;
	}

	public void setPuzzleGetAttractionName(String puzzleGetAttractionName) {
		this.puzzleGetAttractionName = puzzleGetAttractionName;
	}
	
	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	@Override
	public String toString() {
		return "{\"userID\"=\"" + userID + "\", \"puzzleQuestionID\"=\"" + puzzleQuestionID
				+ "\", \"puzzleGetAttractionName\"=\"" + puzzleGetAttractionName
				+ "\", \"county\"=\"" + county
				+ "\", \"level\"=\"" + level + "\"}";
	}

}
