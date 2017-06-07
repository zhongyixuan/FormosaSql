package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PairTracingDao { // 對Pair配對地圖資料表做操作的類別

	private Connection connection;
	private String tableName = "Pair配對地圖";

	public PairTracingDao() {
			connection = DbUtil.getConnection();
	}

	public boolean addPairTracing(PairTracing pairTracing, User user,
			User tracingUser) { // 新增一筆PairTracing，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("insert into `"
							+ tableName
							+ "`(pairID, userID, pairLongitude, pairLatitude, tracingUserID, tracingLongitude, tracingLatitude) values (?, ?, ?, ?, ?, ?, ?)");
			// Parameters start with 1
			preparedStatement.setInt(1, pairTracing.getPairID());
			preparedStatement.setInt(2, user.getUserID());
			preparedStatement.setBigDecimal(3, pairTracing.getPairLongitude());
			preparedStatement.setBigDecimal(4, pairTracing.getPairLatitude());
			preparedStatement.setInt(5, tracingUser.getUserID());
			preparedStatement.setBigDecimal(6,	pairTracing.getTracingLongitude());
			preparedStatement.setBigDecimal(7, pairTracing.getTracingLatitude());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException");
			return false;
		}
	}

	public boolean updatePairTracingByUserID(PairTracing pairTracing) { // 更新PairTracing內容，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("UPDATE `"
							+ tableName
							+ "` SET `pairLongitude` =?, `pairLatitude` =?"
							+ " WHERE `pairID` =? AND `userID` =?");
			preparedStatement.setBigDecimal(1, pairTracing.getPairLongitude());
			preparedStatement.setBigDecimal(2, pairTracing.getPairLatitude());
			preparedStatement.setInt(3, pairTracing.getPairID());
			preparedStatement.setInt(4, pairTracing.getUserID());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean updatePairTracingByTracingUserID(PairTracing pairTracing) { // 更新PairTracing內容，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("UPDATE `"
							+ tableName
							+ "` SET `tracingLongitude` =?, `tracingLatitude` =?"
							+ " WHERE `pairID` =? AND `tracingUserID` =?");
			preparedStatement.setBigDecimal(1, pairTracing.getTracingLongitude());
			preparedStatement.setBigDecimal(2, pairTracing.getTracingLatitude());
			preparedStatement.setInt(3, pairTracing.getPairID());
			preparedStatement.setInt(4, pairTracing.getTracingUserID());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public PairTracing getPairTracingById(int pairID) { // 用Pair編號取得PairTracing，傳回PairTracing物件
		PairTracing pairTracing = new PairTracing();
		pairTracing.setPairID(0);
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where pairID=?");
			preparedStatement.setInt(1, pairID);
			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next()) {
				pairTracing.setPairID(rs.getInt("pairID"));
				pairTracing.setUserID(rs.getInt("userID"));
				pairTracing.setPairLongitude(rs.getBigDecimal("pairLongitude"));
				pairTracing.setPairLatitude(rs.getBigDecimal("pairLatitude"));
				pairTracing.setTracingUserID(rs.getInt("tracingUserID"));
				pairTracing.setTracingLongitude(rs
						.getBigDecimal("tracingLongitude"));
				pairTracing.setTracingLatitude(rs
						.getBigDecimal("tracingLatitude"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pairTracing;
	}

	public List<PairTracing> getPairID(int userID) { // 用userID取得單筆Pair編號，傳回一個PairTracing
		List<PairTracing> pairTracings = new ArrayList<PairTracing>();
		
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where userID=? or tracingUserID=?");
			preparedStatement.setInt(1, userID);
			preparedStatement.setInt(2, userID);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()){
				PairTracing pairTracing = new PairTracing();
				pairTracing.setPairID(rs.getInt("pairID"));
				pairTracings.add(pairTracing);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pairTracings;
	}
}
