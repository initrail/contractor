package messaging_servlets;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import SQLManager.SQLManager;
import accountmanager.SessionManager;
import message_representations.LatestConversationData;

public class LoadConversations extends HttpServlet {
	private static final long serialVersionUID = -2700273837462314709L;
	private SQLManager manager;
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		SessionManager check = new SessionManager(req, resp);
		if(check.validSession()){
			manager = new SQLManager();
			String select = "SELECT convs.conversationId AS conversationId, Users.userId AS userId, CONCAT(CONCAT(firstName, ' '), lastName) AS senderName, count " +
					"FROM (SELECT userId, convIds.conversationId AS conversationId FROM " +
					"(SELECT conversationId FROM Recipients WHERE userId = ?)" +
					" AS convIds JOIN Recipients ON(Recipients.conversationId = convIds.conversationId) WHERE userId != ?)" +
					" AS convs JOIN Users ON(Users.userId = convs.userId) JOIN Conversations ON(convs.conversationId = Conversations.conversationId)";
			Object[] objects = new Object[2];
			objects[0] = check.getUserId();
			objects[1] = check.getUserId();
			//"SELECT conversationId, ogSender, senderName, ogReceiver, receiverName, count FROM conversations WHERE ogSender = ? OR ogReceiver = ?"
			LatestConversationData[] convs = manager.selectWithObjects(select, LatestConversationData[].class, objects);
			String message = String.valueOf(new Gson().toJson(convs, LatestConversationData[].class));
			DataOutputStream writer = new DataOutputStream(resp.getOutputStream());
			writer.writeBytes(message);
		}
	}
}
