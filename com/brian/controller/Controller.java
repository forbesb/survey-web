/**
 * 
 */
package com.brian.controller;

import java.util.*;
import java.util.Map.Entry;
import java.sql.*;

import com.brian.database.*;
//import com.microsoft.jdbcx.sqlserver.*;
/**
 * @author Brian
 *
 */
public class Controller {
	private DBAccess dba;
	private Connection conn;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//THis was for testing 
		Controller c = new Controller();
		com.brian.Servlets.Survey test = c.getSurvey("aa");
		System.out.println(test.name);
		for (Entry<String, ArrayList<String>> entry: test.questions.entrySet()){
			System.out.println(entry.getKey());
			for (String answer: entry.getValue()){
				System.out.println("Answer: " + answer);
			}
			
		}
	}
	public Controller(){
		dba = new DBAccess();
		conn = dba.getConnection();
	}
	
	public boolean insertResponses(ArrayList<Integer> responses, String sName){
		//with the list of responses(one for each question), the program creates a set of response entities
		
		boolean success = true;
		ArrayList<Question> questions = dba.getQuestions(conn, sName);
		//It first gets all of the question IDs on the database for that survey.
		Response resp;
		ArrayList<Response> responseBeans = new ArrayList<Response>();
		
		int qCount = 0;
		int choice = 0;
		for(Question question: questions){
			int qId = question.getId();//Then, using each question ID
			choice = responses.get(qCount);//it gets the given to that question
			resp = new Response(qId, choice);
			responseBeans.add(resp);//and creates the entity.
			qCount++;
		}
		for (Response r: responseBeans){//Once it has all the entities, it inserts them
			if (!dba.insertResult(conn, r))
				success = false;
		}
		
		return success;//THis boolean would be used for the program to determine the success of the operation
		//but I did not end up using it.
	}
	
	
	public com.brian.Servlets.Survey getSurvey(String surveyName){
		//This method returns a survey by name.
		LinkedHashMap<String, ArrayList<String>> questionsAndAnswers = new LinkedHashMap<String, ArrayList<String>>();
		Survey survey = dba.getSurvey(conn, surveyName);//This gets the survey entity from the database.
		ArrayList<Question> questions = dba.getQuestions(conn, surveyName);//This gets its related questions.
		Collections.sort(questions, new QuestionComparator());//This makes sure the questions are in the correct order going into the TreeMap.
		ArrayList<Choice> choices;
		
		
		for (Question q: questions){
			ArrayList<String> stringChoices = new ArrayList<String>();
			choices = dba.getChoices(conn, q.getId());
			for (Choice c: choices){			//THis section gets the related answers for each question
				stringChoices.add(c.getChoice());
			}
			questionsAndAnswers.put(q.getName(), stringChoices);
		}
		
		com.brian.Servlets.Survey fullSurvey = new com.brian.Servlets.Survey(questionsAndAnswers, survey.getName());
		return fullSurvey;//Once it has all of the data from the database, it returns the survey object.
	}
	
	public ArrayList<String> getSurveyNames(){	
		return dba.getSurveyNames(conn);//This method returns the names of every survey on the database.
	}
	
	public boolean insertSurvey (com.brian.Servlets.Survey s){
		//This method inserts a survey object.
		if (dba.insertSurvey(conn, s))
			return true;
		else
			return false;
	}
	
	public int[][] compileResponses(com.brian.Servlets.Survey s){
		//This method gets all of the responses to a survey.
		
		ArrayList<Question> questions = dba.getQuestions(conn, s.name);
		//It first gets all of the questions.
		int[][] responseCounts = new int[questions.size()][6];
		int qNumber = 0;
		int aNumber = 1;
		for (Question question: questions){
			for (aNumber = 1; aNumber<=6; aNumber++){
				responseCounts[qNumber][aNumber-1]=dba.getCount(conn, question.getId(), aNumber);//The answers start from one, but the array starts from zero.
				//and then uses that id to get the count of each answer.
			}
			qNumber++;
		}
		
		return responseCounts;
		
	}

}
