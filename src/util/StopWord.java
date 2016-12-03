/**
 * 
 */
package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Jonghyun Han
 *
 */
public class StopWord {
	private static StopWord instance = new StopWord();
	private HashSet<String> stopwords;
	
	private StopWord() {
		stopwords = new HashSet<String>();
	}
	
	public static StopWord GetInstance() {
		return instance;
	}
	
	public void Initialize(String filename) {
		try {
			//BufferedReader reader = new BufferedReader(new FileReader(filename) );

			BufferedReader reader = new BufferedReader(
					   new InputStreamReader(
			                      new FileInputStream(filename), "UTF-8"));
			
			stopwords.clear();
			String data = "";
			
			while ((data = reader.readLine()) != null) {
				if (data.startsWith("\uFEFF"))
					data = data.substring(1);
				
				if (data.trim().length() > 0)
					stopwords.add(data);
			}		
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Iterator<String> GetIterator() {
		if (stopwords != null) {
			return stopwords.iterator();
		}
		return null;
	}
}
