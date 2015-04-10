package edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.model;

public class CalendappError {
	private int status;
	private String message;
 
	public CalendappError() {
		super();
	}
 
	public CalendappError(int status, String message) {
		super();
		this.status = status;
		this.message = message;
	}
 
	public int getStatus() {
		return status;
	}
 
	public void setStatus(int status) {
		this.status = status;
	}
 
	public String getMessage() {
		return message;
	}
 
	public void setMessage(String message) {
		this.message = message;
	}
}