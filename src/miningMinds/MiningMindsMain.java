package miningMinds;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import model.SVM;
import model.TFIDF;
import parameter.Exp;

public class MiningMindsMain {
	
	public static void main(String args[]) throws Exception{
		
		String labelPath = "D:\\project\\dataset\\label\\LabelResultFB_final.txt";
		String outputPath = "D:\\project\\evaluation\\" + Exp.approach + "_" +Exp.version +".txt";
		
		BufferedReader br = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(new File(labelPath)), "UTF8"));
		
		BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(
						new FileOutputStream(new File(outputPath)), "UTF8"));
		
		
		String line = "";
		DatasetBean datasetBean = new DatasetBean();
		
		int ct = 0;
		int user_ct = 0;
		
		int fold_k = 10;
		UserBean ubean = new UserBean();
		
		boolean isFst = true;
		
		while(true){
			//ID - MSG - TIME - CAT1 - SUBCAT1 - CAT2 - SUBCAT2
			
			line = br.readLine();
			if(line==null){
				
				if(!isFst){
					datasetBean.addUser(ubean);
					isFst = false;
				}
				
				break;
			}
			
			StringTokenizer st = new StringTokenizer(line, "\t");
			
			if(st.countTokens() == 7){
				
				MsgBean msgBean = new MsgBean();
				
				String id = st.nextToken();
				String msg = st.nextToken();
				String time = st.nextToken();
				
				String cat1 = st.nextToken().replaceAll("/", "");
				String subcat1 = st.nextToken().replaceAll("/", "");
				String cat2 = st.nextToken().replaceAll("/", "");
				String subcat2 = st.nextToken().replaceAll("/", "");

				cat1 = cat1.replaceAll(" ", "");
				subcat1 = subcat1.replaceAll(" ", "");
				cat2 = cat2.replaceAll(" ", "");
				subcat2 = subcat2.replaceAll(" ", "");
				
				msgBean.addId(id);
				msgBean.addMsg(msg);
				msgBean.addTime(time);
				msgBean.addCat1(cat1);
				msgBean.addSubcat1(subcat1);
				msgBean.addCat2(cat2);
				msgBean.addSubcat2(subcat2);
				
				msgBean.addfold( (ct % fold_k) +1);				
				datasetBean.addMsg(msgBean);
				
				ubean.addMsg(msgBean);
		
				ct++;
				
			}else{
				
				if(line.contains("UserID")){
					
					if(!isFst){
						datasetBean.addUser(ubean);
						
						System.out.println("Adding user...");
						
						ubean = new UserBean();
						String userID =line.substring(3).trim();
						ubean.setID(userID);
						ubean.addfold((user_ct % fold_k) +1);
						user_ct++;						
					}else{
						ubean = new UserBean();
						String userID =line.substring(3).trim();
						ubean.setID(userID);
						ubean.addfold((user_ct % fold_k) +1);
						user_ct++;
						isFst = false;
					}	
				}
			}		
		}
		
		br.close();
	
		TFIDF tfidf = new TFIDF();
		SVM svm = new SVM();
		
		// k-fold cross validation
		
		double total_acc = 0.0;
		double total_sim = 0.0;
		
		for( int k = 1; k <= fold_k; k++){
			
			if(k != 8) continue;
			
			Set<MsgBean> trainSet = new HashSet<MsgBean>();
			Set<MsgBean> testSet = new HashSet<MsgBean>();
			
			Set<UserBean> trainSet_User = new HashSet<UserBean>();
			Set<UserBean> testSet_User = new HashSet<UserBean>();
			
			trainSet = datasetBean.getTrainSet_Msg(k);
			testSet = datasetBean.getTestSet_Msg(k);
			
			trainSet_User = datasetBean.getTrainSet_User(k);
			testSet_User = datasetBean.getTestSet_User(k);
			
			if(Exp.approach.contains("TFIDF")){
				
				if(Exp.approach.contains("USER")){
					tfidf.init();
					tfidf.train_User(trainSet_User);
					
					String modelPath_tfidf_user = "D:\\project\\model\\TFIDF-USER\\" + k + "\\";
					new File(modelPath_tfidf_user).mkdir();
					tfidf.saveModel(modelPath_tfidf_user);
					
					double sim = tfidf.predict_User(testSet_User);
					System.out.println( k +"-fold accuracy : " + String.format("%.4f", sim));
					total_sim += sim;
					
					bw.write(String.format("%.4f", sim));
					bw.newLine();
					
				}else if(Exp.approach.contains("MSG")){

					tfidf.init();
					tfidf.train_Msg(trainSet);
					
					String modelPath_tfidf = "D:\\project\\model\\TFIDF\\" + k + "\\";
					new File(modelPath_tfidf).mkdir();
					tfidf.saveModel(modelPath_tfidf);
					
					double acc = tfidf.predict_Msg(testSet, false);
					System.out.println( k +"-fold accuracy : " + String.format("%.4f", acc));
					total_acc += acc; 
					
					bw.write(String.format("%.4f", acc));
					bw.newLine();
					
				}else{
					System.out.println("Plz specify USER or MSG !!");
					System.exit(0);
				}
					
			}else if(Exp.approach.contains("SVM")){
				
				if(Exp.approach.contains("USER")){
					System.out.println("SVM Training Start");
					svm.init();
					svm.train_User(trainSet_User);
					System.out.println("SVM Training Complete");

					String modelPath_svm_user = "D:\\project\\model\\SVM-USER\\" + k + "\\";
					new File(modelPath_svm_user).mkdir();
					 
					svm.saveModel(modelPath_svm_user + "model.txt");
					double sim = svm.predict_User(testSet_User);
					
					System.out.println( k +"-fold accuracy : " + String.format("%.4f", sim));
					total_sim += sim;
					
					bw.write(String.format("%.4f", sim));
					bw.newLine();
					
				}else if(Exp.approach.contains("MSG")){
					System.out.println("SVM Training Start");
					svm.init();
					svm.train_Msg(trainSet);
					System.out.println("SVM Training Complete");
					
					String modelPath_svm = "D:\\project\\model\\SVM\\" + k + "\\";
					new File(modelPath_svm).mkdir();
					
					svm.saveModel(modelPath_svm + "model.txt");
					double acc = svm.predict_Msg(testSet, false);
					
					System.out.println( k +"-fold accuracy : " + String.format("%.4f", acc));
					total_acc += acc;
					
					bw.write(String.format("%.4f", acc));
					bw.newLine();
				}else{
					System.out.println("Plz specify USER or MSG !!");
					System.exit(0);
				}

			}
		}
		
		System.out.println();
		
		if(Exp.approach.contains("MSG")){
			System.out.println( "Average accuracy : " + String.format("%.4f", total_acc/(double)fold_k ));
			System.out.println("Done");
			
			bw.newLine();
			bw.write(String.format("%.4f", total_acc/(double)fold_k ));
			bw.newLine();

			bw.close();
		}else if(Exp.approach.contains("USER")){
			System.out.println( "Average similarity : " + String.format("%.4f", total_sim/(double)fold_k ));
			System.out.println("Done");
			
			bw.newLine();
			bw.write(String.format("%.4f", total_sim/(double)fold_k ));
			bw.newLine();

			bw.close();
		}
		
		

		
	}

}
