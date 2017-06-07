package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import tw.org.formosa.restful.Collection;
import tw.org.formosa.restful.DbUtil;

public class CollectionDao { //��f�U�W���ƪ��ާ@�����O
	private Connection connection;
	private String tableName = "�f�U�W��";

	public CollectionDao() {
			connection = DbUtil.getConnection();
	}

	public boolean addUserCollection(Collection collection, User user) { //�s�W�@�����áA���\�Ǧ^true�A���ѶǦ^false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("insert into " + tableName
							+ "(userID, attractionID, attractionName, county) values (?, ?, ?, ?)");
			// Parameters start with 1
			preparedStatement.setInt(1, user.getUserID());
			preparedStatement.setString(2, collection.getAttractionID());
			preparedStatement.setString(3, collection.getAttractionName());
			preparedStatement.setString(4, collection.getCounty());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException");
			return false;
		}
	}

	public boolean deleteUserCollection(int collectionID) { //�Φ��ýs���R���@�����áA���\�Ǧ^true�A���ѶǦ^false
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("delete from " + tableName
							+ " where collectionID=?");
			// Parameters start with 1
			preparedStatement.setInt(1, collectionID);
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean deleteCollectionByUser(int userID) { //��userID�R����User�Ҧ�����
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

	public Collection getUserCollectionById(int collectionID) { //�Φ��ýs�����o�@�����áA�Ǧ^Collection����
		Collection collection = new Collection();
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from " + tableName
							+ " where collectionID=?");
			preparedStatement.setInt(1, collectionID);
			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next()) {
				collection.setUserID(rs.getInt("userID"));
				collection.setCollectionID(rs.getInt("collectionID"));
				collection.setAttractionID(rs.getString("attractionID"));
				collection.setAttractionName(rs.getString("attractionName"));
				collection.setCounty(rs.getString("county"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return collection;
	}
	
	public List<Collection> getUserCollectionByUser(int userID) { //���o�ϥΪ̩Ҧ����áA�Ǧ^���ð}�C
		List<Collection> collections = new ArrayList<Collection>();
		
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from " + tableName
							+ " where userID=?");
			preparedStatement.setInt(1, userID);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				Collection collection = new Collection();
				collection.setUserID(rs.getInt("userID"));
				collection.setCollectionID(rs.getInt("collectionID"));
				collection.setAttractionID(rs.getString("attractionID"));
				collection.setAttractionName(rs.getString("attractionName"));
				collection.setCounty(rs.getString("county"));
				collections.add(collection);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return collections;
	}

	public Collection getCollectionID(int userID) { //��userID���o�浧���ýs���A�Ǧ^�@�Ӧ���
		Collection collection = new Collection();
		collection.setCollectionID(0);
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName
							+ "` where userID=?");
			preparedStatement.setInt(1, userID);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next())
				collection.setCollectionID(rs.getInt("collectionID"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return collection;
	}
}
