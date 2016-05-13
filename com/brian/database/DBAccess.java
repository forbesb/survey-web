package com.brian.database;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

import com.brian.Servlets.ServletAttempt;
import com.microsoft.jdbcx.sqlserver.*;

public class DBAccess {
	private SQLServerDataSource pool;
	
	public DBAccess(){
		pool = new SQLServerDataSource();
		
		pool.setServerName("10.10.10.33");
		pool.setPortNumber(1433);
		pool.setDatabaseName("BrianSurvey");//This section definds attributes of the database.
		pool.setUser("brian");
		pool.setPassword("brian");
		
	}
	
	public static void main(String[] args){
		//This main method was for testing the other methods in here, hence the commented code sections.
		DBAccess dba = new DBAccess();
		Connection conn = dba.getConnection();
		/*
		Survey survey = dba.getSurvey(conn, "aa");
		ArrayList<Question> questions = dba.getQuestions(conn, survey.getName());
		ArrayList<Choice> choices = new ArrayList<Choice>();
		ArrayList<Response> responses;
		for (Question q: questions){
			System.out.println(q.getName());
			choices = dba.getChoices(conn, q.getId());
			for (Choice c: choices){
				System.out.println(c.getChoice());
			}
			responses = dba.getResponses(conn, "aa", q.getId());
			for (Response r: responses){
				System.out.println(r.getResponse());
			}
			
		}
		*/
		//SurveySet set = new SurveySet(questions, choices, survey);
		//dba.insertSurvey(conn, set);
		ServletAttempt.loadSurvey(new File("F:/ICS_Java_2013/learnServlets/testSurvey2.txt"));
		com.brian.Servlets.Survey set = com.brian.Servlets.ServletAttempt.getSurvey((String) ServletAttempt.getNames().toArray()[0]);//testing
		System.out.println(dba.insertSurvey(conn, set));
		//dba.insertResult(conn, 1, 3, "aa");
		
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public Connection getConnection(){
		//This method returns a connection to the database.
		Connection conn = null;
		try {
			conn = pool.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;	
	}
	
	public Survey getSurvey(Connection conn, String name){
		//This method returns a survey entity by name from the database.
		Survey survey = null;
		String query = "select id, name from Survey where name='"+name+"'";
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(query);//It gets back the survey
			if (!rs.equals(null) && rs.next()){
				survey = new Survey(rs.getInt("id"), rs.getString("name"));//and uses it to create the entity
			}
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return survey;
		
	}
	
	public ArrayList<Question> getQuestions(Connection conn, String surveyName) {
		//This method gets a set or question entities by survey name.
		
		ArrayList<Question> questions = new ArrayList<Question>();
		String query = "select question.* from Survey, Question where "
				+ "Survey.id = Question.surveyId and "
				+ "Survey.name='"+surveyName+"'";
		try {
			Statement stmt = conn.createStatement();//The query returns all the questions from the given survey name
			ResultSet rs = stmt.executeQuery(query);
			if (!rs.equals(null)){
				while(rs.next()){
					//and it adds them to the set of questions
					questions.add(new Question(rs.getInt("id"), rs.getInt("surveyId"), rs.getInt("displayOrder"), rs.getString("question")));
				}
			}
			stmt.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		
		return questions;
	}
	
	public ArrayList<Response> getResponses(Connection conn, int questionId){
		//This gets all the responses from a given question id
	ArrayList<Response> responses = new ArrayList<Response>();
	String query = "select response.* from Question, Response where "
		+ "Response.questionId = Question.id and "
		+"Question.id = "+questionId;
	
	try {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);//The query returns the list of responses related to the question.
		if (!rs.equals(null)){
			while(rs.next()){
				responses.add(new Response(rs.getInt("questionId"), rs.getInt("response")));
			}
		}
		stmt.close();
	} catch (SQLException e){
		e.printStackTrace();
	}
	return responses;
	}
	
	public ArrayList<Choice> getChoices(Connection conn, int questionId){
		//This method returns all of the choices related to a question.
		ArrayList<Choice> choices = new ArrayList<Choice>();
		
		String query = "select Choice.* from Question, Choice where "
			+ "Choice.questionId = Question.id and "
			+ "Question.id = " + questionId;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);//the query returns a set of choices with the given question id
			if (!rs.equals(null)){
				while(rs.next()){
					choices.add(new Choice(rs.getInt("questionId"), rs.getInt("displayOrder"), rs.getString("choice")));
				}
			}
			stmt.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
		return choices;
	}
	
	public ArrayList<String> getSurveyNames(Connection conn){
		//This returns all of the names in a given survey,
		ArrayList<String> names = new ArrayList<String>();		
		String query = "Select * from Survey";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);//It returns a set of all the survey objects on the server
			while(rs.next()){
				names.add(rs.getString("name"));//and from those I get the name.
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return names;
	}
	
	public boolean insertSurvey(Connection conn, com.brian.Servlets.Survey set){
		//This method will, given a survey object, insert that survey into the database.
		
		//This method first creates the queries to insert an entire survey
		//and then executes all of the queries.
		
		try {
			String surveyQuery = "insert into Survey (name) "
					+ "Values ('"+set.name+"')";//This query is for the base Survey
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(surveyQuery);//This inserts the survey
			
			String questionQuery, choiceQuery;
			int sId = returnGeneratedKey(stmt);
			int questionOrder = 1;
			int choiceOrder = 1;
			for (Map.Entry<String, ArrayList<String>> question: set.questions.entrySet()){
				questionQuery = "insert into Question (question, displayOrder, surveyId) "
						+ "select '"+question.getKey()+"','"+questionOrder+"',"+sId +" ";
				stmt.executeUpdate(questionQuery);
				int qId = returnGeneratedKey(stmt);
				//System.out.println(questionQuery);
				for (String choice: question.getValue()){
					choiceQuery = "insert into Choice "//This query is more interesting.
							+"(choice, displayOrder, questionId) "//It inserts a choice without knowing both the Survey id and Question ID
					+"values ( " 
						+"'"+choice+"', '"+choiceOrder+++"',"+qId+"  "
					+") ";
					
					//System.out.println(choiceQuery);
					stmt.executeUpdate(choiceQuery);
				}
				questionOrder++;
				choiceOrder = 1;
			}
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		//TODO: fix this.
		return true;
	}
	
	public int returnGeneratedKey(Statement stmt) throws SQLException{
		//This method returns the key a statement just generated.
		
		//It is used after inserting a question or a survey to get its ID to insert things it has relationships to.
		String scopeQuery = "Select SCOPE_IDENTITY() as pKey";
		ResultSet idSet = stmt.executeQuery(scopeQuery);
		int id = -1;
		if (idSet.next()) id = idSet.getInt("pKey");
		return id;
	}
	
	public boolean insertResult(Connection conn, Response resp){
		//This version of the method is only needed if the question Id is already known.
		String query = "insert into Response (response, questionId) "
				+"values ("
				+resp.getResponse()+","+resp.getQuestionID()+")";//given a response entity (with complete information)
		//System.out.println(query);
																//this query inserts it to the database.
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public int getCount(Connection conn, int questionId, int choiceNumber){
		//this returns the count of a given choice for a question.
		int count = 0;
		
		String query = "select count(*) as total from Question, Response where "
				+"Question.id = "+questionId
				+" and Response.response = "+choiceNumber
				+" and Question.id = Response.questionId";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);//The query returns the count of the choices related to the question number
			while(rs.next()){						//which are the selected number.
				count = rs.getInt("total");
			}
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return count;
	}
	
}
