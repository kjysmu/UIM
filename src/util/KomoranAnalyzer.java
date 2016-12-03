
package util;

import java.util.HashMap;
import java.util.List;

import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;

/**
 * @author kjysmu
 *
 */
public class KomoranAnalyzer {

	public Komoran komoran;
	
	public KomoranAnalyzer() {
		initKomoranAnalyzer();
	}
	
	private void initKomoranAnalyzer() {
		komoran = new Komoran("D:\\project\\library\\komoran\\models-full");
	}
	
	public HashMap<String, Double> getNounCounts(String document) {
		return getNounCounts(document, "1");
	}

	
	public HashMap<String, Double> getNounCounts(String document, String mode) {

		HashMap<String,Double> map = new HashMap<String, Double>();
		List<List<Pair<String,String>>> result = komoran.analyze(document);
		//List<List<List<Pair<String,String>>>> analyzeNbestResult = komoran.analyze(in,nbest);
		
		for (List<Pair<String, String>> eojeolResult : result) {
			for (Pair<String, String> wordMorph : eojeolResult) {
				String noun = wordMorph.getFirst();
				String tag = wordMorph.getSecond();
				
				if( mode.equals("1") ){
					if( tag.equals("NNG") || tag.equals("NNP") ){
						if (!map.containsKey(noun))	map.put(noun, 1.0);
						else map.put(noun, map.get(noun)+1.0);
					}
				}else if( mode.equals("2") ){
					if( tag.equals("NNP") ){
						if (!map.containsKey(noun)) map.put(noun, 1.0);
						else map.put(noun, map.get(noun)+1.0);	
					}
				}else if(mode.equals("3")){
					if( tag.equals("NNP") ){
						if (!map.containsKey(noun))
							map.put(noun, 2.0);
						else
							map.put(noun, map.get(noun)+2.0);
					}else if( tag.equals("NNG") ){
						if (!map.containsKey(noun))
							map.put(noun, 1.0);
						else
							map.put(noun, map.get(noun)+1.0);
					}
				}				
			}
		}
		
		return map;
	}
	
	public HashMap<String, Double> getAllCounts(String document) {

		HashMap<String,Double> map = new HashMap<String, Double>();
		List<List<Pair<String,String>>> result = komoran.analyze(document);
		//List<List<List<Pair<String,String>>>> analyzeNbestResult = komoran.analyze(in,nbest);
		
		for (List<Pair<String, String>> eojeolResult : result) {
			for (Pair<String, String> wordMorph : eojeolResult) {
				String noun = wordMorph.getFirst();
				String tag = wordMorph.getSecond();
				
				if (!map.containsKey(noun))	map.put(noun, 1.0);
				else map.put(noun, map.get(noun)+1.0);
				
			}
		}
		
		return map;
	}
	
	

}
