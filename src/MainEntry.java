import java.io.BufferedReader;
import java.io.InputStreamReader;

import BasicOps.Debug;
import DataFormat.TrafficLightMap;
import Model_old.AllGreen;
import Model_old.Model;
import Model_old.ScoreBased;
import Model_old.Straight;
import Simulator.GraderCore;
import Simulator.GraderWrapper;
import Statics.SingleCost;

public class MainEntry {
	public static void main(String[] args) {
		//System.out.println(SingleCost.getCost(1, 100, 0));
		//if (true){return;}
		
		boolean visual=false;
		int silent=0;
		for (String s:args){
			if (s.equals("-v")) visual=true;
			if (s.startsWith("-s")) silent=Integer.valueOf(s.substring(2));
		}
		
		boolean local=(args.length>1&&args[0].equals("local_cxz"));
		
		TrafficLightMap.init();
		
		Model model=new Straight();
		model.init();
		
		if (local){
			GraderWrapper grader=new GraderWrapper(model,args[1],silent,visual);
			grader.run();
		}
		else {
			try{
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String flows_str = br.readLine();	
				while(!"end".equalsIgnoreCase(flows_str)){
					String statusString =  model.response(flows_str);
					System.out.println(statusString);
					flows_str = br.readLine();		
				}
			}catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}
}
