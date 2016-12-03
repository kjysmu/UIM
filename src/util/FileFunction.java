package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

// Function related to FILE

public class FileFunction {
	
    public static String removeUTF8BOM(String s) {
    	String UTF8_BOM = "\uFEFF";
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }
	
	
	public static Map<String, Map<String, Double>> readMapStr_StrDou(String path) {
		Map<String, Map<String, Double>> map = new HashMap<String, Map<String, Double>>();
		try 
		{
			List<File> list = FileFunction.getListOfFiles(path);			
			for (File file : list) {
				Map<String, Double> subMap = new HashMap<String, Double>();
				String filename = file.getName().replaceAll(".txt", "");
				
				BufferedReader br = new BufferedReader(
						   new InputStreamReader(
				                      new FileInputStream(file), "UTF-8"));
				
				String line = "";
				
				boolean isFirst = true;
				while ((line = br.readLine()) != null) {
					
					if(isFirst){
						line = removeUTF8BOM(line);
						isFirst = false;
					}
					
					StringTokenizer token = new StringTokenizer(line, "\t");
					if(token.countTokens() >= 2){
						String key= token.nextToken();
						String value= token.nextToken();
						subMap.put(key, Double.parseDouble(value));
					}
				}
				br.close();
				//fr.close();
				map.put(filename, subMap);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return map;
	}
	
	public static Map<String, String> readMapStrStr(File file) {
		Map<String, String> map = new HashMap<String, String>();
		try 
		{
			//FileReader fr = new FileReader(file);
			//BufferedReader reader = new BufferedReader(fr);
			
			BufferedReader reader = new BufferedReader(
					   new InputStreamReader(
			                      new FileInputStream(file), "UTF-8"));
			
			
			
			String line = "";
			
			boolean isFirst = true;
			
			
			while ((line = reader.readLine()) != null) {
				
				if(isFirst){
					line = removeUTF8BOM(line);
					isFirst = false;
				}
				
				
				StringTokenizer token = new StringTokenizer(line, "\t");
				if(token.countTokens() >= 2){
					String token1= token.nextToken();
					String token2= token.nextToken();
					map.put( token1, token2 );
				}
			}			
			reader.close();
			//fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return map;
	}
	
	public static Map<String, String> readMapStrStr(String path) {		
		File f = new File ( path );
		return readMapStrStr ( f );
	}
	
	public static Map<String, Double> readMapStrDou(File file) {
		Map<String, Double> map = new HashMap<String, Double>();
		try 
		{
			//FileReader fr = new FileReader(file);
			//BufferedReader reader = new BufferedReader(fr);
			
			BufferedReader reader = new BufferedReader(
					   new InputStreamReader(
			                      new FileInputStream(file), "UTF-8"));
			
			String line = "";
			boolean isFirst = true;
			while ((line = reader.readLine()) != null) {
				
				if(isFirst){
					line = removeUTF8BOM(line);
					isFirst = false;
				}
				
				StringTokenizer token = new StringTokenizer(line, "\t");
				if(token.countTokens() >= 2){
					String token1= token.nextToken();
					String token2= token.nextToken();
					map.put( token1, Double.parseDouble( token2 ) );
				}
			}			
			reader.close();
			//fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return map;
	}
	
	public static Map<String, Double> readMapStrDou(String path) {		
		File f = new File ( path );
		return readMapStrDou ( f );
	}
	
	
	
	public static List<String> readListStr(File file) {		
		List<String> list = new ArrayList<String>();
		try 
		{
			//FileReader fr = new FileReader(file);
			//BufferedReader reader = new BufferedReader(fr);
			
			BufferedReader reader = new BufferedReader(
					   new InputStreamReader(
			                      new FileInputStream(file), "UTF-8"));
			
			
			String line = "";
			boolean isFirst = true;
			while ((line = reader.readLine()) != null) {
				
				if(isFirst){
					line = removeUTF8BOM(line);
					isFirst = false;
				}
				
				if(!line.trim().isEmpty()){
					list.add(line);
				}
			}			
			reader.close();
			//fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return list;
	}
	
	public static List<String> readListStr(String path) {		
		File f = new File ( path );
		return readListStr ( f );
	}
	
	public static Set<String> readSetMecab(File file) {		
		Set<String> hset = new HashSet<String>();
		try 
		{
			//FileReader fr = new FileReader(file);
			//BufferedReader reader = new BufferedReader(fr);
			
			BufferedReader reader = new BufferedReader(
					   new InputStreamReader(
			                      new FileInputStream(file), "UTF-8"));
			
			String line = "";
			boolean isFirst = true;
			while ((line = reader.readLine()) != null) {
				
				if(isFirst){
					line = removeUTF8BOM(line);
					isFirst = false;
				}
				
				if(!line.trim().isEmpty()){
					StringTokenizer token = new StringTokenizer(line, ",");
					String token1= token.nextToken().trim();
					if(!token1.isEmpty()){
						hset.add(token1);
					}
				}
			}			
			reader.close();
			//fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return hset;
	}
	
	public static Set<String> readSetMecab(String path) {		
		File f = new File ( path );
		return readSetMecab ( f );
	}
	
	public static List<File> getListOfSubDirectories(String path) {
		File dir = new File(path);
		File[] listDir = dir.listFiles();
		List<File> subDirectories = new ArrayList<File>();
		
		for (File file : listDir) {
			if (file.isDirectory()) {
				subDirectories.add(file);
			}
		}
		
		return subDirectories;
	}
	
	public static List<File> getListOfFiles(String path) {
		File dir = new File(path);
		File[] listFiles = dir.listFiles();
		List<File> files = new ArrayList<File>();
		
		for (File file : listFiles) {
			if (file.isFile()) {
				files.add(file);
			}
		}
		return files;
	}
	
	public static String getNewsContent(String filename) {
		try {
			//FileReader fr = new FileReader(filename);
			//BufferedReader reader = new BufferedReader(fr);
			
			BufferedReader reader = new BufferedReader(
					   new InputStreamReader(
			                      new FileInputStream(filename), "UTF-8"));
			
			
			String fileContent = "", data = "";
			
			
			boolean isFirst= true;
			while ((data = reader.readLine()) != null) {
				
				

				if(isFirst){
					data = removeUTF8BOM(data);
					isFirst = false;
				}
				
				if (data.startsWith("//") || data.trim().isEmpty())
					continue;
				
				fileContent += data + "\r\n";
			}			
			reader.close();
			//fr.close();
			
			return fileContent;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	// write fuction
	public static void writeMapStr_StrDou(Map<String, Map<String, Double>> map, String path) {
		
		if(!path.endsWith("\\") || !path.endsWith("/")){
			
			if(path.contains("\\")) path = path + "\\";
			if(path.contains("/")) path = path + "/";

		}
		
		try 
		{
			for (Entry<String, Map<String, Double>> entry : map.entrySet()) {
				String key = entry.getKey();
				
				// FileWriter fw = new FileWriter(path + key + ".txt");
				// BufferedWriter bw = new BufferedWriter(fw);
				
				BufferedWriter bw = new BufferedWriter(
						   new OutputStreamWriter(
				                      new FileOutputStream(path + key + ".txt"), "UTF-8"));
				
				Map<String, Double> value = entry.getValue();
				
				DoubleValueComparator bvc = new DoubleValueComparator(value);
				TreeMap<String, Double> tMap = new TreeMap<String, Double>(bvc);
				tMap.putAll(value);
				
				for (Entry<String, Double> entry2 : tMap.entrySet()) {
					String key2 = entry2.getKey();
					Double value2 = entry2.getValue();
					bw.write(key2 + "\t" + String.format("%.8f", value2));
					bw.newLine();
				}
				bw.close();
				// fw.close();				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public static void writeMapStrStr(Map<String, String> map, String path) {
		try 
		{
			// FileWriter fw = new FileWriter(path);
			// BufferedWriter bw = new BufferedWriter(fw);
			
			BufferedWriter bw = new BufferedWriter(
					   new OutputStreamWriter(
			                      new FileOutputStream(path), "UTF-8"));
			
			
			
			for (Entry<String, String> entry : map.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				bw.write(key + "\t" + value);
				bw.newLine();
			}
			bw.close();
			//fw.close();	
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public static void writeMapStrDou(Map<String, Double> map, String path) {
		try 
		{
			
			//FileWriter fw = new FileWriter(path);
			//BufferedWriter bw = new BufferedWriter(fw);
			
			BufferedWriter bw = new BufferedWriter(
					   new OutputStreamWriter(
			                      new FileOutputStream(path), "UTF-8"));
			
			DoubleValueComparator bvc = new DoubleValueComparator(map);
			TreeMap<String, Double> tMap = new TreeMap<String, Double>(bvc);
			tMap.putAll(map);
			
			for (Entry<String, Double> entry : tMap.entrySet()) {
				String key = entry.getKey();
				Double value = entry.getValue();
				bw.write(key + "\t" + String.format("%.8f", value));
				bw.newLine();
			}
			
			bw.close();
			
			//fw.close();	
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	
	public static void writeMapStrInt(Map<String, Integer> map, String path) {
		try 
		{
			//FileWriter fw = new FileWriter(path);
			//BufferedWriter bw = new BufferedWriter(fw);
			
			BufferedWriter bw = new BufferedWriter(
					   new OutputStreamWriter(
			                      new FileOutputStream(path), "UTF-8"));
			
			IntValueComparator bvc = new IntValueComparator(map);
			TreeMap<String, Integer> tMap = new TreeMap<String, Integer>(bvc);
			tMap.putAll(map);
			
			for (Entry<String, Integer> entry : tMap.entrySet()) {
				String key = entry.getKey();
				int value = entry.getValue();
				bw.write(key + "\t" + value);
				bw.newLine();
			}
			
			bw.close();
			
			//fw.close();	
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	
	
	
}
