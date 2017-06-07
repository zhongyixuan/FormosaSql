package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PairDao { // ��Pair��ƪ��ާ@�����O

	private Connection connection;
	private String tableName = "Pair";

	public PairDao() {
			connection = DbUtil.getConnection();
	}

	public boolean addPair(Pair pair, User user) { // �s�W�@��Pair�A���\�Ǧ^true�A���ѶǦ^false
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

	public boolean deletePair(int pairID) { // ��Pair�s���R��Pair�A���\�Ǧ^true�A���ѶǦ^false
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

	public boolean deletePairByUser(int userID) { // ��userID�R����User�Ҧ�Pair�A���\�Ǧ^true�A���ѶǦ^false
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

	public boolean updatePair(Pair pair) { // ��sPair���e�A���\�Ǧ^true�A���ѶǦ^false
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

	public Pair getPairById(int pairID) { // ��Pair�s�����oPair�A�Ǧ^Pair����
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

	public Pair getPairID(int userID) { // ��userID���o�浧Pair�s���A�Ǧ^�@��Pair
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
	
	public List<Pair> getPairList() { // ���o�ϥΪ̩Ҧ�Pair�M��A�Ǧ^Pair�}�C
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
	
	public boolean alreadyPair(Pair pair) { // ��s�w�t��pair�A���\�Ǧ^true�A���ѶǦ^false
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
	
	public List<Pair> getPairByUserID(int userID) { // ��Pair�s�����oPair�A�Ǧ^Pair����
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
