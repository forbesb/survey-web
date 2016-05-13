package com.brian.Servlets;

import java.io.PrintWriter;

public class HeaderCreator {

	public HeaderCreator() {
		//This class is meant to output a header for a html file.
	}
	
	public void doHeader(PrintWriter out, String title){
		//Given a title, this method prints out a standard header.
    	out.println("<html>");
    	out.println("<head>");
    	out.println("<title>"+title+"</title>");
    	out.println("</head>");
    }

}
