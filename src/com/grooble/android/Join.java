package com.grooble.android;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONException;
import org.json.JSONObject;

import BCrypt.BCrypt;

import com.grooble.model.EncryptionProtocol;
import com.grooble.model.Member;
import com.grooble.model.Person;
//import com.grooble.android.Encrypter;

/**
*   Used to login new members from
*   the Android app.
*   The response writes JSON back to the calling method:
*   email:the email submitted by the app.
*   password: the hashed password
*   email="error" for unsuccessful join attempt
*/

@SuppressWarnings("serial")
public class Join extends HttpServlet{
    private DataSource ds;
    private String encoding;
    private String secretKeySpec, ivParameterSpec, saltString, password;
    private byte[] salt;
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
                
        // get email and password
        String mail = request.getParameter("email").toLowerCase();
        String pwd = request.getParameter("password");
                
        //exit if email and password paramaters are found
        if((mail != null && mail.length()>0) && (pwd != null && pwd.length() > 0)) {
            
            //Initialize person to be added
            Person user = null;
                        
            //Initialize JSON objects and array
            JSONContainer = new JSONObject();
            
            // Initialize encryption and encrypt email
            EncryptionProtocol encrypter = new EncryptionProtocol();
            String encryptedMail = encrypter.encrypt(mail, pwd);

            // Check if email belongs to existing member
            Member m = new Member();
            // TODO implement lookup on hashed email

            if(m.verify(ds, encryptedMail).size() > 0){
                System.out.println("this email associated with existing acct.: " + mail);
            } 
            else {         
                // Create password hash to add to DB
                String hashedPwd = BCrypt.hashpw(pwd, BCrypt.gensalt());
                
                // Create hash of mail for later lookup
                String hashedEmail = BCrypt.hashpw(mail, BCrypt.gensalt());
                
                // Add new member with pwd hash, email hash and encrypted email
                System.out.println("Join->hashes::email: " + encryptedMail + "; pwd: " + hashedPwd);
                m.addMember(ds, encryptedMail, hashedEmail, hashedPwd);
                //m.addMember(ds, encryptedMail, hashedPwd);
                
                // Check success of Join action. Verify and get current joined user.
                List<Person> checkResults = m.verify(ds, hashedEmail);
                switch(checkResults.size()){
                    // no results returned; the user wasn't added successfully
                    case 0: System.out.println("grooble.android.Join->user was null");
                            user = new Person();                
                            break;
                            
                    // Only one user returned. the add was successful
                    case 1: user = checkResults.get(0);
                            break;
                            
                    // More than one found.
                    // Check which of the results is the user by decrypting the mail, then return the user
                    default: 
                            for(int i = 0; i < checkResults.size(); i++){
                                Person p = checkResults.get(i);
                                String cypherMail = p.getEmail();
                                String clearMail = encrypter.decrypt(cypherMail, pwd);
                                // found match and return as user
                                if (clearMail.equals(mail)){
                                    user = p;
                                    break;
                                }
                            }
                            break;
                }
                
                // ...otherwise, user verified and ready to return
                // user decrypted in Member
                JSONUser = user.getJSONObject();
                try{            
                    JSONContainer.put("user", JSONUser);
                }catch (JSONException e){
                    e.printStackTrace();
                }
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