package com.svail.crawl.panoramio;

public class Grid {
	public long code;
	public double top;
	public double bottom;
	public double left;
	public double right;
     
	public void setCode(long code){
		this.code=code;
	}
	public long getCode(){
		return this.code;
	}
	
	public void setTop(double top){
		this.top=top;
	}
	public double getTop(){
		return this.top;
	}
	
	public void setBottom(double bottom){
		this.bottom=bottom;
	}
	public double getBottom(){
		return this.bottom;
	}
	
	public void setLeft(double left){
		this.left=left;
	}
	public double getLeft(){
		return this.left;
	}
	
	public void setRight(double right){
	    this.right=right;
	}
	public double getRight(){
		return this.right;
	}
}
