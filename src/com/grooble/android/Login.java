package com.grooble.android;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import BCrypt.BCrypt;

import com.grooble.model.JSONMaker;
import com.grooble.model.MarkTest;
import com.grooble.model.Member;
import com.grooble.model.Person;
import com.grooble.model.Result;
import com.grooble.model.Status;
import com.grooble.model.Update;

/*
 * Get user/pwd login information from android app and login
 *  Access com.grooble.android.LoginSetup to perform setup
 *  Write output to app as a JSON Object
 */

@SuppressWarnings("serial")
public class Login extends HttpServlet {

//  データソースを初期化する
    private static final String TAG = "Login ";
    private DataSource datasource;
    private String encoding;
    
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        encoding = context.getInitParameter("PARAMETER_ENCODING");

        try {
            datasource = (DataSource) getServletContext().getAttribute("DBCPool");
        } catch(Exception e) {
            throw new ServletException(e.getMessage());
        }
    }
    
    public void doGet(HttpServletRequest request, 
                            HttpServletResponse response)
                            throws IOException, ServletException {
        doPost(request, response);
    }
    
    public void doPost(HttpServletRequest request, 
                            HttpServletResponse response)
                            throws IOException, ServletException {
        response.setContentType("text/html");
        response.setCharacterEncoding(encoding);

        String mail = request.getParameter("email");
        String password = request.getParameter("password");
        if (mail != null){
            System.out.println("grooble.android.Login...email: " + mail);
        }
        if (password != null){
            System.out.println("grooble.android.Login...password1: " + password);
        }

 /*
 * Use Member.java to lookup the user.
 * If not found set an error flag and write output as JSON OBject to Android app.
 * Check by testing Person object for null email (found users will have the email set.)
 */
        // Lookup user with email and password
        Member m = new Member(datasource);
        MarkTest marker = new MarkTest(datasource);
        
        // retrive user
        // TODO it is very unlikely the hash will collide so we won't handle it yet
        Person user = m.verify(mail, password);
        
        // get UserBuilder instance to add friends, results etc.
        UserBuilderT builder = new UserBuilderT(user, password, datasource);
        
        // Initialize JSON
        JSONObject JSONContainer = new JSONObject();
        
        System.out.println("grooble.android.Login->user email: " + user.getEmail());
        // If user is found and login succeeds
        if (user.getEmail() != null){ 
        
            // ArrayLists to hold friends and pending friends
            List<Person> friends;
            List<Person> pending;
            List<Result> results;
            Integer newTestId = 0, version = 0;
            
            // get Android app version
            version = Integer.parseInt(getServletContext().getInitParameter("ANDROID_VERSION"));
            System.out.println(TAG + "got version code: " + version);
            
            // get lists of friends, pending and results from builder
            friends = (ArrayList<Person>) builder.getUserFriends();
            pending = (ArrayList<Person>) builder.getUserPending();
            results = (ArrayList<Result>) builder.fetchResults();
            if(results != null && (!results.isEmpty())){
                System.out.println("Login... results.size(): " + results.size());
                System.out.println("Login... results.get(0): " + results.get(0).getTestId());
            }
            
            // get index for any new test results that are created in the app.
            newTestId = marker.getTestId(user, password);

            // initialize statusIndex to zero for listing first 20 status items
            Integer statusIndex = new Integer(0);
            System.out.println(TAG + "calling StatusHandler().getStatus()");
            // get first 20 status items
            ArrayList<Status> status = 
                    (ArrayList<Status>) new StatusHandler().getStatus(datasource, user.getId(), 0, 20);
            statusIndex = 20; // index now set at 20 for next 20 items
            System.out.println(TAG + "status.size(): " + status.size());
            
            // Get JSONObjects for friends, pending and status
            // These JSONObjects contain JSONArrays of the friends, pending and status 
            JSONMaker jm = new JSONMaker();
            JSONArray JSONPending = null, JSONFriends = null, JSONStatus = null, JSONResult = null;
            
            // get JSONFriends JSONArray if friends is not null or empty
            if ((friends==null) || (friends.isEmpty())){
                System.out.println("grooble.android.Login... friends is empty or null");
            }
            else {
                System.out.println("grooble.android.Login... friends is not empty");
                JSONFriends = jm.toJSON(friends, "friends");
            }

            // get JSONPending JSONArray if pending is not null or empty
            if ((pending==null) || (pending.isEmpty())){
                System.out.println("grooble.android.Login... pending is empty or null");
            }
            else {
                System.out.println("grooble.android.Login... pending is not empty");
                JSONPending = jm.toJSON(pending, "pending");
            }
            
            // get JSONStatus JSONArray if status is not null or empty
            if ((status==null) || (status.isEmpty())){
                System.out.println("grooble.android.Login... status is empty or null");
            }
            else {
                System.out.println("grooble.android.Login... status is not empty");
                JSONStatus = jm.getJSONArray(status);
            }

            // get JSONResult JSONArray if results not null or empty
            if ((results==null) || (results.isEmpty())){
                System.out.println("grooble.android.Login... results empty or null");
            }
            else {
                System.out.println("grooble.android.Login... results not empty");
                JSONResult = jm.getJSONArray(results);
            }
                        
            // Put friends, pending, status and status index 
            // into JSONObject for writing to output
            try {
                if(user != null){
                    JSONContainer.put("user", user.getJSONObject());
                }
                if(JSONFriends != null){
                    JSONContainer.put("friends", JSONFriends);
                }
                if(JSONPending != null){
                    JSONContainer.put("pending", JSONPending);
                }
                if(JSONStatus != null){                    
                    JSONContainer.put("status", JSONStatus);
                }
                if(statusIndex != null){
                    JSONContainer.put("statusIndex", statusIndex);
                }
                if(JSONResult != null){
                    JSONContainer.put("results", JSONResult);
                }
                JSONContainer.put("newTestId", newTestId);
                JSONContainer.put("version", version);
                
                // Set fcm_token to JSON for messsaging
                String token = user.getFcm_token();
                String email = user.getEmail();
                System.out.println("Login->fcm_token: " + token);
                System.out.println("Login->hashed_user_email: " + email);
                
                JSONContainer.put("fcm_token", token);
                
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // Login fails. Failed to get user. Write JSON error message to out
        else { 
            System.out.println("Login->user was null");
            try {
                JSONContainer.putOpt("error", new JSONObject().put("error", true));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        // Write JSON output to response.
        System.out.println("Login...write response:");
        System.out.println(JSONContainer.toString());
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.write(JSONContainer.toString());
        
    }
}