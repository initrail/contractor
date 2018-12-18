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

import SQLManager.SQLManager;
import accountmanager.SessionManager;
import message_representations.LatestConversationData;
import message_representations.Message;
public class LoadMessages extends HttpServlet {
	private static final long serialVersionUID = -6663608707063395167L;
	private SQLManager manager;
	public static final int LIMITER = 10;
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		SessionManager check = new SessionManager(req, resp);
		if(check.validSession()){
			manager = new SQLManager();
			String conv = new BufferedReader(new InputStreamReader(req.getInputStream())).readLine();
			LatestConversationData[] data = new Gson().fromJson(conv, LatestConversationData[].class);
			if(data!=null){
				if(data.length > 0){
					ArrayList<Object> objects = new ArrayList<Object>();
					String select = "SELECT m1.messageId AS messageId, m1.id AS id, m1.messageFrom AS messageFrom, m1.message AS message, m2.messageRead AS messageRead, m1.timeSent AS timeSent, m2.timeReceived AS timeReceived, m1.mms AS mms, m1.jSessionId AS jSessionId, 0 AS userId FROM messages m1 JOIN messageDetails m2 ON(m1.messageId = m2.messageId) WHERE m1.messageId IN(";
					//String select = "SELECT * FROM messages WHERE messageId IN(";
					for(int i = 0; i<data.length; i++){
						long j = 0;
						if(data[i].getCount()>10)
							j = data[i].getCount() - LIMITER;
						while(j<data[i].getCount()){
							String convId = data[i].getConversationId()+"-"+(j + 1);
							objects.add(convId);
							select+="?";
							if(j<data[i].getCount()-1)
								select+=", ";
							j++;
						}
						if(i<data.length-1)
							select+=", ";
					}
					select+=")";
					objects.add(check.getUserId());
					select+=" AND userId = ?";
					System.out.println("From LoadMessages.class "+select);
					Message[] messages = manager.selectWithObjects(select, Message[].class, objects.toArray(new Object[objects.size()]));
					if(data.length == 1 && messages.length == 10) {
						for(int i = 1; i < messages.length; i++) {
							Message key = messages[i];
							int j = i - 1;
							while(j>=0 && messages[j].getId()>key.getId()) {
								messages[j+1]=messages[j];
								j=j-1;
							}
							messages[j+1] = key;
						}
					}
					String update = "UPDATE messageDetails SET timeReceived = ? WHERE messageId = ? AND userId = ?";
					//String update = "UPDATE messages SET timeReceived = ? WHERE messageId = ?";
					for(int i = 0; i<messages.length; i++){
						if(messages[i].getTimeReceived()==0 && (messages[i].getMessageFrom() != check.getUserId())){
							Object[] objs = new Object[3];
							//Object[] objs = new Object[2];
							long stamp2 = System.currentTimeMillis();
							objs[0] = stamp2;
							objs[1] = messages[i].getMessageId();
							objs[2] = check.getUserId();
							manager.updateWithObjects(update, objs);
							messages[i].setTimeReceived(stamp2);
						}
					}
					DataOutputStream writer = new DataOutputStream(resp.getOutputStream());
					String message = String.valueOf(new Gson().toJson(messages, Message[].class));
					writer.writeBytes(message);
				}
			}
		}
	}
}
