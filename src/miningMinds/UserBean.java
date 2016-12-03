package miningMinds;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserBean {

	Set<MsgBean> msgSet;
	Map<String,Double> catMap;
	String userID;
	
	int fold;
	
	public UserBean(){
		msgSet = new HashSet<MsgBean>();
		catMap = new HashMap<String,Double>();	
	}
	
	public Set<MsgBean> getMsg(){
		return msgSet;
	}
	
	public void addMsg(MsgBean msgBean){
		msgSet.add(msgBean);
	}
	
	public void addCategory(String category){
		
		if(catMap.containsKey(category)){
			catMap.put(category, catMap.get(category) + 1.0);	
		}else{
			catMap.put(category, 1.0);
		}
		
	}
	
	public void addfold(int fold){
		this.fold = fold;
	}
	public int getfold(){
		return fold;
	}
	
	
	public void setID(String id){
		userID = id;
	}
	public String getID(){
		return userID;
	}
	
	
	
}
