package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import miningMinds.MsgBean;
import miningMinds.UserBean;
import parameter.Exp;
import util.DocumentFunction;
import util.DoubleValueComparator;
import util.FileFunction;
import util.KomoranAnalyzer;
import util.TermFunction;

public class TFIDF {
	
	KomoranAnalyzer kom;
	
	Map<String, Map<String, Double>> CategoryTF;
	Map<String, Double> IDF;
	Map<String, Map<String, Double>> CategoryTFIDF;
	Map<String, Map<String, Double>> NaverTFIDF;
	Map<String, Double> NaverIDF;
	
	public TFIDF(){
		
		kom = new KomoranAnalyzer();
		
		NaverTFIDF = FileFunction.readMapStr_StrDou("D:\\project\\model\\TFIDF-NAVER\\");
		NaverIDF = FileFunction.readMapStrDou("D:\\project\\model\\IDF-NAVER\\NaverIDF.txt");
		
		init();
		
	}
	
	
	public void init(){
		CategoryTF = new HashMap<String, Map<String, Double>>();
		IDF = new HashMap<String, Double>();
		CategoryTFIDF = new HashMap<String, Map<String, Double>>();
	}
	
	public void train_User( Set<UserBean> userBeanSet ){
		
		Set<MsgBean> msgBeanSet = new HashSet<MsgBean>();
		
		for( UserBean bean : userBeanSet ){			
			Set<MsgBean> mBean = bean.getMsg();
			for(MsgBean bean2 : mBean ){
				msgBeanSet.add(bean2);
			}
		}
		
		
		train_Msg (msgBeanSet);

	}
	
	public void train_Msg( Set<MsgBean> msgBeanSet ){
		
		for( MsgBean bean : msgBeanSet ){
			
			String id = bean.getId();
			String msg = bean.getMsg();
			String time = bean.getTime();
			
			String cat1 = bean.getCat1();
			String subcat1 = bean.getSubcat1();
			String cat2 = bean.getCat2();
			String subcat2 = bean.getSubcat2();
			
			Map<String, Double> termCounts = kom.getNounCounts(msg);
			//Map<String, Double> termCounts = kom.getAllCounts(msg);
			
			for(Map.Entry<String, Double> entry : termCounts.entrySet()){
				
				String key = entry.getKey();
				double value = entry.getValue();
				
				if( !cat1.equals("NONE") && !subcat1.equals("NONE") ){
					
					if( CategoryTF.containsKey(cat1 +" " +subcat1) ){
						Map<String, Double> TFMap = CategoryTF.get(cat1 +" " +subcat1);
						
						if( TFMap.containsKey(key) ){
							TFMap.put(key, TFMap.get(key) + value);
						}else{
							TFMap.put(key, value);
						}
						
						CategoryTF.put(cat1 +" " +subcat1, TFMap);

					}else{
						
						Map<String, Double> TFMap = new HashMap<String, Double>();
						TFMap.put(key, value);
						
						CategoryTF.put(cat1 +" " +subcat1, TFMap);
						
					}
					
				}
				
				if( !cat2.equals("NONE") && !subcat2.equals("NONE") ){
					
					if( CategoryTF.containsKey(cat2 +" " +subcat2) ){
						Map<String, Double> TFMap = CategoryTF.get(cat2 +" " +subcat2);
						
						if( TFMap.containsKey(key) ){
							TFMap.put(key, TFMap.get(key) + value);
						}else{
							TFMap.put(key, value);
						}
						
						CategoryTF.put(cat2 +" " +subcat2, TFMap);

					}else{
						
						Map<String, Double> TFMap = new HashMap<String, Double>();
						TFMap.put(key, value);
						
						CategoryTF.put(cat2 +" " +subcat2, TFMap);
						
					}	
				}	
			}
		}
		
		// IDF
    	int totalCategory = 0;
		Map<String, Double> termCountsIDF = new HashMap<String, Double>();
		for(Map.Entry<String, Map<String, Double>> entry : CategoryTF.entrySet()){
			
			String key = entry.getKey();
			totalCategory++;
			
			Map<String, Double> valueMap = entry.getValue();
			Map<String, Double> valueMapNorm = TermFunction.getNorm(valueMap);
			
			for(Map.Entry<String, Double> entry2 : valueMapNorm.entrySet()){
				String key2 = entry2.getKey();
				Double value2 = entry2.getValue();
				
				if( value2 > 0.0001){
					if(termCountsIDF.containsKey(key2))
						termCountsIDF.put(key2, termCountsIDF.get(key2) + 1.0  );
	        		else
	        			termCountsIDF.put(key2, 1.0);
				}
				
			}
			
		}
		
		for(Map.Entry<String, Double> termCount : termCountsIDF.entrySet()){
        	IDF.put(termCount.getKey(), Math.log( totalCategory / (double)termCount.getValue() )); 	
        }
		
		// TFIDF
		for(Map.Entry<String, Map<String, Double>> entry : CategoryTF.entrySet()){
			
			String key = entry.getKey();
			
			
			Map<String, Double> valueMap = entry.getValue();
			Map<String, Double> valueMapNorm = TermFunction.getNorm(valueMap);
			
			Map<String, Double> valueTFIDF = new HashMap<String, Double>();
			
			
			for(Map.Entry<String, Double> entry2 : valueMapNorm.entrySet()){
				
				String key2 = entry2.getKey();
				Double value2 = entry2.getValue();
				
				if(IDF.containsKey(key2)){
					
					valueTFIDF.put(key2, value2 * IDF.get(key2) );
					
	        	}
				
			}
			
			CategoryTFIDF.put(key, valueTFIDF);
			
		}

	}
	
