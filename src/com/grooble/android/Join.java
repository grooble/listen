package com.grooble.android;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONException;
import org.json.JSONObject;

import com.grooble.model.Member;
import com.grooble.model.MyDebug;
import com.grooble.model.Person;
//import com.grooble.android.Encrypter;

/**
*   Used to login new members from
*   the Android app.
*   The response writes JSON back to the calling method:
*   email:the email submitted by the app.
*   password: the hashed password
*   email="error" for unsuccessful join attempt
*   
*   When user has been successfully added, 
*   a PUT request is sent to MailChimp to add user to DB.
*/

@SuppressWarnings("serial")
public class Join extends HttpServlet{
    private static final String TAG = "Join ";
    private DataSource ds;
    private String encoding;
    private JSONObject JSONUser, JSONContainer;
 
    // get init parameter and initialize datasource
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        
        try {
            ds = (DataSource) context.getAttribute("DBCPool");
        } catch(Exception e) {
            throw new ServletException(e.getMessage());
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // get encoding to set output to UTF-8
        if (encoding != null) {
            request.setCharacterEncoding(encoding);
        }
        response.setContentType("text/html");
                
        // get clear email and password
        String mail = request.getParameter("email").toLowerCase();
        String password = request.getParameter("password");
        
        if(MyDebug.LOGINLOG){
        	System.out.println(TAG + "email: " + mail + ", pwd: " + password);
        }
                
        //exit if email and password paramaters are not found
        if((mail != null && mail.length()>0) && (password != null && password.length() > 0)) {
            
            //Initialize JSON objects and array
            JSONContainer = new JSONObject();
                        
            // Check if email belongs to existing member
            Member m = new Member(ds);
            if(m.verify(mail)){ // true means an existing user has this email so cannot create account
                try {
                    JSONContainer.put("error", "email associated with existing account");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {         
                // Add new member
            	if(MyDebug.LOGINLOG){
            		System.out.println(TAG + "adding member: email: " + mail + "; password: " + password);
            	}
                m.addMember(mail, password);
                
                // Check success of Join action. Verify and get current joined user.
                Person checkedUser = m.verify(mail, password);
                if (null == checkedUser){
                    try {
                        JSONContainer.put("error", "an error ocurred. user not added.");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    // ...otherwise, user verified and ready to return
                    // user decrypted in Member
                	if(MyDebug.LOGINLOG){
                		System.out.println(TAG + "checkedUser: "+ checkedUser.getEmail());
                	}
                    JSONUser = checkedUser.getJSONObject();
                    try{            
                        JSONContainer.put("user", JSONUser);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    // Send PUT request to MailChimp database
                    DoPost dp = new DoPost(mail);
                   	dp.doPostAction();
                }
            }
        }

        if(MyDebug.LOGINLOG){
        	System.out.println("Join->writing response");
        }
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.write(JSONContainer.toString());
    }

    
    public void doPost(HttpServletRequest request, 
                            HttpServletResponse response)
                            throws IOException, ServletException{
        processRequest(request, response);
    }
}