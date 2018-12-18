package messaging_servlets;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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

public class DetermineReadMessages extends HttpServlet {
	private static final long serialVersionUID = -5208995953093807046L;
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		SessionManager check = new SessionManager(req, resp);
		if(check.validSession()){
			DataOutputStream writer = new DataOutputStream(resp.getOutputStream());
			String messages = "doNothing";
			SQLManager manager = new SQLManager();
			String line = new BufferedReader(new InputStreamReader(req.getInputStream())).readLine();
			String[] arr = new Gson().fromJson(line, String[].class);
			if(arr!=null){
				if(arr.length > 0){
					String select = "SELECT messageId FROM messageDetails WHERE messageRead = 1 AND userId = ? AND messageId IN(";
					//String select = "SELECT messageId FROM messages WHERE messageRead = 1 AND messageId IN(";
					for(int i = 0; i<arr.length; i++){
						select+="?";
						if(i<arr.length-1)
							select+=",";
						
					}
					select+=")";
					/*select = select.replaceAll("\\?", "\\?,");
					select = select.replaceAll("\\?,\\)", "\\?\\)");*/
					Object[] objects = new Object[arr.length+1];
					objects[0] = check.getUserId();
					for(int i = 1; i<objects.length; i++){
						objects[i] = arr[i-1];
					}
					Message[] data = manager.selectWithObjects(select, Message[].class, objects);
					//Message[] data = manager.selectWithObjects(select, Message[].class, (Object[])arr);
					if(data!=null){
						String[] ids = new String[data.length];
						for(int i = 0; i<data.length; i++){
							ids[i] = data[i].getMessageId();
						}
						messages = String.valueOf(new Gson().toJson(ids, String[].class));
					}
				}
			}
			manager.updateWithObjects("UPDATE checkMessages SET setRead = 0 WHERE jSessionId = ?" , req.getSession().getId());
			writer.writeBytes(messages);
		}
	}
}
