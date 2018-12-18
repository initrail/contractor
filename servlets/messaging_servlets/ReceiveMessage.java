package messaging_servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

import accountmanager.SessionManager;
import message_representations.Message;
import SQLManager.SQLManager;

public class ReceiveMessage extends HttpServlet{
	private static final long serialVersionUID = 145735957006261032L;
	private SQLManager manager;
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		String messageId = "";
		long timeSent = 0;
		String selectOthers = "SELECT userId FROM Recipients where conversationId = ?";
		String insertC = "INSERT INTO conversations(conversationId, count) VALUES (?, 1) ON DUPLICATE KEY UPDATE count = count + 1";
		//String insertM = "INSERT INTO messages(messageId, id, messageFrom, message, messageRead, timeSent, mms, jSessionId) SELECT CONCAT(conversationId, '-', count), count, ?, ?, ?, ?, ?, ? FROM conversations WHERE conversationId = ?";
		String insertM = "INSERT INTO messages(messageId, id, messageFrom, message, timeSent, mms, jSessionId) VALUES (?, ?, ?, ?, ?, ?, ?)";
		String selectConvId = "SELECT CONCAT(conversationId, '-', count) AS messageId, count AS id from conversations WHERE conversationId = ?";
		String insertMD = "INSERT INTO messageDetails(userId, messageId) VALUES (?, ?)";
		String insertMDR = "INSERT INTO messageDetails(userId, messageId, timeReceived, messageRead) VALUES (?, ?, ?, ?)";
		//Have messageRead's default be 0 as well as timeReceived.
		SessionManager check = new SessionManager(request, response);
		if(check.validSession()){
			boolean tryAgain = true;
			System.out.println("ReceiveMessage servlet\nThe user "+check.getUser()+" sent a message.");
			manager = new SQLManager();
			String m = new BufferedReader(new InputStreamReader(request.getInputStream())).readLine();
			Message message = new Gson().fromJson(m, Message.class);
			String[] ids = message.getMessageId().split("-");
			long conversationId = Long.parseLong(ids[0]);
			Message[] messages = manager.selectWithObjects(selectOthers, Message[].class, new Object[]{conversationId});
			manager.updateWithObjects(insertC, message.getMessageId());
			while(tryAgain){
				//manager.updateWithObjects(insertC, message.getMessageId());
		  		Message convId = manager.selectWithObjects(selectConvId, Message.class, new Object[]{conversationId});
		  		messageId = convId.getMessageId();
		  		Object[] object = new Object[7];
		  		object[0] = convId.getMessageId();
		  		//object[0] = check.getUserId();
		  		object[1] = convId.getId();
		  		object[2] = check.getUserId();
		  		//object[1] = message.getMessage();
		  		//object[2] = message.getMessageRead();
		  		object[3] = message.getMessage();
		  		//object[3] = (Long) System.currentTimeMillis();
		  		timeSent = (Long) System.currentTimeMillis();
		  		object[4] = timeSent;
		  		//object[4] = message.isMms();
		  		object[5] = message.isMms();
		  		//object[5] = request.getSession().getId();
				object[6] = request.getSession().getId();
				//object[6] = message.getMessageId();
				try{
					manager.updateWithObjectsorThrow(insertM, object);
					tryAgain = false;
				} catch(MySQLIntegrityConstraintViolationException e){
					e.printStackTrace();
				}
			}
			Object[] objects = new Object[messages.length];
			String update = "UPDATE checkMessages SET checkMT = 1 WHERE userId IN(";
			for(int i = 0; i<messages.length; i++) {
				update+="?";
				if(i < (messages.length - 1))
					update+=",";
				else if(i == (messages.length - 1))
					update+=")";
			}
			for(int i = 0; i<messages.length; i++)
				objects[i] = messages[i].getUserId();
			for(int i = 0; i<objects.length; i++) {
				if((Long)objects[i] != check.getUserId())
					manager.updateWithObjects(insertMD, new Object[]{ objects[i], messageId });
				else
					manager.updateWithObjects(insertMDR, new Object[] {objects[i], messageId, timeSent, 1 });
			}
			manager.updateWithObjects(update , objects);
		}
	}
}
/*

		Object[] objects = new Object[messages.length];
		String update = "UPDATE checkMessages SET checkMT = 1 WHERE userId IN(";
		for(int i = 0; i<messages.length; i++) {
			update+="?";
			if(i < (messages.length - 1))
				update+=",";
			else if(i == (messages.length - 1))
				update+=")";
		}
		for(int i = 0; i<messages.length; i++)
			objects[i] = messages[i].getUserId();
		manager.updateWithObjects(update , objects);
*/