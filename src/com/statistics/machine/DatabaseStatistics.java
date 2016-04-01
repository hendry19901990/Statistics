package com.statistics.machine;

import java.io.File;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DatabaseStatistics {
	
	private Connection connection = null;
	private Statement statement = null;
		
	public Connection getConnection() {
		return connection;
	}
		
	public void crearDatabase() throws Exception{
		
		Class.forName("org.sqlite.JDBC");
		
		String directoryDatabase = System.getProperties().getProperty("user.home");
		directoryDatabase = directoryDatabase + "/externalStorage/files/";
		
		File filedirectoryDatabase = new File(directoryDatabase);
		
		if(!filedirectoryDatabase.exists())
			filedirectoryDatabase.mkdirs();
		
		 connection = DriverManager.getConnection("jdbc:sqlite:"+directoryDatabase+"databasestatistics.db");
		 
		 if(connection != null){
			 
	         statement = connection.createStatement();
	         statement.setQueryTimeout(30);  // set timeout to 30 sec.
	         
	         /* only test, it should be commented when is in production */
	         //statement.executeUpdate("drop TABLE IF EXISTS statistics");
	         /* only test, it should be commented when is in production */
	         
	         statement.executeUpdate("CREATE TABLE IF NOT EXISTS statistics (id INTEGER PRIMARY KEY AUTOINCREMENT, "
	         		+ " memory INT NOT NULL,"
	         		+ " cpu INT NOT NULL,"
	         		+ " timeActual DATETIME NOT NULL)");
	         
		 }
		
	}
	
	
	public void saveNewPorcentOfMachine(int memory, int cpu) throws Exception{
		
		if(statement != null){
			java.sql.Timestamp timeActual = new Timestamp(System.currentTimeMillis());
			statement.executeUpdate("INSERT INTO statistics (memory, cpu, timeActual) VALUES ("+memory+", "+cpu+", '"+timeActual+"')");
		}
		
	}
	
	/* only test, it should be commented when is in production */
	public void consultaTest() throws Exception{
		
	   ResultSet rs = statement.executeQuery("select * from statistics");
	   
	   
	   while(rs.next())
	   {
		   
		   DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		   Date date = df.parse(rs.getString("timeActual"));
		   Calendar cal = Calendar.getInstance();
		   cal.setTime(date);
		   
		   String completoDay =  cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH) + 1)+"-"+ cal.get(Calendar.DAY_OF_MONTH);
		     
	       System.out.println("id= " + rs.getInt("id") + " memory= " + rs.getInt("memory") + " cpu= " 
	        	+ rs.getInt("cpu") + " timeActual= " + completoDay);
	   }
	   
	   if(rs != null)
		   rs.close();
	          
	   if(connection != null)
	     connection.close();
	          
	}
	/* only test, it should be commented when is in production */

}
