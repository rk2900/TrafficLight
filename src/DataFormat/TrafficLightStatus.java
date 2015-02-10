package DataFormat;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TrafficLightStatus {
	private int[][] status;
	
	public TrafficLightStatus(TrafficLightStatus status){
		this.status=new int[60][17];
		for (int i=0;i<60;i++) this.status[i]=status.status[i].clone();
	}
	
	public TrafficLightStatus(){
		status=new int[60][17];
		for (int dst:TrafficLightMap.getAll()){
			int[] roads=TrafficLightMap.getIntersect(dst);
			for (int i=0;i<4;i++)
				for (int j=0;j<4;j++)
				if (roads[i]==-1||roads[j]==-1)
					status[dst][(i<<2)+j]=-1;
				else status[dst][(i<<2)+j]=0;
		}
	}
	
	public TrafficLightStatus(String data){
		status=new int[60][17];
		for (int dst:TrafficLightMap.getAll()){
			int[] roads=TrafficLightMap.getIntersect(dst);
			for (int i=0;i<4;i++)
				for (int j=0;j<4;j++)
				if (roads[i]==-1||roads[j]==-1)
					status[dst][(i<<2)+j]=-1;
				else status[dst][(i<<2)+j]=0;
		}
		
		for (String s:data.split(";")){
			String[] sep=s.split(",");
			setStatus(sep[0], sep[1], 1, Integer.valueOf(sep[2])); 
			setStatus(sep[0], sep[1], 3, Integer.valueOf(sep[3])); 
			setStatus(sep[0], sep[1], 2, Integer.valueOf(sep[4])); 
		}
	}
	
	public void setStatus(int inter,int[] light){
		status[inter]=light.clone();
	}
	
	public int[] getStatus(int dst){
		return status[dst].clone();
	}
	
	public int getStatus(int dst,int src,int dir){
		int id=TrafficLightMap.getId(dst, src);
		return status[dst][(id<<2)+((id+dir)&3)];
	}
	
	public void setStatus(String dst,String src,int dir,int s){
		setStatus(TrafficLightMap.gettid(dst), TrafficLightMap.gettid(src),dir,s);
	}
	
	public void setStatus(int dst,int src,int dir,int s){
		int id=TrafficLightMap.getId(dst, src);
		if (status[dst][(id<<2)+((id+dir)&3)]!=-1)
			status[dst][(id<<2)+((id+dir)&3)]=s;
	}
	
	public String toString(){
		StringBuilder sb=new StringBuilder();
		for (int s:TrafficLightMap.getAll()){
			int[] roads=TrafficLightMap.getIntersect(s);
			int[] light=status[s];
			for (int i=0;i<4;i++)
				if (roads[i]!=-1){
					if (sb.length()>0) sb.append(";");
					sb.append("tl"+s+",tl"+roads[i]);
					sb.append(","+light[(i<<2)+((i+1)&3)]);
					sb.append(","+light[(i<<2)+((i+3)&3)]);
					sb.append(","+light[(i<<2)+((i+2)&3)]);
				}
		}
		return sb.toString();
	}
}
