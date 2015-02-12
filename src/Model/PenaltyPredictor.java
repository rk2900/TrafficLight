package Model;

import DataFormat.TrafficLightMap;
import DataFormat.TrafficLightStatus;
import DataFormat.TrafficMonitor;

public class PenaltyPredictor {
	double[][] cost;
	public PenaltyPredictor(){
		cost=new double[60][60];
		for (int dst:TrafficLightMap.getAll())
			for (int src:TrafficLightMap.getIntersect(dst))
				if (src!=-1) 
				cost[dst][src]=0;
	}
	public double query(int dst,int src){
		return cost[dst][src];
	}
	public void calc(TrafficMonitor traffic, TrafficLightStatus status,PenaltyPredictor nxtcost){
		for (int dst:TrafficLightMap.getAll())
			for (int src:TrafficLightMap.getIntersect(dst))
			if (src!=-1) {
				
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
				
				int flow=Math.max(th,traffic.getTraffic(dst,src));
				int left_through=(status.getStatus(dst, src, 1)==1)?(int)(th*initTurnRate[0]):0;
				int straight_through=(status.getStatus(dst, src, 2)==1)?(int)(th*initTurnRate[2]):0;
				int right_through=(status.getStatus(dst, src, 3)==1)?(int)(th*initTurnRate[1]):0;
				
				int left_cnt=(int)Math.floor(flow*initTurnRate[0]);
				int right_cnt=(int)Math.floor(flow*initTurnRate[1]);
				int straight_cnt=flow-left_cnt-right_cnt;
				
				int left_flow=Math.min(left_cnt, left_through);
				int straight_flow=Math.min(straight_cnt, straight_through);
				int right_flow=Math.min(right_cnt, right_through);
				
				int stay=(left_cnt+straight_cnt+right_cnt-left_flow-right_flow-straight_flow);
				
				double curcost=0;
				
				curcost+=stay*(1+nxtcost.query(dst,src));
				if (left_flow>0) curcost+=left_flow*nxtcost.query(TrafficLightMap.getNextRoad(dst, src, 1), dst);
				if (straight_flow>0) curcost+=straight_flow*nxtcost.query(TrafficLightMap.getNextRoad(dst, src, 2), dst);
				if (right_flow>0) curcost+=right_flow*nxtcost.query(TrafficLightMap.getNextRoad(dst, src, 3), dst);

				int left=TrafficLightMap.getNextRoad(dst, src, 1);
				int right=TrafficLightMap.getNextRoad(dst, src, 3);
//				double zeta=0.5;
//				if (status.getStatus(dst, src, 2)==1){
//					if (status.getStatus(dst, left, 2)==1||status.getStatus(dst,right,2)==1)
//						curcost+=zeta*0.5*flow;
//					if (status.getStatus(dst, left, 2)==1)
//						curcost+=zeta*0.5*flow;
//					if (status.getStatus(dst, right, 2)==1)
//						curcost+=zeta*0.5*flow;
//				}
//				
//				//Rule2 Penalty
//				if (right!=-1)
//					if (status.getStatus(dst, src, 2)==1&&status.getStatus(dst, right, 1)==1)
//						curcost+=zeta*flow;
//				if (left!=-1)
//					if (status.getStatus(dst, left, 2)==1&&status.getStatus(dst, src, 1)==1)
//						curcost+=zeta*flow;
				
//				//Red Penalty
//				for (int dir=1;dir<4;dir++)
//					curcost+=flow*Math.sqrt(Math.max(0,traffic.getRedCnt(dst, src, dir)-4));
//				
				cost[dst][src]=(flow>0)?curcost/flow:0;
			}
	}
}
