package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tw.org.formosa.restful.DbUtil;
import tw.org.formosa.restful.HotelOrder;

public class HotelOrderDao { // 對住宿代訂資料表做操作的類別

	private Connection connection;
	private String tableName = "住宿代訂";

	public HotelOrderDao() {
			connection = DbUtil.getConnection();
	}

	public boolean addOrder(HotelOrder order, User user) { // 新增一筆訂單，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("insert into `"
							+ tableName
							+ "`(userID, hotelOrderName, hotelUserPhone, hotelUserAddress, hotelName, hotelAddress, hotelOrderDate, hotelOrderCount, hotelUserNote) values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			// Parameters start with 1
			preparedStatement.setInt(1, user.getUserID());
			preparedStatement.setString(2, order.getHotelOrderName());
			preparedStatement.setString(3, order.getHotelUserPhone());
			preparedStatement.setString(4, order.getHotelUserAddress());
			preparedStatement.setString(5, order.getHotelName());
			preparedStatement.setString(6, order.getHotelAddress());
			preparedStatement.setTimestamp(7, order.getHotelOrderDate());
			preparedStatement.setInt(8, order.getHotelOrderCount());
			preparedStatement.setString(9, order.getHotelUserNote());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException");
			return false;
		}
	}

	public boolean deleteOrder(int hotelOrderID) { // 用訂單編號刪除訂單，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("delete from `" + tableName
							+ "` where hotelOrderID=?");
			preparedStatement.setInt(1, hotelOrderID);
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteOrderByUser(int userID) { // 用userID刪除該User所有訂單
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

	public boolean updateOrder(HotelOrder order) { // 更新訂單內容，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("update `"
							+ tableName
							+ "` set hotelOrderName=?, hotelUserPhone=?, hotelUserAddress=?, hotelOrderDate=?, hotelOrderCount=?, hotelUserNote=?"
							+ "where hotelOrderID=?");
			preparedStatement.setString(1, order.getHotelOrderName());
			preparedStatement.setString(2, order.getHotelUserPhone());
			preparedStatement.setString(3, order.getHotelUserAddress());
			preparedStatement.setTimestamp(4, order.getHotelOrderDate());
			preparedStatement.setInt(5, order.getHotelOrderCount());
			preparedStatement.setString(6, order.getHotelUserNote());
			preparedStatement.setInt(7, order.getHotelOrderID());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public HotelOrder getUserOrderByOrderId(int hotelOrderID) { // 用訂單編號取得訂單，傳回訂單物件
		HotelOrder order = new HotelOrder();
		order.setHotelOrderID(0);
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where hotelOrderID=?");
			preparedStatement.setInt(1, hotelOrderID);
			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next()) {
				order.setUserID(rs.getInt("userID"));
				order.setHotelOrderID(rs.getInt("hotelOrderID"));
				order.setHotelOrderName(rs.getString("hotelOrderName"));
				order.setgetHotelUserPhone(rs.getString("hotelUserPhone"));
				order.setHotelUserAddress(rs.getString("hotelUserAddress"));
				order.setHotelName(rs.getString("hotelName"));
				order.setHotelAddress(rs.getString("hotelAddress"));
				order.setHotelOrderDate(rs.getTimestamp("hotelOrderDate"));
				order.setHotelOrderCount(rs.getInt("hotelOrderCount"));
				order.setHotelUserNote(rs.getString("hotelUserNote"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return order;
	}

	public List<HotelOrder> getOrderByUserID(int userID) { // 用userID取得使用者每筆訂單，傳回訂單陣列
		List<HotelOrder> orders = new ArrayList<HotelOrder>();

		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where userID=?");
			preparedStatement.setInt(1, userID);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				HotelOrder order = new HotelOrder();
				order.setUserID(rs.getInt("userID"));
				order.setHotelOrderID(rs.getInt("hotelOrderID"));
				order.setHotelOrderName(rs.getString("hotelOrderName"));
				order.setgetHotelUserPhone(rs.getString("hotelUserPhone"));
				order.setHotelUserAddress(rs.getString("hotelUserAddress"));
				order.setHotelName(rs.getString("hotelName"));
				order.setHotelAddress(rs.getString("hotelAddress"));
				order.setHotelOrderDate(rs.getTimestamp("hotelOrderDate"));
				order.setHotelOrderCount(rs.getInt("hotelOrderCount"));
				order.setHotelUserNote(rs.getString("hotelUserNote"));
				orders.add(order);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orders;
	}

	public HotelOrder getOrderID(int userID) { // 用userID取得單筆訂單編號，傳回一個訂單物件
		HotelOrder order = new HotelOrder();
		order.setHotelOrderID(0);
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where userID=?");
			preparedStatement.setInt(1, userID);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next())
				order.setHotelOrderID(rs.getInt("hotelOrderID"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return order;
	}
}
