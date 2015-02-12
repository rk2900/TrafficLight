package Model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;

import org.junit.Test;

import BasicOps.FileOps;
import DataFormat.TrafficLightMap;
import DataFormat.TrafficMonitor;
import Statics.StaticFlow;

public class FlowPredictor {
	protected int[][][] predTraffic;
	protected int[][] curTraffic;
//	protected String testLine = "1	0	tl8,tl9,0;tl5,tl49,0;tl21,tl6,1;tl10,tl17,0;tl30,tl23,0;tl41,tl43,1;tl2,tl9,1;tl2,tl47,0;tl16,tl17,0;tl5,tl4,0;tl38,tl5,0;tl14,tl22,0;tl32,tl55,0;tl37,tl38,0;tl34,tl57,0;tl22,tl14,1;tl7,tl14,0;tl22,tl23,1;tl11,tl18,0;tl6,tl46,0;tl31,tl32,0;tl32,tl31,0;tl44,tl42,0;tl44,tl19,1;tl19,tl20,0;tl12,tl38,0;tl11,tl10,0;tl31,tl30,0;tl39,tl40,0;tl26,tl33,0;tl42,tl44,0;tl17,tl40,1;tl23,tl22,2;tl19,tl44,0;tl35,tl58,0;tl34,tl35,0;tl18,tl17,0;tl24,tl23,0;tl1,tl7,1;tl42,tl26,0;tl7,tl1,0;tl3,tl4,0;tl20,tl27,0;tl29,tl22,0;tl16,tl15,1;tl29,tl53,4;tl35,tl52,0;tl16,tl39,0;tl10,tl36,0;tl27,tl34,0;tl15,tl14,0;tl10,tl11,0;tl38,tl37,0;tl7,tl8,0;tl43,tl41,0;tl27,tl20,1;tl6,tl50,0;tl32,tl33,0;tl38,tl12,1;tl26,tl25,1;tl17,tl18,0;tl3,tl2,1;tl16,tl9,0;tl11,tl12,0;tl25,tl41,0;tl15,tl8,2;tl9,tl10,0;tl17,tl16,0;tl2,tl3,0;tl23,tl39,0;tl28,tl27,2;tl37,tl4,0;tl25,tl24,0;tl36,tl37,0;tl43,tl18,1;tl24,tl40,1;tl9,tl2,2;tl35,tl28,1;tl31,tl24,0;tl39,tl16,0;tl5,tl38,0;tl26,tl42,0;tl2,tl1,1;tl25,tl26,0;tl14,tl7,0;tl18,tl43,1;tl33,tl32,1;tl42,tl41,0;tl28,tl21,2;tl33,tl34,0;tl4,tl5,0;tl23,tl30,0;tl4,tl3,0;tl24,tl31,3;tl12,tl11,0;tl20,tl19,1;tl23,tl24,1;tl41,tl42,0;tl40,tl39,2;tl41,tl40,1;tl6,tl5,0;tl24,tl25,0;tl34,tl27,0;tl4,tl48,0;tl3,tl36,0;tl9,tl8,2;tl40,tl17,1;tl30,tl31,0;tl20,tl21,0;tl26,tl27,0;tl40,tl41,0;tl15,tl16,0;tl7,tl13,1;tl40,tl24,0;tl18,tl19,0;tl29,tl30,0;tl12,tl19,0;tl8,tl7,1;tl33,tl56,0;tl4,tl37,0;tl29,tl51,2;tl33,tl26,0;tl41,tl25,0;tl1,tl45,0;tl6,tl21,0;tl36,tl10,0;tl19,tl18,0;tl27,tl26,0;tl32,tl25,0;tl43,tl44,0;tl25,tl32,0;tl1,tl2,0;tl36,tl3,0;tl19,tl12,0;tl34,tl33,0;tl11,tl37,0;tl30,tl29,1;tl17,tl10,0;tl21,tl28,0;tl8,tl15,2;tl37,tl36,1;tl27,tl28,0;tl44,tl43,1;tl5,tl6,0;tl30,tl54,1;tl28,tl35,0;tl37,tl11,3;tl35,tl34,0;tl21,tl20,0;tl9,tl16,0;tl14,tl15,0;tl39,tl23,0;tl22,tl29,0;tl18,tl11,2;tl10,tl9,2";
	
