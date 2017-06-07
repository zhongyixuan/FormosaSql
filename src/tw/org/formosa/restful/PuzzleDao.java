package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PuzzleDao { // 對拼圖資料表做操作的類別

	private Connection connection;
	private String tableName = "拼圖";

	public PuzzleDao() {
			connection = DbUtil.getConnection();
	}

	public boolean addPuzzle(Puzzle puzzle, User user) { // 獲取一塊拼圖，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("insert into `"
							+ tableName
							+ "`(userID, puzzleQuestionID, puzzleGetAttractionName, county, level) values (?, ?, ?, ?, ?)");
			// Parameters start with 1
			preparedStatement.setInt(1, user.getUserID());
			preparedStatement.setInt(2, puzzle.getPuzzleQuestionID());
			preparedStatement.setString(3, puzzle.getPuzzleGetAttractionName());
			preparedStatement.setString(4, puzzle.getCounty());
			preparedStatement.setInt(5, puzzle.getLevel());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException");
			return false;
		}
	}
	
	public boolean deletePuzzleByUser(int userID) { // 用userID刪除該User所有拼圖
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

	public List<Puzzle> getPuzzleByUser(int userID) { // 用userID、puzzleQuestionID取得使用者該拼圖任務每個拼圖，傳回拼圖陣列
		List<Puzzle> puzzles = new ArrayList<Puzzle>();

		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where userID=?");
			preparedStatement.setInt(1, userID);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				Puzzle puzzle = new Puzzle();
				puzzle.setUserID(rs.getInt("userID"));
				puzzle.setPuzzleQuestionID(rs.getInt("puzzleQuestionID"));
				puzzle.setPuzzleGetAttractionName(rs.getString("puzzleGetAttractionName"));
				puzzle.setCounty(rs.getString("county"));
				puzzle.setLevel(rs.getInt("level"));
				puzzles.add(puzzle);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return puzzles;
	}
	
	public List<Puzzle> getPuzzleByUserAndCounty(int userID, String county) { // 用userID、puzzleQuestionID取得使用者該拼圖任務每個拼圖，傳回拼圖陣列
		List<Puzzle> puzzles = new ArrayList<Puzzle>();

		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where userID=? and county=?");
			preparedStatement.setInt(1, userID);
			preparedStatement.setString(2, county);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				Puzzle puzzle = new Puzzle();
				puzzle.setUserID(rs.getInt("userID"));
				puzzle.setPuzzleQuestionID(rs.getInt("puzzleQuestionID"));
				puzzle.setPuzzleGetAttractionName(rs.getString("puzzleGetAttractionName"));
				puzzle.setCounty(rs.getString("county"));
				puzzle.setLevel(rs.getInt("level"));
				puzzles.add(puzzle);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return puzzles;
	}

//	public Puzzle getPuzzleID(int userID) { // 用userID取得拼圖編號，傳回一個拼圖
//		Puzzle puzzle = new Puzzle();
//		puzzle.setPuzzleID(0);
//
//		try {
//			PreparedStatement preparedStatement = connection
//					.prepareStatement("select * from `" + tableName
//							+ "` where userID=?");
//			preparedStatement.setInt(1, userID);
//			ResultSet rs = preparedStatement.executeQuery();
//
//			while (rs.next())
//				puzzle.setPuzzleID(rs.getInt("puzzleID"));
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return puzzle;
//	}
}
