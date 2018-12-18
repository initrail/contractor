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

import SQLManager.SQLManager;

/**
 * @author Integrail
 *
 */
public class DeleteProject extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1110750945516989886L;
	private ValidateInput input;
	private SQLManager manager;
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		SessionManager check = new SessionManager(req, resp);
		if(check.validSession()){
			manager = new SQLManager();
			input = new ValidateInput();
			BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
			Project project = new GsonBuilder().create().fromJson(br.readLine(), Project.class);
			String error = input.message(project);
			DataOutputStream writer = new DataOutputStream(resp.getOutputStream());
			writer.writeBytes(error);
			manager.update("DELETE FROM projects WHERE creator = ? and id = ?", project);
		}
	}
}
