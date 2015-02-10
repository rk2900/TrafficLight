package Model;

import java.util.HashSet;
import java.util.Set;

import DataFormat.TrafficLightMap;
import DataFormat.TrafficLightStatus;

public class Straight extends Model{

	private Set<String> straightout;
	
	@Override
	public void init() {
		super.init();
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
		resolveRule((120-round%120)/5);
		openConsecutiveRed(1);
		//openAllRight();
	}
}
