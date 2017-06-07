package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TravelAttractionDao { // 對我的行程景點資料表做操作的類別

	private Connection connection;
	private String tableName = "我的行程景點";

	public TravelAttractionDao() {
			connection = DbUtil.getConnection();
	}

	public boolean addTravelAttraction(TravelAttraction travelAttraction) { // 新增一筆TravelAttraction，成功傳回true，失敗傳回false
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

	public boolean deleteTravelAttraction(int travelID, String attractionName) { // 用行程編號與景點編號刪除訂單，成功傳回true，失敗傳回false
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

	public boolean deleteTravelAttractionById(int travelID) { // 用行程編號刪除訂單，成功傳回true，失敗傳回false
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
			Timestamp dayDate) { // 用userID取得使用者每筆行程，傳回行程陣列
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
	
	public List<TravelAttraction> getTravelAttractionByID(int travelID) { // 用userID取得使用者每筆行程，傳回行程陣列
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
