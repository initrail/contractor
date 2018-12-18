package messaging_servlets;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import SQLManager.SQLManager;
import accountmanager.SessionManager;
import message_representations.LatestConversationData;

public class GetNewConversations extends HttpServlet{
	private static final long serialVersionUID = -2983101390451619190L;
	private SQLManager manager;
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		SessionManager check = new SessionManager(req, resp);
		if(check.validSession()){
			manager = new SQLManager();
			String c = new BufferedReader(new InputStreamReader(req.getInputStream())).readLine();
			LatestConversationData[] data = new GsonBuilder().create().fromJson(c, LatestConversationData[].class);
			String select = "";
			ArrayList<Object> objs = new ArrayList<Object>();
			//objs.add(check.getUserId());
			if(data.length==0)
				System.out.println("GetNewConversations servlet\nThis user's device sent an empty data array.\nemail: "+check.getUser()+"\nJSESSIONID: "+req.getSession().getId());
			if(data.length>0){
				objs.add(check.getUserId());
				objs.add(check.getUserId());
				select = "SELECT convs.conversationId AS conversationId, Users.userId AS userId, CONCAT(CONCAT(firstName, ' '), lastName) AS senderName, count, 0 AS oldCount FROM (SELECT userId, convIds.conversationId AS conversationId FROM (SELECT conversationId FROM Recipients WHERE userId = ?) AS convIds JOIN Recipients ON(Recipients.conversationId = convIds.conversationId) WHERE userId != ?) AS convs JOIN Users ON(Users.userId = convs.userId) JOIN Conversations ON (convs.conversationId = Conversations.conversationId) WHERE convs.conversationId NOT IN(";
				//select = "SELECT conversationId, ogSender, senderName, ogReceiver, receiverName, count, 0 AS oldCount FROM conversations WHERE ogReceiver = ? AND conversationId NOT IN(";
				for(int i = 0; i<data.length; i++){
					select+="?";
					objs.add(data[i].getConversationId());
					if(i<data.length-1)
						select+=", ";
				}
				select+=")";
				for(int i = 0; i<data.length; i++){
					//select+=" UNION SELECT conversationId, ogSender, senderName, ogReceiver, receiverName, count, ? AS oldCount FROM conversations WHERE conversationId = ? AND count > ?";
					select+=" UNION SELECT convs.conversationId AS conversationId, Users.userId AS userId, CONCAT(CONCAT(firstName, ' '), lastName) AS senderName, count, ? AS oldCount FROM (SELECT userId, convIds.conversationId AS conversationId FROM (SELECT conversationId FROM Recipients WHERE userId = ?) AS convIds JOIN Recipients ON (Recipients.conversationId = convIds.conversationId) WHERE userId != ?) AS convs JOIN Users ON (Users.userId = convs.userId) JOIN Conversations ON (convs.conversationId = Conversations.conversationId) WHERE convs.conversationId = ? AND count > ?";
					objs.add(data[i].getCount());
					objs.add(check.getUserId());
					objs.add(check.getUserId());
					objs.add(data[i].getConversationId());
					objs.add(data[i].getCount());
				}
			} else {
				objs.add(check.getUserId());
				objs.add(check.getUserId());
				//select = "SELECT conversationId, ogSender, senderName, ogReceiver, receiverName, 0 AS count FROM conversations WHERE ogReceiver = ?";
				select = "SELECT convs.conversationId AS conversationId, Users.userId AS userId, CONCAT(CONCAT(firstName, ' '), lastName) AS senderName, count " +
						"FROM (SELECT userId, convIds.conversationId AS conversationId FROM " +
						"(SELECT conversationId FROM Recipients WHERE userId = ?)" +
						" AS convIds JOIN Recipients ON(Recipients.conversationId = convIds.conversationId) WHERE userId != ?)" +
						" AS convs JOIN Users ON(Users.userId = convs.userId) JOIN Conversations ON(convs.conversationId = Conversations.conversationId)";
			}
			System.out.println(select.length());
			data = manager.selectWithObjects(select, LatestConversationData[].class, objs.toArray(new Object[objs.size()]));
			String finalMsg = String.valueOf(new Gson().toJson(data, LatestConversationData[].class));
			DataOutputStream writer = new DataOutputStream(resp.getOutputStream());
			writer.writeBytes(finalMsg);	
		}
	}
}