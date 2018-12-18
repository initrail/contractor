package account_servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import SQLManager.SQLManager;
import accountmanager.SessionManager;

public class SignOut extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2047243151134433505L;
	private SQLManager manager;
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		SessionManager session = new SessionManager(req, resp);
		if(session.validSession()){
			manager = new SQLManager();
			String user = session.getUser();
			manager.updateWithObjects("DELETE FROM checkMessages WHERE jSessionId = ? AND userId = ?", new Object[]{req.getSession().getId(), session.getUserId()});
			session.endSession();
			System.out.println("Goodbye "+user);
		}
	}
}

