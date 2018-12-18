package account_servlets;

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

public class UpdatePic extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3379963555800017482L;
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		SessionManager check = new SessionManager(req, resp);
		if(check.validSession()){
			String insert = "INSERT INTO updatedImage(email) SELECT CONCAT(jSessionId, ?) AS email FROM checkMessages WHERE userId IN(?,";
			SQLManager manager = new SQLManager();
			Long[] users = new Gson().fromJson(new BufferedReader(new InputStreamReader(req.getInputStream())).readLine(), Long[].class);		
			//String insert = "INSERT INTO updatedImage(email, deviceCount) SELECT ?, COUNT(email)-1 FROM checkMessages WHERE email IN(?,";
			if(users.length>0){
				for(int i = 0; i<users.length; i++){
					insert+="?";
					if(i<(users.length-1))
						insert+=",";
				}
				insert+=") AND jSessionId != ? ON DUPLICATE KEY UPDATE email = VALUES(email)";
				Object[] objects = new Object[users.length+3];
				objects[0] = check.getUserId();
				objects[1] = check.getUserId();
				for(int i = 0; i<users.length; i++){
					objects[i+2] = users[i];
				}
				objects[objects.length-1] = req.getSession().getId();
				manager.updateWithObjects(insert, objects);
			}
			String update = "UPDATE checkMessages SET updatePic = 1 WHERE jSessionId!=? AND userId IN(?,";		
			if(users!=null){
				if(users.length>0){
					int length = users.length;
					for(int i = 0; i<length; i++){
						update+="?";
						if(i<(length-1))
							update+=",";
					}
					update+=")";
					Object[] objects = new Object[users.length+2];
					objects[0] = req.getSession().getId();
					objects[1] = check.getUserId();
					for(int i = 0; i<users.length; i++){
						objects[i+2] = users[i];
					}
					manager.updateWithObjects(update, objects);
				}
			}
		}
	}
}
