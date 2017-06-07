package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PuzzleDao { // ����ϸ�ƪ��ާ@�����O

	private Connection connection;
	private String tableName = "����";

	public PuzzleDao() {
			connection = DbUtil.getConnection();
	}

	public boolean addPuzzle(Puzzle puzzle, User user) { // ����@�����ϡA���\�Ǧ^true�A���ѶǦ^false
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
	
	public boolean deletePuzzleByUser(int userID) { // ��userID�R����User�Ҧ�����
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

	public List<Puzzle> getPuzzleByUser(int userID) { // ��userID�BpuzzleQuestionID���o�ϥΪ̸ӫ��ϥ��ȨC�ӫ��ϡA�Ǧ^���ϰ}�C
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
	
	public List<Puzzle> getPuzzleByUserAndCounty(int userID, String county) { // ��userID�BpuzzleQuestionID���o�ϥΪ̸ӫ��ϥ��ȨC�ӫ��ϡA�Ǧ^���ϰ}�C
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

//	public Puzzle getPuzzleID(int userID) { // ��userID���o���Ͻs���A�Ǧ^�@�ӫ���
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
