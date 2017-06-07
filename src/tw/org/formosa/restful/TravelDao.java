package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TravelDao { // 對我的行程資料表做操作的類別

	private Connection connection;
	private String tableName = "我的行程";

	public TravelDao() {
			connection = DbUtil.getConnection();
	}

	public boolean addTravel(Travel travel, User user) { // 新增一筆Travel，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("insert into `"
							+ tableName
							+ "`(userID, travelName, travelDate, travelDays) values (?, ?, ?, ?)");
			// Parameters start with 1
			preparedStatement.setInt(1, user.getUserID());
			preparedStatement.setString(2, travel.getTravelName());
			preparedStatement.setTimestamp(3, travel.getTravelDate());
			preparedStatement.setInt(4, travel.getTravelDays());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException");
			return false;
		}
	}

	public boolean deleteTravel(int travelID) { // 用行程編號刪除訂單，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("delete from `" + tableName
							+ "` where travelID=?");
			preparedStatement.setInt(1, travelID);
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteTravelByUser(int userID) { // 用userID刪除該User所有行程
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

	public boolean updateTravel(Travel travel) { // 更新行程內容，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("update `"
							+ tableName
							+ "` set travelName=?, travelDate=?, travelDays=?"
							+ "where travelID=?");
			preparedStatement.setString(1, travel.getTravelName());
			preparedStatement.setTimestamp(2, travel.getTravelDate());
			preparedStatement.setInt(3, travel.getTravelDays());
			preparedStatement.setInt(4, travel.getTravelID());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Travel getUserTravelById(int travelID) { // 用行程編號取得行程，傳回行程物件
		Travel travel = new Travel();
		travel.setTravelID(0);

		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where travelID=?");
			preparedStatement.setInt(1, travelID);
			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next()) {
				travel.setTravelID(rs.getInt("travelID"));
				travel.setUserID(rs.getInt("userID"));
				travel.setTravelName(rs.getString("travelName"));
				travel.setTravelDate(rs.getTimestamp("travelDate"));
				travel.setTravelDays(rs.getInt("travelDays"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return travel;
	}

	public List<Travel> getTravelByUserID(int userID) { // 用userID取得使用者每筆行程，傳回行程陣列
		List<Travel> travels = new ArrayList<Travel>();

		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where userID=?");
			preparedStatement.setInt(1, userID);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				Travel travel = new Travel();
				travel.setTravelID(rs.getInt("travelID"));
				travel.setUserID(rs.getInt("userID"));
				travel.setTravelName(rs.getString("travelName"));
				travel.setTravelDate(rs.getTimestamp("travelDate"));
				travel.setTravelDays(rs.getInt("travelDays"));
				travels.add(travel);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return travels;
	}

	public Travel getTravelID(int userID) { // 用userID取得單筆行程編號，傳回一個行程物件
		Travel travel = new Travel();
		travel.setTravelID(0);
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where userID=?");
			preparedStatement.setInt(1, userID);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next())
				travel.setTravelID(rs.getInt("travelID"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return travel;
	}
}
