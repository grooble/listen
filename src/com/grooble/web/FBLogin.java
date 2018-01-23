package com.grooble.web;

import org.json.JSONObject;
import org.json.JSONException;

import com.grooble.model.Member;
import com.grooble.model.Person;
import com.grooble.model.Update;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

@SuppressWarnings("serial")
public class FBLogin extends HttpServlet {
	
	private String myfacebookappsecret = "84d824387e61fcfd16a1bf291e65308b";
	private DataSource datasource;
	public void init() throws ServletException {
		try {
			datasource = (DataSource) getServletContext().getAttribute("DBCPool");
		} catch(Exception e) {
			throw new ServletException(e.getMessage());
		}
	}

/**
 * Login breaks down into three steps:
 * 1) Use code from parameter to obtain user's facebook token.
 * 2) Use token to obtain user's facebook graph and extract various data from it.
 * 3) Use extracted data to forward to site login.
 */

    public void service(HttpServletRequest request, HttpServletResponse response) 
    						throws ServletException, IOException {            
        String code = request.getParameter("code");
        HttpSession session = request.getSession();
        String userType = "";
        String dispatcherString = "";
        Update up = new Update();
        if (code == null || code.equals("")) {
        	System.out.println("FBLogin->code not found");
        }

        //Get facebook token
        String token = null;
        try {
            String g = "https://graph.facebook.com/oauth/access_token?" + 
    						"client_id=359559514135121" + 
    						"&redirect_uri=" + URLEncoder.encode("http://www.moeigo.com/FBLogin.do", "UTF-8") + 
    						"&client_secret=" + myfacebookappsecret + 
    						"&code=" + code;
            System.out.println("FBLogin->graph url: " + g);
            URL u = new URL(g);
            URLConnection c = u.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            String inputLine;
            StringBuffer b = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
                b.append(inputLine + "\n");            
            in.close();
            token = b.toString();
            System.out.println("FBLogin->token: " + token);
            if (token.startsWith("{"))
                throw new Exception("error on requesting token: " + token + " with code: " + code);
        } catch (Exception e) {
        	System.out.println("FBLogin->token error: " + token);
        	e.printStackTrace();
        }

        //Get facebook graph
        String graph = null;
        try {
            String gr = "https://graph.facebook.com/me?" + token;
            URL v = new URL(gr);
            URLConnection uc = v.openConnection();
            BufferedReader input = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String inputLn;
            StringBuffer buf = new StringBuffer();
            while ((inputLn = input.readLine()) != null)
                buf.append(inputLn + "\n");            
            input.close();
            graph = buf.toString();
            System.out.println("FBLogin->got graph: " + graph);
        } catch (Exception e) {
        	System.out.println("FBLogin->user data: " + graph);
        	e.printStackTrace();
        }
        System.out.println("FBLogin -> graph.toString: " + graph);
        session.setAttribute("graphJSON", graph);

        //Get user data from JSON graph
        String facebookId = "";
        String firstName = "";
        String lastName = "";
        String email = "";
        try {
            JSONObject json = new JSONObject(graph);
            facebookId = json.getString("id");
            firstName = json.getString("first_name");
            lastName = json.getString("last_name");
            email = json.getString("email").toLowerCase();
        } catch (JSONException e) {
        	System.out.println("FBLogin->JSON error");
        	e.printStackTrace();
        }
        System.out.println("FBLogin->name: " + firstName + " " + lastName);
        System.out.println("FBLogin->email: " + email);

        //login existing user, or if not found,
        //create a new user.
        Member check = new Member(datasource);
        Person user = null;
        // check for existing user with this fbid
        // TODO this login method currently not implemented
        user = check.verifyFB(facebookId);
        
        // if a matching fbid found...
        if(user != null){			
			userType = "member";
			session.setAttribute("user", user);
			dispatcherString = "Setup.do";
			// where users login through fb, their pic should be fb pic
			String profilePic = user.getProfilePic();
			if(!profilePic.startsWith("http")){
				String fbPicString = "http://graph.facebook.com/" + 
										facebookId + 
										"/picture?type=large";
				up.updater(datasource, user.getEmail().toLowerCase(), fbPicString);
				user.setProfilePic(fbPicString);
				System.out.println("FBLogin->pic updater:" + fbPicString);
			}
		// User with this fbid not found.
		// Check existing user by email and link to fbid if found.
		// Otherwise, create new account.
        }else { 
        	// first, check for member with same email
        	user = check.lookup(email.toLowerCase());
        	if (user != null){
        		up.setFbid(datasource, facebookId, email.toLowerCase());
    			String loginMsg = "fb account linked to existing account. /nemail: " 
    				+ email.toLowerCase();
    			request.setAttribute("message", loginMsg);
    			session.setAttribute("user", user);
    			dispatcherString = "Setup.do";
    		// user not found by fbid or email. create new.	
        	}else{ 
        		Person added = 
        			check.addMember(email.toLowerCase(), firstName, lastName, facebookId);
        		//Person added = check.verify(datasource, facebookId);
        		System.out.println("FBLogin->new added:" + added.toString());
        		if(added != null){
        			System.out.println("FBLogin->added not null");
        			userType = "member";
        			session.setAttribute("user", added);
        			dispatcherString = "Setup.do";
        		}     		
        	}
		}
		
		session.setAttribute("userType", userType);
		session.setAttribute("FBLogin", "fblogin");
		System.out.println("FBLogin->userType: " + userType);
		System.out.println("FBLogin->dispatcherString: " + dispatcherString);
        RequestDispatcher view = request.getRequestDispatcher(dispatcherString);
		view.forward(request, response);
    }
}