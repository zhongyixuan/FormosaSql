package tw.org.formosa.restful;

public class User {  //User類別，主要用來讀取、設定User資料
	
	private int userID;
	private String userName;
	private String userAccount;
	private String userPassword;
	private String userEMail;

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUserEMail() {
		return userEMail;
	}

	public void setUserEMail(String userEMail) {
		this.userEMail = userEMail;
	}

	@Override
	public String toString() {
		return "{\"userID\"=\"" + userID + "\", \"userName\"=\"" + userName
				+ "\", \"userPassword\"=\"" + userPassword + "\", \"userEMail\"=\""
				+ userEMail + "\", \"userAccount\"=\"" + userAccount + "\"}";
	}
}
