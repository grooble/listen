package com.grooble.web;

import java.io.*;

import javax.imageio.*;

import java.awt.image.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.*;

import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.io.FilenameUtils;

import com.grooble.model.*;

@SuppressWarnings("serial")
public class UploadHandler extends HttpServlet {

//	データソースを初期化する
	private DataSource datasource;
	public void init() throws ServletException {
		try {
			datasource = (DataSource) getServletContext().getAttribute("DBCPool");
		} catch(Exception e) {
			throw new ServletException(e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request,
			HttpServletResponse response)
			throws IOException, ServletException{
		
//		System.out.println("UploadHandler");
		response.setContentType("text/html");
		HttpSession session = request.getSession();
		Person user = (Person) session.getAttribute("user");
		System.out.println("UploadHandler->user: " +
				"id--" + user.getId() +
				" name--" + user.getFirstName() +
				" email--" + user.getEmail());
		String email = "";
		String dispatchString = "ShowProfile.do"; 
		if(user!=null){
			email = user.getEmail().toLowerCase();
		}
		String fileName = null;
		String extension = null;
		File uploadedImgFile = null;
		File uploadedThumbFile = null;
		boolean fileFound = false;
		
		String realPath = getServletContext().getRealPath("/");
		String imgPath = realPath + "profile" + File.separator + 
			user.getId() + File.separator + "images" + File.separator;
		File imgPathDir = new File(imgPath);
		if (!imgPathDir.isDirectory()){
			System.out.println("UploadHandler->made img dir");
			imgPathDir.mkdir();
		}
		
		System.out.println("UploadHandler-->imgPath: " + imgPath);
		String thumbPath = realPath + "profile" + File.separator + 
			user.getId() + File.separator +"images" +
			File.separator + "thumb" + File.separator;
		File thumbPathDir = new File(thumbPath);
		if(!thumbPathDir.isDirectory()){
			System.out.println("UploadHandler->made thumb dir");
			thumbPathDir.mkdirs();
		}
		
		//get Factory and parse request FileItems
		try{
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List<FileItem> items = upload.parseRequest(request);
			
			Iterator<FileItem> it = items.iterator();
			while (it.hasNext()) {
				FileItem item = (FileItem) it.next();
				if (!item.isFormField()) {
					fileFound = true;
				    fileName = FilenameUtils.getName(item.getName());
				    extension = FilenameUtils.getExtension(fileName);
				    imgPath = FilenameUtils.separatorsToSystem(imgPath);
					System.out.println("UploadHandler->filename: " + fileName);
					uploadedImgFile = new File(imgPath, fileName);
					uploadedThumbFile = new File(thumbPath, fileName);
										
					if(!uploadedThumbFile.getParentFile().isDirectory()){
						uploadedThumbFile.mkdirs();
					}
					item.write(uploadedImgFile);
				}
			}// end while
		}// end try
		catch(FileNotFoundException ex){
			System.out.println("UploadHandler->in catch");
			ex.printStackTrace();
			fileFound = false;
			request.setAttribute("picStatus", "empty");
			RequestDispatcher view = request.getRequestDispatcher("ShowProfile.do");
			view.forward(request, response);
			return;
		}
		catch(Exception ex){ ex.printStackTrace();}

		//call the Scaler function class and scale the pictures here.
		//call write on the scaled BufferedImage. Save as same file type as uploaded image.
		if (fileFound == true) {
			try{
				System.out.println("UploadHandler-->ScalerCall-->1st try");
				BufferedImage thePic = ImageIO.read(uploadedImgFile);
				Scaler scaler = new Scaler();
				BufferedImage profileImg = scaler.getScaledInstance(thePic, 150);
				BufferedImage profileThumb = scaler.getScaledInstance(thePic, 60);
				File profPic = new File(imgPath, fileName);
				File thumbPic = new File(thumbPath, fileName);
				ImageIO.write(profileImg, extension, profPic);
				ImageIO.write(profileThumb, extension, thumbPic);
				System.out.println("UploadHandler-->ScalerCall-->1st try: after writes.");
			}
			catch(Exception ex) {ex.printStackTrace();}

			// update pic to the database and retrieve updated Person object
			Update up = new Update();
			String picString = "profile/" + user.getId() +
								"/images/" + fileName;
			System.out.println("UploadHandler->picString: " + picString);
			up.updater(datasource, email, picString);
			Member m = new Member(datasource);
			user = m.lookup(email);			
		}
		
		// create RequestDispatcher	
		// updated Person object is set to the session. profilepic can now be accessed.
		session.setAttribute("user", user);
		RequestDispatcher view = request.getRequestDispatcher(dispatchString);
		view.forward(request, response);
	} //end doPost	
}