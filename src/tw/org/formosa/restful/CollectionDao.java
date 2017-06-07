package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import tw.org.formosa.restful.Collection;
import tw.org.formosa.restful.DbUtil;

public class CollectionDao { //對口袋名單資料表做操作的類別
	private Connection connection;
	private String tableName = "口袋名單";

	public CollectionDao() {
			connection = DbUtil.getConnection();
	}

	public boolean addUserCollection(Collection collection, User user) { //新增一筆收藏，成功傳回true，失敗傳回false
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

	public boolean deleteUserCollection(int collectionID) { //用收藏編號刪除一筆收藏，成功傳回true，失敗傳回false
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
	
	public boolean deleteCollectionByUser(int userID) { //用userID刪除該User所有收藏
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

	public Collection getUserCollectionById(int collectionID) { //用收藏編號取得一筆收藏，傳回Collection物件
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
	
	public List<Collection> getUserCollectionByUser(int userID) { //取得使用者所有收藏，傳回收藏陣列
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

	public Collection getCollectionID(int userID) { //用userID取得單筆收藏編號，傳回一個收藏
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
