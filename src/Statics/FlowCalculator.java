package Statics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

public class FlowCalculator {
	private static final String filePrefix = "data/flow090";
	private static final String filePostfix = ".txt";
	private static int[][] flowSum = new int[7][1680];
	private static int[] daySum = new int[7];
	
	private void calcSum(int fileCnt, String filePath) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File(filePath)));
			String line;
			while( (line=reader.readLine()) != null) {
				String[] paras = line.split(",");
				int flow = 0;
				if(paras.length == 1682) {
					for(int i=0; i< 1680; i++) {
						flow = Integer.parseInt(paras[i+2]);
						flowSum[fileCnt][i] += flow;
					}
				}
			}
			for(int i=0; i<1680; i++) {
				daySum[fileCnt] += flowSum[fileCnt][i];
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initSum() {
		for(int i=0; i<7; i++) {
			String filePath = filePrefix+(i+1)+filePostfix;
			calcSum(i, filePath);
		}
	}
	
	public int[] getSumDetail(int day) {
		return flowSum[day];
	}
	
	public int getSum(int day) {
		return daySum[day];
	}
	
	@Test
	public void testSum() {
		initSum();
		for(int i=0; i<7; i++) {
			System.out.println(getSum(i));
		}
	}
	
}
