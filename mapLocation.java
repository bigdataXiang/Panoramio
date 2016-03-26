package com.svail.crawl.panoramio;


public class MapLocation {
	private double  lat;
	private double  lon;
	private int panoramio_zoom;
	public MapLocation()
	{
		super();
	}
	public MapLocation(double lat, double lon, int panoramio_zoom)
	{
		super();			
		this.lat = lat;
		this.lon = lon;
		this.panoramio_zoom = panoramio_zoom;
	}
	
	public String toString() {
		
		String ct =  "{lat:" + this.lat + ", lon:" + this.lon
		+ ", panoramio_zoom:" + this.panoramio_zoom;
		
		ct += "}";
		return ct;
	}
	
	public void setLat( double lat ) {
		this.lat = lat;
	}

	public double getLat() {
		return this.lat;
	}
	
	public void setLon( double lon ) {
		this.lon = lon;
	}

	public double getLon() {
		return this.lon;
	}
	
	public void setPanoramio_zoom( int panoramio_zoom ) {
		this.panoramio_zoom = panoramio_zoom;
	}

	public int getPanoramio_zoom() {
		return this.panoramio_zoom;
	}
	
}
