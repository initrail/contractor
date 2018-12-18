package project_servlets;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import project_representations.ProjectFlags;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import SQLManager.SQLManager;
import account_representations.ContractorObjectSimple;
import accountmanager.SessionManager;

public class GetContractorList extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6166952120855465700L;
	private SQLManager manager;
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		SessionManager check = new SessionManager(req, resp);
		if(check.validSession()){
			manager = new SQLManager();
			BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
			String prj = br.readLine();
			ProjectFlags project = new GsonBuilder().create().fromJson(prj, ProjectFlags.class);
			//String json = String.valueOf(new Gson().toJson(manager.matchContractorsToProjects(project.getInteriorFlag(), project.getExteriorFlag()), ContractorObjectSimple[].class));
			String json = String.valueOf(new Gson().toJson(manager.select("SELECT firstName, lastName, email FROM contractors WHERE interiorSkills & ? OR exteriorSkills & ?", ContractorObjectSimple[].class, project), ContractorObjectSimple[].class));
			DataOutputStream writer = new DataOutputStream(resp.getOutputStream());
			writer.writeBytes(json);
		}
	}
}
