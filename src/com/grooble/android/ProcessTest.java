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
public class ProcessTest extends HttpServlet {

    private final static int QUESTION_SIZE = 4;
    private DataSource ds;
    private static final String TAG = "ProcessTest ";
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
        String newTestIdString = request.getParameter("test_id");
        String testType = request.getParameter("testtype");
        String levelString = request.getParameter("level");
        String timestampString = request.getParameter("timestamp");
        String timezoneID = request.getParameter("timezone");
        System.out.println(TAG + " newTestIdString: " + newTestIdString
                  + "\n" + TAG + " timestampString: " + timestampString);
        Integer newTestId = new Integer(0);
        Integer level = new Integer(0);

        // parse newTestIdString and levelString into their Integer equivalents
        try{            
            newTestId = Integer.parseInt(newTestIdString);
            level = Integer.parseInt(levelString);
        } catch(NumberFormatException nex){
            nex.printStackTrace();
        }
        
        // parse date into UTC
        try{
            dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone(timezoneID));
            Date parsedDate = dateFormat.parse(timestampString);
            System.out.println(TAG + "timezone-JP: " + dateFormat.format(parsedDate));

            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            System.out.println(TAG + "timezone-UTC: " + dateFormat.format(parsedDate));

            dateFormat.format(parsedDate);
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
            System.out.println(TAG + "timestamp: " + dateFormat.format(timestamp));
        }catch(Exception e){
            e.printStackTrace();
        }
        
        
        // get user
        Person user = new Member(ds).verify(email, password);
        if (user != null){
            System.out.println("ProcessTest-->user email: " + user.getEmail());
        }
        else{
            System.out.println("ProcessTest-->user is null");
        }
        
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
            System.out.println(TAG + " retrieved questionsArray: " + questionsArray.length);
        }else{
            parameterError = true;
        }
        
        // Create test and add correct and answers 
        // take int[] questions and make ArrayList<Question[]> to add to test
        // all fields except questionId are null
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
                //System.out.println(TAG + "new questionBlock: " + i/QUESTION_SIZE);
            }
        }

        System.out.println(TAG + " testQuestions.size(): " + testQuestions.size());
        // Set questions, correct and answered to test
        test.setTest(testQuestions);
        test.setCorrect(correctArray);
        test.setSelected(answersArray);
        
        // Add the completed user test to the 'tests' table
        // and get the next newTestId to be sent back to the app.
        MarkTest marker = new MarkTest(ds);
        marker.update(user, test, newTestId, timestamp, level, testType);

    }
    
    private int[] makeArray(String input){
        System.out.println(TAG + ": makeArray()...string input to makeArray: " + input);
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
        System.out.println(TAG + "...makeArray() output:");
        System.out.print("[");
        for(int j = 0; j < answers.length-1; j++){
            System.out.print(answers[j] + ", ");
        }
        System.out.print(answers[answers.length-1] + "]");
        System.out.println();
        return answers;
    }

}