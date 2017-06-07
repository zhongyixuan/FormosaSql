package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TravelAttractionDao { // ��ڪ���{���I��ƪ��ާ@�����O

	private Connection connection;
	private String tableName = "�ڪ���{���I";

	public TravelAttractionDao() {
			connection = DbUtil.getConnection();
	}

	public boolean addTravelAttraction(TravelAttraction travelAttraction) { // �s�W�@��TravelAttraction�A���\�Ǧ^true�A���ѶǦ^false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("insert into `"
							+ tableName
							+ "`(travelID, attractionName, dayDate) values (?, ?, ?)");
			// Parameters start with 1
			preparedStatement.setInt(1, travelAttraction.getTravelID());
			preparedStatement.setString(2, travelAttraction.getAttractionName());
			preparedStatement.setTimestamp(3, travelAttraction.getDayDate());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException");
			return false;
		}
	}

	public boolean deleteTravelAttraction(int travelID, String attractionName) { // �Φ�{�s���P���I�s���R���q��A���\�Ǧ^true�A���ѶǦ^false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("delete from `" + tableName
							+ "` where travelID=? and attractionName=?");
			preparedStatement.setInt(1, travelID);
			preparedStatement.setString(2, attractionName);
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteTravelAttractionById(int travelID) { // �Φ�{�s���R���q��A���\�Ǧ^true�A���ѶǦ^false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("delete from `" + tableName
							+ "` where travelID=? and attractionID=?");
			preparedStatement.setInt(1, travelID);
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<TravelAttraction> getTravelAttractionByIDAndDay(int travelID,
			Timestamp dayDate) { // ��userID���o�ϥΪ̨C����{�A�Ǧ^��{�}�C
		List<TravelAttraction> travelAttractions = new ArrayList<TravelAttraction>();

		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where travelID=? and dayDate=?");
			preparedStatement.setInt(1, travelID);
			preparedStatement.setTimestamp(2, dayDate);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				TravelAttraction travelAttraction = new TravelAttraction();
				travelAttraction.setAttractionName(rs.getString("attractionName"));
				travelAttractions.add(travelAttraction);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return travelAttractions;
	}
	
	public List<TravelAttraction> getTravelAttractionByID(int travelID) { // ��userID���o�ϥΪ̨C����{�A�Ǧ^��{�}�C
		List<TravelAttraction> travelAttractions = new ArrayList<TravelAttraction>();

		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where travelID=?");
			preparedStatement.setInt(1, travelID);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				TravelAttraction travelAttraction = new TravelAttraction();
				travelAttraction.setTravelID(rs.getInt("travelID"));
				travelAttraction.setAttractionName(rs.getString("attractionName"));
				travelAttraction.setDayDate(rs.getTimestamp("dayDate"));
				travelAttractions.add(travelAttraction);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return travelAttractions;
	}
}
