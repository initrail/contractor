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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import SQLManager.SQLManager;

import account_representations.Account;
import account_representations.ContractorAccountFinal;
import password.PasswordManager;
import validate.*;

public class CreateAccount extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3632499979014625125L;
	private SQLManager manager;
	ValidateInput input;
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		input = new ValidateInput();
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String account = br.readLine();
		Gson gson = new GsonBuilder().create();
		Account acc = null;
		if(account.contains("licenses")){
			acc = gson.fromJson(account, ContractorAccountFinal.class);
		} else {
			acc = gson.fromJson(account, Account.class);
		}
		String error = input.message(acc);
		DataOutputStream writer = new DataOutputStream(response.getOutputStream());
		writer.writeBytes(error);
		manager = new SQLManager();
		if(error.equals("")){
			PasswordManager hash = new PasswordManager();
			try{
				acc.setPassword(hash.strongPasswordHash(acc.getPassword()));
				if(acc instanceof ContractorAccountFinal){
					if(((ContractorAccountFinal)acc).isComplete())
						manager.insert("contractors", acc);
				} else
					manager.insert("users", acc);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			}
		}
	}
}


