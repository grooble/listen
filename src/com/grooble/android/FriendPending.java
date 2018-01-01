package com.grooble.android;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONException;
import org.json.JSONObject;

import com.grooble.model.Member;
import com.grooble.model.Person;

public class FriendPending extends HttpServlet {
    
    private static final String TAG = "FriendPending: ";
    
    private DataSource ds;
    private Connection con;
    private String email;
    private String password;
    private int pendingId;

    private Person user; 
        
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
        
        // Get parameters
        email = request.getParameter("email");
        password = request.getParameter("password");
        String pendingIdString = request.getParameter("pendingid");
        pendingId = Integer.valueOf(pendingIdString);
        
        System.out.println(TAG + "email: " + email + ", pwd: " + password + ", pendingId: " + String.valueOf(pendingId));
        
        Member member = new Member();
        
        // get user
        user = member.verify(ds, email, password);
        if (user == null){return;}

        /*
         * Add single friend who has approval waiting to the friends list.
         * This is called from the UserFragment pending list via the PendingUpdateDialogFragment
         */
        //plaintext password
        int success = -1; // to denote failure
        FriendUtils friender = new FriendUtils(ds, email, password);
        success = friender.makeFriend(ds, user.getId(), pendingId);
        if(success !=-1){
            //friend request succeeded
            friender.deleteRequest(ds, pendingId, user.getId());
        }
        
        // Create JSON and set success flag
        JSONObject successJson = new JSONObject();
        try {
            successJson.put("success", success);
        } catch (JSONException e) {
            System.out.println(TAG + "ERROR: unable to set success int to JSONObject.");
            e.printStackTrace();
        }
        
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.write(successJson.toString());
           
    }

}