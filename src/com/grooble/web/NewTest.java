package com.grooble.web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.*;

import java.util.*;

import com.grooble.model.*;


/**
 * 
 * 		このサーブレットは、init関数でDatasourceと接続して
 * 		そしてリクエストアトリビュートによって、新しいテスト、次の問題
 * 		またテスト終わったら、結果ページにフォーワードする。
 * 		テストのコントローラーです。
 *
 *      This servlet creates new test from various inputs for other 
 *      pages: notably testpage.jsp called by index.jsp and learn.jsp
 *      The test creation routine is wrapped in a do-while clause to 
 *      include a test for duplicate test question elements. If a duplicate 
 *      is encountered, variables are reinitialized and the test creation 
 *      cycle repeats. 
 *      The test is also tested for null return from 
 *      TestMakerA. 
 */

@SuppressWarnings("serial")
public class NewTest extends HttpServlet {
	private DataSource datasource;

	//	コネクションプールからコネクションを取得
	public void init() throws ServletException {
		try {
			datasource = (DataSource) getServletContext().getAttribute("DBCPool");
		} catch(Exception e) {
			throw new ServletException(e.getMessage());
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
					throws ServletException, IOException {
		doPost(request, response);
	}
	
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
					throws ServletException, IOException {
		response.setContentType("text/html");
		
		// get session
		HttpSession session = request.getSession();
		// get request parameters
		String servletPath = request.getServletPath();
		String queryString = 
				(servletPath + '?' + request.getQueryString()).substring(1);
		String difficulty = request.getParameter("difficulty");
		String category = request.getParameter("category");
		String exclusion = request.getParameter("exclusion");
		String testId = request.getParameter("testId");
		String testType = request.getParameter("type");
		System.out.println("NewTest->category: " + category);
		System.out.println("NewTest->dificulty: " + difficulty);
		System.out.println("NewTest->exclusion: " + exclusion);
		System.out.println("NewTest->testType: " + testType);
		
		// inialize test variables
		Test test = null;
		List<Question[]> testQuestions = null;
		List<Question> excluded = null;
		int[] correct = null;
		int[] answers = null;
		int index = 0;
		Integer length = new Integer(0);
		boolean dupArray = false;
		
		// initialize session variables.
		session.setAttribute("test", null);
		session.setAttribute("index", null);
		session.setAttribute("correct", null);
		
		// enter do-while loop to enable loop if duplicate 
		// questions found.
		do {
			TestMakerA maker = new TestMakerA();
			// call to TestMakerA may fail. Wrap in try.
			try {
				// recover test questions for previously completed test
				// from the database with TestMakerA's getTest method 
				// and testId of the test.
				if(testId != null){
					int id = Integer.parseInt(testId);
					test = maker.getTest(datasource, id);
					answers = test.getSelected();
					correct = test.getCorrect();
					if (testType.equals("challenge")){
						int testPercent = getPercent(answers, correct);
						session.setAttribute("friendPercent", testPercent);
						System.out.println("NewTest-->testId(*challenge*): " + testId);
						System.out.println("NewTest-->testPercent(*challenge*):" + testPercent);
					}
				}
				else{
					// create a test that doesn't contain certain words.
					// this is used to create a test for the Learn.jsp.
					// Five of the learned words are entered into the test
					// later. This clause allows us to exclude those words,
					// eliminating duplicates.
					if(exclusion != null){
						excluded = (ArrayList<Question>)session.getAttribute("exclusion");
						System.out.println("NewTest-->exclusion clause: " + excluded);
						test = maker.makeTest(datasource, excluded);
						correct = test.getCorrect();
					} else 
						// typically, this test will be called from the difficulty
						// icons on index.jsp.	
					{
						System.out.println("NewTest-->normal test clause.");
						test = maker.makeTest(datasource, difficulty, category, 5, null);
						correct = test.getCorrect();
					}
				}
			}
			catch(Exception ex){
				ex.printStackTrace();
				System.out.println("NewTest-->exception: " + ex);
			}
			//check for null returned by TestMakerA (should never happen).				
			if(test != null){
				System.out.println("NewTest-->test not null clause\n" + test);
				testQuestions = test.getTest();
				length = testQuestions.size();
				
				// test for duplicate question. If duplicate is found, 
				// reset test variables and remain in test creation do-while loop
				String[] qnWords = new String[length];
				for(int i=0; i<correct.length; i++){
					String word = testQuestions.get(i)[correct[i]].getImage();
					qnWords[i] = word;
				}
				dupArray = hasDuplicate(qnWords);
				if(dupArray){
					System.out.println("NewTest->dupArray found");
					length = 0;
					maker = null;
					test = null;
					correct = null;
					testQuestions = null;
				}else
					// duplicate question not found.
					// set session variables and forward to test page.
					// Page should then make Ajax request to this servlet.
				{					
					session.setAttribute("length", length);
					session.setAttribute("index", index);
					session.setAttribute("test", test);
					session.setAttribute("theTest", testQuestions);
					session.setAttribute("correct", correct);
					session.setAttribute("answers", answers);
					session.setAttribute("queryString", queryString);
					
					RequestDispatcher view = request.getRequestDispatcher("LearnTest.do");
					view.forward(request, response);
				}
			} else 
				// A null test was returned by TestMakerA
				// Takes user back to front page - index.jsp.
			{
				RequestDispatcher view = request.getRequestDispatcher("index-06.jsp");
				view.forward(request, response);
				return;
			}
		}while(dupArray); // end of do-while loop
		
	}	

/*
 * function checks for duplicate in String array and
 * returns a boolean	
 */
	private boolean hasDuplicate(String[] toCheck){
		boolean flag = false;
		String currCheck;
		for(int i=0; i<toCheck.length; i++){
			currCheck = toCheck[i];
			for(int j=0; j<toCheck.length; j++){
				if((currCheck.equals(toCheck[j]))&&(j!=i)){
					flag = true;
				}
			}
		}
		return flag;
	}
	
	private int getPercent(int[] ans, int[]cor){
		if(ans == null || cor == null){
			System.out.println("NewTest-->testPercent: null error");
			return 0;
		}
		if(ans.length == 0 || cor.length == 0){
			System.out.println("NewTest-->testPercent: 0 length error");
			return 0;
		}
		if(ans.length != cor.length){
			System.out.println("NewTest-->testPercent: unequal length error");
			return 0;
		}
		int percent = 0;
		long corrCount = 0;
		for(int i=0; i<ans.length; i++){
			if(ans[i]==cor[i]){
				corrCount++;
			}
		}
		percent = (int)((corrCount/cor.length)*100);
		System.out.println("NewTest-->testPercent in sub: " + percent);
		return percent;
	}
}