	public void saveModel( String path ){
		
		FileFunction.writeMapStr_StrDou(CategoryTFIDF, path);
	
	}
	
	public double predict_User( Set<UserBean> userBeanSet ){
		
		double total_sim = 0.0;
		int ct = 0;
		for( UserBean userBean : userBeanSet ){
			total_sim += predict_Msg(userBean.getMsg(), true);
			ct++;	
		}		
		double avr_sim = (double) total_sim / ct;
		return avr_sim;
		
	}

	public double predict_Msg( Set<MsgBean> msgBeanSet , boolean isCosineSim ){
		
		double acc = 0.0;
		double total_score = 0.0;
		int ct = 0;
		
		Map<String, Double> labelCatMap = new HashMap<String, Double>();
		Map<String, Double> predCatMap = new HashMap<String, Double>();
		
		for( MsgBean bean : msgBeanSet ){
			
			ct++;
			
			Map<String, Double> similarities = new HashMap<String, Double>();
			Map<String, Double> topsimilarities = new HashMap<String, Double>();

			String id = bean.getId();
			String msg = bean.getMsg();
			String time = bean.getTime();
			
			String cat1 = bean.getCat1();
			String subcat1 = bean.getSubcat1();
			String cat2 = bean.getCat2();
			String subcat2 = bean.getSubcat2();
			
			String[] labelCategory= new String[2];
			
			labelCategory[0] = cat1 + " " + subcat1;
			labelCategory[1] = cat2 + " " + subcat2;
			
			if(cat1.equals("NONE")) labelCategory[0] = "NONE";
			if(cat2.equals("NONE")) labelCategory[1] = "NONE";
			
			

			
			
			Map<String, Double> termCounts = kom.getNounCounts(msg);
			Map<String, Double> termCountsTFIDF = new HashMap<String, Double>();
			
			if(Exp.approach.contains("UNSUP")){
				for(Map.Entry<String, Double> termCount : termCounts.entrySet()){
					String key = termCount.getKey();
					Double value = termCount.getValue();
					if(NaverIDF.containsKey(key)){
						termCountsTFIDF.put(key, value * NaverIDF.get(key) );
		        	}				
				}
				
				for(Map.Entry<String, Map<String, Double>> entry : NaverTFIDF.entrySet()){
					String key = entry.getKey();
					Map<String, Double> valueMap = entry.getValue();
					double sim = DocumentFunction.ComputeCosineSimilarity(termCountsTFIDF, valueMap); // use termCountsTFIDF
					
					similarities.put(key, sim);
				}	
			}else{
				for(Map.Entry<String, Double> termCount : termCounts.entrySet()){
					String key = termCount.getKey();
					Double value = termCount.getValue();
					if(IDF.containsKey(key)){
						termCountsTFIDF.put(key, value * IDF.get(key) );
		        	}				
				}
				
				for(Map.Entry<String, Map<String, Double>> entry : CategoryTFIDF.entrySet()){
					String key = entry.getKey();
					Map<String, Double> valueMap = entry.getValue();
					double sim = DocumentFunction.ComputeCosineSimilarity(termCountsTFIDF, valueMap); // use termCountsTFIDF
					
					similarities.put(key, sim);
				}	
			}
			
			
			DoubleValueComparator bvc = new DoubleValueComparator(similarities);
			TreeMap<String, Double> tMap = new TreeMap<String, Double>(bvc);
			tMap.putAll(similarities);
			
			double totalsim = 0.0;

			for(Map.Entry<String, Double> similarity : tMap.entrySet() ){
				if( !(similarity.getValue() == 0 || similarity.getValue() == null || TermFunction.isNaN(similarity.getValue())) ){
					totalsim += similarity.getValue();
				}			
			}
			
			if(totalsim == 0 || TermFunction.isNaN(totalsim)){
				tMap.clear();
			}else{
				Iterator<Map.Entry<String,Double>> iter = tMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String,Double> entry = iter.next();
					if(entry.getValue() == 0 || entry.getValue() == null || TermFunction.isNaN(entry.getValue()) ) iter.remove();
					else{
						double score = entry.getValue();
						double score2 = score / totalsim;
						if( score <= 0.0 ){
							iter.remove();
						}else if( score2 <= 0.3 ){
							iter.remove();
						}
					}
				}
			}
			
			int count = 0;
			String[] predictCategory= new String[2];
			
			for(int i=0; i<2; i++){
				predictCategory[i] = "NONE";
			}
			for(Map.Entry<String, Double> similarity : tMap.entrySet() ){
				
				topsimilarities.put(similarity.getKey(), similarity.getValue());
				predictCategory[count] = similarity.getKey();
				count ++;
				if(count >= 2) break;
				
			}
			
			int correctNum = 0;
			if( predictCategory[0].equals(labelCategory[0]) || predictCategory[0].equals(labelCategory[1]) ){
				correctNum++;						
			}
			if( predictCategory[1].equals(labelCategory[0]) || predictCategory[1].equals(labelCategory[1]) ){
				correctNum++;						
			}
			if( labelCategory[0].equals(predictCategory[0]) || labelCategory[0].equals(predictCategory[1]) ){
				correctNum++;						
			}
			if( labelCategory[1].equals(predictCategory[0]) || labelCategory[1].equals(predictCategory[1]) ){
				correctNum++;					
			}

			total_score += correctNum/4.0;
			
			
			
			
			if(!labelCategory[0].equals("NONE")){
				if(labelCatMap.containsKey(labelCategory[0])){
					labelCatMap.put(labelCategory[0], labelCatMap.get(labelCategory[0]) + 1.0);
				}else{
					labelCatMap.put(labelCategory[0], 1.0);
				}		
			}
			
			
			if(!labelCategory[1].equals("NONE")){
				if(labelCatMap.containsKey(labelCategory[1])){
					labelCatMap.put(labelCategory[1], labelCatMap.get(labelCategory[1]) + 1.0);
				}else{
					labelCatMap.put(labelCategory[1], 1.0);
				}	
			}

			
			if(!predictCategory[0].equals("NONE")){
				if(predCatMap.containsKey(predictCategory[0])){
					predCatMap.put(predictCategory[0], predCatMap.get(predictCategory[0]) + 1.0);
				}else{
					predCatMap.put(predictCategory[0], 1.0);
				}	
			}

			
			if(!predictCategory[1].equals("NONE")){
				if(predCatMap.containsKey(predictCategory[1])){
					predCatMap.put(predictCategory[1], predCatMap.get(predictCategory[1]) + 1.0);
				}else{
					predCatMap.put(predictCategory[1], 1.0);
				}	
			}

			/*
			System.out.println("msg : " + msg);
			System.out.println("pred : " + predictCategory[0]);
			System.out.println("pred : " + predictCategory[1]);
			
			System.out.println("label : " + labelCategory[0]);
			System.out.println("label : " + labelCategory[1]);
			*/
			
			//System.out.println("score : " + correctNum);
			
			
		}
		
		
		if(isCosineSim){
			double cs_score = DocumentFunction.ComputeCosineSimilarity(predCatMap, labelCatMap);
			return cs_score;
		}else{
			acc = total_score / (double) ct;
			return acc;
		}


	}
	

}
