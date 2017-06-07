package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import tw.org.formosa.restful.DbUtil;
import tw.org.formosa.restful.User;

public class UserDao { // ��|����ƪ��ާ@�����O

	private Connection connection;
	private String tableName = "�|��";

	public UserDao() {
			connection = DbUtil.getConnection();
	}

	public boolean addUser(User user) { // �s�W�|���A���\�Ǧ^true�A���ѶǦ^false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("insert into "
							+ tableName
							+ "(userName, userAccount, userPassword, userEMail) values (?, ?, ?, ? )");
			// Parameters start with 1
			preparedStatement.setString(1, user.getUserName());
			preparedStatement.setString(2, user.getUserAccount());
			preparedStatement.setString(3, user.getUserPassword());
			preparedStatement.setString(4, user.getUserEMail());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException");
			return false;
		}
	}

	public boolean deleteUser(int userID) { // �Q��userID�R���|���A���\�Ǧ^true�A���ѶǦ^false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("delete from " + tableName
							+ " where userID=?");
			// Parameters start with 1
			preparedStatement.setInt(1, userID);
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean updateUser(User user) { // ��s�|����ơA���\�Ǧ^true�A���ѶǦ^false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("update users set userName=?, userPassword=?, userEMail=?"
							+ "where userID=?");
			// Parameters start with 1
			preparedStatement.setString(1, user.getUserName());
			preparedStatement.setString(2, user.getUserPassword());
			preparedStatement.setString(3, user.getUserEMail());
			preparedStatement.setInt(4, user.getUserID());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<User> getAllUsers() { // �C�X�Ҧ��|���A�Ǧ^User�}�C
		List<User> users = new ArrayList<User>();
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("select * from " + tableName);
			while (rs.next()) {
				User user = new User();
				user.setUserID(rs.getInt("userID"));
				user.setUserName(rs.getString("userName"));
				user.setUserAccount(rs.getString("userAccount"));
				user.setUserPassword(rs.getString("userPassword"));
				user.setUserEMail(rs.getString("userEMail"));
				users.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}

	public User getUserById(int userID) { // ��userID���oUser�A�Ǧ^�@��User����
		User user = new User();
		user.setUserID(0);
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from " + tableName
							+ " where userID=?");
			preparedStatement.setInt(1, userID);
			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next()) {
				user.setUserID(rs.getInt("userID"));
				user.setUserName(rs.getString("userName"));
				user.setUserAccount(rs.getString("userAccount"));
				user.setUserPassword(rs.getString("userPassword"));
				user.setUserEMail(rs.getString("userEMail"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	public User getUserByUserAccount(String userAccount) { // �αb�����oUser�A�Ǧ^�@��User����
		User user = new User();
		user.setUserID(0);
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from " + tableName
							+ " where userAccount=?");
			preparedStatement.setString(1, userAccount);
			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next()) {
				user.setUserID(rs.getInt("userid"));
				user.setUserName(rs.getString("userName"));
				user.setUserAccount(rs.getString("userAccount"));
				user.setUserPassword(rs.getString("userPassword"));
				user.setUserEMail(rs.getString("userEMail"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	public boolean checkPassword(String userAccount, String userPassword) { // �ˬd�K�X�A�۵��Ǧ^true�A���۵��Ǧ^false
		String tmpPass = null;
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from " + tableName
							+ " where userAccount=?");
			preparedStatement.setString(1, userAccount);
			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next())
				tmpPass = rs.getString("userPassword");

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		if (tmpPass.equals(userPassword))
			return true;
		else
			return false;
	}

	public boolean checkAccount(String userAccount) { // �ˬd�b���A���b���Ǧ^false�A�S���b���Ǧ^true
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from " + tableName
							+ " where userAccount=?");
			preparedStatement.setString(1, userAccount);
			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next())
				return false; // ���b���Ǧ^false
			else
				return true; // �S���b���Ǧ^true

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
