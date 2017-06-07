package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StoreDao { // 對合作商家資料表做操作的類別

	private Connection connection;
	private String tableName = "合作商家";

	public StoreDao() {
			connection = DbUtil.getConnection();
	}

	public List<Store> getStoreList() { // 取得所有合作商家清單，傳回Store陣列
		List<Store> stores = new ArrayList<Store>();

		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select * from `" + tableName);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				Store store = new Store();
				store.setStoreID(rs.getInt("storeID"));
				store.setStoreName(rs.getString("storeName"));
				store.setStoreType(rs.getString("storeType"));
				store.setStoreAddress(rs.getString("storeAddress"));
				store.setStoreLongitude(rs.getBigDecimal("storeLongitude"));
				store.setStoreLatitude(rs.getBigDecimal("storeLatitude"));
				stores.add(store);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stores;
	}
}
