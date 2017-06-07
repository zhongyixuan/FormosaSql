package tw.org.formosa.restful;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StoreDao { // ��X�@�Ӯa��ƪ��ާ@�����O

	private Connection connection;
	private String tableName = "�X�@�Ӯa";

	public StoreDao() {
			connection = DbUtil.getConnection();
	}

	public List<Store> getStoreList() { // ���o�Ҧ��X�@�Ӯa�M��A�Ǧ^Store�}�C
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
