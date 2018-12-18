package project_servlets;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import project_representations.Project;
import validate.ValidateInput;
import accountmanager.SessionManager;

import com.google.gson.GsonBuilder;

import SQLManager.ProjectSQLManager;

/**
 * @author Integrail
 *
 */
public class CreateProject extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8125110661908760419L;
	private ValidateInput input;
	private ProjectSQLManager manager;
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		SessionManager check = new SessionManager(req, resp);
		if(check.validSession()){
			manager = new ProjectSQLManager();
			String user = check.getUser();
			input = new ValidateInput();
			BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
			Project project = new GsonBuilder().create().fromJson(br.readLine(), Project.class);
			String error = input.message(project);
			DataOutputStream writer = new DataOutputStream(resp.getOutputStream());
			writer.writeBytes(error);
			if(error.equals("")&&(project.getFinished())){
				project.setCreator(user);
				manager.insert("projects", project);
			}
		}
	}
}

//CREATE TABLE projects (exteriorProjectFlag int, interiorProjectFlag int, fullName varchar(200), phone varchar(10), address varchar(200), aptNumber varchar(25), lockBox varchar(25), budget varchar(25), dateOfCompletion varchar(25), projectDescription Text, creator varchar(25), id varchar(25))
