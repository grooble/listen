package com.grooble.android;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import org.json.*;
//import java.util.*;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.grooble.model.Question;
import com.grooble.model.Test;
import com.grooble.model.TestMakerA;

/**
 * Makes int[] from JSon sent from completed test
 * and sets int[] to session as well as adding to the Test object
 */
@SuppressWarnings("serial")
public class RecoverTest extends HttpServlet {

    private DataSource ds;
    private static final String TAG = "RecoverTest";
    
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
                
        // get test and user parameters
        String testIdString = request.getParameter("testid");
        int testId = -1;
        if(testIdString != null){
            testId = Integer.parseInt(testIdString);
            System.out.println(TAG + " got testid: " + testId);
        }
        
        // get test from TestMakerA
        Test test = new TestMakerA().getTest(ds, testId);

        // Create JSONObject 'JSONTest' and add JSONArray of test objects
        JSONObject JSONTest = new JSONObject();
        
        // Check is test has been returned
        // If the test lookup fails, getTest.size() will be 0
        if(test.getTest().size() > 0){

            // convert test into JSON
            
            // get Test elements correct, selected and questions
            int[] correct = test.getCorrect();
            int[] selected = test.getSelected();
            List<Question[]> questions = test.getTest();
            
            // create JSONArray for 'correct' values and fill array
            JSONArray JSONqnar = new JSONArray();
            for(int correctValue : correct){
                JSONqnar.put(correctValue);
            }
            System.out.println(TAG + " correct: " + JSONqnar.toString());
            
            // Create JSONObject called 'correctJSONValues'
            JSONObject correctJSONObject = new JSONObject();
            
            
            // create JSONArray for 'selected' values and fill array
            JSONArray JSONslar = new JSONArray();
            for(int selectedValue : selected){
                JSONslar.put(selectedValue);
            }        
            System.out.println(TAG + " selected: " + JSONslar.toString());
            
            // Create JSONObject called 'JSONSelected' and add 'selected' JSONArray
            JSONObject selectedJSONObject = new JSONObject();
            
            
            // JSONArray for questions
            JSONArray questionSetJSON = new JSONArray();
            Iterator<Question[]> it = questions.iterator();
            while(it.hasNext()){
                JSONArray JSONQn = new JSONArray();
                Question[] nextQn = it.next();
                for(int i = 0; i < nextQn.length; i++){
                    String qnString = nextQn[i].getCategory() + "/" + nextQn[i].getImage(); 
                    JSONQn.put(qnString);
                }
                System.out.println(TAG + " JSONQn: " + JSONQn);
                questionSetJSON.put(JSONQn);
            }
            
            // Create JSONObject 'JSONSelected' and add nested JSONArray of questions
            JSONObject questionsJSONObject = new JSONObject();
            
            // Assemble JSON elements into test JSON
            JSONArray JSONTestArray = new JSONArray();
            JSONTestArray.put(correctJSONObject);
            JSONTestArray.put(selectedJSONObject);
            JSONTestArray.put(questionsJSONObject);
            
            // put JSONArrays to their JSONObjects
            try {
                questionsJSONObject.put("JSONQuestions", questionSetJSON);
                correctJSONObject.put("JSONCorrect", JSONqnar);
                selectedJSONObject.put("JSONSelected", JSONslar);
                JSONTest.put("JSONTest", JSONTestArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            
        }
        // There was an error and a properly formed test cannot be returned.
        else{
            JSONTest = new JSONObject();
            try {
                JSONTest.put("error", "test not found");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println(TAG + "...write response:");
        System.out.println(JSONTest.toString());
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.write(JSONTest.toString());
    }
    
    
}
