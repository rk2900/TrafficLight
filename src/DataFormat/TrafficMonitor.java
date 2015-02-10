package DataFormat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DebugGraphics;
import javax.xml.crypto.Data;

import BasicOps.FileOps;

public class TrafficMonitor{
	protected int[][] traffic;
	
	public TrafficMonitor(TrafficMonitor traffic){
		this.traffic=new int[60][4];
		for (int i=0;i<60;i++) this.traffic[i]=traffic.traffic[i].clone();
	}
	
	public void addTraffic(String dst,String src,int v){
		addTraffic(TrafficLightMap.gettid(dst), TrafficLightMap.gettid(src), v);
	}
	
	public void addTraffic(int dst,int src,int v){
		if (v>0){
			int id=TrafficLightMap.getId(dst, src);
			if (id!=-1)
				traffic[dst][id]+=v;
		}
	}
	
	public TrafficMonitor(String data){
		if (data.contains("\t"))
			data=data.split("\t")[2];
		int sum=0;
		traffic=new int[60][4];
		for (String s:data.split(";")){
			String[] sep=s.split(",");
			addTraffic(sep[0],sep[1],Integer.valueOf(sep[2]));
		}
	}
	
	public TrafficMonitor(String data, 
			TrafficLightStatus status, TrafficMonitor prev){
		traffic=new int[60][4];
		
		if (data.contains("\t"))
			data=data.split("\t")[2];
		int sum=0;

		for (String s:data.split(";")){
			String[] sep=s.split(",");
			addTraffic(sep[0],sep[1],(int)Math.floor(Integer.valueOf(sep[2])*0.5));
		}
		
		for (int inter:TrafficLightMap.getAll()){
			int[] roads=TrafficLightMap.getIntersect(inter);
			for (int src:roads){
				Double[] initTurnRate = {0.2,0.2,0.6};
				
				if (TrafficLightMap.getNextRoad(inter, src, 1)==-1) {
					initTurnRate[1] += initTurnRate[0];
					initTurnRate[0] -= initTurnRate[0];				
				}else if (TrafficLightMap.getNextRoad(inter, src, 3)==-1) {
					initTurnRate[0] += initTurnRate[1];
					initTurnRate[1] -= initTurnRate[1];
				}else if (TrafficLightMap.getNextRoad(inter, src, 2)==-1) {
					initTurnRate[0] += initTurnRate[2]*0.5;
					initTurnRate[1] += initTurnRate[2]*0.5;
					initTurnRate[2] -= initTurnRate[2];
				}
				
				int th=20-5*TrafficLightMap.getId(inter, src);
				
				int flow=prev.getTraffic(inter,src);
				int left_through=(status.getStatus(inter, src, 1)==1)?(int)(th*initTurnRate[0]):0;
				int straight_through=(status.getStatus(inter, src, 2)==1)?(int)(th*initTurnRate[2]):0;
				int right_through=(status.getStatus(inter, src, 3)==1)?(int)(th*initTurnRate[1]):0;
				
				int left_cnt=(int)Math.floor(flow*initTurnRate[0]);
				int right_cnt=(int)Math.floor(flow*initTurnRate[1]);
				int straight_cnt=flow-left_cnt-right_cnt;
				
				int left_flow=Math.min(left_cnt, left_through);
				int straight_flow=Math.min(straight_cnt, straight_through);
				int right_flow=Math.min(right_cnt, right_through);
				
				int stay=(left_cnt+straight_cnt+right_cnt-left_flow-right_flow-straight_flow);
				
				addTraffic(inter, src, stay);
				
				addTraffic(TrafficLightMap.getNextRoad(inter,src,1), inter, left_flow);
				addTraffic(TrafficLightMap.getNextRoad(inter,src,2), inter, straight_flow);
				addTraffic(TrafficLightMap.getNextRoad(inter,src,3), inter, right_flow);
			}
		}
	}
	
	public int[] getTraffic(int dst){
		return traffic[dst];
	}
	
	public int getTraffic(int dst,int src){
		return traffic[dst][TrafficLightMap.getId(dst, src)];
	}
	
	public int sum(){
		int sum=0;
		for (int i=0;i<60;i++)
			for (int j=0;j<4;j++)
				sum+=traffic[i][j];
		return sum;
	}
	
	public String toString(){
		StringBuilder sb=new StringBuilder();
		for (int dst:TrafficLightMap.getAll())
			for (int src:TrafficLightMap.getIntersect(dst))
			if (src!=-1){
				if (sb.length()>0) sb.append(";");
				sb.append("tl"+dst+","+"tl"+src+","+getTraffic(dst, src));
			}
		return sb.toString();
	}
	
	public String getVisual(TrafficLightStatus status){
		StringBuilder sb=new StringBuilder();
		List<String> visual=FileOps.LoadFilebyLine("visualmap.txt");
		Map<Integer, Integer> pos=new HashMap<Integer,Integer>();
		for (int i=0;i<visual.size();i++)
			for (int j=0;j+1<visual.get(i).length();j++){
				if (visual.get(i).charAt(j)>='0'&&visual.get(i).charAt(j)<='9'
				&&visual.get(i).charAt(j+1)>='0'&&visual.get(i).charAt(j+1)<='9')
					pos.put(Integer.valueOf(visual.get(i).substring(j, j+2)), i*1000+j);
			}
		for (int dst:TrafficLightMap.getAll())
			for (int src:TrafficLightMap.getIntersect(dst))
			if (src!=-1){
				String traffic=""+getTraffic(dst, src);
				String ss=""+((status.getStatus(dst, src, 1)==1?4:0)
						+(status.getStatus(dst, src, 2)==1?2:0)
						+(status.getStatus(dst, src, 3)==1?1:0));
				int dx=pos.get(dst)/1000;
				int dy=pos.get(dst)%1000;
				int sx=pos.get(src)/1000;
				int sy=pos.get(src)%1000;
				int tx=0,ty=0,dir=0;
				if (dy==sy){
					if (dx<sx){
						ty=sy+2;
						tx=(dx+sx+1)/2;
						dir=1;
						visual.set(dx+1, visual.get(dx+1).substring(0,dy+2)
								+ss+visual.get(dx+1).substring(dy+3));
					}
					else {
						ty=sy-1;
						tx=(dx+sx+1)/2-1;
						dir=-1;
						visual.set(dx-1, visual.get(dx-1).substring(0,dy-1)
								+ss+visual.get(dx-1).substring(dy));
					}
				}
				else if (dx==sx){
					if (dy<sy){
						tx=dx-1;
						ty=(dy+sy)/2;
						dir=0;
						visual.set(dx-1, visual.get(dx-1).substring(0,dy+2)
								+ss+visual.get(dx-1).substring(dy+3));
					}
					else {
						tx=dx+1;
						ty=(dy+sy)/2;
						dir=0;
						visual.set(dx+1, visual.get(dx+1).substring(0,dy-1)
								+ss+visual.get(dx+1).substring(dy));

					}
				}
				else System.out.println("Error\t"+dst+"\t"+src);
				if (dir==0){
					dir=1;
					ty-=traffic.length()/2;
				}
				if (dir==-1){
					visual.set(tx, 
							visual.get(tx).substring(0,ty+1-traffic.length())
							+traffic+visual.get(tx).substring(ty+1,visual.get(tx).length()));
				}
				else if (dir==1){
					visual.set(tx, 
							visual.get(tx).substring(0,ty)
							+traffic+
							visual.get(tx).substring(ty+traffic.length(),visual.get(tx).length()));
				}
			}
		for (String s:visual) sb.append(s+"\n");
		return sb.toString();
	}
}
