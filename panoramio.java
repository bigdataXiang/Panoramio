package com.svail.crawl.panoramio;

import java.util.List;

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
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSFile;
import com.svail.util.FileTool;

public class Panoramio {
	// http://www.panoramio.com/map/get_panoramas.php?set=full&from=0&to=500&minx=9.74761962890625&miny=9.381322272728047&maxx=17.98736572265625&maxy=11.832406267156314&size=medium&mapfilter=false
	// 首先抓取非洲地区的图片
	public static double stepy = 0.5;
	public static double stepx = 0.5;
	public static byte[] getImageFromNetByUrl(String strUrl){  
        try {  
        	CloseableHttpClient httpclient = HttpClients.createDefault();  
            HttpGet httpget = new HttpGet(strUrl);  
            HttpResponse response = httpclient.execute(httpget);  
            HttpEntity entity = response.getEntity();  
            InputStream in = entity.getContent(); 

            byte[] btImg = readInputStream(in);//得到图片的二进制数据  
            
            httpclient.close();
            return btImg;  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
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
	public static void archive(GeoPhoto photo, GridFS grid) {   
        byte[] bs = getImageFromNetByUrl("http://static.panoramio.com.storage.googleapis.com/photos/large/" + photo.getPhoto_id() + ".jpg");
        
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
	public static void getPhotoID(){
		
	}
	
	public static void main(String[] args) throws Exception {

		Mongo mongo = new Mongo("192.168.6.29", 27017); 
	    DB db = mongo.getDB("geophoto");  // 数据库名称 
	    GridFS grid = new GridFS(db );   
	    
		double top = 16.467695, left = -15.908203;
		double bottom = 9.058702, right = 10.59082;
		Gson gson = new Gson();
		
		for (double x = left; x < right; x += stepx) {
			for (double y = top; y > bottom; y -= stepy) {
				String url = "http://www.panoramio.com/map/get_panoramas.php?set=full&from=0&to=500&minx=" + x + "&miny=" + (y - stepy) + "&maxx=" + (x + stepx) + "&maxy=" + y + "&size=medium&mapfilter=false";
				try {
					Thread.sleep(10000 * ((int) (Math
						.max(1, Math.random() * 3))));
				} catch (final InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				CloseableHttpClient httpclient = HttpClients.createDefault();  
		        
				try {  
		            // 创建httpget.    
		            HttpGet httpget = new HttpGet(url);  
		            System.out.println("executing request " + httpget.getURI());  
		            // 执行get请求.    
		            CloseableHttpResponse response = httpclient.execute(httpget);  
		            
		            try {  
		                // 获取响应实体    
		                HttpEntity entity = response.getEntity();  
		                System.out.println("--------------------------------------");  
		                // 打印响应状态    
		                if (entity != null) {  
		                    // 打印响应内容长度    
		                    System.out.println("Response content length: " + entity.getContentLength());  
		                    // 打印响应内容    
		                    String xml = EntityUtils.toString(entity);
		                    System.out.println("Response content: " + xml); 
		                    
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

		    							if (sr != null)
		    							{
		    								List<GeoPhoto> photos = sr.getPhotos();
		    								
		    								for (int n = 0; n < photos.size(); n ++) {
		    									GeoPhoto photo = photos.get(n);
		    									
//在此处修改，先存Photo_id()，再用Photo_id()得到图片的链接  
		    									FileTool.Dump(photos.get(n).getPhoto_id(), "", "utf-8");
		    									
		    									
		    									
		    									/*将该信息写入到mongodb数据库
		    									 * 暂时先不执行这一步，先只获取图片的id；
		    									 */
		    								//	archive(photo, grid);
		    									
		    									try {
		    										Thread.sleep(2000 * ((int) (Math
		    											.max(1, Math.random() * 3))));
		    									} catch (final InterruptedException e1) {
		    										// TODO Auto-generated catch block
		    										e1.printStackTrace();
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
		                System.out.println("------------------------------------");  
		            } finally {  
		                response.close();  
		            }  
		        } catch (ClientProtocolException e) {  
		            e.printStackTrace();  
		        } catch (ParseException e) {  
		            e.printStackTrace();  
		        } catch (IOException e) {  
		            e.printStackTrace();  
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
}
