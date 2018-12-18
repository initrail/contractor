package validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.json.JSONObject;

import SQLManager.SQLManager;

import account_representations.Account;
import account_representations.ContractorAccountFinal;
import account_representations.SimpleCustomer;
import project_representations.Project;

public class ValidateInput {
	public String validateProjectName(String name){
		String result = "";
		if(!(name==null)){
			if(name.equals("")){
				result = "Required field.";
			}
		}
		return result;
	}
	public String validateFirstName(String firstName) {
		String message = "";
		if(!firstName.equals("")){
			Pattern pattern = Pattern.compile("\\s");
			Matcher matcher = pattern.matcher(firstName);
			if(matcher.find()){
				message = "Cannot contain any spaces.";
			}
		} else {
			message = "Required field.";
		}
		return message;
	}
	public String validateLastName(String lastName) {
		String message = "";
		if(!lastName.equals("")){
			Pattern pattern = Pattern.compile("\\s");
			Matcher matcher = pattern.matcher(lastName);
			if(matcher.find()){
				message = "Cannot contain any spaces.";
			}
		} else {
			message = "Required field.";
		}
		return message;
	}
	public String validateEmail(final String email) {
		String message = "";
		if(!email.equals("")){
			try{
				InternetAddress emailAddr = new InternetAddress(email);
				emailAddr.validate();
			} catch (AddressException e){
				e.printStackTrace();
				message = "Not a valid email address.";
			}
			SQLManager manager = new SQLManager();
			SimpleCustomer user = manager.selectWithObjects("SELECT email FROM Users WHERE email = ?", SimpleCustomer.class, email);
			if(user!=null) {
				if(!user.getEmail().equals(email)) {
					message = "";
				} else {
					message = "Email is already in use.";
				}
			}
		} else {
			message = "Required field.";
		}
		return message;
	}
	public String validateZip(String zip){
		String message = "";
		if(!zip.equals("")){
			String regex = "^[0-9]{5}(?:-[0-9]{4})?$";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(zip);
			if(!matcher.find()){
				message = "The zip code is invalid.";
			}
		} else {
			message = "Required field.";
		}
		return message;
	}
	public String passwordChecker(String pw1, String pw2){
		String message = "";
		if(!(pw2.equals("")&&pw1.equals(""))){
			if(pw1.equals(pw2)){
				Pattern pattern = Pattern.compile("\\s");
				Matcher matcher = pattern.matcher(pw1);
				if(matcher.find()){
					message = "The password cannot contain any spaces.";
				}
				String test2 = pw1.toLowerCase();
				if(!pw1.equals(test2)){
				} else {
					message+="The password must contain a capital letter as well as lower-case ones.";
				}
				if(pw1.matches(".*\\d.*")){
				} else {
					message+="The password must contain a number.";
				}
				if(pw1.length()>=8){
				} else {
					message+="The password must be atleast 8 characters long.";
				}
			} else {
				message+="The re-typed password does not match.";
			}
		} else {
			message="Required field.";
		}
		return message;
	}
	public String validatePhone(String phone){
		String message = "";
		if(!(phone==null)){
			if(phone.equals("")){
				message = "Required field.";
			}
		}
		return message;
	}
	public String message(Account cacc){
		JSONObject message = null;
		if(cacc instanceof ContractorAccountFinal){
			ContractorAccountFinal acc = (ContractorAccountFinal) cacc;
			String valf = validateFirstName(acc.getFirstName());
			String vall = validateFirstName(acc.getLastName());
			String valp = passwordChecker(acc.getPassword(), acc.getPasswordCheck());
			String valz = validateZip(acc.getZip());
			String vale = validateEmail(acc.getEmail());
			String valph = validatePhone(acc.getPhone());
			String valad = validateAddress(acc.getAddress());
			//String valap = validateAptNumber(acc.getAptNumber());
			String valsk = "";
			if(acc.isComplete())
				valsk = validateSkillset(acc.getInteriorSkills(), acc.getExteriorSkills());
			boolean e0, e1, e2, e3, e4, e5, e6, e7;
			e0 = !(valf.equals(""));
			e1 = !(vall.equals(""));
			e2 = !(valp.equals(""));
			e3 = !(valz.equals(""));
			e4 = !(vale.equals(""));
			e5 = !(valph.equals(""));
			e6 = !(valad.equals(""));
			e7 = !(valsk.equals(""));
			if(e0||e1||e2||e3||e4||e5||e6||e7){
				message = new JSONObject();
				InValid messages = new InValid(valf, vall, vale, valp, valz, valph, valad, valsk);
				try{
					message.put("inValidFirstName", messages.getInValidFirstName());
					message.put("inValidLastName", messages.getInValidLastName());
					message.put("inValidEmail", messages.getInValidEmail());
					message.put("inValidPassword", messages.getInValidPassword());
					message.put("inValidZip", messages.getInValidZip());
					message.put("inValidPhone", messages.getInValidPhone());
					message.put("inValidAddress", messages.getInValidAddress());
					if(acc.isComplete())
						message.put("inValidSkills", messages.getInValidSkills());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		} else {
			String valf = validateFirstName(cacc.getFirstName());
			String vall = validateLastName(cacc.getLastName());
			String valp = passwordChecker(cacc.getPassword(), cacc.getPasswordCheck());
			String valz = validateZip(cacc.getZip());
			String vale = validateEmail(cacc.getEmail());
			String valph = validatePhone(cacc.getPhone());
			boolean e0, e1, e2, e3, e4, e5;
			e0 = !(valf.equals(""));
			e1 = !(vall.equals(""));
			e2 = !(valp.equals(""));
			e3 = !(valz.equals(""));
			e4 = !(vale.equals(""));
			e5 = !(valph.equals(""));
			if(e0||e1||e2||e3||e4||e5){
				message = new JSONObject();
				InValid messages = new InValid(valf, vall, vale, valp, valz, valph);
				try{
					message.put("inValidFirstName", messages.getInValidFirstName());
					message.put("inValidLastName", messages.getInValidLastName());
					message.put("inValidEmail", messages.getInValidEmail());
					message.put("inValidPassword", messages.getInValidPassword());
					message.put("inValidZip", messages.getInValidZip());
					message.put("inValidPhone", messages.getInValidPhone());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
		String result = "";
		if(message!=null){
			result = String.valueOf(message);
		}
		return result;
	}
	/*private String validateAptNumber(String aptNumber) {
		String message = "";
		if(!aptNumber.equals("")){
			if (aptNumber.matches("[0-9]+")) {
			} else {
				message = "Must be a number.";
			}
		}
		return message;
	}*/
	private String validateAddress(String address) {
		String message = "";
		if(!(address==null)){
			if(!address.equals("")){
			} else {
				message = "Required field.";
			}
		}
		return message;
	}
	private String validateSkillset(int iS, int eS){
		String message = "";
		if ((iS+eS)==0){
			message = "Atleast one skill is required.";
		}
		return message;
	}
	private String validateBudget(String budget){
		String result = "";
		if(!(budget==null)){
			if(budget.equals("")){
				result = "Please choose a budget.";
			}
		}
		return result;
	}
	private String validateDate(String date){
		String result = "";
		if(!(date==null)){
			if(date.equals("")){
				result = "Please choose date of completion.";
			}
		}
		return result;
	}
	public String message(Project project) {
		JSONObject message = null;
		boolean e0, e1, e2, e3, e4, e5;
		String projectFlagCheck = validateProjectFlags(project.getInteriorProjectFlag(), project.getExteriorProjectFlag());
		String projectNameCheck = validateProjectName(project.getFullName());
		String projectPhoneCheck = validatePhone(project.getPhoneNumber());
		String projectAddress = validateAddress(project.getAddress());
		String projectBudget = validateBudget(project.getBudget());
		String projectDate = validateDate(project.getDateOfCompletion());
		e0 = !(projectFlagCheck.equals(""));
		e1 = !(projectNameCheck.equals(""));
		e2 = !(projectPhoneCheck.equals(""));
		e3 = !(projectAddress.equals(""));
		e4 = !(projectBudget.equals(""));
		e5 = !(projectDate.equals(""));
		if(e0||e1||e2||e3||e4||e5){
			message = new JSONObject();
			InValid messages = new InValid(projectFlagCheck,projectNameCheck, projectAddress, projectPhoneCheck, projectBudget, projectDate, 0);
			try{
				message.put("projectFlag", messages.getInValidProjectFlag());
				message.put("projectName", messages.getProjectName());
				message.put("projectAddress", messages.getProjectAddress());
				message.put("projectPhone", messages.getProjectPhone());
				message.put("projectBudget", messages.getProjectBudget());
				message.put("projectDate", messages.getProjectDate());
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		String result = "";
		if(message!=null){
			result = String.valueOf(message);
		}
		return result;
	}
	public String validateProjectFlags(int x, int y){
		if((x+y)==0){
			return "You must pick one project specifier.";
		}
		else
			return "";
	}
}
