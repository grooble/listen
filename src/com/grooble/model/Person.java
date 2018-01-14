package com.grooble.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
*	ユーザークラス
*	インデックス、名字、名前、メール、パスワード、完成したテストの数。
*	パスワードgetter/setterはまだ実行してない。
*/
@SuppressWarnings("serial")
public class Person implements Serializable{
    private static final String TAG = "Person ";
	private int id;
	private String fbid;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String surrogate;
    private String DOB;
	private String profilePic;
	private boolean tutorial;
	private int points;
	private String fcm_token;
	
	public String getFcm_token() {
        return fcm_token;
    }
    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }
    
    public int getId(){
		return id;
	}
	public void setId(int id){
		this.id = id;
	}
	
	public String getFbid(){
		return fbid;
	}
	public void setFbid(String fbid){
		this.fbid = fbid;
	}
	public String getFirstName(){
		return firstName;
	}
	public void setFirstName(String fName){
		firstName = fName;
	}
		
	public String getLastName(){
		return lastName;
	}
	public void setLastName(String lName){
		lastName = lName;
	}
	
	public String getEmail(){
		return email;
	}
	public void setEmail(String email){
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public String getSurrogate() {
	    return surrogate;
	}
	public void setSurrogate(String surrogate) {
	    this.surrogate = surrogate;
	}

	public String getDOB(){
		return DOB;
	}
	
	public void setDOB(String DOB){
		this.DOB = DOB;
	}
	
	public String getProfilePic() {
		return profilePic;
	}
	
	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}
	
	public boolean getTutorial(){
	    return tutorial;
	}
	
	public void setTutorial(boolean tutorial){
	    this.tutorial = tutorial;
	}
		
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	public String toString(){
		return(firstName + " " + lastName);
	}
	
    public JSONObject getJSONObject(){
        //System.out.println(TAG + "Person.getJsonObject()");
        JSONObject obj = new JSONObject();
        try{
            obj.put("id", id);
            obj.put("fbid", fbid);
            obj.put("firstName", firstName);
            obj.put("lastName", lastName);
            obj.put("email", email);
            obj.put("password", password);
            obj.put("DOB", DOB);
            obj.put("profilePic", profilePic);
            obj.put("points", points);
            obj.put("tutorial", tutorial);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        return obj;
    }
}
