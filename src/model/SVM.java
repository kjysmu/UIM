package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;
import miningMinds.MsgBean;
import miningMinds.UserBean;
import util.DocumentFunction;
import util.FileFunction;
import util.KomoranAnalyzer;

// liblinear SVM

public class SVM {

	KomoranAnalyzer kom;
	Map<String, Integer> dicMap;
	Map<String, Integer> catMap;
	Map<String, Integer> catMapPair;
	Map<Integer, String> catMapPairInv;

	SolverType solverType;
	double C; 
	double eps;

	Model model; 

	public SVM() throws Exception{

		kom = new KomoranAnalyzer();
		init();

	}

	public void init() throws Exception{

		model = new Model();

		dicMap = new HashMap<String, Integer>();
		catMap = new HashMap<String, Integer>();
		catMapPair = new HashMap<String, Integer>();
		catMapPairInv = new HashMap<Integer, String>();
		
		solverType = SolverType.MCSVM_CS; // -s 0 
		
		C = 100.0;    // cost of constraints violation 
		eps = 0.001; // stopping criteria 

		String catPath = "D:\\project\\model\\CategoryTopic.dat";

		BufferedReader br = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(new File(catPath)), "UTF8"));

		String line = "";
		while(true){

			line = br.readLine();
			if(line==null) break;

			StringTokenizer st = new StringTokenizer(line, "\t");
			
			if(st.countTokens() == 2){
				
				String catName = st.nextToken();
				String catID = st.nextToken();
				
				catMap.put(catName, Integer.parseInt(catID));
					
			}

		}
		br.close();
		
		
		String catPathPair = "D:\\project\\model\\CategoryTopicPair.dat";
		BufferedReader brPair = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(new File(catPathPair)), "UTF8"));

		while(true){
			line = brPair.readLine();
			if(line==null) break;

			StringTokenizer st = new StringTokenizer(line, "\t");
			
			
			if(st.countTokens() == 2){

				String catName = st.nextToken();
				String catID = st.nextToken();
				
				catMapPair.put(catName, Integer.parseInt(catID));
				catMapPairInv.put(Integer.parseInt(catID), catName);
								
			}

		}
		
		brPair.close();
		
	}

	public void train_User( Set<UserBean> userBeanSet ){
		
		Set<MsgBean> msgBeanSet = new HashSet<MsgBean>();
		
		for( UserBean bean : userBeanSet ){			
			Set<MsgBean> mBean = bean.getMsg();
			for(MsgBean bean2 : mBean ){
				msgBeanSet.add(bean2);
			}
		}
		
		train_Msg(msgBeanSet);
	}

	
	public void train_Msg( Set<MsgBean> msgBeanSet ){

		Problem problem = new Problem();

		FeatureNode[][] x = new FeatureNode[msgBeanSet.size()][];
		double[] y = new double[msgBeanSet.size()];
		generateDic (msgBeanSet);
		
		int i = 0;

		for( MsgBean bean : msgBeanSet){
			
			String id = bean.getId();
			String msg = bean.getMsg();

			String cat1 = bean.getCat1();
			String subcat1 = bean.getSubcat1();

			String cat2 = bean.getCat2();
			String subcat2 = bean.getSubcat2();

			//Map<String, Double> termCounts = kom.getNounCounts(msg);
			Map<String, Double> termCounts = kom.getAllCounts(msg);
			
			FeatureNode[] x2 = new FeatureNode[termCounts.size()];
			int j=0;
			for(Map.Entry<String, Double> entry : termCounts.entrySet()){
				String key = entry.getKey();
				x2[j] = new FeatureNode(dicMap.get(key), entry.getValue()); 
				j++;
			}

			Arrays.sort(x2, new Comparator<FeatureNode>() { 
				public int compare(FeatureNode o1, FeatureNode o2) { 
					if (o1.index > o2.index) { 
						return 1; 
					} else if (o1.index < o2.index) { 
						return -1; 
					} else { 
						return 0; 
					} 
				} 
			});
			
			x[i] = x2;
			
			
			int y_pair = 0;
			
			if(catMapPair.containsKey(subcat1 + " " + subcat2)){
				y_pair = catMapPair.get(subcat1 + " " + subcat2);
			}else if(catMapPair.containsKey(subcat2 + " " + subcat1)){
				y_pair = catMapPair.get(subcat2 + " " + subcat1);
			}
			
			int y1 = catMap.get(subcat1);
			int y2 = catMap.get(subcat2);
			
			y[i] = y_pair;
			i++;
		}
		
		
		problem.l = msgBeanSet.size();
		problem.n = dicMap.size();
		problem.x = x; 
		problem.y = y;
		problem.bias = 0.0;

		Parameter parameter = new Parameter(solverType, C, eps);
		model = Linear.train(problem, parameter); 

	}

	public void generateDic( Set<MsgBean> msgBeanSet ){

		int index = 1;
		dicMap = new HashMap<String, Integer>();

		for( MsgBean bean : msgBeanSet){			

			String msg = bean.getMsg();
			//Map<String, Double> termCounts = kom.getNounCounts(msg);
			Map<String, Double> termCounts = kom.getAllCounts(msg);
			
			
			for(Map.Entry<String, Double> entry : termCounts.entrySet()){

				String key = entry.getKey();

				if( !dicMap.containsKey(key) ){
					dicMap.put(key, index);
					index++;
				}

			}
		}
	}

	public void saveModel( String path ) throws Exception{

		File modelFile = new File(path);
		model.save(modelFile);

	}
	
	public void loadModel( String path ) throws Exception{

		File modelFile = new File(path);
		model.save(modelFile);

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

	public double predict_Msg( Set<MsgBean> msgBeanSet , boolean isCosineSim  ){
		
		
		
		int i = 0;
		
		double acc = 0.0;
		double total_score = 0.0;
		int ct = 0;
		
		Map<String, Double> labelCatMap = new HashMap<String, Double>();
		Map<String, Double> predCatMap = new HashMap<String, Double>();
		

		for( MsgBean bean : msgBeanSet){
			ct++;
			
			String id = bean.getId();
			String msg = bean.getMsg();

			String cat1 = bean.getCat1();
			String subcat1 = bean.getSubcat1();

			String cat2 = bean.getCat2();
			String subcat2 = bean.getSubcat2();

			//Map<String, Double> termCounts = kom.getNounCounts(msg);
			Map<String, Double> termCounts = kom.getAllCounts(msg);

			
			int fsize = 0;
			for(Map.Entry<String, Double> entry : termCounts.entrySet()){
				if(dicMap.containsKey(entry.getKey())) fsize++;
			}

			FeatureNode[] x2 = new FeatureNode[fsize];
			
			int j=0;
			for(Map.Entry<String, Double> entry : termCounts.entrySet()){
				if(dicMap.containsKey(entry.getKey())){
					x2[j] = new FeatureNode(dicMap.get(entry.getKey()), entry.getValue()); 
					j++;
				}
			}

			Arrays.sort(x2, new Comparator<FeatureNode>() { 
				public int compare(FeatureNode o1, FeatureNode o2) { 
					if (o1.index > o2.index) { 
						return 1; 
					} else if (o1.index < o2.index) { 
						return -1; 
					} else { 
						return 0; 
					} 
				} 
			});
			
			
			int pred_result =  (int) Linear.predict(model, x2);
			String pred_cat_pair = catMapPairInv.get(pred_result);
			
			/*
			System.out.println("TEXT(test) :" + msg);
			System.out.println("predict result:" + pred_cat_pair);
			System.out.println();
			*/
			
			
			String pred_cat[] = pred_cat_pair.split(" ");
			
			int correctNum = 0;
			if( pred_cat[0].equals(subcat1) || pred_cat[0].equals(subcat2) ){
				correctNum++;						
			}
			if( pred_cat[1].equals(subcat1) || pred_cat[1].equals(subcat2) ){
				correctNum++;						
			}
			if( subcat1.equals(pred_cat[0]) || subcat1.equals(pred_cat[1]) ){
				correctNum++;						
			}
			if( subcat2.equals(pred_cat[0]) || subcat2.equals(pred_cat[1]) ){
				correctNum++;					
			}
			total_score += correctNum/4.0;
			
			
			if(!subcat1.equals("NONE")){
				if(labelCatMap.containsKey(subcat1)){
					labelCatMap.put(subcat1, labelCatMap.get(subcat1) + 1.0);
				}else{
					labelCatMap.put(subcat1, 1.0);
				}		
			}
			
			
			if(!subcat2.equals("NONE")){
				if(labelCatMap.containsKey(subcat2)){
					labelCatMap.put(subcat2, labelCatMap.get(subcat2) + 1.0);
				}else{
					labelCatMap.put(subcat2, 1.0);
				}	
			}

			
			if(!pred_cat[0].equals("NONE")){
				if(predCatMap.containsKey(pred_cat[0])){
					predCatMap.put(pred_cat[0], predCatMap.get(pred_cat[0]) + 1.0);
				}else{
					predCatMap.put(pred_cat[0], 1.0);
				}	
			}

			if(!pred_cat[1].equals("NONE")){
				if(predCatMap.containsKey(pred_cat[1])){
					predCatMap.put(pred_cat[1], predCatMap.get(pred_cat[1]) + 1.0);
				}else{
					predCatMap.put(pred_cat[1], 1.0);
				}	
			}
			
		}
		
		System.out.println("----LABEL----");
		for(Map.Entry<String, Double> ent: labelCatMap.entrySet()){
			System.out.println(ent.getKey() + " : " + ent.getValue());
		}
		System.out.println("----PRED----");
		for(Map.Entry<String, Double> ent: predCatMap.entrySet()){
			System.out.println(ent.getKey() + " : " + ent.getValue());
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
