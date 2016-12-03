package miningMinds;

import java.util.HashSet;
import java.util.Set;

public class DatasetBean {

	Set<MsgBean> msgSet;
	Set<UserBean> userSet;
	
	public DatasetBean(){

		msgSet = new HashSet<MsgBean>();
		userSet = new HashSet<UserBean>();
	}

	public void addMsg(MsgBean msgBean){
		msgSet.add(msgBean);
	}

	public void addUser(UserBean userBean){
		userSet.add(userBean);
	}



	public Set<MsgBean> getTrainSet_Msg ( int fold_k ){
		Set<MsgBean> msgSet_Train = new HashSet<MsgBean>();
		for (MsgBean msgBean : msgSet) {
			if( msgBean.getfold() != fold_k){		
				msgSet_Train.add(msgBean);
			}
		}
		return msgSet_Train;	
	}

	public Set<UserBean> getTrainSet_User ( int fold_k ){
		Set<UserBean> userSet_Train = new HashSet<UserBean>();
		for (UserBean userBean : userSet) {
			if( userBean.getfold() != fold_k){		
				userSet_Train.add(userBean);
			}
		}
		return userSet_Train;	
	}

	public Set<MsgBean> getTestSet_Msg ( int fold_k ){
		Set<MsgBean> msgSet_Test = new HashSet<MsgBean>();
		for (MsgBean msgBean : msgSet) {
			if( msgBean.getfold() == fold_k){
				msgSet_Test.add(msgBean);
			}
		}
		return msgSet_Test;
	}
	
	public Set<UserBean> getTestSet_User ( int fold_k ){
		Set<UserBean> userSet_Train = new HashSet<UserBean>();
		for (UserBean userBean : userSet) {
			if( userBean.getfold() == fold_k){		
				userSet_Train.add(userBean);
			}
		}
		return userSet_Train;	
	}
	




}
