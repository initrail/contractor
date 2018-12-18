package project_servlets;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import SQLManager.SQLManager;
import accountmanager.SessionManager;
import project_representations.Project;

public class GetExistingProjects extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8378374804387137812L;
	private SQLManager dbManager;
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		SessionManager manager = new SessionManager(req, resp);
		if(manager.validSession()){
			dbManager = new SQLManager();
			Project search = new Project();
			search.setCreator(manager.getUser());
			Project[] projects = dbManager.select("SELECT * FROM projects WHERE creator = ?", Project[].class, search);
			String json = String.valueOf(new Gson().toJson(projects, Project[].class));
			DataOutputStream writer = new DataOutputStream(resp.getOutputStream());
			writer.writeBytes(json);
		}
	}
}
