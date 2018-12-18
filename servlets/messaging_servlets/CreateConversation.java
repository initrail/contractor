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

public class CreateConversation extends HttpServlet {
	private static final long serialVersionUID = -8093447981802462216L;
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		SessionManager check = new SessionManager(req, res);
		if(check.validSession()){
			BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
			String data[] = new Gson().fromJson(br.readLine(), String[].class);
			SQLManager manager = new SQLManager();
			manager.returnGeneratedKeys(true);
			long id = manager.updateWithObjects("INSERT INTO Conversations (count, creator) VALUES (0, ?)", new Object[] {check.getUserId()});
			Object[] objects = new Object[data.length];
			String select = "SELECT userId AS messageFrom, 0 AS userId FROM Users WHERE email IN(";
			for(int i = 0; i<data.length; i++) {
				objects[i] = data[i];
				select+="?";
				if(i<(data.length - 1))
					select+=",";
				else
					select+=")";
			}
			Message[] users = manager.selectWithObjects(select, Message[].class, objects);
			for(int i = 0; i<users.length; i++)
				manager.updateWithObjects("INSERT INTO Recipients VALUES(?, ?)", new Object[] {users[i].getMessageFrom(), id});
			manager.updateWithObjects("INSERT INTO Recipients VALUES(?,?)", new Object[] {check.getUserId(), id});
			DataOutputStream writer = new DataOutputStream(res.getOutputStream());
			writer.writeBytes(String.valueOf(id));
			writer.flush();
			writer.close();
		}
	}
}
