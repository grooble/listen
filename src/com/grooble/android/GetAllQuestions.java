package com.grooble.android;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONObject;

import com.grooble.model.JSONMaker;
import com.grooble.model.Question;
import com.grooble.model.TestMakerA;


@SuppressWarnings("serial")
public class GetAllQuestions extends HttpServlet {
    private DataSource datasource;

    //  コネクションプールからコネクションを取得
    public void init() throws ServletException {
        try {
            datasource = (DataSource) getServletContext().getAttribute("DBCPool");
        } catch(Exception e) {
            throw new ServletException(e.getMessage());
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        
        ArrayList<Question> allQuestions = (ArrayList<Question>) new TestMakerA().listQuestions(datasource);
        
        JSONObject json = new JSONMaker().allQuestionJSON(allQuestions);
        
        File file = new File(getServletContext().getRealPath("/") + "questionjson.txt");
        FileWriter fw = new FileWriter(file);
        fw.write(json.toString());
        fw.close();
    }

}
