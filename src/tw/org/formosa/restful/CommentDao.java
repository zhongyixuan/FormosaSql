package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommentDao { // 對評論評分資料表做操作的類別

	private Connection connection;
	private String tableName = "評論評分";

	public CommentDao() {
			connection = DbUtil.getConnection();
	}

	public boolean addUserComment(Comment comment, User user) { // 新增一筆評論，成功傳回true，失敗傳回false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("insert into `"
							+ tableName
							+ "`(userID, attractionID, attractionType, userComment, userScore, commentTime) values (?, ?, ?, ?, ?, ?)");
			// Parameters start with 1
			preparedStatement.setInt(1, user.getUserID());
			preparedStatement.setString(2, comment.getAttractionID());
			preparedStatement.setString(3, comment.getAttractionType());
			preparedStatement.setString(4, comment.getUserComment());
			preparedStatement.setInt(5, comment.getUserScore());
			preparedStatement.setTimestamp(6, comment.getCommentTime());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException");
			return false;
		}
	}

	public List<Comment> getCommentByAttraction(String attractionID,
			String attractionType) { // 用attractionID和attractionType取得該景點每筆評論，傳回評論陣列
		List<Comment> comments = new ArrayList<Comment>();

		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where attractionID=? and attractionType=?");
			preparedStatement.setString(1, attractionID);
			preparedStatement.setString(2, attractionType);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				Comment comment = new Comment();
				comment.setUserID(rs.getInt("userID"));
				comment.setCommentID(rs.getInt("commentID"));
				comment.setAttractionID(rs.getString("attractionID"));
				comment.setAttractionType(rs.getString("attractionType"));
				comment.setUserComment(rs.getString("userComment"));
				comment.setUserScore(rs.getInt("userScore"));
				comment.setCommentTime(rs.getTimestamp("commentTime"));
				comments.add(comment);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return comments;
	}

	public Comment getCommentID(int userID) { // 用userID取得單筆評論編號，傳回一個評論評分物件
		Comment comment = new Comment();
		comment.setCommentID(0);
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where userID=?");
			preparedStatement.setInt(1, userID);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next())
				comment.setCommentID(rs.getInt("commentID"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return comment;
	}
}
