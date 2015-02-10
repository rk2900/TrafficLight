package Statics;

import java.util.HashMap;
import java.util.Map;

public class SingleCost {
	private static Map<String, Integer> cost;
	private static Map<String, Boolean> action;
	
	private static void calc(int n,int k,int t){
		if (cost==null){
			cost=new HashMap<String,Integer>();
			action=new HashMap<String,Boolean>();
		}
		String key=n+"-"+k+"-"+t;
		if (cost.containsKey(key)) return;
		if (k==0){
			action.put(key, false);
			cost.put(key, n);
		}
		else {
			int turn=Math.min(2, (int)Math.ceil(n*0.1));
			int straight=Math.min(16, (int)Math.ceil(n*0.8));
			int left=2*Math.max(0, (int)Math.ceil(n*0.1)-2)+Math.max(0, (int)Math.ceil(n*0.8)-16);
			
			int left_red=2*Math.max(0, (int)Math.ceil(n*0.1))+Math.max(0, (int)Math.ceil(n*0.8));
			
			calc(left_red, k-1, t+1);
			int cost_red=cost.get(left_red+"-"+(k-1)+"-"+(t+1))+n;
			if (t>4) cost_red+=3*Math.ceil(n*Math.sqrt(t-4));
			
			calc(turn, k-1, 0);
			calc(straight, k-1, 0);
			calc(left, k-1, 0);
			
			int cost_green=2*cost.get(turn+"-"+(k-1)+"-0")+
					cost.get(straight+"-"+(k-1)+"-0")+
					cost.get(left+"-"+(k-1)+"-0");
			if (cost_green>cost_red){
				cost.put(key, cost_red);
				action.put(key, false);
			}
			else {
				cost.put(key, cost_green);
				action.put(key, true);
			}
		}
		if (n>0&&action.get(key))
			System.out.println(n+"\t"+k+"\t"+t+":"+cost.get(key)+"-"+action.get(key));
	}
	
	public static int getCost(int n,int k,int t){
		calc(n,k,t);
		String key=n+"-"+k+"-"+t;
		return cost.get(key);
	}
	
	public static boolean getAction(int n,int k,int t){
		calc(n,k,t);
		String key=n+"-"+k+"-"+t;
		return action.get(key);
	}
}
