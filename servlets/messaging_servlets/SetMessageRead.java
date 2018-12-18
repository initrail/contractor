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

public class SetMessageRead extends HttpServlet{
	private static final long serialVersionUID = -3703587445690391485L;
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
		SessionManager check = new SessionManager(req, resp);
		if(check.validSession()){
			SQLManager manager = new SQLManager();
			String messages = new BufferedReader(new InputStreamReader(req.getInputStream())).readLine();
			String[] msgs = new Gson().fromJson(messages, String[].class);
			for(int i = 0; i<msgs.length; i++){
				manager.updateWithObjects("UPDATE messageDetails SET messageRead = 1 WHERE messageId = ? AND userId = ?", new Object[]{ msgs[i], check.getUserId() });
				//manager.updateWithObjects("UPDATE messages SET messageRead = 1 WHERE messageId = ?", msgs[i]);
			}			
			String update = "UPDATE checkMessages SET setRead = 1 WHERE jSessionId != ? AND userId = ?";
			manager.updateWithObjects(update , new Object[]{req.getSession().getId(), check.getUserId()});
		}
	}
}
