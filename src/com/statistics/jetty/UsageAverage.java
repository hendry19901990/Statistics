package com.statistics.jetty;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.statistics.jetty.Model.Statistics;
import com.statistics.machine.Main;



@Path("/statistics/admin/usageaverage")
public class UsageAverage {

	
     private Gson gson;
	
	 public UsageAverage() {
	   super();
	   this.gson = new Gson();
	 }
	
	 @GET
	 public String isActive() {
	        return "Statistics is OK!";
	 }
	 
	 @POST
	 @Path("/saveserverconfbyplatform")
	 @Produces(MediaType.APPLICATION_JSON)
	 public Response notificationserverplatform(@FormParam("networkservicetype") String networkServiceTypeReceive, @FormParam("ipserver") String ipserver) {
		 
		 System.out.println("networkservicetype " + networkServiceTypeReceive + " ipserver " +  ipserver);
		 
		 JsonObject jsonobject = new JsonObject();
		 jsonobject.addProperty("success", Boolean.TRUE);
		 jsonobject.addProperty("data", "successfully was save the server");
		 
		 return Response.status(200).entity(gson.toJson(jsonobject)).build();
	 }
	 
	
	@GET
	@Path("/current/month")
	@Produces(MediaType.APPLICATION_JSON)
	public Response consultOfActualMonth() {
		
		JsonObject respond = new JsonObject();
		
		List<Statistics> listComplete = null;
		
		try {
			listComplete = consultCompleteOfActualMonth();
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
	
	@GET
	@Path("/current/day")
	@Produces(MediaType.APPLICATION_JSON)
	public Response consultOfDay() {
		
		JsonObject respond = new JsonObject();
		
		List<Statistics> listComplete = null;
		
		try {
			listComplete = consultCompleteOftheDay(0);
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
		   ResultSet rsMonth = null;
		   Statement statementMonth = null;
		   
		   Date dateRightNow = new Date();
			
		   Calendar calDateRightNow = Calendar.getInstance();
		   calDateRightNow.setTime(dateRightNow);
		

		   if(Main.getConnectionMain() != null){
			   statementMonth = Main.getConnectionMain().createStatement();
			   statementMonth.setQueryTimeout(30);
			   rsMonth = statementMonth.executeQuery("select * from statistics");
		   }
		
		   
		   if(rsMonth != null){
			   
			   listCompleteOfActualMonth = new ArrayList<>();
			   
			   while(rsMonth.next())
			   {
				   Calendar cal = Calendar.getInstance();
				   DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
				   Date date = df.parse(rsMonth.getString("timeActual"));
				   cal.setTime(date);
				   
				   if(cal.get(Calendar.YEAR) == calDateRightNow.get(Calendar.YEAR) && 
						   cal.get(Calendar.MONTH) == calDateRightNow.get(Calendar.MONTH)){
					   
				     Statistics statistics = new Statistics(rsMonth.getInt("id"), rsMonth.getInt("memory"), rsMonth.getInt("cpu"), date);
				     listCompleteOfActualMonth.add(statistics);
				     
				   }
			   }
			   
			   rsMonth.close();
			   statementMonth.close();
			   
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
							   cal.get(Calendar.MONTH) + 1,
							   cal.get(Calendar.DAY_OF_MONTH));
					   
					   listComplete.add(statisticsNew);
					   
				   }
				   
				   listComplete = orderByDay(listComplete);
			   }
			   
			   
			   
		   }
		   
		   return listComplete;
		   
		   
	}
	
	public static List<Statistics> consultCompleteOftheDay(int daySpecial) throws Exception{
		
		   List<Statistics> listCompleteOfTheDay = null;
		   List<Statistics> listComplete = null;
		   ResultSet rsDay = null;
		   Statement statementDay = null;
		   
		   Date dateRightNow = new Date();
		   Calendar calDateRightNow = Calendar.getInstance();
		   calDateRightNow.setTime(dateRightNow);
			
		   int daySelected = (daySpecial == 0) ? calDateRightNow.get(Calendar.DAY_OF_MONTH) : daySpecial;
		
		   if(Main.getConnectionMain() != null){
			   statementDay = Main.getConnectionMain().createStatement();
			   statementDay.setQueryTimeout(30);
			   rsDay = statementDay.executeQuery("select * from statistics");
		   }
		   
		   if(rsDay != null){
			   
			   listCompleteOfTheDay = new ArrayList<>();
			   
			   while(rsDay.next())
			   {
				   Calendar cal = Calendar.getInstance();
				   DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
				   Date date = df.parse(rsDay.getString("timeActual"));
				   cal.setTime(date);
				   
				   if(cal.get(Calendar.YEAR) == calDateRightNow.get(Calendar.YEAR) && 
						   cal.get(Calendar.MONTH) == calDateRightNow.get(Calendar.MONTH) && 
						   cal.get(Calendar.DAY_OF_MONTH) == daySelected){
					   
					 String extraData = cal.get(Calendar.HOUR_OF_DAY) + ":" +  cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);  
					 
				     Statistics statistics = new Statistics(rsDay.getInt("id"), 
				    		 							rsDay.getInt("memory"), 
				    		 							rsDay.getInt("cpu"), 
				    		                           date,
				    								   cal.get(Calendar.YEAR),
				    								   cal.get(Calendar.MONTH) + 1,
				    								   cal.get(Calendar.DAY_OF_MONTH)
				    		                           );
				     
				     statistics.setExtraData(extraData);
				     listCompleteOfTheDay.add(statistics);
				     
				     //System.out.println("Day " + new Gson().toJson(date));
				     
				   }
			   }
			   
			   
			   
			   rsDay.close();
			   statementDay.close();
			   
		   }
		   
		   if(listCompleteOfTheDay != null){
				   listComplete = new ArrayList<>();
				   listComplete = orderById(listCompleteOfTheDay); 
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
	
	private static List<Statistics> orderById(List<Statistics> listDisOrdered){
		
		List<Statistics> listComplete = new ArrayList<>();
		Statistics[] arrayDisOrdered = new Statistics[listDisOrdered.size()];
		
		int v = 0;
		for(Statistics s : listDisOrdered){
			arrayDisOrdered[v] = s;
			v++;
		}
		
		for(int i = 0; i < arrayDisOrdered.length - 1; i++){
			
			for(int j = i+1; j < arrayDisOrdered.length; j++){
				
				if(arrayDisOrdered[i].getId() > arrayDisOrdered[j].getId()){
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
