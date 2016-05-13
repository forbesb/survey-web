package com.brian.database;

import java.util.ArrayList;

public class SurveySet {
	public ArrayList<Question> questions;
	public ArrayList<Choice> choices;
	public Survey survey;
	
	public SurveySet(ArrayList<Question> questions, ArrayList<Choice> choices,
			Survey survey) {
		
		//This class would contain a set of a survey related to its questions and choices.
		this.questions = questions;
		this.choices = choices;
		this.survey = survey;
	}

	
}
