package Model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import BasicOps.FileOps;
import DataFormat.TrafficLightMap;
import DataFormat.TrafficLightStatus;
import DataFormat.TrafficMonitor;

public class PredictBased_v0 extends Model{
	TrafficMonitor [] traffics;
	TrafficLightStatus [] allstatus;
	PenaltyPredictor [] costs;
	String [] curflows;
	LinkedList<String>flows;
	
	private void loadFlow(String path){
		Map<String, Integer[]> flow=new HashMap<String, Integer[]>();
		List<String> rawflow=FileOps.LoadFilebyLine(path);
		for (String line:rawflow){
			String[] sep=line.split(",");
			String dst=sep[0];
			String src=sep[1];
			Integer[] value=new Integer[1680];
			for (int i=0;i<1680;i++)
				value[i]=Integer.valueOf(sep[i+2]);
			flow.put(dst+","+src, value);
		}
			flows=new LinkedList<String>();
			for (int i=0;i<1680;i++){
				String data="";
				for (String s:flow.keySet())
					data+=(data.length()>0?";":"")+s+","+flow.get(s)[i];
				flows.add(data);
			}
	}
	@Override
	public void init(){
		super.init();
		traffics=new TrafficMonitor[120];
		allstatus=new TrafficLightStatus[120];
		costs=new PenaltyPredictor[120];
		loadFlow("data/flow0901.txt");
	}
	
	private TrafficLightStatus getBaseStatus(TrafficMonitor traffic){
		TrafficLightStatus status=new TrafficLightStatus();
		for (int dst:TrafficLightMap.getAll())
			for (int src:TrafficLightMap.getIntersect(dst))
				if (src!=-1)
					for (int dir=1;dir<4;dir++)
						status.setStatus(dst, src, dir, 1);
		return status;
	}

	private void HourInit(){
		curflows=new String[120];
		curflows[0]=flows.get(round);
		traffics[0]=new TrafficMonitor(curflows[0]);
		allstatus[0]=getBaseStatus(traffics[0]);
		for (int i=1;i<120;i++){
			curflows[i]=flows.get(round+i);
			traffics[i]=new TrafficMonitor(curflows[i],allstatus[i-1],traffics[i-1]);
			allstatus[i]=getBaseStatus(traffics[i]);
		}
		costs[119]=new PenaltyPredictor();
		for (int i=118;i>=0;i--){
			costs[i]=new PenaltyPredictor();
			costs[i].calc(traffics[i+1], allstatus[i+1], costs[i+1]);
		}
	}
	
//	private void updatePredictor(int t){
//		//update traffic data downstream
//		for (int i=t+1;i<120;i++)
//			traffics[i]=new TrafficMonitor(curflows[i], allstatus[i-1], traffics[i-1]);
//		updateStatus(119);
//		for (int i=118;i>=t;i--){
//			costs[i].calc(traffics[i+1], allstatus[i+1], costs[i+1]);
////			updateStatus(i);
//		}
//	}
	
