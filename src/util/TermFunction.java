/**
 * 
 */
package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;



/**
 * @author kjysmu
 *
 */
public class TermFunction {

	public static Map<String, Double> getNorm (Map<String, Double> map){
		Map<String, Double> termFrequencies = new HashMap<String, Double>();
		double count = 0;
		for (Map.Entry<String, Double> termcount : map.entrySet()){
			count += termcount.getValue();
		}
		for (Map.Entry<String, Double> termCount : map.entrySet()){
			termFrequencies.put(termCount.getKey(), termCount.getValue() / (double)count);
		}
		return termFrequencies;		
	}
	
	
	
	public static Map<String, Double> getNormSquare (Map<String, Double> map){
		Map<String, Double> termFrequencies = new HashMap<String, Double>();
		
		double count = 0;
		
		
		for (Map.Entry<String, Double> termcount : map.entrySet()){
			count += termcount.getValue() * termcount.getValue() ;
		}
		
		for (Map.Entry<String, Double> termCount : map.entrySet()){
			termFrequencies.put(termCount.getKey(), termCount.getValue() / (double) Math.sqrt(count));
		}
		
		
		return termFrequencies;		
	}
	
	
	
	public static Map<String, Double> SortMap(Map<String, Double> map) {
		DoubleValueComparator bvc =  new DoubleValueComparator(map);
		TreeMap<String, Double> sortedMap = new TreeMap<String, Double>(bvc);
		sortedMap.putAll(map);
		return sortedMap;
	}
	
	public static Map<String, Integer> SortMapInt(Map<String, Integer> map) {
		IntValueComparator bvc =  new IntValueComparator(map);
		TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(bvc);
		sortedMap.putAll(map);
		return sortedMap;
	}
	
	public static boolean isBrokenWord(String str) {
		String list[] = { "ㄱ","ㄲ","ㄴ","ㄷ","ㄸ","ㄹ","ㅁ","ㅂ","ㅃ","ㅅ","ㅆ","ㅇ","ㅈ","ㅉ","ㅊ","ㅋ","ㅌ","ㅍ","ㅎ","ㅏ","ㅐ","ㅑ","ㅒ","ㅓ","ㅔ","ㅕ","ㅖ","ㅗ","ㅘ","ㅙ","ㅚ","ㅛ","ㅜ","ㅝ","ㅞ","ㅟ","ㅠ","ㅡ","ㅢ","ㅣ","ㄳ","ㄵ","ㄶ","ㄺ","ㄻ","ㄼ","ㄽ","ㄾ","ㄿ","ㅀ","ㅄ" };
		boolean b = false;
		for(String s : list){
			if(str.equals(s)){
				b = true;
				break;
			}
		}
		return b;
	}	
	
	
	public static Map<String, Double> CombineCounts(Map<String, Double> source, Map<String, Double> target) {
		Map<String, Double> combinedCounts = new HashMap<String, Double>();
		
		combinedCounts.putAll(source);
		for (Map.Entry<String, Double> entry : target.entrySet()) {
			String key = entry.getKey();
			
			if (source.containsKey(key)) {
				combinedCounts.put(key, source.get(key) + entry.getValue());
			}
			else {
				combinedCounts.put(key, entry.getValue());
			}
		}
		
		return combinedCounts;
	}
	public static Map<String, Double> CombineCounts(Map<String, Double> source, Map<String, Double> target, Double weight) {
		Map<String, Double> combinedCounts = new HashMap<String, Double>();
		
		combinedCounts.putAll(source);
		for (Map.Entry<String, Double> entry : target.entrySet()) {
			String key = entry.getKey();
			
			if (source.containsKey(key)) {
				combinedCounts.put(key, source.get(key) + (entry.getValue() * weight) );
			}
			else {
				combinedCounts.put(key, entry.getValue() * weight );
			}
		}
		
		return combinedCounts;
	}
	

