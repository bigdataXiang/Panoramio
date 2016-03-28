package com.svail.crawl.panoramio;

import java.util.ArrayList;
import java.util.Vector;
import java.math.BigDecimal; 
import com.svail.util.FileTool;

public class SetGrid {
	public static void main(String[] args){
		setgrid("D:/Panomario/boundary.txt",1);
	}
	/**
	 * 将每个扫描区域定义成一个grid
	 */
	public static ArrayList<Grid> grid= new ArrayList<Grid>();

	/**
	 * 对每个grid进行数据的填充
	 */
	public static void addGrid(Grid gd) {
		grid.add(gd);

	}
	public static void setgrid(String folder,int scale){
		Vector<String> gridpoi=FileTool.Load(folder, "utf-8");
		for(int i=0;i<1;i++){//gridpoi.size()
			String country=gridpoi.elementAt(i);
			String[] arr=country.split(",");
			String Name=arr[0];
			double Top=Double.parseDouble(arr[1]);
			double Bottom=Double.parseDouble(arr[2]);
			double Left=Double.parseDouble(arr[3]);
			double Right=Double.parseDouble(arr[4]);
			
			System.out.println(Name+":"+Top+","+Bottom+","+Left+","+Right);
			double length=Right-Left;
			double width=Top-Bottom;
			//ceil()：将小数部分一律向整数部分进位。 floor()：一律舍去，仅保留整数。   round()：进行四舍五入。
			int row=(int) Math.ceil(width/scale);
			int column=(int) Math.ceil(length/scale);
			System.out.println("共有"+row+"行"+column+"列"+column*row+"个格子");
			
			double wsurplus=width-(int) Math.floor(width/scale)*scale;
			double lsurplus=length-(int) Math.floor(length/scale)*scale;
			double top=0;
			double bottom=0;
			double left=0;
			double right=0;
			long code=0;
			for(int cc=1;cc<=column;cc++){
				for(int rr=1;rr<=row;rr++){
					Grid gd=new Grid();
					if(cc==1&&rr==1){
						top=Top;
						gd.setTop(top);
						left=Left;
						gd.setLeft(left);
						bottom=top-scale;
						gd.setBottom(bottom);
						right=Left+scale;
						gd.setRight(right);
						code++;
						gd.setCode(code);
						
						addGrid(gd);
						
					}else if(cc!=1&&cc!=column&&rr==1){
						top=Top;
						gd.setTop(top);
						bottom=top-scale;
						gd.setBottom(bottom);
						left=right;
						gd.setLeft(left);
						right=left+scale;
						gd.setRight(right);
						code++;
						gd.setCode(code);
						
						addGrid(gd);
						
					}else if(cc==column&&rr==1){
						top=Top;
						gd.setTop(top);
						bottom=top-scale;
						gd.setBottom(bottom);
						left=right;
						gd.setLeft(left);
						right=left+lsurplus; //加那一行多出来的部分
						gd.setRight(right);
						code++;
						gd.setCode(code);
						
						addGrid(gd);
						
					}else if(cc==column&&rr!=1&&rr!=row){
						top=bottom;
						gd.setTop(top);
						bottom=top-scale;
						gd.setBottom(bottom);

						gd.setLeft(left);
						gd.setRight(right);//左右经纬度不变
						code++;
						gd.setCode(code);
						
						addGrid(gd);
					}else if(cc==column&&rr==row){
						top=bottom;
						gd.setTop(top);
						bottom=top-wsurplus;//减那一列多出来的部分
						gd.setBottom(bottom);

						gd.setLeft(left);
						gd.setRight(right);//左右经纬度不变
						code++;
						gd.setCode(code);
						
						addGrid(gd);
						
					}else if(rr==row&&cc!=column){
						top=bottom;
						gd.setTop(top);
						bottom=top-wsurplus;//减那一列多出来的部分
						gd.setBottom(bottom);
						
						gd.setLeft(left);//left不变
						right=left+scale;
						gd.setRight(right);
						code++;
						gd.setCode(code);
						
						addGrid(gd);
						
						
					}else{
						top=bottom;
						gd.setTop(top);
						bottom=top-scale;
						gd.setBottom(bottom);
						
						gd.setLeft(left);
						gd.setRight(right);
						code++;
						gd.setCode(code);
						
						addGrid(gd);
					}
				}
				
			}			
		}
		
		for(int g=0;g<grid.size();g++){
			System.out.println(grid.get(g).code+":"+grid.get(g).top+","+grid.get(g).bottom+","+grid.get(g).left+","+grid.get(g).right);
		}
	}

}
