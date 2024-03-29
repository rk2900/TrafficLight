package Model;

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
		else {
			TrafficMonitor last=new TrafficMonitor(traffic);
			traffic=new TrafficMonitor(cur);//,status,traffic);
			traffic.calcRed(last,status);
		}
		updateStatus();
//		hisTraffic.add(traffic);
//		hisStatus.add(status);
		round++;
		return status.toString();
	}
	public void openConsecutiveRed(){
		for (int dst:TrafficLightMap.getAll())
			for (int src:TrafficLightMap.getIntersect(dst))
				if (src!=-1)
				for (int dir=1;dir<4;dir++)
				if (traffic.getRedCnt(dst, src, dir)>3)
					status.setStatus(dst, src, dir, 1);
	}
}

interface ModelInterface{
	public void init();
	public void updateStatus();
}
