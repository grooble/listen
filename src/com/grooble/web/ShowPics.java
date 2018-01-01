package com.grooble.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import com.grooble.model.Person;

@SuppressWarnings({ "unchecked", "serial" })
public class ShowPics extends HttpServlet {
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
                  throws ServletException, IOException{
	HttpSession session = request.getSession();
	Person user = (Person)session.getAttribute("user");
	String imageName = "";
	ArrayList<String> images = new ArrayList<String>();
	String realPath = getServletContext().getRealPath("/");
	String imgPath = realPath + "profile" 
						+ File.separator 
						+ user.getId() 
						+ File.separator 
						+ "images" 
						+ File.separator;
	File imgPathDir = new File(imgPath);
	System.out.println("ShowPics->imgPathDir: " + imgPathDir.toString());
	System.out.println("ShowPics->imgPathDir(dir?): " + imgPathDir.isDirectory());
	
	if (imgPathDir.isDirectory()){
		Collection<File> files = FileUtils.listFiles(imgPathDir, FileFilterUtils.fileFileFilter(), null);
		System.out.println("ShowPics->got dir");
		Iterator<File> i = files.iterator();
		while(i.hasNext()){
			File currentFile = (File)i.next();
			String currentString = currentFile.toString(); 
			imageName = currentString.substring(currentString.lastIndexOf(File.separator)+1);
			images.add(imageName);
		}
		for (String s: (ArrayList<String>)images){
		    System.out.println(s);
		}
		session.setAttribute("profilepics", images);
	}
	  
	RequestDispatcher view = request.getRequestDispatcher("ProfilePics.do");
	view.forward(request, response);

  }
  
  public void doPost(HttpServletRequest request, HttpServletResponse response) 
  				  throws ServletException, IOException{
	  doGet(request, response);
  }
}
