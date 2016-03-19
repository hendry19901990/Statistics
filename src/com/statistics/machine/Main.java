package com.statistics.machine;

import java.util.concurrent.TimeUnit;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;

public class Main {
	
	private static DatabaseStatistics databaseStatistics;

	/*
	 * Run like this:
	 * java -Djava.library.path=lib -jar estadistica.jar
	 */
	public static void main(String[] args) throws Exception  {
		
		databaseStatistics = new DatabaseStatistics();
		databaseStatistics.crearDatabase();
		
		/* only test, it should be commented when is in production */
		int i = 0;
		while(i < 5 ){
			TimeUnit.SECONDS.sleep(2);
			monitoringProcess();
			i++;
		}
		/* only test, it should be commented when is in production */
		
		databaseStatistics.consultaTest();
		

	}
	
	private static void monitoringProcess() throws Exception {
		
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
