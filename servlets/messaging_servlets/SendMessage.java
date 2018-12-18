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
import message_representations.CheckMessages;
import message_representations.LatestConversationData;
import message_representations.Message;

public class SendMessage extends HttpServlet {
	private static final long serialVersionUID = -1254052221242468688L;
	private SQLManager manager;
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		SessionManager check = new SessionManager(request, response);
		if(check.validSession()){
			manager = new SQLManager();
			BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			LatestConversationData data[] = new Gson().fromJson(br.readLine(), LatestConversationData[].class);
			ArrayList<Object> objects = new ArrayList<Object>();
			String select = "SELECT m1.messageId AS messageId, m1.id AS id, m1.messageFrom AS messageFrom, m1.message AS message, m2.messageRead AS messageRead, m1.timeSent AS timeSent, m2.timeReceived AS timeReceived, m1.mms AS mms, m1.jSessionId AS jSessionId, 0 AS userId FROM messages m1 JOIN messageDetails m2 ON(m1.messageId = m2.messageId) WHERE m1.messageId IN(";
			//String select = "SELECT * FROM messages WHERE messageId IN(";
			if(data==null){
				System.out.println("SendMessage servlet\nThis user's device sent a null data array.\nemail: "+check.getUser()+"\nJSESSIONID: "+request.getSession().getId());
			}
			for(int i = 0; i<data.length; i++){
				for(long j = data[i].getOldCount(); j<data[i].getCount(); j++){
					String convId = data[i].getConversationId()+"-"+(j+1);
					objects.add(convId);
					select+="?";
				}
			}
			select+=")";
			select = select.replaceAll("\\?", "\\?,");
			select = select.replaceAll("\\?,\\)", "\\?\\)");
			objects.add(check.getUserId());
			select+=" AND userId = ?";
			System.out.println("From SendMessage.class "+select);
			Message[] messages = manager.selectWithObjects(select, Message[].class, objects.toArray(new Object[objects.size()]));
			String update = "UPDATE messageDetails SET timeReceived = ? WHERE messageId = ? AND userId = ?";
			//String update = "UPDATE messages SET timeReceived = ? WHERE messageId = ?";
			for(int i = 0; i<messages.length; i++){
				if(messages[i].getTimeReceived()==0 && messages[i].getMessageFrom() != check.getUserId()){
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
			DataOutputStream writer = new DataOutputStream(response.getOutputStream());
			String message = String.valueOf(new Gson().toJson(messages, Message[].class));
			CheckMessages cMT = new CheckMessages(request.getSession().getId(), check.getUserId(), false, false, 0);
			manager.update("UPDATE checkMessages SET checkMT = 0 WHERE jSessionId = ?", cMT);
			writer.writeBytes(message);
		}
	}
}