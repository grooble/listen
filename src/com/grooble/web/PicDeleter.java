package com.grooble.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.grooble.model.Person;

@SuppressWarnings("serial")
public class PicDeleter extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
					throws ServletException, IOException {
		response.setContentType("text/html");
		HttpSession session = request.getSession();
		
		Person user = (Person)session.getAttribute("user");
		String message = "";
		String dispatch = "";
		String currentProfPic = 
			user.getProfilePic().substring(user.getProfilePic().lastIndexOf('/') + 1);
		String[] checkboxes = request.getParameterValues("cb");
		for(String s: checkboxes){
			System.out.println("PicDeleter->User.pic: " + currentProfPic);
			System.out.println("PicDeleter->" + s);
			if(s.equals(currentProfPic)){
				message = "cannot delete current profile pic";
				request.setAttribute("message", message);
				dispatch = "GetProfilePics.do";
			}
			else{
				String realPath = getServletContext().getRealPath("/");
				String imgPath = realPath + 
				"profile" + File.separator + 
				user.getId() + File.separator + 
				"images" + File.separator + s;
				String thumbPath = realPath + 
				"profile" + File.separator + 
				user.getId() + File.separator + 
				"images" + File.separator +
				"thumb" + File.separator + s;
				File imgToDelete = new File(imgPath);
				File thumbToDelete = new File(thumbPath);
				System.out.println("PicDeleter->" + imgToDelete.toString());
				if (imgToDelete.exists()){
					imgToDelete.delete();
				}
				if (thumbToDelete.exists()){
					thumbToDelete.delete();
				}
				dispatch = "ShowProfile.do";
			}
		}
		
		RequestDispatcher view = request.getRequestDispatcher(dispatch);
		view.forward(request, response);
	}

}
