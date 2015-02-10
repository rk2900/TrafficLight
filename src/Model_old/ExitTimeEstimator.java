package Model_old;

import java.util.HashMap;
import java.util.Map;

import DataFormat.TrafficLightMap;
import DataFormat.TrafficLightStatus;
import DataFormat.TrafficMonitor;

public class ExitTimeEstimator {
	
	private Map<String, Double> delay;
	private TrafficMonitor traffic;
	
	public ExitTimeEstimator(TrafficMonitor traffic){
		delay=new HashMap<String, Double>();
		this.traffic=traffic;
	}
	
	public double get(int dst,int src){
		String key=dst+"-"+src;
		if (!delay.containsKey(key)){
			double res=0;
			int nxt=TrafficLightMap.getNextRoad(dst, src, 2);
			if (nxt==-1){
				nxt=TrafficLightMap.getNextRoad(dst, src, 1);
				if (TrafficLightMap.getIntersect(nxt)[0]!=0){
					res+=get(nxt, dst);
					res+=traffic.getTraffic(nxt, dst)/2+1;
				}
				nxt=TrafficLightMap.getNextRoad(dst, src, 3);
				if (TrafficLightMap.getIntersect(nxt)[0]!=0){
					res+=get(nxt, dst);
					res+=traffic.getTraffic(nxt, dst)/2+1;
				}
				res/=2;
			}
			else {
				if (TrafficLightMap.getIntersect(nxt)[0]!=0){
					res+=get(nxt, dst);
					res+=traffic.getTraffic(nxt, dst)/16+1;
				}
			}
			delay.put(key, res);
		}
		return delay.get(key); 
	}
}
