package com.count2v.coolist.client;

public class ConnectionStatus<T> {	
	private T element;
	private int status;
	
	
	public ConnectionStatus(T element, int status) {
		this.element = element;
		this.status = status;
	}
	
	public boolean isConnectionSuccess() {
		return status == StatusType.CONNECTION_SUCCESS;
	}
	
	public int getStatus() {
		return status;
	}
	
	public T getElement() {
		return element;
	}
	
	@Override
	public String toString() {
		return "ConnectionStatus: " + status + ", element: " + element;
	}

}
