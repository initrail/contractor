package account_servlets;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import SQLManager.SQLManager;
import accountmanager.SessionManager;
import message_representations.CheckMessages;

public class WhichUserUpdatedPic extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1619723476360806200L;
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		SessionManager check = new SessionManager(req, resp);
		if(check.validSession()){
			SQLManager manager = new SQLManager();
			String select = "SELECT email FROM updatedImage WHERE email LIKE CONCAT(?,'%')";
			Object[] objects = new Object[]{req.getSession().getId()};
 			CheckMessages[] ids = manager.selectWithObjects(select, CheckMessages[].class, objects);
			String[] userIds = new String[ids.length];
			for(int i = 0; i<userIds.length; i++){
				userIds[i] = ids[i].getEmail().replaceAll(req.getSession().getId(), "");
			}
			String delete = "DELETE FROM updatedImage WHERE email = ?";
			for(int i = 0; i < ids.length; i++){
				manager.updateWithObjects(delete, ids[i].getEmail());
			}				
			String update = "UPDATE checkMessages SET updatePic = 0 WHERE jSessionId = ?";
			manager.updateWithObjects(update, req.getSession().getId());
			DataOutputStream writer = new DataOutputStream(resp.getOutputStream());
			writer.writeBytes(new Gson().toJson(userIds, Long[].class));
			writer.flush();
			writer.close();
		}
	}
}