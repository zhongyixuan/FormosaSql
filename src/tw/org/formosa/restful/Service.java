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
public class Service { // restful API���O

	int HTTP_SUCCESS = 200; // ���\�N�X200
	int HTTP_INTERNAL_ERROR = 500; // ���~�N�X500
	int HTTP_PARAMETER = 550; // �ܼ�550
	String RTNCODE_FIELD = "statuscode"; // ���A�X
	String RTNMES_FIELD = "message"; // �T��

	@POST
	@Path("/user/addUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addUser(@Context HttpHeaders header, InputStream requestBody) { // �s�W�@��User�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		String userName = null, userAccount = null, userPassword = null, userEMail = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("userName") != null)
				userName = new String(requJSON.getString("userName").getBytes(
						"ISO-8859-1"), "utf8");

			if (requJSON.getString("userAccount") != null)
				userAccount = requJSON.getString("userAccount");

			if (requJSON.getString("userPassword") != null)
				userPassword = requJSON.getString("userPassword");

			if (requJSON.getString("userEMail") != null)
				userEMail = requJSON.getString("userEMail");

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
			if (userName == null || userAccount == null || userPassword == null
					|| userEMail == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new�@��User����ǳƶǶi��Ʈw
			UserDao userDao = new UserDao();
			User user = new User();
			user.setUserName(userName);
			user.setUserAccount(userAccount);
			user.setUserPassword(userPassword);
			user.setUserEMail(userEMail);

			// �P�_add���S�����\
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
			InputStream requestBody) { // �R��User�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String userID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("userID") != null)
				userID = requJSON.getString("userID");

			// �p�GuserID���ŴN�Ǧ^�Ѽƿ��~
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��User����ΨӨ��ouserID�R���q��
			UserDao userDao = new UserDao();

			// �P�_�R��User���S�����~�A�@�֧R��User�������
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
			InputStream requestBody) { // ��sUser��ơA�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer userID = null;
		String userName = null, userPassword = null, userEMail = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			if (requJSON.getString("userName") != null)
				userName = new String(requJSON.getString("userName").getBytes(
						"ISO-8859-1"), "utf8");

			if (requJSON.getString("userPassword") != null)
				userPassword = requJSON.getString("userPassword");

			if (requJSON.getString("userEMail") != null)
				userEMail = requJSON.getString("userEMail");

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
			if (userID == null || userName == null || userPassword == null
					|| userEMail == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new�@��User����ǳƶǶi��Ʈw���q�椺�e
			UserDao userDao = new UserDao();
			User user = new User();
			user.setUserID(userID);
			user.setUserName(userName);
			user.setUserPassword(userPassword);
			user.setUserEMail(userEMail);

			// �P�_��sUser���S�����\
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
			InputStream requestBody) { // ���o�Ҧ�User���
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
			InputStream requestBody) { // �M��User
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
			InputStream requestBody) { // �s�W�@���q��A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer userID = null, activityOrderCount = null;
		String activityID = null, activityOrderName = null, activityUserPhone = null, activityUserAddress = null, activityName = null, activityUserNote = null;
		Timestamp activityOrderDate = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
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

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
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

			// �P�_��user�s���s�b
			if (user.getUserID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find User ID");
				respJSON.put(RTNCODE_FIELD, "50");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��ActivityOrder����ǳƶǶi��Ʈw
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

			// �P�_add���S�����\
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
			InputStream requestBody) { // ���@���q��A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer activityOrderID = null, activityOrderCount = null;
		String activityOrderName = null, activityUserPhone = null, activityUserAddress = null, activityUserNote = null;
		Timestamp activityOrderDate = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
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

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
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

			// new�@��ActivityOrder����ǳƶǶi��Ʈw���q�椺�e
			ActivityOrderDao orderDao = new ActivityOrderDao();
			ActivityOrder order = new ActivityOrder();
			order.setActivityOrderID(activityOrderID);
			order.setActivityOrderName(activityOrderName);
			order.setActivityUserPhone(activityUserPhone);
			order.setActivityUserAddress(activityUserAddress);
			order.setActivityOrderDate(activityOrderDate);
			order.setActivityOrderCount(activityOrderCount);
			order.setActivityUserNote(activityUserNote);

			// �P�_��s�q�榳�S�����\
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
			InputStream requestBody) { // �R���@���q��A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String activityOrderID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("activityOrderID") != null)
				activityOrderID = requJSON.getString("activityOrderID");

			// �p�GactivityOrderID���ŴN�Ǧ^�Ѽƿ��~
			if (activityOrderID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��activityOrder����ΨӨ��ohotelOrderID�R���q��
			ActivityOrderDao orderDao = new ActivityOrderDao();
			// �P�_�R���q�榳�S�����~
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
			InputStream requestBody) { // ���ouser���q��A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray orderJSONArray = new JSONArray();
		String userID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("userID") != null)
				userID = requJSON.getString("userID");

			// �p�GuserID���ŴN�Ǧ^�Ѽƿ��~
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��activityOrder�}�C�s��user�q��
			List<ActivityOrder> orders = new ActivityOrderDao()
					.getOrderByUserID(Integer.parseInt(userID));

			// �NList�}�C�নJSONArray
			for (ActivityOrder order : orders)
				orderJSONArray.put(order.toString());

			// respJSON�̩�Jorders
			respJSON.put("Orders", orderJSONArray);
			// �P�_order�}�C���S�����e
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
			InputStream requestBody) { // ���o�Y���q��A�Ǧ^Response(�s��Ǧ^���A
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String activityOrderID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("activityOrderID") != null)
				activityOrderID = requJSON.getString("activityOrderID");

			// �p�GactivityOrderID���ŴN�Ǧ^�Ѽƿ��~
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

			// �P�_activityOrderID�s���s�b
			if (order.getActivityOrderID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find ActivityOrder:"
						+ activityOrderID + " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			} else {
				// respJSON�̩�JOrder
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
			InputStream requestBody) { // �s�W�@�����áA�Ǧ^Response(�s��Ǧ^���A)
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

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
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

			// �P�_��user�s���s�b
			if (user.getUserID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find User ID");
				respJSON.put(RTNCODE_FIELD, "50");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��collection����ǳƶǶi��Ʈw
			CollectionDao collectionDao = new CollectionDao();
			Collection collection = new Collection();
			collection.setUserID(user.getUserID());
			collection.setAttractionID(attractionID);
			collection.setAttractionName(attractionName);
			collection.setCounty(county);

			// �P�_add���S�����\
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
			InputStream requestBody) { // �R���@�����áA�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String collectionID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("collectionID") != null)
				collectionID = requJSON.getString("collectionID");

			// �p�GcollectionID���ŴN�Ǧ^�Ѽƿ��~
			if (collectionID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��collectionDao����ΨӨ��ocollectionID�R���q��
			CollectionDao collectionDao = new CollectionDao();
			// �P�_�R�����æ��S�����~
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
			InputStream requestBody) { // ���ouser�����áA�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray collectionJSONArray = new JSONArray();
		String userID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("userID") != null)
				userID = requJSON.getString("userID");

			// �p�GuserID���ŴN�Ǧ^�Ѽƿ��~
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��collection�}�C�s��user�q��
			List<Collection> collections = new CollectionDao()
					.getUserCollectionByUser(Integer.parseInt(userID));

			// �NList�}�C�নJSONArray
			for (Collection collection : collections)
				collectionJSONArray.put(collection.toString());

			// respJSON�̩�Jcollections
			respJSON.put("Collections", collectionJSONArray);
			// �P�_collection�}�C���S�����e
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
			InputStream requestBody) { // ���o�Y�����áA�Ǧ^Response(�s��Ǧ^���A
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String collectionID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("collectionID") != null)
				collectionID = requJSON.getString("collectionID");

			// �p�GcollectionID���ŴN�Ǧ^�Ѽƿ��~
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

			// �P�_hotelOrderID�s���s�b
			if (collection.getCollectionID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find Collection:"
						+ collectionID + " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			} else {
				// respJSON�̩�Jcollection
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
			InputStream requestBody) { // �s�W�@�ӵ��סA�Ǧ^Response(�s��Ǧ^���A)
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

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
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

			// �P�_��user�s���s�b
			if (user.getUserID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find User ID");
				respJSON.put(RTNCODE_FIELD, "50");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��Comment����ǳƶǶi��Ʈw
			CommentDao commentDao = new CommentDao();
			Comment comment = new Comment();
			comment.setUserID(user.getUserID());
			comment.setAttractionID(attractionID);
			comment.setAttractionType(attractionType);
			comment.setUserComment(userComment);
			comment.setUserScore(userScore);
			comment.setCommentTime(commentTime);

			// �P�_add���S�����\
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
			InputStream requestBody) { // ���o���I��comment�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray commentJSONArray = new JSONArray();
		String attractionID = null, attractionType = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("attractionID") != null)
				attractionID = requJSON.getString("attractionID");

			if (requJSON.getString("attractionType") != null)
				attractionType = requJSON.getString("attractionType");

			// �p�GattractionID�BattractionType���ŴN�Ǧ^�Ѽƿ��~
			if (attractionID == null || attractionType == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��Comment�}�C�s���I���׵���
			List<Comment> comments = new CommentDao().getCommentByAttraction(
					attractionID, attractionType);

			// �NList�}�C�নJSONArray
			for (Comment comment : comments)
				commentJSONArray.put(comment.toString());

			// respJSON�̩�Jcomments
			respJSON.put("Comments", commentJSONArray);
			// �P�_comment�}�C���S�����e
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
			InputStream requestBody) { // �s�W�@���q��A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer userID = null, hotelOrderCount = null;
		String hotelOrderName = null, hotelUserPhone = null, hotelUserAddress = null, hotelName = null, hotelAddress = null, hotelUserNote = null;
		Timestamp hotelOrderDate = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
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

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
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

			// �P�_��user�s���s�b
			if (user.getUserID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find User ID");
				respJSON.put(RTNCODE_FIELD, "50");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��hotelOrder����ǳƶǶi��Ʈw
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

			// �P�_add���S�����\
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
			InputStream requestBody) { // ���@���q��A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer hotelOrderID = null, hotelOrderCount = null;
		String hotelOrderName = null, hotelUserPhone = null, hotelUserAddress = null, hotelUserNote = null;
		Timestamp hotelOrderDate = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
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

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
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

			// new�@��hotelOrder����ǳƶǶi��Ʈw���q�椺�e
			HotelOrderDao orderDao = new HotelOrderDao();
			HotelOrder order = new HotelOrder();
			order.setHotelOrderID(hotelOrderID);
			order.setHotelOrderName(hotelOrderName);
			order.setgetHotelUserPhone(hotelUserPhone);
			order.setHotelUserAddress(hotelUserAddress);
			order.setHotelOrderDate(hotelOrderDate);
			order.setHotelOrderCount(hotelOrderCount);
			order.setHotelUserNote(hotelUserNote);

			// �P�_��s�q�榳�S�����\
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
			InputStream requestBody) { // �R���@���q��A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String hotelOrderID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("hotelOrderID") != null)
				hotelOrderID = requJSON.getString("hotelOrderID");

			// �p�GhotelOrderID���ŴN�Ǧ^�Ѽƿ��~
			if (hotelOrderID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��hotelOrder����ΨӨ��ohotelOrderID�R���q��
			HotelOrderDao orderDao = new HotelOrderDao();
			// �P�_�R���q�榳�S�����~
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
			InputStream requestBody) { // ���ouser���q��A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray orderJSONArray = new JSONArray();
		String userID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("userID") != null)
				userID = requJSON.getString("userID");

			// �p�GuserID���ŴN�Ǧ^�Ѽƿ��~
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��hotelOrder�}�C�s��user�q��
			List<HotelOrder> orders = new HotelOrderDao()
					.getOrderByUserID(Integer.parseInt(userID));

			// �NList�}�C�নJSONArray
			for (HotelOrder order : orders)
				orderJSONArray.put(order.toString());

			// respJSON�̩�Jorders
			respJSON.put("Orders", orderJSONArray);
			// �P�_order�}�C���S�����e
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
			InputStream requestBody) { // ���o�Y���q��A�Ǧ^Response(�s��Ǧ^���A
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String hotelOrderID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("hotelOrderID") != null)
				hotelOrderID = requJSON.getString("hotelOrderID");

			// �p�GHotelOrderID���ŴN�Ǧ^�Ѽƿ��~
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

			// �P�_hotelOrderID�s���s�b
			if (order.getHotelOrderID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find HotelOrder:"
						+ hotelOrderID + " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			} else {
				// respJSON�̩�JOrder
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
	public Response addPair(@Context HttpHeaders header, InputStream requestBody) { // �s�W�@��Pair�A�Ǧ^Response(�s��Ǧ^���A)
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

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
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

			// �P�_��user�s���s�b
			if (user.getUserID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find User ID");
				respJSON.put(RTNCODE_FIELD, "50");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��Pair����ǳƶǶi��Ʈw
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

			// �P�_add���S�����\
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
			InputStream requestBody) { // �s�W�@��Pair�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String pairID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("pairID") != null)
				pairID = requJSON.getString("pairID");

			// �p�GpairID���ŴN�Ǧ^�Ѽƿ��~
			if (pairID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��pair����ΨӨ��opairID�R���q��
			PairDao pairDao = new PairDao();
			// �P�_�R���q�榳�S�����~
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
			InputStream requestBody) { // �s�W�@��Pair�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer pairID = null, productPrice = null;
		String productName = null, preferentialType = null, pairAddress = null, userFeature = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
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

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
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

			// new�@��Pair����ǳƶǶi��Ʈw���q�椺�e
			PairDao pairDao = new PairDao();
			Pair pair = new Pair();
			pair.setPairID(pairID);
			pair.setProductName(productName);
			pair.setProductPrice(productPrice);
			pair.setPreferentialType(preferentialType);
			pair.setPairAddress(pairAddress);
			pair.setUserFeature(userFeature);

			// �P�_��s�q�榳�S�����\
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
			InputStream requestBody) { // ���o�Y���q��A�Ǧ^Response(�s��Ǧ^���A
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String pairID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("pairID") != null)
				pairID = requJSON.getString("pairID");

			// �p�GpairID���ŴN�Ǧ^�Ѽƿ��~
			if (pairID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			Pair pair = new PairDao().getPairById(Integer.parseInt(pairID));

			// �P�_pairID�s���s�b
			if (pair.getPairID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find Pair:" + pairID
						+ " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			} else {
				// respJSON�̩�JOrder
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
			InputStream requestBody) { // ���o�Ҧ��X�@�ӮaList�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		JSONObject respJSON = new JSONObject();
		JSONArray pairJSONArray = new JSONArray();

		try {

			// new�@��store�}�C�X�@�ӮaList
			List<Pair> pairs = new PairDao().getPairList();

			// �NList�}�C�নJSONArray
			for (Pair pair : pairs)
				pairJSONArray.put(pair.toString());

			// respJSON�̩�Jstores
			respJSON.put("Pairs", pairJSONArray);
			// �P�_order�}�C���S�����e
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
			InputStream requestBody) { // �s�W�@��Pair�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer pairID = null;
		Boolean paired = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("pairID") != null)
				pairID = Integer.parseInt(requJSON.getString("pairID"));

			if (requJSON.getString("paired") != null)
				paired = new Boolean(requJSON.getString("paired"));

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
			if (pairID == null || paired == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new�@��Pair����ǳƶǶi��Ʈw���q�椺�e
			PairDao pairDao = new PairDao();
			Pair pair = new Pair();
			pair.setPairID(pairID);
			pair.setPaired(paired);

			// �P�_��s�q�榳�S�����\
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
			InputStream requestBody) { // ���o�Ҧ��ϥΪ�Pair List�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		JSONObject respJSON = new JSONObject();
		JSONArray pairJSONArray = new JSONArray();
		String requestBODY = get(requestBody);
		String userID = null;

		try {

			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("userID") != null)
				userID = requJSON.getString("userID");

			// �p�GpairID���ŴN�Ǧ^�Ѽƿ��~
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��pair�}�C�ϥΪ�pairList
			List<Pair> pairs = new PairDao().getPairByUserID(Integer
					.parseInt(userID));

			// �NList�}�C�নJSONArray
			for (Pair pair : pairs)
				pairJSONArray.put(pair.toString());

			// respJSON�̩�Jstores
			respJSON.put("Pairs", pairJSONArray);
			// �P�_pair�}�C���S�����e
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
			InputStream requestBody) { // �s�W�@��pairTracing�A�Ǧ^Response(�s��Ǧ^���A)
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

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
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

			// �P�_��user�s���s�b
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

			// new�@��Pair����ǳƶǶi��Ʈw
			PairTracingDao pairTracingDao = new PairTracingDao();
			PairTracing pairTracing = new PairTracing();
			pairTracing.setPairID(pairID);
			pairTracing.setUserID(user.getUserID());
			pairTracing.setPairLongitude(pairLongitude);
			pairTracing.setPairLatitude(pairLatitude);
			pairTracing.setTracingUserID(tracingUser.getUserID());
			pairTracing.setTracingLongitude(tracingLongitude);
			pairTracing.setTracingLatitude(tracingLatitude);

			// �P�_add���S�����\
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
			InputStream requestBody) { // �s�W�@��PairTracing�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer pairID = null, userID = null;
		BigDecimal pairLongitude = null, pairLatitude = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
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

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
			if (pairID == null || userID == null || pairLongitude == null
					|| pairLatitude == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new�@��PairTracing����ǳƶǶi��Ʈw���q�椺�e
			PairTracingDao pairTracingDao = new PairTracingDao();
			PairTracing pairTracing = new PairTracing();
			pairTracing.setPairID(pairID);
			pairTracing.setUserID(userID);
			pairTracing.setPairLongitude(pairLongitude);
			pairTracing.setPairLatitude(pairLatitude);

			// �P�_��s�q�榳�S�����\
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
			@Context HttpHeaders header, InputStream requestBody) { // �s�W�@��PairTracing�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer pairID = null, tracingUserID = null;
		BigDecimal tracingLongitude = null, tracingLatitude = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
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

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
			if (pairID == null || tracingUserID == null
					|| tracingLongitude == null || tracingLatitude == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new�@��PairTracing����ǳƶǶi��Ʈw���q�椺�e
			PairTracingDao pairTracingDao = new PairTracingDao();
			PairTracing pairTracing = new PairTracing();
			pairTracing.setPairID(pairID);
			pairTracing.setTracingUserID(tracingUserID);
			pairTracing.setTracingLongitude(tracingLongitude);
			pairTracing.setTracingLatitude(tracingLatitude);

			// �P�_��s�q�榳�S�����\
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
			InputStream requestBody) { // ���o�Y���q��A�Ǧ^Response(�s��Ǧ^���A
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String pairID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("pairID") != null)
				pairID = requJSON.getString("pairID");

			// �p�GpairID���ŴN�Ǧ^�Ѽƿ��~
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

			// �P�_pairID�s���s�b
			if (pairTracing.getPairID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find Pair:" + pairID
						+ " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			} else {
				// respJSON�̩�JOrder
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
			InputStream requestBody) { // ���o�Ҧ��X�@�ӮaList�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray pairTracingJSONArray = new JSONArray();
		Integer userID = null;

		try {

			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			// �p�GpairID���ŴN�Ǧ^�Ѽƿ��~
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��store�}�C�X�@�ӮaList
			List<PairTracing> pairTracings = new PairTracingDao()
					.getPairID(userID);

			// �NList�}�C�নJSONArray
			for (PairTracing pairTracing : pairTracings)
				pairTracingJSONArray.put(pairTracing.getPairID());

			// respJSON�̩�Jstores
			respJSON.put("PairID", pairTracingJSONArray);
			// �P�_order�}�C���S�����e
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
			InputStream requestBody) { // �s�W�@��Puzzle�A�Ǧ^Response(�s��Ǧ^���A)
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

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
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

			// �P�_��user�s���s�b
			if (user.getUserID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find User ID");
				respJSON.put(RTNCODE_FIELD, "50");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��Puzzle����ǳƶǶi��Ʈw
			PuzzleDao puzzleDao = new PuzzleDao();
			Puzzle puzzle = new Puzzle();
			puzzle.setUserID(user.getUserID());
			puzzle.setPuzzleQuestionID(puzzleQuestionID);
			puzzle.setPuzzleGetAttractionName(puzzleGetAttractionName);
			puzzle.setCounty(county);
			puzzle.setLevel(level);

			// �P�_add���S�����\
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
			InputStream requestBody) { // ���ouser���q��A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray puzzleJSONArray = new JSONArray();
		String userID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("userID") != null)
				userID = requJSON.getString("userID");

			// �p�GuserID���ŴN�Ǧ^�Ѽƿ��~
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��puzzle�}�C�s��user�q��
			List<Puzzle> puzzles = new PuzzleDao().getPuzzleByUser(Integer
					.parseInt(userID));

			// �NList�}�C�নJSONArray
			for (Puzzle puzzle : puzzles)
				puzzleJSONArray.put(puzzle.toString());

			// respJSON�̩�Jpuzzles
			respJSON.put("Puzzles", puzzleJSONArray);
			// �P�_order�}�C���S�����e
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
			InputStream requestBody) { // ���ouser���q��A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray puzzleJSONArray = new JSONArray();
		String userID = null, county = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("userID") != null)
				userID = requJSON.getString("userID");

			if (requJSON.getString("county") != null)
				county = new String(requJSON.getString("county").getBytes(
						"ISO-8859-1"), "utf8");

			// �p�GuserID���ŴN�Ǧ^�Ѽƿ��~
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��puzzle�}�C�s��user�q��
			List<Puzzle> puzzles = new PuzzleDao().getPuzzleByUserAndCounty(
					Integer.parseInt(userID), county);

			// �NList�}�C�নJSONArray
			for (Puzzle puzzle : puzzles)
				puzzleJSONArray.put(puzzle.toString());

			// respJSON�̩�Jpuzzles
			respJSON.put("Puzzles", puzzleJSONArray);
			// �P�_order�}�C���S�����e
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
			@Context HttpHeaders header, InputStream requestBody) { // ���o�Y���q��A�Ǧ^Response(�s��Ǧ^���A
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String county = null, level = null;
		JSONArray puzzleQuestionJSONArray = new JSONArray();

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("county") != null)
				county = new String(requJSON.getString("county").getBytes(
						"ISO-8859-1"), "utf8");

			if (requJSON.getString("level") != null)
				level = requJSON.getString("level");

			// �p�GpuzzleQuestionID���ŴN�Ǧ^�Ѽƿ��~
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

			// �P�_order�}�C���S�����e
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
			InputStream requestBody) { // ���o�Ҧ��X�@�ӮaList�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		JSONObject respJSON = new JSONObject();
		JSONArray storeJSONArray = new JSONArray();

		try {

			// new�@��store�}�C�X�@�ӮaList
			List<Store> stores = new StoreDao().getStoreList();

			// �NList�}�C�নJSONArray
			for (Store store : stores)
				storeJSONArray.put(store.toString());

			// respJSON�̩�Jstores
			respJSON.put("Stores", storeJSONArray);
			// �P�_order�}�C���S�����e
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
			InputStream requestBody) { // �s�W�@��Travel�A�Ǧ^Response(�s��Ǧ^���A)
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

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
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

			// �P�_��user�s���s�b
			if (user.getUserID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find User ID");
				respJSON.put(RTNCODE_FIELD, "50");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��Travel����ǳƶǶi��Ʈw
			TravelDao travelDao = new TravelDao();
			Travel travel = new Travel();
			travel.setUserID(user.getUserID());
			travel.setTravelName(travelName);
			travel.setTravelDate(travelDate);
			travel.setTravelDays(travelDays);

			// �P�_add���S�����\
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
			InputStream requestBody) { // �R���@���q��A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String travelID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("travelID") != null)
				travelID = requJSON.getString("travelID");

			// �p�GtravelID���ŴN�Ǧ^�Ѽƿ��~
			if (travelID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��travel����ΨӨ��otravelID�R���q��
			TravelDao travelDao = new TravelDao();
			// �P�_�R��travel���S�����~
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
			InputStream requestBody) { // ���@��Travel�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer travelID = null, travelDays = null;
		String travelName = null;
		Timestamp travelDate = null;
		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
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

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
			if (travelID == null || travelName == null || travelDate == null
					|| travelDays == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new�@��travel����ǳƶǶi��Ʈw���q�椺�e
			TravelDao travelDao = new TravelDao();
			Travel travel = new Travel();
			travel.setTravelID(travelID);
			travel.setTravelName(travelName);
			travel.setTravelDate(travelDate);
			travel.setTravelDays(travelDays);

			// �P�_��stravel���S�����\
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
			InputStream requestBody) { // ���o�Y���q��A�Ǧ^Response(�s��Ǧ^���A
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String travelID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("travelID") != null)
				travelID = requJSON.getString("travelID");

			// �p�GtravelID���ŴN�Ǧ^�Ѽƿ��~
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

			// �P�_hotelOrderID�s���s�b
			if (travel.getTravelID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find Travel:" + travelID
						+ " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			} else {
				// respJSON�̩�JOrder
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
			InputStream requestBody) { // ���ouser���q��A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray travelJSONArray = new JSONArray();
		String userID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("userID") != null)
				userID = requJSON.getString("userID");

			// �p�GuserID���ŴN�Ǧ^�Ѽƿ��~
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��Travel�}�C�s��user�q��
			List<Travel> travels = new TravelDao().getTravelByUserID(Integer
					.parseInt(userID));

			// �NList�}�C�নJSONArray
			for (Travel travel : travels)
				travelJSONArray.put(travel.toString());

			// respJSON�̩�Jtravels
			respJSON.put("travels", travelJSONArray);
			// �P�_order�}�C���S�����e
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
			InputStream requestBody) { // �s�W�@��TravelAttraction�A�Ǧ^Response(�s��Ǧ^���A)
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

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
			if (travelID == null || attractionName == null || dayDate == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new�@��TravelAttraction����ǳƶǶi��Ʈw
			TravelAttractionDao travelAttractionDao = new TravelAttractionDao();
			TravelAttraction travelAttraction = new TravelAttraction();
			travelAttraction.setTravelID(travelID);
			travelAttraction.setAttractionName(attractionName);
			travelAttraction.setDayDate(dayDate);

			// �P�_add���S�����\
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
			InputStream requestBody) { // �R���@���q��A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String travelID = null, attractionName = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("travelID") != null)
				travelID = requJSON.getString("travelID");

			if (requJSON.getString("attractionName") != null)
				attractionName = requJSON.getString("attractionName");

			// �p�GtravelID�BattractionID���ŴN�Ǧ^�Ѽƿ��~
			if (travelID == null || attractionName == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��travelAttraction����ΨӨ��otravelID�BattractionID�R��travelAttraction
			TravelAttractionDao travelAttractionDao = new TravelAttractionDao();
			// �P�_�R��travelAttraction���S�����~
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
			InputStream requestBody) { // ���oTravelAttraction�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray travelAttractionJSONArray = new JSONArray();
		String travelID = null;
		Timestamp dayDate = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("travelID") != null)
				travelID = requJSON.getString("travelID");

			if (requJSON.getString("dayDate") != null)
				dayDate = new Timestamp(new SimpleDateFormat("yyyy-MM-dd")
						.parse(requJSON.getString("dayDate")).getTime());

			// �p�GuserID���ŴN�Ǧ^�Ѽƿ��~
			if (travelID == null || dayDate == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��TravelAttraction�}�C�s��user TravelAttraction
			List<TravelAttraction> travelAttractions = new TravelAttractionDao()
					.getTravelAttractionByIDAndDay(Integer.parseInt(travelID),
							dayDate);

			// �NList�}�C�নJSONArray
			for (TravelAttraction travelAttraction : travelAttractions)
				travelAttractionJSONArray.put(travelAttraction.toString());

			// respJSON�̩�Jtravels
			respJSON.put("travelAttractions", travelAttractionJSONArray);
			// �P�_order�}�C���S�����e
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
			InputStream requestBody) { // ���oTravelAttraction�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray travelAttractionJSONArray = new JSONArray();
		String travelID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("travelID") != null)
				travelID = requJSON.getString("travelID");

			// �p�GuserID���ŴN�Ǧ^�Ѽƿ��~
			if (travelID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��TravelAttraction�}�C�s��user TravelAttraction
			List<TravelAttraction> travelAttractions = new TravelAttractionDao()
					.getTravelAttractionByID(Integer.parseInt(travelID));

			// �NList�}�C�নJSONArray
			for (TravelAttraction travelAttraction : travelAttractions)
				travelAttractionJSONArray.put(travelAttraction.toString());

			// respJSON�̩�Jtravels
			respJSON.put("travelAttractions", travelAttractionJSONArray);
			// �P�_order�}�C���S�����e
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
			InputStream requestBody) { // �s�W�@��Pair�A�Ǧ^Response(�s��Ǧ^���A)
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

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
			if (userID == null || travelID == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			User user = new UserDao().getUserById(userID);

			// �P�_��user�s���s�b
			if (user.getUserID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find User ID");
				respJSON.put(RTNCODE_FIELD, "50");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��Pair����ǳƶǶi��Ʈw
			TravelPairDao travelPairDao = new TravelPairDao();
			TravelPair travelPair = new TravelPair();
			travelPair.setUserID(user.getUserID());
			travelPair.setTravelID(travelID);

			// �P�_add���S�����\
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
			InputStream requestBody) { // �s�W�@��Pair�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String travelPairID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("travelPairID") != null)
				travelPairID = requJSON.getString("travelPairID");

			// �p�GpairID���ŴN�Ǧ^�Ѽƿ��~
			if (travelPairID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��pair����ΨӨ��opairID�R���q��
			TravelPairDao travelPairDao = new TravelPairDao();
			// �P�_�R���q�榳�S�����~
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
			InputStream requestBody) { // �s�W�@��Pair�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer travelPairID = null;
		Boolean paired = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("travelPairID") != null)
				travelPairID = Integer.parseInt(requJSON
						.getString("travelPairID"));

			if (requJSON.getString("paired") != null)
				paired = new Boolean(requJSON.getString("paired"));

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
			if (travelPairID == null || paired == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new�@��Pair����ǳƶǶi��Ʈw���q�椺�e
			TravelPairDao travelPairDao = new TravelPairDao();
			TravelPair travelPair = new TravelPair();
			travelPair.setTravelPairID(travelPairID);
			travelPair.setPaired(paired);

			// �P�_��s�q�榳�S�����\
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
			InputStream requestBody) { // ���o�Y���q��A�Ǧ^Response(�s��Ǧ^���A
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String travelPairID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("travelPairID") != null)
				travelPairID = requJSON.getString("travelPairID");

			// �p�GpairID���ŴN�Ǧ^�Ѽƿ��~
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

			// �P�_pairID�s���s�b
			if (travelPair.getTravelPairID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find Pair:" + travelPairID
						+ " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			} else {
				// respJSON�̩�JOrder
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
			InputStream requestBody) { // ���o�Ҧ��X�@�ӮaList�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		JSONObject respJSON = new JSONObject();
		JSONArray travelPairJSONArray = new JSONArray();

		try {

			// new�@��store�}�C�X�@�ӮaList
			List<TravelPair> travelPairs = new TravelPairDao()
					.getTravelPairList();

			// �NList�}�C�নJSONArray
			for (TravelPair travelPair : travelPairs)
				travelPairJSONArray.put(travelPair.toString());

			// respJSON�̩�Jstores
			respJSON.put("TravelPairs", travelPairJSONArray);
			// �P�_order�}�C���S�����e
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
			InputStream requestBody) { // ���o�Ҧ��ϥΪ�Pair List�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		JSONObject respJSON = new JSONObject();
		JSONArray travelPairJSONArray = new JSONArray();
		String requestBODY = get(requestBody);
		String userID = null;

		try {

			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("userID") != null)
				userID = requJSON.getString("userID");

			// �p�GpairID���ŴN�Ǧ^�Ѽƿ��~
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��pair�}�C�ϥΪ�pairList
			List<TravelPair> travelPairs = new TravelPairDao()
					.getTravelPairByUserID(Integer.parseInt(userID));

			// �NList�}�C�নJSONArray
			for (TravelPair travelPair : travelPairs)
				travelPairJSONArray.put(travelPair.toString());

			// respJSON�̩�Jstores
			respJSON.put("TravelPairs", travelPairJSONArray);
			// �P�_pair�}�C���S�����e
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
			InputStream requestBody) { // �s�W�@��pairTracing�A�Ǧ^Response(�s��Ǧ^���A)
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

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
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

			// �P�_��user�s���s�b
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

			// new�@��Pair����ǳƶǶi��Ʈw
			TravelPairUserInfoDao travelPairUserInfoDao = new TravelPairUserInfoDao();
			TravelPairUserInfo travelPairUserInfo = new TravelPairUserInfo();
			travelPairUserInfo.setTravelPairID(travelPairID);
			travelPairUserInfo.setUserID(userID);
			travelPairUserInfo.setPairUserID(pairUserID);
			travelPairUserInfo.setPairUserName(pairUserName);
			travelPairUserInfo.setPairUserEMail(pairUserEMail);
			travelPairUserInfo.setPairUserLine(pairUserLine);
			travelPairUserInfo.setPairUserPhone(pairUserPhone);

			// �P�_add���S�����\
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
			InputStream requestBody) { // �s�W�@��PairTracing�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer travelPairID = null;
		String pairUserName = null, pairUserEMail = null, pairUserLine = null, pairUserPhone = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
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

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
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

			// new�@��PairTracing����ǳƶǶi��Ʈw���q�椺�e
			TravelPairUserInfoDao travelPairUserInfoDao = new TravelPairUserInfoDao();
			TravelPairUserInfo travelPairUserInfo = new TravelPairUserInfo();
			travelPairUserInfo.setTravelPairID(travelPairID);
			travelPairUserInfo.setPairUserName(pairUserName);
			travelPairUserInfo.setPairUserEMail(pairUserEMail);
			travelPairUserInfo.setPairUserLine(pairUserLine);
			travelPairUserInfo.setPairUserPhone(pairUserPhone);
			// �P�_��s�q�榳�S�����\
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
			InputStream requestBody) { // �s�W�@��Pair�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject(); // return value
		Integer travelPairID = null;
		Boolean userSure = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("travelPairID") != null)
				travelPairID = Integer.parseInt(requJSON
						.getString("travelPairID"));

			if (requJSON.getString("userSure") != null)
				userSure = new Boolean(requJSON.getString("userSure"));

			// �p�G�����@��Ƭ��ŴN�Ǧ^�Ѽƿ��~
			if (travelPairID == null || userSure == null) {

				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, "20").toString())
						.build();
				return response;
			}

			// new�@��Pair����ǳƶǶi��Ʈw���q�椺�e
			TravelPairUserInfoDao travelPairUserInfoDao = new TravelPairUserInfoDao();
			TravelPairUserInfo travelPairUserInfo = new TravelPairUserInfo();
			travelPairUserInfo.setTravelPairID(travelPairID);
			travelPairUserInfo.setUserSure(userSure);

			// �P�_��s�q�榳�S�����\
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
			InputStream requestBody) { // ���o�Y���q��A�Ǧ^Response(�s��Ǧ^���A
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		String travelPairID = null;

		try {
			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("travelPairID") != null)
				travelPairID = requJSON.getString("travelPairID");

			// �p�GpairID���ŴN�Ǧ^�Ѽƿ��~
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

			// �P�_pairID�s���s�b
			if (travelPairUserInfo.getTravelPairID() == 0) {
				respJSON.put(RTNMES_FIELD, "Can't find Pair:" + travelPairID
						+ " comments");
				response = Response.status(HTTP_INTERNAL_ERROR)
						.entity(respJSON.put(RTNCODE_FIELD, "40").toString())
						.build();
			} else {
				// respJSON�̩�JOrder
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
			InputStream requestBody) { // ���o�Ҧ��X�@�ӮaList�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray travelPairUserInfoJSONArray = new JSONArray();
		Integer userID = null;

		try {

			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			// �p�GpairID���ŴN�Ǧ^�Ѽƿ��~
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��store�}�C�X�@�ӮaList
			List<TravelPairUserInfo> travelPairUserInfos = new TravelPairUserInfoDao()
					.getTravelPairID(userID);

			// �NList�}�C�নJSONArray
			for (TravelPairUserInfo travelPairUserInfo : travelPairUserInfos) {
				JSONObject travelJSON = new JSONObject();
				travelJSON.accumulate("travelPairID",
						String.valueOf(travelPairUserInfo.getTravelPairID()));
				travelJSON.accumulate("userSure",
						String.valueOf(travelPairUserInfo.getUserSure()));
				travelPairUserInfoJSONArray.put(travelJSON);
			}
			;

			// respJSON�̩�Jstores
			respJSON.put("TravelPairs", travelPairUserInfoJSONArray);
			// �P�_order�}�C���S�����e
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
			InputStream requestBody) { // ���o�Ҧ��X�@�ӮaList�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray travelPairUserInfoJSONArray = new JSONArray();
		Integer userID = null;

		try {

			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			// �p�GpairID���ŴN�Ǧ^�Ѽƿ��~
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��store�}�C�X�@�ӮaList
			List<TravelPairUserInfo> travelPairUserInfos = new TravelPairUserInfoDao()
					.getTravelPairIDByPairUserID(userID);

			// �NList�}�C�নJSONArray
			for (TravelPairUserInfo travelPairUserInfo : travelPairUserInfos) {
				JSONObject travelJSON = new JSONObject();
				travelJSON.accumulate("travelPairID",
						String.valueOf(travelPairUserInfo.getTravelPairID()));
				travelJSON.accumulate("userSure",
						String.valueOf(travelPairUserInfo.getUserSure()));
				travelPairUserInfoJSONArray.put(travelJSON);
			}
			;

			// respJSON�̩�Jstores
			respJSON.put("TravelPairs", travelPairUserInfoJSONArray);
			// �P�_order�}�C���S�����e
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
			InputStream requestBody) { // ���o�Ҧ��X�@�ӮaList�A�Ǧ^Response(�s��Ǧ^���A)
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray travelPairUserInfoJSONArray = new JSONArray();
		Integer userID = null;

		try {

			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			// �p�GpairID���ŴN�Ǧ^�Ѽƿ��~
			if (userID == null) {
				respJSON.put(RTNMES_FIELD, "Parameter error");
				respJSON.put(RTNCODE_FIELD, "60");
				response = Response.status(HTTP_PARAMETER)
						.entity(respJSON.put(RTNCODE_FIELD, 0).toString())
						.build();
				return response;
			}

			// new�@��store�}�C�X�@�ӮaList
			List<TravelPairUserInfo> travelPairUserInfos = new TravelPairUserInfoDao()
					.getTravelPairIDByUserID(userID);

			// �NList�}�C�নJSONArray
			for (TravelPairUserInfo travelPairUserInfo : travelPairUserInfos) {
				JSONObject travelJSON = new JSONObject();
				travelJSON.accumulate("travelPairID",
						String.valueOf(travelPairUserInfo.getTravelPairID()));
				travelJSON.accumulate("userSure",
						String.valueOf(travelPairUserInfo.getUserSure()));
				travelPairUserInfoJSONArray.put(travelJSON);
			}
			;

			// respJSON�̩�Jstores
			respJSON.put("TravelPairs", travelPairUserInfoJSONArray);
			// �P�_order�}�C���S�����e
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
			InputStream requestBody) { // ���o�Ҧ�User���
		Response response = null;
		String requestBODY = get(requestBody);
		JSONObject respJSON = new JSONObject();
		JSONArray travelPairUserInfoJSONArray = new JSONArray();
		Integer userID = null;

		try {

			JSONObject requJSON = new JSONObject(requestBODY);

			// �qrequestBody���X�ݭn���
			if (requJSON.getString("userID") != null)
				userID = Integer.parseInt(requJSON.getString("userID"));

			// �p�GpairID���ŴN�Ǧ^�Ѽƿ��~
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
