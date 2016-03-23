package com.statistics.machine;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainDelete {
	
		//local PC
		//static final String nameDirectory = "/home/hendry/Escritorio/pruebaFiles";
		
		// Cloud Server
		static final String nameDirectory = "/var/log/";

		public static void deleteAllOldFiles() throws Exception {
			
			File directory = new File(nameDirectory);
			String[] listOfFiles = null;
			
			if(directory.exists())
				listOfFiles = directory.list();
			
			List<String> candidatesToDie = null;
			
			if (listOfFiles == null){
				  System.out.println("There are not files in Directory");
			}else { 
				
				candidatesToDie = new ArrayList<String>();
				
				  for (int x=0;x<listOfFiles.length;x++){
					  
					  if(listOfFiles[x].contains("fermat_server.log")){
						  candidatesToDie.add(listOfFiles[x].trim());
					  }
				  }
				  
			}
			
			if(candidatesToDie != null){
				
				System.out.println("****************** DIE WILL *****************************");
				
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date todayis = new Date();
				String mainFileToNotDelete = "fermat_server.log";
				String todayFileToNotDelete = "fermat_server.log." + dateFormat.format(todayis);
				
				for(String nameFile : candidatesToDie){
					
					if(!nameFile.equalsIgnoreCase(mainFileToNotDelete) && !nameFile.equalsIgnoreCase(todayFileToNotDelete))
						deleteFileSure(nameFile);
		
				}
				
				System.out.println("********************************************************");
				
			}
			

		}
		
		
		private static void deleteFileSure(String nameFileToDie) throws Exception {
			
			//System.out.println( nameFileToDie);
			
			File file = new File(nameDirectory + "/" + nameFileToDie);
			
			if(file.exists() && file.isFile() && nameFileToDie.contains("fermat_server.log")){
				//descoment this line before upload
			    //file.delete(); 
				System.out.println(file.getName() + " is deleted!");
			}else{
				System.out.println("Delete operation is failed.");
			}
			
		}
		

}
