package account_servlets;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import validate.InValid;
import accountmanager.SessionManager;
import password.PasswordManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import SQLManager.SQLManager;
import account_representations.LoginCredentials;

public class SignIn extends HttpServlet {
	private static final long serialVersionUID = -2372751297127885439L;
	private SQLManager manager;
	InValid badLogin = null;
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SessionManager cval = new SessionManager(request, response);
		HttpSession session = request.getSession(false);
		if(session==null){
			manager = new SQLManager();
			LoginCredentials signIn = new GsonBuilder().create().fromJson(new BufferedReader(new InputStreamReader(request.getInputStream())).readLine(), LoginCredentials.class);
			String select = "SELECT userId, email, password FROM Users WHERE email = ?";
			LoginCredentials dbSign = manager.selectWithObjects(select, LoginCredentials.class, signIn.getEmail());
			String message = "";
			boolean success = false;
			if(!(signIn.getEmail().equals("")&&signIn.getPassword().equals(""))){
				if(!signIn.getEmail().equals("")){
					LoginCredentials cred = manager.selectWithObjects("SELECT userId, email, password FROM Users WHERE email = ?", LoginCredentials.class, signIn.getEmail());
					if(cred!=null){
						try{
							if(cred.getEmail().equals(signIn.getEmail())){
								if(!signIn.getPassword().equals("")){
									PasswordManager pass = new PasswordManager();
									success = pass.validatePassword(signIn.getPassword(), cred.getPassword());
									System.out.println("Login was successful: " + success);
									if(success==false){
										message = "Either the password or email address are incorrect.";
									}
								}
							}else {
								message = "Either the password or email address are incorrect.";
							}
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
						} catch (InvalidKeySpecException e) {
							e.printStackTrace();
						}
					}else {
						message = "Either the password or email address are incorrect.";
					}
				}
			}
			String error = "";
			String badEmail = "";
			String badPassword = "";
			if(success){
				System.out.println("Hello "+signIn.getEmail());
				cval.generateSession(dbSign);
			} else {
				if(message.equals("")){
					if(signIn.getEmail().equals("")){
						badEmail = "Required field.";
					}
					if(signIn.getPassword().equals("")){
						badPassword = "Required field.";
					}
					error = String.valueOf(new Gson().toJson(new InValid(badEmail, badPassword), InValid.class));
				} else {
					if(signIn.getPassword().equals("")){
						badEmail = "";
						badPassword = "Required field";
					} else {
						badEmail = message;
					}
					error = String.valueOf(new Gson().toJson(new InValid(badEmail, badPassword), InValid.class));
				}
			}
			DataOutputStream writer = new DataOutputStream(response.getOutputStream());
			if(error.equals(""))
				writer.writeBytes(String.valueOf(cval.getUserId()));
			else
				writer.writeBytes(error);
			if(error.equals("")){
				Object[] objects = new Object[6];
				objects[0] = request.getSession().getId();
				objects[1] = cval.getUserId();
				objects[2] = false;
				objects[3] = false;
				objects[4] = false;
				objects[5] = 0;
				//CheckMessages check = new CheckMessages(request.getSession().getId(), cval.getUserId(), false, false, 0);
				manager.updateWithObjects("INSERT INTO CheckMessages VALUES(?, ?, ?, ?, ?, ?)", objects);
			}
		} else {
			cval.validSession();
		}
	}
}
