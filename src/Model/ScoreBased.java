package Model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import BasicOps.FileOps;
import DataFormat.TrafficLightMap;
import DataFormat.TrafficLightStatus;
import DataFormat.TrafficMonitor;
import Simulator.GraderCore;

public class ScoreBased extends Model{

	private Set<String> straightout;
	private Map<String, Integer> redcnt;
	private GraderCore grader;
	int curPenalty;
	
	private List<String> flows;
	
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
	public void init() {
		super.init();
		grader=new GraderCore();
		redcnt=new HashMap<String,Integer>();
		loadFlow("flow0901.txt");
		straightout=new HashSet<String>();
		for (int dst:TrafficLightMap.getAll())
			for (int src:TrafficLightMap.getIntersect(dst))
			if (src!=-1){
				int d=dst;
				int s=src;
				if (TrafficLightMap.getIntersect(s)[0]==0)
					straightout.add(s+"-"+d);
				for (;;){
					if (TrafficLightMap.getIntersect(d)[0]==0){
						straightout.add(dst+"-"+src);
						break;
					}
					int nxt=TrafficLightMap.getNextRoad(d, s, 2);
					if (nxt==-1) break;
					s=d;
					d=nxt;
				}
			}
	}
	
	
	@Override
	public void updateStatus() {
		status=new TrafficLightStatus();
		for (int dst:TrafficLightMap.getAll())
			for (int src:TrafficLightMap.getIntersect(dst)){
				boolean f=false;
				for (int dir=1;dir<4;dir++){
					int nxt=TrafficLightMap.getNextRoad(dst, src, dir);
					if (nxt==-1) continue;
					if (straightout.contains(nxt+"-"+dst)){
						status.setStatus(dst, src, dir, 1);
						f=true;
						//System.out.println(dst+"-"+src+"\t"+dir);
					}
				}
				if (!f)
					for (int dir=1;dir<4;dir++)
						status.setStatus(dst, src, dir, 1);
			}
		
		grader.startRound(round, flow.get(round));
		updateByScore();
		//openConsecutiveRed(1);
		curPenalty+=grader.getPenalty(round, traffic, status, flow.get(round),
				redcnt, 0,false);
		grader.updateRedcnt(redcnt, status);
		grader.setStatus(status);
	}
	
	private void updateByScore(){
		for (int inter:TrafficLightMap.getAll()){
			int[] roads=TrafficLightMap.getIntersect(inter);
			int[] light=status.getStatus(inter);
			int[] best=light.clone();
			int bestCost=calcScore(status);
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
				status.setStatus(inter, cur);
				int v=calcScore(status);
				//System.out.println(stat+"\t"+v);
				if (v<bestCost){
					bestCost=v;
					best=cur.clone();
				}
			}
			status.setStatus(inter,best);
		}
	}
	
	private int calcScore(TrafficLightStatus status){
		int penalty=grader.getPenalty(round, traffic, status, flow.get(round),
				redcnt, 0,false);
		
		Model model=new AllGreen();
		model.init();
		model.round=round+1;
		model.traffic=new TrafficMonitor(traffic);
		model.status=new TrafficLightStatus(status);
		
		GraderCore simulator=new GraderCore(model,
				round, traffic, flows, redcnt, 0, false);
		simulator.setStatus(new TrafficLightStatus());
		
		int limit=119-round%120;
		int p2=simulator.run(limit);
		System.out.println("##"+penalty+"\t"+p2);
		penalty+=p2;
		return penalty;
	}
}