	public static Map<String, Double> CombineCountsWeight(Map<String, Double> source, Map<String, Double> target, double w1, double w2) {
		Map<String, Double> combinedCounts = new HashMap<String, Double>();
		
		//combinedCounts.putAll(source);
		for (Map.Entry<String, Double> entry : source.entrySet()) {
			String key = entry.getKey();
			
			if (combinedCounts.containsKey(key)) {
				
				combinedCounts.put(key, combinedCounts.get(key) + (entry.getValue() * w1) );
			}
			else {
				combinedCounts.put(key, entry.getValue() * w1 );
			}
		}
		
		for (Map.Entry<String, Double> entry : target.entrySet()) {
			String key = entry.getKey();
			
			if (combinedCounts.containsKey(key)) {
				
				combinedCounts.put(key, combinedCounts.get(key) + (entry.getValue() * w2) );
			}
			else {
				combinedCounts.put(key, entry.getValue() * w2 );
			}
		}
		
		return combinedCounts;
	}
	
	public static String ExtractUserID(String tweets, Map<String, Double> mentionCounts) {
		if (mentionCounts == null)
			mentionCounts = new HashMap<String, Double>();
		
		int index = tweets.indexOf('@');
		while (index < tweets.length() && index != -1) {			
			String userid = "@";

			while (true) {
				if (index == tweets.length()-1)
					break;

				char c = tweets.charAt(++index);
				if ((c >= 'a' && c <= 'z') || 
					(c >= 'A' && c <= 'Z') ||
					(c >= '0' && c <= '9') ||
					(c == '_') ) {
					userid += c;
				}
				else
					break;
			}
			
			if (userid.length() != 1) {
				if (mentionCounts.containsKey(userid))
					mentionCounts.put(userid, mentionCounts.get(userid)+1.0);
				else
					mentionCounts.put(userid, 1.0);
			}

			if (index == tweets.length()-1)
				break;
			
			index = tweets.indexOf('@', index);
		}
		
		String removedIDTweets = tweets;
		
		for (Map.Entry<String, Double> entry : mentionCounts.entrySet()) {
			removedIDTweets = removedIDTweets.replace(entry.getKey(), "");
		}
		
		return removedIDTweets;
	}
	
	private static Map<String, Double> ExtractURL(String tweets, String protocol) {
		Map<String, Double> urls = new HashMap<String, Double>();
		
		int index = tweets.indexOf(protocol);
		while (index < tweets.length() && index != -1) {			
			String url = "";

			int indexOfWhiteSpace = tweets.indexOf(' ', index+1);
			if (indexOfWhiteSpace > tweets.indexOf('\r', index+1) && tweets.indexOf('\r', index+1) != -1)
				indexOfWhiteSpace = tweets.indexOf('\r', index+1);
			if (indexOfWhiteSpace > tweets.indexOf('\n', index+1) && tweets.indexOf('\n', index+1) != -1)
				indexOfWhiteSpace = tweets.indexOf('\n', index+1);
			if (indexOfWhiteSpace > tweets.indexOf('\t', index+1) && tweets.indexOf('\t', index+1) != -1)
				indexOfWhiteSpace = tweets.indexOf('\t', index+1);

			if (indexOfWhiteSpace < 0)
				indexOfWhiteSpace = tweets.length();
			
			url = tweets.substring(index, indexOfWhiteSpace);
			
			if (urls.containsKey(url))
				urls.put(url, urls.get(url)+1.0);
			else
				urls.put(url, 1.0);
			
			index = tweets.indexOf(protocol, indexOfWhiteSpace);
		}
		
		return urls;
	}
	
	public static String ExtractURL(String tweets, Map<String, Double> urlCounts) {
		if (urlCounts == null)
			urlCounts = new HashMap<String, Double>();
		
		Map<String, Double> httpURL = ExtractURL(tweets, "http://");
		Map<String, Double> httpsURL = ExtractURL(tweets, "https://");
		
		urlCounts = CombineCounts(urlCounts, httpURL);
		urlCounts = CombineCounts(urlCounts, httpsURL);
		
		String removedURLTweets = tweets;
		
		for (Map.Entry<String, Double> entry : urlCounts.entrySet()) {
			removedURLTweets = removedURLTweets.replace(entry.getKey(), "");
		}

		return removedURLTweets;
	}

