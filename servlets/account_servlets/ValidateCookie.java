package account_servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import accountmanager.SessionManager;

public class ValidateCookie extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1823333220036339617L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		SessionManager cookie = new SessionManager(req, resp);
		cookie.validSession();
	}

}
