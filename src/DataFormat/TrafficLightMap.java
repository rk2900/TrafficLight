package DataFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TrafficLightMap {
	private static String tupoString = "tl44,tl42,tl43,#,tl19;tl44,tl43,tl19,tl42,#;tl44,tl19,#,tl43,tl42;tl43,tl44,tl41,tl18,#;tl43,tl41,#,tl44,tl18;tl43,tl18,tl44,#,tl41;tl42,tl26,tl41,#,tl44;tl42,tl41,tl44,tl26,#;tl42,tl44,#,tl41,tl26;tl41,tl42,tl25,tl43,tl40;tl41,tl25,tl40,tl42,tl43;tl41,tl40,tl43,tl25,tl42;tl41,tl43,tl42,tl40,tl25;tl40,tl41,tl24,tl17,tl39;tl40,tl24,tl39,tl41,tl17;tl40,tl39,tl17,tl24,tl41;tl40,tl17,tl41,tl39,tl24;tl39,tl40,tl23,tl16,#;tl39,tl23,#,tl40,tl16;tl39,tl16,tl40,#,tl23;tl38,tl12,tl37,#,tl5;tl38,tl37,tl5,tl12,#;tl38,tl5,#,tl37,tl12;tl37,tl38,tl11,tl4,tl36;tl37,tl11,tl36,tl38,tl4;tl37,tl36,tl4,tl11,tl38;tl37,tl4,tl38,tl36,tl11;tl36,tl37,tl10,tl3,#;tl36,tl10,#,tl37,tl3;tl36,tl3,tl37,#,tl10;tl35,tl52,tl58,tl28,tl34;tl35,tl58,tl34,tl52,tl28;tl35,tl34,tl28,tl58,tl52;tl35,tl28,tl52,tl34,tl58;tl34,tl35,tl57,tl27,tl33;tl34,tl57,tl33,tl35,tl27;tl34,tl33,tl27,tl57,tl35;tl34,tl27,tl35,tl33,tl57;tl33,tl34,tl56,tl26,tl32;tl33,tl56,tl32,tl34,tl26;tl33,tl32,tl26,tl56,tl34;tl33,tl26,tl34,tl32,tl56;tl32,tl33,tl55,tl25,tl31;tl32,tl55,tl31,tl33,tl25;tl32,tl31,tl25,tl55,tl33;tl32,tl25,tl33,tl31,tl55;tl31,tl32,#,tl24,tl30;tl31,tl30,tl24,#,tl32;tl31,tl24,tl32,tl30,#;tl30,tl31,tl54,tl23,tl29;tl30,tl54,tl29,tl31,tl23;tl30,tl29,tl23,tl54,tl31;tl30,tl23,tl31,tl29,tl54;tl29,tl30,tl53,tl22,tl51;tl29,tl53,tl51,tl30,tl22;tl29,tl51,tl22,tl53,tl30;tl29,tl22,tl30,tl51,tl53;tl28,tl35,tl27,#,tl21;tl28,tl27,tl21,tl35,#;tl28,tl21,#,tl27,tl35;tl27,tl28,tl34,tl20,tl26;tl27,tl34,tl26,tl28,tl20;tl27,tl26,tl20,tl34,tl28;tl27,tl20,tl28,tl26,tl34;tl26,tl27,tl33,tl42,tl25;tl26,tl33,tl25,tl27,tl42;tl26,tl25,tl42,tl33,tl27;tl26,tl42,tl27,tl25,tl33;tl25,tl26,tl32,tl41,tl24;tl25,tl32,tl24,tl26,tl41;tl25,tl24,tl41,tl32,tl26;tl25,tl41,tl26,tl24,tl32;tl24,tl25,tl31,tl40,tl23;tl24,tl31,tl23,tl25,tl40;tl24,tl23,tl40,tl31,tl25;tl24,tl40,tl25,tl23,tl31;tl23,tl24,tl30,tl39,tl22;tl23,tl30,tl22,tl24,tl39;tl23,tl22,tl39,tl30,tl24;tl23,tl39,tl24,tl22,tl30;tl22,tl23,tl29,tl14,#;tl22,tl29,#,tl23,tl14;tl22,tl14,tl23,#,tl29;tl21,tl28,tl20,#,tl6;tl21,tl20,tl6,tl28,#;tl21,tl6,#,tl20,tl28;tl20,tl21,tl27,#,tl19;tl20,tl27,tl19,tl21,#;tl20,tl19,#,tl27,tl21;tl19,tl20,tl44,tl12,tl18;tl19,tl44,tl18,tl20,tl12;tl19,tl18,tl12,tl44,tl20;tl19,tl12,tl20,tl18,tl44;tl18,tl19,tl43,tl11,tl17;tl18,tl43,tl17,tl19,tl11;tl18,tl17,tl11,tl43,tl19;tl18,tl11,tl19,tl17,tl43;tl17,tl18,tl40,tl10,tl16;tl17,tl40,tl16,tl18,tl10;tl17,tl16,tl10,tl40,tl18;tl17,tl10,tl18,tl16,tl40;tl16,tl17,tl39,tl9,tl15;tl16,tl39,tl15,tl17,tl9;tl16,tl15,tl9,tl39,tl17;tl16,tl9,tl17,tl15,tl39;tl15,tl16,#,tl8,tl14;tl15,tl14,tl8,#,tl16;tl15,tl8,tl16,tl14,#;tl14,tl15,tl22,tl7,#;tl14,tl22,#,tl15,tl7;tl14,tl7,tl15,#,tl22;tl12,tl19,tl11,#,tl38;tl12,tl11,tl38,tl19,#;tl12,tl38,#,tl11,tl19;tl11,tl12,tl18,tl37,tl10;tl11,tl18,tl10,tl12,tl37;tl11,tl10,tl37,tl18,tl12;tl11,tl37,tl12,tl10,tl18;tl10,tl11,tl17,tl36,tl9;tl10,tl17,tl9,tl11,tl36;tl10,tl9,tl36,tl17,tl11;tl10,tl36,tl11,tl9,tl17;tl9,tl10,tl16,tl2,tl8;tl9,tl16,tl8,tl10,tl2;tl9,tl8,tl2,tl16,tl10;tl9,tl2,tl10,tl8,tl16;tl8,tl9,tl15,#,tl7;tl8,tl15,tl7,tl9,#;tl8,tl7,#,tl15,tl9;tl7,tl8,tl14,tl1,tl13;tl7,tl14,tl13,tl8,tl1;tl7,tl13,tl1,tl14,tl8;tl7,tl1,tl8,tl13,tl14;tl1,tl2,tl7,#,tl45;tl1,tl7,tl45,tl2,#;tl1,tl45,#,tl7,tl2;tl2,tl47,tl3,tl1,tl9;tl2,tl3,tl9,tl47,tl1;tl2,tl9,tl1,tl3,tl47;tl2,tl1,tl47,tl9,tl3;tl3,tl4,tl36,#,tl2;tl3,tl36,tl2,tl4,#;tl3,tl2,#,tl36,tl4;tl4,tl48,tl5,tl3,tl37;tl4,tl5,tl37,tl48,tl3;tl4,tl37,tl3,tl5,tl48;tl4,tl3,tl48,tl37,tl5;tl5,tl49,tl6,tl4,tl38;tl5,tl6,tl38,tl49,tl4;tl5,tl38,tl4,tl6,tl49;tl5,tl4,tl49,tl38,tl6;tl6,tl50,tl46,tl5,tl21;tl6,tl46,tl21,tl50,tl5;tl6,tl21,tl5,tl46,tl50;tl6,tl5,tl50,tl21,tl46";
	private static int[][] intersects;
	private static int[][] level;
	private static HashSet<Integer> all;
	public static int gettid(String s){
		if (s.equals("#")) return -1;
		return Integer.valueOf(s.substring(2));
	}
	public static void init(){
		intersects=new int[60][4];
		all=new HashSet<Integer>();
		for (String s:tupoString.split(";")){
			String[] sep=s.split(",");
			int id=gettid(sep[0]);
			all.add(id);
			intersects[id][0]=gettid(sep[1]);
			intersects[id][1]=gettid(sep[2]);
			intersects[id][2]=gettid(sep[4]);
			intersects[id][3]=gettid(sep[3]);
		}
		level=new int[60][60];
		level[22][29]=2;
		level[29][53]=2;
		level[44][42]=2;
		level[8][15]=2;
		level[24][31]=2;
		level[16][39]=2;
		level[1][7]=2;
		level[40][24]=2;
		level[10][9]=2;
		level[8][7]=2;
		level[33][56]=2;
		level[40][39]=2;
		level[15][8]=2;
		level[17][40]=1;
		level[9][16]=2;
		level[26][33]=2;
		level[24][40]=1;
		level[10][17]=1;
		level[10][36]=1;
		level[21][28]=2;
		level[35][58]=2;
		level[9][8]=2;
		level[23][30]=2;
		level[7][13]=1;
		level[24][23]=1;
		level[44][43]=1;
		level[21][6]=1;
		level[16][15]=1;
		level[11][10]=1;
		level[23][22]=1;
		level[43][41]=1;
		level[33][32]=1;
		level[36][3]=1;
		level[40][17]=1;
		level[32][31]=1;
		level[38][12]=2;
		level[32][25]=1;
		level[41][40]=1;
		level[28][21]=1;
		level[17][10]=1;
		level[35][34]=1;
		level[28][27]=1;
		level[2][1]=1;
		level[38][5]=0;
		level[44][19]=1;
		level[42][41]=0;
		level[30][29]=1;
		level[4][48]=0;
		level[43][18]=0;
		level[39][23]=0;
		level[5][38]=0;
		level[41][43]=0;
		level[36][10]=1;
		level[31][24]=0;
		level[41][25]=0;
		level[4][3]=0;
		level[2][47]=0;
		level[42][26]=1;
		level[21][20]=0;
		level[4][37]=1;
		level[25][32]=0;
		level[12][11]=0;
		level[30][54]=1;
		level[35][28]=0;
		level[15][14]=0;
		level[31][32]=0;
		level[7][14]=0;
		level[22][23]=0;
		level[25][41]=0;
		level[2][9]=1;
		level[34][33]=0;
		level[33][26]=0;
		level[27][26]=1;
		level[7][1]=0;
		level[43][44]=0;
		level[1][45]=0;
		level[3][2]=0;
		level[22][14]=0;
		level[42][44]=0;
		level[19][44]=0;
		level[39][40]=0;
		level[29][51]=0;
		level[18][11]=0;
		level[26][25]=0;
		level[7][8]=0;
		level[30][31]=0;
		level[23][24]=0;
		level[37][36]=0;
		level[15][16]=0;
		level[19][12]=0;
		level[11][18]=0;
		level[18][43]=0;
		level[37][4]=0;
		level[25][24]=0;
		level[34][57]=0;
		level[27][20]=0;
		level[28][35]=0;
		level[32][33]=0;
		level[12][38]=0;
		level[23][39]=0;
		level[36][37]=0;
		level[33][34]=0;
		level[29][22]=0;
		level[40][41]=0;
		level[31][30]=0;
		level[3][36]=0;
		level[38][37]=0;
		level[39][16]=0;
		level[14][7]=0;
		level[29][30]=0;
		level[17][16]=0;
		level[37][11]=0;
		level[26][42]=0;
		level[2][3]=0;
		level[5][49]=0;
		level[11][37]=0;
		level[27][28]=0;
		level[18][17]=0;
		level[9][2]=0;
		level[20][21]=0;
		level[34][35]=0;
		level[32][55]=0;
		level[10][11]=0;
		level[14][15]=0;
		level[16][9]=0;
		level[26][27]=0;
		level[24][25]=0;
		level[20][19]=0;
		level[9][10]=0;
		level[20][27]=0;
		level[35][52]=0;
		level[14][22]=0;
		level[16][17]=0;
		level[25][26]=0;
		level[17][18]=0;
		level[8][9]=0;
		level[6][5]=0;
		level[4][5]=0;
		level[34][27]=0;
		level[3][4]=0;
		level[19][18]=0;
		level[30][23]=0;
		level[5][4]=0;
		level[41][42]=0;
		level[19][20]=0;
		level[18][19]=0;
		level[12][19]=0;
		level[5][6]=0;
		level[1][2]=0;
		level[37][38]=0;
		level[27][34]=0;
		level[11][12]=0;
		level[6][21]=0;
		level[6][50]=0;
		level[6][46]=0;
	}
	
	public static HashSet<Integer> getAll(){
		return all;
	}
	public static int[] getIntersect(int s){
		return intersects[s];
	}
	public static int getLevel(int dst,int src){
		return level[dst][src];
	}
	public static int getId(int dst,int src){
		for (int i=0;i<4;i++)
			if (intersects[dst][i]==src) return i;
		return -1;
	}
	public static int getNextRoad(int dst,int src,int dir){
		return intersects[dst][(getId(dst, src)+dir)&3];
	}
}
