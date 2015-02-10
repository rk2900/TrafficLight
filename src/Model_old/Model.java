package Model_old;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.Line;

import DataFormat.TrafficLightMap;
import DataFormat.TrafficLightStatus;
import DataFormat.TrafficMonitor;

public abstract class Model implements ModelInterface{
	protected int round;
	protected List<TrafficLightStatus> hisStatus;
	protected List<TrafficMonitor> hisTraffic;
	protected TrafficLightStatus status;
	protected TrafficMonitor traffic;
	protected List<String> flow;
	@Override
	public void init() {
		hisStatus=new ArrayList<TrafficLightStatus>();
		hisTraffic=new ArrayList<TrafficMonitor>();
		round=0;
		flow=new LinkedList<String>();
	}
	public String response(String cur) {
		flow.add(cur);
		if (round%120==0)
			traffic=new TrafficMonitor(cur);
		else traffic=new TrafficMonitor(cur);//,status,traffic);
		updateStatus();
		hisTraffic.add(traffic);
		hisStatus.add(status);
		round++;
		return status.toString();
	}
	public void openConsecutiveRed(int thres){
		for (int dst:TrafficLightMap.getAll())
			for (int src:TrafficLightMap.getIntersect(dst))
				if (traffic.getTraffic(dst, src)>thres)
					for (int dir=1;dir<4;dir++)
					if (TrafficLightMap.getNextRoad(dst, src, dir)!=-1){
						boolean f=true;
						for (int k=1;k<=round&&k<5&&f;k++){
							if ((round-k)%120==119)
								f=false;
							if (hisStatus.get(round-k).getStatus(dst, src, dir)==1)
								f=false;
						}
						if (f) status.setStatus(dst, src, dir, 1); 
					}
	}
	
	public void resolveRule(double ratio){
		ExitTimeEstimator estimator=new ExitTimeEstimator(traffic);
		for (int inter:TrafficLightMap.getAll()){
			int[] roads=TrafficLightMap.getIntersect(inter);
			int[] light=status.getStatus(inter);
			int[] best=light.clone();
			double bestCost=calcIntersectCost(inter,light,estimator);
			int cnt=0;
			for (int i=0;i<4;i++){
				if (light[(i<<2)+((i+1)&3)]==1) cnt++;
				if (light[(i<<2)+((i+2)&3)]==1) cnt++;
			}
			for (int stat=0;stat<(1<<cnt);stat++){
				int[] cur=light.clone();
				int q=stat;
				
				for (int i=0;i<4;i++){
					if (light[(i<<2)+((i+1)&3)]==1){
						cur[(i<<2)+((i+1)&3)]=q&1;
						q/=2;
					}
					if (light[(i<<2)+((i+2)&3)]==1){
						cur[(i<<2)+((i+2)&3)]=q&1;
						q/=2;
					}
				}
				double v=calcIntersectCost(inter, cur, estimator);
				if (v<bestCost){
					bestCost=v;
					best=cur.clone();
				}
			}
			status.setStatus(inter,best);
		}
			
	}
	
	private double calcIntersectCost(int inter,int[] light,ExitTimeEstimator estimator){
		double cost=0;
		int[] c_traffic=traffic.getTraffic(inter);
		int[] roads=TrafficLightMap.getIntersect(inter);
		double a=0,b=0,zeta=0.5;
		int through=0;
		for (int i=0;i<4;i++){
			if (light[(i<<2)+((i+2)&3)]==1)
				if (light[(((i+1)&3)<<2)+((i+3)&3)]==1||light[(((i+3)&3)<<2)+((i+1)&3)]==1)
					a+=c_traffic[i]+c_traffic[(i+1)&3]+c_traffic[(i+3)&3];
				if (light[(((i+3)&3)<<2)+i]==1)
					b+=c_traffic[i]+c_traffic[(i+3)&3];
			double[] ratio={0.1,0.8,0.1};
			if (light[(i<<2)+((i+1)&3)]==-1) {ratio[2]=0.2;ratio[0]=0;}
			if (light[(i<<2)+((i+2)&3)]==-1) {ratio[1]=0;ratio[0]=ratio[2]=0.5;}
			if (light[(i<<2)+((i+3)&3)]==-1) {ratio[0]=0.2;ratio[2]=0;}
			int[] t={2,16,2};
			for (int dir=1;dir<4;dir++)
				if (light[(i<<2)+((i+dir)&3)]==1)
					through+=Math.min(t[dir-1],(int)Math.ceil(c_traffic[i]*ratio[dir-1]))*
						0.9*(Math.max(0,(119-round%120)-1*Math.pow(estimator.get(inter, roads[i]),1.5)));
						
		}
		cost+=0.5*zeta*a+b*zeta;
		return cost-through;
	}
	
	public void openAllRight(){
		for (int dst:TrafficLightMap.getAll())
			for (int src:TrafficLightMap.getIntersect(dst))
				status.setStatus(dst, src, 3, 1);
	}
}

interface ModelInterface{
	public void init();
	public void updateStatus();
}
