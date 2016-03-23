package com.statistics.machine;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;

import com.statistics.jetty.JettyEmbeddedAppServer;

public class Main {
	
	private static DatabaseStatistics databaseStatistics;

	/*
	 * Run like this:
	 * java -Djava.library.path=lib -jar estadistica.jar
	 */
	public static void main(String[] args) throws Exception  {
		
		ExecutorService executorService  =  Executors.newFixedThreadPool(2);
		
		databaseStatistics = new DatabaseStatistics();
		databaseStatistics.crearDatabase();
		
		Runnable threadEmbeddedAppServer = new Runnable() {
			 @Override
		        public void run() {
					JettyEmbeddedAppServer jettyEmbeddedAppServer = new JettyEmbeddedAppServer();
					try {
						jettyEmbeddedAppServer.initialize();
					} catch (Exception e) {
					}
			 }
		};
		
		Runnable threadTimer = new Runnable() {
		
			@Override
	        public void run() {
				Timer timer;
				timer = new Timer();
				
				TimerTask task = new TimerTask() {
				     @Override
				     public void run()
				     {
						 try {
							monitoringProcess();
						 } catch (Exception e) {
						 }
					  }
				};
				 
				timer.schedule(task, 0, 5000);
			}
		 
		};
		 
		 executorService.submit(threadEmbeddedAppServer);
		 executorService.submit(threadTimer);
	 
		
		
		/* only test, it should be commented when is in production */
		//databaseStatistics.consultaTest();
		/* only test, it should be commented when is in production */
		

	}
	
	private static void monitoringProcess() throws Exception {
		
		System.out.println("Executing Process");
		
		Sigar sigar = new Sigar();
		final Mem mem = sigar.getMem(); // Memory
		CpuPerc cpuPerc = sigar.getCpuPerc(); // Percentage CPU
		//Swap swap = sigar.getSwap(); // Swap Space
		
		int memoryActual = (mem != null) ? (int) mem.getUsedPercent() : 0;
		int cpuActual = (cpuPerc != null) ? (int)(cpuPerc.getCombined() * 100) : 0; 
		
		System.out.println("Memory " + memoryActual);
		System.out.println("CPU % " + cpuActual);
		//System.out.println("CPU " + cpuPerc);
				
		databaseStatistics.saveNewPorcentOfMachine(memoryActual, cpuActual);
		
		FileSystem [] fileSystemList = sigar.getFileSystemList();
		FileSystemUsage usage = null;
				
		for(FileSystem f : fileSystemList){
			 if (f.getType() == FileSystem.TYPE_LOCAL_DISK){
				 usage =  sigar.getFileSystemUsage(f.getDirName());
			 }
		}
		
		if(usage != null){
			
			long percentDisk = (usage.getTotal() > 0) ? (usage.getUsed() * 100) / usage.getTotal() : 0;

			System.out.println(" Disk Usage " + percentDisk);
		  
		   /*
		    * execute clear old logs
		    */
			if(percentDisk > 50){
				MainDelete.deleteAllOldFiles();
			}
		  
		}
		
	}

}
