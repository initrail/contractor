package messaging_servlets;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import SQLManager.SQLManager;
import accountmanager.SessionManager;
import message_representations.CheckMessages;

public class CheckMessagesTable extends HttpServlet{
	private static final long serialVersionUID = 3766300829959391579L;
	private static final String STOP_SERVLET = "killingServlet";
	private static final String CHECK_MESSAGES = "checkMessagesTable";
	private static final String SIGNED_OUT = "The user has signed out.";
	private static final String UPDATE_READ = "updateRead";
	private static final String UPDATE_PIC= "someoneUpdatedPic";
	private static final String TIME_OUT = "servletTimedOut";
	private static final long TWO_HOURS = 1000*60*60*2;
	private SQLManager manager;
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		long startTime = System.currentTimeMillis();
		long endTime = startTime+TWO_HOURS;
		SessionManager check = new SessionManager(req, resp);
		if(check.validSession()){
			String ret = "";
			manager = new SQLManager();
			CheckMessages cMT = manager.selectWithObjects("SELECT checkMT, setRead, servletCount, updatePic FROM checkMessages WHERE jSessionId = ?", CheckMessages.class, req.getSession().getId());
			if(cMT!=null) {
				/*if(cMT.getServletCount()<=0)
					manager.updateWithObjects("UPDATE checkMessages SET servletCount = 1 WHERE jSessionId = ?", req.getSession().getId());
				else if(cMT.getServletCount() >= 1){
					manager.updateWithObjects("UPDATE checkMessages SET servletCount = servletCount+1 WHERE jSessionId = ?", req.getSession().getId());
					try{
						Thread.sleep(2000);
					} catch (InterruptedException e){
						e.printStackTrace();
					}
				}*/
				while(true){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(cMT==null){
						ret= SIGNED_OUT;
						break;
					} else { 
						if(cMT.getServletCount() == 1){
							ret = STOP_SERVLET;
							break;
						} else {
							if(!cMT.getCheckMT()){
								if(cMT.isSetRead()){
									ret = UPDATE_READ;
									break;
								}
								if(cMT.isUpdatePic()){
									ret = UPDATE_PIC;
									break;
								}
								cMT = manager.selectWithObjects("SELECT checkMT, setRead, servletCount, updatePic FROM checkMessages WHERE jSessionId = ?", CheckMessages.class, req.getSession().getId());
							} else {
								ret = CHECK_MESSAGES;
								break;
							}
						}
					}
					if(System.currentTimeMillis()>=endTime){
						ret = TIME_OUT;
						break;
					}
				}
				System.out.println("At "+new SimpleDateFormat("MM/dd/yyyy HH:mm a").format(new Timestamp(System.currentTimeMillis()))+" "+check.getUser()+": "+ret);
				//manager.updateWithObjects("UPDATE checkMessages SET servletCount = servletCount - 1 WHERE jSessionId = ?", req.getSession().getId());
				DataOutputStream writer = new DataOutputStream(resp.getOutputStream());
				writer.writeBytes(ret);
				writer.flush();
				writer.close();
				System.out.println("Servlet closing!");
			}
		}
	}
}