package com.grooble.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONMaker {
/**
 * Creates a JSONArray to be passed back to a servlet when 
 * a test is created.
 * The JSONArray is then written to an Ajax call.
 * The first four cells are the paths to images.
 * The fifth cell is an index for the sound. i.e. if the index
 * is 2, it refers to the 3rd path (0,1,2).
 * An index is prefered to a path as it can be compared when 
 * the array is passed and read by Javascript to give a unique
 * value. 
 * There is no 5th field as there are no selected values yet.
 * @param testQns
 * @param correct
 * @return
 */
	public JSONArray toJSON(List<Question[]> testQns, int[] correct){
		JSONArray JSONTest = new JSONArray();
		for (int i=0; i<testQns.size(); i++ ){
			JSONArray JSONData = new JSONArray();
			JSONData.put(testQns.get(i)[0].getCategory() +
				"/" + testQns.get(i)[0].getImage());
			JSONData.put(testQns.get(i)[1].getCategory() +
				"/" + testQns.get(i)[1].getImage());
			JSONData.put(testQns.get(i)[2].getCategory() +
				"/" + testQns.get(i)[2].getImage());
			JSONData.put(testQns.get(i)[3].getCategory() +
				"/" + testQns.get(i)[3].getImage());
			JSONData.put(correct[i]);
			JSONTest.put(JSONData);
		}
		//System.out.println("JSONMaker:toJSON(List<Question int[]>-->" + JSONTest.toString());
		return JSONTest;
	}

/**
 * This overloaded JSONArray constructor takes a Test
 * rather than the Question<List> and correct[int] above.
 * A six-valued array is returned.
 * @param test
 * @return
 */
	public JSONArray toJSON(Test test){
		List<Question[]> testQns = test.getTest();
		int[] correct = test.getCorrect();
		int[] selected = test.getSelected();

		JSONArray JSONTest = new JSONArray();
		for (int i=0; i<testQns.size(); i++ ){
			JSONArray JSONData = new JSONArray();
			JSONData.put(testQns.get(i)[0].getCategory() +
				"/" + testQns.get(i)[0].getImage());
			JSONData.put(testQns.get(i)[1].getCategory() +
				"/" + testQns.get(i)[1].getImage());
			JSONData.put(testQns.get(i)[2].getCategory() +
				"/" + testQns.get(i)[2].getImage());
			JSONData.put(testQns.get(i)[3].getCategory() +
				"/" + testQns.get(i)[3].getImage());
			
			JSONData.put(correct[i]);
			if(selected != null){
				JSONData.put(selected[i]);
			} else {
				JSONData.put(-1);
			}

			JSONTest.put(JSONData);
		}
		return JSONTest;
	}
	
	/**
	 * This overload of the toJSON method takes an ArrayList
	 * of Status and returns the corresponding JSONArray
	 * @param status
	 * @return
	 */
	public JSONArray toJSON(List<Status> status){
		ArrayList<Status> statusList = (ArrayList<Status>) status;
		JSONArray moreJSONStatus = new JSONArray();
		for (int i = 0; i < statusList.size(); i++){
			Status currentStatus = (Status) statusList.get(i);
			JSONArray lineData = new JSONArray();
			lineData.put(currentStatus.getId());
			lineData.put(currentStatus.getDate());
			lineData.put(currentStatus.getUser());
			lineData.put(currentStatus.getPic());
			lineData.put(currentStatus.getType());
			lineData.put(currentStatus.getContent());
			lineData.put(currentStatus.getStdId());
			
			moreJSONStatus.put(lineData);
			//System.out.println("JSONMaker: " + lineData.toString());
		}
		return moreJSONStatus;
	}
	
	/**
	 * This method is used in the Android app to return a JSONArray 
	 * of Status JSONObjects. 
	 * The method calls the getJSONObject method of the Status class.
	 * @param person
	 * @return
	 */
	public JSONArray getJSONArray(List<?> asList){
	    JSONArray JSONtoreturn = new JSONArray();
	    for(int i = 0; i < asList.size(); i++){
	        if(asList.get(i) instanceof Status){
	            JSONtoreturn.put(((Status) asList.get(i)).getJSONObject());
	        }
	        else if(asList.get(i) instanceof Result){
	            JSONtoreturn.put(((Result) asList.get(i)).toJson());
            }
	    }
	    return JSONtoreturn;
	}
	
	public JSONArray toJSON(Person person){
		JSONArray personJSON = new JSONArray();
		personJSON.put(person.getId());
		personJSON.put(person.getEmail());
		personJSON.put(person.getFbid());
		personJSON.put(person.getFirstName());
		personJSON.put(person.getLastName());
		personJSON.put(person.getProfilePic());
		
		return personJSON;
	}
	
	public JSONObject allQuestionJSON(List<Question> questions){
	    JSONObject allQuestions = new JSONObject();
	    JSONArray questionArray = new JSONArray();
	    Iterator<Question> it = questions.iterator();
	    while(it.hasNext()){
	        Question currentQuestion = it.next();
	        JSONArray singleQuestion = new JSONArray();
	        JSONObject id       = null, 
	                   word     = null, 
	                   image    = null, 
	                   sound    = null, 
	                   category = null, 
	                   level    = null, 
	                   difficulty = null;
	        
	        try {
	            id = new JSONObject();
	            id.put("id", currentQuestion.getQuestionId());
	            word = new JSONObject();
	            word.put("word", currentQuestion.getWord());
	            image = new JSONObject();
	            image.put("image", currentQuestion.getImage());
	            sound = new JSONObject();
	            sound.put("sound", currentQuestion.getSound());
	            category = new JSONObject();
	            category.put("category", currentQuestion.getCategory());
	            level = new JSONObject();
	            level.put("level", currentQuestion.getLevel());
	            difficulty = new JSONObject();
	            difficulty.put("difficulty", currentQuestion.getDifficulty());
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }
            singleQuestion.put(id);
	        singleQuestion.put(word);
            singleQuestion.put(image);
            singleQuestion.put(sound);
            singleQuestion.put(category);
            singleQuestion.put(level);
            singleQuestion.put(difficulty);
            
            questionArray.put(singleQuestion);
	    }
	    try {
            allQuestions.put("questions", questionArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
	    return allQuestions;
	}
	
	// Convenience method to convert an ArrayList of Person to JSONObject
	// The "pack" object contains a labeled JSONArray eg. friends, pending  
	public JSONArray toJSON(List<?> aList, String label){
	    JSONArray jArray = new JSONArray();
	    for (int i=0; i < aList.size(); i++){
	        // using getJSONObject method in the Person class
	        if(aList.get(i) instanceof Person){
	            jArray.put(((Person) aList.get(i)).getJSONObject());
	        }
	        // else use the Result version
	        else if(aList.get(i) instanceof Result){
	            jArray.put(((Result) aList.get(i)).toJson());    
	        }
	    }
	    return jArray;
	}
	
}	