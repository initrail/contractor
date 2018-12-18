package account_servlets;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import SQLManager.SQLManager;
import account_representations.SimpleCustomer;
import accountmanager.SessionManager;

public class GetUserInfo extends HttpServlet {
	private static final long serialVersionUID = -3315583850607891100L;
	private SQLManager manager;
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		if(session!=null){
			SessionManager cookie = new SessionManager(req, resp);
			if(cookie.validSession()){
				manager = new SQLManager();
				DataOutputStream writer = new DataOutputStream(resp.getOutputStream());
				writer.writeBytes(String.valueOf(new Gson().toJson(manager.selectWithObjects("SELECT userId, firstName, lastName, email, phone, zip FROM Users WHERE userId = ?", SimpleCustomer.class, cookie.getUserId()), SimpleCustomer.class)));
			}
		}
	}
}
