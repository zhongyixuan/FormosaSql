package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActivityOrderDao { // 對活動代訂資料表做操作的類別

	private Connection connection;
	private String tableName = "活動代訂";

	public ActivityOrderDao() {
			connection = DbUtil.getConnection();
	}

	public boolean addOrder(ActivityOrder order, User user) { // 新增一筆訂單，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("insert into `"
							+ tableName
							+ "`(userID, activityID, activityOrderName, activityUserPhone, activityUserAddress, activityName, activityOrderDate, activityOrderCount, activityUserNote) values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			// Parameters start with 1
			preparedStatement.setInt(1, user.getUserID());
			preparedStatement.setString(2, order.getActivityID());
			preparedStatement.setString(3, order.getActivityOrderName());
			preparedStatement.setString(4, order.getActivityUserPhone());
			preparedStatement.setString(5, order.getActivityUserAddress());
			preparedStatement.setString(6, order.getActivityName());
			preparedStatement.setTimestamp(7, order.getActivityOrderDate());
			preparedStatement.setInt(8, order.getActivityOrderCount());
			preparedStatement.setString(9, order.getActivityUserNote());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException");
			return false;
		}
	}

	public boolean deleteOrder(int activityOrderID) { // 用訂單編號刪除訂單，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("delete from `" + tableName
							+ "` where activityOrderID=?");
			preparedStatement.setInt(1, activityOrderID);
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteOrderByUser(int userID) { // 用userID刪除所有該User訂單，成功傳回true，失敗傳回false
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

	public boolean updateOrder(ActivityOrder order) { // 更新訂單內容，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("update `"
							+ tableName
							+ "` set activityOrderName=?, activityUserPhone=?, activityUserAddress=?, activityOrderDate=?, activityOrderCount=?, activityUserNote=?"
							+ "where activityOrderID=?");
			preparedStatement.setString(1, order.getActivityOrderName());
			preparedStatement.setString(2, order.getActivityUserPhone());
			preparedStatement.setString(3, order.getActivityUserAddress());
			preparedStatement.setTimestamp(4, order.getActivityOrderDate());
			preparedStatement.setInt(5, order.getActivityOrderCount());
			preparedStatement.setString(6, order.getActivityUserNote());
			preparedStatement.setInt(7, order.getActivityOrderID());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public ActivityOrder getUserOrderById(int activityOrderID) { // 用訂單編號取得訂單，傳回訂單物件
		ActivityOrder order = new ActivityOrder();
		order.setActivityOrderID(0);
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where activityOrderID=?");
			preparedStatement.setInt(1, activityOrderID);
			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next()) {
				order.setUserID(rs.getInt("userID"));
				order.setActivityOrderID(rs.getInt("activityOrderID"));
				order.setActivityID(rs.getString("activityID"));
				order.setActivityOrderName(rs.getString("activityOrderName"));
				order.setActivityUserPhone(rs.getString("activityUserPhone"));
				order.setActivityUserAddress(rs.getString("activityUserAddress"));
				order.setActivityName(rs.getString("activityName"));
				order.setActivityOrderDate(rs.getTimestamp("activityOrderDate"));
				order.setActivityOrderCount(rs.getInt("activityOrderCount"));
				order.setActivityUserNote(rs.getString("activityUserNote"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return order;
	}

	public List<ActivityOrder> getOrderByUserID(int userID) { // 用userID取得使用者每筆訂單，傳回訂單陣列
		List<ActivityOrder> orders = new ArrayList<ActivityOrder>();

		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where userID=?");
			preparedStatement.setInt(1, userID);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				ActivityOrder order = new ActivityOrder();
				order.setUserID(rs.getInt("userID"));
				order.setActivityOrderID(rs.getInt("activityOrderID"));
				order.setActivityID(rs.getString("activityID"));
				order.setActivityOrderName(rs.getString("activityOrderName"));
				order.setActivityUserPhone(rs.getString("activityUserPhone"));
				order.setActivityUserAddress(rs.getString("activityUserAddress"));
				order.setActivityName(rs.getString("activityName"));
				order.setActivityOrderDate(rs.getTimestamp("activityOrderDate"));
				order.setActivityOrderCount(rs.getInt("activityOrderCount"));
				order.setActivityUserNote(rs.getString("activityUserNote"));
				orders.add(order);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orders;
	}

	public ActivityOrder getOrderID(int userID) { // 用userID取得單筆訂單編號，傳回一個訂單物件
		ActivityOrder order = new ActivityOrder();
		order.setActivityOrderID(0);
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where userID=?");
			preparedStatement.setInt(1, userID);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next())
				order.setActivityOrderID(rs.getInt("activityOrderID"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return order;
	}
}
