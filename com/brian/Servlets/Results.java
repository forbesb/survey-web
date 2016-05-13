package com.brian.Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class rResults
 */
public class Results extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Results() {
        super();
        //This class is for the results page.
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Hashtable<String, String[]> hTable = new Hashtable<String, String[]>();
		String key;
		String[] values;
		for(Enumeration<?> e = request.getParameterNames(); e.hasMoreElements();){
			key = (String) e.nextElement();
			values = request.getParameterValues(key);//This gets all of the parameters passed to the web site
			hTable.put(key, values); 
		}
		
		HeaderCreator h = new HeaderCreator();
		PrintWriter out = response.getWriter();
		
		out.println("<html>");
		if (hTable.get("choice")!=null){
			h.doHeader(out, "Results");
			out.println("<body>");
			Survey ch = ServletAttempt.getSurvey(hTable.get("choice")[0]);
			int[][] responseCounts = ServletAttempt.getController().compileResponses(ch);//This gets the int array of the counts.
			int aCount=0;
			int qCount = 0;
			for (Map.Entry<String, ArrayList<String>> en: ch.questions.entrySet()){//This goes through al the questions and responses
				out.println("<BR><BR>"+en.getKey()+":<BR><BR>");
				for (String answer: en.getValue()){
					out.print(responseCounts[qCount][aCount]);//and prints out the count
					out.println(": " + answer+"<BR>");//and the question.
					aCount++;
				}
				aCount=0;
				qCount++;
			}
		} else {

			ServletAttempt.makeChoice(out, h, "Results");
		}
		
		out.println("<form name=\"editor\" method=\"post\" action=\"/servlet/com.brian.Servlets.SurveyEditor\">");
		out.println("<input type=\"submit\" value=\"Editor\">");//This button links to the editor.
		out.println("</form>");
		
		out.println("<form name=\"surveys\" method=\"post\" action=\"/servlet/com.brian.Servlets.ServletAttempt\">");
		out.println("<input type=\"submit\" value=\"Surveys\">");//This button links to the survey page.
		out.println("</form>");
		
		out.println("</body>");
		out.println("</html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
}
