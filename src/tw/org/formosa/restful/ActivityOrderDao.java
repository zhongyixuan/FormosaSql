package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActivityOrderDao { // �ﬡ�ʥN�q��ƪ��ާ@�����O

	private Connection connection;
	private String tableName = "���ʥN�q";

	public ActivityOrderDao() {
			connection = DbUtil.getConnection();
	}

	public boolean addOrder(ActivityOrder order, User user) { // �s�W�@���q��A���\�Ǧ^true�A���ѶǦ^false
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

	public boolean deleteOrder(int activityOrderID) { // �έq��s���R���q��A���\�Ǧ^true�A���ѶǦ^false
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

	public boolean deleteOrderByUser(int userID) { // ��userID�R���Ҧ���User�q��A���\�Ǧ^true�A���ѶǦ^false
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

	public boolean updateOrder(ActivityOrder order) { // ��s�q�椺�e�A���\�Ǧ^true�A���ѶǦ^false
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

	public ActivityOrder getUserOrderById(int activityOrderID) { // �έq��s�����o�q��A�Ǧ^�q�檫��
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

	public List<ActivityOrder> getOrderByUserID(int userID) { // ��userID���o�ϥΪ̨C���q��A�Ǧ^�q��}�C
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

	public ActivityOrder getOrderID(int userID) { // ��userID���o�浧�q��s���A�Ǧ^�@�ӭq�檫��
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