	private double calcIntersectCost(int t,int dst,int[] light){
		int[] roads=TrafficLightMap.getIntersect(dst);
		double res=0;
		for (int i=0;i<4;i++) if (roads[i]!=-1){
			int src=roads[i];
			Double[] initTurnRate = {0.2,0.2,0.6};
			
			if (TrafficLightMap.getNextRoad(dst, src, 1)==-1) {
				initTurnRate[1] += initTurnRate[0];
				initTurnRate[0] -= initTurnRate[0];				
			}else if (TrafficLightMap.getNextRoad(dst, src, 3)==-1) {
				initTurnRate[0] += initTurnRate[1];
				initTurnRate[1] -= initTurnRate[1];
			}else if (TrafficLightMap.getNextRoad(dst, src, 2)==-1) {
				initTurnRate[0] += initTurnRate[2]*0.5;
				initTurnRate[1] += initTurnRate[2]*0.5;
				initTurnRate[2] -= initTurnRate[2];
			}
			
			int th=20-5*TrafficLightMap.getId(dst, src);
			
			int flow=traffics[t].getTraffic(dst,src);
			int left_through=(light[(i<<2)+((i+1)&3)]==1)?(int)(th*initTurnRate[0]):0;
			int straight_through=(light[(i<<2)+((i+2)&3)]==1)?(int)(th*initTurnRate[2]):0;
			int right_through=(light[(i<<2)+((i+3)&3)]==1)?(int)(th*initTurnRate[1]):0;
			
			int left_cnt=(int)Math.floor(flow*initTurnRate[0]);
			int right_cnt=(int)Math.floor(flow*initTurnRate[1]);
			int straight_cnt=flow-left_cnt-right_cnt;
			
			int left_flow=Math.min(left_cnt, left_through);
			int straight_flow=Math.min(straight_cnt, straight_through);
			int right_flow=Math.min(right_cnt, right_through);
			
			int stay=(left_cnt+straight_cnt+right_cnt-left_flow-right_flow-straight_flow);
			
			res+=stay*(1+costs[t].query(dst, src));
			if (left_flow>0) res+=left_flow*costs[t].query(TrafficLightMap.getNextRoad(dst, src, 1), dst);
			if (straight_flow>0) res+=straight_flow*costs[t].query(TrafficLightMap.getNextRoad(dst, src, 2), dst);
			if (right_flow>0) res+=right_flow*costs[t].query(TrafficLightMap.getNextRoad(dst, src, 3), dst);
			
			double red=0;
			for (int dir=1;dir<4;dir++)
				if (light[(i<<2)+((i+dir)&3)]==0) {
					red+=flow*Math.sqrt(Math.max(0, traffics[t].getRedCnt(dst, src, dir)-3));
				}
					
			res+=red;
			
//			double zeta=0.5;
//			if (light[(i<<2)+((i+2)&3)]==1){
//				if (light[(((i+1)&3)<<2)+((i+3)&3)]==1||light[(((i+3)&3)<<2)+((i+1)&3)]==1)
//					res+=zeta*0.5*flow;
//				if (light[(((i+1)&3)<<2)+((i+3)&3)]==1)
//					res+=zeta*0.5*flow;
//				if (light[(((i+3)&3)<<2)+((i+1)&3)]==1)
//					res+=zeta*0.5*flow;
//			}
//			if (light[(i<<2)+((i+2)&3)]==1&&light[(((i+3)&3)<<2)+i]==1)
//					res+=zeta*flow;
//			if (light[(((i+1)&3)<<2)+(i+3)&3]==1&&light[(i<<2)+(i+1)&3]==1)
//					res+=zeta*flow;
		}
		return res;
	}
	
	private void updateStatus(int t){
		Random random=new Random();
		for (int inter:TrafficLightMap.getAll()){
			int[] roads=TrafficLightMap.getIntersect(inter);
			int[] light=allstatus[t].getStatus(inter);
			int[] best=light.clone();
			double bestCost=calcIntersectCost(t,inter,light);
			int cnt=0;
			for (int i=0;i<4;i++)
				for (int j=0;j<4;j++) if (i!=j&&roads[i]!=-1&&roads[j]!=-1){
					light[(i<<2)+j]=1;
					cnt++;
				}
			for (int stat=0;stat<(1<<cnt);stat++){
//				if (random.nextInt(2)!=0) continue;
				int[] cur=light.clone();
				int q=stat;
				
				for (int i=0;i<4;i++)
					for (int j=0;j<4;j++) if (i!=j&&roads[i]!=-1&&roads[j]!=-1){
						cur[(i<<2)+j]=q&1;
						q/=2;
					}
				double v=calcIntersectCost(t,inter, cur);
				if (v<bestCost){
					bestCost=v;
					best=cur.clone();
				}
			}
			allstatus[t].setStatus(inter,best);
		}
	}
	
	private void updateFlow(int t,String data){
		curflows[t]=data;
		traffics[t]=new TrafficMonitor(traffic);
		for (int dst:TrafficLightMap.getAll())
			for (int src:TrafficLightMap.getIntersect(dst))
				if (src!=-1){
					for (int dir=1;dir<4;dir++){
						int nxt=TrafficLightMap.getNextRoad(dst, src, dir);
						if (nxt==-1) continue;
						allstatus[t].setStatus(dst, src, dir, 0);
						if (traffics[t].getRedCnt(dst, src, dir)==4)
							allstatus[t].setStatus(dst, src, dir, 1);
						if (costs[t].query(nxt, dst)<costs[t].query(dst, src)+1)
							allstatus[t].setStatus(dst, src, dir, 1);
					}
				}
//		for (int i=t;i<120;i++){
//			updateStatus(i);
//			if (i<119) {
//				traffics[i+1]=new TrafficMonitor(curflows[i+1], allstatus[i], traffics[i]);
//				costs[i].calc(traffics[i+1], allstatus[i+1], costs[i+1]);
//			}
//		}
	}
	@Override
	public void updateStatus() {
		if (round%120==0){
			HourInit();
			updateFlow(0,curflows[0]);
		}
		
		updateFlow(round%120,flow.get(round));
		status=allstatus[round%120];
//		openConsecutiveRed();
	}
}
