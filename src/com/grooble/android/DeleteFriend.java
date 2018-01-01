package com.grooble.android;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.grooble.model.Friender;

public class DeleteFriend extends HttpServlet {
    
    private static final String TAG = "DeleteFriend";
    
    private DataSource ds;
    private Connection con;

    private int user; 
    private int friend;
        
    public void init() throws ServletException {
        try {
            ds = (DataSource) getServletContext().getAttribute("DBCPool");
        } catch(Exception e) {
            throw new ServletException(e.getMessage());
        }
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String userInt = request.getParameter("user");
        String friendInt = request.getParameter("friend");
        System.out.println(TAG + "user: " + user + ", friend: " + friend);
        int userId = -1, friendId = -1;
        if(userInt != null){
            userId = Integer.parseInt(userInt); 
        }
        if(friendInt != null){
            friendId = Integer.parseInt(friendInt);
        }
        int defriended = new Friender().removeFriend(ds, userId, friendId);
        System.out.println(TAG + " defriended: " + defriended);
        
        // return int indicating success or otherwise of delete friend
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.write(defriended + "");        

    }
        
}