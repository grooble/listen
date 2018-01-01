package com.grooble.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Result {

    /*
     * Created after the completion of a test.
     * A testId is generated and set and question ids
     * and correct and answered indices are set.
     * A timestamp is also set from the Database.
     * 
     *  The questions List<Integer> contains all of the 
     *  test questions in their taken order. (Currently, 20 qns)
     *  
     *  correct and answered are indicies [0 .. 3] of 
     *  each test question and set at creation of the question.
     */
    
private int testId;
private java.util.Date date;
private List<Integer> questions;
private List<Integer> correct;
private List<Integer> answered;
private int level;
private String comment;

    public Result(int testId, java.util.Date date, List<Integer> questions, List<Integer> correct, List<Integer> answered, String comment){
        this.testId = testId;
        this.date = date;
        this.questions = questions;
        this.correct = correct;
        this.answered = answered;
        this.level = 1;
        this.comment = comment;
    }

    public Result(int testId, 
                  java.util.Date date, 
                  List<Integer> questions, 
                  List<Integer> correct, 
                  List<Integer> answered, 
                  int level, 
                  String comment){
        this.testId = testId;
        this.date = date;
        this.questions = questions;
        this.correct = correct;
        this.answered = answered;
        this.level = level;
        this.comment = comment;
    }
    
    
    public Result(){
        new Result(0, new Date(), new ArrayList<Integer>(), new ArrayList<Integer>(), new ArrayList<Integer>(), 0, null);
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

    public List<Integer> getQuestions() {
        return questions;
    }
    
    public void setQuestions(List<Integer> questions) {
        this.questions = questions;
    }
    
    public List<Integer> getCorrect() {
        return correct;
    }
    
    public void setCorrect(List<Integer> correct) {
        this.correct = correct;
    }
    
    public List<Integer> getAnswered() {
        return answered;
    }
    
    public void setAnswered(List<Integer> answered) {
        this.answered = answered;
    }
    
    public int getLevel(){
        return level;
    }
    
    public void setLevel(int level){
        this.level = level;
    }
    
    public String getComment(){
        return comment;
    }
    
    public void setComment(String comment){
        this.comment = comment;
    }
    
    public JSONObject toJson(){
        JSONObject job = new JSONObject();
        JSONArray JSONQuestions, JSONCorrect, JSONAnswered;
        // populate JSONArrays
        try {
            JSONQuestions = new JSONArray();
            for(Integer qnIndex : questions){
                JSONQuestions.put(qnIndex);
            }

            JSONCorrect = new JSONArray();
            for(Integer cIndex : correct){
                JSONCorrect.put(cIndex);
            }

            JSONAnswered = new JSONArray();
            for(Integer anIndex : answered){
                JSONAnswered.put(anIndex);
            }

            //set JSON data to return object
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yy HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            job.put("testid", testId + "");
            job.put("date", dateFormat.format(date));
            job.put("questions", JSONQuestions);
            job.put("correct", JSONCorrect);
            job.put("answered", JSONAnswered);
            job.put("level", level);
            job.put("comment", comment);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return job;
    }
}