	public FlowPredictor() {
		TrafficLightMap.init();
		predTraffic = new int[1680][60][4];
		init();
	}
	
	public void init() {
		Map<String, Integer[]> flow=new HashMap<String, Integer[]>();
		for (String line:StaticFlow.dayFlow){
			String[] sep=line.split(",");
			String dst=sep[0];
			String src=sep[1];
			Integer[] value=new Integer[1680];
			for (int i=0;i<1680;i++)
				value[i]=Integer.valueOf(sep[i+2]);
			flow.put(dst+","+src, value);
		}
		
		LinkedList<String> trafficFlows = new LinkedList<String>();
		for(int i=0; i<1680; i++) {
			StringBuilder timeslice = new StringBuilder();
			for(String dir: flow.keySet()) {
				timeslice.append(timeslice.length()>0?";":"");
				timeslice.append(dir);
				timeslice.append(",");
				timeslice.append(flow.get(dir)[i]);
			}
			trafficFlows.add(timeslice.toString());
		}
		int round = 0;
		
//		FileOps.SaveFile("data/dayFlow", trafficFlows);
		for(String s: trafficFlows) {
//			System.out.println(s);
			this.observe(round, s);
			round++;
		}
	}
	
	public String query(int round){
		curTraffic = predTraffic[round];
		return parseString(curTraffic);
	}
	
	private String parseString(int[][] curTraffic2) {
		StringBuilder sb = new StringBuilder();
		for(int dst: TrafficLightMap.getAll()) {
			for(int src: TrafficLightMap.getIntersect(dst)) {
				if(src!=-1) {
					if(sb.length()>0) 
						sb.append(";");
					sb.append("tl"+dst+","+"tl"+src+","+getTraffic(dst, src));
				}
			}
		}
		return sb.toString();
	}

	private int getTraffic(int dst, int src) {
		return curTraffic[dst][TrafficLightMap.getId(dst, src)];
	}

	public void observe(int round,String str){
		String data = new String(str);
		if(str.contains("\t")) {
			data = str.split("\t")[2];
		}
		curTraffic = new int[60][4];
		for(String s: data.split(";")) {
			String[] sep = s.split(",");
			addTraffic(sep[0], sep[1], Integer.parseInt(sep[2]));
		}
		predTraffic[round] = curTraffic;
		//TODO predict
		predict(round);
	}
	
	private void predict(int round) {
		// TODO Auto-generated method stub
		for(int i=round+1; i<1680; i++) {
			
		}
	}

	private void addTraffic(String dst, String src, int v) {
		addTraffic(TrafficLightMap.gettid(dst), TrafficLightMap.gettid(src), v);
	}

	private void addTraffic(int dst, int src, int v) {
		if(v>0) {
			int id = TrafficLightMap.getId(dst, src);
			if(id!=-1) {
				curTraffic[dst][id] += v;
			}
		}
	}
	
	@Test
	public void predictorTest() {
//		TrafficLightMap.init();
		FlowPredictor p = new FlowPredictor();
//		String inputFile = "data/flow0901.txt";
//		String outputFile = "data/flowOut.txt";
//		BufferedReader bf;
//		BufferedWriter bw;
//		try {
//			bf = new BufferedReader(new FileReader(new File(inputFile)));
//			bw = new BufferedWriter(new FileWriter(new File(outputFile)));
//			String line;
//			while( (line = bf.readLine()) != null ) {
//				StringBuilder sb = new StringBuilder();
//				sb.append("\"");
//				sb.append(line);
//				sb.append("\",");
////				System.out.println(sb.toString());
//				bw.write(sb.toString());
//				bw.newLine();
//			}
//			bf.close();
//			bw.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		
		
		
		
	}
}

