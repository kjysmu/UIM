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

public class Test {
	
	public static void main(String args[]) throws Exception{
		
		Map<String, Integer> catMap = new HashMap<String, Integer>();
		Map<String, Integer> catMapPair = new HashMap<String, Integer>();
		
		String catPath = "D:\\project\\model\\CategoryTopic.dat";
		String catPathOutput = "D:\\project\\model\\CategoryTopicPair.dat";
		
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
			
			String catName = st.nextToken();
			String catID = st.nextToken();
			
			catMap.put(catName, Integer.parseInt(catID));
			
		}
		
		br.close();
		
		int index = 1;
		
		for(Map.Entry<String, Integer> entry1 : catMap.entrySet() ){
			String key1 = entry1.getKey();
			
			for(Map.Entry<String, Integer> entry2 : catMap.entrySet() ){	
				String key2 = entry2.getKey();
				
				if(key1.equals(key2)) continue;
				
				if( !catMapPair.containsKey( key1 + " " + key2 ) && !catMapPair.containsKey( key2 + " " + key1 ) ){
					
					catMapPair.put(key1 + " " + key2, index);
					index ++;	
				}
			}
		}
		
		for(Map.Entry<String, Integer> entry : catMapPair.entrySet() ){
			
			String key = entry.getKey();
			int value = entry.getValue();
			
			bw.write(key + "\t" + value);
			bw.newLine();

		}

		bw.close();
		
	}

}
