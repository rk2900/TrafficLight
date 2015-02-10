package Simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import BasicOps.FileOps;
import DataFormat.TrafficLightMap;
import DataFormat.TrafficLightStatus;
import DataFormat.TrafficMonitor;
import Model.Model;

public class GraderCore {
	private Model model;
	private TrafficMonitor traffic;
	private TrafficLightStatus status;
	private List<String> flows;
	private Map<String, Integer> redcnt;
	int round;
	int silent;
	boolean visual;
	
	int gpenalty;
	
	public GraderCore(){
		round=0;
		redcnt=new HashMap<String,Integer>();
		silent=0;
		visual=false;
		gpenalty=0;
	}
	
	public GraderCore(Model model,int round,TrafficMonitor traffic,
			List<String> flows,Map<String, Integer> redcnt,
			int silent,boolean visual){
		this.model=model;
		this.round=round;
		if (traffic!=null) this.traffic=new TrafficMonitor(traffic);
		this.flows=new LinkedList<String>(flows);
		if (redcnt!=null) this.redcnt=new HashMap<String,Integer>(redcnt);
		else this.redcnt=new HashMap<String,Integer>();
		this.round=round;
		this.visual=visual;
		this.silent=silent;
	}
	
	public void setStatus(TrafficLightStatus status){
		this.status=new TrafficLightStatus(status);
	}
	
	
	public void startRound(int round,String data){
		if (round%120==0){
			traffic=new TrafficMonitor(data);
			redcnt=new HashMap<String,Integer>();
		}
		else traffic=new TrafficMonitor(data, status, traffic);
	}
	
	public int run(int limit){
		int penalty=0;
		
		for (int i=round;i<limit;i++){
			String data=flows.get(i);
			startRound(i,data);
			
			status=new TrafficLightStatus(model.response(traffic.toString()));
			
			penalty+=getPenalty(i, traffic, status, data, redcnt, silent, visual);
			
			updateRedcnt(redcnt, status);
			
			if (silent>0){
				if (i%silent==0){
					System.out.println("Overall Penalty : "+penalty);
				}
			}
		}
		return penalty;
	}
	
	public static void updateRedcnt(
			Map<String, Integer> redcnt,TrafficLightStatus status){
		for (int dst:TrafficLightMap.getAll())
			for (int src:TrafficLightMap.getIntersect(dst))
				for (int dir=1;dir<4;dir++){
					String key=dst+"-"+src+"-"+dir;
					int s=status.getStatus(dst, src, dir);
					if (s==0){
						if (redcnt.containsKey(key))
							redcnt.put(key, redcnt.get(key)+1);
						else redcnt.put(key, 1);
					}
					if (s==1)
						redcnt.put(key, 0);
				}
	}
	
	public static int getPenalty(int round,
			TrafficMonitor traffic,TrafficLightStatus status,
			String data,Map<String, Integer> redcnt_r,
			int silent,boolean visual){
		int penalty=0;
		Map<String, Integer> redcnt=new HashMap<String,Integer>(redcnt_r);
		updateRedcnt(redcnt, status);
		
		int stayPenalty=calcStayPenalty(traffic,status);
		int redPenalty=calcRedPenalty(traffic,redcnt);
		int rulePenalty=calcRulePenalty(traffic,status);
		penalty=stayPenalty+redPenalty+rulePenalty;
		if (silent>0){
			if (round%silent==0){
				System.out.println("======== Iteration "+round+" ========");
				System.out.println("Stay Penalty : "+stayPenalty);
				System.out.println("Red Penalty : "+redPenalty);
				System.out.println("Rule Penalty : "+rulePenalty);
				System.out.println("Iteration Penalty : "+(stayPenalty+redPenalty+rulePenalty));				
				System.out.println("Current Traffic : "+traffic.sum());
				
				if (visual) System.out.println(traffic.getVisual(status));
			}
		}
		return penalty;
	}
	
	
	private static int calcStayPenalty(TrafficMonitor traffic,TrafficLightStatus status){
		int penalty=0;
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
				
				int flow=traffic.getTraffic(inter,src);
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
				
				penalty+=stay;
			}
		}
		return penalty;
	}
	
	private static int calcRedPenalty(TrafficMonitor traffic,Map<String, Integer> redcnt){
		int penalty=0;
		for (int dst:TrafficLightMap.getAll())
			for (int src:TrafficLightMap.getIntersect(dst))
			 for (int dir=1;dir<4;dir++){
				String key=dst+"-"+src+"-"+dir;
				int k=0;
				if (redcnt.containsKey(key))
					k=redcnt.get(key);
				penalty+=(int)Math.ceil(traffic.getTraffic(dst, src)*Math.sqrt(Math.max(k-4, 0)));
			}
		return penalty;
	}
	
	private static int calcRulePenalty(TrafficMonitor traffic,TrafficLightStatus status){
		int penalty=0;
		for (int dst:TrafficLightMap.getAll())
			for (int src:TrafficLightMap.getIntersect(dst)){
				double a=0,b=0,zeta=0.5;
				if (status.getStatus(dst, src, 2)==1){
						if (status.getStatus(dst,TrafficLightMap.getNextRoad(dst, src, 1),2)==1
						||status.getStatus(dst,TrafficLightMap.getNextRoad(dst, src, 3),2)==1){
							a+=traffic.getTraffic(dst, src);
							a+=traffic.getTraffic(dst, TrafficLightMap.getNextRoad(dst, src, 1));
							a+=traffic.getTraffic(dst, TrafficLightMap.getNextRoad(dst, src, 3));
						}
						if (status.getStatus(dst,TrafficLightMap.getNextRoad(dst, src, 1),1)==1){
							b+=traffic.getTraffic(dst, src);
							b+=traffic.getTraffic(dst, TrafficLightMap.getNextRoad(dst, src, 1));
						}
				}
				penalty+=0.5*zeta*a+b*zeta;
			}
		return penalty;
	}
}
