package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TravelPairDao {

	private Connection connection;
	private String tableName = "TravelPair";

	public TravelPairDao() {
		connection = DbUtil.getConnection();
	}

	public boolean addTravelPair(TravelPair travelPair, User user) { // 新增一筆Pair，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("insert into `" + tableName
							+ "`(userID, travelID) values (?, ?)");
			// Parameters start with 1
			preparedStatement.setInt(1, user.getUserID());
			preparedStatement.setInt(2, travelPair.getTravelID());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException");
			return false;
		}
	}

	public boolean deleteTravelPair(int travelPairID) { // 用Pair編號刪除Pair，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("delete from `" + tableName
							+ "` where travelPairID=?");
			preparedStatement.setInt(1, travelPairID);
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteTravelPairByUser(int userID) { // 用userID刪除該User所有Pair，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("delete from `" + tableName
							+ "` where userID=?");
			// Parameters start with 1
			preparedStatement.setInt(1, userID);
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean alreadyPair(TravelPair travelPair) { // 更新Pair內容，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("UPDATE `"
							+ tableName
							+ "` SET `paired` =?"
							+ " WHERE `travelPairID` =?");
			preparedStatement.setBoolean(1, travelPair.getPaired());
			preparedStatement.setInt(2, travelPair.getTravelPairID());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public TravelPair getTravelPairById(int travelPairID) { // 用Pair編號取得Pair，傳回Pair物件
		TravelPair travelPair = new TravelPair();
		travelPair.setTravelPairID(0);
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where travelPairID=?");
			preparedStatement.setInt(1, travelPairID);
			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next()) {
				travelPair.setTravelPairID(rs.getInt("travelPairID"));
				travelPair.setUserID(rs.getInt("userID"));
				travelPair.setTravelID(rs.getInt("travelID"));
				travelPair.setPaired(rs.getBoolean("paired"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return travelPair;
	}
	
	public TravelPair getTravelPairID(int userID) { // 用userID取得單筆Pair編號，傳回一個Pair
		TravelPair travelPair = new TravelPair();
		travelPair.setTravelPairID(0);
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where userID=?");
			preparedStatement.setInt(1, userID);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next())
				travelPair.setTravelPairID(rs.getInt("travelPairID"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return travelPair;
	}
	
	public List<TravelPair> getTravelPairList() { // 取得使用者所有Pair清單，傳回Pair陣列
		List<TravelPair> travelPairs = new ArrayList<TravelPair>();

		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				TravelPair travelPair = new TravelPair();
				travelPair.setTravelPairID(rs.getInt("travelPairID"));
				travelPair.setUserID(rs.getInt("userID"));
				travelPair.setTravelID(rs.getInt("travelID"));
				travelPair.setPaired(rs.getBoolean("paired"));
				travelPairs.add(travelPair);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return travelPairs;
	}
	
	public List<TravelPair> getTravelPairByUserID(int userID) { // 取得使用者所有Pair清單，傳回Pair陣列
		List<TravelPair> travelPairs = new ArrayList<TravelPair>();

		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where userID=?");
			preparedStatement.setInt(1, userID);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				TravelPair travelPair = new TravelPair();
				travelPair.setTravelPairID(rs.getInt("travelPairID"));
				travelPair.setUserID(rs.getInt("userID"));
				travelPair.setTravelID(rs.getInt("travelID"));
				travelPair.setPaired(rs.getBoolean("paired"));
				travelPairs.add(travelPair);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return travelPairs;
	}
}
