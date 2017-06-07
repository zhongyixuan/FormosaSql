package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TravelDao { // ��ڪ���{��ƪ��ާ@�����O

	private Connection connection;
	private String tableName = "�ڪ���{";

	public TravelDao() {
			connection = DbUtil.getConnection();
	}

	public boolean addTravel(Travel travel, User user) { // �s�W�@��Travel�A���\�Ǧ^true�A���ѶǦ^false
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

	public boolean deleteTravel(int travelID) { // �Φ�{�s���R���q��A���\�Ǧ^true�A���ѶǦ^false
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

	public boolean deleteTravelByUser(int userID) { // ��userID�R����User�Ҧ���{
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

	public boolean updateTravel(Travel travel) { // ��s��{���e�A���\�Ǧ^true�A���ѶǦ^false
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

	public Travel getUserTravelById(int travelID) { // �Φ�{�s�����o��{�A�Ǧ^��{����
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

	public List<Travel> getTravelByUserID(int userID) { // ��userID���o�ϥΪ̨C����{�A�Ǧ^��{�}�C
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

	public Travel getTravelID(int userID) { // ��userID���o�浧��{�s���A�Ǧ^�@�Ӧ�{����
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
