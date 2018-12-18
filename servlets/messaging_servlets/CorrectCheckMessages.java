package messaging_servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import SQLManager.SQLManager;
import accountmanager.SessionManager;

public class CorrectCheckMessages extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6565091239280583646L;

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		SessionManager check = new SessionManager(req, res);
		if(check.validSession()){
			SQLManager manager = new SQLManager();
			manager.updateWithObjects("UPDATE checkMessages SET checkMT = 0 WHERE jSessionId = ?", req.getSession().getId());
		}
	}
}
