package accountmanager;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import account_representations.LoginCredentials;

public class SessionManager {
	HttpServletRequest request;
	HttpServletResponse response;
	public SessionManager(HttpServletRequest request, HttpServletResponse response){
		this.request = request;
		this.response = response;
	}
	public void endSession(){
		request.getSession().invalidate();
		Cookie[] erase = request.getCookies();
		erase[0].setValue(null);
		response.addCookie(erase[0]);
	}
	public String getUser(){
		HttpSession session = request.getSession();
		return (String)session.getAttribute("user");
	}
	public long getUserId() {
		HttpSession session = request.getSession();
		return (Long) session.getAttribute("userId");
	}
	public boolean validSession(){
  		boolean valid = false;
        HttpSession session = request.getSession(false);
        if(session!=null){
        	long user = (Long)session.getAttribute("userId");
    		Cookie[] sess = request.getCookies();
        	if(user<=0){
        		session.invalidate();
        		if(sess!=null){
        			sess[0].setValue(null);
        			response.addCookie(sess[0]);
        		}
        		valid = false;
        	} else {
        		valid = true;
        		sess[0].setValue(session.getId());
            	response.addCookie(sess[0]);
        	}
        }
        return valid;
	}
	public void generateSession(LoginCredentials login){
        HttpSession session = request.getSession();
        session.setAttribute("user", login.getEmail());
        session.setAttribute("userId", login.getUserId());
        session.setMaxInactiveInterval(1000*60*60*24*7);
	}
}
