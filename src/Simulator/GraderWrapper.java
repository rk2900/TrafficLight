package Simulator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import BasicOps.FileOps;
import DataFormat.TrafficMonitor;
import Model_old.Model;

public class GraderWrapper {
	private List<String> flows;
	private Model model;
	private boolean visual;
	private int silent;
	
	public GraderWrapper(Model model, String path,int silent,boolean visual){
		this.model=model;
		this.visual=visual;
		this.silent=silent;
		loadFlow(path);
	}
	
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
	
	public void run(){
		GraderCore core=new GraderCore(model, 0, null, flows, null, silent, visual);
		core.run(1680);
	}
}
