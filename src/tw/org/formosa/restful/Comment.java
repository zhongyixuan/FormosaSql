package tw.org.formosa.restful;

import java.sql.Timestamp;

public class Comment { // Comment類別，主要用來讀取、設定Comment資料

	private int userID;
	private int commentID;
	private String attractionID;
	private String attractionType;
	private String userComment;
	private int userScore;
	private Timestamp commentTime;

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getCommentID() {
		return commentID;
	}

	public void setCommentID(int commentID) {
		this.commentID = commentID;
	}

	public String getAttractionID() {
		return attractionID;
	}

	public void setAttractionID(String attractionID) {
		this.attractionID = attractionID;
	}

	public String getAttractionType() {
		return attractionType;
	}

	public void setAttractionType(String attractionType) {
		this.attractionType = attractionType;
	}

	public String getUserComment() {
		return userComment;
	}

	public void setUserComment(String userComment) {
		this.userComment = userComment;
	}

	public int getUserScore() {
		return userScore;
	}

	public void setUserScore(int userScore) {
		this.userScore = userScore;
	}

	public Timestamp getCommentTime() {
		return commentTime;
	}

	public void setCommentTime(Timestamp commentTime) {
		this.commentTime = commentTime;
	}

	@Override
	public String toString() {
		String spl[] = commentTime.toString().split(" ");
		return "{\"userID\"=\"" + userID + "\", \"commentID\"=\"" + commentID
				+ "\", \"attractionID\"=\"" + attractionID
				+ "\", \"attractionType\"=\"" + attractionType
				+ "\", \"userComment\"=\"" + userComment
				+ "\", \"userScore\"=\"" + userScore + "\", \"commentTime\"=\""
				+ spl[0] + "\"}";
	}
}
