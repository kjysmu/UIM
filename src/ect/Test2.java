package ect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import util.DoubleValueComparator;
import util.IntValueComparator;

public class Test2 {
	
	public static void main(String args[]) throws Exception{
		
		Map<String, Integer> catMap = new HashMap<String, Integer>();
		Map<String, Integer> catMapPair = new HashMap<String, Integer>();
		
		String catPath = "D:\\project\\model\\CategoryTopicPair.dat";
		String catPathOutput = "D:\\project\\model\\CategoryTopicPairInv.dat";
		
		BufferedReader br = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(new File(catPath)), "UTF8"));
		
		BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(
						new FileOutputStream(new File(catPathOutput)), "UTF8"));
		
		String line = "";
		
		while(true){
			
			line = br.readLine();
			if(line==null) break;
			
			StringTokenizer st = new StringTokenizer(line, "\t");
			
			if(st.countTokens() == 2){
				String catName = st.nextToken();
				String catID = st.nextToken();
				
				catMapPair.put(catName, Integer.parseInt(catID));
			}
			
		}
		
		br.close();
		
		
		IntValueComparator bvc = new IntValueComparator(catMapPair);
		TreeMap<String, Integer> tMap = new TreeMap<String, Integer>(bvc);
		tMap.putAll(catMapPair);
		
		
		for(Map.Entry<String, Integer> entry : tMap.descendingMap().entrySet()){
			String key = entry.getKey();
			int value = entry.getValue();
			
			bw.write(key + "\t" + value);
			bw.newLine();
		}
		
		bw.close();
		
		
		
	}
	

}
