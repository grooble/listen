package com.grooble.model;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class ResultBak {
    
private int testId;
private java.util.Date date;
private int count;
private int pass;

    public ResultBak(int testId, java.util.Date date, int count, int pass){
        this.testId = testId;
        this.date = date;
        this.count = count;
        this.pass = pass;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPass() {
        return pass;
    }

    public void setPass(int pass) {
        this.pass = pass;
    }
    
    public JSONObject toJson(){
        JSONObject job = new JSONObject();
        try {
            job.put("testid", testId);
            job.put("date", date.toString());
            job.put("count", count);
            job.put("pass", pass);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return job;
    }
}
