package tw.org.formosa.restful;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import tw.org.formosa.restful.User;
import tw.org.formosa.restful.UserDao;
import tw.org.formosa.restful.HotelOrder;
import tw.org.formosa.restful.HotelOrderDao;
import tw.org.formosa.restful.CollectionDao;

@Path("/rest")
public class Service { // restful API類別

	int HTTP_SUCCESS = 200; // 成功代碼200
	int HTTP_INTERNAL_ERROR = 500; // 錯誤代碼500
	int HTTP_PARAMETER = 550; // 變數550
	String RTNCODE_FIELD = "statuscode"; // 狀態碼
	String RTNMES_FIELD = "message"; // 訊息

	@POST
	@Path("/user/addUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addUser(@Context HttpHeaders header, InputStream requestBody) { // 新增一個User，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		String userName = null, userAccount = null, userPassword = null, userEMail = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("userName") != null)
				userName = new String(requJSON.getString("userName").getBytes(
						"ISO-8859-1"), "utf8");

			if (requJSON.getString("userAccount") != null)
				userAccount = requJSON.getString("userAccount");

			if (requJSON.getString("userPassword") != null)
				userPassword = requJSON.getString("userPassword");

			if (requJSON.getString("userEMail") != null)
				userEMail = requJSON.getString("userEMail");

			// 如果有任一資料為空就傳回參數錯誤
			if (userName == null || userAccount == null || userPassword == null
					|| userEMail == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new一個User物件準備傳進資料庫
			UserDao userDao = new UserDao();
			User user = new User();
			user.setUserName(userName);
			user.setUserAccount(userAccount);
			user.setUserPassword(userPassword);
			user.setUserEMail(userEMail);

			// 判斷add有沒有成功
			if (userDao.addUser(user)) {
				int userID = new UserDao().getUserByUserAccount(userAccount)
						.getUserID();
				respJSON.put("userID", userID);
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("UnsupportedEncodingException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/user/deleteUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUser(@Context HttpHeaders header,
			InputStream requestBody) { // 刪除User，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String userID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("userID") != null)
				userID = requJSON.getString("userID");

			// 如果userID為空就傳回參數錯誤
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個User物件用來取得userID刪除訂單
			UserDao userDao = new UserDao();

			// 判斷刪除User有沒有錯誤，一併刪除User相關資料
			if (userDao.deleteUser(Integer.parseInt(userID))) {
				ActivityOrderDao activityOrderDao = new ActivityOrderDao();
				activityOrderDao.deleteOrderByUser(Integer.parseInt(userID));
				CollectionDao collectionDao = new CollectionDao();
				collectionDao.deleteCollectionByUser(Integer.parseInt(userID));
				HotelOrderDao hotelOrderDao = new HotelOrderDao();
				hotelOrderDao.deleteOrderByUser(Integer.parseInt(userID));
				PairDao pairDao = new PairDao();
				pairDao.deletePairByUser(Integer.parseInt(userID));
				PuzzleDao puzzleDao = new PuzzleDao();
				puzzleDao.deletePuzzleByUser(Integer.parseInt(userID));
				TravelDao travelDao = new TravelDao();
				List<Travel> travels = new TravelDao()
						.getTravelByUserID(Integer.parseInt(userID));
				for (int i = 0; i <= travels.size(); i++) {
					TravelAttractionDao travelAttractionDao = new TravelAttractionDao();
					travelAttractionDao.deleteTravelAttractionById(travels.get(
							i).getTravelID());
				}
				travelDao.deleteTravelByUser(Integer.parseInt(userID));
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/user/updateUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUser(@Context HttpHeaders header,
			InputStream requestBody) { // 更新User資料，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer userID = null;
		String userName = null, userPassword = null, userEMail = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			if (requJSON.getString("userName") != null)
				userName = new String(requJSON.getString("userName").getBytes(
						"ISO-8859-1"), "utf8");

			if (requJSON.getString("userPassword") != null)
				userPassword = requJSON.getString("userPassword");

			if (requJSON.getString("userEMail") != null)
				userEMail = requJSON.getString("userEMail");

			// 如果有任一資料為空就傳回參數錯誤
			if (userID == null || userName == null || userPassword == null
					|| userEMail == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new一個User物件準備傳進資料庫更改訂單內容
			UserDao userDao = new UserDao();
			User user = new User();
			user.setUserID(userID);
			user.setUserName(userName);
			user.setUserPassword(userPassword);
			user.setUserEMail(userEMail);

			// 判斷更新User有沒有成功
			if (userDao.updateUser(user)) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("UnsupportedEncodingException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/user/getAllUsers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUsers(@Context HttpHeaders header,
			InputStream requestBody) { // 取得所有User資料
		Response response = null;
		JSONObject respJSON = new JSONObject(); // return value
		JSONArray userJSONArray = new JSONArray();
		try {
			UserDao userDao = new UserDao();
			List<User> users = userDao.getAllUsers();

			for (User u : users) {
				userJSONArray.put(u.toString());
			}
			respJSON.put("Users", userJSONArray);
			response = Response.status(HTTP_SUCCESS)
					.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
					.build();
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "60").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/user/searchUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchUser(@Context HttpHeaders header,
			InputStream requestBody) { // 尋找User
		Response response = null;
		JSONObject respJSON = new JSONObject(); // return value
		String requestBODY = get(requestBody);
		JSONObject requJSON;
		String userID = null;

		try {
			requJSON = new JSONObject(requestBODY);

			if (requJSON.getString("userID") != null)
				userID = requJSON.getString("userID");

			if (userID == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}
			UserDao userDao = new UserDao();
			User user = userDao.getUserById(Integer.parseInt(userID));

			if (user.getUserID() == 0) {
				respJSON.put(RTNMES_FIELD, "No Found Any User");
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Success");
				respJSON.put("User", user.toString());
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();

			try {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "60").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/user/checkUserAccount")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkUserAccount(@Context HttpHeaders header,
			InputStream requestBody) {
		Response response = null;
		JSONObject respJSON = new JSONObject(); // return value
		String requestBODY = get(requestBody);
		JSONObject requJSON;
		String userAccount = null;

		try {
			requJSON = new JSONObject(requestBODY);

			if (requJSON.getString("userAccount") != null)
				userAccount = requJSON.getString("userAccount");

			if (userAccount == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}
			UserDao userDao = new UserDao();
			if (userDao.checkAccount(userAccount)) {
				respJSON.put(RTNMES_FIELD, "Success");
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Fail");
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "99").toString())
						.build();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		}
		return response;
	}

	@POST
	@Path("/user/checkPassword")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkUserPassword(@Context HttpHeaders header,
			InputStream requestBody) {
		Response response = null;
		JSONObject respJSON = new JSONObject(); // return value
		String requestBODY = get(requestBody);
		JSONObject requJSON;
		String userAccount = null, userPassword = null;

		try {
			requJSON = new JSONObject(requestBODY);

			if (requJSON.getString("userAccount") != null)
				userAccount = requJSON.getString("userAccount");

			if (requJSON.getString("userPassword") != null)
				userPassword = requJSON.getString("userPassword");

			if (userAccount == null || userPassword == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}
			UserDao userDao = new UserDao();
			if (userDao.checkPassword(userAccount, userPassword)) {
				respJSON.put(RTNMES_FIELD, "Success");
				int userID = new UserDao().getUserByUserAccount(userAccount)
						.getUserID();
				respJSON.put("userID", userID);
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Fail");
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "99").toString())
						.build();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		}
		return response;
	}

	@POST
	@Path("/activityOrder/createActivityOrder")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createActivityOrder(@Context HttpHeaders header,
			InputStream requestBody) { // 新增一筆訂單，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer userID = null, activityOrderCount = null;
		String activityID = null, activityOrderName = null, activityUserPhone = null, activityUserAddress = null, activityName = null, activityUserNote = null;
		Timestamp activityOrderDate = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			if (requJSON.getString("activityID") != null)
				activityID = new String(requJSON.getString("activityID")
						.getBytes("ISO-8859-1"), "utf8");

			if (requJSON.getString("activityOrderName") != null)
				activityOrderName = new String(requJSON.getString(
						"activityOrderName").getBytes("ISO-8859-1"), "utf8");

			if (requJSON.getString("activityUserPhone") != null)
				activityUserPhone = requJSON.getString("activityUserPhone");

			if (requJSON.getString("activityUserAddress") != null)
				activityUserAddress = new String(requJSON.getString(
						"activityUserAddress").getBytes("ISO-8859-1"), "utf8");

			if (requJSON.getString("activityName") != null)
				activityName = new String(requJSON.getString("activityName")
						.getBytes("ISO-8859-1"), "utf8");

			if (requJSON.getString("activityOrderDate") != null)
				activityOrderDate = new Timestamp(new SimpleDateFormat(
						"yyyy-MM-dd").parse(
						requJSON.getString("activityOrderDate")).getTime());

			if (requJSON.getString("activityOrderCount") != null)
				activityOrderCount = Integer.parseInt(requJSON
						.getString("activityOrderCount"));

			if (requJSON.getString("activityUserNote") != null)
				activityUserNote = new String(requJSON.getString(
						"activityUserNote").getBytes("ISO-8859-1"), "utf8");

			// 如果有任一資料為空就傳回參數錯誤
			if (userID == null || activityOrderCount == null
					|| activityID == null || activityOrderName == null
					|| activityUserPhone == null || activityUserAddress == null
					|| activityName == null || activityUserNote == null
					|| activityOrderDate == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			User user = new UserDao().getUserById(userID);

			// 判斷該user存不存在
			if (user.getUserID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find User ID");
				respJSON.put(RTNCODE_FIELD, "50");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個ActivityOrder物件準備傳進資料庫
			ActivityOrderDao orderDao = new ActivityOrderDao();
			ActivityOrder order = new ActivityOrder();
			order.setUserID(userID);
			order.setActivityID(activityID);
			order.setActivityOrderName(activityOrderName);
			order.setActivityUserPhone(activityUserPhone);
			order.setActivityUserAddress(activityUserAddress);
			order.setActivityName(activityName);
			order.setActivityOrderDate(activityOrderDate);
			order.setActivityOrderCount(activityOrderCount);
			order.setActivityUserNote(activityUserNote);

			// 判斷add有沒有成功
			if (orderDao.addOrder(order, user)) {
				int activityOrderID = new ActivityOrderDao().getOrderID(userID)
						.getActivityOrderID();
				respJSON.put("activityOrderID", activityOrderID);
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("UnsupportedEncodingException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("ParseException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/activityOrder/updateActivityOrder")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateActivityOrder(@Context HttpHeaders header,
			InputStream requestBody) { // 更改一筆訂單，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer activityOrderID = null, activityOrderCount = null;
		String activityOrderName = null, activityUserPhone = null, activityUserAddress = null, activityUserNote = null;
		Timestamp activityOrderDate = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("activityOrderID") != null)
				activityOrderID = Integer.parseInt(requJSON
						.getString("activityOrderID"));

			if (requJSON.getString("activityOrderName") != null)
				activityOrderName = new String(requJSON.getString(
						"activityOrderName").getBytes("ISO-8859-1"), "utf8");

			if (requJSON.getString("activityUserPhone") != null)
				activityUserPhone = requJSON.getString("activityUserPhone");

			if (requJSON.getString("activityUserAddress") != null)
				activityUserAddress = new String(requJSON.getString(
						"activityUserAddress").getBytes("ISO-8859-1"), "utf8");

			if (requJSON.getString("activityOrderDate") != null)
				activityOrderDate = new Timestamp(new SimpleDateFormat(
						"yyyy-MM-dd").parse(
						requJSON.getString("activityOrderDate")).getTime());

			if (requJSON.getString("activityOrderCount") != null)
				activityOrderCount = Integer.parseInt(requJSON
						.getString("activityOrderCount"));

			if (requJSON.getString("activityUserNote") != null)
				activityUserNote = new String(requJSON.getString(
						"activityUserNote").getBytes("ISO-8859-1"), "utf8");

			// 如果有任一資料為空就傳回參數錯誤
			if (activityOrderID == null || activityUserPhone == null
					|| activityOrderName == null || activityUserAddress == null
					|| activityUserNote == null || activityOrderCount == null
					|| activityOrderDate == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new一個ActivityOrder物件準備傳進資料庫更改訂單內容
			ActivityOrderDao orderDao = new ActivityOrderDao();
			ActivityOrder order = new ActivityOrder();
			order.setActivityOrderID(activityOrderID);
			order.setActivityOrderName(activityOrderName);
			order.setActivityUserPhone(activityUserPhone);
			order.setActivityUserAddress(activityUserAddress);
			order.setActivityOrderDate(activityOrderDate);
			order.setActivityOrderCount(activityOrderCount);
			order.setActivityUserNote(activityUserNote);

			// 判斷更新訂單有沒有成功
			if (orderDao.updateOrder(order)) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("UnsupportedEncodingException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("ParseException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/activityOrder/deleteActivityOrder")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteActivityOrder(@Context HttpHeaders header,
			InputStream requestBody) { // 刪除一筆訂單，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String activityOrderID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("activityOrderID") != null)
				activityOrderID = requJSON.getString("activityOrderID");

			// 如果activityOrderID為空就傳回參數錯誤
			if (activityOrderID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個activityOrder物件用來取得hotelOrderID刪除訂單
			ActivityOrderDao orderDao = new ActivityOrderDao();
			// 判斷刪除訂單有沒有錯誤
			if (orderDao.deleteOrder(Integer.parseInt(activityOrderID))) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/activityOrder/getActivityOrderByUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActivityOrderByUser(@Context HttpHeaders header,
			InputStream requestBody) { // 取得user的訂單，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray orderJSONArray = new JSONArray();
		String userID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("userID") != null)
				userID = requJSON.getString("userID");

			// 如果userID為空就傳回參數錯誤
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個activityOrder陣列存放user訂單
			List<ActivityOrder> orders = new ActivityOrderDao()
					.getOrderByUserID(Integer.parseInt(userID));

			// 將List陣列轉成JSONArray
			for (ActivityOrder order : orders)
				orderJSONArray.put(order.toString());

			// respJSON裡放入orders
			respJSON.put("Orders", orderJSONArray);
			// 判斷order陣列有沒有內容
			if (orders.size() != 0) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Can't find User Order:"
						+ new UserDao().getUserById(Integer.parseInt(userID))
								.getUserAccount() + " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/activityOrder/getActivityOrderByID")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActivityOrderByID(@Context HttpHeaders header,
			InputStream requestBody) { // 取得某筆訂單，傳回Response(存放傳回狀態
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String activityOrderID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("activityOrderID") != null)
				activityOrderID = requJSON.getString("activityOrderID");

			// 如果activityOrderID為空就傳回參數錯誤
			if (activityOrderID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			ActivityOrder order = new ActivityOrderDao()
					.getUserOrderById(Integer.parseInt(activityOrderID));

			// 判斷activityOrderID存不存在
			if (order.getActivityOrderID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find ActivityOrder:"
						+ activityOrderID + " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			} else {
				// respJSON裡放入Order
				respJSON.put("Order", order.toString());
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/collection/addUserCollection")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addUserCollection(@Context HttpHeaders header,
			InputStream requestBody) { // 新增一筆收藏，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer userID = null;
		String attractionID = null, attractionName = null, county = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			if (requJSON.getString("attractionID") != null)
				attractionID = requJSON.getString("attractionID");
			
			if (requJSON.getString("attractionName") != null)
				attractionName = new String(requJSON.getString("attractionName").getBytes(
						"ISO-8859-1"), "utf8");

			if (requJSON.getString("county") != null)
				county = new String(requJSON.getString("county").getBytes(
						"ISO-8859-1"), "utf8");

			// 如果有任一資料為空就傳回參數錯誤
			if (userID == null || attractionID == null || attractionName == null
					|| county == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			User user = new UserDao().getUserById(userID);

			// 判斷該user存不存在
			if (user.getUserID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find User ID");
				respJSON.put(RTNCODE_FIELD, "50");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個collection物件準備傳進資料庫
			CollectionDao collectionDao = new CollectionDao();
			Collection collection = new Collection();
			collection.setUserID(user.getUserID());
			collection.setAttractionID(attractionID);
			collection.setAttractionName(attractionName);
			collection.setCounty(county);

			// 判斷add有沒有成功
			if (collectionDao.addUserCollection(collection, user)) {
				int collectionID = new CollectionDao().getCollectionID(userID)
						.getCollectionID();
				respJSON.put("collectionID", collectionID);
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("UnsupportedEncodingException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/collection/deleteUserCollection")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUserCollection(@Context HttpHeaders header,
			InputStream requestBody) { // 刪除一筆收藏，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String collectionID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("collectionID") != null)
				collectionID = requJSON.getString("collectionID");

			// 如果collectionID為空就傳回參數錯誤
			if (collectionID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個collectionDao物件用來取得collectionID刪除訂單
			CollectionDao collectionDao = new CollectionDao();
			// 判斷刪除收藏有沒有錯誤
			if (collectionDao.deleteUserCollection(Integer
					.parseInt(collectionID))) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/collection/getUserCollectionByUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserCollectionByUser(@Context HttpHeaders header,
			InputStream requestBody) { // 取得user的收藏，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray collectionJSONArray = new JSONArray();
		String userID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("userID") != null)
				userID = requJSON.getString("userID");

			// 如果userID為空就傳回參數錯誤
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個collection陣列存放user訂單
			List<Collection> collections = new CollectionDao()
					.getUserCollectionByUser(Integer.parseInt(userID));

			// 將List陣列轉成JSONArray
			for (Collection collection : collections)
				collectionJSONArray.put(collection.toString());

			// respJSON裡放入collections
			respJSON.put("Collections", collectionJSONArray);
			// 判斷collection陣列有沒有內容
			if (collections.size() != 0) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Can't find User Collection:"
						+ new UserDao().getUserById(Integer.parseInt(userID))
								.getUserAccount() + " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/collection/getCollectionByID")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCollectionByID(@Context HttpHeaders header,
			InputStream requestBody) { // 取得某筆收藏，傳回Response(存放傳回狀態
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String collectionID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("collectionID") != null)
				collectionID = requJSON.getString("collectionID");

			// 如果collectionID為空就傳回參數錯誤
			if (collectionID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			Collection collection = new CollectionDao()
					.getUserCollectionById(Integer.parseInt(collectionID));

			// 判斷hotelOrderID存不存在
			if (collection.getCollectionID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find Collection:"
						+ collectionID + " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			} else {
				// respJSON裡放入collection
				respJSON.put("Collection", collection.toString());
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/comment/addUserComment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addUserComment(@Context HttpHeaders header,
			InputStream requestBody) { // 新增一個評論，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer userID = null, userScore = null;
		String attractionID = null, attractionType = null, userComment = null;
		Timestamp commentTime = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			if (requJSON.getString("attractionID") != null)
				attractionID = requJSON.getString("attractionID");

			if (requJSON.getString("attractionType") != null)
				attractionType = requJSON.getString("attractionType");

			if (requJSON.getString("userComment") != null)
				userComment = requJSON.getString("userComment");

			if (requJSON.getString("userScore") != null)
				userScore = Integer.parseInt(requJSON.getString("userScore"));

			if (requJSON.getString("commentTime") != null)
				commentTime = new Timestamp(new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss").parse(
						requJSON.getString("commentTime")).getTime());

			// 如果有任一資料為空就傳回參數錯誤
			if (userID == null || attractionID == null
					|| attractionType == null || userComment == null
					|| userScore == null || commentTime == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			User user = new UserDao().getUserById(userID);

			// 判斷該user存不存在
			if (user.getUserID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find User ID");
				respJSON.put(RTNCODE_FIELD, "50");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個Comment物件準備傳進資料庫
			CommentDao commentDao = new CommentDao();
			Comment comment = new Comment();
			comment.setUserID(user.getUserID());
			comment.setAttractionID(attractionID);
			comment.setAttractionType(attractionType);
			comment.setUserComment(userComment);
			comment.setUserScore(userScore);
			comment.setCommentTime(commentTime);

			// 判斷add有沒有成功
			if (commentDao.addUserComment(comment, user)) {
				int commentID = new CommentDao().getCommentID(userID)
						.getCommentID();
				respJSON.put("commentID", commentID);
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("ParseException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/comment/getCommentByAttraction")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCommentByAttraction(@Context HttpHeaders header,
			InputStream requestBody) { // 取得景點的comment，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray commentJSONArray = new JSONArray();
		String attractionID = null, attractionType = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("attractionID") != null)
				attractionID = requJSON.getString("attractionID");

			if (requJSON.getString("attractionType") != null)
				attractionType = requJSON.getString("attractionType");

			// 如果attractionID、attractionType為空就傳回參數錯誤
			if (attractionID == null || attractionType == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個Comment陣列存放景點評論評分
			List<Comment> comments = new CommentDao().getCommentByAttraction(
					attractionID, attractionType);

			// 將List陣列轉成JSONArray
			for (Comment comment : comments)
				commentJSONArray.put(comment.toString());

			// respJSON裡放入comments
			respJSON.put("Comments", commentJSONArray);
			// 判斷comment陣列有沒有內容
			if (comments.size() != 0) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Can't find Attraction Comment:"
						+ attractionID + " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/hotelOrder/createHotelOrder")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createHotelOrder(@Context HttpHeaders header,
			InputStream requestBody) { // 新增一筆訂單，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer userID = null, hotelOrderCount = null;
		String hotelOrderName = null, hotelUserPhone = null, hotelUserAddress = null, hotelName = null, hotelAddress = null, hotelUserNote = null;
		Timestamp hotelOrderDate = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			if (requJSON.getString("hotelOrderName") != null)
				hotelOrderName = new String(requJSON
						.getString("hotelOrderName").getBytes("ISO-8859-1"),
						"utf8");

			if (requJSON.getString("hotelUserPhone") != null)
				hotelUserPhone = requJSON.getString("hotelUserPhone");

			if (requJSON.getString("hotelUserAddress") != null)
				hotelUserAddress = new String(requJSON.getString(
						"hotelUserAddress").getBytes("ISO-8859-1"), "utf8");

			if (requJSON.getString("hotelName") != null)
				hotelName = new String(requJSON.getString("hotelName")
						.getBytes("ISO-8859-1"), "utf8");

			if (requJSON.getString("hotelAddress") != null)
				hotelAddress = new String(requJSON.getString("hotelAddress")
						.getBytes("ISO-8859-1"), "utf8");

			if (requJSON.getString("hotelOrderDate") != null)
				hotelOrderDate = new Timestamp(new SimpleDateFormat(
						"yyyy-MM-dd").parse(
						requJSON.getString("hotelOrderDate")).getTime());

			if (requJSON.getString("hotelOrderCount") != null)
				hotelOrderCount = Integer.parseInt(requJSON
						.getString("hotelOrderCount"));

			if (requJSON.getString("hotelUserNote") != null)
				hotelUserNote = new String(requJSON.getString("hotelUserNote")
						.getBytes("ISO-8859-1"), "utf8");

			// 如果有任一資料為空就傳回參數錯誤
			if (userID == null || hotelOrderCount == null
					|| hotelOrderName == null || hotelUserPhone == null
					|| hotelUserAddress == null || hotelName == null
					|| hotelAddress == null || hotelUserNote == null
					|| hotelOrderDate == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			User user = new UserDao().getUserById(userID);

			// 判斷該user存不存在
			if (user.getUserID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find User ID");
				respJSON.put(RTNCODE_FIELD, "50");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個hotelOrder物件準備傳進資料庫
			HotelOrderDao orderDao = new HotelOrderDao();
			HotelOrder order = new HotelOrder();
			order.setUserID(userID);
			order.setHotelOrderName(hotelOrderName);
			order.setgetHotelUserPhone(hotelUserPhone);
			order.setHotelUserAddress(hotelUserAddress);
			order.setHotelName(hotelName);
			order.setHotelAddress(hotelAddress);
			order.setHotelOrderDate(hotelOrderDate);
			order.setHotelOrderCount(hotelOrderCount);
			order.setHotelUserNote(hotelUserNote);

			// 判斷add有沒有成功
			if (orderDao.addOrder(order, user)) {
				int hotelOrderID = new HotelOrderDao().getOrderID(userID)
						.getHotelOrderID();
				respJSON.put("hotelOrderID", hotelOrderID);
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("UnsupportedEncodingException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("ParseException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/hotelOrder/updateHotelOrder")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateHotelOrder(@Context HttpHeaders header,
			InputStream requestBody) { // 更改一筆訂單，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer hotelOrderID = null, hotelOrderCount = null;
		String hotelOrderName = null, hotelUserPhone = null, hotelUserAddress = null, hotelUserNote = null;
		Timestamp hotelOrderDate = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("hotelOrderID") != null)
				hotelOrderID = Integer.parseInt(requJSON
						.getString("hotelOrderID"));

			if (requJSON.getString("hotelOrderName") != null)
				hotelOrderName = new String(requJSON
						.getString("hotelOrderName").getBytes("ISO-8859-1"),
						"utf8");

			if (requJSON.getString("hotelUserPhone") != null)
				hotelUserPhone = requJSON.getString("hotelUserPhone");

			if (requJSON.getString("hotelUserAddress") != null)
				hotelUserAddress = new String(requJSON.getString(
						"hotelUserAddress").getBytes("ISO-8859-1"), "utf8");

			if (requJSON.getString("hotelOrderDate") != null)
				hotelOrderDate = new Timestamp(new SimpleDateFormat(
						"yyyy-MM-dd").parse(
						requJSON.getString("hotelOrderDate")).getTime());

			if (requJSON.getString("hotelOrderCount") != null)
				hotelOrderCount = Integer.parseInt(requJSON
						.getString("hotelOrderCount"));

			if (requJSON.getString("hotelUserNote") != null)
				hotelUserNote = new String(requJSON.getString("hotelUserNote")
						.getBytes("ISO-8859-1"), "utf8");

			// 如果有任一資料為空就傳回參數錯誤
			if (hotelOrderID == null || hotelUserPhone == null
					|| hotelOrderName == null || hotelUserAddress == null
					|| hotelUserNote == null || hotelOrderCount == null
					|| hotelOrderDate == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new一個hotelOrder物件準備傳進資料庫更改訂單內容
			HotelOrderDao orderDao = new HotelOrderDao();
			HotelOrder order = new HotelOrder();
			order.setHotelOrderID(hotelOrderID);
			order.setHotelOrderName(hotelOrderName);
			order.setgetHotelUserPhone(hotelUserPhone);
			order.setHotelUserAddress(hotelUserAddress);
			order.setHotelOrderDate(hotelOrderDate);
			order.setHotelOrderCount(hotelOrderCount);
			order.setHotelUserNote(hotelUserNote);

			// 判斷更新訂單有沒有成功
			if (orderDao.updateOrder(order)) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("UnsupportedEncodingException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("ParseException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/hotelOrder/deleteHotelOrder")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteHotelOrder(@Context HttpHeaders header,
			InputStream requestBody) { // 刪除一筆訂單，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String hotelOrderID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("hotelOrderID") != null)
				hotelOrderID = requJSON.getString("hotelOrderID");

			// 如果hotelOrderID為空就傳回參數錯誤
			if (hotelOrderID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個hotelOrder物件用來取得hotelOrderID刪除訂單
			HotelOrderDao orderDao = new HotelOrderDao();
			// 判斷刪除訂單有沒有錯誤
			if (orderDao.deleteOrder(Integer.parseInt(hotelOrderID))) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/hotelOrder/getHotelOrderByUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHotelOrderByUser(@Context HttpHeaders header,
			InputStream requestBody) { // 取得user的訂單，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray orderJSONArray = new JSONArray();
		String userID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("userID") != null)
				userID = requJSON.getString("userID");

			// 如果userID為空就傳回參數錯誤
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個hotelOrder陣列存放user訂單
			List<HotelOrder> orders = new HotelOrderDao()
					.getOrderByUserID(Integer.parseInt(userID));

			// 將List陣列轉成JSONArray
			for (HotelOrder order : orders)
				orderJSONArray.put(order.toString());

			// respJSON裡放入orders
			respJSON.put("Orders", orderJSONArray);
			// 判斷order陣列有沒有內容
			if (orders.size() != 0) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Can't find User Order:"
						+ new UserDao().getUserById(Integer.parseInt(userID))
								.getUserAccount() + " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/hotelOrder/getHotelOrderByID")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHotelOrderByID(@Context HttpHeaders header,
			InputStream requestBody) { // 取得某筆訂單，傳回Response(存放傳回狀態
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String hotelOrderID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("hotelOrderID") != null)
				hotelOrderID = requJSON.getString("hotelOrderID");

			// 如果HotelOrderID為空就傳回參數錯誤
			if (hotelOrderID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			HotelOrder order = new HotelOrderDao()
					.getUserOrderByOrderId(Integer.parseInt(hotelOrderID));

			// 判斷hotelOrderID存不存在
			if (order.getHotelOrderID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find HotelOrder:"
						+ hotelOrderID + " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			} else {
				// respJSON裡放入Order
				respJSON.put("Order", order.toString());
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/pair/addPair")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addPair(@Context HttpHeaders header, InputStream requestBody) { // 新增一筆Pair，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer userID = null, productPrice = null;
		String shopName = null, productName = null, preferentialType = null, pairAddress = null, userFeature = null;
		Timestamp pairTime = null;
		Time waitTime = null;
		BigDecimal pairLongitude = null, pairLatitude = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			if (requJSON.getString("shopName") != null)
				shopName = new String(requJSON.getString("shopName").getBytes(
						"ISO-8859-1"), "utf8");

			if (requJSON.getString("productName") != null)
				productName = new String(requJSON.getString("productName")
						.getBytes("ISO-8859-1"), "utf8");

			if (requJSON.getString("productPrice") != null)
				productPrice = Integer.parseInt(requJSON
						.getString("productPrice"));

			if (requJSON.getString("preferentialType") != null)
				preferentialType = new String(requJSON.getString(
						"preferentialType").getBytes("ISO-8859-1"), "utf8");

			if (requJSON.getString("pairAddress") != null)
				pairAddress = new String(requJSON.getString("pairAddress")
						.getBytes("ISO-8859-1"), "utf8");

			if (requJSON.getString("userFeature") != null)
				userFeature = new String(requJSON.getString("userFeature")
						.getBytes("ISO-8859-1"), "utf8");

			if (requJSON.getString("pairTime") != null)
				pairTime = new Timestamp(new SimpleDateFormat(
						"yyyy-MM-dd hh:mm:ss").parse(
						requJSON.getString("pairTime")).getTime());

			if (requJSON.getString("waitTime") != null)
				waitTime = new Time(new SimpleDateFormat("hh:mm:ss").parse(
						requJSON.getString("waitTime")).getTime());

			if (requJSON.getString("pairLongitude") != null)
				pairLongitude = new BigDecimal(
						requJSON.getString("pairLongitude"));

			if (requJSON.getString("pairLatitude") != null)
				pairLatitude = new BigDecimal(
						requJSON.getString("pairLatitude"));

			// 如果有任一資料為空就傳回參數錯誤
			if (userID == null || shopName == null || productName == null
					|| productPrice == null || preferentialType == null
					|| pairAddress == null || userFeature == null
					|| pairTime == null || waitTime == null
					|| pairLongitude == null || pairLatitude == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			User user = new UserDao().getUserById(userID);

			// 判斷該user存不存在
			if (user.getUserID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find User ID");
				respJSON.put(RTNCODE_FIELD, "50");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個Pair物件準備傳進資料庫
			PairDao pairDao = new PairDao();
			Pair pair = new Pair();
			pair.setUserID(user.getUserID());
			pair.setShopName(shopName);
			pair.setProductName(productName);
			pair.setProductPrice(productPrice);
			pair.setPreferentialType(preferentialType);
			pair.setPairAddress(pairAddress);
			pair.setUserFeature(userFeature);
			pair.setPairTime(pairTime);
			pair.setWaitTime(waitTime);
			pair.setPairLongitude(pairLongitude);
			pair.setPairLatitude(pairLatitude);

			// 判斷add有沒有成功
			if (pairDao.addPair(pair, user)) {
				int pairID = new PairDao().getPairID(userID).getPairID();
				respJSON.put("pairID", pairID);
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("ParseException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("UnsupportedEncodingException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/pair/deletePair")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletePair(@Context HttpHeaders header,
			InputStream requestBody) { // 新增一筆Pair，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String pairID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("pairID") != null)
				pairID = requJSON.getString("pairID");

			// 如果pairID為空就傳回參數錯誤
			if (pairID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個pair物件用來取得pairID刪除訂單
			PairDao pairDao = new PairDao();
			// 判斷刪除訂單有沒有錯誤
			if (pairDao.deletePair(Integer.parseInt(pairID))) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/pair/updatePair")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatePair(@Context HttpHeaders header,
			InputStream requestBody) { // 新增一筆Pair，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer pairID = null, productPrice = null;
		String productName = null, preferentialType = null, pairAddress = null, userFeature = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("pairID") != null)
				pairID = Integer.parseInt(requJSON.getString("pairID"));

			if (requJSON.getString("productName") != null)
				productName = new String(requJSON.getString("productName")
						.getBytes("ISO-8859-1"), "utf8");

			if (requJSON.getString("productPrice") != null)
				productPrice = Integer.parseInt(requJSON
						.getString("productPrice"));

			if (requJSON.getString("preferentialType") != null)
				preferentialType = new String(requJSON.getString(
						"preferentialType").getBytes("ISO-8859-1"), "utf8");

			if (requJSON.getString("pairAddress") != null)
				pairAddress = new String(requJSON.getString("pairAddress")
						.getBytes("ISO-8859-1"), "utf8");

			if (requJSON.getString("userFeature") != null)
				userFeature = new String(requJSON.getString("userFeature")
						.getBytes("ISO-8859-1"), "utf8");

			// 如果有任一資料為空就傳回參數錯誤
			if (pairID == null || productName == null || productPrice == null
					|| preferentialType == null || pairAddress == null
					|| userFeature == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new一個Pair物件準備傳進資料庫更改訂單內容
			PairDao pairDao = new PairDao();
			Pair pair = new Pair();
			pair.setPairID(pairID);
			pair.setProductName(productName);
			pair.setProductPrice(productPrice);
			pair.setPreferentialType(preferentialType);
			pair.setPairAddress(pairAddress);
			pair.setUserFeature(userFeature);

			// 判斷更新訂單有沒有成功
			if (pairDao.updatePair(pair)) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("UnsupportedEncodingException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}

		return response;
	}

	@POST
	@Path("/pair/getPairByPairId")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPairByPairId(@Context HttpHeaders header,
			InputStream requestBody) { // 取得某筆訂單，傳回Response(存放傳回狀態
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String pairID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("pairID") != null)
				pairID = requJSON.getString("pairID");

			// 如果pairID為空就傳回參數錯誤
			if (pairID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			Pair pair = new PairDao().getPairById(Integer.parseInt(pairID));

			// 判斷pairID存不存在
			if (pair.getPairID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find Pair:" + pairID
						+ " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			} else {
				// respJSON裡放入Order
				respJSON.put("Pair", pair.toString());
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/pair/getPairList")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPairList(@Context HttpHeaders header,
			InputStream requestBody) { // 取得所有合作商家List，傳回Response(存放傳回狀態)
		Response response = null;
		JSONObject respJSON = new JSONObject();
		JSONArray pairJSONArray = new JSONArray();

		try {

			// new一個store陣列合作商家List
			List<Pair> pairs = new PairDao().getPairList();

			// 將List陣列轉成JSONArray
			for (Pair pair : pairs)
				pairJSONArray.put(pair.toString());

			// respJSON裡放入stores
			respJSON.put("Pairs", pairJSONArray);
			// 判斷order陣列有沒有內容
			if (pairs.size() != 0) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Can't find Stores");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/pair/alreadyPair")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response alreadyPair(@Context HttpHeaders header,
			InputStream requestBody) { // 新增一筆Pair，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer pairID = null;
		Boolean paired = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("pairID") != null)
				pairID = Integer.parseInt(requJSON.getString("pairID"));

			if (requJSON.getString("paired") != null)
				paired = new Boolean(requJSON.getString("paired"));

			// 如果有任一資料為空就傳回參數錯誤
			if (pairID == null || paired == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new一個Pair物件準備傳進資料庫更改訂單內容
			PairDao pairDao = new PairDao();
			Pair pair = new Pair();
			pair.setPairID(pairID);
			pair.setPaired(paired);

			// 判斷更新訂單有沒有成功
			if (pairDao.alreadyPair(pair)) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		}
		return response;
	}

	@POST
	@Path("/pair/getPairByUserId")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPairByUserId(@Context HttpHeaders header,
			InputStream requestBody) { // 取得所有使用者Pair List，傳回Response(存放傳回狀態)
		Response response = null;
		JSONObject respJSON = new JSONObject();
		JSONArray pairJSONArray = new JSONArray();
		String requestBODY = get(requestBody);
		String userID = null;

		try {

			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("userID") != null)
				userID = requJSON.getString("userID");

			// 如果pairID為空就傳回參數錯誤
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個pair陣列使用者pairList
			List<Pair> pairs = new PairDao().getPairByUserID(Integer
					.parseInt(userID));

			// 將List陣列轉成JSONArray
			for (Pair pair : pairs)
				pairJSONArray.put(pair.toString());

			// respJSON裡放入stores
			respJSON.put("Pairs", pairJSONArray);
			// 判斷pair陣列有沒有內容
			if (pairs.size() != 0) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Can't find Pairs");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/pairTracing/addPairTracing")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addPairTracing(@Context HttpHeaders header,
			InputStream requestBody) { // 新增一筆pairTracing，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer pairID = null, userID = null, tracingUserID = null;
		BigDecimal pairLongitude = null, pairLatitude = null, tracingLongitude = null, tracingLatitude = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			if (requJSON.getString("pairID") != null)
				pairID = Integer.parseInt(requJSON.getString("pairID"));

			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			if (requJSON.getString("pairLongitude") != null)
				pairLongitude = new BigDecimal(
						requJSON.getString("pairLongitude"));

			if (requJSON.getString("pairLatitude") != null)
				pairLatitude = new BigDecimal(
						requJSON.getString("pairLatitude"));

			if (requJSON.getString("tracingUserID") != null)
				tracingUserID = Integer.parseInt(requJSON
						.getString("tracingUserID"));

			if (requJSON.getString("tracingLongitude") != null)
				tracingLongitude = new BigDecimal(
						requJSON.getString("tracingLongitude"));

			if (requJSON.getString("tracingLatitude") != null)
				tracingLatitude = new BigDecimal(
						requJSON.getString("tracingLatitude"));

			// 如果有任一資料為空就傳回參數錯誤
			if (pairID == null || userID == null || pairLongitude == null
					|| pairLatitude == null || tracingUserID == null
					|| tracingLongitude == null || tracingLatitude == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			User user = new UserDao().getUserById(userID);
			User tracingUser = new UserDao().getUserById(tracingUserID);

			// 判斷該user存不存在
			if (user.getUserID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find User ID");
				respJSON.put(RTNCODE_FIELD, "50");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			if (tracingUser.getUserID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find Tracing User ID");
				respJSON.put(RTNCODE_FIELD, "50");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個Pair物件準備傳進資料庫
			PairTracingDao pairTracingDao = new PairTracingDao();
			PairTracing pairTracing = new PairTracing();
			pairTracing.setPairID(pairID);
			pairTracing.setUserID(user.getUserID());
			pairTracing.setPairLongitude(pairLongitude);
			pairTracing.setPairLatitude(pairLatitude);
			pairTracing.setTracingUserID(tracingUser.getUserID());
			pairTracing.setTracingLongitude(tracingLongitude);
			pairTracing.setTracingLatitude(tracingLatitude);

			// 判斷add有沒有成功
			if (pairTracingDao.addPairTracing(pairTracing, user, tracingUser)) {
				respJSON.put("pairID", pairID);
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		}
		return response;
	}

	@POST
	@Path("/pairTracing/updatePairTracingByUserID")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatePairTracingByUserID(@Context HttpHeaders header,
			InputStream requestBody) { // 新增一筆PairTracing，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer pairID = null, userID = null;
		BigDecimal pairLongitude = null, pairLatitude = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("pairID") != null)
				pairID = Integer.parseInt(requJSON.getString("pairID"));

			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			if (requJSON.getString("pairLongitude") != null)
				pairLongitude = new BigDecimal(
						requJSON.getString("pairLongitude"));

			if (requJSON.getString("pairLatitude") != null)
				pairLatitude = new BigDecimal(
						requJSON.getString("pairLatitude"));

			// 如果有任一資料為空就傳回參數錯誤
			if (pairID == null || userID == null || pairLongitude == null
					|| pairLatitude == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new一個PairTracing物件準備傳進資料庫更改訂單內容
			PairTracingDao pairTracingDao = new PairTracingDao();
			PairTracing pairTracing = new PairTracing();
			pairTracing.setPairID(pairID);
			pairTracing.setUserID(userID);
			pairTracing.setPairLongitude(pairLongitude);
			pairTracing.setPairLatitude(pairLatitude);

			// 判斷更新訂單有沒有成功
			if (pairTracingDao.updatePairTracingByUserID(pairTracing)) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/pairTracing/updatePairTracingByTracingUserID")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatePairTracingByTracingUserID(
			@Context HttpHeaders header, InputStream requestBody) { // 新增一筆PairTracing，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer pairID = null, tracingUserID = null;
		BigDecimal tracingLongitude = null, tracingLatitude = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("pairID") != null)
				pairID = Integer.parseInt(requJSON.getString("pairID"));

			if (requJSON.getString("tracingUserID") != null)
				tracingUserID = Integer.parseInt(requJSON
						.getString("tracingUserID"));

			if (requJSON.getString("tracingLongitude") != null)
				tracingLongitude = new BigDecimal(
						requJSON.getString("tracingLongitude"));

			if (requJSON.getString("tracingLatitude") != null)
				tracingLatitude = new BigDecimal(
						requJSON.getString("tracingLatitude"));

			// 如果有任一資料為空就傳回參數錯誤
			if (pairID == null || tracingUserID == null
					|| tracingLongitude == null || tracingLatitude == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new一個PairTracing物件準備傳進資料庫更改訂單內容
			PairTracingDao pairTracingDao = new PairTracingDao();
			PairTracing pairTracing = new PairTracing();
			pairTracing.setPairID(pairID);
			pairTracing.setTracingUserID(tracingUserID);
			pairTracing.setTracingLongitude(tracingLongitude);
			pairTracing.setTracingLatitude(tracingLatitude);

			// 判斷更新訂單有沒有成功
			if (pairTracingDao.updatePairTracingByTracingUserID(pairTracing)) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/pairTracing/getPairTracingById")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPairTracingById(@Context HttpHeaders header,
			InputStream requestBody) { // 取得某筆訂單，傳回Response(存放傳回狀態
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String pairID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("pairID") != null)
				pairID = requJSON.getString("pairID");

			// 如果pairID為空就傳回參數錯誤
			if (pairID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			PairTracing pairTracing = new PairTracingDao()
					.getPairTracingById(Integer.parseInt(pairID));

			// 判斷pairID存不存在
			if (pairTracing.getPairID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find Pair:" + pairID
						+ " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			} else {
				// respJSON裡放入Order
				respJSON.put("PairTracing", pairTracing.toString());
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/pairTracing/getPairID")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPairID(@Context HttpHeaders header,
			InputStream requestBody) { // 取得所有合作商家List，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray pairTracingJSONArray = new JSONArray();
		Integer userID = null;

		try {

			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			// 如果pairID為空就傳回參數錯誤
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個store陣列合作商家List
			List<PairTracing> pairTracings = new PairTracingDao()
					.getPairID(userID);

			// 將List陣列轉成JSONArray
			for (PairTracing pairTracing : pairTracings)
				pairTracingJSONArray.put(pairTracing.getPairID());

			// respJSON裡放入stores
			respJSON.put("PairID", pairTracingJSONArray);
			// 判斷order陣列有沒有內容
			if (pairTracings.size() != 0) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Can't find Pairs");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/puzzle/addPuzzle")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addPuzzle(@Context HttpHeaders header,
			InputStream requestBody) { // 新增一筆Puzzle，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer userID = null, puzzleQuestionID = null, level = null;
		String puzzleGetAttractionName = null, county = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			if (requJSON.getString("puzzleQuestionID") != null)
				puzzleQuestionID = Integer.parseInt(requJSON
						.getString("puzzleQuestionID"));

			if (requJSON.getString("puzzleGetAttractionName") != null)
				puzzleGetAttractionName = new String(requJSON.getString(
						"puzzleGetAttractionName").getBytes("ISO-8859-1"),
						"utf8");

			if (requJSON.getString("county") != null)
				county = new String(requJSON.getString("county").getBytes(
						"ISO-8859-1"), "utf8");

			if (requJSON.getString("level") != null)
				level = Integer.parseInt(requJSON.getString("level"));

			// 如果有任一資料為空就傳回參數錯誤
			if (userID == null || puzzleQuestionID == null
					|| puzzleGetAttractionName == null || county == null
					|| level == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			User user = new UserDao().getUserById(userID);

			// 判斷該user存不存在
			if (user.getUserID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find User ID");
				respJSON.put(RTNCODE_FIELD, "50");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個Puzzle物件準備傳進資料庫
			PuzzleDao puzzleDao = new PuzzleDao();
			Puzzle puzzle = new Puzzle();
			puzzle.setUserID(user.getUserID());
			puzzle.setPuzzleQuestionID(puzzleQuestionID);
			puzzle.setPuzzleGetAttractionName(puzzleGetAttractionName);
			puzzle.setCounty(county);
			puzzle.setLevel(level);

			// 判斷add有沒有成功
			if (puzzleDao.addPuzzle(puzzle, user)) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("UnsupportedEncodingException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/puzzle/getPuzzleByUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPuzzleByUser(@Context HttpHeaders header,
			InputStream requestBody) { // 取得user的訂單，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray puzzleJSONArray = new JSONArray();
		String userID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("userID") != null)
				userID = requJSON.getString("userID");

			// 如果userID為空就傳回參數錯誤
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個puzzle陣列存放user訂單
			List<Puzzle> puzzles = new PuzzleDao().getPuzzleByUser(Integer
					.parseInt(userID));

			// 將List陣列轉成JSONArray
			for (Puzzle puzzle : puzzles)
				puzzleJSONArray.put(puzzle.toString());

			// respJSON裡放入puzzles
			respJSON.put("Puzzles", puzzleJSONArray);
			// 判斷order陣列有沒有內容
			if (puzzles.size() != 0) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Can't find User Puzzle:"
						+ new UserDao().getUserById(Integer.parseInt(userID))
								.getUserAccount() + " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/puzzle/getPuzzleByUserAndCounty")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPuzzleByUserAndCounty(@Context HttpHeaders header,
			InputStream requestBody) { // 取得user的訂單，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray puzzleJSONArray = new JSONArray();
		String userID = null, county = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("userID") != null)
				userID = requJSON.getString("userID");

			if (requJSON.getString("county") != null)
				county = new String(requJSON.getString("county").getBytes(
						"ISO-8859-1"), "utf8");

			// 如果userID為空就傳回參數錯誤
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個puzzle陣列存放user訂單
			List<Puzzle> puzzles = new PuzzleDao().getPuzzleByUserAndCounty(
					Integer.parseInt(userID), county);

			// 將List陣列轉成JSONArray
			for (Puzzle puzzle : puzzles)
				puzzleJSONArray.put(puzzle.toString());

			// respJSON裡放入puzzles
			respJSON.put("Puzzles", puzzleJSONArray);
			// 判斷order陣列有沒有內容
			if (puzzles.size() != 0) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Can't find User Puzzle:"
						+ new UserDao().getUserById(Integer.parseInt(userID))
								.getUserAccount() + " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("ParseException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/puzzleQuestion/getPuzzleQuestionByCountyAndLevel")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPuzzleQuestionByCountyAndLevel(
			@Context HttpHeaders header, InputStream requestBody) { // 取得某筆訂單，傳回Response(存放傳回狀態
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String county = null, level = null;
		JSONArray puzzleQuestionJSONArray = new JSONArray();

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("county") != null)
				county = new String(requJSON.getString("county").getBytes(
						"ISO-8859-1"), "utf8");

			if (requJSON.getString("level") != null)
				level = requJSON.getString("level");

			// 如果puzzleQuestionID為空就傳回參數錯誤
			if (county == null || level == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			List<PuzzleQuestion> puzzleQuestions = new PuzzleQuestionDao()
					.getPuzzleQuestionByCountyAndLevel(county,
							Integer.parseInt(level));

			for (PuzzleQuestion puzzleQuestion : puzzleQuestions)
				puzzleQuestionJSONArray.put(puzzleQuestion.toString());

			respJSON.put("PuzzleQuestions", puzzleQuestionJSONArray);

			// 判斷order陣列有沒有內容
			if (puzzleQuestions.size() != 0) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Can't find Puzzle Questions");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("UnsupportedEncodingException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/store/getStoreList")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStoreList(@Context HttpHeaders header,
			InputStream requestBody) { // 取得所有合作商家List，傳回Response(存放傳回狀態)
		Response response = null;
		JSONObject respJSON = new JSONObject();
		JSONArray storeJSONArray = new JSONArray();

		try {

			// new一個store陣列合作商家List
			List<Store> stores = new StoreDao().getStoreList();

			// 將List陣列轉成JSONArray
			for (Store store : stores)
				storeJSONArray.put(store.toString());

			// respJSON裡放入stores
			respJSON.put("Stores", storeJSONArray);
			// 判斷order陣列有沒有內容
			if (stores.size() != 0) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Can't find Stores");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/travel/addTravel")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addTravel(@Context HttpHeaders header,
			InputStream requestBody) { // 新增一筆Travel，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer userID = null, travelDays = null;
		String travelName = null;
		Timestamp travelDate = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			if (requJSON.getString("travelName") != null)
				travelName = new String(requJSON.getString("travelName")
						.getBytes("ISO-8859-1"), "utf8");

			if (requJSON.getString("travelDate") != null)
				travelDate = new Timestamp(new SimpleDateFormat("yyyy-MM-dd")
						.parse(requJSON.getString("travelDate")).getTime());

			if (requJSON.getString("travelDays") != null)
				travelDays = Integer.parseInt(requJSON.getString("travelDays"));

			// 如果有任一資料為空就傳回參數錯誤
			if (userID == null || travelName == null || travelDate == null
					|| travelDays == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			User user = new UserDao().getUserById(userID);

			// 判斷該user存不存在
			if (user.getUserID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find User ID");
				respJSON.put(RTNCODE_FIELD, "50");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個Travel物件準備傳進資料庫
			TravelDao travelDao = new TravelDao();
			Travel travel = new Travel();
			travel.setUserID(user.getUserID());
			travel.setTravelName(travelName);
			travel.setTravelDate(travelDate);
			travel.setTravelDays(travelDays);

			// 判斷add有沒有成功
			if (travelDao.addTravel(travel, user)) {
				int travelID = new TravelDao().getTravelID(userID)
						.getTravelID();
				respJSON.put("travelID", travelID);
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("UnsupportedEncodingException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("ParseException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/travel/deleteTravel")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteTravel(@Context HttpHeaders header,
			InputStream requestBody) { // 刪除一筆訂單，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String travelID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("travelID") != null)
				travelID = requJSON.getString("travelID");

			// 如果travelID為空就傳回參數錯誤
			if (travelID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個travel物件用來取得travelID刪除訂單
			TravelDao travelDao = new TravelDao();
			// 判斷刪除travel有沒有錯誤
			if (travelDao.deleteTravel(Integer.parseInt(travelID))) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/travel/updateTravel")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateTravel(@Context HttpHeaders header,
			InputStream requestBody) { // 更改一筆Travel，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer travelID = null, travelDays = null;
		String travelName = null;
		Timestamp travelDate = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("travelID") != null)
				travelID = Integer.parseInt(requJSON.getString("travelID"));

			if (requJSON.getString("travelName") != null)
				travelName = new String(requJSON.getString("travelName")
						.getBytes("ISO-8859-1"), "utf8");

			if (requJSON.getString("travelDate") != null)
				travelDate = new Timestamp(new SimpleDateFormat("yyyy-MM-dd")
						.parse(requJSON.getString("travelDate")).getTime());

			if (requJSON.getString("travelDays") != null)
				travelDays = Integer.parseInt(requJSON.getString("travelDays"));

			// 如果有任一資料為空就傳回參數錯誤
			if (travelID == null || travelName == null || travelDate == null
					|| travelDays == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new一個travel物件準備傳進資料庫更改訂單內容
			TravelDao travelDao = new TravelDao();
			Travel travel = new Travel();
			travel.setTravelID(travelID);
			travel.setTravelName(travelName);
			travel.setTravelDate(travelDate);
			travel.setTravelDays(travelDays);

			// 判斷更新travel有沒有成功
			if (travelDao.updateTravel(travel)) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("UnsupportedEncodingException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("ParseException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/travel/getUserTravelByTravelId")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserTravelByTravelId(@Context HttpHeaders header,
			InputStream requestBody) { // 取得某筆訂單，傳回Response(存放傳回狀態
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String travelID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("travelID") != null)
				travelID = requJSON.getString("travelID");

			// 如果travelID為空就傳回參數錯誤
			if (travelID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			Travel travel = new TravelDao().getUserTravelById(Integer
					.parseInt(travelID));

			// 判斷hotelOrderID存不存在
			if (travel.getTravelID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find Travel:" + travelID
						+ " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			} else {
				// respJSON裡放入Order
				respJSON.put("Travel", travel.toString());
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/travel/getTravelByUserID")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTravelByUserID(@Context HttpHeaders header,
			InputStream requestBody) { // 取得user的訂單，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray travelJSONArray = new JSONArray();
		String userID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("userID") != null)
				userID = requJSON.getString("userID");

			// 如果userID為空就傳回參數錯誤
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個Travel陣列存放user訂單
			List<Travel> travels = new TravelDao().getTravelByUserID(Integer
					.parseInt(userID));

			// 將List陣列轉成JSONArray
			for (Travel travel : travels)
				travelJSONArray.put(travel.toString());

			// respJSON裡放入travels
			respJSON.put("travels", travelJSONArray);
			// 判斷order陣列有沒有內容
			if (travels.size() != 0) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Can't find User Order:"
						+ new UserDao().getUserById(Integer.parseInt(userID))
								.getUserAccount() + " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/travelAttraction/addTravelAttraction")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addTravelAttraction(@Context HttpHeaders header,
			InputStream requestBody) { // 新增一筆TravelAttraction，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer travelID = null;
		String attractionName = null;
		Timestamp dayDate = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			if (requJSON.getString("travelID") != null)
				travelID = Integer.parseInt(requJSON.getString("travelID"));

			if (requJSON.getString("attractionName") != null)
				attractionName = new String(requJSON
						.getString("attractionName").getBytes("ISO-8859-1"),
						"utf8");

			if (requJSON.getString("dayDate") != null)
				dayDate = new Timestamp(new SimpleDateFormat(
						"yyyy-MM-dd hh:mm:ss").parse(
						requJSON.getString("dayDate")).getTime());

			// 如果有任一資料為空就傳回參數錯誤
			if (travelID == null || attractionName == null || dayDate == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new一個TravelAttraction物件準備傳進資料庫
			TravelAttractionDao travelAttractionDao = new TravelAttractionDao();
			TravelAttraction travelAttraction = new TravelAttraction();
			travelAttraction.setTravelID(travelID);
			travelAttraction.setAttractionName(attractionName);
			travelAttraction.setDayDate(dayDate);

			// 判斷add有沒有成功
			if (travelAttractionDao.addTravelAttraction(travelAttraction)) {
				respJSON.put("travelID", travelID);
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("UnsupportedEncodingException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("ParseException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/travelAttraction/deleteTravelAttraction")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteTravelAttraction(@Context HttpHeaders header,
			InputStream requestBody) { // 刪除一筆訂單，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String travelID = null, attractionName = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("travelID") != null)
				travelID = requJSON.getString("travelID");

			if (requJSON.getString("attractionName") != null)
				attractionName = requJSON.getString("attractionName");

			// 如果travelID、attractionID為空就傳回參數錯誤
			if (travelID == null || attractionName == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個travelAttraction物件用來取得travelID、attractionID刪除travelAttraction
			TravelAttractionDao travelAttractionDao = new TravelAttractionDao();
			// 判斷刪除travelAttraction有沒有錯誤
			if (travelAttractionDao.deleteTravelAttraction(
					Integer.parseInt(travelID), attractionName)) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/travelAttraction/getTravelAttractionByIDAndDay")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTravelAttractionByIDAndDay(@Context HttpHeaders header,
			InputStream requestBody) { // 取得TravelAttraction，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray travelAttractionJSONArray = new JSONArray();
		String travelID = null;
		Timestamp dayDate = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("travelID") != null)
				travelID = requJSON.getString("travelID");

			if (requJSON.getString("dayDate") != null)
				dayDate = new Timestamp(new SimpleDateFormat("yyyy-MM-dd")
						.parse(requJSON.getString("dayDate")).getTime());

			// 如果userID為空就傳回參數錯誤
			if (travelID == null || dayDate == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個TravelAttraction陣列存放user TravelAttraction
			List<TravelAttraction> travelAttractions = new TravelAttractionDao()
					.getTravelAttractionByIDAndDay(Integer.parseInt(travelID),
							dayDate);

			// 將List陣列轉成JSONArray
			for (TravelAttraction travelAttraction : travelAttractions)
				travelAttractionJSONArray.put(travelAttraction.toString());

			// respJSON裡放入travels
			respJSON.put("travelAttractions", travelAttractionJSONArray);
			// 判斷order陣列有沒有內容
			if (travelAttractions.size() != 0) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Can't find Travel Attraction");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("ParseException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/travelAttraction/getTravelAttractionByID")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTravelAttractionByID(@Context HttpHeaders header,
			InputStream requestBody) { // 取得TravelAttraction，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray travelAttractionJSONArray = new JSONArray();
		String travelID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("travelID") != null)
				travelID = requJSON.getString("travelID");

			// 如果userID為空就傳回參數錯誤
			if (travelID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個TravelAttraction陣列存放user TravelAttraction
			List<TravelAttraction> travelAttractions = new TravelAttractionDao()
					.getTravelAttractionByID(Integer.parseInt(travelID));

			// 將List陣列轉成JSONArray
			for (TravelAttraction travelAttraction : travelAttractions)
				travelAttractionJSONArray.put(travelAttraction.toString());

			// respJSON裡放入travels
			respJSON.put("travelAttractions", travelAttractionJSONArray);
			// 判斷order陣列有沒有內容
			if (travelAttractions.size() != 0) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Can't find Travel Attraction");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/travelPair/addTravelPair")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addTravelPair(@Context HttpHeaders header,
			InputStream requestBody) { // 新增一筆Pair，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer userID = null, travelID = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			if (requJSON.getString("travelID") != null)
				travelID = Integer.parseInt(requJSON.getString("travelID"));

			// 如果有任一資料為空就傳回參數錯誤
			if (userID == null || travelID == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			User user = new UserDao().getUserById(userID);

			// 判斷該user存不存在
			if (user.getUserID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find User ID");
				respJSON.put(RTNCODE_FIELD, "50");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個Pair物件準備傳進資料庫
			TravelPairDao travelPairDao = new TravelPairDao();
			TravelPair travelPair = new TravelPair();
			travelPair.setUserID(user.getUserID());
			travelPair.setTravelID(travelID);

			// 判斷add有沒有成功
			if (travelPairDao.addTravelPair(travelPair, user)) {
				int travelPairID = new TravelPairDao().getTravelPairID(userID)
						.getTravelPairID();
				respJSON.put("travelPairID", travelPairID);
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		}
		return response;
	}

	@POST
	@Path("/travelPair/deleteTravelPair")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteTravelPair(@Context HttpHeaders header,
			InputStream requestBody) { // 新增一筆Pair，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String travelPairID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("travelPairID") != null)
				travelPairID = requJSON.getString("travelPairID");

			// 如果pairID為空就傳回參數錯誤
			if (travelPairID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個pair物件用來取得pairID刪除訂單
			TravelPairDao travelPairDao = new TravelPairDao();
			// 判斷刪除訂單有沒有錯誤
			if (travelPairDao.deleteTravelPair(Integer.parseInt(travelPairID))) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/travelPair/alreadyTravelPair")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response alreadyTravelPair(@Context HttpHeaders header,
			InputStream requestBody) { // 新增一筆Pair，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer travelPairID = null;
		Boolean paired = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("travelPairID") != null)
				travelPairID = Integer.parseInt(requJSON
						.getString("travelPairID"));

			if (requJSON.getString("paired") != null)
				paired = new Boolean(requJSON.getString("paired"));

			// 如果有任一資料為空就傳回參數錯誤
			if (travelPairID == null || paired == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new一個Pair物件準備傳進資料庫更改訂單內容
			TravelPairDao travelPairDao = new TravelPairDao();
			TravelPair travelPair = new TravelPair();
			travelPair.setTravelPairID(travelPairID);
			travelPair.setPaired(paired);

			// 判斷更新訂單有沒有成功
			if (travelPairDao.alreadyPair(travelPair)) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		}
		return response;
	}

	@POST
	@Path("/travelPair/getTravelPairById")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTravelPairById(@Context HttpHeaders header,
			InputStream requestBody) { // 取得某筆訂單，傳回Response(存放傳回狀態
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String travelPairID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("travelPairID") != null)
				travelPairID = requJSON.getString("travelPairID");

			// 如果pairID為空就傳回參數錯誤
			if (travelPairID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			TravelPair travelPair = new TravelPairDao()
					.getTravelPairById(Integer.parseInt(travelPairID));

			// 判斷pairID存不存在
			if (travelPair.getTravelPairID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find Pair:" + travelPairID
						+ " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			} else {
				// respJSON裡放入Order
				respJSON.put("TravelPair", travelPair.toString());
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/travelPair/getTravelPairList")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTravelPairList(@Context HttpHeaders header,
			InputStream requestBody) { // 取得所有合作商家List，傳回Response(存放傳回狀態)
		Response response = null;
		JSONObject respJSON = new JSONObject();
		JSONArray travelPairJSONArray = new JSONArray();

		try {

			// new一個store陣列合作商家List
			List<TravelPair> travelPairs = new TravelPairDao()
					.getTravelPairList();

			// 將List陣列轉成JSONArray
			for (TravelPair travelPair : travelPairs)
				travelPairJSONArray.put(travelPair.toString());

			// respJSON裡放入stores
			respJSON.put("TravelPairs", travelPairJSONArray);
			// 判斷order陣列有沒有內容
			if (travelPairs.size() != 0) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Can't find Stores");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/travelPair/getPairByUserID")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPairByUserID(@Context HttpHeaders header,
			InputStream requestBody) { // 取得所有使用者Pair List，傳回Response(存放傳回狀態)
		Response response = null;
		JSONObject respJSON = new JSONObject();
		JSONArray travelPairJSONArray = new JSONArray();
		String requestBODY = get(requestBody);
		String userID = null;

		try {

			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("userID") != null)
				userID = requJSON.getString("userID");

			// 如果pairID為空就傳回參數錯誤
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個pair陣列使用者pairList
			List<TravelPair> travelPairs = new TravelPairDao()
					.getTravelPairByUserID(Integer.parseInt(userID));

			// 將List陣列轉成JSONArray
			for (TravelPair travelPair : travelPairs)
				travelPairJSONArray.put(travelPair.toString());

			// respJSON裡放入stores
			respJSON.put("TravelPairs", travelPairJSONArray);
			// 判斷pair陣列有沒有內容
			if (travelPairs.size() != 0) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Can't find Pairs");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/travelPairUserInfo/addTravelPairUserInfo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addTravelPairUserInfo(@Context HttpHeaders header,
			InputStream requestBody) { // 新增一筆pairTracing，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer travelPairID = null, userID = null, pairUserID = null;
		String pairUserName = null, pairUserEMail = null, pairUserLine = null, pairUserPhone = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			if (requJSON.getString("travelPairID") != null)
				travelPairID = Integer.parseInt(requJSON
						.getString("travelPairID"));

			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			if (requJSON.getString("pairUserID") != null)
				pairUserID = Integer.parseInt(requJSON.getString("pairUserID"));

			if (requJSON.getString("pairUserName") != null)
				pairUserName = new String(requJSON.getString("pairUserName")
						.getBytes("ISO-8859-1"), "utf8");

			if (requJSON.getString("pairUserEMail") != null)
				pairUserEMail = requJSON.getString("pairUserEMail");

			if (requJSON.getString("pairUserLine") != null)
				pairUserLine = requJSON.getString("pairUserLine");

			if (requJSON.getString("pairUserPhone") != null)
				pairUserPhone = requJSON.getString("pairUserPhone");

			// 如果有任一資料為空就傳回參數錯誤
			if (travelPairID == null || userID == null || pairUserID == null
					|| pairUserName == null || pairUserEMail == null
					|| pairUserLine == null || pairUserPhone == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			User user = new UserDao().getUserById(userID);
			User pairUser = new UserDao().getUserById(pairUserID);

			// 判斷該user存不存在
			if (user.getUserID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find User ID");
				respJSON.put(RTNCODE_FIELD, "50");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			if (pairUser.getUserID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find Tracing User ID");
				respJSON.put(RTNCODE_FIELD, "50");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個Pair物件準備傳進資料庫
			TravelPairUserInfoDao travelPairUserInfoDao = new TravelPairUserInfoDao();
			TravelPairUserInfo travelPairUserInfo = new TravelPairUserInfo();
			travelPairUserInfo.setTravelPairID(travelPairID);
			travelPairUserInfo.setUserID(userID);
			travelPairUserInfo.setPairUserID(pairUserID);
			travelPairUserInfo.setPairUserName(pairUserName);
			travelPairUserInfo.setPairUserEMail(pairUserEMail);
			travelPairUserInfo.setPairUserLine(pairUserLine);
			travelPairUserInfo.setPairUserPhone(pairUserPhone);

			// 判斷add有沒有成功
			if (travelPairUserInfoDao.addTravelPairUserInfo(travelPairUserInfo,
					user, pairUser)) {
				respJSON.put("travelPairID", travelPairID);
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("UnsupportedEncodingException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/travelPairUserInfo/updateTravelPairUserInfo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateTravelPairUserInfo(@Context HttpHeaders header,
			InputStream requestBody) { // 新增一筆PairTracing，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer travelPairID = null;
		String pairUserName = null, pairUserEMail = null, pairUserLine = null, pairUserPhone = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("travelPairID") != null)
				travelPairID = Integer.parseInt(requJSON
						.getString("travelPairID"));

			if (requJSON.getString("pairUserName") != null)
				pairUserName = new String(requJSON.getString("pairUserName")
						.getBytes("ISO-8859-1"), "utf8");

			if (requJSON.getString("pairUserEMail") != null)
				pairUserEMail = requJSON.getString("pairUserEMail");

			if (requJSON.getString("pairUserLine") != null)
				pairUserLine = requJSON.getString("pairUserLine");

			if (requJSON.getString("pairUserPhone") != null)
				pairUserPhone = requJSON.getString("pairUserPhone");

			// 如果有任一資料為空就傳回參數錯誤
			if (travelPairID == null || pairUserName == null
					|| pairUserEMail == null || pairUserLine == null
					|| pairUserPhone == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new一個PairTracing物件準備傳進資料庫更改訂單內容
			TravelPairUserInfoDao travelPairUserInfoDao = new TravelPairUserInfoDao();
			TravelPairUserInfo travelPairUserInfo = new TravelPairUserInfo();
			travelPairUserInfo.setTravelPairID(travelPairID);
			travelPairUserInfo.setPairUserName(pairUserName);
			travelPairUserInfo.setPairUserEMail(pairUserEMail);
			travelPairUserInfo.setPairUserLine(pairUserLine);
			travelPairUserInfo.setPairUserPhone(pairUserPhone);
			// 判斷更新訂單有沒有成功
			if (travelPairUserInfoDao
					.updateTravelPairUserInfo(travelPairUserInfo)) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("UnsupportedEncodingException Parse Error");
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/travelPairUserInfo/alreadySure")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response alreadySure(@Context HttpHeaders header,
			InputStream requestBody) { // 新增一筆Pair，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer travelPairID = null;
		Boolean userSure = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("travelPairID") != null)
				travelPairID = Integer.parseInt(requJSON
						.getString("travelPairID"));

			if (requJSON.getString("userSure") != null)
				userSure = new Boolean(requJSON.getString("userSure"));

			// 如果有任一資料為空就傳回參數錯誤
			if (travelPairID == null || userSure == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new一個Pair物件準備傳進資料庫更改訂單內容
			TravelPairUserInfoDao travelPairUserInfoDao = new TravelPairUserInfoDao();
			TravelPairUserInfo travelPairUserInfo = new TravelPairUserInfo();
			travelPairUserInfo.setTravelPairID(travelPairID);
			travelPairUserInfo.setUserSure(userSure);

			// 判斷更新訂單有沒有成功
			if (travelPairUserInfoDao.alreadySure(travelPairUserInfo)) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		}
		return response;
	}

	@POST
	@Path("/travelPairUserInfo/getTravelPairUserInfoById")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTravelPairUserInfoById(@Context HttpHeaders header,
			InputStream requestBody) { // 取得某筆訂單，傳回Response(存放傳回狀態
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String travelPairID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("travelPairID") != null)
				travelPairID = requJSON.getString("travelPairID");

			// 如果pairID為空就傳回參數錯誤
			if (travelPairID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			TravelPairUserInfo travelPairUserInfo = new TravelPairUserInfoDao()
					.getTravelPairUserInfoById(Integer.parseInt(travelPairID));

			// 判斷pairID存不存在
			if (travelPairUserInfo.getTravelPairID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find Pair:" + travelPairID
						+ " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			} else {
				// respJSON裡放入Order
				respJSON.put("TravelPairUserInfo",
						travelPairUserInfo.toString());
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/travelPairUserInfo/getTravelPairID")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTravelPairID(@Context HttpHeaders header,
			InputStream requestBody) { // 取得所有合作商家List，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray travelPairUserInfoJSONArray = new JSONArray();
		Integer userID = null;

		try {

			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			// 如果pairID為空就傳回參數錯誤
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個store陣列合作商家List
			List<TravelPairUserInfo> travelPairUserInfos = new TravelPairUserInfoDao()
					.getTravelPairID(userID);

			// 將List陣列轉成JSONArray
			for (TravelPairUserInfo travelPairUserInfo : travelPairUserInfos) {
				JSONObject travelJSON = new JSONObject();
				travelJSON.accumulate("travelPairID",
						String.valueOf(travelPairUserInfo.getTravelPairID()));
				travelJSON.accumulate("userSure",
						String.valueOf(travelPairUserInfo.getUserSure()));
				travelPairUserInfoJSONArray.put(travelJSON);
			}
			;

			// respJSON裡放入stores
			respJSON.put("TravelPairs", travelPairUserInfoJSONArray);
			// 判斷order陣列有沒有內容
			if (travelPairUserInfos.size() != 0) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Can't find Pairs");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/travelPairUserInfo/getTravelPairIDByPairUserID")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTravelPairIDByPairUserID(@Context HttpHeaders header,
			InputStream requestBody) { // 取得所有合作商家List，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray travelPairUserInfoJSONArray = new JSONArray();
		Integer userID = null;

		try {

			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			// 如果pairID為空就傳回參數錯誤
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個store陣列合作商家List
			List<TravelPairUserInfo> travelPairUserInfos = new TravelPairUserInfoDao()
					.getTravelPairIDByPairUserID(userID);

			// 將List陣列轉成JSONArray
			for (TravelPairUserInfo travelPairUserInfo : travelPairUserInfos) {
				JSONObject travelJSON = new JSONObject();
				travelJSON.accumulate("travelPairID",
						String.valueOf(travelPairUserInfo.getTravelPairID()));
				travelJSON.accumulate("userSure",
						String.valueOf(travelPairUserInfo.getUserSure()));
				travelPairUserInfoJSONArray.put(travelJSON);
			}
			;

			// respJSON裡放入stores
			respJSON.put("TravelPairs", travelPairUserInfoJSONArray);
			// 判斷order陣列有沒有內容
			if (travelPairUserInfos.size() != 0) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Can't find Pairs");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/travelPairUserInfo/getTravelPairIDByUserID")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTravelPairIDByUserID(@Context HttpHeaders header,
			InputStream requestBody) { // 取得所有合作商家List，傳回Response(存放傳回狀態)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray travelPairUserInfoJSONArray = new JSONArray();
		Integer userID = null;

		try {

			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			// 如果pairID為空就傳回參數錯誤
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new一個store陣列合作商家List
			List<TravelPairUserInfo> travelPairUserInfos = new TravelPairUserInfoDao()
					.getTravelPairIDByUserID(userID);

			// 將List陣列轉成JSONArray
			for (TravelPairUserInfo travelPairUserInfo : travelPairUserInfos) {
				JSONObject travelJSON = new JSONObject();
				travelJSON.accumulate("travelPairID",
						String.valueOf(travelPairUserInfo.getTravelPairID()));
				travelJSON.accumulate("userSure",
						String.valueOf(travelPairUserInfo.getUserSure()));
				travelPairUserInfoJSONArray.put(travelJSON);
			}
			;

			// respJSON裡放入stores
			respJSON.put("TravelPairs", travelPairUserInfoJSONArray);
			// 判斷order陣列有沒有內容
			if (travelPairUserInfos.size() != 0) {
				response = Response.status(HTTP_SUCCESS)
						.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
						.build();
			} else {
				respJSON.put(RTNMES_FIELD, "Can't find Pairs");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	@POST
	@Path("/travelPairUserInfo/getTravelPairInfoByUserID")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTravelPairInfoByUserID(@Context HttpHeaders header,
			InputStream requestBody) { // 取得所有User資料
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray travelPairUserInfoJSONArray = new JSONArray();
		Integer userID = null;

		try {

			JSONObject requJSON = new JSONObject(requestBODY);

			// 從requestBody取出需要資料
			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			// 如果pairID為空就傳回參數錯誤
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			UserDao userDao = new UserDao();
			List<TravelPairUserInfo> travelPairUserInfos = new TravelPairUserInfoDao()
					.getTravelPairInfoByUserID(userID);

			for (TravelPairUserInfo travelPairUserInfo : travelPairUserInfos)
				travelPairUserInfoJSONArray.put(travelPairUserInfo.toString());

			respJSON.put("TravelPairs", travelPairUserInfoJSONArray);
			response = Response.status(HTTP_SUCCESS)
					.entity(respJSON.put(RTNCODE_FIELD, "0").toString())
					.build();
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "60").toString())
						.build();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return response;
	}

	public String get(InputStream is) {
		StringBuffer buffer = new StringBuffer();

		int bufferContent = 0;
		do {
			try {
				bufferContent = is.read();
				if (bufferContent > 0)
					buffer.append((char) bufferContent);

			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (bufferContent > 0);
		return buffer.toString();
	}
}
