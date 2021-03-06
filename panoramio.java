package com.svail.crawl.panoramio;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.svail.util.FileTool;

public class Panoramio_mode_2{
	// http://www.panoramio.com/map/get_panoramas.php?set=full&from=0&to=500&minx=9.74761962890625&miny=9.381322272728047&maxx=17.98736572265625&maxy=11.832406267156314&size=medium&mapfilter=false
	// 首先抓取非洲地区的图片
	public static double stepy = 0.25;
	public static double stepx = 0.25;
	
	//将获得的文摘数据存入digistFileName对应的文件中
	public static String digistFileName = "/home/gir/crawldata/googlephoto/0331/Panoramio.txt";
	
	//将获取文摘失败的链接存入cellUrlError对应的文件中
	public static String cellUrlError = "/home/gir/crawldata/googlephoto/0331/Panoramio-url.txt";
	
	//如果图片抓取失败，则将图片的文摘放入poiError.txt文件中
	public static String poiError = "/home/gir/crawldata/googlephoto/0331/Panoramio-error.txt";
	
	//将已经被抓取过的网格的编码写入logFile中
	public static String logFile = "/home/gir/crawldata/googlephoto/0331/Panoramio-log.txt";
	
	//cellLog记录更深一级网格的经纬度范围，方便下次的深一级别的抓取
	public static String cellLog = "/home/gir/crawldata/googlephoto/0331/Panoramio-cell-log.txt";
	
