package Model_old;

import DataFormat.TrafficLightMap;
import DataFormat.TrafficLightStatus;

public class AllGreen extends Model{

	@Override
	public void init() {
		super.init();
	}
	@Override
	public void updateStatus() {
		status=new TrafficLightStatus();
		if (round%1==0){
			for (int inter:TrafficLightMap.getAll()){
				int[] roads=TrafficLightMap.getIntersect(inter);
				int[] traffic=this.traffic.getTraffic(inter);
				for (int i=0;i<4;i++)
					//if (traffic[i]>=10||round%5==0)
						for (int dir=1;dir<4;dir++)
							status.setStatus(inter, roads[i], dir, 1);
			}
			return;
		}
		for (int inter:TrafficLightMap.getAll()){
			int[] roads=TrafficLightMap.getIntersect(inter);
			int[] traffic=this.traffic.getTraffic(inter);
			int k=0;
			if (traffic[1]+traffic[3]>traffic[0]+traffic[2])
				k=1;
			for (int i=1;i<4;i++){
				status.setStatus(inter, roads[k], i, 1);
				status.setStatus(inter, roads[k+2], i, 1);
			}
			for (int i=0;i<4;i++)
				status.setStatus(inter, roads[i], 3, 1);
		}
	}
}
