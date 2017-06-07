package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PairDao { // 對Pair資料表做操作的類別

	private Connection connection;
	private String tableName = "Pair";

	public PairDao() {
			connection = DbUtil.getConnection();
	}

	public boolean addPair(Pair pair, User user) { // 新增一筆Pair，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("insert into `"
							+ tableName
							+ "`(userID, shopName, productName, productPrice, preferentialType,  pairAddress, userFeature, pairTime, waitTime, pairLongitude, pairLatitude) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			// Parameters start with 1
			preparedStatement.setInt(1, user.getUserID());
			preparedStatement.setString(2, pair.getShopName());
			preparedStatement.setString(3, pair.getProductName());
			preparedStatement.setInt(4, pair.getProductPrice());
			preparedStatement.setString(5, pair.getPreferentialType());
			preparedStatement.setString(6, pair.getPairAddress());
			preparedStatement.setString(7, pair.getUserFeature());
			preparedStatement.setTimestamp(8, pair.getPairTime());
			preparedStatement.setTime(9, pair.getWaitTime());
			preparedStatement.setBigDecimal(10, pair.getPairLongitude());
			preparedStatement.setBigDecimal(11, pair.getPairLatitude());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException");
			return false;
		}
	}

	public boolean deletePair(int pairID) { // 用Pair編號刪除Pair，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("delete from `" + tableName
							+ "` where pairID=?");
			preparedStatement.setInt(1, pairID);
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deletePairByUser(int userID) { // 用userID刪除該User所有Pair，成功傳回true，失敗傳回false
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

	public boolean updatePair(Pair pair) { // 更新Pair內容，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("UPDATE `"
							+ tableName
							+ "` SET `productName` =?, `productPrice` =?,`preferentialType` =?, `pairAddress` =?, `userFeature` =?"
							+ " WHERE `pairID` =?");
			preparedStatement.setString(1, pair.getProductName());
			preparedStatement.setInt(2, pair.getProductPrice());
			preparedStatement.setString(3, pair.getPreferentialType());
			preparedStatement.setString(4, pair.getPairAddress());
			preparedStatement.setString(5, pair.getUserFeature());
			preparedStatement.setInt(7, pair.getPairID());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Pair getPairById(int pairID) { // 用Pair編號取得Pair，傳回Pair物件
		Pair pair = new Pair();
		pair.setPairID(0);
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where pairID=?");
			preparedStatement.setInt(1, pairID);
			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next()) {
				pair.setPairID(rs.getInt("pairID"));
				pair.setUserID(rs.getInt("userID"));
				pair.setShopName(rs.getString("shopName"));
				pair.setProductName(rs.getString("productName"));
				pair.setProductPrice(rs.getInt("productPrice"));
				pair.setPreferentialType(rs.getString("preferentialType"));
				pair.setPairAddress(rs.getString("pairAddress"));
				pair.setUserFeature(rs.getString("userFeature"));
				pair.setPairTime(rs.getTimestamp("pairTime"));
				pair.setWaitTime(rs.getTime("waitTime"));
				pair.setPairLongitude(rs.getBigDecimal("pairLongitude"));
				pair.setPairLatitude(rs.getBigDecimal("pairLatitude"));
				pair.setPaired(rs.getBoolean("paired"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pair;
	}

	public Pair getPairID(int userID) { // 用userID取得單筆Pair編號，傳回一個Pair
		Pair pair = new Pair();
		pair.setPairID(0);
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where userID=?");
			preparedStatement.setInt(1, userID);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next())
				pair.setPairID(rs.getInt("pairID"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pair;
	}
	
	public List<Pair> getPairList() { // 取得使用者所有Pair清單，傳回Pair陣列
		List<Pair> pairs = new ArrayList<Pair>();

		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				Pair pair = new Pair();
				pair.setPairID(rs.getInt("pairID"));
				pair.setUserID(rs.getInt("userID"));
				pair.setShopName(rs.getString("shopName"));
				pair.setProductName(rs.getString("productName"));
				pair.setProductPrice(rs.getInt("productPrice"));
				pair.setPreferentialType(rs.getString("preferentialType"));
				pair.setPairAddress(rs.getString("pairAddress"));
				pair.setUserFeature(rs.getString("userFeature"));
				pair.setPairTime(rs.getTimestamp("pairTime"));
				pair.setWaitTime(rs.getTime("waitTime"));
				pair.setPairLongitude(rs.getBigDecimal("pairLongitude"));
				pair.setPairLatitude(rs.getBigDecimal("pairLatitude"));
				pair.setPaired(rs.getBoolean("paired"));
				pairs.add(pair);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pairs;
	}
	
	public boolean alreadyPair(Pair pair) { // 更新已配對pair，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("UPDATE `"
							+ tableName
							+ "` SET `paired` =?"
							+ " WHERE `pairID` =?");
			preparedStatement.setBoolean(1, pair.getPaired());
			preparedStatement.setInt(2, pair.getPairID());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public List<Pair> getPairByUserID(int userID) { // 用Pair編號取得Pair，傳回Pair物件
		List<Pair> pairs = new ArrayList<Pair>();

		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where userID=?");
			preparedStatement.setInt(1, userID);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				Pair pair = new Pair();
				pair.setPairID(rs.getInt("pairID"));
				pair.setUserID(rs.getInt("userID"));
				pair.setShopName(rs.getString("shopName"));
				pair.setProductName(rs.getString("productName"));
				pair.setProductPrice(rs.getInt("productPrice"));
				pair.setPreferentialType(rs.getString("preferentialType"));
				pair.setPairAddress(rs.getString("pairAddress"));
				pair.setUserFeature(rs.getString("userFeature"));
				pair.setPairTime(rs.getTimestamp("pairTime"));
				pair.setWaitTime(rs.getTime("waitTime"));
				pair.setPairLongitude(rs.getBigDecimal("pairLongitude"));
				pair.setPairLatitude(rs.getBigDecimal("pairLatitude"));
				pair.setPaired(rs.getBoolean("paired"));
				pairs.add(pair);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pairs;
	}
}
