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

import BCrypt.BCrypt;

import com.grooble.model.Member;
import com.grooble.model.Person;

/**
*   Used to login new members from
*   the Android app.
*   The response writes JSON back to the calling method:
*   email:the email submitted by the app.
*   password: the hashed password
*   email="error" for unsuccessful join attempt
*/

@SuppressWarnings("serial")
public class Join2 extends HttpServlet{
    private DataSource ds;
    private String encoding;
    private String secretKeySpec;
    private String ivParameterSpec;
    
    private JSONObject JSONUser, JSONContainer;
 
    // get init parameter and initialize datasource
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        encoding = context.getInitParameter("PARAMETER_ENCODING");
        // get specs for encryption
        secretKeySpec = context.getInitParameter("SECRET_KEY_SPEC");
        ivParameterSpec = context.getInitParameter("IV_PARAMETER_SPEC");
        
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
        
        // get email and password
        String mail = request.getParameter("email").toLowerCase();
        String pwd = request.getParameter("password");
            
        //Initialize person to be added
        Person user = null;
        
        //Initialize JSON objects and array
        JSONContainer = new JSONObject();
        
        // Initialize encryption
        Encrypter encrypter = new Encrypter(secretKeySpec, ivParameterSpec);
        
        // Check if email belongs to existing member
        Member m = new Member();
        if(m.isUser(ds, mail)){
            System.out.println("this email associated with existing acct.: " + mail);
        } 
        else {         
            // Add new member with encrypted password and email
            String hashedPwd = BCrypt.hashpw(pwd, BCrypt.gensalt());
            System.out.println("Join->hashes::email: " + mail + "; pwd: " + hashedPwd);
            // Encrypt email
            String encryptedMail = encrypter.encryptMe(mail);
            m.addMember(ds, encryptedMail, hashedPwd);
            
            // Check success of Join action. Verify and get current joined user.
            user = m.verify(ds, encryptedMail, pwd);
            if (!(user.getEmail().equals(encryptedMail))){
                // email not verified
                // Set empty Person to return as user.
                System.out.println("grooble.android.Join->user was null");
                user = new Person();                
            }
            
            // ...otherwise, user verified and ready to return
            // Decrypt user data and add to JSON container.
            Person decryptedUser = encrypter.decryptMe(user);
            JSONUser = decryptedUser.getJSONObject();
            try{            
                JSONContainer.put("user", JSONUser);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        System.out.println("Join->writing response");
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