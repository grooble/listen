package com.grooble.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("serial")
public class Status implements Serializable{
	private int id; // id of the status
	private int stdId; // student's id
	private String user; // user's name
	private String date; // date of the new status. two longs with time separated by hyphen '-'
	private String pic;  // a pic or url
	private String type; // type of status: test, comment etc.
	private String content; // content to be shown
	private int parent; // id of parent of this comment
	private int childCount; // used in placing next comment
	
    public Status (){
		setType("comment");
	}
	
	public Status (int stdId, String content){
	    new Status(stdId, "comment", content);
	}

	public Status (int stdId, String type, String content){
        this.stdId = stdId;
        this.content = content;
        this.type = type;
        this.parent = 0;
    }
	
    public Status (int stdId, String type, String content, int parent){
        this.stdId = stdId;
        this.content = content;
        this.type = type;
        this.parent = parent;
    }
    
	public Status(int id, int stdId, String user, String pic, String type, String content){
		this.id = id;
		this.stdId = stdId;
		this.user = user;
		this.pic = pic;
		this.type = type;
		this.content = content;
		this.parent = 0;
	}
	
    public Status(int id, int stdId, String user, String pic, String type, String content, int parent){
        this.id = id;
        this.stdId = stdId;
        this.user = user;
        this.pic = pic;
        this.type = type;
        this.content = content;
        this.parent = parent;
    }

	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setStdId(int stdId) {
		this.stdId = stdId;
	}
	public int getStdId() {
		return stdId;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getDate() {
		return date;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getUser() {
		return user;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getPic() {
		return pic;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getContent() {
		return content;
	}
	
    public int getChildCount() {
        return childCount;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public JSONObject getJSONObject(){
        //System.out.println("Status.getJsonObject()");
        JSONObject obj = new JSONObject();
        try{
            obj.put("id", id);
            
            obj.put("stdId", stdId);
            
            if((user == null) || (user.isEmpty()))
                obj.put("user", "");
            else
                obj.put("user", user);
            
            if(date == null) 
                obj.put("date", "");
            else
                obj.put("date", date.toString());

            if((pic == null) || (pic.isEmpty()))
                obj.put("pic", "");
            else
                obj.put("pic", pic);

            if((type == null) || (type.isEmpty()))
                obj.put("type", "");
            else
                obj.put("type", type);
            
            if((content == null) || (content.isEmpty()))
                obj.put("content", "");
            else
                obj.put("content", content);
            
            obj.put("parent", parent);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        String content = null;
        try {
            content = obj.getString("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return obj;
    }
	
	public String toString(){
		return "id: " + this.id +
		"--stdId: " + this.stdId +
		"--name: " + this.user + 
        "--date: " + this.date + 
        "--pic: " + this.pic + 
		"--type: " + this.type + 
		"--content: " +	this.content +
		"--parent: " + this.parent;
	}
}
