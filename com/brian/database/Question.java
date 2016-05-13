package com.brian.database;

public class Question {
	private int id;
	private int surveyId;
	private int order;
	private String name;

	public Question(int id, int surveyId, int order, String name) {
		this.id = id; //This question is uniquely identified by this. It is used for its relationships with Choice and Response
		this.surveyId = surveyId;//It has a relationship with Survey
		this.order = order;//This represents the display order
		this.name = name;//and this is the string of the actual question.
		//An example of a question entity would be "What is your favorite color" being related to the survey "Favorites", whose ID was 2.
		//The question entity would have a 'name' of "What is your favorite color" and a 'surveyId' of 2.
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(int surveyId) {
		this.surveyId = surveyId;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
