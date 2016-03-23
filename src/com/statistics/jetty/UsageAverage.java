package com.statistics.jetty;


import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.statistics.jetty.Model.Statistics;



@Path("/statistics/admin/usageaverage")
public class UsageAverage {
	
    /*
     * Represent the gson
     */
     private Gson gson;
     
 	private static Connection connection = null;
 	private static Statement statement = null;
	
	 public UsageAverage() {
	   super();
	   this.gson = new Gson();
	 }
	
	 @GET
	 public String isActive() {
	        return "Statistics is OK!";
	 }
	 
	 
	 
	public static void conexionDB() throws Exception{
			
		Class.forName("org.sqlite.JDBC");
			
		String directoryDatabase = System.getProperties().getProperty("user.home");
		directoryDatabase = directoryDatabase + "/externalStorage/files/";
			
		File filedirectoryDatabase = new File(directoryDatabase);
			
		if(filedirectoryDatabase.exists()){
			  connection = DriverManager.getConnection("jdbc:sqlite:"+directoryDatabase+"databasestatistics.db");
	          statement = connection.createStatement();
	          statement.setQueryTimeout(30);  // set timeout to 30 sec.
		}
			
	}
	
	@GET
	@Path("/current/data")
	@Produces(MediaType.APPLICATION_JSON)
	public Response consultOfActualMonth() {
		
		JsonObject respond = new JsonObject();
		
		List<Statistics> listComplete = null;
		
		try {
			conexionDB();
			listComplete = consultCompleteOfActualMonth();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(listComplete != null){
			
			respond.addProperty("success", Boolean.TRUE);
            respond.addProperty("data", gson.toJson(listComplete));
            
		}else{
			
			respond.addProperty("success", Boolean.FALSE);
			respond.addProperty("data", "Error: There is not Actual Percent.");
			
		}
		
		
        return Response.status(200).entity(gson.toJson(respond)).build();

	}
	
	public static List<Statistics> consultCompleteOfActualMonth() throws Exception{
		
		   List<Statistics> listCompleteOfActualMonth = null;
		   List<Statistics> listComplete = null;
		   HashMap <Integer, List<Statistics>> listofDays = null;
		   ResultSet rs = null;
		   
		    Date dateRightNow = new Date();
			
			Calendar calDateRightNow = Calendar.getInstance();
			calDateRightNow.setTime(dateRightNow);
		
			if(statement != null)
				rs = statement.executeQuery("select * from statistics");
		   
		   if(rs != null){
			   
			   listCompleteOfActualMonth = new ArrayList<>();
			   
			   while(rs.next())
			   {
				   Calendar cal = Calendar.getInstance();
				   DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				   Date date = df.parse(rs.getString("timeActual"));
				   cal.setTime(date);
				   
				   if(cal.get(Calendar.YEAR) == calDateRightNow.get(Calendar.YEAR) && 
						   cal.get(Calendar.MONTH) == calDateRightNow.get(Calendar.MONTH)){
					   
				     Statistics statistics = new Statistics(rs.getInt("id"), rs.getInt("memory"), rs.getInt("cpu"), date);
				     listCompleteOfActualMonth.add(statistics);
				     
				   }
			   }
			   
			   rs.close();
			   
		   }
		   
		   if(listCompleteOfActualMonth != null){
			   
			   		   
			   listofDays = new HashMap<>();
			   
			   for(Statistics statistics : listCompleteOfActualMonth){
				   
				   Date date = statistics.getTimeActual();
				   Calendar cal = Calendar.getInstance();
				   cal.setTime(date);
				   
				   if(!listofDays.containsKey(cal.get(Calendar.DAY_OF_MONTH))){
					   
					   List<Statistics> listNew = new ArrayList<>();
					   listNew.add(statistics);
					   
					   listofDays.put(cal.get(Calendar.DAY_OF_MONTH), listNew);
					   
				   }else{
					   listofDays.get(cal.get(Calendar.DAY_OF_MONTH)).add(statistics);
				   }
				   
			   }
			   
			   if(listofDays != null){
				   
				   listComplete = new ArrayList<>();
				   
				   for(List<Statistics> list : listofDays.values()){
					   
					   int memory = 0;
					   int cpu = 0;
					   int memoryAverage = 0;
					   int cpuAverage = 0;
					   Date dateMain = null;
					   
					   if(list != null){
						   
						   for(Statistics statistics : list){
							   memory += statistics.getMemory();
							   cpu += statistics.getCpu();
							   dateMain = statistics.getTimeActual();
						   }
						   
						   memoryAverage = memory / list.size();
						   cpuAverage = cpu / list.size();
						   
					   }
					   
					   Calendar cal = Calendar.getInstance();
					   cal.setTime(dateMain);
					   
					   Statistics statisticsNew = new Statistics(0,
							   memoryAverage,
							   cpuAverage,
							   dateMain,
							   cal.get(Calendar.YEAR),
							   cal.get(Calendar.MONTH),
							   cal.get(Calendar.DAY_OF_MONTH));
					   
					   listComplete.add(statisticsNew);
					   
				   }
				   
				   listComplete = orderByDay(listComplete);
			   }
			   
			   
			   
		   }
		   
		   return listComplete;
		   
		   
	}
		
	private static List<Statistics> orderByDay(List<Statistics> listDisOrdered){
			
			List<Statistics> listComplete = new ArrayList<>();
			Statistics[] arrayDisOrdered = new Statistics[listDisOrdered.size()];
			
			int v = 0;
			for(Statistics s : listDisOrdered){
				arrayDisOrdered[v] = s;
				v++;
			}
			
			for(int i = 0; i < arrayDisOrdered.length - 1; i++){
				
				for(int j = i+1; j < arrayDisOrdered.length; j++){
					
					if(arrayDisOrdered[i].getDay() > arrayDisOrdered[j].getDay()){
						Statistics statisticsAux = arrayDisOrdered[i];
						arrayDisOrdered[i] = arrayDisOrdered[j];
						arrayDisOrdered[j] = statisticsAux;
					}
				}
				
			}
			
			for(int i = 0; i < arrayDisOrdered.length; i++)
				listComplete.add(arrayDisOrdered[i]);
			
			
			
			
			
			return listComplete;
			
	}

}
