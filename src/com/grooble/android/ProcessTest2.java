package com.grooble.android;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import org.json.*;
//import java.util.*;
import javax.sql.DataSource;

import org.json.JSONException;
import org.json.JSONObject;

import com.grooble.model.MarkTest;
import com.grooble.model.Member;
import com.grooble.model.Person;
import com.grooble.model.Question;
import com.grooble.model.Test;

/**
 * Makes int[] from JSon sent from completed test
 * and sets int[] to session as well as adding to the Test object
 */
@SuppressWarnings("serial")
public class ProcessTest2 extends HttpServlet {

    private final static int QUESTION_SIZE = 4;
    private DataSource ds;
    private static final String TAG = "ProcessTest2 ";
    private Timestamp timestamp;
    private SimpleDateFormat dateFormat;
    
    public void init() throws ServletException {
        try {
            ds = (DataSource) getServletContext().getAttribute("DBCPool");
        } catch(Exception e) {
            throw new ServletException(e.getMessage());
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        
        boolean parameterError = false;
       
        // get test and user parameters
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String correct = request.getParameter("correct");
        String answers = request.getParameter("answers");
        String questions = request.getParameter("questions");
        String testType = request.getParameter("testtype");
        String levelString = request.getParameter("level");
        String timestampString = request.getParameter("timestamp");
        String timezoneID = request.getParameter("timezone");
        Integer level = new Integer(0);

        // parse levelString into its Integer equivalent
        try{            
            level = Integer.parseInt(levelString);
        } catch(NumberFormatException nex){
            nex.printStackTrace();
        }
        
        // parse date into UTC
        try{
            dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone(timezoneID));
            Date parsedDate = dateFormat.parse(timestampString);
       
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
       
            dateFormat.format(parsedDate);
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
            System.out.println(TAG + "timestamp: " + dateFormat.format(timestamp));
        }catch(Exception e){
            e.printStackTrace();
        }
        
        
        // get user
        Person user = new Member().verify(ds, email, password);
        System.out.println(TAG + "user_email,points: " + user.getEmail() + ", " + user.getPoints());
        
        // convert parameter strings to int arrays
        int[] answersArray = null;
        int[] correctArray = null;
        int[] questionsArray = null;
        
        if (answers != null){
            answersArray = makeArray(answers);
        }else{
            // answers array not found so return error
            parameterError = true;
        }
        if ((correct != null)&&(!parameterError)){
            correctArray = makeArray(correct);
        }else{
            parameterError = true;
        }
        if ((questions != null)&&(!parameterError)){
            questionsArray = makeArray(questions);
            //System.out.println(TAG + " retrieved questionsArray: " + questionsArray.length);
        }else{
            parameterError = true;
        }
        
        // Create test object from the questions and results
        Test test = buildTest(questionsArray, answersArray, correctArray);
        
        // Add the completed user test to the 'tests' table
        // and get the next newTestId to be sent back to the app.
        MarkTest marker = new MarkTest(ds);
        //marker.update(user, test, newTestId, timestamp, level, testType);
        int gotTestId = marker.update(user, test, timestamp, level, testType);
        
        // Create JSON
        JSONObject JSONContainer = new JSONObject();
        try {
            if(gotTestId > 0){
                JSONContainer.put("test_id", gotTestId);
            }
            else{
                JSONContainer.put("error", "failed to add test");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Write JSON output to response
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.write(JSONContainer.toString());

    }
    
    
    /*
     * The test results come as a String from the request.
     * This is a convenience method to process the string into an int[]
     * for later processing of test results.
     */
    private int[] makeArray(String input){
        //System.out.println(TAG + ": makeArray()...string input to makeArray: " + input);
        StringBuilder is = new StringBuilder(input);
        String s = is.substring(1, is.length()-1);
        String[] ans = s.split(",");
        int l = ans.length;
        int[] answers = new int[l];
        for (int i=0; i<l; i++){
            if (!ans[i].equals("null")){
                answers[i] = Integer.parseInt(ans[i].trim());
            }
            else{
                answers[i] = -1;
            }
        }

        return answers;
    }
    

 /*
 * Create test and add correct and answers
 * take int[] questions and make ArrayList<Question[]> to add to test
 * all fields except questionId are null
 */
    private Test buildTest(int[] questionsArray, int[] answersArray, int[] correctArray){
        Test test = new Test();
        List<Question[]> testQuestions = new ArrayList<Question[]>();
        Question[] questionBlock = new Question[QUESTION_SIZE];
        for (int i = 0; i < questionsArray.length;){
            Question q = new Question();
            q.setQuestionId(questionsArray[i]);
            //System.out.println("ProcessTest: Qn[" + i + "]...");
            questionBlock[i%QUESTION_SIZE] = q;
            i++;
            if(i%QUESTION_SIZE==0){
                testQuestions.add(questionBlock);
                questionBlock = new Question[QUESTION_SIZE];
            }
        }

        // Set questions, correct and answered to test
        test.setTest(testQuestions);
        test.setCorrect(correctArray);
        test.setSelected(answersArray);
        
        return test;
    }

}