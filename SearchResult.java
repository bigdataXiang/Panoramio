package com.svail.crawl.panoramio;

import java.util.List;

public class SearchResult {
	private int count;
	private boolean has_more;
	private MapLocation map_location;
	private List<GeoPhoto> photos;
    public SearchResult()
	{
		super();
	}
	public SearchResult(int count, boolean has_more, MapLocation map_location, List<GeoPhoto> photos)
	{
		super();			
		this.count = count;
		this.has_more = has_more;
		this.map_location = map_location;
		this.photos = photos;
	}
	
	public String toString() {
		
		String ct =  "{count:" + this.count + ", has_more:" + this.has_more
		+ ", map_location:" + this.map_location;
		
		if (this.photos != null && this.photos.size() > 0)
		{
			ct += ",photos:[";
			ct += photos.get(0);
			for (int n = 1; n < photos.size(); n ++)
			{
				ct += "," + photos.get(n);
			}
			ct += "]";
		}
		ct += "}";
		return ct;
	}
	
	public void setCount( int count ) {
		this.count = count;
	}

	public int getCount() {
		return this.count;
	}
	
	public void setHas_more( boolean has_more ) {
		this.has_more = has_more;
	}

	public boolean getHas_more() {
		return this.has_more;
	}
	
	public void setMap_location( MapLocation map_location ) {
		this.map_location = map_location;
	}

	public MapLocation getMap_location() {
		return this.map_location;
	}
	
	public void setPhotos( List<GeoPhoto> photos ) {
		this.photos = photos;
	}

	public List<GeoPhoto> getPhotos() {
		return this.photos;
	}
}
