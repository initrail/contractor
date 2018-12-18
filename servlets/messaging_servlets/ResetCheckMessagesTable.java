package messaging_servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import SQLManager.SQLManager;
import accountmanager.SessionManager;
public class ResetCheckMessagesTable extends HttpServlet {
	private static final long serialVersionUID = -8183626604058769064L;
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException{
		SessionManager check = new SessionManager(req, res);
		if(check.validSession()) {
			System.out.println(check.getUser()+" is Resetting checkMessages");
			SQLManager manager = new SQLManager();
			//UPDATE checkMessages SET checkMT = 0 WHERE jSessionId =
			manager.updateWithObjects("UPDATE checkMessages SET servletCount = 1 WHERE jSessionId = ?", new Object[]{req.getSession().getId()});
			try {
				Thread.sleep(2000);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
			manager.updateWithObjects("UPDATE checkMessages SET servletCount = 0 WHERE jSessionId = ?", req.getSession().getId());
		}
	}
}
