package messaging_servlets;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import accountmanager.SessionManager;
import constants.FileSystemConstants;

public class GetMMS extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6565330437291275562L;
	private static final String NO_IMAGE = "noUserImage";
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		SessionManager check = new SessionManager(req, resp);
		if(check.validSession()){
			String image = new BufferedReader(new InputStreamReader(req.getInputStream())).readLine();
			byte[] buffer;
			String fileName = image;
			String conversationId = null;
			Matcher m = Pattern.compile("^\\d+-").matcher(fileName);
			if(m.find()){
				conversationId = m.group().replaceAll("-", "");
			}
			System.out.println("fileName: "+fileName);
			String fileLocation = FileSystemConstants.MESSAGE_IMAGES_PATH+File.separator+conversationId+File.separator+fileName;
			System.out.println("fileLocation: "+fileLocation);
			DataOutputStream writer = new DataOutputStream(resp.getOutputStream()); 
			InputStream inputStream = null;
			int bytesAvailable = 0;
			try{
				BufferedImage original = ImageIO.read(new File(fileLocation));
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				System.out.println(check.getUser()+" is getting their own image");
				ImageIO.write(original, "jpg", os);
				inputStream = new ByteArrayInputStream(os.toByteArray());
				System.out.println("File found.");
	            bytesAvailable = inputStream.available();
				resp.setContentType("image/jpg");
				resp.setHeader("Content-Disposition", "attachment;filename="+fileName);
				resp.setContentLength(bytesAvailable);            
				System.out.println("File size: "+bytesAvailable);
	            int bufferSize = Math.min(bytesAvailable, 5*1024*1024);
	            buffer = new byte[bufferSize];
	            int bytesRead = inputStream.read(buffer, 0, bufferSize);
	            while(bytesRead>0){
	                writer.write(buffer, 0, bufferSize);
	                bytesAvailable = inputStream.available();
	                bufferSize = Math.min(bytesAvailable, 5*1024*1024);
	                bytesRead = inputStream.read(buffer, 0, bufferSize);
	            }
			} catch(FileNotFoundException e){
				e.printStackTrace();
				System.out.println("No file found.");
				resp.setContentType("image/jpg");
				resp.setHeader("Content-Disposition", "attachment;filename="+NO_IMAGE);
				resp.setContentLength(bytesAvailable);
			} finally {
				if(inputStream!=null){
					inputStream.close();
				}
			}
			writer.flush();
			writer.close();
		}
	}
}

