package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PuzzleQuestionDao { // 對拼圖任務資料表做操作的類別

	private Connection connection;
	private String tableName = "拼圖任務";

	public PuzzleQuestionDao() {
			connection = DbUtil.getConnection();
	}

	public List<PuzzleQuestion> getPuzzleQuestionByCountyAndLevel(String county, int level) { // 用puzzleQuestionID取得拼圖任務內容，傳回一個拼圖任務
		List<PuzzleQuestion> puzzleQuestions = new ArrayList<PuzzleQuestion>();

		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where county=? and level=?");
			preparedStatement.setString(1, county);
			preparedStatement.setInt(2, level);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()){
				PuzzleQuestion puzzleQuestion = new PuzzleQuestion();
				puzzleQuestion.setPuzzleQuestionID(rs.getInt("puzzleQuestionID"));
				puzzleQuestion.setPuzzleQuestionName(rs.getString("puzzleQuestionName"));
				puzzleQuestion.setCounty(rs.getString("county"));
				puzzleQuestion.setPuzzleGetAttractionName(rs.getString("puzzleGetAttractionName"));
				puzzleQuestion.setAttractionLongitude(rs.getBigDecimal("attractionLongitude"));
				puzzleQuestion.setAttractionLatitude(rs.getBigDecimal("attractionLatitude"));
				puzzleQuestion.setLevel(rs.getInt("level"));
				puzzleQuestion.setImage(rs.getString("image"));
				puzzleQuestions.add(puzzleQuestion);
			}
			

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return puzzleQuestions;
	}
}
