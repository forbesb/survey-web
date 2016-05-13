package com.brian.database;

public class Survey {
	private int id;//This uniquely identifies a survey, and is used for its relationship with Questions.
	private String name;//This string is the name of the survey.
	//For example, a survey entity could be called "Favorites" and have an id of 2.
	//It's 'id' would be "2", and it's name would be "Favorites".

	public Survey(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
