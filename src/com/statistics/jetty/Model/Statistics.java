package com.statistics.jetty.Model;


import java.util.Date;

public class Statistics {
	
	private long id;
	private int memory;
	private int cpu;
	private Date timeActual;
	private int year = 0;
	private int month = 0;
	private int day = 0;
	private String extraData;
	


	public Statistics(){}
	
	public Statistics(long id, int memory, int cpu, Date timeActual){
		this.id = id;
		this.memory = memory;
		this.cpu = cpu;
		this.timeActual = timeActual;
	}
	
	public Statistics(long id, int memory, int cpu, Date timeActual, int year, int month, int day){
		this.id = id;
		this.memory = memory;
		this.cpu = cpu;
		this.timeActual = timeActual;
		this.year = year;
		this.month = month;
		this.day = day;
	}
	
	public Statistics(long id, int memory, int cpu, Date timeActual, int year, int month, int day, String extraData){
		this.id = id;
		this.memory = memory;
		this.cpu = cpu;
		this.timeActual = timeActual;
		this.year = year;
		this.month = month;
		this.day = day;
		this.extraData = extraData;
	}
	
	public String getExtraData() {
		return extraData;
	}

	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}
	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getMemory() {
		return memory;
	}
	public void setMemory(int memory) {
		this.memory = memory;
	}
	public int getCpu() {
		return cpu;
	}
	public void setCpu(int cpu) {
		this.cpu = cpu;
	}
	public Date getTimeActual() {
		return timeActual;
	}
	public void setTimeActual(Date timeActual) {
		this.timeActual = timeActual;
	}
	

}