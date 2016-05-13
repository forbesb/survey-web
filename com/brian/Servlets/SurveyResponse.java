package com.brian.Servlets;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class SurveyResponse {
	protected ArrayList<Integer> responses= new ArrayList<Integer>();
	public Survey survey;
	
	public SurveyResponse(Survey s, Hashtable<String, String[]> hTable) {
		// TODO Auto-generated constructor stub
		//this.survey = ServletAttempt.getSurvey(hTable.get("choice")[0]);
		this.survey = s;
		int count = 1;
		Integer choice;
		for (Map.Entry<String, ArrayList<String>> en: s.questions.entrySet()){
			choice =Integer.valueOf(hTable.get("survey"+count)[0]);//This goes through all the questions and adds the users choice for that question to responses.
			responses.add(choice);
			count++;
		}
		ServletAttempt.getController().insertResponses(responses, s.name);
	}
	
	

}
