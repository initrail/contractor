package account_servlets;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import accountmanager.SessionManager;

import com.google.gson.GsonBuilder;

import SQLManager.SQLManager;
import account_representations.SimpleCustomer;

public class UpdateData extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6986586200283327879L;
	private SQLManager manager;
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		SessionManager check = new SessionManager(req, resp);
		if(check.validSession()){
			manager = new SQLManager();
			BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
			String update = br.readLine();
			SimpleCustomer info = new GsonBuilder().create().fromJson(update, SimpleCustomer.class);
			//manager.updateUserInfo(info);
			Object[] objects = new Object[6];
			objects[0] = info.getFname();
			objects[1] = info.getLname();
			objects[2] = info.getEmail();
			objects[3] = info.getPhone();
			objects[4] = info.getZip();
			objects[5] = check.getUserId();
			manager.updateWithObjects("UPDATE Users SET firstName = ?, lastName = ?, email=?, phone=?, zip=? WHERE userId = ?", objects);
			DataOutputStream writer = new DataOutputStream(resp.getOutputStream());
			writer.writeBytes("Success!");
		}
	}
}