	public static Map<String, Double> RemoveDigit(Map<String, Double> map) {
		Iterator<Map.Entry<String, Double>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Double> entry = iter.next();
			if (isInteger(entry.getKey())) {
				iter.remove();
			}
		}
		iter = null;
		
		return map;
	}
	public static Map<String, Double> RemoveBrokenWord(Map<String, Double> map) {
		
		Iterator<Map.Entry<String, Double>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Double> entry = iter.next();
			if (isBrokenWord(entry.getKey())) {
				iter.remove();
			}
		}
		iter = null;
		
		return map;
	}
	
    public static boolean isInteger( String input )  
    {  
       try  
       {  
          Integer.parseInt( input );  
          return true;  
       }  
       catch( Exception e)  
       {  
          return false;  
       }  
    }
 
	
	public static void SaveWordCount(Map<String, Double> map, String filename) {
		try {
			// FileWriter fw = new FileWriter(filename);
			// BufferedWriter writer = new BufferedWriter(fw);
			
			BufferedWriter writer = new BufferedWriter(
					   new OutputStreamWriter(
			                      new FileOutputStream(filename), "UTF8"));
			
			
			
			Map<String, Double> sortedMap = SortMap(map);
			
			for (Map.Entry<String, Double> entry : sortedMap.entrySet()) {
				writer.write(entry.getKey() + "\t" + entry.getValue() + "\r\n");
			}
			
			writer.close();
			// fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
	public static void SaveWordCount(Map<String, Double> map, String filename, int tweetcount) {
		try {
			// FileWriter fw = new FileWriter(filename);
			// BufferedWriter writer = new BufferedWriter(fw);
		
			BufferedWriter writer = new BufferedWriter(
					   new OutputStreamWriter(
			                      new FileOutputStream(filename), "UTF8"));
			
			Map<String, Double> sortedMap = SortMap(map);
			
			writer.write("// # of Tweets: " + tweetcount + "\r\n");
			for (Map.Entry<String, Double> entry : sortedMap.entrySet()) {
				writer.write(entry.getKey() + "\t" + entry.getValue() + "\r\n");
			}
			
			writer.close();
			// fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	
	public static void RemoveUnImportantWords(Map<String, Double> nounCounts, double frequency) {
		double totalCount = 0.0;
		
		for (Map.Entry<String, Double> nounCount : nounCounts.entrySet()) {
			totalCount += nounCount.getValue();
		}
		
		System.out.print(nounCounts.size() + "\t");
		
		Iterator<Map.Entry<String, Double>> iter = nounCounts.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Double> entry = iter.next();
			if (entry.getValue()/(double)totalCount < frequency) {
				iter.remove();
			}
		}		
		iter = null;
		
		System.out.print(nounCounts.size() + "\t");
	}
	
	
	
	
	public static void RemoveStopWords(Map<String, Double> nounCounts) {
		StopWord stopwords = StopWord.GetInstance();
		stopwords.Initialize("D:/Development/Data/NewsX/StopWords.txt");
	
		for (Iterator<String> iter = stopwords.GetIterator(); iter.hasNext();) {
			nounCounts.remove(iter.next());
		}
	}
	
	public static Map<String, Double> getTermCounts(String content, KomoranAnalyzer kom) {
		return getTermCounts(content, kom, false);
	}

	public static Map<String, Double> getTermCounts(String content, KomoranAnalyzer kom, Boolean isOnlyNNP) {
		
		
		String tweets = content;
		
		Map<String, Double> mentionCounts = new HashMap<String, Double>();
		Map<String, Double> urlCounts = new HashMap<String, Double>();
		
		tweets = TermFunction.ExtractUserID(tweets, mentionCounts);
		tweets = TermFunction.ExtractURL(tweets, urlCounts);
		
		Map<String, Double> nounCounts = new HashMap<String, Double>();
		tweets = tweets.replaceAll("[^가-힣0-9a-zA-Z.]"," ");
		if (!tweets.trim().isEmpty())
		{
			
			if(!isOnlyNNP){
				nounCounts = kom.getNounCounts(tweets, "1");
			}else{
				nounCounts = kom.getNounCounts(tweets, "2");
			}
			
			/*
			if (!tweets.replaceAll("[^가-힣]", " ").trim().isEmpty()) {
				if(!isOnlyNNP){
					nounCounts = kom.GetNounCounts(tweets, "1");
				}else{
					nounCounts = kom.GetNounCounts(tweets, "2");
				}
			}*/
			
			
			
		}
		
		nounCounts = TermFunction.RemoveDigit(nounCounts);
		nounCounts = TermFunction.RemoveBrokenWord(nounCounts);
		
		return nounCounts;
	}
	
	public static Map<String, Double> convertTermCountMap(String str){
		
		Map<String,Double> termCounts = new HashMap<String,Double>();
		
		if(!str.equals("NONE")){
			String valueArr[] = str.split("\\[&]");
			for(String value : valueArr){
				String valueArr2[] = value.split("\\[#]");
				
				String term = valueArr2[0];
				String count = valueArr2[1];
				
				termCounts.put(term, Double.parseDouble(count));
			}
		}
	
		return termCounts;
	}
	
	public static String convertTermCountStr(Map<String, Double> termCounts){
		
		String valueStr = "";
		for (Map.Entry<String, Double> termCount : termCounts.entrySet()) {
			String termCountKey = termCount.getKey();
			int termCountValue = termCount.getValue().intValue();
			
			String termCountStr = termCountKey + "[#]" + termCountValue;

			if(!valueStr.equals("")) valueStr += "[&]";
			valueStr += termCountStr;
		}
		if(valueStr.equals("")) valueStr = "NONE";
		
		return valueStr;
	}
	
	public static Map<String, Double> convertStrToMapSD(String str, String seperator_keyValue, String seperator_Map){
		
		Map<String,Double> map = new HashMap<String,Double>();
		
		if(!str.equals("NONE")){
			String valueArr[] = str.split(seperator_Map);
			for(String value : valueArr){
				String valueArr2[] = value.split(seperator_keyValue);
				
				String term = valueArr2[0];
				String count = valueArr2[1];
				
				map.put(term, Double.parseDouble(count));
			}
		}
	
		return map;
	}
	
	public static String convertMapSDToStr(Map<String, Double> map, String format){
		
		String valueStr = "";
		for (Map.Entry<String, Double> mapEntry : map.entrySet()) {
			String mapEntryKey = mapEntry.getKey();
			String mapEntryValue = String.format(format, mapEntry.getValue());
			
			String mapStr = mapEntryKey + "[#]" + mapEntryValue;

			if(!valueStr.equals("")) valueStr += "[&]";
			valueStr += mapStr;
		}
		
		return valueStr;
	}
	
	//////////////////////////-----Almost rarely used or not used------////////////////////////////////
	
	// All the weight is 1 for IDF
	public static Map<String, Double> readUniWordCountFile(String wordcountfilename) {
		Map<String, Double> wordCounts = new HashMap<String, Double>();

		try {
			// FileReader fr = new FileReader(wordcountfilename);
			// BufferedReader reader = new BufferedReader(fr);
			
			BufferedReader reader = new BufferedReader(
					   new InputStreamReader(
			                      new FileInputStream(wordcountfilename), "UTF-8"));
			
			
			String data = "";
			while ((data = reader.readLine()) != null) {
				String[] parsedData = data.split("\t");
				if (parsedData.length == 2) {
					wordCounts.put(parsedData[0], 1.0 );
				}
			}
			
			reader.close();
			// fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return wordCounts;
	}
	
	public static Map<String, Double> readNormWordCountFile(String wordcountfilename) {
		Map<String, Double> wordCounts = new HashMap<String, Double>();
		Map<String, Double> normWordCounts = new HashMap<String, Double>();
		try {
			// FileReader fr = new FileReader(wordcountfilename);
			// BufferedReader reader = new BufferedReader(fr);
			
			BufferedReader reader = new BufferedReader(
					   new InputStreamReader(
			                      new FileInputStream(wordcountfilename), "UTF8"));
			
			String data = "";
			Double countSum = 0.0;
			
			while ((data = reader.readLine()) != null) {
				String[] parsedData = data.split("\t");
				if (parsedData.length == 2) {
					wordCounts.put(parsedData[0], Double.parseDouble(parsedData[1]));
					countSum += Double.parseDouble(parsedData[1]);
				}
			}
			for (Map.Entry<String, Double> wordCount : wordCounts.entrySet()) {
				normWordCounts.put(wordCount.getKey(), wordCount.getValue()/countSum  );	
			}
			
			reader.close();
			// fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return normWordCounts;
	}
	
	public static Map<String, Double> readWordCountFile(String wordcountfilename) {
		Map<String, Double> wordCounts = new HashMap<String, Double>();

		try {
			// FileReader fr = new FileReader(wordcountfilename);
			// BufferedReader reader = new BufferedReader(fr);
			
			BufferedReader reader = new BufferedReader(
					   new InputStreamReader(
			                      new FileInputStream(wordcountfilename), "UTF8"));
			
			
			String data = "";
			while ((data = reader.readLine()) != null) {
				String[] parsedData = data.split("\t");
				if (parsedData.length == 2) {
					wordCounts.put(parsedData[0], Double.parseDouble(parsedData[1]));
				}
			}
			
			reader.close();
			
			// fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return wordCounts;
	}
	
	public static HashMap<String, String> ReadUserIDMap(String filename) {
		HashMap<String, String> userIDMap = new HashMap<String, String>();
		try 
		{
			//FileReader fr = new FileReader(filename);
			//BufferedReader reader = new BufferedReader(fr);
	
			BufferedReader reader = new BufferedReader(
					   new InputStreamReader(
			                      new FileInputStream(filename), "UTF8"));
			
			String data = "";
			while ((data = reader.readLine()) != null) {
				String[] parsedLine = data.split("\t");
				userIDMap.put(parsedLine[1], parsedLine[2]);
			}
			
			reader.close();
			
			//fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return userIDMap;
	}
	
	public static void AddCounts(Map<String, Double> terms, Map<String, Double> addedTerms) {
		if (terms == null)
			terms = new HashMap<String, Double>();
		
		for (Map.Entry<String, Double> entry : addedTerms.entrySet()) {
			String key = entry.getKey();
			
			if (terms.containsKey(key)) {
				terms.put(key, terms.get(key) + entry.getValue());
			}
			else {
				terms.put(key, entry.getValue());
			}
		}
	}
	
	public static Map<String, Double> CombineCountsWeight(Map<String, Double> sim1, Map<String, Double> sim2, Map<String, Double> sim3, double w1, double w2, double w3) {
		Map<String, Double> combinedCounts = new HashMap<String, Double>();
		
		//combinedCounts.putAll(source);
		for (Map.Entry<String, Double> entry : sim1.entrySet()) {
			String key = entry.getKey();
			
			if (combinedCounts.containsKey(key)) {
				
				combinedCounts.put(key, combinedCounts.get(key) + (entry.getValue() * w1) );
			}
			else {
				combinedCounts.put(key, entry.getValue() * w1 );
			}
		}
		
		for (Map.Entry<String, Double> entry : sim2.entrySet()) {
			String key = entry.getKey();
			
			if (combinedCounts.containsKey(key)) {
				
				combinedCounts.put(key, combinedCounts.get(key) + (entry.getValue() * w2) );
			}
			else {
				combinedCounts.put(key, entry.getValue() * w2 );
			}
		}
		
		for (Map.Entry<String, Double> entry : sim3.entrySet()) {
			String key = entry.getKey();
			
			if (combinedCounts.containsKey(key)) {
				
				combinedCounts.put(key, combinedCounts.get(key) + (entry.getValue() * w3) );
			}
			else {
				combinedCounts.put(key, entry.getValue() * w3 );
			}
		}
		
		return combinedCounts;
	}
	
	public static boolean isNaN(double x) {return x != x;}
	
	
	
	
	
	
	
	
	
	
}
