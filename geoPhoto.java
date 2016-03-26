package com.svail.crawl.panoramio;

public class GeoPhoto {
	//{"height":375,"latitude":10.519026999999999,"longitude":13.589326,"owner_id":1270976,
	// "owner_name":"maremagna","owner_url":"http://www.panoramio.com/user/1270976",
	// "photo_file_url":"http://mw2.google.com/mw-panoramio/photos/medium/9116484.jpg","photo_id":9116484,
	// "photo_title":"Picchi di Rhumsiki","photo_url":"http://www.panoramio.com/photo/9116484",
	// "upload_date":"05 April 2008","width":500
	private int height;
	private int width;
	private double latitude;
	private double longitude;
	private int owner_id;
	private String owner_name;
	private String owner_url;
	private String photo_file_url;
	private int photo_id;
	private String photo_title;
	private String photo_url;
	private String upload_date;	
	
	public GeoPhoto()
	{
		super();
	}
	public GeoPhoto(int photo_id, String photo_title, String photo_url, String photo_file_url, int height, int width, 
			double latitude, double longitude, int owner_id, String owner_name,
			String owner_url, String upload_date)
	{
		super();			
		this.height = height;
		this.width = width;
		this.latitude = latitude;		
		this.longitude = longitude;	
		
		this.owner_id = owner_id;
		this.owner_name = owner_name;
		this.owner_url = owner_url;
		
		this.photo_file_url = photo_file_url;
		this.photo_id = photo_id;
		this.photo_title = photo_title;
		this.photo_url = photo_url;
		this.upload_date = upload_date;
	}
	
	public String toString() {
		
		String ct =  "{photo_id:" + this.photo_id + ", photo_title:" + this.photo_title
		+ ", photo_url:" + this.photo_url + ", photo_file_url:" + this.photo_file_url
		+ ", height:" + this.height + ", width:" + this.width
		+ ", latitude:" + this.latitude + ", longitude:" + this.longitude
		+ ", owner_id:" + this.owner_id + ", owner_name:" + this.owner_name
		+ ", owner_url:" + this.owner_url + ", upload_date:" + this.upload_date;
		
		ct += "}";
		return ct;
	}
	
	public void setHeight( int height ) {
		this.height = height;
	}

	public int getHeight() {
		return this.height;
	}
	
	public void setWidth( int width ) {
		this.width = width;
	}

	public int getWidth() {
		return this.width;
	}
	
	public void setLatitude( double latitude ) {
		this.latitude = latitude;
	}

	public double getLatitude() {
		return this.latitude;
	}
	
	public void setLongitude( double longitude ) {
		this.longitude = longitude;
	}

	public double getLongitude() {
		return this.longitude;
	}
	
	public void setOwner_id( int owner_id ) {
		this.owner_id = owner_id;
	}

	public int getOwner_id() {
		return this.owner_id;
	}
	
	public void setOwner_name( String owner_name ) {
		this.owner_name = owner_name;
	}

	public String getOwner_name() {
		return this.owner_name;
	}
	
	public void setOwner_url( String owner_url ) {
		this.owner_url = owner_url;
	}

	public String getOwner_url() {
		return this.owner_url;
	}
	
	public void setPhoto_file_url( String photo_file_url ) {
		this.photo_file_url = photo_file_url;
	}

	public String getPhoto_file_url() {
		return this.photo_file_url;
	}
	
	public void setPhoto_id( int photo_id ) {
		this.photo_id = photo_id;
	}

	public int getPhoto_id() {
		return this.photo_id;
	}

	public void setPhoto_title( String photo_title ) {
		this.photo_title = photo_title;
	}

	public String getPhoto_title() {
		return this.photo_title;
	}

	public void setPhoto_url( String photo_url ) {
		this.photo_url = photo_url;
	}

	public String getPhoto_url() {
		return this.photo_url;
	}

	public void setUpload_date( String upload_date ) {
		this.upload_date = upload_date;
	}

	public String getUpload_date() {
		return this.upload_date;
	}
}
