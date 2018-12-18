package messaging_servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import SQLManager.SQLManager;
import accountmanager.SessionManager;
import message_representations.Message;

public class DeleteConversation extends HttpServlet {
	private static final long serialVersionUID = -8093447981802462216L;
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		SessionManager check = new SessionManager(req, res);
		if(check.validSession()){
			SQLManager manager = new SQLManager();
			String delete = "DELETE FROM Recipients WHERE userId = ? AND conversationId = ?";
			String delete2 = "DELETE FROM messageDetails WHERE userId = ? AND messageId LIKE CONCAT(?, '%')";
			BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
			Long convId = new Gson().fromJson(br.readLine(), Long.class);
			Object[] objects = new Object[2];
			objects[0] = check.getUserId();
			objects[1] = convId;
			manager.updateWithObjects(delete, objects);
			manager.updateWithObjects(delete2, objects);
			String select = "SELECT userId FROM Recipients WHERE conversationId = ?";
			Message[] userIds = manager.selectWithObjects(select, Message[].class, new Object[] {convId});
			if(userIds==null) {
				String deleteConv = "DELETE FROM Conversations WHERE conversationId = ?";
				manager.updateWithObjects(deleteConv, new Object[] {convId});
				String deleteMessages = "DELETE FROM Messages WHERE messageId LIKE CONCAT(?, '%')";
				manager.updateWithObjects(deleteMessages, new Object[] {convId});
			} else {
				if(userIds.length==0) {
					String deleteConv = "DELETE FROM Conversations WHERE conversationId = ?";
					manager.updateWithObjects(deleteConv, new Object[] {convId});
					String deleteMessages = "DELETE FROM Messages WHERE messageId LIKE CONCAT(?, '%')";
					manager.updateWithObjects(deleteMessages, new Object[] {convId});
				}
			}
		}
	}
}