	//将已经抓取的id写入文件中，避免突然中断后再次抓取
	public static String finishedid="/home/gir/crawldata/googlephoto/0331/finished-id.txt";
	public static byte[] getImageFromNetByUrl(String strUrl) throws Exception{  
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        HttpGet httpget = new HttpGet(strUrl);  
        HttpResponse response = httpclient.execute(httpget);  
        HttpEntity entity = response.getEntity();  
        InputStream in = entity.getContent(); 

        byte[] btImg = readInputStream(in);//得到图片的二进制数据  
            
        httpclient.close();
        return btImg;  
          
    }  
    /** 
     * 从输入流中获取数据 
     * @param inStream 输入流 
     * @return 
     * @throws Exception 
     */  
    public static byte[] readInputStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = 0;  
        while( (len=inStream.read(buffer)) != -1 ){  
            outStream.write(buffer, 0, len);  
        }  
        inStream.close();  
        return outStream.toByteArray();  
    }  
	public static void archive(GeoPhoto photo, GridFS grid) throws Exception { 
		//全图
        byte[] bs = getImageFromNetByUrl("http://static.panoramio.com.storage.googleapis.com/photos/large/" + photo.getPhoto_id() + ".jpg");
        
        //缩略图
        byte[] square = getImageFromNetByUrl("http://static.panoramio.com.storage.googleapis.com/photos/square/" + photo.getPhoto_id() + ".jpg");
        	
        GridFSFile document = grid.createFile(bs);   
        document.put("photo_id", photo.getPhoto_id());
        document.put("photo_title", photo.getPhoto_title());
        document.put("photo_url", photo.getPhoto_url());
        document.put("photo_file_url", "http://static.panoramio.com.storage.googleapis.com/photos/large/" + photo.getPhoto_id() + ".jpg");
        document.put("latitude", photo.getLatitude());
        document.put("longitude", photo.getLongitude());
        document.put("width", photo.getWidth());
        document.put("height", photo.getHeight());

        document.put("owner_id", photo.getOwner_id());
        document.put("owner_name", photo.getOwner_name());
        document.put("owner_url", photo.getOwner_url());

        document.put("upload_date", photo.getUpload_date());
        document.put("square", square);
        document.save();  
    } 
	public static void digistDeeper(double left, double right, double top, double bottom, int divideNum, boolean save, int nextDivideNum)
	{
		Gson gson = new Gson();
		double deltax = (right - left) / divideNum;
		double deltay = (top - bottom) / divideNum;
		
		for (double x = left; x < right; x += deltax) {
			for (double y = top; y > bottom; y -= deltax) {
				String url = "http://www.panoramio.com/map/get_panoramas.php?set=full&from=0&to=1000&minx=" + x + "&miny=" + (y - stepy) + "&maxx=" + (x + stepx) + "&maxy=" + y + "&size=medium&mapfilter=false";
				try {
					Thread.sleep(10000 * ((int) (Math
						.max(0.5, Math.random() * 3))));
				} catch (final InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				CloseableHttpClient httpclient = HttpClients.createDefault();  
				if (!save)
				{
					System.out.println("2级网格抓取: [" +  x + "," + (x + deltax) + "," + y + "," + (y -deltay) + "]");  
				}
				else
				{
					System.out.println("3级网格抓取: [" +  x + "," + (x + deltax) + "," + y + "," + (y -deltay) + "]");  
			        
				}
				try {  
		            // 创建httpget.    
		            HttpGet httpget = new HttpGet(url);  
		            // System.out.println("executing request " + httpget.getURI());  
		            // 执行get请求.    
		            CloseableHttpResponse response = httpclient.execute(httpget);  
		            
		            try {  
		                // 获取响应实体    
		                HttpEntity entity = response.getEntity();  
		                System.out.println("--------------------------------------");  
		                // 打印响应状态    
		                if (entity != null) {  
		                    // 打印响应内容长度    
		                    // 打印响应内容    
		                    String xml = EntityUtils.toString(entity);
		                    // System.out.println("Response content: " + xml); 
		                    
		                    if (xml != null)
		    				{
		                    	// 创建一个JsonParser
		    					JsonParser parser = new JsonParser();
		    			
		    					//通过JsonParser对象可以把json格式的字符串解析成一个JsonElement对象
		    					try {
		    						JsonElement el = parser.parse(xml);

		    						//把JsonElement对象转换成JsonObject
		    						JsonObject jsonObj = null;
		    						if(el.isJsonObject())
		    						{
		    							jsonObj = el.getAsJsonObject();
		    							SearchResult sr = gson.fromJson(jsonObj, SearchResult.class);
		    							System.out.println("count: " + sr.getCount() + "  photo: " + sr.getPhotos().size());	
		    							if (sr != null)
		    							{
		    								List<GeoPhoto> photos = sr.getPhotos();
		    								if (sr.getCount() > 0 && sr.getCount() > photos.size() && photos.size() > 0)
		    								{
		    									if (save)
		    										FileTool.Dump(xml, digistFileName, "utf-8");
		    									else
		    									{
		    										FileTool.Dump(xml, digistFileName, "utf-8");
		    										
		    										digistDeeper(x, x + deltax, y, y - deltay, nextDivideNum, true, 0);
		    									}
		    								}
		    								else if (sr.getCount() > 0 && photos.size() > 0)
		    									FileTool.Dump(xml, digistFileName, "utf-8");
		    									
		    							}
		    						}
		    					}catch (JsonSyntaxException e) {
		    						// TODO Auto-generated catch block
		    						e.printStackTrace();
		    					}
		    				}
		                    else
		                    {
		                    	FileTool.Dump(url, cellUrlError, "utf-8");
		                    }
		                }  
		            } finally {  
		                response.close();  
		            }  
		        } catch (ClientProtocolException e) {  
		            e.printStackTrace();  
		            FileTool.Dump(url, cellUrlError, "utf-8");
		        } catch (ParseException e) {  
		            e.printStackTrace();  
		            FileTool.Dump(url, cellUrlError, "utf-8");
		        } catch (IOException e) {  
		            e.printStackTrace();  
		            FileTool.Dump(url, cellUrlError, "utf-8");
		        } finally {  
		            // 关闭连接,释放资源    
		            try {  
		                httpclient.close();  
		            } catch (IOException e) {  
		                e.printStackTrace();  
		            }  
		        }  
			}
		}
	}
	
	public static void digistCells() {
		Vector<String> ls = FileTool.Load(cellLog, "utf-8");
		
		if (ls != null){
			for (int n = 0; n < ls.size(); n ++ ) {
				
				String tks [] = ls.get(n).split(",");
				
				if (tks.length == 4) {
					double top = Double.parseDouble(tks[0]);
					double bottom = Double.parseDouble(tks[1]);
					double left = Double.parseDouble(tks[2]);
					double right = Double.parseDouble(tks[3]);
					
					digistDeeper(left, right, top, bottom, 2, false, 10);
				}
			}
		}
		
	}
	public static void digist(double top, double bottom, double left, double right)
	{
		//double top = 16.467695, left = -15.908203;
		//double bottom = 9.058702, right = 10.59082;
		// 4.302591 7.646484
		// -11,781325 43,417969
		// double top = 15.029686, left = 9.228510646484;
		// double bottom = -4.477856, right = 39.023438;		
		Gson gson = new Gson();
		
		for (double x = left; x < right; x += stepx) {
			for (double y = top; y > bottom; y -= stepy) {
				String url = "http://www.panoramio.com/map/get_panoramas.php?set=full&from=0&to=1000&minx=" + x + "&miny=" + (y - stepy) + "&maxx=" + (x + stepx) + "&maxy=" + y + "&size=medium&mapfilter=false";
				try {
					Thread.sleep(10000 * ((int) (Math
						.max(0.5, Math.random() * 3))));
				} catch (final InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				CloseableHttpClient httpclient = HttpClients.createDefault();  
				System.out.println("1级网格抓取: [" +  x + "," + (x + stepx) + "," + y + "," + (y -stepy) + "]");  
	            
				try {  
					
					// 创建httpget.    
		            HttpGet httpget = new HttpGet(url);  
		            // System.out.println("executing request " + httpget.getURI());  
		            // 执行get请求.    
		            CloseableHttpResponse response = httpclient.execute(httpget);  
		            
		            try {  
		                // 获取响应实体    
		                HttpEntity entity = response.getEntity();  
		                // 打印响应状态    
		                if (entity != null) {  
		                    // 打印响应内容长度    
		                    // 打印响应内容    
		                    String xml = EntityUtils.toString(entity);
		                    // System.out.println("Response content: " + xml); 
		                    System.out.println("--------------------------------------");  
			                
		                    if (xml != null)
		    				{
		                    	// 创建一个JsonParser
		    					JsonParser parser = new JsonParser();
		    			
		    					//通过JsonParser对象可以把json格式的字符串解析成一个JsonElement对象
		    					try {
		    						JsonElement el = parser.parse(xml);

		    						//把JsonElement对象转换成JsonObject
		    						JsonObject jsonObj = null;
		    						if(el.isJsonObject())
		    						{
		    							jsonObj = el.getAsJsonObject();
		    							SearchResult sr = gson.fromJson(jsonObj, SearchResult.class);
		    							System.out.println("count: " + sr.getCount() + "  photo: " + sr.getPhotos().size());
		    							if (sr != null)
		    							{
		    								List<GeoPhoto> photos = sr.getPhotos();
		    								if (sr.getCount() > 0 && sr.getCount() > photos.size() && photos.size() > 0)
		    								{
		    									FileTool.Dump(xml, digistFileName, "utf-8");
		    									
		    									/*  此时不抓取细分网格, 仅记录该1级网格, 以供未来抓取 */
		    									// digistDeeper(x, x + stepx, y, y - stepy, 5, false, 10);
		    									
		    									//cellLog记录更深一级网格的经纬度范围，方便下次的深一级别的抓取
		    									FileTool.Dump("" + x + "," + (x + stepx) + "," + y + "," + (y -stepy) , cellLog, "utf-8");
		    								}
		    								else if (sr.getCount() > 0 && photos.size() > 0)
		    									FileTool.Dump(xml, digistFileName, "utf-8");
		    							}
		    						}
		    					}catch (JsonSyntaxException e) {
		    						// TODO Auto-generated catch block
		    						e.printStackTrace();
		    					}
		    				}
		                    else
		                    {
		                    	FileTool.Dump(url, cellUrlError, "utf-8");
		                    }
		                }  
		                
		            } finally {  
		                response.close();  
		            }  
		        } catch (ClientProtocolException e) {  
		            e.printStackTrace();  
		            FileTool.Dump(url, cellUrlError, "utf-8");
		        } catch (ParseException e) {  
		            e.printStackTrace();  
		            FileTool.Dump(url, cellUrlError, "utf-8");
		        } catch (IOException e) {  
		            e.printStackTrace();  
		            FileTool.Dump(url, cellUrlError, "utf-8");
		        } finally {  
		            // 关闭连接,释放资源    
		            try {  
		                httpclient.close();  
		            } catch (IOException e) {  
		                e.printStackTrace();  
		            }  
		        }  
			}
		}
	}
	
	public static void fetchData(String digist) {//导入文摘数据
		try {
			Mongo mongo = new Mongo("192.168.6.9", 27017);
			DB db = mongo.getDB("geophoto");  // 数据库名称
			GridFS grid = new GridFS(db);
			Gson gson = new Gson();
			
			Vector<String> ls = FileTool.Load(digist, "utf-8");
			Vector<String> visits = FileTool.Load(finishedid, "utf-8");
			Set<Integer> vis = new TreeSet<Integer>();
			
			// 假如finished-id.txt文件中有数据，将其加到vis中去,该文件存储了图片的id
			if (visits != null) {
				for (int i = 0; i < visits.size(); i++) {
					int v = Integer.parseInt(visits.get(i));
					vis.add(v);
				}
			}
			
			if (ls != null)
			{
				for (int n = 0; n < ls.size(); n ++)
				{
					JsonParser parser = new JsonParser();
			    	//通过JsonParser对象可以把json格式的字符串解析成一个JsonElement对象
			    	try {
			    		JsonElement el = parser.parse(ls.get(n));
			    		//把JsonElement对象转换成JsonObject
			    		JsonObject jsonObj = null;
			    		if(el.isJsonObject())
			    		{
			    			jsonObj = el.getAsJsonObject();
			    			SearchResult sr = gson.fromJson(jsonObj, SearchResult.class);

			    			if (sr != null)
			    			{
			    				List<GeoPhoto> photos = sr.getPhotos();
			    				
			    				for (int m = 0; m < photos.size(); m ++) {
			    					GeoPhoto photo = photos.get(m);
			    					// 将该信息写入到mongodb数据库
			    					DBObject dbo = new BasicDBObject();
			    					
			    					dbo.put("photo_id", photo.getPhoto_id());
			    					
			    					List<GridFSDBFile> rls = grid.find(dbo);
			    					if (rls == null || rls.size() == 0)
			    					{
			    						try {
			    							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			    							
			    							//假设id并没有在已经访问过的id名单内，则执行图片抓取程序，否则不执行
			    							if(!vis.contains(photo.getPhoto_id())){
			    								System.out.println("@by " + df.format(new Date()) + "   [" + n + "-" + m + "]");
				    							
					    						archive(photo, grid);
					    						//将已经抓取的id写入文件中，避免突然中断后再次抓取
						    					FileTool.Dump(photo.getPhoto_id(), finishedid, "utf-8");
			    							}
			    							
					    					
				    					} catch (java.lang.NullPointerException e1) {
				    						// TODO Auto-generated catch block
				    						e1.printStackTrace();
				    						FileTool.Dump(photo.toString(), poiError, "utf-8");
				    					} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
											FileTool.Dump(photo.toString(), poiError, "utf-8");
										}
			    						try {
				    						Thread.sleep(2000 * ((int) (Math
				    							.max(0.1, Math.abs(Math.sin(Math.random()) * 5)))));
				    					} catch (final InterruptedException e1) {
				    						// TODO Auto-generated catch block
				    						e1.printStackTrace();
				    					}
			    					}
			    				}
			    			}
			    		}
			    	}catch (JsonSyntaxException e) {
			    		// TODO Auto-generated catch block
			    		e.printStackTrace();
			    	}
			    }
			 }
			 
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (MongoException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} 
	     
	}
	
	public static void setBoundary(String folder) {
		Map<String, String> map = new HashMap<String, String>();
		Vector<String> boundary = FileTool.Load(folder, "utf-8");
		Vector<String> visits = FileTool.Load(logFile, "utf-8");
		Set<Integer> vis = new TreeSet<Integer>();
		
		//假如logFile文件中有已经访问过的网格，将其加到vis中去
		if (visits != null)
		{
			for (int i = 0; i < visits.size(); i ++) {
				int v = Integer.parseInt(visits.get(i));
				vis.add(v);
			}
		}
		
		boolean server_one = true;
		int start = 0;
		int end = boundary.size() / 2;
		
		if (!server_one) {
			start = end;
			end = boundary.size();
		}
		
		List<Integer> list = new ArrayList<Integer>();
        
		for (int i = start; i < end; i++) {
			String poi = boundary.elementAt(i);
			String[] grid = poi.split(",");
			int v = Integer.parseInt(grid[0]);
			
			//如果vis中没有包含该网格，则在list中加入i
			if (!vis.contains(v))
			{
				list.add(i);
				// map.put(grid[0], grid[1] + "," + grid[2] + "," + grid[3] + "," + grid[4]);
			}
        }
		
		Collections.shuffle(list);  //shuffle（） 是 Collections 中的静态方法 ，它用于将一个 list 集合中的元素顺序进行打乱 ，类似于洗牌的过程
        Iterator<Integer> ite = list.iterator();  
        
        while (ite.hasNext()) {
        	
        	int v = ite.next();
            System.out.println("0级网格抓取: " +  v);  
            String poi = boundary.elementAt(v);
			String[] grid = poi.split(",");
            
            double Top = Double.parseDouble(grid[1]);
			double Bottom = Double.parseDouble(grid[2]);
			double Left = Double.parseDouble(grid[3]);
			double Right = Double.parseDouble(grid[4]);
			
			digist(Top, Bottom, Left, Right);
			//将已经被抓取过的网格的编码计入logFile中
			FileTool.Dump(grid[0], logFile, "UTF-8");
        }

	}
	public static void main(String[] args) throws Exception {
		int mode = 1; // 1  	抓取摘要
		              // 2  	抓取实际数据
		
		if (mode == 1) 
		{
			setBoundary("config/littlegrid.txt");
		}else if(mode == 2){
			fetchData("/home/gir/crawldata/googlephoto/0331/Panoramio-digist.txt");
		}
			
	}	
}
