package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PuzzleQuestionDao { // ����ϥ��ȸ�ƪ��ާ@�����O

	private Connection connection;
	private String tableName = "���ϥ���";

	public PuzzleQuestionDao() {
			connection = DbUtil.getConnection();
	}

	public List<PuzzleQuestion> getPuzzleQuestionByCountyAndLevel(String county, int level) { // ��puzzleQuestionID���o���ϥ��Ȥ��e�A�Ǧ^�@�ӫ��ϥ���
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
