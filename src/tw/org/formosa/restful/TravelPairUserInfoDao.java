package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TravelPairUserInfoDao {
	
	private Connection connection;
	private String tableName = "TravelPair配對人資料";

	public TravelPairUserInfoDao() {
			connection = DbUtil.getConnection();
	}
	
	public boolean addTravelPairUserInfo(TravelPairUserInfo travelPairUserInfo, User user, User pairUser) { // 新增一筆PairTracing，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("insert into `"
							+ tableName
							+ "`(travelPairID, userID, pairUserID, pairUserName, pairUserEMail, pairUserLine, pairUserPhone) values (?, ?, ?, ?, ?, ?, ?)");
			// Parameters start with 1
			preparedStatement.setInt(1, travelPairUserInfo.getTravelPairID());
			preparedStatement.setInt(2, user.getUserID());
			preparedStatement.setInt(3, pairUser.getUserID());
			preparedStatement.setString(4, travelPairUserInfo.getPairUserName());
			preparedStatement.setString(5, travelPairUserInfo.getPairUserEMail());
			preparedStatement.setString(6, travelPairUserInfo.getPairUserLine());
			preparedStatement.setString(7, travelPairUserInfo.getPairUserPhone());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException");
			return false;
		}
	}
	
	public boolean updateTravelPairUserInfo(TravelPairUserInfo travelPairUserInfo) { // 更新PairTracing內容，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("UPDATE `"
							+ tableName
							+ "` SET `pairUserName` =?, `pairUserEMail` =?, `pairUserLine` =?, `pairUserPhone` =?"
							+ " WHERE `travelPairID` =?");
			preparedStatement.setString(1, travelPairUserInfo.getPairUserName());
			preparedStatement.setString(2, travelPairUserInfo.getPairUserEMail());
			preparedStatement.setString(3, travelPairUserInfo.getPairUserLine());
			preparedStatement.setString(4, travelPairUserInfo.getPairUserPhone());
			preparedStatement.setInt(5, travelPairUserInfo.getTravelPairID());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean alreadySure(TravelPairUserInfo travelPairUserInfo) { // 更新PairTracing內容，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("UPDATE `"
							+ tableName
							+ "` SET `userSure` =?"
							+ " WHERE `travelPairID` =?");
			preparedStatement.setBoolean(1, travelPairUserInfo.getUserSure());
			preparedStatement.setInt(2, travelPairUserInfo.getTravelPairID());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public TravelPairUserInfo getTravelPairUserInfoById(int travelPairID) { // 用Pair編號取得PairTracing，傳回PairTracing物件
		TravelPairUserInfo travelPairUserInfo = new TravelPairUserInfo();
		travelPairUserInfo.setTravelPairID(0);
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where travelPairID=?");
			preparedStatement.setInt(1, travelPairID);
			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next()) {
				travelPairUserInfo.setTravelPairID(rs.getInt("travelPairID"));
				travelPairUserInfo.setUserID(rs.getInt("userID"));
				travelPairUserInfo.setPairUserID(rs.getInt("pairUserID"));
				travelPairUserInfo.setPairUserName(rs.getString("pairUserName"));
				travelPairUserInfo.setPairUserEMail(rs.getString("pairUserEMail"));
				travelPairUserInfo.setPairUserLine(rs.getString("pairUserLine"));
				travelPairUserInfo.setPairUserPhone(rs.getString("pairUserPhone"));
				travelPairUserInfo.setUserSure(rs.getBoolean("userSure"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return travelPairUserInfo;
	}

	public List<TravelPairUserInfo> getTravelPairID(int userID) { // 用userID取得單筆Pair編號，傳回一個PairTracing
		List<TravelPairUserInfo> travelPairUserInfos = new ArrayList<TravelPairUserInfo>();
		
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where userID=? or pairUserID=?");
			preparedStatement.setInt(1, userID);
			preparedStatement.setInt(2, userID);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()){
				TravelPairUserInfo travelPairUserInfo = new TravelPairUserInfo();
				travelPairUserInfo.setTravelPairID(rs.getInt("travelPairID"));
				travelPairUserInfo.setUserSure(rs.getBoolean("userSure"));
				travelPairUserInfos.add(travelPairUserInfo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return travelPairUserInfos;
	}
	
	public List<TravelPairUserInfo> getTravelPairIDByPairUserID(int userID) { // 用userID取得單筆Pair編號，傳回一個PairTracing
		List<TravelPairUserInfo> travelPairUserInfos = new ArrayList<TravelPairUserInfo>();
		
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where pairUserID=?");
			preparedStatement.setInt(1, userID);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()){
				TravelPairUserInfo travelPairUserInfo = new TravelPairUserInfo();
				travelPairUserInfo.setTravelPairID(rs.getInt("travelPairID"));
				travelPairUserInfo.setUserSure(rs.getBoolean("userSure"));
				travelPairUserInfos.add(travelPairUserInfo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return travelPairUserInfos;
	}
	
	public List<TravelPairUserInfo> getTravelPairIDByUserID(int userID) { // 用userID取得單筆Pair編號，傳回一個PairTracing
		List<TravelPairUserInfo> travelPairUserInfos = new ArrayList<TravelPairUserInfo>();
		
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where userID=?");
			preparedStatement.setInt(1, userID);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()){
				TravelPairUserInfo travelPairUserInfo = new TravelPairUserInfo();
				travelPairUserInfo.setTravelPairID(rs.getInt("travelPairID"));
				travelPairUserInfo.setUserSure(rs.getBoolean("userSure"));
				travelPairUserInfos.add(travelPairUserInfo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return travelPairUserInfos;
	}
	
	public List<TravelPairUserInfo> getTravelPairInfoByUserID(int userID) { // 用userID取得單筆Pair編號，傳回一個PairTracing
		List<TravelPairUserInfo> travelPairUserInfos = new ArrayList<TravelPairUserInfo>();
		
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where userID=?");
			preparedStatement.setInt(1, userID);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()){
				TravelPairUserInfo travelPairUserInfo = new TravelPairUserInfo();
				travelPairUserInfo.setTravelPairID(rs.getInt("travelPairID"));
				travelPairUserInfo.setUserID(rs.getInt("userID"));
				travelPairUserInfo.setPairUserID(rs.getInt("pairUserID"));
				travelPairUserInfo.setPairUserName(rs.getString("pairUserName"));
				travelPairUserInfo.setPairUserEMail(rs.getString("pairUserEMail"));
				travelPairUserInfo.setPairUserLine(rs.getString("pairUserLine"));
				travelPairUserInfo.setPairUserPhone(rs.getString("pairUserPhone"));
				travelPairUserInfo.setUserSure(rs.getBoolean("userSure"));
				travelPairUserInfos.add(travelPairUserInfo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return travelPairUserInfos;
	}
}
